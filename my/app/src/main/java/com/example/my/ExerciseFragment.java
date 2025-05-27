package com.example.my;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.cardview.widget.CardView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.Map;

public class ExerciseFragment extends Fragment {
    private TextView tvDayCount;
    private CardView cardWristExercise, cardBackExercise, cardNeckExercise, cardCustomExercise,
                    cardShoulderExercise, cardArmExercise, cardChestExercise, cardAbsExercise,
                    cardHipExercise, cardLegExercise;
    private static final String PREF_NAME = "ExercisePrefs";
    private static final String KEY_INSTALL_DATE = "install_date";
    private String currentUserName;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_exercise, container, false);
        
        // 현재 로그인한 사용자 이름 가져오기
        currentUserName = getArguments() != null ? getArguments().getString("userName") : null;
        
        // View 초기화
        tvDayCount = view.findViewById(R.id.tvDayCount);
        cardWristExercise = view.findViewById(R.id.cardWristExercise);
        cardBackExercise = view.findViewById(R.id.cardBackExercise);
        cardNeckExercise = view.findViewById(R.id.cardNeckExercise);
        cardCustomExercise = view.findViewById(R.id.cardCustomExercise);
        cardShoulderExercise = view.findViewById(R.id.cardShoulderExercise);
        cardArmExercise = view.findViewById(R.id.cardArmExercise);
        cardChestExercise = view.findViewById(R.id.cardChestExercise);
        cardAbsExercise = view.findViewById(R.id.cardAbsExercise);
        cardHipExercise = view.findViewById(R.id.cardHipExercise);
        cardLegExercise = view.findViewById(R.id.cardLegExercise);

        // 회원가입 후 경과일수 가져오기
        if (currentUserName != null) {
            fetchRegistrationDate();
        }

        // 클릭 리스너 설정
        setupClickListeners();

        return view;
    }

    private void fetchRegistrationDate() {
        ApiService.getClient().create(ApiInterface.class)
                .getRegistrationDate(currentUserName)
                .enqueue(new Callback<Map<String, String>>() {
                    @Override
                    public void onResponse(Call<Map<String, String>> call, Response<Map<String, String>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            String registrationDate = response.body().get("registrationDate");
                            if (registrationDate != null) {
                                // 현재 날짜와 회원가입 날짜의 차이를 계산
                                long days = calculateDaysSinceRegistration(registrationDate);
                                tvDayCount.setText(days + " 일차");
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<Map<String, String>> call, Throwable t) {
                        Toast.makeText(getContext(), "회원가입 정보를 가져오는데 실패했습니다.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private long calculateDaysSinceRegistration(String registrationDate) {
        try {
            java.time.LocalDate regDate = java.time.LocalDate.parse(registrationDate);
            java.time.LocalDate currentDate = java.time.LocalDate.now();
            return java.time.temporal.ChronoUnit.DAYS.between(regDate, currentDate);
        } catch (Exception e) {
            return 0;
        }
    }

    private void setupClickListeners() {
        cardWristExercise.setOnClickListener(v -> showExerciseGuide(
            "손목 운동", 
            "손목 통증 완화를 위한 운동",
            "m6nnpHeH86E"  // 손목 운동 영상 ID
        ));
        
        cardBackExercise.setOnClickListener(v -> showExerciseGuide(
            "허리 운동", 
            "허리 건강을 위한 스트레칭",
            "m6nnpHeH86E"  // 허리 운동 영상 ID
        ));
        
        cardNeckExercise.setOnClickListener(v -> showExerciseGuide(
            "목 운동", 
            "목 통증 완화를 위한 스트레칭",
            "m6nnpHeH86E"  // 목 운동 영상 ID
        ));

        cardShoulderExercise.setOnClickListener(v -> showExerciseGuide(
            "어깨 운동",
            "어깨 근력 강화를 위한 운동",
            "m6nnpHeH86E"  // 어깨 운동 영상 ID
        ));

        cardArmExercise.setOnClickListener(v -> showExerciseGuide(
            "팔 운동",
            "상완근과 전완근 강화 운동",
            "m6nnpHeH86E"  // 팔 운동 영상 ID
        ));

        cardChestExercise.setOnClickListener(v -> showExerciseGuide(
            "가슴 운동",
            "대흉근 강화를 위한 운동",
            "m6nnpHeH86E"  // 가슴 운동 영상 ID
        ));

        cardAbsExercise.setOnClickListener(v -> showExerciseGuide(
            "복근 운동",
            "복근 강화를 위한 코어 운동",
            "m6nnpHeH86E"  // 복근 운동 영상 ID
        ));

        cardHipExercise.setOnClickListener(v -> showExerciseGuide(
            "엉덩이 운동",
            "둔근 강화를 위한 운동",
            "m6nnpHeH86E"  // 엉덩이 운동 영상 ID
        ));

        cardLegExercise.setOnClickListener(v -> showExerciseGuide(
            "다리 운동",
            "하체 근력 강화를 위한 운동",
            "m6nnpHeH86E"  // 다리 운동 영상 ID
        ));
        
        cardCustomExercise.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), CustomExerciseActivity.class);
            startActivity(intent);
        });
    }

    private void showExerciseGuide(String exerciseType, String description, String videoId) {
        try {
            if (getActivity() == null) {
                Toast.makeText(getContext(), "활동이 유효하지 않습니다.", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent intent = new Intent(getActivity(), ExerciseGuideActivity.class);
            intent.putExtra("exercise_type", exerciseType);
            intent.putExtra("exercise_description", description);
            intent.putExtra("video_id", videoId);
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "운동 가이드를 불러오는데 실패했습니다.", Toast.LENGTH_SHORT).show();
        }
    }

    private void startExercise(String exerciseType) {
        if (currentUserName == null) {
            Toast.makeText(getContext(), "로그인이 필요합니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        // 운동 시작 로직
        Toast.makeText(getContext(), exerciseType + " 시작", Toast.LENGTH_SHORT).show();
    }
} 