plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    kotlin("kapt")
    id("com.google.dagger.hilt.android")
}

android {
    namespace = "com.evtimov.ui"
    compileSdk = 34

    defaultConfig {
        minSdk = 26

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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

    implementation(libs.kotlin.core)
    implementation(platform(libs.kotlin.bom))
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
    kapt(libs.hilt.kapt)
    implementation(libs.hilt.android)
    implementation(libs.hilt.compose)
    implementation(libs.lifecycle.compose)

    implementation(project(":domain"))
    implementation(project(":common"))
}