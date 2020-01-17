/*
 * Server.java
 * Author : Arakene
 * Created Date : 2020-01-15
 */
package com.thunder_cut.server;

import com.thunder_cut.server.handler.ServerAcceptHandler;
import com.thunder_cut.server.handler.ServerReadHandler;
import com.thunder_cut.server.handler.ServerWriteHandler;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Vector;

public class Server implements Runnable{

    private final static int PORT = 3001;
    private AsynchronousServerSocketChannel serverSocket;
    private Vector<Attachment> clientGroup = new Vector<>();
    private Charset charset = StandardCharsets.UTF_8;
    private ByteBuffer buffer;

    /**
     * All IP, Default Port
     */
    public Server() {
        try {
            serverSocket = AsynchronousServerSocketChannel.open();
            serverSocket.bind(new InetSocketAddress(PORT));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * All IP, Custom Port
     * @param port Custom Port is that user want to connect
     */
    public Server(int port) {
        try {
            serverSocket = AsynchronousServerSocketChannel.open();
            serverSocket.bind(new InetSocketAddress(port));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Specific IP, Default Port
     * @param ip Specific IP is that user want to connect
     */
    public Server(String ip) {
        try {
            serverSocket = AsynchronousServerSocketChannel.open();
            serverSocket.bind(new InetSocketAddress(ip, PORT));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * @param ip Custom IP
     * @param port Custom Port
     */
    public Server(String ip, int port) {
        try {
            serverSocket = AsynchronousServerSocketChannel.open();
            serverSocket.bind(new InetSocketAddress(ip, port));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * When this method is called Server Will be waiting for connect from client with ServerAcceptHandler
     * and Start reading with ServerReadHandler
     */
    public void startAccept(){
        Attachment ServerInfo = new Attachment();
        ServerInfo.setServer(serverSocket);
        ServerInfo.setClientGroup(clientGroup);
        serverSocket.accept(ServerInfo, new ServerAcceptHandler(this::readFromClient).getHandler());
    }

    /**
     * If Client is not readMode start Reading
     */
    public void readFromClient(){
        for(Attachment clientInfo : clientGroup){
            if(!clientInfo.isReadMode()){
                clientInfo.setReadMode(true);
                clientInfo.getClient().read(clientInfo.getBuffer(),clientInfo, new ServerReadHandler().getReadHandler());
            }
        }
    }

    /**
     * Send String Message to All client in ClientGroup
     * @param message String Message
     */
    public void writeToAllClients(String message){
        buffer = charset.encode(message);
        for(Attachment clientInfo : clientGroup){
            clientInfo.getClient().write(buffer, buffer,new ServerWriteHandler().getWriteHandler());
        }
        buffer.clear();
    }

    /**
     * Pick specific client and send String message
     * @param message String message
     * @param clientIndex Sending Target
     */
    public void writeToSpecificClient(String message, int clientIndex){
        buffer = charset.encode(message);
        clientGroup.get(clientIndex).getClient().write(buffer, buffer, new ServerWriteHandler().getWriteHandler());
        buffer.clear();
    }

    /**
     * If you use GUI remove Implement Runnable
     */
    @Override
    public void run() {
        startAccept();

        try {
            Thread.currentThread().join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
