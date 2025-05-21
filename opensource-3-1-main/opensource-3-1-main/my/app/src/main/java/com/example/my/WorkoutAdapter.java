package com.example.my;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class WorkoutAdapter extends RecyclerView.Adapter<WorkoutAdapter.WorkoutViewHolder> {
    private List<Workout> workouts;
    private SimpleDateFormat dateFormat;

    public WorkoutAdapter(List<Workout> workouts) {
        this.workouts = workouts;
        this.dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
    }

    public void updateWorkouts(List<Workout> newWorkouts) {
        this.workouts = newWorkouts;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public WorkoutViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.item_workout, parent, false);
        return new WorkoutViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WorkoutViewHolder holder, int position) {
        Workout workout = workouts.get(position);
        
        // 운동 종류 한글화
        String type = switch (workout.getType()) {
            case "wrist" -> "손목 운동";
            case "back" -> "등 운동";
            case "neck" -> "목 운동";
            case "custom" -> "커스텀 운동";
            default -> workout.getType();
        };
        
        holder.tvWorkoutType.setText(type);
        holder.tvWorkoutDuration.setText(formatDuration(workout.getDuration()));
        holder.tvWorkoutDate.setText(dateFormat.format(workout.getDate()));
    }

    @Override
    public int getItemCount() {
        return workouts.size();
    }

    private String formatDuration(int seconds) {
        int minutes = seconds / 60;
        int remainingSeconds = seconds % 60;
        return String.format("%02d:%02d", minutes, remainingSeconds);
    }

    static class WorkoutViewHolder extends RecyclerView.ViewHolder {
        TextView tvWorkoutType, tvWorkoutDuration, tvWorkoutDate;

        WorkoutViewHolder(View itemView) {
            super(itemView);
            tvWorkoutType = itemView.findViewById(R.id.tvWorkoutType);
            tvWorkoutDuration = itemView.findViewById(R.id.tvWorkoutDuration);
            tvWorkoutDate = itemView.findViewById(R.id.tvWorkoutDate);
        }
    }
} 