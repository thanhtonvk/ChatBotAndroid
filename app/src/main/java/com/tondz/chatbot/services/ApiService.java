package com.tondz.chatbot.services;

import com.tondz.chatbot.models.ChatRequest;
import com.tondz.chatbot.models.ChatResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {
    @POST("/chat")
        // Hoặc "/chat_normal" nếu không dùng file PDF
    Call<ChatResponse> sendMessage(@Body ChatRequest request);
}
