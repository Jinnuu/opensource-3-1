package com.example.my;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class ExerciseActivity extends AppCompatActivity {
    private static final long DEFAULT_EXERCISE_TIME = 180000; // 3분

    private TextView tvExerciseTitle;
    private TextView tvExerciseDescription;
    private TextView tvTimer;
    private Button btnStart, btnStop;
    private CountDownTimer timer;
    private String exerciseType;
    private String routineName;
    private int currentExerciseIndex;
    private int totalExercises;
    private ArrayList<String> routineExercises;
    private long exerciseTime;
    private boolean isExerciseRunning = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise);

        // Intent에서 데이터 가져오기
        Intent intent = getIntent();
        exerciseType = intent.getStringExtra("exercise_type");
        routineName = intent.getStringExtra("routine_name");
        currentExerciseIndex = intent.getIntExtra("current_exercise_index", 0);
        totalExercises = intent.getIntExtra("total_exercises", 1);
        routineExercises = intent.getStringArrayListExtra("routine_exercises");
        exerciseTime = intent.getLongExtra("exercise_time", DEFAULT_EXERCISE_TIME);

        // View 초기화
        tvExerciseTitle = findViewById(R.id.tvExerciseTitle);
        tvExerciseDescription = findViewById(R.id.tvExerciseDescription);
        tvTimer = findViewById(R.id.tvTimer);
        btnStart = findViewById(R.id.btnStart);
        btnStop = findViewById(R.id.btnStop);

        // 운동 정보 설정
        String title = routineName != null ? 
            String.format("%s (%d/%d)", exerciseType, currentExerciseIndex + 1, totalExercises) :
            exerciseType;
        tvExerciseTitle.setText(title);
        tvExerciseDescription.setText("운동을 시작하세요!");

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
                
                // 다음 운동이 있는지 확인
                if (routineExercises != null && currentExerciseIndex < totalExercises - 1) {
                    // 다음 운동으로 이동
                    Intent intent = new Intent(ExerciseActivity.this, ExerciseActivity.class);
                    intent.putExtra("exercise_type", routineExercises.get(currentExerciseIndex + 1));
                    intent.putExtra("routine_name", routineName);
                    intent.putExtra("current_exercise_index", currentExerciseIndex + 1);
                    intent.putExtra("total_exercises", totalExercises);
                    intent.putExtra("routine_exercises", routineExercises);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(ExerciseActivity.this, "모든 운동이 완료되었습니다!", Toast.LENGTH_SHORT).show();
                    finish();
                }
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
        initializeTimer();
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
        // 운동 데이터 저장 로직
        // TODO: 서버에 운동 데이터 저장
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timer != null) {
            timer.cancel();
        }
    }
} 