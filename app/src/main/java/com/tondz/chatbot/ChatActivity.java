package com.tondz.chatbot;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
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

import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.GenerateContentResponse;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.tondz.chatbot.adapters.ChatAdapter;
import com.tondz.chatbot.models.ChatContent;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ChatActivity extends AppCompatActivity {
    GenerativeModel gm;
    GenerativeModelFutures model;
    List<ChatContent> chatList = new ArrayList<>();
    ChatAdapter chatAdapter;
    RecyclerView recyclerView;
    Button btn_send;
    EditText edt_content;
    TextToSpeech textToSpeech;
    private static final int REQUEST_CODE_SPEECH_INPUT = 1000;
    private static final int REQUEST_CODE_PERMISSION = 12000;
    ImageButton btnVoice;
    boolean isVoice = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        initView();
        initModel();
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        onClick();
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.RECORD_AUDIO}, REQUEST_CODE_PERMISSION);
        }

    }

    private void startSpeechToText() {
        Intent intent
                = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,
                Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak to text");

        try {
            startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT);
        } catch (Exception e) {
            Toast.makeText(this, "Thiết bị không hỗ trợ tính năng này", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_SPEECH_INPUT && resultCode == RESULT_OK && data != null) {
            ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (result != null && !result.isEmpty()) {
                edt_content.setText(result.get(0));  // Hiển thị văn bản sau khi chuyển đổi
                String message = edt_content.getText().toString().trim();
                if (!message.isEmpty()) {
                    chatList.add(new ChatContent("1", "2", message));
                    chatAdapter.notifyDataSetChanged();
                    generateText(message);
                } else {
                    Toast.makeText(getApplicationContext(), "Khong duoc bo trong", Toast.LENGTH_SHORT).show();
                }
                edt_content.setText("");
            }
        }
    }

    private void initView() {
        btnVoice = findViewById(R.id.btnSound);
        btn_send = findViewById(R.id.btn_send);
        edt_content = findViewById(R.id.edt_content);
        recyclerView = findViewById(R.id.lv_chat);
        chatAdapter = new ChatAdapter(getApplicationContext(), chatList);
        recyclerView.setAdapter(chatAdapter);
        textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if (i != TextToSpeech.ERROR) {
                    textToSpeech.setLanguage(Locale.forLanguageTag("vi-VN"));
                }
            }
        });
    }

    private void initModel() {
        gm = new GenerativeModel(/* modelName */ "gemini-1.5-flash", getResources().getString(R.string.api_key));
        model = GenerativeModelFutures.from(gm);
    }

    private void onClick() {
        btn_send.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onClick(View v) {
                String message = edt_content.getText().toString().trim();
                if (!message.isEmpty()) {
                    chatList.add(new ChatContent("1", "2", message));
                    chatAdapter.notifyDataSetChanged();
                    generateText(message);
                } else {
                    Toast.makeText(getApplicationContext(), "Khong duoc bo trong", Toast.LENGTH_SHORT).show();
                }
                edt_content.setText("");
            }
        });
        findViewById(R.id.btnMic).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSpeechToText();
            }
        });
        btnVoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isVoice) {
                    btnVoice.setImageResource(R.drawable.mute);
                    isVoice = false;
                } else {
                    btnVoice.setImageResource(R.drawable.volume);
                    isVoice = true;
                }
            }
        });
    }

    private void generateText(String value) {
        Content content = new Content.Builder()
                .addText(value)
                .build();
        ListenableFuture<GenerateContentResponse> response = model.generateContent(content);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onSuccess(GenerateContentResponse result) {
                    String resultText = result.getText();
                    if (isVoice) {
                        textToSpeech.speak(resultText, TextToSpeech.QUEUE_FLUSH, null);
                    }
                    chatList.add(new ChatContent("2", "1", resultText));
                    chatAdapter.notifyDataSetChanged();
                }

                @Override
                public void onFailure(Throwable t) {
                    t.printStackTrace();
                }
            }, this.getMainExecutor());
        }
    }
}