plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.haluuvananh.ecommerce_app_v1"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.haluuvananh.ecommerce_app_v1"
        minSdk = 22
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
}

dependencies {

    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")


    // Slide image
    implementation("com.github.denzcoskun:ImageSlideshow:0.1.2")

    // Firebase
    implementation("com.google.firebase:firebase-auth:22.3.1")
    implementation("com.google.firebase:firebase-database:20.3.1")
    implementation("com.google.firebase:firebase-firestore:24.11.1")
    implementation("com.google.firebase:firebase-storage:20.3.0")

    // Glide for Category
    implementation("com.github.bumptech.glide:glide:4.16.0")

    // Razorpay for Payment SDK
    implementation("com.razorpay:checkout:1.6.20")


    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    // Annotation
    implementation("androidx.annotation:annotation:1.7.1")
}