package chatting.hadler.forServer;

import chatting.core.Attachment;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.Vector;

//TODO  1:N 접속 가능한 채팅서버 구현
/**
 * Not used Class
 */
public class ServerHandler implements CompletionHandler<Integer, Attachment> {


    //TODO 성공적으로 데이터를 읽은 뒤 모든 클라이언트에게 받은 데이터 전송
    private  Vector<AsynchronousSocketChannel> clients;
    //성공적으로 데이터를 읽은 경우
    @Override
    public void completed(Integer result, Attachment attachment) {
        Charset charset = StandardCharsets.UTF_8;

        if(attachment.isReadMode()){
            ByteBuffer buffer = attachment.getBuffer();
            buffer.flip();
            System.out.println("Server Received : "+charset.decode(buffer));

            BufferedReader keyInput = new BufferedReader(new InputStreamReader(System.in));
            try {
                String input = keyInput.readLine();
                buffer.clear();
                buffer.put(charset.encode(input));
                buffer.flip();
                attachment.setReadMode(false);
            } catch (IOException e) {
                e.printStackTrace();
            }
            attachment.getClient().write(buffer,attachment,this );
        }else{
            //전 타임에서 데이터를 보냈으니 읽는 함수 실행
            attachment.setReadMode(true);
            attachment.getBuffer().clear();
            attachment.getClient().read(attachment.getBuffer(),attachment,this);
        }
    }

    //읽기 실패
    @Override
    public void failed(Throwable exc, Attachment attachment) {
        System.out.println("ServerHandler Error");
    }

    public void sendAllClient(){

    }
}
