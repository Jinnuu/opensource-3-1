package com.example.my;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import java.util.concurrent.TimeUnit;

/**
 * API 통신 서비스 클래스
 * Retrofit 라이브러리를 사용하여 서버와의 HTTP 통신을 관리
 * 싱글톤 패턴으로 구현되어 앱 전체에서 하나의 Retrofit 인스턴스 공유
 * JSON 데이터와 Java 객체 간의 자동 변환을 위한 GsonConverterFactory 설정
 */
public class ApiService {
    // Retrofit 인스턴스를 저장하는 정적 변수 (싱글톤 패턴)
    private static Retrofit retrofit = null;

    /**
     * Retrofit 클라이언트 인스턴스를 반환하는 정적 메서드
     * 처음 호출 시에만 새로운 인스턴스를 생성하고, 이후에는 기존 인스턴스 재사용
     * @return 설정된 Retrofit 인스턴스
     */
    public static Retrofit getClient() {
        if (retrofit == null) {
            // HTTP 클라이언트 설정 (타임아웃 포함)
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)  // 연결 타임아웃
                    .readTimeout(30, TimeUnit.SECONDS)     // 읽기 타임아웃
                    .writeTimeout(30, TimeUnit.SECONDS)    // 쓰기 타임아웃
                    .build();

            // Retrofit 빌더를 사용하여 클라이언트 생성
            retrofit = new Retrofit.Builder()
                    .baseUrl(Constants.BASE_URL + "/")     // 서버 기본 URL 설정
                    .client(okHttpClient)                  // HTTP 클라이언트 설정
                    .addConverterFactory(GsonConverterFactory.create())  // JSON 변환기 설정
                    .build();
        }
        return retrofit;
    }
} 