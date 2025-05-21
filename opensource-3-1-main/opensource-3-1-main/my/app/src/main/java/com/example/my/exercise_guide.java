package com.example.my;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ImageView;

import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

public class exercise_guide extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private Handler handler;
    private YouTubePlayer activeYouTubePlayer;
    private Runnable pauseRunnable;
    private String mParam1;
    private String mParam2;

    public exercise_guide() {
    }

    public static exercise_guide newInstance(String param1, String param2) {
        exercise_guide fragment = new exercise_guide();
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
        View view = inflater.inflate(R.layout.fragment_exercise_guide, container, false);

        YouTubePlayerView youtubePlayerView = view.findViewById(R.id.youtubePlayerView);
        getLifecycle().addObserver(youtubePlayerView);

        handler = new Handler(Looper.getMainLooper()); // 핸들러 초기화

        youtubePlayerView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
            @Override
            public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                activeYouTubePlayer = youTubePlayer; // 인스턴스 저장
                String videoId = "m6nnpHeH86E";
                youTubePlayer.loadVideo(videoId, 0); // 자동 재생 시작
            }

            @Override
            public void onStateChange(@NonNull YouTubePlayer youTubePlayer,
                                      @NonNull PlayerConstants.PlayerState state) {
                if (state == PlayerConstants.PlayerState.PLAYING) {
                    // 재생 시작 시 10초 타이머 설정
                    pauseRunnable = new Runnable() {
                        @Override
                        public void run() {
                            youTubePlayer.pause();
                            showCompletionToast();
                        }
                    };
                    handler.postDelayed(pauseRunnable, 10000);
                } else {
                    // 일시정지/중지 시 타이머 취소
                    handler.removeCallbacks(pauseRunnable);
                }
            }
        });

        return view;
    }

    private void showCompletionToast() {
        if (getContext() != null) {
            Toast.makeText(getContext(), "10초 재생 완료", Toast.LENGTH_SHORT).show();
        }
    }
}

