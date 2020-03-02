/*
 * ConnectionCallback.java
 * Author: Seokjin Yoon
 * Created Date: 2020-02-29
 */

package com.thunder_cut.netio;

import java.nio.ByteBuffer;

public interface ConnectionCallback {
    void received(Connection source, ByteBuffer data);

    void disconnected(Connection connection);
}
