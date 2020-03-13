/*
 * WhiteBoardServer.java
 * Author: Seokjin Yoon
 * Created Date: 2020-03-13
 */

package com.thunder_cut;

import com.thunder_cut.data.ImageSender;
import com.thunder_cut.encryption.SymmetricKeyEncryption;
import com.thunder_cut.netio.Server;

import javax.crypto.SecretKey;

public class WhiteBoardServer {
    public static final int PORT = 3001;
    public static final int STRENGTH = 256;
    public static final int FRAME_PER_SECOND = 30;

    private Server server;
    private SecretKey secretKey;
    private ImageSender imageSender;

    public WhiteBoardServer() {
        server = new Server(this, PORT);
        secretKey = SymmetricKeyEncryption.generateKey(STRENGTH);
        imageSender = new ImageSender(this);
    }

    public void start() {
        server.start();
        imageSender.start(FRAME_PER_SECOND);
    }

    public void stop() {
        imageSender.stop();
        server.stop();
    }

    public Server getServer() {
        return server;
    }

    public SecretKey getSecretKey() {
        return secretKey;
    }

    public ImageSender getImageSender() {
        return imageSender;
    }
}
