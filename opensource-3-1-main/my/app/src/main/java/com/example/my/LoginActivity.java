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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * 로그인 액티비티 클래스
 * 사용자 인증을 담당하며, 서버와 통신하여 로그인 처리
 * 자동 로그인 기능과 회원가입 화면으로의 이동 기능 제공
 * 회원가입 직후 첫 로그인 시 Body_Setting 화면으로 이동
 */
public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    
    // UI 컴포넌트들
    private EditText etName, etPassword;
    private Button btnLogin;
    private TextView tvRegister;
    private CheckBox cbKeepLoggedIn;
    
    // 네트워크 통신을 위한 HTTP 클라이언트 (타임아웃 설정 포함)
    private final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .build();
    
    // JSON 데이터 처리를 위한 Gson 객체
    private final Gson gson = new Gson();
    
    // SharedPreferences 키 상수들
    private static final String PREF_NAME = "LoginPrefs";
    private static final String KEY_USER_NAME = "user_name";
    private static final String KEY_KEEP_LOGGED_IN = "keep_logged_in";
    private static final String KEY_LAST_LOGIN_DATE = "last_login_date";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // UI 컴포넌트 초기화
        etName = findViewById(R.id.etName);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvRegister = findViewById(R.id.btnRegister);
        cbKeepLoggedIn = findViewById(R.id.cbKeepLoggedIn);

        // 자동 로그인 체크 (이전에 로그인 상태를 유지하기로 했는지 확인)
        checkAutoLogin();

        // 로그인 버튼 클릭 이벤트 설정
        btnLogin.setOnClickListener(v -> login());
        
        // 회원가입 버튼 클릭 시 회원가입 화면으로 이동
        tvRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }

    /**
     * 자동 로그인 기능
     * 이전에 "로그인 상태 유지"를 체크했다면 자동으로 로그인 시도
     */
    private void checkAutoLogin() {
        SharedPreferences prefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        boolean keepLoggedIn = prefs.getBoolean(KEY_KEEP_LOGGED_IN, false);

        if (keepLoggedIn) {
            String savedName = prefs.getString(KEY_USER_NAME, null);
            if (savedName != null) {
                // 저장된 사용자 이름을 입력창에 설정하고 자동 로그인 실행
                etName.setText(savedName);
                cbKeepLoggedIn.setChecked(true);
                login();
            }
        }
    }

    /**
     * 로그인 처리 메서드
     * 사용자 입력을 검증하고 서버에 로그인 요청을 보냄
     */
    private void login() {
        String name = etName.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // 입력값 검증
        if (name.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "이름과 비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            // 서버로 전송할 JSON 데이터 생성
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("name", name);
            jsonObject.put("password", password);

            String requestUrl = Constants.API_USERS + "/login";
            Log.d(TAG, "Attempting to login at URL: " + requestUrl);
            Log.d(TAG, "Request body: " + jsonObject.toString());

            // HTTP 요청 본문 생성
            RequestBody body = RequestBody.create(
                    MediaType.parse("application/json"), jsonObject.toString());

            // HTTP 요청 객체 생성
            Request request = new Request.Builder()
                    .url(requestUrl)
                    .post(body)
                    .build();

            // 비동기로 서버에 로그인 요청 전송
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    // 네트워크 오류 발생 시 처리
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
                            // 로그인 성공 시 사용자 정보를 로컬에 저장
                            SharedPreferences.Editor editor = getSharedPreferences(PREF_NAME, MODE_PRIVATE).edit();
                            editor.putString(KEY_USER_NAME, name);
                            editor.putBoolean(KEY_KEEP_LOGGED_IN, cbKeepLoggedIn.isChecked());
                            editor.apply();

                            // 첫 로그인인지 확인하여 적절한 화면으로 이동
                            checkFirstLoginAndNavigate(name);
                        } else {
                            // 로그인 실패 시 오류 메시지 표시
                            Toast.makeText(LoginActivity.this,
                                    "로그인 실패: 잘못된 이름 또는 비밀번호", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
        } catch (Exception e) {
            // 예외 발생 시 오류 메시지 표시
            Toast.makeText(this, "로그인 실패: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 첫 로그인 여부를 확인하고 적절한 화면으로 이동
     * @param userName 로그인한 사용자 이름
     */
    private void checkFirstLoginAndNavigate(String userName) {
        // 서버에서 사용자의 회원가입 날짜를 조회
        String requestUrl = Constants.API_USERS + "/registration-date?userName=" + userName;
        
        Request request = new Request.Builder()
                .url(requestUrl)
                .get()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "Failed to get registration date", e);
                // 오류 발생 시 기본적으로 MainActivity로 이동
                runOnUiThread(() -> navigateToMainActivity());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    try {
                        // JSON 응답에서 회원가입 날짜 추출
                        JSONObject jsonResponse = new JSONObject(responseBody);
                        String registrationDate = jsonResponse.getString("registrationDate");
                        
                        // 현재 날짜와 회원가입 날짜 비교
                        boolean isFirstLogin = isFirstLoginToday(registrationDate, userName);
                        
                        runOnUiThread(() -> {
                            if (isFirstLogin) {
                                // 첫 로그인인 경우 Body_Setting 화면으로 이동
                                navigateToBodySetting(userName);
                            } else {
                                // 두 번째 로그인부터는 MainActivity로 이동
                                navigateToMainActivity();
                            }
                        });
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing registration date", e);
                        // 파싱 오류 시 기본적으로 MainActivity로 이동
                        runOnUiThread(() -> navigateToMainActivity());
                    }
                } else {
                    // 서버 오류 시 기본적으로 MainActivity로 이동
                    runOnUiThread(() -> navigateToMainActivity());
                }
            }
        });
    }

    /**
     * 오늘 첫 로그인인지 확인
     * @param registrationDate 회원가입 날짜
     * @param userName 사용자 이름
     * @return 첫 로그인 여부
     */
    private boolean isFirstLoginToday(String registrationDate, String userName) {
        try {
            // 회원가입 날짜 파싱
            LocalDate regDate = LocalDate.parse(registrationDate);
            LocalDate today = LocalDate.now();
            
            // 회원가입 날짜가 오늘이고, 이전에 로그인한 기록이 없는 경우 첫 로그인으로 판단
            if (regDate.equals(today)) {
                SharedPreferences prefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
                String lastLoginDate = prefs.getString(KEY_LAST_LOGIN_DATE + "_" + userName, null);
                
                // 이전 로그인 기록이 없으면 첫 로그인
                if (lastLoginDate == null) {
                    // 오늘 로그인 기록 저장
                    prefs.edit().putString(KEY_LAST_LOGIN_DATE + "_" + userName, today.toString()).apply();
                    return true;
                }
            }
            
            return false;
        } catch (Exception e) {
            Log.e(TAG, "Error checking first login", e);
            return false;
        }
    }

    /**
     * Body_Setting 화면으로 이동
     * @param userName 사용자 이름
     */
    private void navigateToBodySetting(String userName) {
        Intent intent = new Intent(LoginActivity.this, Body_Setting.class);
        intent.putExtra("userName", userName);
        startActivity(intent);
        finish(); // 로그인 화면 종료
    }

    /**
     * MainActivity로 이동 (기존 로직)
     */
    private void navigateToMainActivity() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.putExtra("fragment", "exercise");  // ExerciseFragment로 이동하기 위한 플래그
        startActivity(intent);
        finish(); // 로그인 화면 종료
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
