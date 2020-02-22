/*
 * MainClass.java
 * Author: Seokjin Yoon
 * Created Date: 2020-01-10
 */

package com.thunder_cut;

import com.thunder_cut.socket.SyncServer;

public class MainClass {
    public static void main(String[] args) {
        SyncServer server;
        if (args.length == 0) {
            server = new SyncServer();
        } else if (args.length == 1) {
            int port = Integer.parseInt(args[0]);
            server = new SyncServer(port);
        } else {
            String ip = args[0];
            int port = Integer.parseInt(args[1]);
            server = new SyncServer(ip, port);
        }
        new Thread(server).start();
    }
}
