/*
 * ImageSender.java
 * Author: Seokjin Yoon
 * Created Date: 2020-03-08
 */

package com.thunder_cut.data;

import com.thunder_cut.netio.Connection;
import com.thunder_cut.netio.Server;

import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ImageSender {
    private ScheduledExecutorService scheduledExecutorService;
    private long fps;
    private Server server;

    public ImageSender(Server server) {
        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        this.server = server;
    }

    public void start(long fps) {
        this.fps = fps;
        scheduledExecutorService.scheduleAtFixedRate(this::run, 0, 1000L / fps, TimeUnit.MILLISECONDS);
    }

    public void stop() {
        scheduledExecutorService.shutdown();
    }

    private void run() {
        Connection[] connections = server.getConnections().toArray(new Connection[0]);
        for (Connection connection : connections) {
            User user = connection.getUser();
            if (user.isImageUpdated()) {
                user.setImageUpdated(false);
                server.send(new Data(DataType.IMAGE, user.id, user.getImage()).toEncrypted(server.getSecretKey()));
            }
        }
    }

    public void refresh() {
        Connection[] connections = server.getConnections().toArray(new Connection[0]);
        for (Connection connection : connections) {
            User user = connection.getUser();
            byte[] image = user.getImage();
            if (Objects.nonNull(image)) {
                server.send(new Data(DataType.IMAGE, user.id, image).toEncrypted(server.getSecretKey()));
            }
        }
    }

    public long getFps() {
        return fps;
    }
}
