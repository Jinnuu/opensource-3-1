package com.example.my;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.gson.Gson;
import okhttp3.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// 사용자 정의 운동 루틴 화면

public class CustomFragment extends Fragment {
    private EditText etRoutineName, etRoutineDescription;
    private ChipGroup chipGroupExercises;
    private Button btnSaveRoutine, btnViewSavedRoutines;
    private TextView tvExerciseDescription;
    private final OkHttpClient client = new OkHttpClient();
    private final Gson gson = new Gson();
    private static final String PREF_NAME = "LoginPrefs";
    private static final String KEY_USER_NAME = "user_name";
    private final Map<String, String> exerciseDescriptions = new HashMap<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_custom, container, false);

        // View 초기화
        etRoutineName = view.findViewById(R.id.etRoutineName);
        etRoutineDescription = view.findViewById(R.id.etRoutineDescription);
        chipGroupExercises = view.findViewById(R.id.chipGroupExercises);
        btnSaveRoutine = view.findViewById(R.id.btnSaveRoutine);
        btnViewSavedRoutines = view.findViewById(R.id.btnViewSavedRoutines);
        tvExerciseDescription = view.findViewById(R.id.tvExerciseDescription);

        // 운동 설명 초기화
        initializeExerciseDescriptions();

        // 운동 종류 칩 추가
        addExerciseChips();

        // 저장 버튼 클릭 리스너
        btnSaveRoutine.setOnClickListener(v -> saveRoutine());

        // 저장된 루틴 보기 버튼 클릭 리스너
        btnViewSavedRoutines.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), RoutineManagementActivity.class);
            startActivity(intent);
        });

        return view;
    }

    private void initializeExerciseDescriptions() {
        // 1단계 운동
        exerciseDescriptions.put("목 앞 근육 스트레칭", "목 앞쪽 근육을 스트레칭하는 운동입니다.");
        exerciseDescriptions.put("목 좌우 근육 스트레칭", "목 좌우 근육을 스트레칭하는 운동입니다.");
        exerciseDescriptions.put("몸통 앞쪽 근육 스트레칭", "몸통 앞쪽 근육을 스트레칭하는 운동입니다.");
        exerciseDescriptions.put("몸통 옆쪽 근육 스트레칭", "몸통 옆쪽 근육을 스트레칭하는 운동입니다.");
        exerciseDescriptions.put("몸통회전 근육 스트레칭", "몸통 회전 근육을 스트레칭하는 운동입니다.");
        exerciseDescriptions.put("몸통 스트레칭 1단계", "기본적인 몸통 스트레칭 운동입니다.");
        exerciseDescriptions.put("몸통 스트레칭 2단계", "심화된 몸통 스트레칭 운동입니다.");
        exerciseDescriptions.put("날개뼈 움직이기", "날개뼈 주변 근육을 강화하는 운동입니다.");
        exerciseDescriptions.put("어깨 들어올리기", "어깨 근육을 강화하는 운동입니다.");
        exerciseDescriptions.put("날개뼈 모으기", "날개뼈 주변 근육을 강화하는 운동입니다.");
        exerciseDescriptions.put("손목 및 팔꿈치 주변 근육 스트레칭", "손목과 팔꿈치 주변 근육을 스트레칭하는 운동입니다.");
        exerciseDescriptions.put("허벅지 및 종아리 근육 스트레칭", "하체 근육을 스트레칭하는 운동입니다.");

        // 2단계 운동
        exerciseDescriptions.put("엉덩이 들기", "엉덩이 근육을 강화하는 운동입니다.");
        exerciseDescriptions.put("엎드려 누운 상태로 다리 들기", "하체 근육을 강화하는 운동입니다.");
        exerciseDescriptions.put("엉덩이 옆 근육 운동", "엉덩이 옆쪽 근육을 강화하는 운동입니다.");
        exerciseDescriptions.put("무릎 벌리기", "무릎 주변 근육을 강화하는 운동입니다.");
        exerciseDescriptions.put("무릎 펴기", "무릎 주변 근육을 강화하는 운동입니다.");
        exerciseDescriptions.put("런지", "하체 전반적인 근육을 강화하는 운동입니다.");
        exerciseDescriptions.put("좌우런지", "하체 측면 근육을 강화하는 운동입니다.");
        exerciseDescriptions.put("발전된 런지", "심화된 런지 운동입니다.");
        exerciseDescriptions.put("손목 및 팔꿈치 주변 근육", "상완부 근육을 강화하는 운동입니다.");
        exerciseDescriptions.put("날개 뼈 모음 근육", "등 근육을 강화하는 운동입니다.");
        exerciseDescriptions.put("앉았다 일어서기", "하체 근육을 강화하는 기본 운동입니다.");
        exerciseDescriptions.put("발전된 앉았다 일어서기", "심화된 하체 근육 강화 운동입니다.");
        exerciseDescriptions.put("어깨 운동 1단계", "기본적인 어깨 근육 강화 운동입니다.");
        exerciseDescriptions.put("어깨 운동 2단계", "심화된 어깨 근육 강화 운동입니다.");

        // 3단계 운동
        exerciseDescriptions.put("한발 서기", "균형감과 하체 근육을 강화하는 운동입니다.");
        exerciseDescriptions.put("버드독 1단계", "기본적인 코어 근육 강화 운동입니다.");
        exerciseDescriptions.put("버드독 2단계", "심화된 코어 근육 강화 운동입니다.");
        exerciseDescriptions.put("앉은 상태에서 제자리 걷기", "하체 근육을 강화하는 운동입니다.");
        exerciseDescriptions.put("움직이는 런지", "동적인 하체 근육 강화 운동입니다.");
    }

    private void addExerciseChips() {
        String[] exercises = {
            // 1단계 운동
            "목 앞 근육 스트레칭", "목 좌우 근육 스트레칭", "몸통 앞쪽 근육 스트레칭",
            "몸통 옆쪽 근육 스트레칭", "몸통회전 근육 스트레칭", "몸통 스트레칭 1단계",
            "몸통 스트레칭 2단계", "날개뼈 움직이기", "어깨 들어올리기",
            "날개뼈 모으기", "손목 및 팔꿈치 주변 근육 스트레칭", "허벅지 및 종아리 근육 스트레칭",

            // 2단계 운동
            "엉덩이 들기", "엎드려 누운 상태로 다리 들기", "엉덩이 옆 근육 운동",
            "무릎 벌리기", "무릎 펴기", "런지", "좌우런지", "발전된 런지",
            "손목 및 팔꿈치 주변 근육", "날개 뼈 모음 근육", "앉았다 일어서기",
            "발전된 앉았다 일어서기", "어깨 운동 1단계", "어깨 운동 2단계",

            // 3단계 운동
            "한발 서기", "버드독 1단계", "버드독 2단계",
            "앉은 상태에서 제자리 걷기", "움직이는 런지"
        };

        for (String exercise : exercises) {
            Chip chip = new Chip(requireContext());
            chip.setText(exercise);
            chip.setCheckable(true);
            
            // 칩 클릭 리스너 추가
            chip.setOnClickListener(v -> {
                if (chip.isChecked()) {
                    showExerciseDescription(exercise);
                } else {
                    hideExerciseDescription();
                }
            });
            
            chipGroupExercises.addView(chip);
        }
    }

    private void showExerciseDescription(String exercise) {
        String description = exerciseDescriptions.get(exercise);
        if (description != null) {
            tvExerciseDescription.setText(description);
            tvExerciseDescription.setVisibility(View.VISIBLE);
        }
    }

    private void hideExerciseDescription() {
        tvExerciseDescription.setVisibility(View.GONE);
    }

    private void saveRoutine() {
        String name = etRoutineName.getText().toString().trim();
        String description = etRoutineDescription.getText().toString().trim();
        List<String> selectedExercises = new ArrayList<>();

        // 선택된 운동 종류 가져오기
        for (int i = 0; i < chipGroupExercises.getChildCount(); i++) {
            Chip chip = (Chip) chipGroupExercises.getChildAt(i);
            if (chip.isChecked()) {
                selectedExercises.add(chip.getText().toString());
            }
        }

        if (name.isEmpty() || description.isEmpty() || selectedExercises.isEmpty()) {
            Toast.makeText(requireContext(), "모든 필드를 입력하고 운동을 선택해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        SharedPreferences prefs = requireActivity().getSharedPreferences(PREF_NAME, 0);
        String userName = prefs.getString(KEY_USER_NAME, null);

        if (userName == null) {
            Toast.makeText(requireContext(), "로그인이 필요합니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        ExerciseRoutine routine = new ExerciseRoutine();
        routine.setName(name);
        routine.setDescription(description);
        routine.setExercises(selectedExercises);

        String json = gson.toJson(routine);
        RequestBody body = RequestBody.create(
            MediaType.parse("application/json"), json);

        Request request = new Request.Builder()
            .url(Constants.API_ROUTINES + "?userName=" + userName)
            .post(body)
            .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        Toast.makeText(requireContext(), 
                            "루틴 저장 실패: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        if (response.isSuccessful()) {
                            Toast.makeText(requireContext(), 
                                "루틴이 저장되었습니다.", Toast.LENGTH_SHORT).show();
                            clearInputs();
                        } else {
                            try {
                                ResponseBody responseBody = response.body();
                                String errorMessage = responseBody != null ? 
                                    responseBody.string() : "알 수 없는 오류";
                                Toast.makeText(requireContext(), 
                                    "루틴 저장 실패: " + errorMessage, 
                                    Toast.LENGTH_SHORT).show();
                            } catch (IOException e) {
                                Toast.makeText(requireContext(), 
                                    "루틴 저장 실패: 응답 처리 중 오류 발생", 
                                    Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }

    private void clearInputs() {
        etRoutineName.setText("");
        etRoutineDescription.setText("");
        for (int i = 0; i < chipGroupExercises.getChildCount(); i++) {
            Chip chip = (Chip) chipGroupExercises.getChildAt(i);
            chip.setChecked(false);
        }
        hideExerciseDescription();
    }
} 