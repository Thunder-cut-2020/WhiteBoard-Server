/*
 * DataType.java
 * Author: Seokjin Yoon
 * Created Date: 2020-02-29
 */

package com.thunder_cut.netio;

public enum DataType {
    HELLO('H'),
    IMAGE('I'),
    COMMAND('C'),
    MESSAGE('M');

    public final char code;

    DataType(char code) {
        this.code = code;
    }
}
