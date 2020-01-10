package result.thread;

import result.Attachment;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.CompletionHandler;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class ServerHandler implements CompletionHandler<Integer, Attachment> {


    @Override
    public void completed(Integer result, Attachment attachment) {
        Charset charset = StandardCharsets.UTF_8;
        if (attachment.isReadMode()) {
            ByteBuffer buffer = attachment.getByteBuffer();
            buffer.flip();
            try {
                SocketAddress address = attachment.getClient().getRemoteAddress();
                System.out.println("Received from this client " + address +" : "+charset.decode(buffer));
            } catch (IOException e) {
                e.printStackTrace();
            }


            attachment.setReadMode(false);
            buffer.clear();
//            Scanner scanner = new Scanner(System.in);
//            String line = scanner.nextLine();
            buffer.put(charset.encode("Server Message"));
            buffer.flip();
            attachment.getClient().write(buffer,attachment,this);

//            buffer.flip();
//            ByteBuffer standard = charset.encode("next");
//            standard.flip();
//            if (buffer.compareTo(standard) == 0){
//                System.out.println("entered");
//                buffer.clear();
//                Scanner scanner = new Scanner(System.in);
//                String line = scanner.nextLine();
//                buffer.put(charset.encode(line));
//                buffer.flip();
//                attachment.getClient().write(buffer, attachment, this);
//            }
//            else{
//                attachment.getClient().read(attachment.getByteBuffer(), attachment, this);
//            }

//            attachment.getClient().read(attachment.getByteBuffer(), attachment, this);
        } else {
            attachment.setReadMode(true);
            attachment.getByteBuffer().clear();
            attachment.getClient().read(attachment.getByteBuffer(), attachment, this);
        }
    }

    @Override
    public void failed(Throwable exc, Attachment attachment) {
        System.out.println("Server Read Failed");
    }
}
//    @Override
//    public void completed(Integer result, ByteBuffer buffer) {
//        Charset charset = StandardCharsets.UTF_8;
//        if(attachment.isReadMode()){
//            ByteBuffer buffer = attachment.getByteBuffer();
//            buffer.flip();
//            System.out.println("Received Data : "+charset.decode(buffer));
//
//            buffer.clear();
//            buffer.put(charset.encode("serverSend"));
//            buffer.flip();
//
//            attachment.setReadMode(false);
//
//            attachment.getClient().write(buffer,attachment,this);
//        }
//        else{
//            attachment.setReadMode(true);
//            attachment.getByteBuffer().clear();
//            attachment.getClient().read(attachment.getByteBuffer(),attachment,this);
//        }
//    }