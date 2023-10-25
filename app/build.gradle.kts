plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-kapt")
    id("com.google.gms.google-services")
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

    implementation(platform(libs.google.firebase.bom))
    implementation(libs.google.firebase.database)
    implementation(libs.kotlin.coroutines.playservices)

    implementation(libs.kotlin.core)
    implementation(libs.androidx.appcompat)
    implementation(libs.google.android.material)
    implementation(libs.compose.material3)
    implementation(libs.compose.material.windows)

    // Jetpack Compose
    implementation(libs.compose.ui)
    // Tooling support (Previews, etc.)
    implementation(libs.compose.ui.tooling)
    // Foundation (Border, Background, Box, Image, Scroll, shapes, animations, etc.)
    implementation(libs.compose.foundation)
    // Material Design
    implementation(libs.compose.material)
    // Material design icons
    implementation(libs.compose.iconsExtended)
    // UI Tests
    androidTestImplementation(libs.compose.ui.test)
    // Activity
    implementation(libs.compose.activity)

    implementation(libs.androidx.fragment)
    implementation(libs.androidx.activity)
    implementation(libs.compose.constraintLayout)
    implementation(libs.androidx.preference)
}
