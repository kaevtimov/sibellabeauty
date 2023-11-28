plugins {
    id("com.android.application")
    id("kotlin-android")
    id("com.google.gms.google-services")
    kotlin("kapt")
    id("com.google.dagger.hilt.android")
}

android {
    namespace = "com.example.sibellabeauty"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.sibellabeauty"
        minSdk = 26
        targetSdk = 34
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
    implementation(libs.google.android.material)

    implementation(libs.kotlin.core)
    implementation(libs.androidx.appcompat)
    implementation(project(mapOf("path" to ":common")))
    kapt(libs.hilt.kapt)
    implementation(libs.hilt.android)
    // Firebase
    implementation(platform(libs.google.firebase.bom))
    implementation(libs.google.firebase.installations)
    implementation(libs.google.firebase.common)
    implementation(libs.bundles.compose)
    implementation(project(":ui"))
}
