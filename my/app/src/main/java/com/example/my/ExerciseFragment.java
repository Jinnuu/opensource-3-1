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
import com.example.my.databinding.FragmentExerciseBinding;

// 운동 프래그먼트 (운동 목록 화면)

public class ExerciseFragment extends Fragment {
    private FragmentExerciseBinding binding;
    private TextView tvMessage;
    private TextView tvDayCount;
    private CardView cardWristExercise, cardBackExercise, cardNeckExercise, cardCustomExercise,
                    cardShoulderExercise, cardArmExercise, cardChestExercise, cardAbsExercise,
                    cardHipExercise, cardLegExercise;
    public static final String PREF_NAME = "ExercisePrefs";
    private static final String KEY_INSTALL_DATE = "install_date";
    private String currentUserName;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentExerciseBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        
        try {
        // 현재 로그인한 사용자 이름 가져오기
        currentUserName = getArguments() != null ? getArguments().getString("userName") : null;
        
        // View 초기화
            tvMessage = binding.tvMessage;
            tvDayCount = binding.tvDayCount;

        // 회원가입 후 경과일수 가져오기
        if (currentUserName != null) {
            fetchRegistrationDate();
        }

            // 운동 버튼 클릭 리스너 설정
        setupClickListeners();

            // 운동 일수 표시
            updateExerciseDayCount();

        } catch (Exception e) {
            e.printStackTrace();
            if (getContext() != null) {
                Toast.makeText(getContext(), "운동 화면을 불러오는데 실패했습니다.", Toast.LENGTH_SHORT).show();
            }
        }

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
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
        try {
            // 1단계 운동
            binding.btnOneOne.setOnClickListener(v -> showExerciseGuide("목 앞 근육 스트레칭", "one_one.mp4"));
            binding.btnOneTwo.setOnClickListener(v -> showExerciseGuide("목 좌우 근육 스트레칭", "one_two.mp4"));
            binding.btnOneThree.setOnClickListener(v -> showExerciseGuide("몸통 앞쪽 근육 스트레칭", "one_three.mp4"));
            binding.btnOneFour.setOnClickListener(v -> showExerciseGuide("몸통 옆쪽 근육 스트레칭", "one_four.mp4"));
            binding.btnOneFive.setOnClickListener(v -> showExerciseGuide("몸통회전 근육 스트레칭", "one_five.mp4"));
            binding.btnOneSix.setOnClickListener(v -> showExerciseGuide("몸통 스트레칭 1단계", "one_six.mp4"));
            binding.btnOneSeven.setOnClickListener(v -> showExerciseGuide("몸통 스트레칭 2단계", "one_seven.mp4"));
            binding.btnOneEight.setOnClickListener(v -> showExerciseGuide("날개뼈 움직이기", "one_eight.mp4"));
            binding.btnOneNine.setOnClickListener(v -> showExerciseGuide("어깨 들어올리기", "one_nine.mp4"));
            binding.btnOneTen.setOnClickListener(v -> showExerciseGuide("날개뼈 모으기", "one_ten.mp4"));
            binding.btnOneEleven.setOnClickListener(v -> showExerciseGuide("손목 및 팔꿈치 주변 근육 스트레칭", "one_eleven.mp4"));
            binding.btnOneTwelve.setOnClickListener(v -> showExerciseGuide("허벅지 및 종아리 근육 스트레칭", "one_twelve.mp4"));

            // 2단계 운동
            binding.btnTwoOne.setOnClickListener(v -> showExerciseGuide("엉덩이 들기", "two_one.mp4"));
            binding.btnTwoTwo.setOnClickListener(v -> showExerciseGuide("엎드려 누운 상태로 다리 들기", "two_two.mp4"));
            binding.btnTwoThree.setOnClickListener(v -> showExerciseGuide("엉덩이 옆 근육 운동", "two_three.mp4"));
            binding.btnTwoFour.setOnClickListener(v -> showExerciseGuide("무릎 벌리기", "two_four.mp4"));
            binding.btnTwoFive.setOnClickListener(v -> showExerciseGuide("무릎 펴기", "two_five.mp4"));
            binding.btnTwoSix.setOnClickListener(v -> showExerciseGuide("런지", "two_six.mp4"));
            binding.btnTwoSeven.setOnClickListener(v -> showExerciseGuide("좌우런지", "two_seven.mp4"));
            binding.btnTwoEight.setOnClickListener(v -> showExerciseGuide("발전된 런지", "two_eight.mp4"));
            binding.btnTwoNine.setOnClickListener(v -> showExerciseGuide("손목 및 팔꿈치 주변 근육", "two_nine.mp4"));
            binding.btnTwoTen.setOnClickListener(v -> showExerciseGuide("날개 뼈 모음 근육", "two_ten.mp4"));
            binding.btnTwoEleven.setOnClickListener(v -> showExerciseGuide("앉았다 일어서기", "two_eleven.mp4"));
            binding.btnTwoTwelve.setOnClickListener(v -> showExerciseGuide("발전된 앉았다 일어서기", "two_twelve.mp4"));
            binding.btnTwoThirteen.setOnClickListener(v -> showExerciseGuide("어깨 운동 1단계", "two_thirteen.mp4"));
            binding.btnTwoFourteen.setOnClickListener(v -> showExerciseGuide("어깨 운동 2단계", "two_fourteen.mp4"));

            // 3단계 운동
            binding.btnThreeOne.setOnClickListener(v -> showExerciseGuide("한발 서기", "three_one.mp4"));
            binding.btnThreeTwo.setOnClickListener(v -> showExerciseGuide("버드독 1단계", "three_two.mp4"));
            binding.btnThreeThree.setOnClickListener(v -> showExerciseGuide("버드독 2단계", "three_three.mp4"));
            binding.btnThreeFour.setOnClickListener(v -> showExerciseGuide("앉은 상태에서 제자리 걷기", "three_four.mp4"));
            binding.btnThreeFive.setOnClickListener(v -> showExerciseGuide("움직이는 런지", "three_five.mp4"));

            // 나만의 운동 루틴 만들기
            binding.cardCustomExercise.setOnClickListener(v -> {
                if (getActivity() != null) {
            Intent intent = new Intent(getActivity(), CustomExerciseActivity.class);
            startActivity(intent);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            if (getContext() != null) {
                Toast.makeText(getContext(), "운동 버튼 설정에 실패했습니다.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void showExerciseGuide(String exerciseName, String videoFileName) {
        try {
            if (getActivity() != null) {
                // 운동 일수 업데이트
                ExerciseManager.updateExerciseDayCount(requireContext());

                Intent intent = new Intent(getActivity(), ExerciseGuideActivity.class);
                intent.putExtra("exercise_type", exerciseName);
                intent.putExtra("video_file_name", videoFileName);
                startActivity(intent);
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (getContext() != null) {
                Toast.makeText(getContext(), "운동 가이드를 불러오는데 실패했습니다.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void updateExerciseDayCount() {
        try {
            if (getContext() != null) {
                int dayCount = ExerciseManager.getExerciseDayCount(getContext());
                tvDayCount.setText(String.format("%d일째", dayCount));

                // 오늘의 총 운동 시간 가져오기
                SharedPreferences prefs = requireContext().getSharedPreferences("ExercisePrefs", 0);
                String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
                long totalDuration = prefs.getLong("exercise_duration_" + today, 0);
                long totalMinutes = totalDuration / 60000;  // 밀리초를 분으로 변환

                tvMessage.setText(String.format("오늘도 %d일째 운동을 시작해볼까요? (오늘 총 %d분)", dayCount, totalMinutes));
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (getContext() != null) {
                Toast.makeText(getContext(), "운동 일수를 불러오는데 실패했습니다.", Toast.LENGTH_SHORT).show();
            }
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