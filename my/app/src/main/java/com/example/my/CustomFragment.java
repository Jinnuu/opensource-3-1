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
        exerciseDescriptions.put("손목 운동", 
            "손목 관절의 유연성과 근력을 강화하는 운동입니다.\n\n" +
            "1. 손목 스트레칭\n" +
            "2. 손목 회전 운동\n" +
            "3. 손가락 굴곡/신전 운동\n\n" +
            "주의사항: 통증이 있을 경우 즉시 중단하세요.");
        
        exerciseDescriptions.put("등 운동", 
            "등 근육을 강화하고 자세를 개선하는 운동입니다.\n\n" +
            "1. 등 스트레칭\n" +
            "2. 어깨 으쓱하기\n" +
            "3. 등 근력 강화 운동\n\n" +
            "주의사항: 허리를 보호하기 위해 올바른 자세를 유지하세요.");
        
        exerciseDescriptions.put("목 운동", 
            "목 근육을 이완하고 긴장을 풀어주는 운동입니다.\n\n" +
            "1. 목 스트레칭\n" +
            "2. 목 회전 운동\n" +
            "3. 어깨 으쓱하기\n\n" +
            "주의사항: 갑작스러운 움직임을 피하고 천천히 진행하세요.");

        exerciseDescriptions.put("어깨 운동",
            "어깨 근육을 강화하고 유연성을 높이는 운동입니다.\n\n" +
            "1. 어깨 프레스\n" +
            "2. 사이드 레터럴 레이즈\n" +
            "3. 프론트 레이즈\n\n" +
            "주의사항: 무거운 무게는 피하고 천천히 진행하세요.");

        exerciseDescriptions.put("팔 운동",
            "상완근과 전완근을 강화하는 운동입니다.\n\n" +
            "1. 덤벨 컬\n" +
            "2. 트라이셉스 익스텐션\n" +
            "3. 해머 컬\n\n" +
            "주의사항: 적절한 무게를 선택하고 자세를 유지하세요.");

        exerciseDescriptions.put("가슴 운동",
            "대흉근을 발달시키는 운동입니다.\n\n" +
            "1. 푸시업\n" +
            "2. 덤벨 플라이\n" +
            "3. 벤치 프레스\n\n" +
            "주의사항: 어깨와 팔꿈치 각도를 유지하세요.");

        exerciseDescriptions.put("복근 운동",
            "복근과 코어 근육을 강화하는 운동입니다.\n\n" +
            "1. 크런치\n" +
            "2. 플랭크\n" +
            "3. 레그 레이즈\n\n" +
            "주의사항: 허리를 보호하기 위해 천천히 진행하세요.");

        exerciseDescriptions.put("엉덩이 운동",
            "둔근을 강화하고 발달시키는 운동입니다.\n\n" +
            "1. 스쿼트\n" +
            "2. 힙 쓰러스트\n" +
            "3. 글루트 브릿지\n\n" +
            "주의사항: 무릎이 발끝을 넘지 않도록 주의하세요.");

        exerciseDescriptions.put("다리 운동",
            "하체 근력을 강화하는 운동입니다.\n\n" +
            "1. 스쿼트\n" +
            "2. 런지\n" +
            "3. 레그 프레스\n\n" +
            "주의사항: 무릎 관절을 보호하기 위해 자세를 유지하세요.");
    }

    private void addExerciseChips() {
        String[] exercises = {
            "손목 운동", "등 운동", "목 운동", "어깨 운동", 
            "팔 운동", "가슴 운동", "복근 운동", "엉덩이 운동", "다리 운동"
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
                            "운동 루틴 저장 실패: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        if (response.isSuccessful()) {
                            Toast.makeText(requireContext(), 
                                "운동 루틴이 저장되었습니다.", Toast.LENGTH_SHORT).show();
                            clearInputs();
                        } else {
                            try {
                                ResponseBody responseBody = response.body();
                                String errorMessage = responseBody != null ? 
                                    responseBody.string() : "알 수 없는 오류";
                                Toast.makeText(requireContext(), 
                                    "운동 루틴 저장 실패: " + errorMessage, 
                                    Toast.LENGTH_SHORT).show();
                            } catch (IOException e) {
                                Toast.makeText(requireContext(), 
                                    "운동 루틴 저장 실패: 응답 처리 중 오류 발생", 
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