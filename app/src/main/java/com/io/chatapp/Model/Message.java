package com.io.chatapp.Model;

import java.util.Date;

public class Message {
    private String MessageText;
    private String MessageUser;
    private long MessageTime;

    public Message(String messageText, String messageUser){
        MessageText = messageText;
        MessageUser = messageUser;
        MessageTime = new Date().getTime();
    }

    public Message() {
    }

    public String getMessageText() {
        return MessageText;
    }

    public void setMessageText(String messageText) {
        MessageText = messageText;
    }

    public String getMessageUser() {
        return MessageUser;
    }

    public void setMessageUser(String messageUser) {
        MessageUser = messageUser;
    }

    public long getMessageTime() {
        return MessageTime;
    }

    public void setMessageTime(long messageTime) {
        MessageTime = messageTime;
    }
}
