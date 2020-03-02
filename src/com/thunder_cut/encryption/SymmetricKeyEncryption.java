/*
 * SymmetricKeyEncryption.java
 * Author: Seokjin Yoon
 * Created Date: 2020-02-29
 */

package com.thunder_cut.encryption;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.SecureRandom;

public class SymmetricKeyEncryption {
    public static final String ALGORITHM = "AES";

    public final SecretKey symmetricKey;
    private final Cipher encryption;
    private final Cipher decryption;

    /**
     * Create a SymmectricKeyEncryption.
     *
     * @param symmetricKey a symmetric key
     */
    public SymmetricKeyEncryption(SecretKey symmetricKey) {
        this.symmetricKey = symmetricKey;
        encryption = makeCipher(Cipher.ENCRYPT_MODE);
        decryption = makeCipher(Cipher.DECRYPT_MODE);
    }

    /**
     * Encrypt data with a symmetric key.
     *
     * @param data plain data
     * @return encrypted data
     */
    public byte[] encrypt(byte[] data) {
        try {
            return encryption.doFinal(data);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Decrypt data with a symmetric key.
     *
     * @param data encrypted data
     * @return decrypted data
     */
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

    /**
     * Generate a symmetric key/
     *
     * @param strength length of key
     * @return a symmetric key
     */
    public static SecretKey generateKey(int strength) {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance(ALGORITHM);
            keyGenerator.init(strength, SecureRandom.getInstance("SHA1PRNG"));
            return keyGenerator.generateKey();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
