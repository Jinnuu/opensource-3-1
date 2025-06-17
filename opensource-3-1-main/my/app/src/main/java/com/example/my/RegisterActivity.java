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

/**
 * 회원가입 액티비티 클래스
 * 새로운 사용자의 계정 생성을 담당하는 화면
 * 사용자 이름과 비밀번호를 입력받아 서버에 회원가입 요청을 전송
 * 회원가입 완료 후 로그인 화면으로 이동 가능
 */
public class RegisterActivity extends AppCompatActivity {
    // UI 컴포넌트들
    private EditText etName, etPassword;
    private Button btnRegister, btnGoToLogin;
    private TextView tvMessage;
    
    // 네트워크 통신을 위한 HTTP 클라이언트 (타임아웃 설정 포함)
    private final OkHttpClient client = new OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .writeTimeout(10, TimeUnit.SECONDS)
        .build();
    
    // JSON 데이터 처리를 위한 Gson 객체
    private final Gson gson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // UI 컴포넌트 초기화
        etName = findViewById(R.id.etName);
        etPassword = findViewById(R.id.etPassword);
        btnRegister = findViewById(R.id.btnRegister);
        btnGoToLogin = findViewById(R.id.btnGoToLogin);
        tvMessage = findViewById(R.id.tvMessage);

        // 회원가입 버튼 클릭 이벤트 설정
        btnRegister.setOnClickListener(v -> registerUser());
        
        // 로그인 화면으로 이동 버튼 클릭 이벤트 설정
        btnGoToLogin.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
        });
    }

    /**
     * 회원가입 처리 메서드
     * 사용자 입력을 검증하고 서버에 회원가입 요청을 전송
     */
    private void registerUser() {
        // 사용자 입력값 가져오기
        String name = etName.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // 입력값 검증
        if (name.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "이름과 비밀번호를 모두 입력해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        // User 객체 생성 및 데이터 설정
        User user = new User();
        user.setName(name);
        user.setPassword(password);

        // JSON 형태로 데이터 변환
        String json = gson.toJson(user);
        String requestUrl = Constants.API_USERS + "/register";
        Log.d("RegisterActivity", "Attempting to register at URL: " + requestUrl);
        Log.d("RegisterActivity", "Request body: " + json);

        // HTTP 요청 본문 생성
        RequestBody body = RequestBody.create(json, MediaType.parse("application/json"));

        // HTTP 요청 객체 생성
        Request request = new Request.Builder()
                .url(requestUrl)
                .post(body)
                .build();

        // 비동기로 서버에 회원가입 요청 전송
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // 네트워크 오류 발생 시 처리
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
                        // 회원가입 성공 시 처리
                        tvMessage.setText("회원가입이 완료되었습니다.");
                        // 입력 필드 초기화
                        etName.setText("");
                        etPassword.setText("");
                    } else {
                        // 회원가입 실패 시 서버 오류 메시지 표시
                        tvMessage.setText(responseBody);
                    }
                });
            }
        });
    }

    /**
     * 사용자 정보를 담는 내부 클래스
     * 서버와의 데이터 교환을 위한 모델 클래스
     */
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