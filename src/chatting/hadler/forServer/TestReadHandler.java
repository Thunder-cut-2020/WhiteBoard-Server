/*
 * TestReadHandler.java
 * Author : Arakene
 * Created Date : 2020-01-09
 */
package chatting.hadler.forServer;

import chatting.core.Attachment;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.CompletionHandler;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

public class TestReadHandler implements CompletionHandler<Integer, Attachment> {
    private Consumer<String> display;

    public TestReadHandler(Consumer<String> display) {
        this.display = display;
    }

    @Override
    public void completed(Integer result, Attachment attachment) {
        Charset charset = StandardCharsets.UTF_8;
        ByteBuffer buffer = attachment.getBuffer();
        buffer.flip();


        try {
            display.accept(attachment.getClient().getRemoteAddress() + charset.decode(buffer).toString());
        } catch (IOException e) {
            e.printStackTrace();
        }

        buffer.clear();
        attachment.getClient().read(attachment.getBuffer(), attachment, this);
    }

    @Override
    public void failed(Throwable exc, Attachment attachment) {
        System.out.println("Server Read Failed");
    }
}
