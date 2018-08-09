package utils.tcp.aio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.LinkedList;
import java.util.UUID;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class AsyncClientHandler implements CompletionHandler<Void, AsynchronousSocketChannel>{
    private AsynchronousSocketChannel channel;
    private ReentrantReadWriteLock channelLock = new ReentrantReadWriteLock();
    private volatile boolean connIng = false;
    private volatile boolean connEd = false;

    private volatile LinkedList<ByteBuffer> bsList = new LinkedList<>();
    private ReentrantLock bsListLock = new ReentrantLock();
    private ReentrantLock sendLock = new ReentrantLock();
    private volatile boolean sendIng = false;

    private String host;
    private int port;
    private ReadHandler readHandler;
    private WriteHandler writeHandler;
    private String clientId;


    public AsyncClientHandler(String host, int port, ReadHandler readHandler) {
        this.host = host;
        this.port = port;
        this.clientId = UUID.randomUUID().toString();

        this.readHandler = readHandler;
        this.readHandler.setClient(this);
        this.writeHandler = new WriteHandler(this);
    }

    public String getClientId() {
        return clientId;
    }

    public void beginConnect() {
        try {
            channelLock.writeLock().lock();
            // 正在连接中直接返回
            if (connIng || connEd)
                return;
            connect();
        } finally {
            channelLock.writeLock().unlock();
        }
    }

    private void connect(){
        //发起异步连接操作，回调参数就是这个类本身，如果连接成功会回调completed方法
        //创建异步的客户端通道
        AsynchronousSocketChannel client = null;
        try {
            client = AsynchronousSocketChannel.open();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        client.connect(new InetSocketAddress(host, port), client, this);
        connIng = true;
    }

    public void reConnect(){
        try {
            channelLock.writeLock().lock();
            connEd = false;
            connIng = false;

            connect();
        } finally {
            channelLock.writeLock().unlock();
        }
    }

    public void closeConnect(AsynchronousSocketChannel socketChannel) {
        try {
            if (socketChannel.isOpen()) {
                System.err.println("通道关闭...");
                socketChannel.close();
            }
        } catch (IOException e) {
        }
    }

    @Override
    public void completed(Void result, AsynchronousSocketChannel attachment) {
        connResult(true , attachment);
        System.out.println("conn ok");
    }

    @Override
    public void failed(Throwable exc, AsynchronousSocketChannel attachment) {
        connResult(false , attachment);
        System.out.println("conn fail");
    }

    public void connResult(boolean flag, AsynchronousSocketChannel attachment){
        try {
            channelLock.writeLock().lock();
            if (flag) {
                this.channel = attachment;
                connIng = false;
                connEd = true;
            } else {
                connIng = false;
                connEd = false;
            }
        } finally {
            channelLock.writeLock().unlock();
        }
        if (flag){
            readHandler.beginRead(attachment);
            beginWrite(attachment);
        }
    }

    public void sendResult(boolean flag, AsynchronousSocketChannel attachment){
        // 开始收发
        try {
            sendLock.lock();
            sendIng = false;
            if (flag) {
                try {
                    bsListLock.lock();
                    bsList.removeFirst();
                    if (!bsList.isEmpty()) {
                        ByteBuffer writeBuffer = bsList.getFirst();
                        attachment.write(writeBuffer, attachment, writeHandler);
                        sendIng = true;
                    }
                } finally {
                    bsListLock.unlock();
                }
            }
        } finally {
            sendLock.unlock();
        }
    }

    private void beginWrite(AsynchronousSocketChannel attachment){
        // 开始收发
        try {
            sendLock.lock();
            try {
                bsListLock.lock();
                if (!bsList.isEmpty()) {
                    ByteBuffer writeBuffer = bsList.getFirst();
                    attachment.write(writeBuffer, attachment, writeHandler);
                    sendIng = true;
                }
            } finally {
                bsListLock.unlock();
            }
        } finally {
            sendLock.unlock();
        }
    }

    private void beginWrite() {
        AsynchronousSocketChannel ch = null;
        try {
            channelLock.readLock().lock();
            if (connEd) {
                ch = channel;
            } else {
                return;
            }
        } finally {
            channelLock.readLock().unlock();
        }
        beginWrite(ch);
    }

    public void sendBytes(byte[] bs) {
        ByteBuffer writeBuffer = ByteBuffer.allocate(bs.length);
        writeBuffer.put(bs);
        writeBuffer.flip();
        try {
            bsListLock.lock();
            bsList.addLast(writeBuffer);
        } finally {
            bsListLock.unlock();
        }
        try {
            sendLock.lock();
            if (!sendIng){
                beginWrite();
            }
        } finally {
            sendLock.unlock();
        }
    }
}
