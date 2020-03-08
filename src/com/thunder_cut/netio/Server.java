/*
 * Server.java
 * Author: Seokjin Yoon
 * Created Date: 2020-02-29
 */

package com.thunder_cut.netio;

import com.thunder_cut.command.CommandType;
import com.thunder_cut.data.Data;
import com.thunder_cut.data.DataType;
import com.thunder_cut.encryption.PublicKeyEncryption;
import com.thunder_cut.encryption.SymmetricKeyEncryption;

import javax.crypto.SecretKey;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.security.PublicKey;
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
            secretKey = SymmetricKeyEncryption.generateKey(256);
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
                System.out.println(connection.socketAddress + " (" + connection.id + ") is accepted.");
                new Thread(() -> { // handshake
                    // Receive a public key.
                    ByteBuffer hello = connection.read();
                    if (Objects.isNull(hello)) {
                        connection.disconnect();
                        return;
                    }

                    // Make a public key from received data.
                    PublicKey publicKey = PublicKeyEncryption.makePublicKey(hello.array());
                    if (Objects.isNull(publicKey)) {
                        connection.disconnect();
                        return;
                    }

                    // Encrypt a symmetric key using RSA.
                    PublicKeyEncryption publicKeyEncryption = new PublicKeyEncryption(publicKey, null);
                    byte[] encryptedKey = publicKeyEncryption.encrypt(secretKey.getEncoded());
                    ByteBuffer byteBuffer = ByteBuffer.allocate(Integer.BYTES + encryptedKey.length);
                    byteBuffer.putInt(encryptedKey.length);
                    byteBuffer.put(encryptedKey);
                    connection.write(byteBuffer);

                    // Add a connection in list and start receiving data.
                    connections.add(connection);
                    connection.start();
                    System.out.println(connection.socketAddress + " (" + connection.id + ") is connected.");

                    // Send a connection list.
                    send(new Data(DataType.LIST, 0, connectionsToString().getBytes(StandardCharsets.UTF_8)).toEncrypted(secretKey));
                }).start();
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
        if (parsed.dataType == DataType.IMAGE) {
            source.getUser().setImage(parsed.getData());
            // temporary codes
            parsed.setSrcId(source.id);
            send(parsed.toEncrypted(secretKey));
        } else if (parsed.dataType == DataType.MESSAGE) {
            System.out.println(source.getUser().getName() + " (" + source.id + "): " + new String(parsed.getData(), StandardCharsets.UTF_8));
            parsed.setSrcId(source.id);
            send(parsed.toEncrypted(secretKey));
        } else if (parsed.dataType == DataType.COMMAND) {
            String command = new String(parsed.getData(), StandardCharsets.UTF_8);
            String[] args = command.split(" ");
            if (CommandType.getCommand(args[0]) == CommandType.NAME) {
                source.getUser().setName(command.substring(command.indexOf(' ') + 1));
                send(new Data(DataType.LIST, 0, connectionsToString().getBytes(StandardCharsets.UTF_8)).toEncrypted(secretKey));
            }
        }
    }

    @Override
    public void disconnected(Connection connection) {
        System.out.println(connection.socketAddress + " (" + connection.id + ") is disconnected.");
        boolean ret = connections.remove(connection);
        if (ret) {
            send(new Data(DataType.LIST, 0, connectionsToString().getBytes(StandardCharsets.UTF_8)).toEncrypted(secretKey));
        }
    }

    private String connectionsToString() {
        Connection[] array = connections.toArray(new Connection[0]);
        StringBuilder stringBuilder = new StringBuilder();
        for (Connection connection : array) {
            stringBuilder.append(connection.id);
            stringBuilder.append('/');
            stringBuilder.append(connection.getUser().getName());
            stringBuilder.append('/');
        }
        stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        return stringBuilder.toString();
    }
}
