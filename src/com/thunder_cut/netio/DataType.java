/*
 * DataType.java
 * Author: Seokjin Yoon
 * Created Date: 2020-02-29
 */

package com.thunder_cut.netio;

public enum DataType {
    IMAGE('I'),
    COMMAND('C'),
    MESSAGE('M'),
    LIST('L');

    public final char code;

    DataType(char code) {
        this.code = code;
    }

    public static DataType valueOf(char code) {
        for (DataType dataType : DataType.values()) {
            if (dataType.code == code) {
                return dataType;
            }
        }
        return null;
    }
}
