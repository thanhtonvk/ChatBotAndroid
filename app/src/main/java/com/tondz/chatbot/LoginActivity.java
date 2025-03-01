package com.tondz.chatbot;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.tondz.chatbot.databinding.ActivityLoginBinding;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    ActivityLoginBinding binding;
    FirebaseDatabase database;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        database = FirebaseDatabase.getInstance();
        reference = database.getReference();
        onClick();
        loadAccount();
    }


    private void onClick() {
        binding.btnLogin.setOnClickListener(v -> {
            login();
        });
    }


    private void login() {
        String email = binding.edtEmail.getText().toString();
        String password = binding.edtPassword.getText().toString();
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(getApplicationContext(), " Thông tin không được bỏ trong ", Toast.LENGTH_SHORT).show();
        } else {
            ProgressDialog dialog = new ProgressDialog(LoginActivity.this);
            dialog.setTitle("Đang đăng nhập");
            dialog.show();
            reference.child("users").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    boolean isExsist = false;
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()
                    ) {
                        String user = dataSnapshot.getKey();
                        Map<String, String> map = dataSnapshot.getValue(new GenericTypeIndicator<Map<String, String>>() {
                        });
                        String pass = map.get("password");
                        Log.d("TAG", "snapshot: " + pass);
                        if (user.equalsIgnoreCase(email)) {
                            if (pass.equalsIgnoreCase(password)) {
                                Toast.makeText(getApplicationContext(), "Đăng nhập thành công", Toast.LENGTH_SHORT).show();
                                saveAccount(email, password);
                                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                dialog.dismiss();
                                isExsist = true;
                                finish();
                            }
                        }
                    }
                    if (!isExsist) {
                        Toast.makeText(getApplicationContext(), "Tài khoản hoặc mật khẩu không chinh xác", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    dialog.dismiss();
                }
            });

        }
    }

    private void saveAccount(String email, String password) {
        SharedPreferences prefs = getSharedPreferences("Account", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("email", email);
        editor.putString("password", password);
        editor.apply();
    }

    private void loadAccount() {
        SharedPreferences prefs = getSharedPreferences("Account", Context.MODE_PRIVATE);
        String email = prefs.getString("email", "");
        String password = prefs.getString("password", "");
        binding.edtEmail.setText(email);
        binding.edtPassword.setText(password);
        login();
    }


}