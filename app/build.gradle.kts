plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.hilt)
    id("kotlin-kapt") // Required for Hilt compiler
    id("com.google.devtools.ksp") // required for KSP
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.demo.userauth"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.demo.userauth"
        minSdk = 24
        targetSdk = 35
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
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
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    // extended icons like password visible/invisible
    implementation(libs.material.icon)
    // splash screen
    implementation(libs.androidx.splash)
    // hilt
    implementation(libs.hilt)
    ksp (libs.hilt.compiler)
    implementation(libs.hilt.navigation) // Ensure this is included
    // navigation
    implementation(libs.navigation.compose)
    implementation(libs.kotlinx.serialization.json)
    // Room Database
    implementation(libs.androidx.rooms.runtime)
    ksp (libs.androidx.rooms.compiler)
    implementation(libs.androidx.rooms.ktx)
    // testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}