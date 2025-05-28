package com.example.my;

import android.content.Intent;
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
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

// 운동 가이드 화면

public class exercise_guide extends Fragment {
    private static final String ARG_EXERCISE_TYPE = "exercise_type";
    private static final String ARG_EXERCISE_DESCRIPTION = "exercise_description";
    private static final String ARG_VIDEO_ID = "video_id";
    
    private Handler handler;
    private YouTubePlayer activeYouTubePlayer;
    private Runnable pauseRunnable;
    private String exerciseType;
    private String exerciseDescription;
    private String videoId;
    private Button btnComplete;
    private TextView tvExerciseDescription;

    public exercise_guide() {
    }

    public static exercise_guide newInstance(String type, String description, String videoId) {
        exercise_guide fragment = new exercise_guide();
        Bundle args = new Bundle();
        args.putString(ARG_EXERCISE_TYPE, type);
        args.putString(ARG_EXERCISE_DESCRIPTION, description);
        args.putString(ARG_VIDEO_ID, videoId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            exerciseType = getArguments().getString(ARG_EXERCISE_TYPE);
            exerciseDescription = getArguments().getString(ARG_EXERCISE_DESCRIPTION);
            videoId = getArguments().getString(ARG_VIDEO_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_exercise_guide, container, false);

        try {
            // View 초기화
            YouTubePlayerView youtubePlayerView = view.findViewById(R.id.youtubePlayerView);
            TextView tvExerciseTitle = view.findViewById(R.id.tvExerciseTitle);
            TextView tvFocusArea = view.findViewById(R.id.tvFocusArea);
            TextView tvEquipment = view.findViewById(R.id.tvEquipment);
            TextView tvTip = view.findViewById(R.id.tvTip);
            btnComplete = view.findViewById(R.id.btnComplete);
            tvExerciseDescription = view.findViewById(R.id.tvExerciseDescription);

            // 운동 정보 설정
            if (exerciseType != null) {
                tvExerciseTitle.setText(exerciseType);
                tvFocusArea.setText("집중 영역: " + (exerciseDescription != null ? exerciseDescription : ""));
                tvEquipment.setText("장비: 없음");
                tvTip.setText(getExerciseTip(exerciseType));
            }

            // YouTube 플레이어 설정
            if (getActivity() != null) {
                getLifecycle().addObserver(youtubePlayerView);
                handler = new Handler(Looper.getMainLooper());

                youtubePlayerView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
                    @Override
                    public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                        activeYouTubePlayer = youTubePlayer;
                        if (videoId != null) {
                            youTubePlayer.loadVideo(videoId, 0);
                        }
                    }

                    @Override
                    public void onStateChange(@NonNull YouTubePlayer youTubePlayer,
                                            @NonNull PlayerConstants.PlayerState state) {
                        if (state == PlayerConstants.PlayerState.PLAYING) {
                            pauseRunnable = new Runnable() {
                                @Override
                                public void run() {
                                    youTubePlayer.pause();
                                    showCompletionToast();
                                }
                            };
                            handler.postDelayed(pauseRunnable, 10000);
                        } else if (pauseRunnable != null) {
                            handler.removeCallbacks(pauseRunnable);
                        }
                    }
                });
            }

            // 완료 버튼 클릭 리스너
            btnComplete.setOnClickListener(v -> {
                if (getActivity() != null) {
                    ExerciseTimeDialog dialog = new ExerciseTimeDialog(getActivity(), (minutes, seconds) -> {
                        // 선택된 시간을 밀리초로 변환
                        long exerciseTime = (minutes * 60 + seconds) * 1000L;
                        
                        Intent intent = new Intent(getActivity(), ExerciseActivity.class);
                        intent.putExtra("exercise_type", exerciseType);
                        intent.putExtra("exercise_description", exerciseDescription);
                        intent.putExtra("exercise_time", exerciseTime);
                        startActivity(intent);
                    });
                    dialog.show();
                }
            });

