/*
 * Attachment.java
 * Author : Arakene
 * Created Date : 2020-01-15
 */
package com.thunder_cut.socket;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.Vector;

/**
 *  This class have information about connected client
 *  server = connected server
 *  client = want to connect to server
 *  buffer = save data for receive or send
 *  isReadMode = If read is running true else false
 *  clientGroup = collection about connected clients
 */

public class Attachment {
    private AsynchronousServerSocketChannel server;
    private AsynchronousSocketChannel client;
    private ByteBuffer buffer;
    private boolean isReadMode;
    private Vector<Attachment> clientGroup;

    public Vector<Attachment> getClientGroup() {
        return clientGroup;
    }

    public void setClientGroup(Vector<Attachment> clientGroup) {
        this.clientGroup = clientGroup;
    }

    public AsynchronousServerSocketChannel getServer() {
        return server;
    }

    public void setServer(AsynchronousServerSocketChannel server) {
        this.server = server;
    }

    public AsynchronousSocketChannel getClient() {
        return client;
    }

    public void setClient(AsynchronousSocketChannel client) {
        this.client = client;
    }

    public ByteBuffer getBuffer() {
        return buffer;
    }

    public void setBuffer(ByteBuffer buffer) {
        this.buffer = buffer;
    }

    public boolean isReadMode() {
        return isReadMode;
    }

    public void setReadMode(boolean readMode) {
        isReadMode = readMode;
    }
}