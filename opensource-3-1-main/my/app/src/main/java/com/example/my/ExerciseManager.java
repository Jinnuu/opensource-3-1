package com.example.my;

import android.content.Context;
import android.content.SharedPreferences;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ExerciseManager {
    private static final String PREF_NAME = "ExercisePrefs";
    private static final String KEY_LAST_EXERCISE_DATE = "last_exercise_date";
    private static final String KEY_EXERCISE_DAY_COUNT = "exercise_day_count";

    public static int getExerciseDayCount(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getInt(KEY_EXERCISE_DAY_COUNT, 0);
    }

    public static void updateExerciseDayCount(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String lastDate = prefs.getString(KEY_LAST_EXERCISE_DATE, "");
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        // 오늘 날짜와 마지막 운동 날짜가 다르면 일수 증가
        if (!lastDate.equals(today)) {
            int currentCount = prefs.getInt(KEY_EXERCISE_DAY_COUNT, 0);
            prefs.edit()
                .putInt(KEY_EXERCISE_DAY_COUNT, currentCount + 1)
                .putString(KEY_LAST_EXERCISE_DATE, today)
                .apply();
        }
    }

    public static void resetExerciseDayCount(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit()
            .putInt(KEY_EXERCISE_DAY_COUNT, 0)
            .putString(KEY_LAST_EXERCISE_DATE, "")
            .apply();
    }
} 