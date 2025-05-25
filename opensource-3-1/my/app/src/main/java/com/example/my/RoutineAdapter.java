package com.example.my;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class RoutineAdapter extends RecyclerView.Adapter<RoutineAdapter.RoutineViewHolder> {
    private List<ExerciseRoutine> routines;
    private final OnRoutineClickListener listener;

    public interface OnRoutineClickListener {
        void onDeleteClick(Long routineId);
    }

    public RoutineAdapter(List<ExerciseRoutine> routines, OnRoutineClickListener listener) {
        this.routines = routines;
        this.listener = listener;
    }

    public void updateRoutines(List<ExerciseRoutine> newRoutines) {
        this.routines = newRoutines;
        notifyDataSetChanged();
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
        holder.tvRoutineDescription.setText(routine.getDescription());
        
        // 운동 목록을 쉼표로 구분하여 표시
        StringBuilder exercisesText = new StringBuilder();
        for (int i = 0; i < routine.getExercises().size(); i++) {
            if (i > 0) {
                exercisesText.append(", ");
            }
            exercisesText.append(routine.getExercises().get(i));
        }
        holder.tvExercises.setText(exercisesText.toString());

        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDeleteClick(routine.getId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return routines.size();
    }

    static class RoutineViewHolder extends RecyclerView.ViewHolder {
        TextView tvRoutineName, tvRoutineDescription, tvExercises;
        Button btnDelete;

        RoutineViewHolder(View itemView) {
            super(itemView);
            tvRoutineName = itemView.findViewById(R.id.tvRoutineName);
            tvRoutineDescription = itemView.findViewById(R.id.tvRoutineDescription);
            tvExercises = itemView.findViewById(R.id.tvExercises);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
} 