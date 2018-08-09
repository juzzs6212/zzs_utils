package utils.tcp.aio;

import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

public class WriteHandler implements CompletionHandler<Integer, AsynchronousSocketChannel> {
    private AsyncClientHandler client;
    private long writeByteCnt;

    public WriteHandler(AsyncClientHandler client) {
        this.client = client;
    }

    @Override
    public void completed(Integer result, AsynchronousSocketChannel attachment) {
        writeByteCnt += result;
        client.sendResult(true, attachment);
        System.out.println("write ok");
    }

    @Override
    public void failed(Throwable exc, AsynchronousSocketChannel attachment) {
        client.sendResult(false, attachment);
        System.out.println("write fail");
    }
}