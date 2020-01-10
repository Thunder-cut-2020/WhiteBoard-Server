package chatting.hadler.forServer;

import chatting.core.Attachment;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Vector;

/**
 * Not used Class
 */
public class NotUsedAcceptHandler implements CompletionHandler<AsynchronousSocketChannel, Attachment> {

    private ServerHandler serverHandler = new ServerHandler();
    private Vector<AsynchronousSocketChannel> clients;

    public NotUsedAcceptHandler() {
        clients = new Vector<>();
    }

    @Override
    public void completed(AsynchronousSocketChannel result, Attachment attachment) {
        clients.add(result);

        try {
            SocketAddress address = result.getRemoteAddress();
            System.out.println("This Address : " + address + " is connected");
        } catch (IOException e) {
            e.printStackTrace();
        }

        attachment.getServer().accept(attachment, this);

        Attachment forClient = new Attachment();
        forClient.setServer(attachment.getServer());
        forClient.setClient(result);
        forClient.setBuffer(ByteBuffer.allocate(1024));
        forClient.setReadMode(true);


        forClient.getClient().read(forClient.getBuffer(), forClient, new CompletionHandler<Integer, Attachment>() {
            Charset charset = StandardCharsets.UTF_8;

            @Override
            public void completed(Integer result, Attachment attachment) {
                ByteBuffer buffer = attachment.getBuffer();
                buffer.flip();
                System.out.println("Server Received : " + charset.decode(buffer));
                attachment.getBuffer().clear();
                attachment.getClient().read(attachment.getBuffer(),attachment,this);
            }

            @Override
            public void failed(Throwable exc, Attachment attachment) {
                System.out.println("Server Read Failed");
            }
        });

        forClient.getClient().write(forClient.getBuffer(), forClient, new CompletionHandler<Integer, Attachment>() {
            Charset charset = StandardCharsets.UTF_8;
            @Override
            public void completed(Integer result, Attachment attachment) {
                BufferedReader keyInput = new BufferedReader(new InputStreamReader(System.in));
                try {
                    String input = keyInput.readLine();
                    attachment.getBuffer().clear();
                    attachment.getBuffer().put(charset.encode(input));
                    attachment.getBuffer().flip();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void failed(Throwable exc, Attachment attachment) {

            }
        });
    }

    @Override
    public void failed(Throwable exc, Attachment attachment) {

    }
}
