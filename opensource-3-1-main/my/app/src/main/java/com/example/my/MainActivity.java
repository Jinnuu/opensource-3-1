package com.example.my;

import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.util.ArrayList;
import java.util.List;

// 메인 화면

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigation = findViewById(R.id.bottom_navigation);
        bottomNavigation.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int itemId = item.getItemId();

            if (itemId == R.id.navigation_exercise) {
                selectedFragment = createExerciseFragment();
            } else if (itemId == R.id.navigation_custom) {
                selectedFragment = new CustomFragment();
            } else if (itemId == R.id.navigation_report) {
                selectedFragment = new ReportFragment();
            } else if (itemId == R.id.navigation_settings) {
                selectedFragment = new SettingsFragment();
            }

            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, selectedFragment)
                        .commit();
            }

            return true;
        });

        // Intent로 전달된 fragment 파라미터 처리
        String fragmentName = getIntent().getStringExtra("fragment");
        if (fragmentName != null) {
            Fragment selectedFragment = null;
            if ("custom".equals(fragmentName)) {
                selectedFragment = new CustomFragment();
                bottomNavigation.setSelectedItemId(R.id.navigation_custom);
            }

            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, selectedFragment)
                        .commit();
            }
        } else {
            // 기본 Fragment 설정
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, createExerciseFragment())
                        .commit();
            }
        }
    }

    private ExerciseFragment createExerciseFragment() {
        ExerciseFragment fragment = new ExerciseFragment();
        Bundle args = new Bundle();

        // Body_Setting에서 전달받은 데이터 처리
        ArrayList<String> orderedParts = getIntent().getStringArrayListExtra("orderedParts");
        if (orderedParts != null) {
            args.putStringArrayList("orderedParts", orderedParts);
            fragment.setArguments(args);
        } else {
            // SharedPreferences에서 저장된 부위 정보 가져오기
            SharedPreferences prefs = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
            String selectedPartsJson = prefs.getString("selected_parts", "[]");
            Gson gson = new Gson();
            List<String> selectedParts = gson.fromJson(selectedPartsJson, new TypeToken<List<String>>(){}.getType());
            args.putStringArrayList("orderedParts", new ArrayList<>(selectedParts));
            fragment.setArguments(args);
        }

        return fragment;
    }

    public void selectCustomTab() {
        bottomNavigation.setSelectedItemId(R.id.navigation_custom);
    }
}