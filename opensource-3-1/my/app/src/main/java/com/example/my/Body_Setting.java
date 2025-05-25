package com.example.my;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import java.util.ArrayList;
import java.util.List;

public class Body_Setting extends AppCompatActivity {

    private List<String> selectedParts = new ArrayList<>();
    private Button btnBack, btnShoulder, btnArm, btnChest, btnAbs, btnHip, btnLeg, btnFull, btnNext;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_body_setting);

        // 버튼 초기화
        initViews();
        setupButtonListeners();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void initViews() {
        btnBack = findViewById(R.id.btn_back);
        btnShoulder = findViewById(R.id.btn_shoulder);
        btnArm = findViewById(R.id.btn_arm);
        btnChest = findViewById(R.id.btn_chest);
        btnAbs = findViewById(R.id.btn_abs);
        btnHip = findViewById(R.id.btn_hip);
        btnLeg = findViewById(R.id.btn_leg);
        btnFull = findViewById(R.id.btn_full);
        btnNext = findViewById(R.id.btn_next);
    }

    private void setupButtonListeners() {
        // 부위 선택 버튼 리스너
        View.OnClickListener partClickListener = v -> {
            Button button = (Button) v;
            String part = button.getText().toString();
            toggleSelection(part, button);
        };

        btnBack.setOnClickListener(partClickListener);
        btnShoulder.setOnClickListener(partClickListener);
        btnArm.setOnClickListener(partClickListener);
        btnChest.setOnClickListener(partClickListener);
        btnAbs.setOnClickListener(partClickListener);
        btnHip.setOnClickListener(partClickListener);
        btnLeg.setOnClickListener(partClickListener);
        btnFull.setOnClickListener(partClickListener);

        // 다음 버튼 리스너
        btnNext.setOnClickListener(v -> navigateToExerciseFragment());
    }

    private void toggleSelection(String part, Button button) {
        if (selectedParts.contains(part)) {
            selectedParts.remove(part);
            button.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.primaryGreen)));
        } else {
            selectedParts.add(part);
            button.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FFA0A0"))); // 선택된 색상
        }
    }

    private void navigateToExerciseFragment() {
        Intent intent = new Intent(this, ExerciseFragment.class);
        intent.putStringArrayListExtra("selectedParts", new ArrayList<>(selectedParts));
        startActivity(intent);
    }
}
