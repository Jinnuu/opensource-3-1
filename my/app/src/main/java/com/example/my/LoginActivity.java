package com.example.my;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AlertDialog;
import com.google.gson.Gson;
import okhttp3.*;
import org.json.JSONObject;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

// 로그인 화면

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private EditText etName, etPassword;
    private Button btnLogin;
    private TextView tvRegister;
    private CheckBox cbKeepLoggedIn;
    private final OkHttpClient client = new OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .writeTimeout(10, TimeUnit.SECONDS)
        .build();
    private final Gson gson = new Gson();
    private static final String PREF_NAME = "LoginPrefs";
    private static final String KEY_USER_NAME = "user_name";
    private static final String KEY_KEEP_LOGGED_IN = "keep_logged_in";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etName = findViewById(R.id.etName);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvRegister = findViewById(R.id.btnRegister);
        cbKeepLoggedIn = findViewById(R.id.cbKeepLoggedIn);

        checkAutoLogin();

        btnLogin.setOnClickListener(v -> login());
        tvRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }

    private void checkAutoLogin() {
        SharedPreferences prefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        boolean keepLoggedIn = prefs.getBoolean(KEY_KEEP_LOGGED_IN, false);
        
        if (keepLoggedIn) {
            String savedName = prefs.getString(KEY_USER_NAME, null);
            if (savedName != null) {
                etName.setText(savedName);
                cbKeepLoggedIn.setChecked(true);
                login();
            }
        }
    }

    private void login() {
        String name = etName.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (name.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "이름과 비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("name", name);
            jsonObject.put("password", password);

            String requestUrl = Constants.API_USERS + "/login";
            Log.d(TAG, "Attempting to login at URL: " + requestUrl);
            Log.d(TAG, "Request body: " + jsonObject.toString());

            RequestBody body = RequestBody.create(
                MediaType.parse("application/json"), jsonObject.toString());

            Request request = new Request.Builder()
                .url(requestUrl)
                .post(body)
                .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.e(TAG, "Login request failed", e);
                    runOnUiThread(() -> {
                        Toast.makeText(LoginActivity.this, 
                            "로그인 실패: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String responseBody = response.body().string();
                    Log.d(TAG, "Login response code: " + response.code());
                    Log.d(TAG, "Login response body: " + responseBody);
                    
                    runOnUiThread(() -> {
                        if (response.isSuccessful()) {
                            // 로그인 성공 시 사용자 이름 저장
                            SharedPreferences.Editor editor = getSharedPreferences(PREF_NAME, MODE_PRIVATE).edit();
                            editor.putString(KEY_USER_NAME, name);
                            editor.putBoolean(KEY_KEEP_LOGGED_IN, cbKeepLoggedIn.isChecked());
                            editor.apply();

                            // ExerciseFragment로 이동
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            intent.putExtra("fragment", "exercise");  // ExerciseFragment로 이동하기 위한 플래그
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(LoginActivity.this, 
                                "로그인 실패: 잘못된 이름 또는 비밀번호", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
        } catch (Exception e) {
            Toast.makeText(this, "로그인 실패: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
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
