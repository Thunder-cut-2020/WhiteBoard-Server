package chatting.communication;

import chatting.core.Attachment;
import chatting.hadler.forClient.ClientHandler;
import chatting.hadler.forClient.ClientReadHandler;
import chatting.hadler.forClient.ClientReadImageHandler;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;

public class Client {

    private Consumer<String> display;
    private ClientHandler clientHandler;
    private Charset charset = StandardCharsets.UTF_8;
    private Attachment attachment = new Attachment();
    private ClientReadHandler readHandler = new ClientReadHandler();
    private Consumer<ImageIcon> imageDisplay;

    private String imagePath = "resource/image/cat.jpg";

    public Client(Consumer<String> display, Consumer<ImageIcon> imageDisplay) {
        this.imageDisplay = imageDisplay;
        this.display = display;
        clientHandler = new ClientHandler();
        clientHandler.addDisplay(display);
        readHandler.addDisplay(display);
    }

    public void run() {
        int failed = 0;
        boolean success = false;
        try {
            AsynchronousSocketChannel client = AsynchronousSocketChannel.open();
            Future<?> connectFuture = client.connect(new InetSocketAddress("127.0.0.1", 3000));
            connectFuture.get(10, TimeUnit.SECONDS);

            Charset charset = StandardCharsets.UTF_8;

            attachment.setClient(client);
            attachment.setReadMode(false);
            attachment.setBuffer(ByteBuffer.allocate(100000));

//            readFromServer();
            readImageFromServer();
        } catch (IOException | InterruptedException | ExecutionException e) {
            e.printStackTrace();
        } catch (TimeoutException t) {
            failed++;
            System.out.println("TimeOut");
        }
    }

    /**
     * Send Data with UserInput( TextField Data)
     *
     * @param msg Message
     */
    public void writeToServer(String msg) {
        attachment.getBuffer().clear();
        attachment.getBuffer().put(charset.encode(msg));
        attachment.getBuffer().flip();
        attachment.getClient().write(attachment.getBuffer());
        System.out.println("Client Send");
    }

    /**
     * Read String Data from Server
     */
    public void readFromServer() {
        attachment.getBuffer().clear();
        attachment.getClient().read(attachment.getBuffer(), attachment, readHandler);
    }

    public void addDisplay(Consumer<String> display) {
        this.display = display;
    }

    /**
     * Read Image Data from Server
     */
    public void readImageFromServer(){
        attachment.getClient().read(attachment.getBuffer(), attachment,new ClientReadImageHandler(imageDisplay));
    }

    /**
     * Get Image in JLabel and convert Image to ByteBuffer
     * When converting is finished, send to server
     * @param label that have a image
     */
    public void sendImageToServer(JLabel label) {
        try {
            ImageIcon imageIcon = (ImageIcon) label.getIcon();
            Image image = imageIcon.getImage();
            BufferedImage bufferedImage = new BufferedImage(image.getWidth(null),image.getHeight(null),BufferedImage.TYPE_INT_RGB);
            Graphics2D graphics = bufferedImage.createGraphics();
            graphics.drawImage(image,null,null);
            graphics.dispose();
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, "jpg", output);
            output.flush();
            ByteBuffer buffer = ByteBuffer.wrap(output.toByteArray());
            attachment.getClient().write(buffer, buffer, new CompletionHandler<Integer, ByteBuffer>() {
                @Override
                public void completed(Integer result, ByteBuffer attachment) {
                    System.out.println("Client Send Data : " + attachment.toString());
                }

                @Override
                public void failed(Throwable exc, ByteBuffer attachment) {

                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sorry testCode
     * @return ImageIcon
     */
    public ImageIcon getImage() {
        ImageIcon readImageIcon = null;
        try {
            JLabel label = new JLabel();
            label.setIcon(new ImageIcon(imagePath));
            Image img = ((ImageIcon)label.getIcon()).getImage();
            BufferedImage bufferedImage = new BufferedImage(img.getWidth(null),img.getHeight(null),BufferedImage.TYPE_INT_RGB);
            Graphics2D g2 = bufferedImage.createGraphics();
            g2.drawImage(img,null,null);
            g2.dispose();
//            BufferedImage bufferedImage = ImageIO.read(new File(imagePath));
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, "jpg", output);
            output.flush();
            ByteBuffer byteBuffer = ByteBuffer.wrap(output.toByteArray());
            byteBuffer.flip();

            ByteArrayInputStream input = new ByteArrayInputStream(byteBuffer.array());
            BufferedImage readBufferedImage = ImageIO.read(input);
            readImageIcon = new ImageIcon(readBufferedImage);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return readImageIcon;
    }
}