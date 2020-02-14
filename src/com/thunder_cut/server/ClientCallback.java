/*
 * ClientCallback.java
 * Author: Seokjin Yoon
 * Created Date: 2020-02-14
 */

package com.thunder_cut.server;

public interface ClientCallback {
    void received(ClientInformation client, DataType type, byte[] data);

    void disconnected(ClientInformation client);
}
