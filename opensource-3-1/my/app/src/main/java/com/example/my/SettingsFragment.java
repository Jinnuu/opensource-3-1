package com.example.my;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.android.material.button.MaterialButton;

public class SettingsFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // "재활 부위 선택" 버튼 클릭 시 Body_SettingActivity로 이동
        MaterialButton btnMyProfile = view.findViewById(R.id.btnMyProfile);
        btnMyProfile.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), Body_Setting.class);
            startActivity(intent);
        });

        // "가이드 보기" 버튼 클릭 시 GuideActivity로 이동
        MaterialButton btnAppGuide = view.findViewById(R.id.btnAppGuide);
        btnAppGuide.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), Guide_Activity.class);
            startActivity(intent);
        });
    }
}
