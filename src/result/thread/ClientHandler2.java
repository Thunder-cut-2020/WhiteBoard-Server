package result.thread;

import result.Attachment;

import java.nio.ByteBuffer;
import java.nio.channels.CompletionHandler;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class ClientHandler2 implements CompletionHandler<Integer, Attachment> {
    @Override
    public void completed(Integer result, Attachment att) {
        ByteBuffer buffer = att.getByteBuffer();
        if (att.isReadMode()) {
            buffer.flip();
            Charset charset = StandardCharsets.UTF_8;
            String msg = charset.decode(buffer).toString();


            //TODO callback 함수가 여러개 존재시 처리 순서 방식 해결
            att.setReadMode(false);
            buffer.clear();
            System.out.println("Entered data");
            Scanner scanner = new Scanner(System.in);
            String newMsg = scanner.nextLine();
            buffer.put(charset.encode(newMsg));
            buffer.flip();
            att.getClient().write(buffer, att, this);

        } else {
            att.setReadMode(true);
            buffer.clear();
            att.getClient().read(buffer, att, this);
        }
    }

    @Override
    public void failed(Throwable t, Attachment att) {
        System.out.println("Server no response");
    }
}
