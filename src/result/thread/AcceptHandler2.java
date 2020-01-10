package result.thread;

import result.Attachment;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

public class AcceptHandler2 implements CompletionHandler<AsynchronousSocketChannel, Attachment> {

    @Override
    public void completed(AsynchronousSocketChannel client, Attachment attachment) {
        try {
            SocketAddress clientAddress = client.getRemoteAddress();
            System.out.println(clientAddress+" is connected");

            attachment.getServerSocket().accept(attachment,this);

            Attachment newAtt = new Attachment();
            newAtt.setServerSocket(attachment.getServerSocket());
            newAtt.setClient(client);
            newAtt.setReadMode(true);
            newAtt.setByteBuffer(ByteBuffer.allocate(1024));

            attachment.getClients().add(newAtt);

            client.read(newAtt.getByteBuffer(),newAtt,new ServerHandler());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void failed(Throwable exc, Attachment attachment) {
        System.out.println("Accept failed");
    }
}
