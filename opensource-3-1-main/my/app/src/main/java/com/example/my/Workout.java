package com.example.my;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 운동 기록 모델 클래스
 * 사용자의 운동 활동을 추적하기 위한 데이터 구조
 * 서버와의 JSON 데이터 교환을 위해 Gson 라이브러리와 호환되도록 설계
 * 운동 종류, 지속 시간, 날짜, 사용자 정보를 포함
 */
public class Workout {
    // 운동 종류 (예: wrist, back, neck, custom 등)
    private String type;
    
    // 운동 지속 시간 (초 단위로 저장)
    private int duration;
    
    // 운동을 수행한 날짜와 시간
    private Date date;
    
    // 운동을 수행한 사용자의 이름
    private String userName;
    
    // 날짜 형식화를 위한 SimpleDateFormat 객체
    // ISO 8601 형식으로 날짜를 문자열로 변환 (서버 통신용)
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());

    /**
     * 기본 생성자
     * Gson 라이브러리가 JSON에서 객체로 변환할 때 사용
     */
    public Workout() {}

    /**
     * 매개변수 생성자
     * 새로운 운동 기록을 생성할 때 사용
     * @param type 운동 종류
     * @param duration 운동 지속 시간 (초)
     * @param date 운동 날짜
     * @param userName 사용자 이름
     */
    public Workout(String type, int duration, Date date, String userName) {
        this.type = type;
        this.duration = duration;
        this.date = date;
        this.userName = userName;
    }

    // Getter와 Setter 메서드들
    // Gson 라이브러리가 JSON 직렬화/역직렬화 시 사용

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * 날짜를 ISO 8601 형식의 문자열로 변환
     * Gson이 JSON으로 직렬화할 때 사용하는 메서드
     * @return 형식화된 날짜 문자열
     */
    public String getFormattedDate() {
        return dateFormat.format(date);
    }

    /**
     * ISO 8601 형식의 문자열을 Date 객체로 변환
     * Gson이 JSON에서 객체로 역직렬화할 때 사용하는 메서드
     * @param dateStr 형식화된 날짜 문자열
     */
    public void setFormattedDate(String dateStr) {
        try {
            this.date = dateFormat.parse(dateStr);
        } catch (Exception e) {
            // 파싱 실패 시 현재 날짜로 설정
            this.date = new Date();
        }
    }
} 