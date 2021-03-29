package com.io.chatapp.Model;

import java.util.Date;

public class Message {
    private String content;
    private Boolean isSendfromRoot;
    private long time;

    public Message(String content,Boolean isSendfromRoot){
        this.content = content;
        this.isSendfromRoot = isSendfromRoot;
        this.time = new Date().getTime();
    }
    public Message() {
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }


    public Boolean getSendfromRoot() {
        return isSendfromRoot;
    }

    public void setSendfromRoot(Boolean sendfromRoot) {
        isSendfromRoot = sendfromRoot;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
