package com.example.my;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.gson.Gson;
import okhttp3.*;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class ExerciseActivity extends AppCompatActivity {
    private static final String BASE_URL = "http://192.168.56.1:8080";  // 원래 IP 주소로 복구
    private static final long DEFAULT_EXERCISE_TIME = 180000; // 3분

    private TextView tvExerciseTitle;
    private TextView tvExerciseDescription;
    private TextView tvTimer;
    private Button btnStart, btnStop;
    private CountDownTimer timer;
    private String exerciseType;
    private String exerciseDescription;
    private long exerciseTime;
    private boolean isExerciseRunning = false;
    private final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .build();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise);

        // Intent에서 데이터 가져오기
        Intent intent = getIntent();
        exerciseType = intent.getStringExtra("exercise_type");
        exerciseDescription = intent.getStringExtra("exercise_description");
        exerciseTime = intent.getLongExtra("exercise_time", DEFAULT_EXERCISE_TIME);

        // View 초기화
        tvExerciseTitle = findViewById(R.id.tvExerciseTitle);
        tvExerciseDescription = findViewById(R.id.tvExerciseDescription);
        tvTimer = findViewById(R.id.tvTimer);
        btnStart = findViewById(R.id.btnStart);
        btnStop = findViewById(R.id.btnStop);

        // 운동 정보 설정
        tvExerciseTitle.setText(exerciseType);
        tvExerciseDescription.setText(exerciseDescription);

        // 타이머 초기화
        long minutes = (exerciseTime / 1000) / 60;
        long seconds = (exerciseTime / 1000) % 60;
        tvTimer.setText(String.format("%02d:%02d", minutes, seconds));

        // 타이머 초기화
        initializeTimer();

        // 버튼 클릭 리스너 설정
        setupClickListeners();
    }

    private void initializeTimer() {
        timer = new CountDownTimer(exerciseTime, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long minutes = TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished);
                long seconds = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) % 60;
                tvTimer.setText(String.format("%02d:%02d", minutes, seconds));
            }

            @Override
            public void onFinish() {
                isExerciseRunning = false;
                btnStart.setEnabled(true);
                btnStop.setEnabled(false);
                saveWorkoutData(true);
                Toast.makeText(ExerciseActivity.this, "운동이 완료되었습니다!", Toast.LENGTH_SHORT).show();
            }
        };
    }

    private void setupClickListeners() {
        btnStart.setOnClickListener(v -> {
            if (!isExerciseRunning) {
                startExercise();
            }
        });

        btnStop.setOnClickListener(v -> {
            if (isExerciseRunning) {
                stopExercise();
            }
        });
    }

    private void startExercise() {
        isExerciseRunning = true;
        btnStart.setEnabled(false);
        btnStop.setEnabled(true);
        initializeTimer(); // 타이머를 새로 초기화
        timer.start();
        Toast.makeText(this, "운동을 시작합니다!", Toast.LENGTH_SHORT).show();
    }

    private void stopExercise() {
        isExerciseRunning = false;
        timer.cancel();
        btnStart.setEnabled(true);
        btnStop.setEnabled(false);
        saveWorkoutData(false);
        Toast.makeText(this, "운동이 중단되었습니다.", Toast.LENGTH_SHORT).show();
    }

    private void saveWorkoutData(boolean completed) {
        WorkoutData workoutData = new WorkoutData(exerciseType, exerciseTime, completed);
        String json = new Gson().toJson(workoutData);

        // SharedPreferences에서 토큰 가져오기
        android.content.SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String token = prefs.getString("auth_token", "");

        RequestBody body = RequestBody.create(
            MediaType.parse("application/json; charset=utf-8"), json);

        Request request = new Request.Builder()
            .url(BASE_URL + "/workouts")
            .addHeader("Authorization", "Bearer " + token)
            .post(body)
            .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> {
                    String errorMessage = "서버 연결 실패: " + e.getMessage() + "\nURL: " + BASE_URL + "/workouts";
                    Toast.makeText(ExerciseActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                });
            }

            @Override
            public void onResponse(Call call, Response response) {
                runOnUiThread(() -> {
                    if (response.isSuccessful()) {
                        Toast.makeText(ExerciseActivity.this, 
                            "운동 데이터가 저장되었습니다.", 
                            Toast.LENGTH_SHORT).show();
                    } else {
                        String errorMessage = "서버 응답 실패: " + response.code() + "\nURL: " + BASE_URL + "/workouts";
                        if (response.code() == 401) {
                            errorMessage += "\n인증이 필요합니다. 다시 로그인해주세요.";
                        }
                        Toast.makeText(ExerciseActivity.this, errorMessage, Toast.LENGTH_LONG).show();
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