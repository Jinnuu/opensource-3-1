package com.example.my;

import retrofit2.Call;
import retrofit2.http.*;

import java.util.Map;

/**
 * API 통신 인터페이스
 * Retrofit 라이브러리를 사용하여 서버 API 엔드포인트를 정의
 * 각 메서드는 HTTP 요청 방식과 URL을 어노테이션으로 지정
 * 서버와의 데이터 교환을 위한 RESTful API 호출 메서드들을 포함
 */
public interface ApiInterface {
    /**
     * 사용자 회원가입 API
     * 새로운 사용자 계정을 생성하는 POST 요청
     * @param user 회원가입할 사용자 정보 (이름, 비밀번호)
     * @return 서버 응답 결과
     */
    @POST("api/users/register")
    Call<Map<String, String>> register(@Body Map<String, String> user);

    /**
     * 사용자 로그인 API
     * 기존 사용자의 인증을 처리하는 POST 요청
     * @param user 로그인할 사용자 정보 (이름, 비밀번호)
     * @return 서버 응답 결과 (로그인 성공/실패)
     */
    @POST("api/users/login")
    Call<Map<String, String>> login(@Body Map<String, String> user);

    /**
     * 사용자 회원가입 날짜 조회 API
     * 특정 사용자의 회원가입 날짜를 조회하는 GET 요청
     * @param userName 조회할 사용자 이름
     * @return 회원가입 날짜 정보
     */
    @GET("api/users/registration-date")
    Call<Map<String, String>> getRegistrationDate(@Query("userName") String userName);

    /**
     * 운동 기록 저장 API
     * 사용자의 운동 활동을 서버에 저장하는 POST 요청
     * @param workout 저장할 운동 기록 데이터
     * @return 저장 결과
     */
    @POST("api/workouts")
    Call<Map<String, String>> saveWorkout(@Body Map<String, Object> workout);
} 