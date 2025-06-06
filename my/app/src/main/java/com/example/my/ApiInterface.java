package com.example.my;

import retrofit2.Call;
import retrofit2.http.*;

import java.util.Map;

//API 통신을 위한 인터페이스 (엔드포인트 정의의)

public interface ApiInterface {
    @POST("api/users/register")
    Call<Map<String, String>> register(@Body Map<String, String> user);

    @POST("api/users/login")
    Call<Map<String, String>> login(@Body Map<String, String> user);

    @GET("api/users/registration-date")
    Call<Map<String, String>> getRegistrationDate(@Query("userName") String userName);

    @POST("api/workouts")
    Call<Map<String, String>> saveWorkout(@Body Map<String, Object> workout);
} 