/*
 * SyncServer.java
 * Author : Arakene
 * Created Date : 2020-02-04
 */

package com.thunder_cut.server;

import com.thunder_cut.server.data.DataType;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * This class is main class about server
 * If program start, generate server and bind given data
 * Accept method keep running until program is shutdown
 */
public class SyncServer implements ClientCallback, Runnable {
    private static final int PORT = 3001;
    private ServerSocketChannel server;
    private final List<ClientInfo> clientGroup;

    /**
     * All IP, Default Port
     */
    public SyncServer() {
        this(null, PORT);
    }

    /**
     * All IP, Custom Port
     *
     * @param port Custom Port is that user want to connect
     */
    public SyncServer(int port) {
        this(null, port);
    }

    /**
     * @param ip   Custom IP
     * @param port Custom Port
     */
    public SyncServer(String ip, int port) {
        try {
            server = ServerSocketChannel.open();
            server.bind(ip == null ? new InetSocketAddress(port) : new InetSocketAddress(ip, port));
        } catch (IOException e) {
            e.printStackTrace();
        }
        clientGroup = Collections.synchronizedList(new ArrayList<>());
    }

    /**
     * Detect client connection and Generate ClientInfo with connected client
     * After Generating Add clientGroup and start readingData from client
     */
    @Override
    public void run() {
        while (true) {
            try {
                SocketChannel client = server.accept();
                System.out.println(client.getRemoteAddress() + " is connected.");
                ClientInfo clientInformation = new ClientInfo(client, this);
                clientGroup.add(clientInformation);
                clientInformation.read();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Check dataType and decide writeMode
     * If type is command, send data to srcID
     * or write to everyone
     *
     * @param src  client who send data
     * @param type data type
     * @param data pure data(No header)
     */
    private void identifyWriteMode(ClientInfo src, DataType type, byte[] data) {
        if (type == DataType.CMD) {
            send(src, type, data, src);
        } else {
            send(src, type, data);
        }
    }

    /**
     * Generate with srcID, data type, pure data and Send to a specific client.
     *
     * @param src  client who send data
     * @param type data type
     * @param data pure data(No header)
     * @param dest
     */
    public void send(ClientInfo src, DataType type, byte[] data, ClientInfo dest) {
        SendingData sendingData = new SendingData(clientGroup.indexOf(src), clientGroup.indexOf(dest), type, data);
        try {
            dest.getClient().write(sendingData.toByteBuffer());
        } catch (IOException e) {
            disconnected(dest);
        }
    }

    /**
     * Generate with srcID, data type, pure data and Send to everyone in clientGroup
     *
     * @param src  client who send data
     * @param type data type
     * @param data pure data(No header)
     */
    public void send(ClientInfo src, DataType type, byte[] data) {
        for (Iterator<ClientInfo> iterator = clientGroup.iterator(); iterator.hasNext(); ) {
            ClientInfo dest = iterator.next();
            send(src, type, data, dest);
        }
    }

    @Override
    public void received(ClientInfo client, DataType type, byte[] data) {
        identifyWriteMode(client, type, data);
    }

    /**
     * Remove disconnected client in clientGroup
     * After that change client's ID by ascending sort
     *
     * @param client disconnected client
     */
    @Override
    public void disconnected(ClientInfo client) {
        try {
            System.out.println(client.getClient().getRemoteAddress() + " is disconnected.");
            client.getClient().close();
            synchronized (clientGroup) {
                clientGroup.remove(client);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
