package com.tondz.chatbot;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tondz.chatbot.adapters.ChatAdapter;
import com.tondz.chatbot.models.ChatContent;
import com.tondz.chatbot.models.ChatRequest;
import com.tondz.chatbot.models.ChatResponse;
import com.tondz.chatbot.services.ApiService;
import com.tondz.chatbot.services.RetrofitClient;

import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.text.TextContentRenderer;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RAGActivity extends AppCompatActivity {
    private final List<ChatContent> chatList = new ArrayList<>();
    private ChatAdapter chatAdapter;
    private EditText edt_content;
    private TextToSpeech textToSpeech;
    private static final int REQUEST_CODE_SPEECH_INPUT = 1000;
    private static final int REQUEST_CODE_PERMISSION = 12000;
    private boolean isVoice = false;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ragactivity);
        initView();
        requestAudioPermission();
    }

    private void initView() {
        apiService = RetrofitClient.getApiService();
        edt_content = findViewById(R.id.edt_content);
        RecyclerView recyclerView = findViewById(R.id.lv_chat);
        Button btn_send = findViewById(R.id.btn_send);
        ImageButton btnVoice = findViewById(R.id.btnSound);
        ImageButton btnMic = findViewById(R.id.btnMic);

        chatAdapter = new ChatAdapter(this, chatList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(chatAdapter);

        textToSpeech = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                textToSpeech.setLanguage(Locale.forLanguageTag("vi-VN"));
            }
        });

        btn_send.setOnClickListener(v -> sendMessage());
        btnMic.setOnClickListener(v -> startSpeechToText());
        btnVoice.setOnClickListener(v -> {
            isVoice = !isVoice;
            btnVoice.setImageResource(isVoice ? R.drawable.volume : R.drawable.mute);
        });
    }

    private void requestAudioPermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.RECORD_AUDIO}, REQUEST_CODE_PERMISSION);
        }
    }

    private void startSpeechToText() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak to text");

        try {
            startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT);
        } catch (Exception e) {
            Toast.makeText(this, "Thiết bị không hỗ trợ tính năng này", Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private void sendMessage() {
        String message = edt_content.getText().toString().trim();
        if (message.isEmpty()) {
            Toast.makeText(this, "Không được bỏ trống", Toast.LENGTH_SHORT).show();
            return;
        }

        chatList.add(new ChatContent("1", "2", message));
        chatAdapter.notifyDataSetChanged();
        edt_content.setText("");

        apiService.sendMessage(new ChatRequest(message, true)).enqueue(new Callback<ChatResponse>() {
            @Override
            public void onResponse(Call<ChatResponse> call, Response<ChatResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String reply = response.body().getReply();
                    chatList.add(new ChatContent("2", "1", reply));
                    chatAdapter.notifyDataSetChanged();

                    if (isVoice) {
                        speakText(reply);
                    }
                }
            }

            @Override
            public void onFailure(Call<ChatResponse> call, Throwable t) {
                Log.e("ChatActivity", "Lỗi gửi tin nhắn: " + t.getMessage());
            }
        });
    }

    private void speakText(String text) {
        Parser parser = Parser.builder().build();
        Node document = parser.parse(text);
        String plainText = TextContentRenderer.builder().build().render(document);
        textToSpeech.speak(plainText, TextToSpeech.QUEUE_FLUSH, null, null);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_SPEECH_INPUT && resultCode == RESULT_OK && data != null) {
            List<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (result != null && !result.isEmpty()) {
                edt_content.setText(result.get(0));
                sendMessage();
            }
        }
    }
}