package result;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.Vector;

public class Attachment {
    private AsynchronousServerSocketChannel serverSocket;
    private AsynchronousSocketChannel client;
    private ByteBuffer byteBuffer;
    private boolean isReadMode;
    private Vector<Attachment> clients;

    public Vector<Attachment> getClients() {
        return clients;
    }

    public void setClients(Vector<Attachment> clients) {
        this.clients = clients;
    }

    public AsynchronousServerSocketChannel getServerSocket() {
        return serverSocket;
    }

    public void setServerSocket(AsynchronousServerSocketChannel serverSocket) {
        this.serverSocket = serverSocket;
    }

    public AsynchronousSocketChannel getClient() {
        return client;
    }

    public void setClient(AsynchronousSocketChannel client) {
        this.client = client;
    }

    public ByteBuffer getByteBuffer() {
        return byteBuffer;
    }

    public void setByteBuffer(ByteBuffer byteBuffer) {
        this.byteBuffer = byteBuffer;
    }

    public boolean isReadMode() {
        return isReadMode;
    }

    public void setReadMode(boolean readMode) {
        isReadMode = readMode;
    }
}
