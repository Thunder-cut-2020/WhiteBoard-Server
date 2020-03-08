/*
 * ImageSender.java
 * Author: Seokjin Yoon
 * Created Date: 2020-03-08
 */

package com.thunder_cut.data;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ImageSender {
    private ScheduledExecutorService scheduledExecutorService;
    private long fps;

    public ImageSender() {
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
    }

    public long getFps() {
        return fps;
    }
}
