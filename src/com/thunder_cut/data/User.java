/*
 * User.java
 * Author: Seokjin Yoon
 * Created Date: 2020-03-03
 */

package com.thunder_cut.data;

import java.util.Arrays;

public class User {
    public final int id;
    private String name;
    private boolean operator;
    private byte[] image;
    private boolean imageUpdated;

    public User(int id) {
        this.id = id;
        name = Naming.generateName();
        operator = false;
    }

    public String getName() {
        if (operator) {
            return '*' + name;
        } else {
            return name;
        }
    }

    public void setName(String name) {
        if (name.charAt(0) == '*') {
            return;
        }

        this.name = name.strip();
    }

    public boolean isOperator() {
        return operator;
    }

    public void setOperator(boolean operator) {
        this.operator = operator;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        if (Arrays.equals(this.image, image)) {
            return;
        }

        this.image = image;
        imageUpdated = true;
    }

    public boolean isImageUpdated() {
        return imageUpdated;
    }

    public void setImageUpdated(boolean imageUpdated) {
        this.imageUpdated = imageUpdated;
    }
}
