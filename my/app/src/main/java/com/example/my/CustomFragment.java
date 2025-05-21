package com.example.my;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import okhttp3.*;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class CustomFragment extends Fragment {
    private EditText etRoutineName, etRoutineDescription;
    private ChipGroup chipGroupExercises;
    private Button btnSaveRoutine;
    private RecyclerView recyclerViewRoutines;
    private RoutineAdapter adapter;
    private final OkHttpClient client = new OkHttpClient();
    private final Gson gson = new Gson();
    private static final String PREF_NAME = "LoginPrefs";
    private static final String KEY_USER_NAME = "user_name";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_custom, container, false);

        // View 초기화
        etRoutineName = view.findViewById(R.id.etRoutineName);
        etRoutineDescription = view.findViewById(R.id.etRoutineDescription);
        chipGroupExercises = view.findViewById(R.id.chipGroupExercises);
        btnSaveRoutine = view.findViewById(R.id.btnSaveRoutine);
        recyclerViewRoutines = view.findViewById(R.id.recyclerViewRoutines);

        // 운동 종류 칩 추가
        addExerciseChips();

        // RecyclerView 설정
        setupRecyclerView();

        // 저장 버튼 클릭 리스너
        btnSaveRoutine.setOnClickListener(v -> saveRoutine());

        // 저장된 루틴 로드
        loadRoutines();

        return view;
    }

    private void addExerciseChips() {
        String[] exercises = {"손목 운동", "등 운동", "목 운동"};
        for (String exercise : exercises) {
            Chip chip = new Chip(requireContext());
            chip.setText(exercise);
            chip.setCheckable(true);
            chipGroupExercises.addView(chip);
        }
    }

    private void setupRecyclerView() {
        adapter = new RoutineAdapter(new ArrayList<>(), this::deleteRoutine);
        recyclerViewRoutines.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerViewRoutines.setAdapter(adapter);
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
                            loadRoutines();
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

    private void loadRoutines() {
        SharedPreferences prefs = requireActivity().getSharedPreferences(PREF_NAME, 0);
        String userName = prefs.getString(KEY_USER_NAME, null);

        if (userName == null) {
            Toast.makeText(requireContext(), "로그인이 필요합니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        Request request = new Request.Builder()
            .url(Constants.API_ROUTINES + "?userName=" + userName)
            .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        Toast.makeText(requireContext(), 
                            "운동 루틴 로드 실패: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (getActivity() != null) {
                    String responseBody = response.body().string();
                    getActivity().runOnUiThread(() -> {
                        if (response.isSuccessful()) {
                            Type listType = new TypeToken<List<ExerciseRoutine>>(){}.getType();
                            List<ExerciseRoutine> routines = gson.fromJson(responseBody, listType);
                            adapter.updateRoutines(routines);
                        } else {
                            Toast.makeText(requireContext(), 
                                "운동 루틴 로드 실패: " + responseBody, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    private void deleteRoutine(Long routineId) {
        SharedPreferences prefs = requireActivity().getSharedPreferences(PREF_NAME, 0);
        String userName = prefs.getString(KEY_USER_NAME, null);

        if (userName == null) {
            Toast.makeText(requireContext(), "로그인이 필요합니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        Request request = new Request.Builder()
            .url(Constants.API_ROUTINES + "/" + routineId + "?userName=" + userName)
            .delete()
            .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        Toast.makeText(requireContext(), 
                            "운동 루틴 삭제 실패: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        if (response.isSuccessful()) {
                            Toast.makeText(requireContext(), 
                                "운동 루틴이 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                            loadRoutines();
                        } else {
                            try {
                                ResponseBody responseBody = response.body();
                                String errorMessage = responseBody != null ? 
                                    responseBody.string() : "알 수 없는 오류";
                                Toast.makeText(requireContext(), 
                                    "운동 루틴 삭제 실패: " + errorMessage, 
                                    Toast.LENGTH_SHORT).show();
                            } catch (IOException e) {
                                Toast.makeText(requireContext(), 
                                    "운동 루틴 삭제 실패: 응답 처리 중 오류 발생", 
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
    }
} 