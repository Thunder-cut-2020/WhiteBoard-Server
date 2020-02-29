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

    public Connection(SocketChannel socketChannel, ConnectionCallback callback) throws Exception {
        this.socketChannel = socketChannel;
        socketAddress = socketChannel.getRemoteAddress();
        id = socketAddress.hashCode();
        this.callback = callback;
        executorService = Executors.newSingleThreadExecutor();
    }

    public void start() {
        executorService.submit(this::reading);
    }

    public void stop() {
        executorService.shutdownNow();
    }

    public ByteBuffer read() {
        ByteBuffer size = ByteBuffer.allocate(4);
        ByteBuffer data;
        try {
            int ret = socketChannel.read(size);
            if (ret == -1) {
                return null;
            }
            data = ByteBuffer.allocate(size.getInt());
            socketChannel.read(data);
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

    public void write(ByteBuffer data) {
        try {
            socketChannel.write(data);
        } catch (IOException e) {
            disconnect();
        }
    }

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
}
