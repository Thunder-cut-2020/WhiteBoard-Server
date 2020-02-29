/*
 * SymmetricKeyEncryption.java
 * Author: Seokjin Yoon
 * Created Date: 2020-02-29
 */

package com.thunder_cut.encryption;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;

public class SymmetricKeyEncryption {
    public static final String ALGORITHM = "AES";

    public final SecretKey symmetricKey;
    private final Cipher encryption;
    private final Cipher decryption;

    public SymmetricKeyEncryption(SecretKey symmetricKey) {
        this.symmetricKey = symmetricKey;
        encryption = makeCipher(Cipher.ENCRYPT_MODE);
        decryption = makeCipher(Cipher.DECRYPT_MODE);
    }

    public byte[] encrypt(byte[] data) {
        try {
            return encryption.doFinal(data);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public byte[] decrypt(byte[] data) {
        try {
            return decryption.doFinal(data);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private Cipher makeCipher(int mode) {
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(mode, symmetricKey);
            return cipher;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
