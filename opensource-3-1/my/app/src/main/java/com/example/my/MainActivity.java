package com.example.my;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 1. Body_Setting에서 전달받은 데이터 확인
        ArrayList<String> orderedParts = getIntent().getStringArrayListExtra("orderedParts");

        BottomNavigationView bottomNavigation = findViewById(R.id.bottom_navigation);
        bottomNavigation.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int itemId = item.getItemId();

            if (itemId == R.id.navigation_exercise) {
                // ExerciseFragment에 데이터 전달 (orderedParts 존재 시)
                selectedFragment = (orderedParts != null)
                        ? ExerciseFragment.newInstance(orderedParts)
                        : new ExerciseFragment();
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

        // 2. 초기 Fragment 설정 (orderedParts 데이터 반영)
        if (savedInstanceState == null) {
            Fragment initialFragment = (orderedParts != null)
                    ? ExerciseFragment.newInstance(orderedParts)
                    : new ExerciseFragment();

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, initialFragment)
                    .commit();
        }
    }
}
