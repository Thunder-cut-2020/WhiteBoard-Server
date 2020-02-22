/*
 * ClientInformation.java
 * Author : Arakene
 * Created Date : 2020-02-04
 */
package com.thunder_cut.socket;

import com.thunder_cut.processing.data.DataType;
import com.thunder_cut.processing.data.ReceivedData;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * This class has information, read and write about connected client
 */
public class ClientInformation {
    private String name;
    private SocketChannel client;
    private ClientCallback callback;
    private boolean op;

    public ClientInformation(SocketChannel client, ClientCallback callback) {
        this.client = client;
        this.callback = callback;
        op = false;
    }

    public SocketChannel getClient() {
        return client;
    }

    public void read() {
        new Thread(this::reading).start();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isOp() {
        return op;
    }

    public void setOp(boolean op) {
        this.op = op;
    }

    /**
     * 1. Allocate ByteBuffer with header size and read header
     * 2. Figure out data type, data size and allocate ByteBuffer with data size
     * 3. Receive data until buffer has no space
     * 4. Give ID, data type and data(no header) to write
     */
    private void reading() {
        while (true) {
            ByteBuffer buffer = ByteBuffer.allocate(6);
            try {
                int ret = client.read(buffer);
                if (ret == -1) {
                    break;
                }
            } catch (IOException e) {
                break;
            }
            buffer.flip();

            char type = buffer.getChar();
            int size = buffer.getInt();
            buffer = ByteBuffer.allocate(size);

            while (buffer.hasRemaining()) {
                try {
                    client.read(buffer);
                } catch (IOException e) {
                    break;
                }
            }

            buffer.flip();
            DataType dataType = DataType.valueOf(type);
            byte[] data = buffer.array();
            ReceivedData receivedData = new ReceivedData(this, DataType.valueOf(type), buffer);
            callback.received(receivedData);
        }
        callback.disconnected(this);
    }
}
