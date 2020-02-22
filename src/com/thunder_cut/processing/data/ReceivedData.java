/*
 * ReceivedData.java
 * Author : Arakene
 * Created Date : 2020-02-14
 */
package com.thunder_cut.processing.data;

import com.thunder_cut.socket.ClientInformation;

import java.nio.ByteBuffer;

public class ReceivedData {

    private DataType dataType;
    private ByteBuffer buffer;
    private ClientInformation src;

    public ReceivedData(ClientInformation src, DataType dataType, ByteBuffer buffer) {
        this.src = src;
        this.dataType = dataType;
        this.buffer = buffer;
    }

    public ClientInformation getSrc() {
        return src;
    }

    public DataType getDataType() {
        return dataType;
    }

    public ByteBuffer getBuffer() {
        return buffer;
    }
}
