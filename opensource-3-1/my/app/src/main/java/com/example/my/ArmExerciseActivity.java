package com.example.my;

import static android.app.ProgressDialog.show;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.gson.Gson;
import okhttp3.*;
import java.io.IOException;
import java.util.Date;

public class ArmExerciseActivity extends AppCompatActivity {
    private TextView tvTimer;
    private Button btnStart;
    private CountDownTimer timer;
    private boolean isExerciseStarted = false;
    private int selectedMinutes = 3; // 기본값 3분
    private int selectedSeconds = 0;
    private long startTime;
    private final OkHttpClient client = new OkHttpClient();
    private final Gson gson = new Gson();
    private static final String PREF_NAME = "LoginPrefs";
    private static final String KEY_USER_NAME = "user_name";

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_arm_exercise);

        // 선택된 시간 가져오기
        selectedMinutes = getIntent().getIntExtra("minutes", 3);
        selectedSeconds = getIntent().getIntExtra("seconds", 0);

        tvTimer = findViewById(R.id.tvTimer);
        btnStart = findViewById(R.id.btnStart);

        btnStart.setOnClickListener(v -> {
            if (!isExerciseStarted) {
                startExercise();
            } else {
                stopExercise();
            }
        });
    }

    private void startExercise() {
        isExerciseStarted = true;
        btnStart.setText("운동 중지");
        startTime = System.currentTimeMillis();

        long totalMilliseconds = (selectedMinutes * 60 + selectedSeconds) * 1000L;
        timer = new CountDownTimer(totalMilliseconds, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                int minutes = (int) (millisUntilFinished / 1000) / 60;
                int seconds = (int) (millisUntilFinished / 1000) % 60;
                tvTimer.setText(String.format("%02d:%02d", minutes, seconds));
            }

            @Override
            public void onFinish() {
                saveWorkoutData();
                stopExercise();
            }
        }.start();
    }

    private void stopExercise() {
        isExerciseStarted = false;
        btnStart.setText("운동 시작");
        if (timer != null) {
            timer.cancel();
        }
        tvTimer.setText(String.format("%02d:%02d", selectedMinutes, selectedSeconds));
    }

    private void saveWorkoutData() {
        SharedPreferences prefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        String userName = prefs.getString(KEY_USER_NAME, null);

        if (userName == null) {
            Toast.makeText(this, "로그인이 필요합니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        long duration = (System.currentTimeMillis() - startTime) / 1000; // 초 단위로 변환
        // 부위 이름만 바꿔주세요!
        Workout workout = new Workout("[부위영문]", (int)duration, new Date(), userName);

        String json = gson.toJson(workout);
        RequestBody body = RequestBody.create(
                MediaType.parse("application/json"), json);

        Request request = new Request.Builder()
                .url(Constants.API_WORKOUTS + "?userName=" + userName)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> {
                    Toast.makeText(ArmExerciseActivity.this,
                            "운동 데이터 저장 실패: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseBody = response.body().string();
                runOnUiThread(() -> {
                    if (response.isSuccessful()) {
                        Toast.makeText(ArmExerciseActivity.this,
                                "운동 데이터가 저장되었습니다.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(ArmExerciseActivity.this,
                                "운동 데이터 저장 실패: " + responseBody, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timer != null) {
            timer.cancel();
        }
    }
}
