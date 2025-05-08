package com.example.my;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
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
import java.util.Map;

public class ReportFragment extends Fragment {
    private static final String TAG = "ReportFragment";
    private BarChart barChart;
    private RecyclerView recyclerView;
    private TextView tvTotalTime;
    private WorkoutAdapter adapter;
    private final OkHttpClient client = new OkHttpClient();
    private final Gson gson = new Gson();
    private static final String PREF_NAME = "LoginPrefs";
    private static final String KEY_USER_NAME = "user_name";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_report, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        barChart = view.findViewById(R.id.barChart);
        recyclerView = view.findViewById(R.id.recyclerView);
        tvTotalTime = view.findViewById(R.id.tvTotalTime);

        setupRecyclerView();
        setupBarChart();
        loadWorkoutData();
    }

    private void setupRecyclerView() {
        adapter = new WorkoutAdapter(new ArrayList<>());
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);
    }

    private void setupBarChart() {
        barChart.getDescription().setEnabled(false);
        barChart.setDrawGridBackground(false);
        barChart.setDrawBarShadow(false);
        barChart.setScaleEnabled(false);
        barChart.setPinchZoom(false);
        barChart.setDrawValueAboveBar(true);
        barChart.getLegend().setEnabled(false);

        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);
        xAxis.setLabelCount(7);
        xAxis.setLabelRotationAngle(45f);

        YAxis leftAxis = barChart.getAxisLeft();
        leftAxis.setDrawGridLines(true);
        leftAxis.setAxisMinimum(0f);

        barChart.getAxisRight().setEnabled(false);
    }

    private void loadWorkoutData() {
        SharedPreferences prefs = requireActivity().getSharedPreferences(PREF_NAME, 0);
        String userName = prefs.getString(KEY_USER_NAME, null);

        if (userName == null) {
            Toast.makeText(requireContext(), "로그인이 필요합니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d(TAG, "Loading workout data for user: " + userName);
        String url = Constants.API_WORKOUTS + "/weekly?userName=" + userName;
        Log.d(TAG, "Request URL: " + url);

        Request request = new Request.Builder()
            .url(url)
            .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "Failed to load workout data", e);
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        Toast.makeText(requireContext(), 
                            "운동 데이터 로드 실패: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseBody = response.body().string();
                Log.d(TAG, "Server response code: " + response.code());
                Log.d(TAG, "Server response body: " + responseBody);

                if (getActivity() == null) return;

                if (response.isSuccessful()) {
                    try {
                        Type listType = new TypeToken<List<Map<String, Object>>>(){}.getType();
                        List<Map<String, Object>> weeklyData = gson.fromJson(responseBody, listType);
                        
                        if (weeklyData != null && !weeklyData.isEmpty()) {
                            getActivity().runOnUiThread(() -> {
                                updateChart(weeklyData);
                                updateTotalTime(weeklyData);
                            });
                        } else {
                            Log.d(TAG, "No workout data available");
                            getActivity().runOnUiThread(() -> {
                                Toast.makeText(requireContext(), 
                                    "운동 데이터가 없습니다.", Toast.LENGTH_SHORT).show();
                            });
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing response", e);
                        getActivity().runOnUiThread(() -> {
                            Toast.makeText(requireContext(), 
                                "데이터 파싱 실패: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
                    }
                } else {
                    getActivity().runOnUiThread(() -> {
                        Toast.makeText(requireContext(), 
                            "운동 데이터 로드 실패: " + responseBody, Toast.LENGTH_SHORT).show();
                    });
                }
            }
        });
    }

    private void updateChart(List<Map<String, Object>> weeklyData) {
        List<BarEntry> entries = new ArrayList<>();
        List<String> labels = new ArrayList<>();
        
        for (int i = 0; i < weeklyData.size(); i++) {
            Map<String, Object> dayData = weeklyData.get(i);
            double seconds = ((Number) dayData.get("duration")).doubleValue();
            double minutes = seconds / 60;
            entries.add(new BarEntry(i, (float) minutes));
            labels.add((String) dayData.get("date"));
        }

        BarDataSet dataSet = new BarDataSet(entries, "운동 시간 (분)");
        dataSet.setColor(requireContext().getResources().getColor(android.R.color.holo_blue_bright));

        BarData data = new BarData(dataSet);
        data.setBarWidth(0.7f);

        barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labels));
        barChart.setData(data);
        barChart.animateY(1000);
        barChart.invalidate();
    }

    private void updateTotalTime(List<Map<String, Object>> weeklyData) {
        double totalSeconds = 0;
        for (Map<String, Object> dayData : weeklyData) {
            totalSeconds += ((Number) dayData.get("duration")).doubleValue();
        }

        double totalMinutes = totalSeconds / 60;
        int hours = (int) (totalMinutes / 60);
        int minutes = (int) (totalMinutes % 60);
        tvTotalTime.setText(String.format("총 운동 시간: %d시간 %d분", hours, minutes));
    }
} 