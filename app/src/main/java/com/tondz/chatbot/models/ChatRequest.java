package com.tondz.chatbot.models;

public class ChatRequest {
    private String message;
    private boolean user_pdf;

    public ChatRequest(String message, boolean user_pdf) {
        this.message = message;
        this.user_pdf = user_pdf;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isUser_pdf() {
        return user_pdf;
    }

    public void setUser_pdf(boolean user_pdf) {
        this.user_pdf = user_pdf;
    }
}
