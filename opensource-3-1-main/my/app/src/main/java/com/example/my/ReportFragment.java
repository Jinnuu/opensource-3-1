package com.example.my;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import okhttp3.*;
import java.io.IOException;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Date;
import java.util.HashMap;
import com.github.mikephil.charting.formatter.ValueFormatter;

/**
 * 운동 기록 리포트 프래그먼트 클래스
 * 사용자의 운동 기록을 시각적으로 분석하고 표시하는 화면
 * MPAndroidChart 라이브러리를 사용한 막대 그래프와 테이블 형태로 데이터 제공
 * 운동 시간, 완료율, 루틴 진행 상황 등을 종합적으로 확인 가능
 */
public class ReportFragment extends Fragment {
    private static final String TAG = "ReportFragment";
    
    // UI 컴포넌트들
    private TableLayout tableLayout;    // 운동 기록을 표시하는 테이블
    private BarChart workoutChart;      // 운동 시간을 시각화하는 막대 그래프
    
    // 네트워크 통신을 위한 객체들
    private final OkHttpClient client = new OkHttpClient();
    private final Gson gson = new Gson();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_report, container, false);
        
        // UI 컴포넌트 초기화
        tableLayout = view.findViewById(R.id.tableLayout);
        workoutChart = view.findViewById(R.id.workoutChart);
        
        // 서버에서 운동 데이터 로드
        loadWorkoutData();
        
        return view;
    }

    /**
     * 서버에서 사용자의 운동 기록 데이터를 로드
     * 로그인한 사용자의 운동 이력을 가져와서 화면에 표시
     */
    private void loadWorkoutData() {
        // SharedPreferences에서 현재 로그인한 사용자 이름 가져오기
        String userName = getActivity().getSharedPreferences("LoginPrefs", 0)
            .getString("user_name", null);

        if (userName == null) {
            Toast.makeText(getContext(), "로그인이 필요합니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        // 서버 API 엔드포인트로 운동 데이터 요청
        String url = Constants.API_WORKOUTS + "?userName=" + userName;
        Log.d(TAG, "Loading workout data from URL: " + url);

        Request request = new Request.Builder()
            .url(url)
            .header("Accept", "application/json")
            .get()
            .build();

        // 비동기로 서버에 요청 전송
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // 네트워크 오류 발생 시 처리
                Log.e(TAG, "Failed to load workout data", e);
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        Toast.makeText(getContext(), 
                            "운동 데이터 로드 실패: " + e.getMessage(), 
                            Toast.LENGTH_SHORT).show();
                    });
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (getActivity() == null) return;

                String responseBody = response.body().string();
                Log.d(TAG, "Server response code: " + response.code());
                Log.d(TAG, "Server response body: " + responseBody);

                getActivity().runOnUiThread(() -> {
                    if (response.isSuccessful()) {
                        try {
                            // 서버에서 빈 배열이 반환된 경우 처리
                            if (responseBody.trim().equals("[]")) {
                                Log.d(TAG, "No workout data available");
                                displayWorkoutData(new ArrayList<>());
                                return;
                            }

                            // JSON 응답을 WorkoutData 객체 리스트로 파싱
                            Type listType = new TypeToken<List<WorkoutData>>(){}.getType();
                            List<WorkoutData> workouts = gson.fromJson(responseBody, listType);
                            
                            if (workouts != null) {
                                Log.d(TAG, "Successfully parsed " + workouts.size() + " workout records");
                                // 파싱된 데이터를 화면에 표시
                                for (WorkoutData workout : workouts) {
                                    Log.d(TAG, "Workout: " + workout.getExerciseType() + 
                                          ", Duration: " + workout.getDuration() + 
                                          ", Date: " + workout.getWorkoutDate());
                                }
                                displayWorkoutData(workouts);
                            } else {
                                Log.e(TAG, "Failed to parse workout data: null workouts");
                                Toast.makeText(getContext(), 
                                    "운동 데이터 파싱 실패", 
                                    Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            // JSON 파싱 오류 처리
                            Log.e(TAG, "Error parsing workout data: " + e.getMessage(), e);
                            Log.e(TAG, "Response body that caused the error: " + responseBody);
                            Toast.makeText(getContext(), 
                                "운동 데이터 파싱 실패: " + e.getMessage(), 
                                Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        // 서버 오류 처리
                        Log.e(TAG, "Server error: " + response.code() + " - " + responseBody);
                        Toast.makeText(getContext(), 
                            "서버 오류: " + response.code(), 
                            Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    /**
     * 운동 데이터를 테이블과 차트로 표시
     * @param workouts 표시할 운동 데이터 리스트
     */
    private void displayWorkoutData(List<WorkoutData> workouts) {
        // 운동 데이터를 날짜순으로 정렬 (최신순)
        Collections.sort(workouts, Comparator.comparing(WorkoutData::getWorkoutDate));

        // 막대 그래프 설정
        setupWorkoutChart(workouts);

        // 테이블 헤더 생성
        TableRow headerRow = new TableRow(getContext());
        addHeaderCell(headerRow, "날짜");
        addHeaderCell(headerRow, "운동 종류");
        addHeaderCell(headerRow, "루틴");
        addHeaderCell(headerRow, "운동 번호");
        addHeaderCell(headerRow, "시간");
        addHeaderCell(headerRow, "완료");
        tableLayout.addView(headerRow);

        if (workouts == null || workouts.isEmpty()) {
            // 운동 데이터가 없을 때 안내 메시지 표시
            TableRow emptyRow = new TableRow(getContext());
            TextView emptyText = new TextView(getContext());
            emptyText.setText("운동 데이터가 없습니다.");
            emptyText.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            emptyText.setPadding(10, 20, 10, 20);
            
            TableRow.LayoutParams params = new TableRow.LayoutParams();
            params.span = 6; // 6개 컬럼을 모두 차지
            emptyText.setLayoutParams(params);
            
            emptyRow.addView(emptyText);
            tableLayout.addView(emptyRow);
            return;
        }

        // 각 운동 기록을 테이블 행으로 추가
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        for (WorkoutData workout : workouts) {
            TableRow row = new TableRow(getContext());
            
            // 날짜 표시
            addCell(row, dateFormat.format(workout.getWorkoutDate()));
            
            // 운동 종류 표시
            addCell(row, workout.getExerciseType());
            
            // 루틴 정보 표시 (루틴명과 진행률)
            String routineInfo = workout.getRoutineName() != null ? 
                workout.getRoutineName() + " (" + workout.getExerciseNumber() + "/" + workout.getTotalExercises() + ")" :
                "-";
            addCell(row, routineInfo);
            
            // 운동 번호 표시
            addCell(row, workout.getRoutineName() != null ? 
                workout.getExerciseNumber() + "/" + workout.getTotalExercises() : "-");
            
            // 운동 시간을 밀리초에서 분:초 형식으로 변환하여 표시
            double totalMilliseconds = workout.getDuration();
            int minutes = (int) (totalMilliseconds / 60000);
            int seconds = (int) ((totalMilliseconds % 60000) / 1000);
            addCell(row, String.format("%02d:%02d", minutes, seconds));
            
            // 완료 여부 표시
            addCell(row, workout.isCompleted() ? "완료" : "중단");
            
            tableLayout.addView(row);
        }
    }

    private void setupWorkoutChart(List<WorkoutData> workouts) {
        if (workouts == null || workouts.isEmpty()) {
            workoutChart.setNoDataText("운동 데이터가 없습니다.");
            return;
        }

        // 날짜별로 운동 시간을 합산
        Map<String, Float> dailyWorkouts = new HashMap<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        
        for (WorkoutData workout : workouts) {
            String date = dateFormat.format(workout.getWorkoutDate());
            float duration = (float) (workout.getDuration() / 60000.0); // 밀리초를 분으로 변환
            dailyWorkouts.merge(date, duration, Float::sum);
        }

        // 정렬된 날짜 리스트 생성
        List<String> sortedDates = new ArrayList<>(dailyWorkouts.keySet());
        Collections.sort(sortedDates);

        List<BarEntry> entries = new ArrayList<>();
        final List<String> dateLabels = new ArrayList<>();
        SimpleDateFormat displayFormat = new SimpleDateFormat("MM/dd", Locale.getDefault());
        
        for (int i = 0; i < sortedDates.size(); i++) {
            String date = sortedDates.get(i);
            entries.add(new BarEntry(i, dailyWorkouts.get(date)));  // x 값을 i로 변경
            try {
                Date workoutDate = dateFormat.parse(date);
                dateLabels.add(displayFormat.format(workoutDate));
            } catch (Exception e) {
                dateLabels.add(date);
            }
        }

        BarDataSet dataSet = new BarDataSet(entries, "운동 시간 (분)");
        dataSet.setColor(Color.parseColor("#1976D2"));
        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setValueTextSize(12f);

        BarData barData = new BarData(dataSet);
        barData.setBarWidth(0.15f);
        workoutChart.setData(barData);

        // X축 설정
        XAxis xAxis = workoutChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setDrawGridLines(false);
        xAxis.setTextSize(12f);
        xAxis.setLabelCount(0);  // X축 레이블 숨기기
        xAxis.setDrawLabels(false);  // X축 레이블 그리지 않기
        xAxis.setAxisMinimum(-0.2f);
        xAxis.setAxisMaximum(dateLabels.size() - 0.6f);
        
        // Y축 설정
        workoutChart.getAxisRight().setEnabled(false);
        workoutChart.getAxisLeft().setDrawGridLines(true);
        workoutChart.getAxisLeft().setAxisMinimum(0f);
        
        // 차트 설정
        workoutChart.getDescription().setEnabled(false);
        workoutChart.setDrawGridBackground(false);
        workoutChart.setDrawBorders(false);
        workoutChart.setTouchEnabled(true);
        workoutChart.setDragEnabled(true);
        workoutChart.setScaleEnabled(true);
        workoutChart.setPinchZoom(true);
        workoutChart.setFitBars(true);
        
        // 커스텀 마커 설정
        workoutChart.setDrawMarkers(true);
        workoutChart.setMarker(new CustomMarkerView(getContext(), dateLabels));
        
        // 차트 여백 설정
        workoutChart.setExtraBottomOffset(30f);  // 하단 여백 증가
        
        // 차트 갱신
        workoutChart.invalidate();
    }

    private void addHeaderCell(TableRow row, String text) {
        TextView textView = new TextView(getContext());
        textView.setText(text);
        textView.setPadding(10, 20, 10, 20);
        textView.setTextColor(Color.BLACK);
        textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        textView.setTextSize(14);
        textView.setTypeface(null, android.graphics.Typeface.BOLD);
        row.addView(textView);
    }

    private void addCell(TableRow row, String text) {
        TextView textView = new TextView(getContext());
        textView.setText(text);
        textView.setPadding(10, 20, 10, 20);
        textView.setTextColor(Color.BLACK);
        textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        textView.setTextSize(14);
        row.addView(textView);
    }
} 