/*
 * Connection.java
 * Author: Seokjin Yoon
 * Created Date: 2020-02-29
 */

package com.thunder_cut.netio;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Connection {
    private final SocketChannel socketChannel;
    public final SocketAddress socketAddress;
    public final int id;
    private final ConnectionCallback callback;
    private ExecutorService executorService;
    private String name;

    /**
     * Create a connection.
     *
     * @param socketChannel SocketChannel accepted by ServerSocketChannel
     * @param callback      ServerSocketChannel with ConnectionCallback
     * @throws Exception
     */
    public Connection(SocketChannel socketChannel, ConnectionCallback callback) throws Exception {
        this.socketChannel = socketChannel;
        socketAddress = socketChannel.getRemoteAddress();
        id = socketAddress.hashCode();
        this.callback = callback;
        executorService = Executors.newSingleThreadExecutor();
        name = "user" + id;
    }

    /**
     * Start reading SocketChannel buffer.
     */
    public void start() {
        executorService.submit(this::reading);
    }

    /**
     * Stop reading SocketChannel buffer.
     */
    public void stop() {
        executorService.shutdownNow();
    }

    /**
     * Read SocketChannel buffer.
     *
     * @return received data
     */
    public ByteBuffer read() {
        ByteBuffer size = ByteBuffer.allocate(Integer.BYTES);
        ByteBuffer data;
        try {
            int ret = socketChannel.read(size);
            size.flip();
            if (ret == -1) {
                return null;
            }
            data = ByteBuffer.allocate(size.getInt());
            while (data.hasRemaining()) {
                socketChannel.read(data);
            }
            data.flip();
            return data;
        } catch (IOException e) {
            return null;
        }
    }

    private void reading() {
        while (true) {
            ByteBuffer data = read();
            if (Objects.isNull(data)) {
                break;
            }
            callback.received(this, data);
        }
        disconnect();
    }

    /**
     * Write SocketChannel buffer.
     *
     * @param data data to send
     */
    public void write(ByteBuffer data) {
        if (!socketChannel.isConnected()) {
            return;
        }

        data.flip();
        try {
            socketChannel.write(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Disconnect SocketChannel.
     */
    public void disconnect() {
        stop();
        try {
            socketChannel.shutdownInput();
            socketChannel.shutdownOutput();
            socketChannel.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        callback.disconnected(this);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
