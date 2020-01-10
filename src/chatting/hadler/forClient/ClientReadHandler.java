package chatting.hadler.forClient;



import chatting.core.Attachment;

import java.nio.ByteBuffer;
import java.nio.channels.CompletionHandler;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

/**
 * display Method ==> receive String Data and display in Client Window
 *
 * If once this method run, keep running until program shutdown
 *
 * Wait receiving data and work something and announce
 */
public class ClientReadHandler implements CompletionHandler<Integer, Attachment> {

    private Consumer<String> display;

    public void addDisplay(Consumer<String> display){
        this.display = display;
    }

    @Override
    public void completed(Integer result, Attachment attachment) {
        ByteBuffer buffer = attachment.getBuffer();
        buffer.flip();
        Charset charset = StandardCharsets.UTF_8;
        display.accept(charset.decode(buffer).toString());
        buffer.clear();

        attachment.getClient().read(attachment.getBuffer(),attachment,this);
    }

    @Override
    public void failed(Throwable exc, Attachment attachment) {
        System.out.println("Client Read Failed");
    }
}
