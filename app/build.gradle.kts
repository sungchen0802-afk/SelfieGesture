plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "tw.edu.pu.tcyang.selfiegesture"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "tw.edu.pu.tcyang.selfiegesture"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    implementation("com.google.accompanist:accompanist-permissions:0.37.3")  //確保使用最新版
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.9.2") //請確認最新版

    val cameraxVersion = "1.5.2" // 請檢查最新的穩定版本
    implementation("androidx.camera:camera-core:$cameraxVersion")
    implementation("androidx.camera:camera-camera2:$cameraxVersion")
    // CameraX Lifecycle - 用於將 CameraX 綁定到 Activity/Fragment 的生命週期
    implementation("androidx.camera:camera-lifecycle:$cameraxVersion")
    // CameraX View - 包含 PreviewView，用於顯示相機預覽
    implementation("androidx.camera:camera-view:$cameraxVersion")
    // CameraX Extensions (可選) - 用於啟用特殊效果，如 HDR, 夜間模式等，需要裝置支援
    implementation("androidx.camera:camera-extensions:$cameraxVersion")
    // CameraX VideoCapture (可選) - 用於錄影功能
    implementation("androidx.camera:camera-video:$cameraxVersion")
    // MediaPipe Tasks Vision
    implementation("com.google.mediapipe:tasks-vision:0.10.21")// 請檢查最新版本


}