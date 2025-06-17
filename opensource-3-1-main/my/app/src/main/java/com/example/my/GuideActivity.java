package com.example.my;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import com.google.android.material.button.MaterialButton;

/**
 * 운동 가이드 액티비티 클래스
 * 사용자가 선택한 운동의 상세 가이드를 제공하는 화면
 * 운동 설명과 비디오 가이드를 포함한 exercise_guide 프래그먼트를 호스팅
 * 앱 사용법을 처음 보는 사용자를 위한 온보딩 역할도 수행
 */
public class GuideActivity extends AppCompatActivity {
    // 시작하기 버튼 - 가이드 완료 후 메인 화면으로 이동
    private MaterialButton btnStart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);

        // 시작하기 버튼 초기화 및 클릭 이벤트 설정
        btnStart = findViewById(R.id.btn_start);
        btnStart.setOnClickListener(v -> {
            // 가이드 완료 후 이전 화면으로 돌아가기
            finish();
        });

        // Intent에서 전달받은 운동 정보 추출
        // 다른 액티비티에서 운동 가이드를 요청할 때 전달되는 데이터
        String exerciseType = getIntent().getStringExtra("exercise_type");
        String exerciseDescription = getIntent().getStringExtra("exercise_description");
        String videoId = getIntent().getStringExtra("video_id");

        // 운동 정보가 모두 제공된 경우 exercise_guide 프래그먼트 생성
        if (exerciseType != null && exerciseDescription != null && videoId != null) {
            // 운동 가이드 프래그먼트 생성 (정적 팩토리 메서드 사용)
            exercise_guide guideFragment = exercise_guide.newInstance(
                    exerciseType,
                    exerciseDescription,
                    videoId
            );

            // 프래그먼트를 화면에 추가
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, guideFragment);
            transaction.commit();
        }
    }
} 