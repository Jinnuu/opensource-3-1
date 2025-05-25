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

        // View 초기화
        YouTubePlayerView youtubePlayerView = view.findViewById(R.id.youtubePlayerView);
        TextView tvExerciseTitle = view.findViewById(R.id.tvExerciseTitle);
        TextView tvFocusArea = view.findViewById(R.id.tvFocusArea);
        TextView tvEquipment = view.findViewById(R.id.tvEquipment);
        TextView tvTip = view.findViewById(R.id.tvTip);
        btnComplete = view.findViewById(R.id.btnComplete);

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
} 