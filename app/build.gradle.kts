plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.hilt.android)
//    alias(libs.plugins.ksp)
    alias(libs.plugins.compose.compiler)
    id("kotlin-kapt")
}

android {
    namespace = "com.theralieve"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.theralieve"
        minSdk = 28
        targetSdk = 36
        versionCode = 3
        versionName = "3.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        getByName("debug") {
            storeFile = file("kozen.jks")
            storePassword = "kozen"
            keyAlias = "xc-buildsrv"
            keyPassword = "kozen"
            enableV1Signing = true
            enableV2Signing = true
        }
    }



    buildTypes {

        debug {
            signingConfig = signingConfigs.getByName("debug")
            isMinifyEnabled =  false
//            isShrinkResources = true   // keep false for faster debug builds
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

//    splits {
//        abi {
//            isEnable = true
//            reset()
//            include(  "armeabi-v7a", "arm64-v8a")
//            isUniversalApk = false
//        }
//    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlin {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11)
            freeCompilerArgs.addAll(
                "-Xjvm-default=all",
                "-Xskip-metadata-version-check"
            )
        }
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.11"
    }

    buildFeatures {
        compose = true
    }
}

dependencies {

    // Core
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)

    // Compose
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation("androidx.compose.material:material-icons-extended")
    implementation(libs.androidx.compose.foundation)

    // Lottie
    implementation("com.airbnb.android:lottie-compose:6.3.0")
    implementation("com.squareup.okio:okio:3.3.0")
    // Coil
    implementation(libs.coil.compose)
    implementation(libs.okhttp)

    // Modules
    implementation(project(":domain"))
    implementation(project(":data"))

    // Hilt
    implementation(libs.hilt.android)
    implementation(libs.androidx.compose.foundation.layout)
    kapt(libs.hilt.compiler)
    implementation(libs.hilt.navigation.compose)

    // Navigation
    implementation(libs.navigation.compose)

//    implementation(libs.aws.android.sdk.iot)
//    implementation(libs.aws.android.sdk.core)

    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    // Square / Payments
//    implementation(libs.mobile.payments.sdk) {
//        exclude(group = "androidx.metrics", module = "metrics-performance")
//    }

//    implementation(libs.mockreader.ui)
    implementation(libs.splash.screen)
//    implementation(libs.stripe.terminal)

    // ViewModel
    implementation(libs.lifecycle.viewmodel.compose)
    implementation(libs.invoke.dvpay.lite)

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))

    debugImplementation(libs.androidx.compose.ui.tooling)
}
