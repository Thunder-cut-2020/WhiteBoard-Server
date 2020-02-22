/*
 * SyncServer.java
 * Author : Arakene
 * Created Date : 2020-02-04
 */

package com.thunder_cut.socket;

import com.thunder_cut.processing.Process;
import com.thunder_cut.processing.data.ReceivedData;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This class is main class about server
 * If program start, generate server and bind given data
 * Accept method keep running until program is shutdown
 */
public class SyncServer implements ClientCallback, Runnable {
    private static final int PORT = 3001;
    private ServerSocketChannel server;
    private final List<ClientInformation> clientGroup;
    private Process process;

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
        process = new Process(this);
    }

    /**
     * Detect client connection and Generate ClientInformation with connected client
     * After Generating Add clientGroup and start readingData from client
     */
    @Override
    public void run() {
        while (true) {
            try {
                SocketChannel client = server.accept();
                ClientInformation clientInfo = new ClientInformation(client, this);
                synchronized (clientGroup) {
                    clientGroup.add(clientInfo);
                }
                clientInfo.read();
                System.out.println(client.getRemoteAddress() + " is connected. " + getId(clientInfo));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void send(ByteBuffer data, ClientInformation dest) {
        try {
            dest.getClient().write(data);
        } catch (IOException e) {
            disconnected(dest);
        }
    }

    public void send(ByteBuffer data) {
        ClientInformation[] array;
        synchronized (clientGroup) {
            array = clientGroup.toArray(new ClientInformation[clientGroup.size()]);
        }
        for (ClientInformation dest : array) {
            send(data, dest);
        }
    }

    @Override
    public void received(ReceivedData data) {
        process.processWithType(data, clientGroup);
    }

    /**
     * Remove disconnected client in clientGroup
     * After that change client's ID by ascending sort
     *
     * @param client disconnected client
     */
    @Override
    public void disconnected(ClientInformation client) {
        try {
            System.out.println(client.getClient().getRemoteAddress() + " is disconnected. " + getId(client));
            if (client.getClient().isOpen()) {
                client.getClient().close();
            }
            synchronized (clientGroup) {
                clientGroup.remove(client);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getId(ClientInformation client) {
        synchronized (clientGroup) {
            return clientGroup.indexOf(client);
        }
    }

    private void clearConnection() {
        for (ClientInformation client : clientGroup) {
            try {
                client.getClient().close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        clientGroup.clear();
    }
}