            initializeExerciseGuide();
        } catch (Exception e) {
            e.printStackTrace();
            if (getContext() != null) {
                Toast.makeText(getContext(), "운동 가이드를 불러오는데 실패했습니다.", Toast.LENGTH_SHORT).show();
            }
        }

        // View 초기화
        YouTubePlayerView youtubePlayerView = view.findViewById(R.id.youtubePlayerView);
        TextView tvExerciseTitle = view.findViewById(R.id.tvExerciseTitle);
        TextView tvFocusArea = view.findViewById(R.id.tvFocusArea);
        TextView tvEquipment = view.findViewById(R.id.tvEquipment);
        TextView tvTip = view.findViewById(R.id.tvTip);
        btnComplete = view.findViewById(R.id.btnComplete);
        tvExerciseDescription = view.findViewById(R.id.tvExerciseDescription);

        // 운동 정보 설정
        tvExerciseTitle.setText(exerciseType);
        tvFocusArea.setText("집중 영역: " + exerciseDescription);
        tvEquipment.setText("장비: 없음");
        tvTip.setText(getExerciseTip(exerciseType));

        // YouTube 플레이어 설정
        getLifecycle().addObserver(youtubePlayerView);
        handler = new Handler(Looper.getMainLooper());

        youtubePlayerView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
            @Override
            public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                activeYouTubePlayer = youTubePlayer;
                youTubePlayer.loadVideo(videoId, 0);
            }

            @Override
            public void onStateChange(@NonNull YouTubePlayer youTubePlayer,
                                      @NonNull PlayerConstants.PlayerState state) {
                if (state == PlayerConstants.PlayerState.PLAYING) {
                    pauseRunnable = new Runnable() {
                        @Override
                        public void run() {
                            youTubePlayer.pause();
                            showCompletionToast();
                        }
                    };
                    handler.postDelayed(pauseRunnable, 10000);
                } else {
                    handler.removeCallbacks(pauseRunnable);
                }
            }
        });

        // 완료 버튼 클릭 리스너
        btnComplete.setOnClickListener(v -> {
            if (getActivity() != null) {
                ExerciseTimeDialog dialog = new ExerciseTimeDialog(getActivity(), (minutes, seconds) -> {
                    // 선택된 시간을 밀리초로 변환
                    long exerciseTime = (minutes * 60 + seconds) * 1000L;
                    
                    Intent intent = new Intent(getActivity(), ExerciseActivity.class);
                    intent.putExtra("exercise_type", exerciseType);
                    intent.putExtra("exercise_description", exerciseDescription);
                    intent.putExtra("exercise_time", exerciseTime);
                    startActivity(intent);
                });
                dialog.show();
            }
        });

        initializeExerciseGuide();

        return view;
    }

    private String getExerciseTip(String exerciseType) {
        switch (exerciseType) {
            case "손목 운동":
                return "팁: 손목을 부드럽게 움직이세요.";
            case "허리 운동":
                return "팁: 허리를 천천히 스트레칭하세요.";
            case "목 운동":
                return "팁: 목을 부드럽게 돌리세요.";
            case "커스텀 운동":
                return "팁: 자신의 페이스에 맞춰 진행하세요.";
            default:
                return "팁: 천천히 진행하세요.";
        }
    }

    private Class<?> getExerciseActivityClass(String exerciseType) {
        return ExerciseActivity.class;
    }

    private void showCompletionToast() {
        if (getContext() != null) {
            Toast.makeText(getContext(), "10초 재생 완료", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (handler != null && pauseRunnable != null) {
            handler.removeCallbacks(pauseRunnable);
        }
    }

    private void initializeExerciseGuide() {
        String guideText = "";
        switch (exerciseType) {
            case "손목 운동":
                guideText = "손목 운동 가이드\n\n" +
                    "1. 손목 스트레칭 (30초)\n" +
                    "   - 손바닥을 위로 향하게 하고 다른 손으로 손가락을 아래로 당깁니다.\n" +
                    "   - 반대 방향으로도 동일하게 수행합니다.\n\n" +
                    "2. 손목 회전 운동 (각 방향 10회)\n" +
                    "   - 손목을 시계 방향과 반시계 방향으로 천천히 회전합니다.\n\n" +
                    "3. 손가락 굴곡/신전 운동 (각 10회)\n" +
                    "   - 손가락을 구부렸다 펴는 동작을 반복합니다.";
                break;

            case "허리 운동":
                guideText = "허리 운동 가이드\n\n" +
                    "1. 허리 스트레칭 (30초)\n" +
                    "   - 무릎을 구부리고 누운 상태에서 양쪽 무릎을 한쪽으로 천천히 기울입니다.\n\n" +
                    "2. 고양이 자세 (10회)\n" +
                    "   - 네 발 자세에서 등을 둥글게 말았다 펴는 동작을 반복합니다.\n\n" +
                    "3. 허리 회전 운동 (각 방향 10회)\n" +
                    "   - 서서 허리를 좌우로 천천히 회전합니다.";
                break;

            case "목 운동":
                guideText = "목 운동 가이드\n\n" +
                    "1. 목 스트레칭 (각 방향 30초)\n" +
                    "   - 목을 좌우로 기울여 스트레칭합니다.\n\n" +
                    "2. 목 회전 운동 (각 방향 10회)\n" +
                    "   - 목을 천천히 좌우로 회전합니다.\n\n" +
                    "3. 어깨 으쓱하기 (10회)\n" +
                    "   - 어깨를 위로 올렸다 내리는 동작을 반복합니다.";
                break;

            case "어깨 운동":
                guideText = "어깨 운동 가이드\n\n" +
                    "1. 어깨 프레스 (3세트 12회)\n" +
                    "   - 덤벨을 들고 어깨 높이에서 위로 밀어올립니다.\n\n" +
                    "2. 사이드 레터럴 레이즈 (3세트 12회)\n" +
                    "   - 양손에 덤벨을 들고 옆으로 들어올립니다.\n\n" +
                    "3. 프론트 레이즈 (3세트 12회)\n" +
                    "   - 덤벨을 들고 앞으로 들어올립니다.";
                break;

            case "팔 운동":
                guideText = "팔 운동 가이드\n\n" +
                    "1. 덤벨 컬 (3세트 12회)\n" +
                    "   - 덤벨을 들고 팔꿈치를 고정한 채 앞으로 구부립니다.\n\n" +
                    "2. 트라이셉스 익스텐션 (3세트 12회)\n" +
                    "   - 덤벨을 들고 팔을 뒤로 펴는 동작을 반복합니다.\n\n" +
                    "3. 해머 컬 (3세트 12회)\n" +
                    "   - 덤벨을 세로로 잡고 구부리는 동작을 반복합니다.";
                break;

            case "가슴 운동":
                guideText = "가슴 운동 가이드\n\n" +
                    "1. 푸시업 (3세트 10회)\n" +
                    "   - 팔꿈치를 45도로 유지하며 푸시업을 수행합니다.\n\n" +
                    "2. 덤벨 플라이 (3세트 12회)\n" +
                    "   - 누운 상태에서 덤벨을 들고 가슴을 벌렸다 모았다 합니다.\n\n" +
                    "3. 벤치 프레스 (3세트 10회)\n" +
                    "   - 바벨을 들고 가슴에서 위로 밀어올립니다.";
                break;

            case "복근 운동":
                guideText = "복근 운동 가이드\n\n" +
                    "1. 크런치 (3세트 15회)\n" +
                    "   - 누운 상태에서 상체를 들어올립니다.\n\n" +
                    "2. 플랭크 (3세트 30초)\n" +
                    "   - 팔꿈치를 구부리고 엎드린 자세를 유지합니다.\n\n" +
                    "3. 레그 레이즈 (3세트 12회)\n" +
                    "   - 누운 상태에서 다리를 들어올립니다.";
                break;

            case "엉덩이 운동":
                guideText = "엉덩이 운동 가이드\n\n" +
                    "1. 스쿼트 (3세트 15회)\n" +
                    "   - 발을 어깨 너비로 벌리고 앉았다 일어섭니다.\n\n" +
                    "2. 힙 쓰러스트 (3세트 12회)\n" +
                    "   - 누운 상태에서 엉덩이를 들어올립니다.\n\n" +
                    "3. 글루트 브릿지 (3세트 15회)\n" +
                    "   - 한쪽 다리를 들어올린 상태에서 엉덩이를 들어올립니다.";
                break;

            case "다리 운동":
                guideText = "다리 운동 가이드\n\n" +
                    "1. 스쿼트 (3세트 15회)\n" +
                    "   - 발을 어깨 너비로 벌리고 앉았다 일어섭니다.\n\n" +
                    "2. 런지 (각 다리 3세트 10회)\n" +
                    "   - 한 발을 앞으로 내딛고 앉았다 일어섭니다.\n\n" +
                    "3. 레그 프레스 (3세트 12회)\n" +
                    "   - 기계를 사용하여 다리를 밀어냅니다.";
                break;
        }
        tvExerciseDescription.setText(guideText);
    }
} 