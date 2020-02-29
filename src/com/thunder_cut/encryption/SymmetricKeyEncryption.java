/*
 * SymmetricKeyEncryption.java
 * Author: Seokjin Yoon
 * Created Date: 2020-02-29
 */

package com.thunder_cut.encryption;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import java.util.Base64;

public class SymmetricKeyEncryption {
    public static final String ALGORITHM = "AES";

    public final SecretKey symmetricKey;
    private final Cipher encryption;
    private final Cipher decryption;
    private final Base64.Encoder encoder;
    private final Base64.Decoder decoder;

    public SymmetricKeyEncryption(SecretKey symmetricKey) {
        this.symmetricKey = symmetricKey;
        encryption = makeCipher(Cipher.ENCRYPT_MODE);
        decryption = makeCipher(Cipher.DECRYPT_MODE);
        encoder = Base64.getEncoder();
        decoder = Base64.getDecoder();
    }

    public byte[] encrypt(byte[] data) {
        try {
            byte[] encrypted = encryption.doFinal(data);
            return encoder.encode(encrypted);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public byte[] decrypt(byte[] data) {
        try {
            byte[] decoded = decoder.decode(data);
            return decryption.doFinal(decoded);
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
