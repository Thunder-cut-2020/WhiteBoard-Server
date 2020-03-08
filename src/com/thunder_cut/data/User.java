/*
 * User.java
 * Author: Seokjin Yoon
 * Created Date: 2020-03-03
 */

package com.thunder_cut.data;

public class User {
    public final int id;
    private String name;
    private byte[] image;
    private boolean imageUpdated;

    public User(int id) {
        this.id = id;
        name = Naming.generateName();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name.strip();
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
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
