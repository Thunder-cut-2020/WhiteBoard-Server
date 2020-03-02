/*
 * Data.java
 * Author: Seokjin Yoon
 * Created Date: 2020-02-29
 */

package com.thunder_cut.data;

import com.thunder_cut.encryption.SymmetricKeyEncryption;

import javax.crypto.SecretKey;
import java.nio.ByteBuffer;

public class Data {
    public final DataType dataType;
    private int srcId;
    private byte[] data;

    /**
     * Create wrapped data.
     *
     * @param dataType data type of data
     * @param srcId    source connection's ID
     * @param data     plain data
     */
    public Data(DataType dataType, int srcId, byte[] data) {
        this.dataType = dataType;
        this.srcId = srcId;
        this.data = data;
    }

    /**
     * Create wrapped data by encrypted data.
     *
     * @param encryptedData encrypted data
     * @param secretKey     a symmetric key
     */
    public Data(byte[] encryptedData, SecretKey secretKey) {
        SymmetricKeyEncryption encryption = new SymmetricKeyEncryption(secretKey);
        byte[] decrypted = encryption.decrypt(encryptedData);
        ByteBuffer byteBuffer = ByteBuffer.wrap(decrypted);
        dataType = DataType.valueOf(byteBuffer.getChar());
        srcId = byteBuffer.getInt();
        data = new byte[byteBuffer.limit() - byteBuffer.position()];
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

    /**
     * Returns data size and encrypted data for transfer.
     *
     * @param secretKey a symmetric key
     * @return data size and encrypted data.
     */
    public ByteBuffer toEncrypted(SecretKey secretKey) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(Character.BYTES + Integer.BYTES + data.length);
        byteBuffer.putChar(dataType.code);
        byteBuffer.putInt(srcId);
        byteBuffer.put(data);

        SymmetricKeyEncryption encryption = new SymmetricKeyEncryption(secretKey);
        byte[] encrypted = encryption.encrypt(byteBuffer.array());
        byteBuffer = ByteBuffer.allocate(Integer.BYTES + encrypted.length);
        byteBuffer.putInt(encrypted.length);
        byteBuffer.put(encrypted);

        return byteBuffer;
    }
}
