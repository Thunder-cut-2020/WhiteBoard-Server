/*
 * CommandType.java
 * Author: Seokjin Yoon
 * Created Date: 2020-02-29
 */

package com.thunder_cut.command;

public enum CommandType {
    HELP("/HELP"),
    NAME("/NAME"),
    IGNORE("/IGNORE"),
    KICK("/KICK"),
    OP("/OP");

    public final String command;

    CommandType(String command) {
        this.command = command;
    }

    public static CommandType getCommand(String command) {
        for (CommandType commandType : CommandType.values()) {
            if (commandType.command.equalsIgnoreCase(command)) {
                return commandType;
            }
        }
        return null;
    }
}
