/*
 * SendingData.java
 * Author : Arakene
 * Created Date : 2020-02-07
 */
package com.thunder_cut.server.data;

import com.thunder_cut.server.ClientInfo;

import java.nio.ByteBuffer;

/**
 * Generate data for send to all client
 */
public class SendingData {

    public final DataType dataType;
    public final ClientInfo src;
    public final ClientInfo dest;
    public final byte[] data;

    public SendingData(ClientInfo src, ClientInfo dest, DataType dataType, byte[] data) {
        this.src = src;
        this.dest = dest;
        this.dataType = dataType;
        this.data = data;
    }

    public ByteBuffer identifyType(){
        if(dataType == DataType.MSG){
            return toByteBuffer(src.getName());
        }
        else{
            return toByteBuffer();
        }
    }
    /**
     * Generate header and attach data
     * Header must promised sequence => dataType(char), srcID(int), destID(int), dataSize(int) and data(byte[])
     * @return generated ByteBuffer with given data
     */
    private synchronized ByteBuffer toByteBuffer() {
        ByteBuffer buffer = ByteBuffer.allocate(data.length + 14);
        buffer.putChar(dataType.type);
        buffer.putInt(src.ID);
        buffer.putInt(dest.ID);
        buffer.putInt(data.length);
        buffer.put(data);

        buffer.flip();

        return buffer;
    }
    private synchronized ByteBuffer toByteBuffer(String name) {

        String nickName = name.concat("::");
        ByteBuffer buffer = ByteBuffer.allocate(data.length + 14 + nickName.getBytes().length);
        buffer.putChar(dataType.type);
        buffer.putInt(src.ID);
        buffer.putInt(dest.ID);

        int dataSize = data.length + nickName.length();
        buffer.putInt(dataSize);

        buffer.put(nickName.getBytes());
        buffer.put(data);

        buffer.flip();

//        System.out.println(buffer.toString());

        return buffer;
    }

    public ClientInfo getDest(){
        return dest;
    }
}
