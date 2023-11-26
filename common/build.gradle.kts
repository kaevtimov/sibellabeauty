plugins {
    id("com.android.application")
    id("kotlin-android")
    id("com.google.gms.google-services")
    kotlin("kapt")
    id("com.google.dagger.hilt.android")
}

android {
    namespace = "com.example.sibellabeauty"
    compileSdk = 33

    defaultConfig {
        applicationId = "com.example.sibellabeauty"
        minSdk = 26
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        compose = true
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.2.0"
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    composeOptions {
        val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")
        kotlinCompilerExtensionVersion = libs.findVersion("composeCompiler").get().toString()
    }
}

dependencies {

    implementation(libs.kotlin.coroutines.playservices)
    implementation(libs.kotlin.core)
    implementation(libs.androidx.preference)
    kapt(libs.hilt.kapt)
    implementation(libs.hilt.android)
    implementation(libs.google.firebase.installations)
    implementation(libs.androidx.security)
}
