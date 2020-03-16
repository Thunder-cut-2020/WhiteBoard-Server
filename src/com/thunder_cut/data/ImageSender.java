/*
 * ImageSender.java
 * Author: Seokjin Yoon
 * Created Date: 2020-03-08
 */

package com.thunder_cut.data;

import com.thunder_cut.WhiteBoardServer;
import com.thunder_cut.netio.Connection;

import javax.crypto.SecretKey;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ImageSender {
    private WhiteBoardServer owner;
    private ScheduledExecutorService scheduledExecutorService;
    private long fps;

    public ImageSender(WhiteBoardServer owner) {
        this.owner = owner;
        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
    }

    public void start(long fps) {
        this.fps = fps;
        scheduledExecutorService.scheduleAtFixedRate(this::run, 0, 1000L / fps, TimeUnit.MILLISECONDS);
    }

    public void stop() {
        scheduledExecutorService.shutdown();
    }

    private void run() {
        SecretKey secretKey = owner.getSecretKey();
        Connection[] connections = owner.getServer().getConnections().toArray(new Connection[0]);
        for (Connection connection : connections) {
            User user = connection.getUser();
            if (user.isImageUpdated()) {
                user.setImageUpdated(false);
                owner.getServer().send(new Data(DataType.IMAGE, user.id, user.getImage()).toEncrypted(secretKey));
            }
        }
    }

    public void refresh() {
        SecretKey secretKey = owner.getSecretKey();
        Connection[] connections = owner.getServer().getConnections().toArray(new Connection[0]);
        for (Connection connection : connections) {
            User user = connection.getUser();
            byte[] image = user.getImage();
            if (Objects.nonNull(image)) {
                owner.getServer().send(new Data(DataType.IMAGE, user.id, image).toEncrypted(secretKey));
            }
        }
    }

    public long getFps() {
        return fps;
    }
}
