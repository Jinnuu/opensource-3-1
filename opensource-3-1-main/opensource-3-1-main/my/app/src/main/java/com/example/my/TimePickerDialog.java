package com.example.my;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import androidx.annotation.NonNull;

public class TimePickerDialog extends Dialog {
    private OnTimeSelectedListener listener;
    private NumberPicker minutePicker;
    private NumberPicker secondPicker;

    public interface OnTimeSelectedListener {
        void onTimeSelected(int minutes, int seconds);
    }

    public TimePickerDialog(@NonNull Context context, OnTimeSelectedListener listener) {
        super(context);
        this.listener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_time_picker);

        minutePicker = findViewById(R.id.minutePicker);
        secondPicker = findViewById(R.id.secondPicker);
        Button btnConfirm = findViewById(R.id.btnConfirm);
        Button btnCancel = findViewById(R.id.btnCancel);

        // 분 선택기 설정 (0-60분)
        minutePicker.setMinValue(0);
        minutePicker.setMaxValue(60);
        minutePicker.setValue(3); // 기본값 3분

        // 초 선택기 설정 (0-59초)
        secondPicker.setMinValue(0);
        secondPicker.setMaxValue(59);
        secondPicker.setValue(0);

        btnConfirm.setOnClickListener(v -> {
            if (listener != null) {
                listener.onTimeSelected(minutePicker.getValue(), secondPicker.getValue());
            }
            dismiss();
        });

        btnCancel.setOnClickListener(v -> dismiss());
    }
} 