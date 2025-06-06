package com.example.my;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

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
                selectedFragment = new ExerciseFragment();
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

        // Intent에서 fragment 플래그 확인
        String fragment = getIntent().getStringExtra("fragment");
        if ("exercise".equals(fragment)) {
            // ExerciseFragment로 이동
            getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new ExerciseFragment())
                .commit();
        } else {
            // 기본적으로 ExerciseFragment 표시
            getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new ExerciseFragment())
                .commit();
        }

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
                        .replace(R.id.fragment_container, new ExerciseFragment())
                        .commit();
            }
        }
    }

    public void selectCustomTab() {
        bottomNavigation.setSelectedItemId(R.id.navigation_custom);
    }
}