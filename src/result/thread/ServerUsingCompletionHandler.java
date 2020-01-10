package result.thread;

import result.Attachment;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Vector;
import java.util.concurrent.Executors;

public class ServerUsingCompletionHandler implements Runnable{

    private AsynchronousServerSocketChannel serverSocket;
    private AsynchronousChannelGroup group;
    private Vector<Attachment> clients;


    public ServerUsingCompletionHandler(){
        try{
            clients = new Vector<>();
            group = AsynchronousChannelGroup.withFixedThreadPool(5, Executors.defaultThreadFactory());
            serverSocket = AsynchronousServerSocketChannel.open();
            serverSocket.bind(new InetSocketAddress("localHost",3000));
        }catch (IOException e){
            e.printStackTrace();
        }
    }
    @Override
    public void run() {
        Attachment attachment = new Attachment();
        attachment.setServerSocket(serverSocket);
        attachment.setClients(clients);
        serverSocket.accept(attachment,new AcceptHandler2());


        try{
            Thread.currentThread().join();
        }catch (InterruptedException e ){
            e.printStackTrace();
        }
    }
}
