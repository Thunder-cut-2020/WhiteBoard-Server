package connect;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class Client1 {

    public static void main(String[] args) {
        try{

            SocketChannel socket = SocketChannel.open();
            socket.connect(new InetSocketAddress("127.0.0.1",3000));

            BufferedReader keyboard = new BufferedReader(new InputStreamReader(System.in));
            Charset charset = StandardCharsets.UTF_8;
            ByteBuffer byteBuffer;
            String line = null;

            while((line = keyboard.readLine())!=null){
                if(line.equals("quit")){
                    break;
                }
                byteBuffer = charset.encode(line);
                socket.write(byteBuffer);
                byteBuffer.flip();
                byteBuffer = ByteBuffer.allocate(1024);
                socket.read(byteBuffer);

                System.out.println("Received Data : "+new String(byteBuffer.array(),charset));
            }

            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}