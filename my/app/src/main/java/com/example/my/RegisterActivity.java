package com.example.my;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.gson.Gson;
import okhttp3.*;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

// 회원가입 화면

public class RegisterActivity extends AppCompatActivity {
    private EditText etName, etPassword;
    private Button btnRegister, btnGoToLogin;
    private TextView tvMessage;
    private final OkHttpClient client = new OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .writeTimeout(10, TimeUnit.SECONDS)
        .build();
    private final Gson gson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etName = findViewById(R.id.etName);
        etPassword = findViewById(R.id.etPassword);
        btnRegister = findViewById(R.id.btnRegister);
        btnGoToLogin = findViewById(R.id.btnGoToLogin);
        tvMessage = findViewById(R.id.tvMessage);

        btnRegister.setOnClickListener(v -> registerUser());
        btnGoToLogin.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
        });
    }

    private void registerUser() {
        String name = etName.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (name.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "이름과 비밀번호를 모두 입력해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        User user = new User();
        user.setName(name);
        user.setPassword(password);

        String json = gson.toJson(user);
        String requestUrl = Constants.API_USERS + "/register";
        Log.d("RegisterActivity", "Attempting to register at URL: " + requestUrl);
        Log.d("RegisterActivity", "Request body: " + json);

        RequestBody body = RequestBody.create(json, MediaType.parse("application/json"));

        Request request = new Request.Builder()
                .url(requestUrl)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("RegisterActivity", "Register request failed", e);
                runOnUiThread(() -> {
                    tvMessage.setText("서버 연결 실패: " + e.getMessage());
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.d("RegisterActivity", "Register response code: " + response.code());
                String responseBody = response.body().string();
                Log.d("RegisterActivity", "Register response body: " + responseBody);
                
                runOnUiThread(() -> {
                    if (response.isSuccessful()) {
                        tvMessage.setText("회원가입이 완료되었습니다.");
                        etName.setText("");
                        etPassword.setText("");
                    } else {
                        tvMessage.setText(responseBody);
                    }
                });
            }
        });
    }

    private static class User {
        private String name;
        private String password;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }
} 