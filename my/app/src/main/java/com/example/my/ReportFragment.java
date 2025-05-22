package com.example.my;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import okhttp3.*;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ReportFragment extends Fragment {
    private BarChart barChart;
    private static final String SERVER_URL = "http://172.30.69.236:8080/api/workouts";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_report, container, false);
        
        barChart = view.findViewById(R.id.barChart);
        setupBarChart();
        loadWorkoutData();
        
        return view;
    }

    private void setupBarChart() {
        barChart.getDescription().setEnabled(false);
        barChart.setDrawGridBackground(false);
        barChart.setDrawBarShadow(false);
        barChart.setScaleEnabled(true);
        barChart.setPinchZoom(false);
        barChart.setDrawValueAboveBar(true);

        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);
        xAxis.setLabelCount(4);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(new String[]{"손목", "허리", "목", "커스텀"}));

        barChart.getAxisLeft().setDrawGridLines(true);
        barChart.getAxisRight().setEnabled(false);
        barChart.getLegend().setEnabled(false);
    }

    private void loadWorkoutData() {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
            .url(SERVER_URL)
            .get()
            .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        // 에러 처리
                    });
                }
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    String jsonData = response.body().string();
                    Gson gson = new Gson();
                    Type listType = new TypeToken<List<Workout>>(){}.getType();
                    List<Workout> workouts = gson.fromJson(jsonData, listType);
                    
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> updateChart(workouts));
                    }
                }
            }
        });
    }

    private void updateChart(List<Workout> workouts) {
        int[] exerciseCounts = new int[4]; // 손목, 허리, 목, 커스텀

        for (Workout workout : workouts) {
            switch (workout.getType()) {
                case "WRIST":
                    exerciseCounts[0]++;
                    break;
                case "BACK":
                    exerciseCounts[1]++;
                    break;
                case "NECK":
                    exerciseCounts[2]++;
                    break;
                case "CUSTOM":
                    exerciseCounts[3]++;
                    break;
            }
        }

        ArrayList<BarEntry> entries = new ArrayList<>();
        for (int i = 0; i < exerciseCounts.length; i++) {
            entries.add(new BarEntry(i, exerciseCounts[i]));
        }

        BarDataSet dataSet = new BarDataSet(entries, "운동 횟수");
        dataSet.setColor(Color.parseColor("#3366FF"));
        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setValueTextSize(12f);

        BarData data = new BarData(dataSet);
        barChart.setData(data);
        barChart.invalidate();
    }
} 