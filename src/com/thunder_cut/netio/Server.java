/*
 * Server.java
 * Author: Seokjin Yoon
 * Created Date: 2020-02-29
 */

package com.thunder_cut.netio;

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
            secretKey = KeyGenerator.getInstance(SymmetricKeyEncryption.ALGORITHM).generateKey();
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
                new Thread(() -> {
                    Connection pending = connection;
                    byte[] hello = pending.read().array();
                    if (Objects.isNull(hello)) {
                        pending.disconnect();
                        return;
                    }

                    PublicKey publicKey;
                    try {
                        publicKey = KeyFactory.getInstance(PublicKeyEncryption.ALGORITHM).generatePublic(new X509EncodedKeySpec(hello));
                    } catch (Exception e) {
                        e.printStackTrace();
                        pending.disconnect();
                        return;
                    }

                    PublicKeyEncryption publicKeyEncryption = new PublicKeyEncryption(publicKey, null);
                    byte[] encryptedKey = publicKeyEncryption.encrypt(secretKey.getEncoded());
                    ByteBuffer byteBuffer = ByteBuffer.allocate(Integer.BYTES + encryptedKey.length);
                    byteBuffer.putInt(encryptedKey.length);
                    byteBuffer.put(encryptedKey);
                    pending.write(byteBuffer);

                    connections.add(connection);
                    pending.start();
                    System.out.println(pending.socketAddress + " (" + pending.id + ")" + " is connected.");
                });
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
}
