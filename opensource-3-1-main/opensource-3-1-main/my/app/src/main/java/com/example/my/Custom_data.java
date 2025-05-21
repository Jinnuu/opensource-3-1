package com.example.my;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class Custom_data extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_custom_data, container, false);

        // btnStart 버튼 클릭 시 Activity로 화면 전환
        Button btnGoToExerciseData = view.findViewById(R.id.btnStart);
        btnGoToExerciseData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // ExerciseDataActivity로 이동
                Intent intent = new Intent(getActivity(), exercise_Data.class);
                startActivity(intent);
            }
        });

        return view;
    }
}
