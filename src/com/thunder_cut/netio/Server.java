/*
 * Server.java
 * Author: Seokjin Yoon
 * Created Date: 2020-02-29
 */

package com.thunder_cut.netio;

import com.thunder_cut.WhiteBoardServer;
import com.thunder_cut.command.CommandType;
import com.thunder_cut.data.Data;
import com.thunder_cut.data.DataType;
import com.thunder_cut.data.User;
import com.thunder_cut.encryption.PublicKeyEncryption;

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
    private WhiteBoardServer owner;
    private ServerSocketChannel serverSocketChannel;
    private List<Connection> connections;
    private ExecutorService executorService;

    /**
     * Create a ServerSocketChannel.
     *
     * @param address
     * @param port
     */
    public Server(WhiteBoardServer owner, String address, int port) {
        this(owner, new InetSocketAddress(address, port));
    }

    /**
     * Create a ServerSocketChannel.
     *
     * @param port
     */
    public Server(WhiteBoardServer owner, int port) {
        this(owner, new InetSocketAddress(port));
    }

    /**
     * Create a ServerSocketChannel.
     *
     * @param local SocketAddress
     */
    public Server(WhiteBoardServer owner, SocketAddress local) {
        this.owner = owner;
        try {
            serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.bind(local);
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
                    SecretKey secretKey = owner.getSecretKey();
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
                    owner.getImageSender().refresh();
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
        Connection destination = getConnectionById(destinationId);
        if (Objects.nonNull(destination)) {
            destination.write(data);
        }
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
        SecretKey secretKey = owner.getSecretKey();
        Data parsed = new Data(data.array(), secretKey);
        User user = source.getUser();
        if (parsed.dataType == DataType.IMAGE) {
            user.setImage(parsed.getData());
        } else if (parsed.dataType == DataType.MESSAGE) {
            System.out.println(user.getName() + " (" + source.id + "): " + new String(parsed.getData(), StandardCharsets.UTF_8));
            parsed.setSrcId(source.id);
            send(parsed.toEncrypted(secretKey));
        } else if (parsed.dataType == DataType.COMMAND) {
            String command = new String(parsed.getData(), StandardCharsets.UTF_8);
            String[] args = command.split(" ");
            if (CommandType.getCommand(args[0]) == CommandType.NAME) {
                String oldName = user.getName();
                user.setName(command.substring(command.indexOf(' ') + 1));
                String newName = user.getName();
                if (!oldName.equals(newName)) {
                    String message = oldName + " → " + newName;
                    System.out.println(message);
                    send(new Data(DataType.MESSAGE, user.id, message.getBytes(StandardCharsets.UTF_8)).toEncrypted(secretKey));
                }
                send(new Data(DataType.LIST, 0, connectionsToString().getBytes(StandardCharsets.UTF_8)).toEncrypted(secretKey));
            }
        }
    }

    @Override
    public void disconnected(Connection connection) {
        System.out.println(connection.socketAddress + " (" + connection.id + ") is disconnected.");
        boolean ret = connections.remove(connection);
        if (ret) {
            send(new Data(DataType.LIST, 0, connectionsToString().getBytes(StandardCharsets.UTF_8)).toEncrypted(owner.getSecretKey()));
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

    public List<Connection> getConnections() {
        return connections;
    }
}
