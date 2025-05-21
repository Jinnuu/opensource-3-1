package com.example.my;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

public class Settings extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public Settings() {
        // Required empty public constructor
    }

    public static Settings newInstance(String param1, String param2) {
        Settings fragment = new Settings();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        // btnMyProfile 버튼 클릭 시 Body_Settings로 화면 전환
        Button btnMyProfile = view.findViewById(R.id.btnMyProfile);
        if (btnMyProfile == null) {
            Log.e("Settings", "btnMyProfile is NULL! XML에서 id를 확인하세요.");
        } else {
            Log.d("Settings", "btnMyProfile is NOT null.");
            btnMyProfile.setOnClickListener(v -> {
                Log.d("Settings", "btnMyProfile 클릭됨");
                Toast.makeText(getContext(), "MyProfile 버튼 클릭됨", Toast.LENGTH_SHORT).show();

                requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, new Body_Settings())
                        .addToBackStack(null)
                        .commit();
            });
        }

        return view;
    }
}
