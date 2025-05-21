package com.example.my;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.gson.Gson;
import okhttp3.*;
import java.io.IOException;

public class SettingsFragment extends Fragment {
    private static final String PREF_NAME = "LoginPrefs";
    private static final String KEY_USER_NAME = "user_name";
    private final OkHttpClient client = new OkHttpClient();
    private final Gson gson = new Gson();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        Button btnChangePassword = view.findViewById(R.id.btnChangePassword);
        Button btnLogout = view.findViewById(R.id.btnLogout);

        btnChangePassword.setOnClickListener(v -> showChangePasswordDialog());
        btnLogout.setOnClickListener(v -> logout());

        return view;
    }

    private void showChangePasswordDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_change_password, null);
        EditText etCurrentPassword = dialogView.findViewById(R.id.etCurrentPassword);
        EditText etNewPassword = dialogView.findViewById(R.id.etNewPassword);
        EditText etConfirmPassword = dialogView.findViewById(R.id.etConfirmPassword);

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext())
            .setTitle("비밀번호 변경")
            .setView(dialogView)
            .setPositiveButton("변경", (dialog, which) -> {
                String currentPassword = etCurrentPassword.getText().toString();
                String newPassword = etNewPassword.getText().toString();
                String confirmPassword = etConfirmPassword.getText().toString();

                if (currentPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
                    Toast.makeText(requireContext(), "모든 필드를 입력해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!newPassword.equals(confirmPassword)) {
                    Toast.makeText(requireContext(), "새 비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                    return;
                }

                changePassword(currentPassword, newPassword);
            })
            .setNegativeButton("취소", null);

        builder.create().show();
    }

    private void changePassword(String currentPassword, String newPassword) {
        SharedPreferences prefs = requireActivity().getSharedPreferences(PREF_NAME, 0);
        String userName = prefs.getString(KEY_USER_NAME, null);

        if (userName == null) {
            Toast.makeText(requireContext(), "로그인이 필요합니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        String json = gson.toJson(new PasswordChangeRequest(userName, currentPassword, newPassword));
        RequestBody body = RequestBody.create(
            MediaType.parse("application/json"), json);

        Request request = new Request.Builder()
            .url(Constants.API_USERS + "/change-password")
            .post(body)
            .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        Toast.makeText(requireContext(), 
                            "비밀번호 변경 실패: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        if (response.isSuccessful()) {
                            Toast.makeText(requireContext(), 
                                "비밀번호가 변경되었습니다.", Toast.LENGTH_SHORT).show();
                        } else {
                            try {
                                ResponseBody responseBody = response.body();
                                String errorMessage = responseBody != null ? 
                                    responseBody.string() : "알 수 없는 오류";
                                Toast.makeText(requireContext(), 
                                    "비밀번호 변경 실패: " + errorMessage, 
                                    Toast.LENGTH_SHORT).show();
                            } catch (IOException e) {
                                Toast.makeText(requireContext(), 
                                    "비밀번호 변경 실패: 응답 처리 중 오류 발생", 
                                    Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }

    private void logout() {
        new MaterialAlertDialogBuilder(requireContext())
            .setTitle("로그아웃")
            .setMessage("정말 로그아웃 하시겠습니까?")
            .setPositiveButton("확인", (dialog, which) -> {
                SharedPreferences prefs = requireActivity().getSharedPreferences(PREF_NAME, 0);
                SharedPreferences.Editor editor = prefs.edit();
                editor.clear();
                editor.apply();

                Intent intent = new Intent(getActivity(), LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                requireActivity().finish();
            })
            .setNegativeButton("취소", null)
            .show();
    }

    private static class PasswordChangeRequest {
        private String userName;
        private String currentPassword;
        private String newPassword;

        public PasswordChangeRequest(String userName, String currentPassword, String newPassword) {
            this.userName = userName;
            this.currentPassword = currentPassword;
            this.newPassword = newPassword;
        }
    }
} 