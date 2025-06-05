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
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.text.SimpleDateFormat;
import java.util.*;
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
    private List<String> selectedParts = new ArrayList<>();

    // 부위별 운동 매핑
    private final Map<String, Map<Integer, List<ExerciseInfo>>> partExercises = new HashMap<String, Map<Integer, List<ExerciseInfo>>>() {{
        put("목", new HashMap<Integer, List<ExerciseInfo>>() {{
            put(1, Arrays.asList(
                    new ExerciseInfo("목 앞 근육 스트레칭", "one_one.mp4"),
                    new ExerciseInfo("목 좌우 근육 스트레칭", "one_two.mp4")
            ));
            put(2, Arrays.asList(
                    new ExerciseInfo("목 근력 운동", "two_fifteen.mp4"),
                    new ExerciseInfo("목 스트레칭", "two_sixteen.mp4")
            ));
            put(3, Arrays.asList(
                    new ExerciseInfo("목 회전 운동", "three_six.mp4")
            ));
        }});
        put("어깨", new HashMap<Integer, List<ExerciseInfo>>() {{
            put(1, Arrays.asList(
                    new ExerciseInfo("어깨 들어올리기", "one_nine.mp4"),
                    new ExerciseInfo("어깨 스트레칭", "one_thirteen.mp4")
            ));
            put(2, Arrays.asList(
                    new ExerciseInfo("어깨 운동 1단계", "two_thirteen.mp4"),
                    new ExerciseInfo("어깨 운동 2단계", "two_fourteen.mp4")
            ));
            put(3, Arrays.asList(
                    new ExerciseInfo("어깨 회전 운동", "three_seven.mp4")
            ));
        }});
        put("팔", new HashMap<Integer, List<ExerciseInfo>>() {{
            put(1, Arrays.asList(
                    new ExerciseInfo("손목 및 팔꿈치 주변 근육 스트레칭", "one_eleven.mp4")
            ));
            put(2, Arrays.asList(
                    new ExerciseInfo("팔 근력 운동", "two_nine.mp4"),
                    new ExerciseInfo("팔 스트레칭", "two_seventeen.mp4")
            ));
            put(3, Arrays.asList(
                    new ExerciseInfo("팔 회전 운동", "three_eight.mp4")
            ));
        }});
        put("등", new HashMap<Integer, List<ExerciseInfo>>() {{
            put(1, Arrays.asList(
                    new ExerciseInfo("날개뼈 움직이기", "one_eight.mp4"),
                    new ExerciseInfo("날개뼈 모으기", "one_ten.mp4")
            ));
            put(2, Arrays.asList(
                    new ExerciseInfo("등 근력 운동", "two_ten.mp4"),
                    new ExerciseInfo("등 스트레칭", "two_eighteen.mp4")
            ));
            put(3, Arrays.asList(
                    new ExerciseInfo("등 회전 운동", "three_nine.mp4")
            ));
        }});
        put("다리", new HashMap<Integer, List<ExerciseInfo>>() {{
            put(1, Arrays.asList(
                    new ExerciseInfo("허벅지 및 종아리 근육 스트레칭", "one_twelve.mp4")
            ));
            put(2, Arrays.asList(
                    new ExerciseInfo("런지", "two_six.mp4"),
                    new ExerciseInfo("좌우런지", "two_seven.mp4"),
                    new ExerciseInfo("발전된 런지", "two_eight.mp4"),
                    new ExerciseInfo("앉았다 일어서기", "two_eleven.mp4"),
                    new ExerciseInfo("발전된 앉았다 일어서기", "two_twelve.mp4")
            ));
            put(3, Arrays.asList(
                    new ExerciseInfo("움직이는 런지", "three_five.mp4"),
                    new ExerciseInfo("다리 회전 운동", "three_ten.mp4")
            ));
        }});
    }};

    // 전신 운동 매핑
    private final Map<Integer, List<ExerciseInfo>> fullBodyExercises = new HashMap<Integer, List<ExerciseInfo>>() {{
        put(1, Arrays.asList(
                new ExerciseInfo("몸통 앞쪽 근육 스트레칭", "one_three.mp4"),
                new ExerciseInfo("몸통 옆쪽 근육 스트레칭", "one_four.mp4"),
                new ExerciseInfo("몸통회전 근육 스트레칭", "one_five.mp4"),
                new ExerciseInfo("몸통 스트레칭 1단계", "one_six.mp4"),
                new ExerciseInfo("몸통 스트레칭 2단계", "one_seven.mp4")
        ));
        put(2, Arrays.asList(
                new ExerciseInfo("엉덩이 들기", "two_one.mp4"),
                new ExerciseInfo("엎드려 누운 상태로 다리 들기", "two_two.mp4"),
                new ExerciseInfo("엉덩이 옆 근육 운동", "two_three.mp4"),
                new ExerciseInfo("무릎 벌리기", "two_four.mp4"),
                new ExerciseInfo("무릎 펴기", "two_five.mp4")
        ));
        put(3, Arrays.asList(
                new ExerciseInfo("한발 서기", "three_one.mp4"),
                new ExerciseInfo("버드독 1단계", "three_two.mp4"),
                new ExerciseInfo("버드독 2단계", "three_three.mp4"),
                new ExerciseInfo("앉은 상태에서 제자리 걷기", "three_four.mp4")
        ));
    }};

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

            // 선택된 부위들 가져오기
            if (getArguments() != null && getArguments().containsKey("orderedParts")) {
                selectedParts = getArguments().getStringArrayList("orderedParts");
            } else {
                SharedPreferences prefs = requireContext().getSharedPreferences("LoginPrefs", 0);
                String selectedPartsJson = prefs.getString("selected_parts", "[]");
                Gson gson = new Gson();
                selectedParts = gson.fromJson(selectedPartsJson, new TypeToken<List<String>>(){}.getType());
            }

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
            // 모든 버튼의 클릭 리스너 초기화
            clearAllButtonListeners();

            // 단계별로 운동 설정
            for (int stage = 1; stage <= 3; stage++) {
                List<ExerciseInfo> stageExercises = new ArrayList<>();
                List<ExerciseInfo> otherExercises = new ArrayList<>();

                // 1. 선택된 부위의 운동을 해당 단계에 우선 배치
                if (selectedParts != null && !selectedParts.isEmpty()) {
                    // 전신이 선택된 경우
                    if (selectedParts.contains("전신")) {
                        if (fullBodyExercises.containsKey(stage)) {
                            stageExercises.addAll(fullBodyExercises.get(stage));
                        }
                    } else {
                        // 선택된 부위의 운동을 우선 배치
                        for (String part : selectedParts) {
                            Map<Integer, List<ExerciseInfo>> partStageExercises = partExercises.get(part);
                            if (partStageExercises != null && partStageExercises.containsKey(stage)) {
                                stageExercises.addAll(partStageExercises.get(stage));
                            }
                        }

                        // 선택되지 않은 부위의 운동을 별도 리스트에 추가
                        for (Map.Entry<String, Map<Integer, List<ExerciseInfo>>> entry : partExercises.entrySet()) {
                            if (!selectedParts.contains(entry.getKey())) {
                                Map<Integer, List<ExerciseInfo>> partStageExercises = entry.getValue();
                                if (partStageExercises != null && partStageExercises.containsKey(stage)) {
                                    otherExercises.addAll(partStageExercises.get(stage));
                                }
                            }
                        }

                        // 전신 운동을 별도 리스트에 추가
                        if (fullBodyExercises.containsKey(stage)) {
                            otherExercises.addAll(fullBodyExercises.get(stage));
                        }

                        // 선택되지 않은 운동들을 마지막에 추가
                        stageExercises.addAll(otherExercises);
                    }
                } else {
                    // 선택된 부위가 없는 경우 모든 운동 추가
                    for (Map<Integer, List<ExerciseInfo>> partStageExercises : partExercises.values()) {
                        if (partStageExercises.containsKey(stage)) {
                            stageExercises.addAll(partStageExercises.get(stage));
                        }
                    }
                    if (fullBodyExercises.containsKey(stage)) {
                        stageExercises.addAll(fullBodyExercises.get(stage));
                    }
                }

                // 운동 버튼 생성 및 리스너 설정
                for (ExerciseInfo exercise : stageExercises) {
                    View button = findButtonForExercise(exercise.name);
                    if (button != null) {
                        button.setOnClickListener(v -> showExerciseGuide(exercise.name, exercise.videoFileName));
                    }
                }
            }

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

    private void clearAllButtonListeners() {
        // 1단계 운동 버튼
        binding.btnOneOne.setOnClickListener(null);
        binding.btnOneTwo.setOnClickListener(null);
        binding.btnOneThree.setOnClickListener(null);
        binding.btnOneFour.setOnClickListener(null);
        binding.btnOneFive.setOnClickListener(null);
        binding.btnOneSix.setOnClickListener(null);
        binding.btnOneSeven.setOnClickListener(null);
        binding.btnOneEight.setOnClickListener(null);
        binding.btnOneNine.setOnClickListener(null);
        binding.btnOneTen.setOnClickListener(null);
        binding.btnOneEleven.setOnClickListener(null);
        binding.btnOneTwelve.setOnClickListener(null);

        // 2단계 운동 버튼
        binding.btnTwoOne.setOnClickListener(null);
        binding.btnTwoTwo.setOnClickListener(null);
        binding.btnTwoThree.setOnClickListener(null);
        binding.btnTwoFour.setOnClickListener(null);
        binding.btnTwoFive.setOnClickListener(null);
        binding.btnTwoSix.setOnClickListener(null);
        binding.btnTwoSeven.setOnClickListener(null);
        binding.btnTwoEight.setOnClickListener(null);
        binding.btnTwoNine.setOnClickListener(null);
        binding.btnTwoTen.setOnClickListener(null);
        binding.btnTwoEleven.setOnClickListener(null);
        binding.btnTwoTwelve.setOnClickListener(null);
        binding.btnTwoThirteen.setOnClickListener(null);
        binding.btnTwoFourteen.setOnClickListener(null);

        // 3단계 운동 버튼
        binding.btnThreeOne.setOnClickListener(null);
        binding.btnThreeTwo.setOnClickListener(null);
        binding.btnThreeThree.setOnClickListener(null);
        binding.btnThreeFour.setOnClickListener(null);
        binding.btnThreeFive.setOnClickListener(null);
    }

    private View findButtonForExercise(String exerciseName) {
        // 운동 이름에 해당하는 버튼 찾기
        switch (exerciseName) {
            case "목 앞 근육 스트레칭": return binding.btnOneOne;
            case "목 좌우 근육 스트레칭": return binding.btnOneTwo;
            case "몸통 앞쪽 근육 스트레칭": return binding.btnOneThree;
            case "몸통 옆쪽 근육 스트레칭": return binding.btnOneFour;
            case "몸통회전 근육 스트레칭": return binding.btnOneFive;
            case "몸통 스트레칭 1단계": return binding.btnOneSix;
            case "몸통 스트레칭 2단계": return binding.btnOneSeven;
            case "날개뼈 움직이기": return binding.btnOneEight;
            case "어깨 들어올리기": return binding.btnOneNine;
            case "날개뼈 모으기": return binding.btnOneTen;
            case "손목 및 팔꿈치 주변 근육 스트레칭": return binding.btnOneEleven;
            case "허벅지 및 종아리 근육 스트레칭": return binding.btnOneTwelve;
            case "엉덩이 들기": return binding.btnTwoOne;
            case "엎드려 누운 상태로 다리 들기": return binding.btnTwoTwo;
            case "엉덩이 옆 근육 운동": return binding.btnTwoThree;
            case "무릎 벌리기": return binding.btnTwoFour;
            case "무릎 펴기": return binding.btnTwoFive;
            case "런지": return binding.btnTwoSix;
            case "좌우런지": return binding.btnTwoSeven;
            case "발전된 런지": return binding.btnTwoEight;
            case "손목 및 팔꿈치 주변 근육": return binding.btnTwoNine;
            case "날개 뼈 모음 근육": return binding.btnTwoTen;
            case "앉았다 일어서기": return binding.btnTwoEleven;
            case "발전된 앉았다 일어서기": return binding.btnTwoTwelve;
            case "어깨 운동 1단계": return binding.btnTwoThirteen;
            case "어깨 운동 2단계": return binding.btnTwoFourteen;
            case "한발 서기": return binding.btnThreeOne;
            case "버드독 1단계": return binding.btnThreeTwo;
            case "버드독 2단계": return binding.btnThreeThree;
            case "앉은 상태에서 제자리 걷기": return binding.btnThreeFour;
            case "움직이는 런지": return binding.btnThreeFive;
            default: return null;
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

    // 운동 정보를 담는 내부 클래스
    private static class ExerciseInfo {
        final String name;
        final String videoFileName;

        ExerciseInfo(String name, String videoFileName) {
            this.name = name;
            this.videoFileName = videoFileName;
        }
    }
} 