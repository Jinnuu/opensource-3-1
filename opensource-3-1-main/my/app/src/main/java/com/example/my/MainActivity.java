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

/**
 * 메인 액티비티 클래스
 * 앱의 핵심 네비게이션을 담당하며, 하단 탭을 통해 각 기능 화면으로 이동할 수 있게 함
 * 운동, 커스텀, 리포트, 설정의 4개 주요 기능을 탭으로 구분하여 제공
 */
public class MainActivity extends AppCompatActivity {

    // 하단 네비게이션 뷰 - 탭 전환을 위한 UI 컴포넌트
    private BottomNavigationView bottomNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 하단 네비게이션 뷰 초기화 및 설정
        bottomNavigation = findViewById(R.id.bottom_navigation);
        
        // 탭 선택 시 해당하는 프래그먼트로 화면 전환
        bottomNavigation.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int itemId = item.getItemId();

            // 선택된 탭에 따라 적절한 프래그먼트 생성
            if (itemId == R.id.navigation_exercise) {
                selectedFragment = createExerciseFragment();
            } else if (itemId == R.id.navigation_custom) {
                selectedFragment = new CustomFragment();
            } else if (itemId == R.id.navigation_report) {
                selectedFragment = new ReportFragment();
            } else if (itemId == R.id.navigation_settings) {
                selectedFragment = new SettingsFragment();
            }

            // 선택된 프래그먼트가 있으면 화면에 표시
            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, selectedFragment)
                        .commit();
            }

            return true;
        });

        // 다른 액티비티에서 전달받은 프래그먼트 정보 처리
        // 특정 화면으로 직접 이동하고 싶을 때 사용
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
            // 앱 시작 시 기본 화면 설정 (운동 화면)
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, createExerciseFragment())
                        .commit();
            }
        }
    }

    /**
     * 운동 프래그먼트 생성 메서드
     * 사용자가 선택한 운동 부위 정보를 프래그먼트에 전달
     * @return 설정된 운동 프래그먼트
     */
    private ExerciseFragment createExerciseFragment() {
        ExerciseFragment fragment = new ExerciseFragment();
        Bundle args = new Bundle();

        // Body_Setting 액티비티에서 전달받은 운동 부위 순서 정보 처리
        ArrayList<String> orderedParts = getIntent().getStringArrayListExtra("orderedParts");
        if (orderedParts != null) {
            // 새로 설정된 부위 정보가 있으면 사용
            args.putStringArrayList("orderedParts", orderedParts);
            fragment.setArguments(args);
        } else {
            // 저장된 설정 정보가 없으면 SharedPreferences에서 기존 설정 가져오기
            SharedPreferences prefs = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
            String selectedPartsJson = prefs.getString("selected_parts", "[]");
            Gson gson = new Gson();
            List<String> selectedParts = gson.fromJson(selectedPartsJson, new TypeToken<List<String>>(){}.getType());
            args.putStringArrayList("orderedParts", new ArrayList<>(selectedParts));
            fragment.setArguments(args);
        }

        return fragment;
    }

    /**
     * 커스텀 탭으로 강제 이동하는 메서드
     * 다른 액티비티에서 커스텀 화면으로 돌아가고 싶을 때 호출
     */
    public void selectCustomTab() {
        bottomNavigation.setSelectedItemId(R.id.navigation_custom);
    }
}