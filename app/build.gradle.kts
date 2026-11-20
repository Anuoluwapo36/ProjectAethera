
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

// Secrets and environment-specific values come from local.properties (not committed),
// then from Gradle properties or environment variables, so nothing sensitive lands in git.
val localProperties = Properties().apply {
    val localPropsFile = rootProject.file("local.properties")
    if (localPropsFile.exists()) {
        localPropsFile.inputStream().use { stream -> load(stream) }
    }
}

fun resolveSetting(name: String, fallback: String): String =
    (localProperties.getProperty(name)
        ?: project.findProperty(name)?.toString()
        ?: System.getenv(name)
        ?: fallback)

android {
    namespace = "com.example.projectaethera"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.projectaethera"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField(
            "String",
            "API_KEY",
            "\"${resolveSetting("AETHERA_API_KEY", "")}\""
        )
        buildConfigField(
            "String",
            "REAL_DEVICE_HOST",
            "\"${resolveSetting("AETHERA_DEV_HOST", "http://10.0.2.2:8000")}\""
        )
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
        buildConfig = true
    }
}

dependencies {
//added dependencies start here

    // MVVM & Lifecycle
    implementation(libs.viewmodel)
    implementation(libs.livedata)

    // Retrofit (Network)
    implementation(libs.retrofit)
    implementation(libs.convertergson)

    // Glide - Image Loading Library
    implementation(libs.glide)
    annotationProcessor(libs.glidecompiler)

    implementation("androidx.camera:camera-core:1.3.0")
    implementation("androidx.camera:camera-camera2:1.3.0")
    implementation("androidx.camera:camera-lifecycle:1.3.0")
    implementation("androidx.camera:camera-view:1.3.0")

    // Room Database
    implementation(libs.room.runtime)
    annotationProcessor(libs.room.compiler)

    //RecyclerView
    implementation(libs.recyclerview)
    
    //okhttp
    implementation(libs.okhttp)

    //appcompat
    implementation(libs.appcompat)
    //google materials
    implementation(libs.material)

    //constraintlayout
    implementation(libs.constraintlayout)

    implementation("androidx.core:core-ktx:1.15.0")


    //facebook shimmer skeleton loading  dependency
    implementation("com.facebook.shimmer:shimmer:0.5.0")

    // Lottie cartoon animations on auth screen
    implementation("com.airbnb.android:lottie:6.6.2")


    //Default dependency

    implementation(libs.swiperFresh)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}