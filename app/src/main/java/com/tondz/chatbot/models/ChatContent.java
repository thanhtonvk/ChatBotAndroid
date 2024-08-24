package com.tondz.chatbot.models;


public class ChatContent {
    private String senderUser, receiveUser, message;



    public String getSenderUser() {
        return senderUser;
    }

    public void setSenderUser(String senderUser) {
        this.senderUser = senderUser;
    }

    public String getReceiveUser() {
        return receiveUser;
    }

    public void setReceiveUser(String receiveUser) {
        this.receiveUser = receiveUser;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ChatContent( String senderUser, String receiveUser, String message) {

        this.senderUser = senderUser;
        this.receiveUser = receiveUser;
        this.message = message;
    }

    public ChatContent() {

    }
}
