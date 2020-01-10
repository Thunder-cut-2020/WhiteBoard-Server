package chatting.hadler.forClient;


import chatting.core.Attachment;

import java.nio.ByteBuffer;
import java.nio.channels.CompletionHandler;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;


/**
 * This Class need to fix
 */
public class ClientHandler implements CompletionHandler<Integer, Attachment> {
    private Consumer<String> display;

    public void addDisplay(Consumer<String> display) {
        this.display = display;
    }

    @Override
    public void completed(Integer result, Attachment att) {
        ByteBuffer buffer = att.getBuffer();
        if (att.isReadMode()) {
            //읽어온 데이터
            buffer.flip();
            Charset charset = StandardCharsets.UTF_8;
            String msg = charset.decode(buffer).toString();
            System.out.println(msg);
            display.accept(msg);

//            att.setReadMode(false);
//            buffer.clear();
//
//            buffer.put(charset.encode(newMsg));
//            buffer.flip();
//            att.getClient().write(buffer, att, this);

        } else {
//            att.setReadMode(true);
//            buffer.clear();
//            att.getClient().read(buffer, att, this);
        }
    }

    @Override
    public void failed(Throwable t, Attachment att) {
        System.out.println("ClientHandler Error");
    }
}
