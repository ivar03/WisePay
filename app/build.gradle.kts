plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
}

repositories {
    google()
    mavenCentral()
    gradlePluginPortal()
    maven ( url = "https://jitpack.io" )
}

android {
    namespace = "com.ivar7284.rbi_pay"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.ivar7284.rbi_pay"
        minSdk = 24
        targetSdk = 34
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
        //noinspection DataBindingWithoutKapt
        dataBinding = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.camera.core)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // custom dependencies
    implementation("androidx.vectordrawable:vectordrawable:1.2.0")
    implementation("androidx.legacy:legacy-support-v4:1.0.0")
    implementation("androidx.cardview:cardview:1.0.0")
    implementation("com.github.bumptech.glide:glide:4.12.0")
    implementation("com.android.volley:volley:1.2.1")
    implementation("com.journeyapps:zxing-android-embedded:4.3.0")
    implementation("com.google.zxing:core:3.5.3")
    implementation("com.google.android.material:material:1.5.0") // Ensure the correct version is used
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.9.1")
    implementation("br.com.simplepass:loading-button-android:2.2.0")
    implementation("androidx.viewpager2:viewpager2:1.0.0")
    implementation("org.jetbrains.kotlinx:atomicfu:0.23.1")
    implementation("io.ktor:ktor-client-core:2.2.0")
    implementation("io.ktor:ktor-client-cio:2.2.0")
    implementation("io.ktor:ktor-client-content-negotiation:2.2.0")
    implementation("io.ktor:ktor-serialization-kotlinx-json:2.2.0")
    implementation("de.hdodenhof:circleimageview:3.1.0")
    implementation("com.google.mlkit:barcode-scanning:16.1.0")
    implementation("androidx.camera:camera-camera2:1.1.0")
    implementation("androidx.camera:camera-lifecycle:1.1.0")
    implementation("androidx.camera:camera-view:1.1.0-alpha09")
    implementation ("androidx.biometric:biometric:1.1.0")
}
