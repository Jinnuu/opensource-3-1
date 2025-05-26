package com.example.my;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.Button;
import android.widget.NumberPicker;
import androidx.annotation.NonNull;

public class ExerciseTimeDialog extends Dialog {
    private OnTimeSelectedListener listener;
    private NumberPicker npMinutes;
    private NumberPicker npSeconds;

    public interface OnTimeSelectedListener {
        void onTimeSelected(int minutes, int seconds);
    }

    public ExerciseTimeDialog(@NonNull Context context, OnTimeSelectedListener listener) {
        super(context);
        this.listener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_exercise_time);

        npMinutes = findViewById(R.id.npMinutes);
        npSeconds = findViewById(R.id.npSeconds);
        Button btnCancel = findViewById(R.id.btnCancel);
        Button btnConfirm = findViewById(R.id.btnConfirm);

        // 분 설정
        npMinutes.setMinValue(0);
        npMinutes.setMaxValue(60);
        npMinutes.setValue(3); // 기본값 3분

        // 초 설정
        npSeconds.setMinValue(0);
        npSeconds.setMaxValue(59);
        npSeconds.setValue(0); // 기본값 0초

        btnCancel.setOnClickListener(v -> dismiss());

        btnConfirm.setOnClickListener(v -> {
            if (listener != null) {
                listener.onTimeSelected(npMinutes.getValue(), npSeconds.getValue());
            }
            dismiss();
        });
    }
} 