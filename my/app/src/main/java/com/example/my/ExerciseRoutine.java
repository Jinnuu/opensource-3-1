package com.example.my;

import java.util.List;

// 운동 루틴 정보 클래스

public class ExerciseRoutine {
    private Long id; // 루틴 ID
    private String name; // 루틴 이름
    private String description; // 루틴 설명
    private List<String> exercises; // 운동 목록

    // 루틴 ID 반환
    public Long getId() {
        return id;
    }

    // 루틴 ID 설정
    public void setId(Long id) {
        this.id = id;
    }

    // 루틴 이름 반환
    public String getName() {
        return name;
    }

    // 루틴 이름 설정
    public void setName(String name) {
        this.name = name;
    }

    // 루틴 설명 반환
    public String getDescription() {
        return description;
    }

    // 루틴 설명 설정
    public void setDescription(String description) {
        this.description = description;
    }

    // 운동 목록 반환
    public List<String> getExercises() {
        return exercises;
    }

    // 운동 목록 설정
    public void setExercises(List<String> exercises) {
        this.exercises = exercises;
    }
} 