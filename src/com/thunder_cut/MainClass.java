/*
 * MainClass.java
 * Author: Seokjin Yoon
 * Created Date: 2020-01-10
 */

package com.thunder_cut;

import com.thunder_cut.netio.Server;

public class MainClass {
    public static void main(String[] args) {
        Server server = new Server(3001);
        server.start();
    }
}
