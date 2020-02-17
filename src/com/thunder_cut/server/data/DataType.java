package com.thunder_cut.server.data;

/**
 * This type used to communication, server to client, client to server
 */
public enum DataType {
    IMG('I'), MSG('M'), CMD('C');

    public final char type;

    DataType(char type) {
        this.type = type;
    }

    public static DataType valueOf(char type) {
        for (DataType dataType : DataType.values()) {
            if (dataType.type == type) {
                return dataType;
            }
        }
        return null;
    }
}
