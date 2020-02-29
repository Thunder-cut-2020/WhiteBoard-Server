/*
 * Data.java
 * Author: Seokjin Yoon
 * Created Date: 2020-02-29
 */

package com.thunder_cut.netio;

import com.thunder_cut.encryption.SymmetricKeyEncryption;

import javax.crypto.SecretKey;
import java.nio.ByteBuffer;

public class Data {
    public final DataType dataType;
    private int srcId;
    private byte[] data;

    public Data(DataType dataType, int srcId, byte[] data) {
        this.dataType = dataType;
        this.srcId = srcId;
        this.data = data;
    }

    public Data(byte[] encryptedData, SecretKey secretKey) {
        SymmetricKeyEncryption encryption = new SymmetricKeyEncryption(secretKey);
        byte[] decrypted = encryption.decrypt(encryptedData);
        ByteBuffer byteBuffer = ByteBuffer.wrap(decrypted);
        dataType = DataType.valueOf(byteBuffer.getChar());
        srcId = byteBuffer.getInt();
        byteBuffer.get(data);
    }

    public int getSrcId() {
        return srcId;
    }

    public void setSrcId(int srcId) {
        this.srcId = srcId;
    }

    public byte[] getData() {
        return data;
    }

    public ByteBuffer toEncrypted(SecretKey secretKey) {
        SymmetricKeyEncryption encryption = new SymmetricKeyEncryption(secretKey);
        byte[] encrypted = encryption.encrypt(data);
        ByteBuffer byteBuffer = ByteBuffer.allocate(Integer.BYTES + encrypted.length);
        byteBuffer.putInt(encrypted.length);
        byteBuffer.put(encrypted);
        return byteBuffer;
    }
}
