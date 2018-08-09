package utils.tcp.aio;

public class DefReadHandler extends ReadHandler {
    @Override
    public void handleRecv(byte[] data) {
        if (data != null && data.length > 0) {
            System.out.println(new String(data));
        }
    }

    @Override
    public void connColse() {
        //recvBuf = new byte[0];
    }
}
