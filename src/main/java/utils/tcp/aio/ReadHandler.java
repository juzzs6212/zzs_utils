package utils.tcp.aio;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

public abstract class ReadHandler implements CompletionHandler<Integer, AsynchronousSocketChannel> {
    private AsyncClientHandler client;
    private ByteBuffer readBuffer;// 接收缓冲区
    private int analyzeBufferSize;

    public ReadHandler() {
        this.readBuffer = ByteBuffer.allocate(256);
    }

    public AsyncClientHandler getClient() {
        return client;
    }

    public void setClient(AsyncClientHandler client) {
        this.client = client;
    }

    public void beginRead(AsynchronousSocketChannel attachment) {
        readBuffer.clear();
        attachment.read(readBuffer, attachment, this);
    }

    @Override
    public void completed(Integer result, AsynchronousSocketChannel attachment) {
        System.out.println("read ok");
        // 收到字节为0表示断开连接了
        if (result > 0) {
            analyzeBufferSize += result;
            readBuffer.flip();
            byte[] bytes = new byte[readBuffer.remaining()];
            readBuffer.get(bytes);
            try {
                handleRecv(bytes);
            } catch (Exception e){
                e.printStackTrace();
            }
            try {
                beginRead(attachment);// 收到后继续接收
                return;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        closeConnect(attachment);
    }

    @Override
    public void failed(Throwable exc, AsynchronousSocketChannel attachment) {
        System.out.println("write fail");
        closeConnect(attachment);
    }

    private void closeConnect(AsynchronousSocketChannel attachment){
        // 如果走到这里说明有问题 直接关闭链路
        client.closeConnect(attachment);
        // 开始重新建立链路
        client.reConnect();
        connColse();
    }

    public abstract void handleRecv(byte[] data);

    public abstract void connColse();
}