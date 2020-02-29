/*
 * CommandType.java
 * Author: Seokjin Yoon
 * Created Date: 2020-02-29
 */

package com.thunder_cut.command;

public enum CommandType {
    NAME("/NAME"),
    IGNORE("/IGNORE"),
    KICK("/KICK"),
    OP("/OP");

    public final String command;

    CommandType(String command) {
        this.command = command;
    }
}
