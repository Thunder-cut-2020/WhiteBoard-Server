package result;

import result.thread.ClientHandler2;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class Client2 {
    public static void main(String[] args) {
        try{
            AsynchronousSocketChannel client = AsynchronousSocketChannel.open();
            Future<?> future = client.connect(new InetSocketAddress("localHost",3000));
            future.get();

            Scanner scanner = new Scanner(System.in);
            String line;
            Charset charset = StandardCharsets.UTF_8;
            Attachment attachment = new Attachment();
            attachment.setClient(client);
            attachment.setReadMode(false);
            attachment.setByteBuffer(ByteBuffer.allocate(1024));
            line = scanner.nextLine();
            attachment.getByteBuffer().put(charset.encode(line));
            attachment.getByteBuffer().flip();


            client.write(attachment.getByteBuffer(),attachment,new ClientHandler2());

            try{
                Thread.currentThread().join();
            }
            catch (InterruptedException e){
                e.printStackTrace();
            }

        } catch (IOException | InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }
}