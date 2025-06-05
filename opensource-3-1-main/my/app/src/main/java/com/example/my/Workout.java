package com.example.my;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

// 운동 기록 클래스

public class Workout {
    private String type;        // 운동 종류 (wrist, back, neck, custom)
    private int duration;       // 운동 시간 (초 단위)
    private Date date;          // 운동 날짜
    private String userName;    // 사용자 이름
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());

    public Workout() {}

    public Workout(String type, int duration, Date date, String userName) {
        this.type = type;
        this.duration = duration;
        this.date = date;
        this.userName = userName;
    }

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

    // Gson이 사용할 메서드
    public String getFormattedDate() {
        return dateFormat.format(date);
    }

    public void setFormattedDate(String dateStr) {
        try {
            this.date = dateFormat.parse(dateStr);
        } catch (Exception e) {
            this.date = new Date();
        }
    }
} 