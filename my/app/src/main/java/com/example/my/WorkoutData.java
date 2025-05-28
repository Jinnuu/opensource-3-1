package com.example.my;

import java.util.Date;

// 운동 기록 데이터 클래스

public class WorkoutData {
    private Long id;
    private String exerciseType;
    private double duration;
    private boolean completed;
    private String routineName;
    private Integer exerciseNumber;
    private Integer totalExercises;
    private Date workoutDate;

    // 기본 생성자 (Gson이 필요로 함)
    public WorkoutData() {
        this.workoutDate = new Date();  // 기본 생성자에서도 날짜 설정
    }

    public WorkoutData(String exerciseType, double duration, boolean completed) {
        this.exerciseType = exerciseType;
        this.duration = duration;
        this.completed = completed;
        this.workoutDate = new Date();
    }

    public WorkoutData(String exerciseType, double duration, boolean completed, 
                      String routineName, Integer exerciseNumber, Integer totalExercises) {
        this.exerciseType = exerciseType;
        this.duration = duration;
        this.completed = completed;
        this.routineName = routineName;
        this.exerciseNumber = exerciseNumber;
        this.totalExercises = totalExercises;
        this.workoutDate = new Date();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getExerciseType() {
        return exerciseType;
    }

    public void setExerciseType(String exerciseType) {
        this.exerciseType = exerciseType;
    }

    public double getDuration() {
        return duration;
    }

    public void setDuration(double duration) {
        this.duration = duration;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public String getRoutineName() {
        return routineName;
    }

    public void setRoutineName(String routineName) {
        this.routineName = routineName;
    }

    public Integer getExerciseNumber() {
        return exerciseNumber;
    }

    public void setExerciseNumber(Integer exerciseNumber) {
        this.exerciseNumber = exerciseNumber;
    }

    public Integer getTotalExercises() {
        return totalExercises;
    }

    public void setTotalExercises(Integer totalExercises) {
        this.totalExercises = totalExercises;
    }

    public Date getWorkoutDate() {
        return workoutDate;
    }

    public void setWorkoutDate(Date workoutDate) {
        this.workoutDate = workoutDate;
    }

    @Override
    public String toString() {
        return "WorkoutData{" +
                "id=" + id +
                ", exerciseType='" + exerciseType + '\'' +
                ", duration=" + duration +
                ", completed=" + completed +
                ", routineName='" + routineName + '\'' +
                ", exerciseNumber=" + exerciseNumber +
                ", totalExercises=" + totalExercises +
                ", workoutDate=" + workoutDate +
                '}';
    }
} 