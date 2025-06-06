package com.example.my;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Button;
import android.widget.VideoView;
import android.media.MediaPlayer;
import android.util.Log;

// 운동 가이드 화면

public class exercise_guide extends Fragment {
    private static final String ARG_EXERCISE_TYPE = "exercise_type";
    private static final String ARG_EXERCISE_DESCRIPTION = "exercise_description";
    private static final String ARG_VIDEO_FILE_NAME = "video_file_name";
    
    private Handler handler;
    private Runnable pauseRunnable;
    private String exerciseType;
    private String exerciseDescription;
    private String videoFileName;
    private Button btnStartExercise;
    private TextView tvExerciseName;
    private TextView tvExerciseDescription;
    private VideoView videoView;
    private boolean isPaused = false;

    public exercise_guide() {
    }

    public static exercise_guide newInstance(String type, String description, String videoFileName) {
        exercise_guide fragment = new exercise_guide();
        Bundle args = new Bundle();
        args.putString(ARG_EXERCISE_TYPE, type);
        args.putString(ARG_EXERCISE_DESCRIPTION, description);
        args.putString(ARG_VIDEO_FILE_NAME, videoFileName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            exerciseType = getArguments().getString(ARG_EXERCISE_TYPE);
            exerciseDescription = getArguments().getString(ARG_EXERCISE_DESCRIPTION);
            videoFileName = getArguments().getString(ARG_VIDEO_FILE_NAME);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_exercise_guide, container, false);

        try {
            // View 초기화
            videoView = view.findViewById(R.id.videoView);
            tvExerciseName = view.findViewById(R.id.tvExerciseName);
            tvExerciseDescription = view.findViewById(R.id.tvExerciseDescription);
            btnStartExercise = view.findViewById(R.id.btnStartExercise);

            // 운동 정보 설정
            if (exerciseType != null) {
                tvExerciseName.setText(exerciseType);
                String description = getExerciseDescription(exerciseType);
                tvExerciseDescription.setText(description);
            }

            // 비디오 설정
            if (getActivity() != null && videoFileName != null) {
                int resId = getResources().getIdentifier(
                    videoFileName.replace(".mp4", ""),
                    "raw",
                    requireContext().getPackageName()
                );
                videoView.setVideoURI(Uri.parse("android.resource://" + requireContext().getPackageName() + "/" + resId));
                videoView.setOnPreparedListener(mp -> {
                    mp.setLooping(true);
                    videoView.start();
                });

                // 10초 후 일시정지
                handler = new Handler(Looper.getMainLooper());
                pauseRunnable = () -> {
                    videoView.pause();
                    isPaused = true;
                                    showCompletionToast();
                            };
                            handler.postDelayed(pauseRunnable, 10000);
            }

            // 시작하기 버튼 클릭 리스너
            btnStartExercise.setOnClickListener(v -> {
                if (getActivity() != null) {
                    ExerciseTimeDialog dialog = new ExerciseTimeDialog(getActivity(), (minutes, seconds) -> {
                        // 선택된 시간을 밀리초로 변환
                        long exerciseTime = (minutes * 60 + seconds) * 1000L;
                        
                        Intent intent = new Intent(getActivity(), ExerciseActivity.class);
                        intent.putExtra("exercise_type", exerciseType);
                        Log.d("exercise_guide", "Sending exercise_type: " + exerciseType);
                        intent.putExtra("exercise_time", exerciseTime);
                        startActivity(intent);
                    });
                    dialog.show();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            if (getContext() != null) {
                Toast.makeText(getContext(), "운동 가이드를 불러오는데 실패했습니다.", Toast.LENGTH_SHORT).show();
            }
        }

        return view;
    }

    private void showCompletionToast() {
        if (getContext() != null) {
            Toast.makeText(getContext(), "10초 재생 완료", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (videoView != null && videoView.isPlaying()) {
            videoView.pause();
            isPaused = true;
        }
        if (handler != null && pauseRunnable != null) {
            handler.removeCallbacks(pauseRunnable);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isPaused && videoView != null) {
            videoView.start();
            isPaused = false;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (handler != null && pauseRunnable != null) {
            handler.removeCallbacks(pauseRunnable);
        }
    }

    private String getExerciseDescription(String exerciseName) {
        switch (exerciseName) {
            case "목 앞 근육 스트레칭":
                return "기본 자세:\n- 바른 자세로 서서 목을 앞으로 내밀었다가 뒤로 젖힙니다.\n\n주의사항:\n- 갑작스러운 움직임을 피하고 천천히 진행합니다.\n- 통증이 있다면 즉시 중단하세요.";
            case "목 좌우 근육 스트레칭":
                return "기본 자세:\n- 바른 자세로 서서 목을 좌우로 부드럽게 기울입니다.\n\n주의사항:\n- 어깨를 들지 않고 자연스럽게 유지합니다.\n- 통증이 있는 방향은 피하세요.";
            case "몸통 앞쪽 근육 스트레칭":
                return "기본 자세:\n- 바른 자세로 서서 팔을 위로 올리고 몸을 뒤로 젖힙니다.\n\n주의사항:\n- 허리를 과도하게 젖히지 않습니다.\n- 천천히 진행하고 호흡을 자연스럽게 유지하세요.";
            case "몸통 옆쪽 근육 스트레칭":
                return "기본 자세:\n- 바른 자세로 서서 한쪽 팔을 위로 올리고 반대쪽으로 기울입니다.\n\n주의사항:\n- 무릎을 구부리지 않고 다리를 곧게 유지합니다.\n- 통증이 있다면 즉시 중단하세요.";
            case "몸통회전 근육 스트레칭":
                return "기본 자세:\n- 바른 자세로 서서 상체를 좌우로 부드럽게 회전합니다.\n\n주의사항:\n- 하체는 고정된 상태를 유지합니다.\n- 갑작스러운 회전을 피하세요.";
            case "몸통 스트레칭 1단계":
                return "기본 자세:\n- 엎드린 상태에서 팔꿈치로 상체를 지탱합니다.\n\n주의사항:\n- 허리를 과도하게 젖히지 않습니다.\n- 통증이 있다면 즉시 중단하세요.";
            case "몸통 스트레칭 2단계":
                return "기본 자세:\n- 엎드린 상태에서 손바닥으로 상체를 지탱합니다.\n\n주의사항:\n- 허리를 과도하게 젖히지 않습니다.\n- 통증이 있다면 즉시 중단하세요.";
            case "날개뼈 움직이기":
                return "기본 자세:\n- 바른 자세로 서서 날개뼈를 모았다 펼치는 동작을 반복합니다.\n\n주의사항:\n- 어깨를 들지 않고 자연스럽게 유지합니다.\n- 천천히 진행하세요.";
            case "어깨 들어올리기":
                return "기본 자세:\n- 바른 자세로 서서 어깨를 위로 올렸다 내립니다.\n\n주의사항:\n- 목을 과도하게 움직이지 않습니다.\n- 천천히 진행하세요.";
            case "날개뼈 모으기":
                return "기본 자세:\n- 바른 자세로 서서 양팔을 뒤로 모아 날개뼈를 모읍니다.\n\n주의사항:\n- 어깨를 들지 않고 자연스럽게 유지합니다.\n- 천천히 진행하세요.";
            case "손목 및 팔꿈치 주변 근육 스트레칭":
                return "기본 자세:\n- 한쪽 팔을 앞으로 뻗고 반대쪽 손으로 손목을 잡아 스트레칭합니다.\n\n주의사항:\n- 통증이 있는 방향은 피하세요.\n- 천천히 진행하세요.";
            case "허벅지 및 종아리 근육 스트레칭":
                return "기본 자세:\n- 한쪽 다리를 앞으로 내밀고 반대쪽 다리는 뒤로 뻗어 스트레칭합니다.\n\n주의사항:\n- 무릎이 발끝을 넘지 않도록 합니다.\n- 통증이 있다면 즉시 중단하세요.";
            case "엉덩이 들기":
                return "기본 자세:\n- 누운 상태에서 무릎을 구부리고 엉덩이를 들어올립니다.\n\n주의사항:\n- 허리를 과도하게 젖히지 않습니다.\n- 천천히 진행하세요.";
            case "엎드려 누운 상태로 다리 들기":
                return "기본 자세:\n- 엎드린 상태에서 한쪽 다리를 들어올립니다.\n\n주의사항:\n- 허리를 과도하게 젖히지 않습니다.\n- 통증이 있다면 즉시 중단하세요.";
            case "엉덩이 옆 근육 운동":
                return "기본 자세:\n- 옆으로 누운 상태에서 위쪽 다리를 들어올립니다.\n\n주의사항:\n- 몸이 앞뒤로 기울어지지 않도록 합니다.\n- 천천히 진행하세요.";
            case "무릎 벌리기":
                return "기본 자세:\n- 누운 상태에서 무릎을 구부리고 벌렸다 모았다를 반복합니다.\n\n주의사항:\n- 통증이 있다면 즉시 중단하세요.\n- 천천히 진행하세요.";
            case "무릎 펴기":
                return "기본 자세:\n- 누운 상태에서 한쪽 다리를 펴서 들어올립니다.\n\n주의사항:\n- 무릎을 완전히 펴지 않아도 됩니다.\n- 통증이 있다면 즉시 중단하세요.";
            case "런지":
                return "기본 자세:\n- 한쪽 다리를 앞으로 내밀고 무릎을 구부립니다.\n\n주의사항:\n- 무릎이 발끝을 넘지 않도록 합니다.\n- 천천히 진행하세요.";
            case "좌우런지":
                return "기본 자세:\n- 한쪽 다리를 옆으로 내밀고 무릎을 구부립니다.\n\n주의사항:\n- 무릎이 발끝을 넘지 않도록 합니다.\n- 천천히 진행하세요.";
            case "발전된 런지":
                return "기본 자세:\n- 한쪽 다리를 뒤로 내밀고 무릎을 구부립니다.\n\n주의사항:\n- 무릎이 발끝을 넘지 않도록 합니다.\n- 천천히 진행하세요.";
            case "손목 및 팔꿈치 주변 근육":
                return "기본 자세:\n- 고무줄을 사용하여 손목과 팔꿈치 주변 근육을 강화합니다.\n\n주의사항:\n- 통증이 있다면 즉시 중단하세요.\n- 천천히 진행하세요.";
            case "날개 뼈 모음 근육":
                return "기본 자세:\n- 고무줄을 사용하여 날개뼈를 모으는 동작을 반복합니다.\n\n주의사항:\n- 어깨를 들지 않고 자연스럽게 유지합니다.\n- 천천히 진행하세요.";
            case "앉았다 일어서기":
                return "기본 자세:\n- 의자에 앉았다 일어서는 동작을 반복합니다.\n\n주의사항:\n- 무릎이 발끝을 넘지 않도록 합니다.\n- 천천히 진행하세요.";
            case "발전된 앉았다 일어서기":
                return "기본 자세:\n- 의자에 앉았다 일어서는 동작을 팔을 앞으로 내밀며 반복합니다.\n\n주의사항:\n- 무릎이 발끝을 넘지 않도록 합니다.\n- 천천히 진행하세요.";
            case "어깨 운동 1단계":
                return "기본 자세:\n- 가벼운 무게를 들고 어깨를 들어올립니다.\n\n주의사항:\n- 목을 과도하게 움직이지 않습니다.\n- 천천히 진행하세요.";
            case "어깨 운동 2단계":
                return "기본 자세:\n- 양팔을 든 상태에서 손을 배쪽으로 교차시킵니다.\n\n주의사항:\n- 어깨를 들지 않고 자연스럽게 유지합니다.\n- 천천히 진행하세요.";
            case "한발 서기":
                return "기본 자세:\n- 한쪽 다리로 서서 균형을 잡습니다.\n\n주의사항:\n- 필요하다면 벽이나 의자를 잡고 진행하세요.\n- 천천히 진행하세요.";
            case "버드독 1단계":
                return "기본 자세:\n- 네 발 자세에서 한쪽 다리를 들어올립니다.\n\n주의사항:\n- 허리를 과도하게 젖히지 않습니다.\n- 천천히 진행하세요.";
            case "버드독 2단계":
                return "기본 자세:\n- 네 발 자세에서 반대쪽 팔과 다리를 들어올립니다.\n\n주의사항:\n- 허리를 과도하게 젖히지 않습니다.\n- 천천히 진행하세요.";
            case "앉은 상태에서 제자리 걷기":
                return "기본 자세:\n- 의자에 앉은 상태에서 팔과 다리를 번갈아 들어올립니다.\n\n주의사항:\n- 허리를 과도하게 젖히지 않습니다.\n- 천천히 진행하세요.";
            case "움직이는 런지":
                return "기본 자세:\n- 앞으로 나가며 런지 자세를 취합니다.\n\n주의사항:\n- 무릎이 발끝을 넘지 않도록 합니다.\n- 천천히 진행하세요.";
            default:
                return "운동을 시작하기 전에 기본 자세와 주의사항을 확인하세요.";
        }
    }
} 