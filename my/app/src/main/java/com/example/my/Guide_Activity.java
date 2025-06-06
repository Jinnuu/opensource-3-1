package com.example.my;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import com.google.android.material.button.MaterialButton;

// 운동 가이드 화면

public class Guide_Activity extends AppCompatActivity {
    private MaterialButton btnStart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);

        // 시작하기 버튼 초기화
        btnStart = findViewById(R.id.btn_Start);
        btnStart.setOnClickListener(v -> {
            // MainActivity로 이동
            finish();
        });

        // exercise_guide Fragment 생성 및 추가
        String exerciseType = getIntent().getStringExtra("exercise_type");
        String exerciseDescription = getIntent().getStringExtra("exercise_description");
        String videoId = getIntent().getStringExtra("video_id");

        if (exerciseType != null && exerciseDescription != null && videoId != null) {
            exercise_guide guideFragment = exercise_guide.newInstance(
                exerciseType,
                exerciseDescription,
                videoId
            );

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, guideFragment);
            transaction.commit();
        }
    }
} 