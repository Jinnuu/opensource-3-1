package com.example.my;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.gson.Gson;
import okhttp3.*;
import org.json.JSONObject;
import java.io.IOException;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private EditText etName, etPassword;
    private Button btnLogin;
    private TextView tvRegister;
    private CheckBox cbKeepLoggedIn;
    private final OkHttpClient client = new OkHttpClient();
    private final Gson gson = new Gson();
    private static final String PREF_NAME = "LoginPrefs";
    private static final String KEY_USER_NAME = "user_name";
    private static final String KEY_KEEP_LOGGED_IN = "keep_logged_in";
    private static final String KEY_BODY_SETTING_DONE = "is_body_setting_done";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 자동 로그인 및 Body_Setting 분기 처리
        SharedPreferences prefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        boolean keepLoggedIn = prefs.getBoolean(KEY_KEEP_LOGGED_IN, false);
        boolean isBodySettingDone = prefs.getBoolean(KEY_BODY_SETTING_DONE, false);

        if (keepLoggedIn) {
            if (isBodySettingDone) {
                // 이미 Body_Setting까지 완료 → 바로 MainActivity로 이동
                startActivity(new Intent(this, MainActivity.class));
                finish();
                return;
            } else {
                // 로그인은 했지만 Body_Setting은 아직 → Body_Setting으로 이동
                startActivity(new Intent(this, Body_Setting.class));
                finish();
                return;
            }
        }

        setContentView(R.layout.activity_login);

        etName = findViewById(R.id.etName);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvRegister = findViewById(R.id.tvRegister);
        cbKeepLoggedIn = findViewById(R.id.cbKeepLoggedIn);

        btnLogin.setOnClickListener(v -> login());
        tvRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
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

            RequestBody body = RequestBody.create(
                    MediaType.parse("application/json"), jsonObject.toString());

            Request request = new Request.Builder()
                    .url(Constants.API_USERS + "/login")
                    .post(body)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    runOnUiThread(() -> {
                        Toast.makeText(LoginActivity.this,
                                "로그인 실패: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        SharedPreferences prefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putString(KEY_USER_NAME, name);
                        editor.putBoolean(KEY_KEEP_LOGGED_IN, cbKeepLoggedIn.isChecked());
                        // 로그인 성공 시 Body_Setting 완료 여부는 false로 초기화
                        editor.putBoolean(KEY_BODY_SETTING_DONE, false);
                        editor.apply();

                        runOnUiThread(() -> {
                            Toast.makeText(LoginActivity.this,
                                    "로그인 성공", Toast.LENGTH_SHORT).show();
                            // Body_Setting으로 이동
                            Intent intent = new Intent(LoginActivity.this, Body_Setting.class);
                            startActivity(intent);
                            finish();
                        });
                    } else {
                        runOnUiThread(() -> {
                            Toast.makeText(LoginActivity.this,
                                    "로그인 실패: 잘못된 이름 또는 비밀번호", Toast.LENGTH_SHORT).show();
                        });
                    }
                }
            });
        } catch (Exception e) {
            Toast.makeText(this, "로그인 실패: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
