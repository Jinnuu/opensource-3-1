package com.example.my;

/**
 * 운동 정보 모델 클래스
 * 개별 운동의 기본 정보를 담는 데이터 구조
 * 운동 이름과 설명을 포함하여 운동 목록에서 사용
 * 간단한 데이터 구조로 운동 정보를 효율적으로 관리
 */
public class Exercise {
    // 운동 이름
    private String name;
    
    // 운동에 대한 상세 설명
    private String description;

    /**
     * 운동 객체 생성자
     * @param name 운동 이름
     * @param description 운동 설명
     */
    public Exercise(String name, String description) {
        this.name = name;
        this.description = description;
    }

    /**
     * 운동 이름 반환
     * @return 운동 이름
     */
    public String getName() {
        return name;
    }

    /**
     * 운동 설명 반환
     * @return 운동에 대한 상세 설명
     */
    public String getDescription() {
        return description;
    }
} 