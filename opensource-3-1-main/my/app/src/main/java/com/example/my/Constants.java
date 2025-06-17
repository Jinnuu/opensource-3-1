package com.example.my;

/**
 * 상수 클래스
 * 앱에서 사용하는 모든 상수값들을 중앙에서 관리
 * 주로 서버 통신을 위한 API 엔드포인트 URL들을 정의
 * 개발 환경에 따라 BASE_URL을 변경하여 서버 주소 설정 가능
 */
public class Constants {
    // 서버 기본 URL - 개발 환경에 따라 변경 필요
    // WiFi IP 주소를 사용하여 로컬 네트워크 내 서버와 통신
    public static final String BASE_URL = "http://172.30.78.238:8080";
    
    // 사용자 관련 API 엔드포인트
    public static final String API_USERS = BASE_URL + "/api/users";
    
    // 인증 관련 API 엔드포인트
    public static final String API_LOGIN = BASE_URL + "/api/login";
    public static final String API_REGISTER = BASE_URL + "/api/register";
    
    // 운동 루틴 관련 API 엔드포인트
    public static final String API_ROUTINES = BASE_URL + "/api/routines";
    
    // 운동 정보 관련 API 엔드포인트
    public static final String API_EXERCISES = BASE_URL + "/api/exercises";
    
    // 리포트 관련 API 엔드포인트
    public static final String API_REPORTS = BASE_URL + "/api/reports";
    
    // 워크아웃 기록 관련 API 엔드포인트
    public static final String API_WORKOUTS = BASE_URL + "/api/workouts";
} 