package com.example.my;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

//API 통신을 위한 서비스 클래스 (서버와 통신하기 위한 retrofit 인터페이스를 생성)

public class ApiService {
    private static final String BASE_URL = "http://172.30.68.82:8080/";
    private static Retrofit retrofit = null;

    public static Retrofit getClient() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
} 