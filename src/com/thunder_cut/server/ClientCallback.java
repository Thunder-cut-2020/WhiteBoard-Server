/*
 * ClientCallback.java
 * Author: Seokjin Yoon
 * Created Date: 2020-02-14
 */

package com.thunder_cut.server;

import com.thunder_cut.server.data.ReceivedData;

public interface ClientCallback {
    void received(ReceivedData data);

    void disconnected(ClientInformation client);

    int getId(ClientInformation client);
}
