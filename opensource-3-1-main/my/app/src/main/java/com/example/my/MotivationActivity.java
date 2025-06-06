package com.example.my;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Random;

public class MotivationActivity extends AppCompatActivity {
    private ImageView ivMotivationImage;
    private TextView tvMotivationText;
    private Button btnContinue;

    // 동기부여 문구 배열
    private final String[] motivationQuotes = {
        "오늘 하루도 건강하게 시작해보세요!",
        "작은 노력이 큰 변화를 만듭니다.",
        "당신의 건강한 노력이 미래의 행복을 만듭니다.",
        "매일 조금씩, 꾸준히 실천하는 것이 중요합니다.",
        "건강한 생활습관이 최고의 투자입니다.",
        "오늘의 운동이 내일의 건강을 만듭니다.",
        "시작이 반입니다. 지금 시작해보세요!",
        "건강한 몸은 건강한 마음의 시작입니다.",
        "작은 변화가 큰 차이를 만듭니다.",
        "당신의 노력이 건강한 미래를 만듭니다."
    };

    // 동기부여 이미지 리소스 ID 배열
    private final int[] motivationImages = {
        R.drawable.motivation1,
        R.drawable.motivation2,
        R.drawable.motivation3,
        R.drawable.motivation4,
        R.drawable.motivation5
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_motivation);

        // View 초기화
        ivMotivationImage = findViewById(R.id.ivMotivationImage);
        tvMotivationText = findViewById(R.id.tvMotivationText);
        btnContinue = findViewById(R.id.btnContinue);

        // 랜덤으로 동기부여 문구와 이미지 선택
        Random random = new Random();
        String randomQuote = motivationQuotes[random.nextInt(motivationQuotes.length)];
        int randomImage = motivationImages[random.nextInt(motivationImages.length)];

        // 선택된 문구와 이미지 설정
        tvMotivationText.setText(randomQuote);
        ivMotivationImage.setImageResource(randomImage);

        // 시작하기 버튼 클릭 리스너
        btnContinue.setOnClickListener(v -> {
            // 로그인 화면으로 이동
            Intent intent = new Intent(MotivationActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }
} 