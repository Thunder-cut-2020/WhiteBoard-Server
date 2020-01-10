package connect;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class Client3 {
    public static void main(String[] args) {
        try{
            SocketChannel socket = SocketChannel.open();
            socket.connect(new InetSocketAddress("127.0.0.1",3000));
            BufferedReader keyboard = new BufferedReader(new InputStreamReader(System.in));

//            OutputStream outputStream = socket.getOutputStream();
//            InputStream inputStream = socket.getInputStream();

//            PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(outputStream));
//            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            String line = null;
            Charset charset = StandardCharsets.UTF_8;
            ByteBuffer byteBuffer = ByteBuffer.allocate(256);
            while((line = keyboard.readLine())!=null){
                if(line.equals("quit")){
                    break;
                }
//                printWriter.println(line);
//                printWriter.flush();
                byteBuffer = charset.encode(line);
                socket.write(byteBuffer);
                byteBuffer = ByteBuffer.allocate(256);
                socket.read(byteBuffer);
                String receive = new String(byteBuffer.array(),charset);
//                String echo = bufferedReader.readLine();
                System.out.println("Received Data : "+ receive);

            }

//            printWriter.close();
//            bufferedReader.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}