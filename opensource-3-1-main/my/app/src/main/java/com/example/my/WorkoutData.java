package com.example.my;

import java.util.Date;

/**
 * 운동 기록 데이터 모델 클래스
 * 사용자의 운동 활동을 상세하게 기록하는 데이터 구조
 * 서버와의 JSON 데이터 교환을 위해 Gson 라이브러리와 호환되도록 설계
 * 운동 종류, 지속 시간, 완료 여부, 루틴 정보 등을 포함
 */
public class WorkoutData {
    // 데이터베이스 고유 식별자
    private Long id;
    
    // 운동 종류 (예: 목, 어깨, 팔, 등, 다리 등)
    private String exerciseType;
    
    // 운동 지속 시간 (밀리초 단위)
    private double duration;
    
    // 운동 완료 여부
    private boolean completed;
    
    // 운동 루틴 이름 (커스텀 루틴인 경우)
    private String routineName;
    
    // 현재 운동 번호 (루틴 내에서의 순서)
    private Integer exerciseNumber;
    
    // 전체 운동 개수 (루틴의 총 운동 수)
    private Integer totalExercises;
    
    // 운동 수행 날짜 및 시간
    private Date workoutDate;

    /**
     * 기본 생성자
     * Gson 라이브러리가 JSON에서 객체로 변환할 때 사용
     * 생성 시점을 현재 시간으로 설정
     */
    public WorkoutData() {
        this.workoutDate = new Date();  // 기본 생성자에서도 날짜 설정
    }

    /**
     * 기본 운동 기록 생성자
     * @param exerciseType 운동 종류
     * @param duration 운동 지속 시간 (밀리초)
     * @param completed 운동 완료 여부
     */
    public WorkoutData(String exerciseType, double duration, boolean completed) {
        this.exerciseType = exerciseType;
        this.duration = duration;
        this.completed = completed;
        this.workoutDate = new Date();
    }

    /**
     * 루틴 운동 기록 생성자
     * 커스텀 루틴 내에서의 운동 정보를 포함
     * @param exerciseType 운동 종류
     * @param duration 운동 지속 시간 (밀리초)
     * @param completed 운동 완료 여부
     * @param routineName 루틴 이름
     * @param exerciseNumber 현재 운동 번호
     * @param totalExercises 전체 운동 개수
     */
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

    // Getter와 Setter 메서드들
    // Gson 라이브러리가 JSON 직렬화/역직렬화 시 사용

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

    /**
     * 객체의 문자열 표현을 반환
     * 디버깅 및 로그 출력 시 사용
     * @return 객체의 모든 필드 정보를 포함한 문자열
     */
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