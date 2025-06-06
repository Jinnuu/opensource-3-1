package com.example.my;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.VideoView;
import androidx.appcompat.app.AppCompatActivity;
import android.speech.tts.TextToSpeech;
import java.util.Locale;
import android.widget.ImageButton;

// 운동 가이드 화면

public class ExerciseGuideActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {
    private VideoView videoView;
    private TextView tvExerciseName;
    private TextView tvExerciseDescription;
    private Button btnStartExercise;
    private String videoFileName;
    private Handler handler = new Handler();
    private boolean isPaused = false;
    private TextToSpeech tts;
    private String exerciseDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_exercise_guide);

        // Initialize TTS
        tts = new TextToSpeech(this, this);

        videoView = findViewById(R.id.videoView);
        tvExerciseName = findViewById(R.id.tvExerciseName);
        tvExerciseDescription = findViewById(R.id.tvExerciseDescription);
        btnStartExercise = findViewById(R.id.btnStartExercise);

        // Intent에서 데이터 가져오기
        String exerciseName = getIntent().getStringExtra("exercise_type");
        videoFileName = getIntent().getStringExtra("video_file_name");

        // 운동 이름 설정
        tvExerciseName.setText(exerciseName);

        // 운동 설명 설정
        exerciseDescription = getExerciseDescription(exerciseName);
        tvExerciseDescription.setText(exerciseDescription);

        // Set up the speak button click listener
        ImageButton btnSpeak = findViewById(R.id.btnSpeak);
        btnSpeak.setOnClickListener(v -> {
            if (videoView.isPlaying()) {
                videoView.pause();
            }
            speak(exerciseDescription);
            // Resume video after TTS finishes (using a delay)
            new Handler().postDelayed(() -> {
                if (!isPaused) {
                    videoView.start();
                }
            }, 5000); // Adjust delay as needed
        });

        // 비디오 설정
        int resId = getResources().getIdentifier(
            videoFileName.replace(".mp4", ""),
            "raw",
            getPackageName()
        );
        videoView.setVideoURI(Uri.parse("android.resource://" + getPackageName() + "/" + resId));
        videoView.setOnPreparedListener(mp -> {
            mp.setLooping(true);
            videoView.start();
        });

        // 시작하기 버튼 클릭 리스너
        btnStartExercise.setOnClickListener(v -> {
            Intent intent = new Intent(ExerciseGuideActivity.this, ExerciseActivity.class);
            intent.putExtra("exercise_type", exerciseName);
            intent.putExtra("exercise_time", 30); // 기본 30초
            startActivity(intent);
            finish();
        });
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            tts.setLanguage(Locale.KOREAN);
            tts.setSpeechRate(0.7f);
            
            // TTS 초기화가 완료된 후에 speak 호출
            if (videoView.isPlaying()) {
                videoView.pause();
            }
            speak(exerciseDescription);
            // Resume video after TTS finishes
            new Handler().postDelayed(() -> {
                if (!isPaused) {
                    videoView.start();
                }
            }, 5000);
        }
    }

    private void speak(String text) {
        if (tts != null) {
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
        }
    }

    private String getExerciseDescription(String exerciseName) {
        switch (exerciseName) {
            case "목 앞 근육 스트레칭":
                return "기본 자세:\n- 바른 자세로 서서 목을 앞으로 내밀었다가 뒤로 젖힙니다.\n\n주의사항:\n- 갑작스러운 움직임을 피하고 천천히 진행합니다.\n- 통증이 있다면 즉시 중단하세요.";
            case "목 좌우 근육 스트레칭":
                return "기본 자세:\n- 바른 자세로 서서 목을 좌우로 부드럽게 기울입니다.\n\n주의사항:\n- 어깨를 들지 않고 자연스럽게 유지합니다.\n- 통증이 있는 방향은 피하세요.";
            case "몸통 앞쪽 근육 스트레칭":
                return "기본 자세:\n- 바른 자세로 서서 팔을 위로 올리고 몸을 뒤로 젖힙니다.\n\n주의사항:\n- 허리를 과도하게 젖히지 않습니다.\n- 천천히 진행하고 호흡을 자연스럽게 유지하세요.";
            case "몸통 옆쪽 근육 스트레칭":
                return "기본 자세:\n- 바른 자세로 서서 한쪽 팔을 위로 올리고 반대쪽으로 기울입니다.\n\n주의사항:\n- 무릎을 구부리지 않고 다리를 곧게 유지합니다.\n- 통증이 있다면 즉시 중단하세요.";
            case "몸통회전 근육 스트레칭":
                return "기본 자세:\n- 바른 자세로 서서 상체를 좌우로 부드럽게 회전합니다.\n\n주의사항:\n- 하체는 고정된 상태를 유지합니다.\n- 갑작스러운 회전을 피하세요.";
            case "몸통 스트레칭 1단계":
                return "기본 자세:\n- 엎드린 상태에서 팔꿈치로 상체를 지탱합니다.\n\n주의사항:\n- 허리를 과도하게 젖히지 않습니다.\n- 통증이 있다면 즉시 중단하세요.";
            case "몸통 스트레칭 2단계":
                return "기본 자세:\n- 엎드린 상태에서 손바닥으로 상체를 지탱합니다.\n\n주의사항:\n- 허리를 과도하게 젖히지 않습니다.\n- 통증이 있다면 즉시 중단하세요.";
            case "날개뼈 움직이기":
                return "기본 자세:\n- 바른 자세로 서서 날개뼈를 모았다 펼치는 동작을 반복합니다.\n\n주의사항:\n- 어깨를 들지 않고 자연스럽게 유지합니다.\n- 천천히 진행하세요.";
            case "어깨 들어올리기":
                return "기본 자세:\n- 바른 자세로 서서 어깨를 위로 올렸다 내립니다.\n\n주의사항:\n- 목을 과도하게 움직이지 않습니다.\n- 천천히 진행하세요.";
            case "날개뼈 모으기":
                return "기본 자세:\n- 바른 자세로 서서 양팔을 뒤로 모아 날개뼈를 모읍니다.\n\n주의사항:\n- 어깨를 들지 않고 자연스럽게 유지합니다.\n- 천천히 진행하세요.";
            case "손목 및 팔꿈치 주변 근육 스트레칭":
                return "기본 자세:\n- 한쪽 팔을 앞으로 뻗고 반대쪽 손으로 손목을 잡아 스트레칭합니다.\n\n주의사항:\n- 통증이 있는 방향은 피하세요.\n- 천천히 진행하세요.";
            case "허벅지 및 종아리 근육 스트레칭":
                return "기본 자세:\n- 한쪽 다리를 앞으로 내밀고 반대쪽 다리는 뒤로 뻗어 스트레칭합니다.\n\n주의사항:\n- 무릎이 발끝을 넘지 않도록 합니다.\n- 통증이 있다면 즉시 중단하세요.";
            case "엉덩이 들기":
                return "기본 자세:\n- 누운 상태에서 무릎을 구부리고 엉덩이를 들어올립니다.\n\n주의사항:\n- 허리를 과도하게 젖히지 않습니다.\n- 천천히 진행하세요.";
            case "엎드려 누운 상태로 다리 들기":
                return "기본 자세:\n- 엎드린 상태에서 한쪽 다리를 들어올립니다.\n\n주의사항:\n- 허리를 과도하게 젖히지 않습니다.\n- 통증이 있다면 즉시 중단하세요.";
            case "엉덩이 옆 근육 운동":
                return "기본 자세:\n- 옆으로 누운 상태에서 위쪽 다리를 들어올립니다.\n\n주의사항:\n- 몸이 앞뒤로 기울어지지 않도록 합니다.\n- 천천히 진행하세요.";
            case "무릎 벌리기":
                return "기본 자세:\n- 누운 상태에서 무릎을 구부리고 벌렸다 모았다를 반복합니다.\n\n주의사항:\n- 통증이 있다면 즉시 중단하세요.\n- 천천히 진행하세요.";
            case "무릎 펴기":
                return "기본 자세:\n- 누운 상태에서 한쪽 다리를 펴서 들어올립니다.\n\n주의사항:\n- 무릎을 완전히 펴지 않아도 됩니다.\n- 통증이 있다면 즉시 중단하세요.";
            case "런지":
                return "기본 자세:\n- 한쪽 다리를 앞으로 내밀고 무릎을 구부립니다.\n\n주의사항:\n- 무릎이 발끝을 넘지 않도록 합니다.\n- 천천히 진행하세요.";
            case "좌우런지":
                return "기본 자세:\n- 한쪽 다리를 옆으로 내밀고 무릎을 구부립니다.\n\n주의사항:\n- 무릎이 발끝을 넘지 않도록 합니다.\n- 천천히 진행하세요.";
            case "발전된 런지":
                return "기본 자세:\n- 한쪽 다리를 뒤로 내밀고 무릎을 구부립니다.\n\n주의사항:\n- 무릎이 발끝을 넘지 않도록 합니다.\n- 천천히 진행하세요.";
            case "손목 및 팔꿈치 주변 근육":
                return "기본 자세:\n- 고무줄을 사용하여 손목과 팔꿈치 주변 근육을 강화합니다.\n\n주의사항:\n- 통증이 있다면 즉시 중단하세요.\n- 천천히 진행하세요.";
            case "날개 뼈 모음 근육":
                return "기본 자세:\n- 고무줄을 사용하여 날개뼈를 모으는 동작을 반복합니다.\n\n주의사항:\n- 어깨를 들지 않고 자연스럽게 유지합니다.\n- 천천히 진행하세요.";
            case "앉았다 일어서기":
                return "기본 자세:\n- 의자에 앉았다 일어서는 동작을 반복합니다.\n\n주의사항:\n- 무릎이 발끝을 넘지 않도록 합니다.\n- 천천히 진행하세요.";
            case "발전된 앉았다 일어서기":
                return "기본 자세:\n- 의자에 앉았다 일어서는 동작을 팔을 앞으로 내밀며 반복합니다.\n\n주의사항:\n- 무릎이 발끝을 넘지 않도록 합니다.\n- 천천히 진행하세요.";
            case "어깨 운동 1단계":
                return "기본 자세:\n- 가벼운 무게를 들고 어깨를 들어올립니다.\n\n주의사항:\n- 목을 과도하게 움직이지 않습니다.\n- 천천히 진행하세요.";
            case "어깨 운동 2단계":
                return "기본 자세:\n- 양팔을 든 상태에서 손을 배쪽으로 교차시킵니다.\n\n주의사항:\n- 어깨를 들지 않고 자연스럽게 유지합니다.\n- 천천히 진행하세요.";
            case "한발 서기":
                return "벽 또는 지탱할 물건으로 몸을 받친 상태로 한쪽 발을 들어올려 다리 근육을 키웁니다.";
            case "버드독 1단계":
                return "무릎과 손목으로 몸을 바닥에 지탱한 상태에서 다리를 한쪽 씩 들어올리며 근육을 키웁니다.";
            case "버드독 2단계":
                return "무릎과 손목으로 바닥에 몸을 지탱한 상태에서 손목과 무릎을 한쪽 씩 들어올리며 근육을 키웁니다.";
            case "앉은 상태에서 제자리 걷기":
                return "의자에 앉은 상태로 팔과 다리를 올리며 근육을 키웁니다.";
            case "움직이는 런지":
                return "다리를 어깨 너비만큼 벌린 후 허벅지를 구부려 다리 근육을 키웁니다.";
            default:
                return "운동을 시작하기 전에 기본 자세와 주의사항을 확인하세요.";
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (videoView != null && videoView.isPlaying()) {
            videoView.pause();
            isPaused = true;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isPaused && videoView != null) {
            videoView.start();
            isPaused = false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
    }
} 