package com.example.my;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.button.MaterialButton;
import com.google.gson.Gson;
import okhttp3.*;
import java.io.IOException;

/**
 * 설정 프래그먼트 클래스
 * 앱의 다양한 설정을 관리하는 화면
 * 사용자 프로필, 글자 크기, 비밀번호 변경, 로그아웃 등의 기능 제공
 * 어르신들의 사용 편의성을 고려한 접근성 설정 포함
 */
public class SettingsFragment extends Fragment {
    // SharedPreferences 관련 상수
    private static final String PREF_NAME = "LoginPrefs";
    private static final String KEY_USER_NAME = "user_name";
    private static final String KEY_TEXT_SIZE = "text_size";
    
    // 네트워크 통신을 위한 객체들
    private final OkHttpClient client = new OkHttpClient();
    private final Gson gson = new Gson();
    
    // 로컬 데이터 저장소
    private SharedPreferences prefs;
    
    // 현재 로그인한 사용자 이름
    private String userName;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        // 재활 부위 선택 버튼 - 사용자의 운동 부위 설정
        MaterialButton btnMyProfile = view.findViewById(R.id.btnMyProfile);
        btnMyProfile.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), Body_Setting.class);
            startActivity(intent);
        });

        // 가이드 보기 버튼 - 앱 사용법 안내
        MaterialButton btnAppGuide = view.findViewById(R.id.btnAppGuide);
        btnAppGuide.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), GuideActivity.class);
            startActivity(intent);
        });

        // 비밀번호 변경 기능
        TextView tvChangePassword = view.findViewById(R.id.tvChangePassword);
        tvChangePassword.setOnClickListener(v -> showChangePasswordDialog());

        // 로그아웃 기능
        TextView tvLogout = view.findViewById(R.id.tvLogout);
        tvLogout.setOnClickListener(v -> logout());

        // 글자 크기 설정 - 어르신들의 가독성을 위한 접근성 기능
        TextView tvTextSize = view.findViewById(R.id.tvTextSize);
        tvTextSize.setOnClickListener(v -> showTextSizeDialog());

        // 사용자 ID 표시 및 로그인 상태 확인
        prefs = requireActivity().getSharedPreferences(PREF_NAME, 0);
        userName = prefs.getString(KEY_USER_NAME, "");
        TextView tvUserId = view.findViewById(R.id.tvUserId);
        if (!userName.isEmpty()) {
            tvUserId.setText(userName);
        } else {
            // 로그인되지 않은 경우 로그인 화면으로 이동
            Toast.makeText(requireContext(), "로그인이 필요합니다.", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
            requireActivity().finish();
        }

        return view;
    }

    /**
     * 글자 크기 설정 다이얼로그 표시
     * 어르신들의 가독성을 위해 앱 전체의 글자 크기를 조정할 수 있는 기능
     */
    private void showTextSizeDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_text_size, null);
        SeekBar textSizeSeekBar = dialogView.findViewById(R.id.textSizeSeekBar);
        TextView previewText = dialogView.findViewById(R.id.previewText);

        // SeekBar 최대값 설정 (0: 작게, 1: 보통, 2: 크게)
        textSizeSeekBar.setMax(2);

        // 저장된 설정값 불러오기 (기본값: 0 = 작게)
        int savedTextSize = prefs.getInt(KEY_TEXT_SIZE, 0);
        textSizeSeekBar.setProgress(savedTextSize);
        updatePreviewTextSize(previewText, savedTextSize);

        // SeekBar 리스너 설정 - 실시간 미리보기 제공
        textSizeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                updatePreviewTextSize(previewText, progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        // 다이얼로그 생성 및 표시
        new MaterialAlertDialogBuilder(requireContext())
            .setTitle("글자 크기 설정")
            .setView(dialogView)
            .setPositiveButton("적용", (dialog, which) -> {
                int selectedSize = textSizeSeekBar.getProgress();
                saveTextSize(selectedSize);
                applyTextSizeToApp(selectedSize);
                Toast.makeText(getContext(), "글자 크기가 적용되었습니다.", Toast.LENGTH_SHORT).show();
            })
            .setNegativeButton("취소", null)
            .show();
    }

    /**
     * 미리보기 텍스트의 크기를 업데이트
     * @param textView 미리보기용 텍스트뷰
     * @param size 선택된 크기 (0: 작게, 1: 보통, 2: 크게)
     */
    private void updatePreviewTextSize(TextView textView, int size) {
        float textSize;
        switch (size) {
            case 0: // 작게
                textSize = 12f;
                break;
            case 1: // 보통
                textSize = 14f;
                break;
            case 2: // 크게
                textSize = 18f;
                break;
            default:
                textSize = 14f;
        }
        textView.setTextSize(textSize);
    }

    /**
     * 선택된 글자 크기를 SharedPreferences에 저장
     * @param size 저장할 글자 크기 설정값
     */
    private void saveTextSize(int size) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(KEY_TEXT_SIZE, size);
        editor.apply();
    }

    /**
     * 앱 전체에 글자 크기 설정을 적용
     * @param size 적용할 글자 크기 (0: 작게, 1: 보통, 2: 크게)
     */
    private void applyTextSizeToApp(int size) {
        // 앱 전체에 글자 크기 적용
        float scale;
        switch (size) {
            case 0: // 작게
                scale = 0.8f;
                break;
            case 1: // 보통
                scale = 1.0f;
                break;
            case 2: // 크게
                scale = 1.6f;  // 어르신들의 가독성을 위해 더 크게 설정
                break;
            default:
                scale = 1.0f;
        }
        
        // 앱 전체에 스케일 적용하고 화면 새로고침
        requireActivity().getResources().getConfiguration().fontScale = scale;
        requireActivity().recreate();
    }

    /**
     * 비밀번호 변경 다이얼로그 표시
     * 현재 비밀번호 확인 후 새 비밀번호로 변경하는 기능
     */
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

                // 입력값 검증
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

    /**
     * 서버에 비밀번호 변경 요청 전송
     * @param currentPassword 현재 비밀번호
     * @param newPassword 새 비밀번호
     */
    private void changePassword(String currentPassword, String newPassword) {
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