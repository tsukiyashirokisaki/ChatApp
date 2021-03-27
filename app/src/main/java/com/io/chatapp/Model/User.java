package com.io.chatapp.Model;

public class User {
    private  String name;
    private String uid;
    private String password;
    private String image;
    private String email;

    public User(String name, String uid, String password, String image, String email) {
        this.name = name;
        this.uid = uid;
        this.password = password;
        this.image = image;
        this.email = email;
    }

    public User() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
