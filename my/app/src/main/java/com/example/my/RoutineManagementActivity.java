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

// 운동 루틴 관리 화면

public class RoutineManagementActivity extends AppCompatActivity {
    private RecyclerView recyclerViewRoutines;
    private RoutineAdapter adapter;
    private final OkHttpClient client = new OkHttpClient();
    private final Gson gson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_routine_management);

        recyclerViewRoutines = findViewById(R.id.recyclerViewRoutines);
        Button btnAddRoutine = findViewById(R.id.btnAddRoutine);

        // RecyclerView 설정
        adapter = new RoutineAdapter(new ArrayList<>(), this::deleteRoutine, true);
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
                    Toast.makeText(RoutineManagementActivity.this, 
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
                        Toast.makeText(RoutineManagementActivity.this, 
                            "운동 루틴 로드 실패: " + responseBody, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void deleteRoutine(ExerciseRoutine routine) {
        String userName = getSharedPreferences("LoginPrefs", 0)
            .getString("user_name", null);

        if (userName == null) {
            Toast.makeText(this, "로그인이 필요합니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        // HttpUrl로 쿼리 파라미터 추가
        HttpUrl url = HttpUrl.parse(Constants.API_ROUTINES + "/" + routine.getId())
            .newBuilder()
            .addQueryParameter("userName", userName)
            .build();

        Request request = new Request.Builder()
            .url(url)
            .delete()
            .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> {
                    Toast.makeText(RoutineManagementActivity.this, 
                        "루틴 삭제 실패: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                runOnUiThread(() -> {
                    if (response.isSuccessful()) {
                        Toast.makeText(RoutineManagementActivity.this, 
                            "루틴이 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                        // 루틴 목록 새로고침
                        loadRoutines();
                    } else {
                        String errorMessage;
                        try {
                            errorMessage = response.body().string();
                        } catch (IOException e) {
                            errorMessage = "알 수 없는 오류가 발생했습니다.";
                        }
                        Toast.makeText(RoutineManagementActivity.this, 
                            "루틴 삭제 실패: " + errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
} 