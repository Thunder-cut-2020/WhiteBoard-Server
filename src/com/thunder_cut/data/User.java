/*
 * User.java
 * Author: Seokjin Yoon
 * Created Date: 2020-03-03
 */

package com.thunder_cut.data;

public class User {
    public final int id;
    private String name;

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
}
