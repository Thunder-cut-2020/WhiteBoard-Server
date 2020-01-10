/*
 * TestAcceptHandler.java
 * Author : Arakene
 * Created Date : 2020-01-09
 */
package chatting.hadler.forServer;

import chatting.core.Attachment;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.function.Consumer;

public class TestAcceptHandler implements CompletionHandler<AsynchronousSocketChannel, Attachment> {

    private Consumer<String> display;
    private Runnable readHandler;

    public TestAcceptHandler(Consumer<String> display, Runnable readHandler) {
        this.display = display;
        this.readHandler = readHandler;
    }

    @Override
    public void completed(AsynchronousSocketChannel result, Attachment attachment) {
//        try {
//            display.accept(result.getRemoteAddress()+" is connected");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        Attachment client = new Attachment();
        client.setClient(result);
        client.setBuffer(ByteBuffer.allocate(1024));
        client.setReadMode(false);
        client.setServer(attachment.getServer());

        attachment.getClients().add(client);
        readHandler.run();

        attachment.getServer().accept(attachment, this);
    }

    @Override
    public void failed(Throwable exc, Attachment attachment) {

    }
}
