/*
 * Server.java
 * Author : Arakene
 * Created Date : 2020-01-15
 */
package com.thunder_cut.socket;

import com.thunder_cut.socket.handler.ServerAcceptHandler;
import com.thunder_cut.socket.handler.ServerReadHandler;
import com.thunder_cut.socket.handler.ServerWriteHandler;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.Vector;

/**
 *  Server can open with 4 pattern
 *      1. All IP, Default Port
 *      2. All IP, User Custom Port
 *      3. User Custom IP, Default Port
 *      4.  User Custom IP, User Custom Port
 *
 *  If accept is success Read method will keep running until shutdown
 *  If read is success Write to All client given data
 */

public class Server implements Runnable {

    private final static int PORT = 3001;
    private AsynchronousServerSocketChannel serverSocket;
    private Vector<Attachment> clientGroup = new Vector<>();
    private ServerAcceptHandler serverAcceptHandler;
    private ServerReadHandler serverReadHandler;
    private ServerWriteHandler serverWriteHandler;

    /**
     * All IP, Default Port
     */
    public Server() {
        this(null, PORT);
    }

    /**
     * All IP, Custom Port
     *
     * @param port Custom Port is that user want to connect
     */
    public Server(int port) {
        this(null, port);
    }

    /**
     * Specific IP, Default Port
     *
     * @param ip Specific IP is that user want to connect
     */
    public Server(String ip) {
        this(ip, PORT);
    }

    /**
     * @param ip   Custom IP
     * @param port Custom Port
     */
    public Server(String ip, int port) {
        try {
            serverSocket = AsynchronousServerSocketChannel.open();
            serverSocket.bind(ip == null ? new InetSocketAddress(port) : new InetSocketAddress(ip, port));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * When this method is called Server Will be waiting for connect from client with ServerAcceptHandler
     * and Start reading with ServerReadHandler
     */
    private void startAccept() {
        handlerInitialize();
        Attachment ServerInfo = new Attachment();
        ServerInfo.setServer(serverSocket);
        ServerInfo.setClientGroup(clientGroup);
        serverSocket.accept(ServerInfo, serverAcceptHandler.getHandler());
    }

    private void handlerInitialize() {
        serverAcceptHandler = new ServerAcceptHandler(this::readFromClient);
        serverReadHandler = new ServerReadHandler(this::writeToAllClients);
        serverWriteHandler = new ServerWriteHandler();
    }


    /**
     * If Client is not readMode start Reading
     */
    private void readFromClient() {
        for (Attachment clientInfo : clientGroup) {
            if (!clientInfo.isReadMode()) {
                clientInfo.setReadMode(true);
                clientInfo.getClient().read(clientInfo.getBuffer(), clientInfo, serverReadHandler.getReadHandler());
            }
        }
    }

    /**
     * Send Data to All client in ClientGroup
     *
     * @param data Data
     */
    private void writeToAllClients(ByteBuffer data) {
        try{
            for (Attachment clientInfo : clientGroup) {
//                clientInfo.getClient().write(data, data, new ServerWriteHandler().getWriteHandler());
                clientInfo.getClient().write(data);
            }
            data.clear();
        }
        catch (BufferOverflowException overflow){
            overflow.printStackTrace();
        }

    }


    /**
     * If you use GUI remove Implement Runnable
     * <p>
     * If user input Quit writing will shutdown
     */
    @Override
    public void run() {
        startAccept();

        String input;
        Scanner scanner = new Scanner(System.in);
        Charset charset = StandardCharsets.UTF_8;

        while (!(input = scanner.nextLine()).equals("Quit")) {
            ByteBuffer inputBuffer = charset.encode(input);
            writeToAllClients(inputBuffer);
        }

        try {
            Thread.currentThread().join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            try {
                serverSocket.close();
                scanner.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
