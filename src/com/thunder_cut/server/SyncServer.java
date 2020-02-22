/*
 * SyncServer.java
 * Author : Arakene
 * Created Date : 2020-02-04
 */

package com.thunder_cut.server;

import com.thunder_cut.server.data.ReceivedData;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.*;

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
    private final Map<ClientInformation, List<ClientInformation>> clientMap;

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
        process = new Process(this::disconnected);
        clientMap = new HashMap<>();
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
                System.out.println(client.getRemoteAddress() + " is connected.");
                ClientInformation clientInfo = new ClientInformation(client, this);

                synchronized (clientMap) {
                    clientMap.put(clientInfo, clientGroup);
                    for (ClientInformation information : clientMap.keySet()) {
                        clientMap.get(information).add(clientInfo);
                    }
                }
                synchronized (clientGroup) {
                    clientGroup.add(clientInfo);
                }
                clientInfo.read();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void received(ReceivedData data) {
        process.processWithType(data, clientMap);
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
            System.out.println(client.getClient().getRemoteAddress() + " is disconnected.");
            client.getClient().close();
            synchronized (clientGroup) {
                clientGroup.remove(client);
            }
            synchronized (clientMap) {
                clientMap.remove(client);

                changeClientList();
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

    private void changeClientList() {
        for (ClientInformation key : clientMap.keySet()) {
            clientMap.get(key).clear();
            clientMap.get(key).addAll(clientGroup);
        }
    }

    private void clearConnection() {
        for (ClientInformation key : clientMap.keySet()) {
            for (ClientInformation client : clientMap.get(key)) {
                try {
                    client.getClient().close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            clientMap.remove(key);
        }
        clientGroup.clear();
    }
}
