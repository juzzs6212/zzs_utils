package utils.tcp.aio;

import java.util.concurrent.atomic.AtomicLong;

public class TcpClientTest {
    public static AtomicLong sendCnt = new AtomicLong();
    public static AtomicLong recvCnt = new AtomicLong();

    public static void main(String[] args) throws Exception {
        AsyncClientHandler client = new AsyncClientHandler("192.168.1.98", 45678, new DefReadHandler());
        client.beginConnect();
        while (true) {
            System.in.read();
            System.out.println("I am coming.");
            byte[] bs = "Hello World".getBytes();
            client.sendBytes(bs);
        }
    }
}