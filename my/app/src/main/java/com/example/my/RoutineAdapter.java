package com.example.my;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

// 운동 루틴 어댑터

public class RoutineAdapter extends RecyclerView.Adapter<RoutineAdapter.RoutineViewHolder> {
    private List<ExerciseRoutine> routines;
    private final OnRoutineClickListener listener;
    private final boolean isDeleteMode;

    public interface OnRoutineClickListener {
        void onRoutineClick(ExerciseRoutine routine);
    }

    public RoutineAdapter(List<ExerciseRoutine> routines, OnRoutineClickListener listener, boolean isDeleteMode) {
        this.routines = routines;
        this.listener = listener;
        this.isDeleteMode = isDeleteMode;
    }

    @NonNull
    @Override
    public RoutineViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_routine, parent, false);
        return new RoutineViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RoutineViewHolder holder, int position) {
        ExerciseRoutine routine = routines.get(position);
        holder.tvRoutineName.setText(routine.getName());
        holder.tvExerciseCount.setText(routine.getExercises().size() + "개의 운동");
        
        // 모드에 따라 버튼 텍스트와 색상 변경
        if (isDeleteMode) {
            holder.btnAction.setText("삭제");
            holder.btnAction.setBackgroundTintList(holder.itemView.getContext().getColorStateList(R.color.delete_button));
        } else {
            holder.btnAction.setText("운동 시작");
            holder.btnAction.setBackgroundTintList(holder.itemView.getContext().getColorStateList(R.color.start_button));
        }
        
        holder.btnAction.setOnClickListener(v -> {
            if (listener != null) {
                listener.onRoutineClick(routine);
            }
        });
    }

    @Override
    public int getItemCount() {
        return routines.size();
    }

    public void updateRoutines(List<ExerciseRoutine> newRoutines) {
        this.routines = newRoutines;
        notifyDataSetChanged();
    }

    static class RoutineViewHolder extends RecyclerView.ViewHolder {
        TextView tvRoutineName;
        TextView tvExerciseCount;
        Button btnAction;

        RoutineViewHolder(View itemView) {
            super(itemView);
            tvRoutineName = itemView.findViewById(R.id.tvRoutineName);
            tvExerciseCount = itemView.findViewById(R.id.tvExerciseCount);
            btnAction = itemView.findViewById(R.id.btnAction);
        }
    }
} 