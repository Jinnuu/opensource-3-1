package com.example.my;

import static com.example.my.ExerciseFragment.PREF_NAME;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// 신체 설정 화면 (환경 설정에서 연결)

public class Body_Setting extends AppCompatActivity {

    // 전체 부위 리스트
    private final List<String> allParts = Arrays.asList("팔", "등", "전신", "다리", "목", "어깨");
    // 사용자가 선택한 부위 순서대로 저장
    private List<String> selectedParts = new ArrayList<>();

    private Button btnArm, btnBack, btnFull, btnLeg, btnNeck, btnShoulder, btnNext;
    private ImageView bodyImage;

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
        btnArm = findViewById(R.id.btn_arm);
        btnBack = findViewById(R.id.btn_back);
        btnFull = findViewById(R.id.btn_full);
        btnLeg = findViewById(R.id.btn_leg);
        btnNeck = findViewById(R.id.btn_neck);
        btnShoulder = findViewById(R.id.btn_shoulder);
        btnNext = findViewById(R.id.btn_next);
        bodyImage = findViewById(R.id.body);
    }

    private void setupButtonListeners() {
        // 부위 선택 버튼 리스너
        setupPartButton(btnArm, "팔", R.drawable.arm);
        setupPartButton(btnBack, "등", R.drawable.back);
        setupPartButton(btnFull, "전신", R.drawable.full);
        setupPartButton(btnLeg, "다리", R.drawable.leg);
        setupPartButton(btnNeck, "목", R.drawable.neck);
        setupPartButton(btnShoulder, "어깨", R.drawable.shoulder);

        // 다음 버튼 클릭 시 ExerciseFragment로 전환
        btnNext.setOnClickListener(v -> {
            // 1. 신체설정 완료 상태 저장
            SharedPreferences prefs = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
            String userName = getIntent().getStringExtra("userName");

            // 선택된 부위들을 JSON 문자열로 변환
            Gson gson = new Gson();
            String selectedPartsJson = gson.toJson(selectedParts);

            prefs.edit()
                    .putBoolean("is_body_setting_done", true)
                    .putString("selected_parts", selectedPartsJson)
                    .putBoolean(PREF_NAME + "_" + userName + "_body_setting", true)
                    .apply();

            // 2. MainActivity로 이동하면서 데이터 전달
            Intent intent = new Intent(Body_Setting.this, MainActivity.class);
            intent.putStringArrayListExtra("orderedParts", new ArrayList<>(selectedParts));
            intent.putExtra("userName", userName);
            startActivity(intent);
            finish(); // Body_Setting 화면 종료
        });
    }

    private void setupPartButton(Button button, String part, int imageResId) {
        button.setOnClickListener(v -> {
            toggleSelection(part, button);
            updateBodyImage();
        });
    }

    private void toggleSelection(String part, Button button) {
        if (selectedParts.contains(part)) {
            selectedParts.remove(part);
            button.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.primaryGreen)));
        } else {
            // 전신이 선택된 경우 다른 부위 선택 해제
            if (part.equals("전신")) {
                clearAllSelections();
            }
            // 다른 부위가 선택된 경우 전신 선택 해제
            else if (selectedParts.contains("전신")) {
                selectedParts.remove("전신");
                btnFull.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.primaryGreen)));
            }
            selectedParts.add(part);
            button.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FFA0A0"))); // 선택된 색상
        }
    }

    private void clearAllSelections() {
        selectedParts.clear();
        btnArm.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.primaryGreen)));
        btnBack.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.primaryGreen)));
        btnFull.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.primaryGreen)));
        btnLeg.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.primaryGreen)));
        btnNeck.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.primaryGreen)));
        btnShoulder.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.primaryGreen)));
    }

    private void updateBodyImage() {
        if (selectedParts.isEmpty()) {
            bodyImage.setImageResource(R.drawable.body);
        } else if (selectedParts.contains("전신")) {
            bodyImage.setImageResource(R.drawable.full);
        } else if (!selectedParts.isEmpty()) {
            // 마지막으로 선택된 부위의 이미지 표시
            String lastSelectedPart = selectedParts.get(selectedParts.size() - 1);
            switch (lastSelectedPart) {
                case "팔":
                    bodyImage.setImageResource(R.drawable.arm);
                    break;
                case "등":
                    bodyImage.setImageResource(R.drawable.back);
                    break;
                case "다리":
                    bodyImage.setImageResource(R.drawable.leg);
                    break;
                case "목":
                    bodyImage.setImageResource(R.drawable.neck);
                    break;
                case "어깨":
                    bodyImage.setImageResource(R.drawable.shoulder);
                    break;
            }
        }
    }
}