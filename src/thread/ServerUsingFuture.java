package thread;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class ServerUsingFuture implements Runnable {

    private AsynchronousServerSocketChannel serverSocket;
    private Future<AsynchronousSocketChannel> acceptFuture;
    public ServerUsingFuture() {
        try{
            serverSocket = AsynchronousServerSocketChannel.open();
            serverSocket.bind(new InetSocketAddress("localHost",3000));
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }
    @Override
    public void run() {
        try {
//            Attachment attachment = new Attachment();
//            attachment.setServerSocket(serverSocket);
//            serverSocket.accept(attachment,new AcceptHandler());

            acceptFuture = serverSocket.accept();

            AsynchronousSocketChannel worker = acceptFuture.get(10, TimeUnit.SECONDS);
            if(worker != null && worker.isOpen()){
                while(true){
                    ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
                    Charset charset = StandardCharsets.UTF_8;
                    Future<Integer> readResult = worker.read(byteBuffer);
                    //작업이 끝날때까지 기다림
                    readResult.get();
                    if(new String(byteBuffer.array(),charset).equals("Exit")){
                        break;
                    }
                    byteBuffer.flip();
                    Future<Integer> writeResult = worker.write(byteBuffer);
                    writeResult.get();
                    byteBuffer.clear();
                }
                worker.close();
                serverSocket.close();
            }
        } catch (InterruptedException | ExecutionException | TimeoutException | IOException e) {
            e.printStackTrace();
        }
    }
}
