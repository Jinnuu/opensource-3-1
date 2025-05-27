package com.example.my;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

public class ExerciseGuideActivity extends AppCompatActivity {
    private String exerciseType;
    private String exerciseDescription;
    private String videoId;
    private YouTubePlayerView youtubePlayerView;
    private TextView tvExerciseTitle;
    private TextView tvFocusArea;
    private TextView tvEquipment;
    private TextView tvTip;
    private Button btnComplete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_exercise_guide);

        // Intent에서 데이터 가져오기
        exerciseType = getIntent().getStringExtra("exercise_type");
        exerciseDescription = getIntent().getStringExtra("exercise_description");
        videoId = getIntent().getStringExtra("video_id");

        // View 초기화
        youtubePlayerView = findViewById(R.id.youtubePlayerView);
        tvExerciseTitle = findViewById(R.id.tvExerciseTitle);
        tvFocusArea = findViewById(R.id.tvFocusArea);
        tvEquipment = findViewById(R.id.tvEquipment);
        tvTip = findViewById(R.id.tvTip);
        btnComplete = findViewById(R.id.btnComplete);

        // 운동 정보 설정
        tvExerciseTitle.setText(exerciseType);
        tvFocusArea.setText("집중 영역: " + exerciseDescription);
        tvEquipment.setText("장비: 없음");
        tvTip.setText(getExerciseTip(exerciseType));

        // YouTube 플레이어 설정
        getLifecycle().addObserver(youtubePlayerView);
        youtubePlayerView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
            @Override
            public void onReady(YouTubePlayer youTubePlayer) {
                youTubePlayer.loadVideo(videoId, 0);
            }
        });

        // 완료 버튼 클릭 리스너
        btnComplete.setOnClickListener(v -> {
            ExerciseTimeDialog dialog = new ExerciseTimeDialog(this, (minutes, seconds) -> {
                // 선택된 시간을 밀리초로 변환
                long exerciseTime = (minutes * 60 + seconds) * 1000L;
                
                // ExerciseActivity로 전환
                Intent intent = new Intent(this, ExerciseActivity.class);
                intent.putExtra("exercise_type", exerciseType);
                intent.putExtra("exercise_description", exerciseDescription);
                intent.putExtra("exercise_time", exerciseTime);
                startActivity(intent);
            });
            dialog.show();
        });
    }

    private String getExerciseTip(String exerciseType) {
        switch (exerciseType) {
            case "손목 운동":
                return "팁: 손목을 부드럽게 움직이세요.";
            case "허리 운동":
                return "팁: 허리를 천천히 스트레칭하세요.";
            case "목 운동":
                return "팁: 목을 부드럽게 돌리세요.";
            case "어깨 운동":
                return "팁: 어깨를 천천히 움직이세요.";
            case "팔 운동":
                return "팁: 적절한 무게로 시작하세요.";
            case "가슴 운동":
                return "팁: 올바른 자세를 유지하세요.";
            case "복근 운동":
                return "팁: 천천히 호흡하세요.";
            case "엉덩이 운동":
                return "팁: 무릎이 발끝을 넘지 않도록 하세요.";
            case "다리 운동":
                return "팁: 자세를 유지하며 천천히 진행하세요.";
            default:
                return "팁: 천천히 진행하세요.";
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        youtubePlayerView.release();
    }
} 