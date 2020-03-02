/*
 * PublicKeyEncryption.java
 * Author: Seokjin Yoon
 * Created Date: 2020-02-29
 */

package com.thunder_cut.encryption;

import javax.crypto.Cipher;
import java.security.*;
import java.security.spec.X509EncodedKeySpec;
import java.util.Objects;

public class PublicKeyEncryption {
    public static final String ALGORITHM = "RSA";

    public final PublicKey publicKey;
    public final PrivateKey privateKey;
    private Cipher encryption;
    private Cipher decryption;

    /**
     * Create a PublicKeyEncryption.
     *
     * @param keyPair a public key and a private key
     */
    public PublicKeyEncryption(KeyPair keyPair) {
        this(keyPair.getPublic(), keyPair.getPrivate());
    }

    /**
     * Create a PublicKeyEncryption.
     *
     * @param publicKey  a public key
     * @param privateKey a private key
     */
    public PublicKeyEncryption(PublicKey publicKey, PrivateKey privateKey) {
        this.publicKey = publicKey;
        this.privateKey = privateKey;
        if (Objects.nonNull(publicKey)) {
            encryption = makeCipher(Cipher.ENCRYPT_MODE, publicKey);
        }
        if (Objects.nonNull(privateKey)) {
            decryption = makeCipher(Cipher.DECRYPT_MODE, privateKey);
        }
    }

    /**
     * Encrypt data with a public key.
     *
     * @param data plain data
     * @return encrypted data
     */
    public byte[] encrypt(byte[] data) {
        if (Objects.isNull(publicKey)) {
            return null;
        }

        try {
            return encryption.doFinal(data);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Decrypt data with a private key.
     *
     * @param data encrypted data
     * @return decrypted data
     */
    public byte[] decrypt(byte[] data) {
        if (Objects.isNull(privateKey)) {
            return null;
        }

        try {
            return decryption.doFinal(data);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private Cipher makeCipher(int mode, Key key) {
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(mode, key);
            return cipher;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Make a public key from byte[].
     *
     * @param publicKey byte[]
     * @return PublicKey
     */
    public static PublicKey makePublicKey(byte[] publicKey) {
        try {
            X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(publicKey);
            KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
            return keyFactory.generatePublic(x509EncodedKeySpec);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
