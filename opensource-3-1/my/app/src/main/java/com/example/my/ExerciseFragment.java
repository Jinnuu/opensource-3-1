package com.example.my;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class ExerciseFragment extends Fragment {
    private static final String ARG_SELECTED_PARTS = "selected_parts";
    private static final String PREF_NAME = "ExercisePrefs";
    private static final String KEY_INSTALL_DATE = "install_date";

    // 전체 부위 리스트
    private final List<String> allParts = Arrays.asList(
            "등", "어깨", "팔", "가슴", "복근", "엉덩이", "다리", "전신"
    );

    private TextView tvDayCount;
    private LinearLayout cardContainer;
    private List<String> selectedParts = new ArrayList<>();

    public static ExerciseFragment newInstance(ArrayList<String> selectedParts) {
        ExerciseFragment fragment = new ExerciseFragment();
        Bundle args = new Bundle();
        args.putStringArrayList(ARG_SELECTED_PARTS, selectedParts);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            selectedParts = getArguments().getStringArrayList(ARG_SELECTED_PARTS);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_exercise, container, false);

        cardContainer = view.findViewById(R.id.card_container);
        tvDayCount = view.findViewById(R.id.tvDayCount);

        loadMembershipDays();
        createDynamicCards(inflater);

        return view;
    }

    private void createDynamicCards(LayoutInflater inflater) {
        cardContainer.removeAllViews(); // 기존 뷰 초기화

        // 1. 선택한 부위 먼저 추가
        for (String part : selectedParts) {
            if (allParts.contains(part)) {
                addExerciseCard(inflater, part);
            }
        }

        // 2. 선택되지 않은 나머지 부위 추가
        for (String part : allParts) {
            if (!selectedParts.contains(part)) {
                addExerciseCard(inflater, part);
            }
        }
    }

    private void addExerciseCard(LayoutInflater inflater, String part) {
        CardView card = (CardView) inflater.inflate(R.layout.exercise_card, cardContainer, false);

        TextView title = card.findViewById(R.id.exercise_title);
        TextView desc = card.findViewById(R.id.exercise_desc);

        // 부위별 정보 설정
        switch (part) {
            case "등":
                title.setText("등 운동");
                desc.setText("등 근육 강화 운동");
                card.setOnClickListener(v -> launchExercise(BackExerciseActivity.class));
                break;
            case "어깨":
                title.setText("어깨 운동");
                desc.setText("어깨 유연성 향상 운동");
                card.setOnClickListener(v -> launchExercise(ShoulderExerciseActivity.class));
                break;
            case "팔":
                title.setText("팔 운동");
                desc.setText("팔 근력 강화 운동");
                card.setOnClickListener(v -> launchExercise(ArmExerciseActivity.class));
                break;
            case "가슴":
                title.setText("가슴 운동");
                desc.setText("가슴 근육 발달 운동");
                card.setOnClickListener(v -> launchExercise(ChestExerciseActivity.class));
                break;
            case "복근":
                title.setText("복근 운동");
                desc.setText("복부 근육 단련 운동");
                card.setOnClickListener(v -> launchExercise(AbsExerciseActivity.class));
                break;
            case "엉덩이":
                title.setText("엉덩이 운동");
                desc.setText("힙 업 운동");
                card.setOnClickListener(v -> launchExercise(HipExerciseActivity.class));
                break;
            case "다리":
                title.setText("다리 운동");
                desc.setText("하체 근력 강화 운동");
                card.setOnClickListener(v -> launchExercise(LegExerciseActivity.class));
                break;
            case "전신":
                title.setText("전신 운동");
                desc.setText("종합 체력 향상 운동");
                card.setOnClickListener(v -> launchExercise(FullBodyExerciseActivity.class));
                break;
        }

        cardContainer.addView(card);
    }

    private void launchExercise(Class<?> activityClass) {
        com.example.my.TimePickerDialog dialog = new com.example.my.TimePickerDialog(
                getActivity(),
                (minutes, seconds) -> {
                    Intent intent = new Intent(getActivity(), activityClass);
                    intent.putExtra("minutes", minutes);
                    intent.putExtra("seconds", seconds);
                    startActivity(intent);
                }
        );
        dialog.show();
    }

    private void loadMembershipDays() {
        SharedPreferences prefs = getActivity().getSharedPreferences(PREF_NAME, 0);
        String installDate = prefs.getString(KEY_INSTALL_DATE, null);

        if (installDate == null) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            installDate = sdf.format(new Date());
            prefs.edit().putString(KEY_INSTALL_DATE, installDate).apply();
        }

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date regDate = sdf.parse(installDate);
            Date now = new Date();

            long diffInMillies = Math.abs(now.getTime() - regDate.getTime());
            long diff = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);

            tvDayCount.setText(String.format("%d 일차", diff + 1));
        } catch (Exception e) {
            tvDayCount.setText("- 일차");
        }
    }
}
