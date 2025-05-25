package com.example.my;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import androidx.cardview.widget.CardView;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class ExerciseFragment extends Fragment {
    private TextView tvDayCount;
    private CardView cardWristExercise, cardBackExercise, cardNeckExercise, cardCustomExercise;
    private static final String PREF_NAME = "ExercisePrefs";
    private static final String KEY_INSTALL_DATE = "install_date";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_exercise, container, false);

        // View 초기화
        tvDayCount = view.findViewById(R.id.tvDayCount);
        cardWristExercise = view.findViewById(R.id.cardWristExercise);
        cardBackExercise = view.findViewById(R.id.cardBackExercise);
        cardNeckExercise = view.findViewById(R.id.cardNeckExercise);
        cardCustomExercise = view.findViewById(R.id.cardCustomExercise);

        // 회원가입 일수 로드
        loadMembershipDays();

        // 클릭 리스너 설정
        setupClickListeners();

        return view;
    }

    private void loadMembershipDays() {
        SharedPreferences prefs = getActivity().getSharedPreferences(PREF_NAME, 0);
        String installDate = prefs.getString(KEY_INSTALL_DATE, null);

        if (installDate == null) {
            // 처음 실행하는 경우 현재 날짜 저장
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
            e.printStackTrace();
            tvDayCount.setText("- 일차");
        }
    }

    private void setupClickListeners() {
        cardWristExercise.setOnClickListener(v -> showTimePickerDialog(WristExerciseActivity.class));
        cardBackExercise.setOnClickListener(v -> showTimePickerDialog(BackExerciseActivity.class));
        cardNeckExercise.setOnClickListener(v -> showTimePickerDialog(NeckExerciseActivity.class));
        cardCustomExercise.setOnClickListener(v -> showTimePickerDialog(CustomExerciseActivity.class));
    }

    private void showTimePickerDialog(Class<?> activityClass) {
        com.example.my.TimePickerDialog dialog = new com.example.my.TimePickerDialog(getActivity(), (minutes, seconds) -> {
            Intent intent = new Intent(getActivity(), activityClass);
            intent.putExtra("minutes", minutes);
            intent.putExtra("seconds", seconds);
            startActivity(intent);
        });
        dialog.show();
    }
}