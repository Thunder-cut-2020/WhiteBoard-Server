/*
 * Server.java
 * Author: Seokjin Yoon
 * Created Date: 2020-02-29
 */

package com.thunder_cut.netio;

import com.thunder_cut.data.Data;
import com.thunder_cut.data.DataType;
import com.thunder_cut.encryption.PublicKeyEncryption;
import com.thunder_cut.encryption.SymmetricKeyEncryption;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server implements ConnectionCallback {
    private ServerSocketChannel serverSocketChannel;
    private List<Connection> connections;
    private ExecutorService executorService;
    private SecretKey secretKey;

    /**
     * Create a ServerSocketChannel.
     *
     * @param address
     * @param port
     */
    public Server(String address, int port) {
        this(new InetSocketAddress(address, port));
    }

    /**
     * Create a ServerSocketChannel.
     *
     * @param port
     */
    public Server(int port) {
        this(new InetSocketAddress(port));
    }

    /**
     * Create a ServerSocketChannel.
     *
     * @param local SocketAddress
     */
    public Server(SocketAddress local) {
        try {
            serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.bind(local);
            secretKey = KeyGenerator.getInstance(SymmetricKeyEncryption.ALGORITHM).generateKey();
        } catch (Exception e) {
            e.printStackTrace();
        }
        connections = Collections.synchronizedList(new ArrayList<>());
        executorService = Executors.newSingleThreadExecutor();
    }

    /**
     * Start accepting connections.
     */
    public void start() {
        executorService.submit(this::accepting);
    }

    /**
     * Stop accepting connections.
     */
    public void stop() {
        executorService.shutdownNow();
    }

    private void accepting() {
        while (true) {
            try {
                SocketChannel socketChannel = serverSocketChannel.accept();
                Connection connection = new Connection(socketChannel, this);
                new Thread(() -> {
                    ByteBuffer hello = connection.read();
                    if (Objects.isNull(hello)) {
                        connection.disconnect();
                        return;
                    }

                    PublicKey publicKey;
                    try {
                        publicKey = KeyFactory.getInstance(PublicKeyEncryption.ALGORITHM).generatePublic(new X509EncodedKeySpec(hello.array()));
                    } catch (Exception e) {
                        e.printStackTrace();
                        connection.disconnect();
                        return;
                    }

                    PublicKeyEncryption publicKeyEncryption = new PublicKeyEncryption(publicKey, null);
                    byte[] encryptedKey = publicKeyEncryption.encrypt(secretKey.getEncoded());
                    ByteBuffer byteBuffer = ByteBuffer.allocate(Integer.BYTES + encryptedKey.length);
                    byteBuffer.putInt(encryptedKey.length);
                    byteBuffer.put(encryptedKey);
                    connection.write(byteBuffer);

                    connections.add(connection);
                    connection.start();
                    System.out.println(connection.socketAddress + " (" + connection.id + ")" + " is connected.");

                    send(new Data(DataType.LIST, 0, connectionsToString().getBytes(StandardCharsets.UTF_8)).toEncrypted(secretKey));
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Send data to specific connection.
     *
     * @param destination where to send data
     * @param data        data to send
     */
    public void send(Connection destination, ByteBuffer data) {
        destination.write(data);
    }

    /**
     * Send data to specific connection by ID.
     *
     * @param destinationId where to send data
     * @param data          data to send
     */
    public void send(int destinationId, ByteBuffer data) {
        getConnectionById(destinationId).write(data);
    }

    /**
     * Send data to all connections.
     *
     * @param data data to send
     */
    public void send(ByteBuffer data) {
        Connection[] array = connections.toArray(new Connection[0]);
        for (Connection destination : array) {
            send(destination, data);
        }
    }

    private Connection getConnectionById(int id) {
        Connection[] array = connections.toArray(new Connection[0]);
        for (Connection connection : array) {
            if (connection.id == id) {
                return connection;
            }
        }
        return null;
    }

    @Override
    public void received(Connection source, ByteBuffer data) {
        Data parsed = new Data(data.array(), secretKey);
        if (parsed.dataType == DataType.COMMAND) {
        } else {
            if (parsed.dataType == DataType.MESSAGE) {
                System.out.println(source.id + ": " + new String(parsed.getData(), StandardCharsets.UTF_8));
            }
            parsed.setSrcId(source.id);
            send(parsed.toEncrypted(secretKey));
        }
    }

    @Override
    public void disconnected(Connection connection) {
        System.out.println(connection.socketAddress + " (" + connection.id + ")" + " is disconnected.");
        connections.remove(connection);
    }

    private String connectionsToString() {
        Connection[] array = connections.toArray(new Connection[0]);
        StringBuilder stringBuilder = new StringBuilder();
        for (Connection connection : array) {
            stringBuilder.append(connection.id);
            stringBuilder.append('/');
            stringBuilder.append(connection.getName());
            stringBuilder.append('/');
        }
        stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        return stringBuilder.toString();
    }
}
