package com.example.my;

public class WorkoutData {
    private String exerciseType;
    private long duration;
    private boolean completed;

    public WorkoutData(String exerciseType, long duration, boolean completed) {
        this.exerciseType = exerciseType;
        this.duration = duration;
        this.completed = completed;
    }

    // Getters
    public String getExerciseType() {
        return exerciseType;
    }

    public long getDuration() {
        return duration;
    }

    public boolean isCompleted() {
        return completed;
    }

    // Setters
    public void setExerciseType(String exerciseType) {
        this.exerciseType = exerciseType;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }
} 