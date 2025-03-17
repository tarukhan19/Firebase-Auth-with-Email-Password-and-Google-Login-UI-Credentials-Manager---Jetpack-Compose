import org.jetbrains.kotlin.konan.properties.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.hilt)
    id("kotlin-kapt") // Required for Hilt compiler
    id("com.google.devtools.ksp") // required for KSP
    id("com.google.gms.google-services") // required for google services
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
        android.buildFeatures.buildConfig = true
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        /* to read api key from local.properties. Define API Keys inside local.properties and retrieve it here. in kotlin code,
        we can achieve the API key using BuildConfig.API
        BuildConfig file is generated during compile time.
        Normally we should define our API keys in server but this is one of other option to make it work locally. we should never
        define the API key inside classes.
         */
        val properties= Properties()
        properties.load(project.rootProject.file("local.properties").inputStream())
        buildConfigField ("String","API_KEY","\"${properties.getProperty("API_KEY")}\"")
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
    // datastore
    implementation(libs.androidx.datastore)
    //google-signIn
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth)
    implementation(libs.androidx.credential)
    implementation(libs.androidx.credential.playservice)
    implementation(libs.identity.googleid)

    // testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}