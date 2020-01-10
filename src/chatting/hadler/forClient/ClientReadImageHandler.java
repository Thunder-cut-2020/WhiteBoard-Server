/*
 * ClientReadImageHandler.java
 * Author : Arakene
 * Created Date : 2020-01-10
 */
package chatting.hadler.forClient;

import chatting.core.Attachment;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.CompletionHandler;
import java.util.function.Consumer;

/**
 * display method ==> Receive ImageIcon and set Icon JLabel in Client Window
 *
 * This method run only one time
 */
public class ClientReadImageHandler implements CompletionHandler<Integer, Attachment> {

    private Consumer<ImageIcon> display;

    public ClientReadImageHandler(Consumer<ImageIcon> display){
        this.display = display;
    }

    @Override
    public void completed(Integer result, Attachment attachment) {
        try{
            ByteBuffer buffer = attachment.getBuffer();
            buffer.flip();
            System.out.println(buffer.toString());
            ByteArrayInputStream input = new ByteArrayInputStream(buffer.array());
            BufferedImage image = ImageIO.read(input);
            ImageIcon icon = new ImageIcon(image);
            display.accept(icon);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void failed(Throwable exc, Attachment attachment) {

    }
}
