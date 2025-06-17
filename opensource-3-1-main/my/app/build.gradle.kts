// 앱 모듈의 빌드 설정 파일 (Kotlin DSL)
// 앱의 컴파일, 패키징, 의존성 관리를 담당

plugins {
    // 안드로이드 애플리케이션 플러그인 적용
    alias(libs.plugins.android.application)
}

android {
    // 앱의 패키지명 정의
    namespace = "com.example.my"
    
    // 컴파일 대상 SDK 버전 (최신 안드로이드 API 레벨)
    compileSdk = 35

    defaultConfig {
        // 앱의 고유 식별자 (Google Play Store에서 사용)
        applicationId = "com.example.my"
        
        // 최소 지원 안드로이드 버전 (API 24 = Android 7.0)
        minSdk = 24
        
        // 타겟 안드로이드 버전 (최신 버전 타겟팅)
        targetSdk = 35
        
        // 앱 버전 정보
        versionCode = 1
        versionName = "1.0"

        // 테스트 실행기 설정
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        // 릴리즈 빌드 설정 (배포용)
        release {
            // 코드 난독화 비활성화 (디버깅 용이성)
            isMinifyEnabled = false
            
            // ProGuard 규칙 파일 설정
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    
    // Java 컴파일 옵션 설정
    compileOptions {
        // Java 11 버전 사용 (최신 언어 기능 활용)
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

// 프로젝트 의존성 정의
// 앱이 사용하는 외부 라이브러리들을 명시
dependencies {
    // 안드로이드 기본 UI 라이브러리들
    implementation(libs.appcompat)        // AppCompat - 하위 버전 호환성
    implementation(libs.material)         // Material Design 컴포넌트
    implementation(libs.activity)         // Activity 관련 기능
    implementation(libs.constraintlayout) // ConstraintLayout - 유연한 레이아웃
    
    // 테스트 관련 라이브러리들
    testImplementation(libs.junit)        // 단위 테스트
    androidTestImplementation(libs.ext.junit)    // 안드로이드 테스트
    androidTestImplementation(libs.espresso.core) // UI 테스트
}