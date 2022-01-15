package com.example.mdpmessenger.Models;

public class ChatList {
    public String id;
    public Boolean newMessage;

    public ChatList(String id, Boolean newMessage) {
        this.id = id;
        this.newMessage = newMessage;
    }

    public ChatList() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Boolean getNewMessage() {
        return newMessage;
    }

    public void setNewMessage(Boolean newMessage) {
        this.newMessage = newMessage;
    }
}