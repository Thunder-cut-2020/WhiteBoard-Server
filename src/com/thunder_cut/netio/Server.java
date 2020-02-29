/*
 * Server.java
 * Author: Seokjin Yoon
 * Created Date: 2020-02-29
 */

package com.thunder_cut.netio;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server implements ConnectionCallback {
    private ServerSocketChannel serverSocketChannel;
    private List<Connection> connections;
    private ExecutorService executorService;

    public Server(String address, int port) {
        this(new InetSocketAddress(address, port));
    }

    public Server(int port) {
        this(new InetSocketAddress(port));
    }

    public Server(SocketAddress local) {
        try {
            serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.bind(local);
        } catch (Exception e) {
            e.printStackTrace();
        }
        connections = Collections.synchronizedList(new ArrayList<>());
        executorService = Executors.newSingleThreadExecutor();
    }

    public void start() {
        executorService.submit(this::accepting);
    }

    public void stop() {
        executorService.shutdownNow();
    }

    private void accepting() {
        while (true) {
            try {
                SocketChannel socketChannel = serverSocketChannel.accept();
                Connection connection = new Connection(socketChannel, this);
                connections.add(connection);
                connection.start();
                System.out.println(connection.socketAddress + " (" + connection.id + ")" + " is connected.");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void send(Connection destination, ByteBuffer data) {
        destination.write(data);
    }

    public void send(int destinationId, ByteBuffer data) {
        getConnectionById(destinationId).write(data);
    }

    public void send(ByteBuffer data) {
        Connection[] array = connections.toArray(new Connection[connections.size()]);
        for (Connection destination : array) {
            send(destination, data);
        }
    }

    private Connection getConnectionById(int id) {
        Connection[] array = connections.toArray(new Connection[connections.size()]);
        for (Connection connection : array) {
            if (connection.id == id) {
                return connection;
            }
        }
        return null;
    }

    @Override
    public void received(Connection source, ByteBuffer data) {
        System.out.println(source.id + ": " + new String(data.array(), StandardCharsets.UTF_8)); // for testing
    }

    @Override
    public void disconnected(Connection connection) {
        System.out.println(connection.socketAddress + " (" + connection.id + ")" + " is disconnected.");
        connections.remove(connection);
    }
}
