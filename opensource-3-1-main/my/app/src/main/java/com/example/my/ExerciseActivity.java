package com.example.my;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import com.google.gson.Gson;
import java.util.Date;
import android.view.View;
import android.widget.NumberPicker;
import android.content.SharedPreferences;
import java.text.SimpleDateFormat;
import java.util.Locale;
import android.net.Uri;
import android.widget.VideoView;

// 운동 활동 화면

public class ExerciseActivity extends AppCompatActivity {
    private static final long DEFAULT_EXERCISE_TIME = 180000; // 3분
    private static final long[] EXERCISE_TIMES = {
        180000,  // 3분
        300000,  // 5분
        600000,  // 10분
        900000   // 15분
    };

    private TextView tvExerciseTitle;
    private TextView tvExerciseDescription;
    private TextView tvTimer;
    private Button btnStart, btnStop;
    private VideoView videoView;
    private CountDownTimer timer;
    private String exerciseType;
    private String routineName;
    private int currentExerciseIndex;
    private int totalExercises;
    private ArrayList<String> routineExercises;
    private long exerciseTime;
    private boolean isExerciseRunning = false;
    private long remainingTime;
    private String videoFileName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise);

        // Intent에서 데이터 가져오기
        Intent intent = getIntent();
        exerciseType = intent.getStringExtra("exercise_type");
        Log.d("ExerciseActivity", "Received exercise_type: " + exerciseType);
        
        if (exerciseType == null) {
            Toast.makeText(this, "운동 종류를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        routineName = intent.getStringExtra("routine_name");
        currentExerciseIndex = intent.getIntExtra("current_exercise_index", 0);
        totalExercises = intent.getIntExtra("total_exercises", 1);
        routineExercises = intent.getStringArrayListExtra("routine_exercises");
        
        // 비디오 파일 이름 설정
        videoFileName = getVideoFileName(exerciseType);
        
        // 운동 시간 설정 다이얼로그 표시
        showExerciseTimeDialog();
    }

    private String getVideoFileName(String exerciseType) {
        // 운동 이름에 따른 비디오 파일 이름 매핑
        switch (exerciseType) {
            case "목 앞 근육 스트레칭": return "one_one.mp4";
            case "목 좌우 근육 스트레칭": return "one_two.mp4";
            case "몸통 앞쪽 근육 스트레칭": return "one_three.mp4";
            case "몸통 옆쪽 근육 스트레칭": return "one_four.mp4";
            case "몸통회전 근육 스트레칭": return "one_five.mp4";
            case "몸통 스트레칭 1단계": return "one_six.mp4";
            case "몸통 스트레칭 2단계": return "one_seven.mp4";
            case "날개뼈 움직이기": return "one_eight.mp4";
            case "어깨 들어올리기": return "one_nine.mp4";
            case "날개뼈 모으기": return "one_ten.mp4";
            case "손목 및 팔꿈치 주변 근육 스트레칭": return "one_eleven.mp4";
            case "허벅지 및 종아리 근육 스트레칭": return "one_twelve.mp4";
            case "엉덩이 들기": return "two_one.mp4";
            case "엎드려 누운 상태로 다리 들기": return "two_two.mp4";
            case "엉덩이 옆 근육 운동": return "two_three.mp4";
            case "무릎 벌리기": return "two_four.mp4";
            case "무릎 펴기": return "two_five.mp4";
            case "런지": return "two_six.mp4";
            case "좌우런지": return "two_seven.mp4";
            case "발전된 런지": return "two_eight.mp4";
            case "손목 및 팔꿈치 주변 근육": return "two_nine.mp4";
            case "날개 뼈 모음 근육": return "two_ten.mp4";
            case "앉았다 일어서기": return "two_eleven.mp4";
            case "발전된 앉았다 일어서기": return "two_twelve.mp4";
            case "어깨 운동 1단계": return "two_thirteen.mp4";
            case "어깨 운동 2단계": return "two_fourteen.mp4";
            case "한발 서기": return "three_one.mp4";
            case "버드독 1단계": return "three_two.mp4";
            case "버드독 2단계": return "three_three.mp4";
            case "앉은 상태에서 제자리 걷기": return "three_four.mp4";
            case "움직이는 런지": return "three_five.mp4";
            default: return null;
        }
    }

    private void showExerciseTimeDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_exercise_time, null);
        AlertDialog dialog = new AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(false)
            .create();

        NumberPicker npMinutes = dialogView.findViewById(R.id.npMinutes);
        NumberPicker npSeconds = dialogView.findViewById(R.id.npSeconds);
        Button btnCancel = dialogView.findViewById(R.id.btnCancel);
        Button btnConfirm = dialogView.findViewById(R.id.btnConfirm);

        // 분 설정
        npMinutes.setMinValue(0);
        npMinutes.setMaxValue(60);
        npMinutes.setValue(3); // 기본값 3분

        // 초 설정
        npSeconds.setMinValue(0);
        npSeconds.setMaxValue(59);
        npSeconds.setValue(0); // 기본값 0초

        btnCancel.setOnClickListener(v -> {
            dialog.dismiss();
            finish(); // 다이얼로그 취소 시 액티비티 종료
        });

        btnConfirm.setOnClickListener(v -> {
            // 선택된 시간을 밀리초로 변환
            exerciseTime = (npMinutes.getValue() * 60 + npSeconds.getValue()) * 1000L;
            remainingTime = exerciseTime;
            dialog.dismiss();
            initializeViews();
        });

        dialog.show();
    }

    private void initializeViews() {
        // View 초기화
        tvExerciseTitle = findViewById(R.id.tvExerciseTitle);
        tvExerciseDescription = findViewById(R.id.tvExerciseDescription);
        tvTimer = findViewById(R.id.tvTimer);
        btnStart = findViewById(R.id.btnStart);
        btnStop = findViewById(R.id.btnStop);
        videoView = findViewById(R.id.videoView);

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

        // 비디오 설정
        if (videoFileName != null) {
            int resId = getResources().getIdentifier(
                videoFileName.replace(".mp4", ""),
                "raw",
                getPackageName()
            );
            videoView.setVideoURI(Uri.parse("android.resource://" + getPackageName() + "/" + resId));
            videoView.setOnPreparedListener(mp -> {
                mp.setLooping(true);
            });
        }

        // 타이머 초기화
        initializeTimer();

        // 버튼 클릭 리스너 설정
        setupClickListeners();
    }

    private void initializeTimer() {
        timer = new CountDownTimer(exerciseTime, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                remainingTime = millisUntilFinished;  // 남은 시간 업데이트
                long minutes = TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished);
                long seconds = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) % 60;
                tvTimer.setText(String.format("%02d:%02d", minutes, seconds));
            }

            @Override
            public void onFinish() {
                remainingTime = 0;  // 타이머 종료 시 남은 시간 0으로 설정
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
            } else {
                resumeExercise();
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
        btnStart.setText("재생");
        btnStart.setEnabled(false);
        btnStop.setEnabled(true);
        initializeTimer();
        timer.start();
        if (videoView != null) {
            videoView.start();
        }
        Toast.makeText(this, "운동을 시작합니다!", Toast.LENGTH_SHORT).show();
    }

    private void resumeExercise() {
        isExerciseRunning = true;
        btnStart.setEnabled(false);
        btnStop.setEnabled(true);
        timer.start();
        if (videoView != null) {
            videoView.start();
        }
        Toast.makeText(this, "운동을 재개합니다!", Toast.LENGTH_SHORT).show();
    }

    private void stopExercise() {
        isExerciseRunning = false;
        timer.cancel();
        btnStart.setText("재생");
        btnStart.setEnabled(true);
        btnStop.setEnabled(false);
        if (videoView != null) {
            videoView.pause();
        }
        
        // 운동 중지 시 현재까지의 운동 시간을 계산
        long elapsedTime = exerciseTime - remainingTime;  // 실제 운동 시간 계산
        saveWorkoutData(false, elapsedTime);
        
        Toast.makeText(this, "운동이 중단되었습니다.", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (videoView != null && videoView.isPlaying()) {
            videoView.pause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isExerciseRunning && videoView != null) {
            videoView.start();
        }
    }

    private void saveWorkoutData(boolean completed) {
        saveWorkoutData(completed, exerciseTime);
    }

    private void saveWorkoutData(boolean completed, long duration) {
        // 운동 데이터 저장
        WorkoutData workoutData = new WorkoutData(
            exerciseType,
            duration,  // 실제 운동 시간 사용
            completed,
            routineName != null ? routineName : "일반 운동",
            currentExerciseIndex + 1,
            totalExercises
        );
        workoutData.setWorkoutDate(new Date());  // 현재 시간 설정

        // SharedPreferences에서 사용자 이름 가져오기
        String userName = getSharedPreferences("LoginPrefs", 0)
            .getString("user_name", null);

        if (userName == null) {
            Toast.makeText(this, "로그인이 필요합니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        // 오늘 날짜의 운동 시간을 가져와서 누적
        SharedPreferences prefs = getSharedPreferences("ExercisePrefs", 0);
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        final long totalDuration = prefs.getLong("exercise_duration_" + today, 0) + duration;
        prefs.edit().putLong("exercise_duration_" + today, totalDuration).apply();

        // 서버에 데이터 전송
        String json = new Gson().toJson(workoutData);
        Log.d("ExerciseActivity", "Sending workout data: " + json);
        RequestBody body = RequestBody.create(
            MediaType.parse("application/json; charset=utf-8"), json);

        Request request = new Request.Builder()
            .url(Constants.API_WORKOUTS + "?userName=" + userName)  // 사용자 이름을 쿼리 파라미터로 전달
            .post(body)
            .build();

        new OkHttpClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("ExerciseActivity", "Failed to save workout data", e);
                runOnUiThread(() -> {
                    Toast.makeText(ExerciseActivity.this, 
                        "운동 데이터 저장 실패: " + e.getMessage(), 
                        Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseBody = response.body().string();
                Log.d("ExerciseActivity", "Server response: " + responseBody);
                
                if (!response.isSuccessful()) {
                    Log.e("ExerciseActivity", "Server error: " + response.code() + " - " + responseBody);
                runOnUiThread(() -> {
                        Toast.makeText(ExerciseActivity.this, 
                            "서버 오류: " + response.code() + " - " + responseBody, 
                            Toast.LENGTH_SHORT).show();
                    });
                    } else {
                    runOnUiThread(() -> {
                        // 오늘의 총 운동 시간을 표시
                        long totalMinutes = totalDuration / 60000;  // 밀리초를 분으로 변환
                        Toast.makeText(ExerciseActivity.this, 
                            String.format("운동 데이터가 저장되었습니다. (오늘 총 %d분)", totalMinutes), 
                            Toast.LENGTH_SHORT).show();
                    });
                    }
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