package com.example.my;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import android.widget.Button;

public class exercise_Data extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_data); // ▲ 반드시 Activity용 레이아웃 사용 ▲

        Button btnGoToGuide = findViewById(R.id.btnFilter);
        btnGoToGuide.setOnClickListener(v -> {
            // Fragment 트랜잭션은 Activity의 SupportFragmentManager로 처리
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new exercise_guide())
                    .addToBackStack(null)
                    .commit();
        });
    }
}
