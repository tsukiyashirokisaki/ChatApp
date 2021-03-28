package com.io.chatapp.Model;

import java.util.Date;

public class Message {
    private String content;
    private String sender_uid;
    private String receiver_uid;
    private long time;

    public Message(String content, String sender_uid,String receiver_uid){
        this.content = content;
        this.sender_uid = sender_uid;
        this.receiver_uid = receiver_uid;
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
    public String getSender_uid() {
        return sender_uid;
    }
    public void setSender_uid(String sender_uid) {
        this.sender_uid = sender_uid;
    }
    public String getReceiver_uid() {
        return receiver_uid;
    }
    public void setReceiver_uid(String receiver_uid) {
        this.receiver_uid = receiver_uid;
    }
    public long getTime() {
        return time;
    }
    public void setTime(long time) {
        this.time = time;
    }
}
