plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)

    id("com.google.gms.google-services")
    id("org.jetbrains.kotlin.kapt")
}

android {
    namespace = "vcmsa.projects.kloofhillcaringapp"
    compileSdk = 35

    defaultConfig {
        applicationId = "vcmsa.projects.kloofhillcaringapp"
        minSdk = 28
        targetSdk = 35
        versionCode = 2
        versionName = "2.0"

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
}

dependencies {

    implementation("com.github.bumptech.glide:glide:4.15.1")
    kapt("com.github.bumptech.glide:compiler:4.15.1")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    implementation("com.google.android.material:material:1.12.0")
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    implementation(platform("com.google.firebase:firebase-bom:34.1.0"))

    implementation("com.google.firebase:firebase-analytics")

    implementation("com.google.firebase:firebase-auth")

    implementation("com.google.firebase:firebase-firestore")

    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.1")
    implementation("androidx.browser:browser:1.6.0")

    implementation("com.squareup.picasso:picasso:2.8")

    implementation ("org.osmdroid:osmdroid-android:6.1.14")

    implementation ("org.osmdroid:osmdroid-mapsforge:6.1.14")
}