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
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * This class is main class about server
 * If program start, generate server and bind given data
 */
public class SyncServer {

    private static final Map<Character, DataType> dataTypeMap;
    private static final int PORT = 3001;
    private ServerSocketChannel server;
    private ExecutorService executorService;
    private final List<ClientInfo> clientGroup;
    private final Map<ClientInfo, List<ClientInfo>> blindMap;

    static {
        dataTypeMap = new HashMap<>();
        for (DataType dataTypeEnum : DataType.values()) {
            dataTypeMap.put(dataTypeEnum.type, dataTypeEnum);
        }
    }

    /**
     * Connect with All IP, Default Port
     */
    public SyncServer() {
        this(null, PORT);
    }

    /**
     * Connect with All IP, Custom Port
     *
     * @param port Custom Port is that user want to connect
     */
    public SyncServer(int port) {
        this(null, port);
    }

    /**
     * Connect with Specific IP, Default Port
     *
     * @param ip Specific IP is that user want to connect
     */
    public SyncServer(String ip) {
        this(ip, PORT);
    }

    /**
     * Connect with Custom IP, Custom Port
     *
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
        initialize();
        clientGroup = Collections.synchronizedList(new ArrayList<>());
        blindMap = new HashMap<>();
    }

    private void initialize() {
        executorService = Executors.newFixedThreadPool(10);
    }

    private synchronized Map<ClientInfo, List<ClientInfo>> getMap() {
        return blindMap;
    }

    /**
     * Start Server
     */
    public void run() {
        executorService.submit(this::accept);
    }

    /**
     * Detect client connection and Generate ClientInfo with connected client
     * After Generating,  Add clientGroup and start reading Data from client
     */
    private void accept() {
        ClientInfo.DisconnectCallBack disconnectCallBack = this::disconnect;
        while (true) {
            try {
                SocketChannel client = server.accept();
                System.out.println(client.getRemoteAddress() + " is connect");
                ClientInfo clientInformation = new ClientInfo(clientGroup.size());
                clientInformation.setClient(client);
                clientInformation.setCallBack(disconnectCallBack, this::getMap);
                clientInformation.setOp(false);
                synchronized (clientGroup) {
                    clientGroup.add(clientInformation);
                }
                synchronized (blindMap) {
                    for (ClientInfo information : blindMap.keySet()) {
                        blindMap.get(information).add(clientInformation);
                    }
                    blindMap.put(clientInformation, clientGroup);
                }
                clientInformation.read();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Remove disconnected client in clientGroup
     * After that change client's ID by ascending sort
     *
     * @param removeTarget disconnected client
     */
    private void disconnect(ClientInfo removeTarget) {
        System.out.println("Client " + removeTarget.ID + " is disconnected");
        synchronized (clientGroup) {
            clientGroup.remove(removeTarget);
            for (int i = 0; i < clientGroup.size(); i++) {
                clientGroup.get(i).ID = i;
            }
        }
        synchronized (blindMap) {
            blindMap.remove(removeTarget);
            for (ClientInfo information : blindMap.keySet()) {
                blindMap.get(information).remove(removeTarget);
                for (int i = 0; i < blindMap.get(information).size(); i++) {
                    blindMap.get(information).get(i).ID = i;
                }
            }
        }
        try {
            removeTarget.getClient().close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
