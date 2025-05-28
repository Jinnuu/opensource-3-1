package com.example.my;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import okhttp3.*;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

// 사용자 정의 운동 루틴 화면

public class CustomExerciseActivity extends AppCompatActivity {
    private RecyclerView recyclerViewRoutines;
    private RoutineAdapter adapter;
    private final OkHttpClient client = new OkHttpClient();
    private final Gson gson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_exercise);

        recyclerViewRoutines = findViewById(R.id.recyclerViewRoutines);
        Button btnAddRoutine = findViewById(R.id.btnAddRoutine);

        // RecyclerView 설정
        adapter = new RoutineAdapter(new ArrayList<>(), this::startExercise, false);
        recyclerViewRoutines.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewRoutines.setAdapter(adapter);

        // 새 루틴 만들기 버튼 클릭 리스너
        btnAddRoutine.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("fragment", "custom");
            startActivity(intent);
            finish();
        });

        // 저장된 루틴 로드
        loadRoutines();
    }

    private void loadRoutines() {
        String userName = getSharedPreferences("LoginPrefs", 0)
            .getString("user_name", null);

        if (userName == null) {
            Toast.makeText(this, "로그인이 필요합니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        Request request = new Request.Builder()
            .url(Constants.API_ROUTINES + "?userName=" + userName)
            .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> {
                    Toast.makeText(CustomExerciseActivity.this, 
                        "운동 루틴 로드 실패: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseBody = response.body().string();
                runOnUiThread(() -> {
                    if (response.isSuccessful()) {
                        Type listType = new TypeToken<List<ExerciseRoutine>>(){}.getType();
                        List<ExerciseRoutine> routines = gson.fromJson(responseBody, listType);
                        adapter.updateRoutines(routines);
                    } else {
                        Toast.makeText(CustomExerciseActivity.this, 
                            "운동 루틴 로드 실패: " + responseBody, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void startExercise(ExerciseRoutine routine) {
        // 운동 루틴의 첫 번째 운동으로 시작
        if (routine.getExercises() != null && !routine.getExercises().isEmpty()) {
            Intent intent = new Intent(this, ExerciseActivity.class);
            intent.putExtra("exercise_type", routine.getExercises().get(0));
            intent.putExtra("routine_name", routine.getName());
            intent.putExtra("current_exercise_index", 0);
            intent.putExtra("total_exercises", routine.getExercises().size());
            intent.putExtra("routine_exercises", new ArrayList<>(routine.getExercises()));
            startActivity(intent);
        } else {
            Toast.makeText(this, "이 루틴에는 운동이 없습니다.", Toast.LENGTH_SHORT).show();
        }
    }
} 