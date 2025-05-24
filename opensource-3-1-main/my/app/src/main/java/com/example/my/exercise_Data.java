package com.example.my;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Arrays;
import java.util.List;

public class exercise_Data extends AppCompatActivity {

    private List<Pair<LinearLayout, String>> exerciseItems;
    private LinearLayout layoutSubFilters;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_data);

        // 운동 아이템 참조 (XML에 추가한 모든 아이템 포함)
        LinearLayout itemVup = findViewById(R.id.item_vup);
        LinearLayout itemGluteBridge = findViewById(R.id.item_glute_bridge);
        LinearLayout itemPullup = findViewById(R.id.item_pullup);
        LinearLayout itemShoulderPress = findViewById(R.id.item_shoulder_press);
        LinearLayout itemBicepsCurl = findViewById(R.id.item_biceps_curl);
        LinearLayout itemBenchPress = findViewById(R.id.item_bench_press);
        LinearLayout itemSquat = findViewById(R.id.item_squat);
        LinearLayout itemBurpee = findViewById(R.id.item_burpee);

        // 운동 아이템과 부위 정보 연결
        exerciseItems = Arrays.asList(
                new Pair<>(itemVup, "복근"),
                new Pair<>(itemGluteBridge, "엉덩이"),
                new Pair<>(itemPullup, "등"),
                new Pair<>(itemShoulderPress, "어깨"),
                new Pair<>(itemBicepsCurl, "팔"),
                new Pair<>(itemBenchPress, "가슴"),
                new Pair<>(itemSquat, "다리"),
                new Pair<>(itemBurpee, "전신")
        );

        // 하위 필터 버튼 영역 참조
        layoutSubFilters = findViewById(R.id.layoutSubFilters);

        // 각 운동 아이템 클릭 시 프래그먼트 전환
        for (Pair<LinearLayout, String> item : exerciseItems) {
            item.first.setOnClickListener(v -> {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.layout_exercise, new exercise_guide())
                        .addToBackStack(null)
                        .commit();
            });
        }

        // "모두" 버튼 설정
        Button btnFilterAll = findViewById(R.id.btnFilterAll);
        btnFilterAll.setOnClickListener(v -> {
            // 하위 필터 토글
            toggleSubFilters();
            // 모든 운동 보이기
            showAllExercises();
        });

        // 검색창 설정
        setupSearch();

        // 부위별 필터 버튼 설정
        setupFilterButtons();
    }

    // 하위 필터 버튼 표시/숨김 토글
    private void toggleSubFilters() {
        layoutSubFilters.setVisibility(
                layoutSubFilters.getVisibility() == View.VISIBLE ?
                        View.GONE : View.VISIBLE
        );
    }

    // 모든 운동 보이기
    private void showAllExercises() {
        for (Pair<LinearLayout, String> item : exerciseItems) {
            item.first.setVisibility(View.VISIBLE);
        }
    }

    // 검색 기능 설정
    private void setupSearch() {
        EditText etSearch = findViewById(R.id.etSearch);
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                filterExercises(s.toString());
            }
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });
    }

    // 부위별 필터 버튼 설정
    private void setupFilterButtons() {
        setupFilterButton(R.id.btnFilterBack, "등");
        setupFilterButton(R.id.btnFilterShoulder, "어깨");
        setupFilterButton(R.id.btnFilterArm, "팔");
        setupFilterButton(R.id.btnFilterChest, "가슴");
        setupFilterButton(R.id.btnFilterAbs, "복근");
        setupFilterButton(R.id.btnFilterGlutes, "엉덩이");
        setupFilterButton(R.id.btnFilterLeg, "다리");
        setupFilterButton(R.id.btnFilterWhole, "전신");
    }

    // 개별 필터 버튼 클릭 리스너
    private void setupFilterButton(int buttonId, String part) {
        Button btn = findViewById(buttonId);
        btn.setOnClickListener(v -> {
            for (Pair<LinearLayout, String> item : exerciseItems) {
                item.first.setVisibility(
                        item.second.equals(part) ? View.VISIBLE : View.GONE
                );
            }
        });
    }

    // 검색어로 필터링
    private void filterExercises(String query) {
        for (Pair<LinearLayout, String> item : exerciseItems) {
            // 운동명(버튼 텍스트)과 부위에서 검색
            Button btn = item.first.findViewById(R.id.btnFilterAll);
            String name = btn.getText().toString();
            boolean match = name.contains(query) || item.second.contains(query);
            item.first.setVisibility(match ? View.VISIBLE : View.GONE);
        }
    }
}
