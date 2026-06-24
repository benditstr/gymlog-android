plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "de.th.nuernberg.bme.gymlog"
    compileSdk {
        version = release(36) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        applicationId = "de.th.nuernberg.bme.gymlog"
        minSdk = 24
        targetSdk = 36
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    // ── Standard Android ──────────────────────────────────────
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.2.1")

    // ── Navigation Component ───────────────────────────────────
    implementation("androidx.navigation:navigation-fragment:2.8.9")
    implementation("androidx.navigation:navigation-ui:2.8.9")

    // ── ViewModel + LiveData ───────────────────────────────────
    implementation("androidx.lifecycle:lifecycle-viewmodel:2.8.7")
    implementation("androidx.lifecycle:lifecycle-livedata:2.8.7")

    // ── Room Datenbank ─────────────────────────────────────────
    implementation("androidx.room:room-runtime:2.7.0")
    annotationProcessor("androidx.room:room-compiler:2.7.0")

    // ── MPAndroidChart ─────────────────────────────────────────
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")

    // ── BlurView (echter Backdrop-Blur für floatende Bottom-Nav) ─
    implementation("com.github.Dimezis:BlurView:version-3.2.0")

    // ── Tests ──────────────────────────────────────────────────
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
    androidTestImplementation("androidx.room:room-testing:2.7.0")
    androidTestImplementation("androidx.arch.core:core-testing:2.2.0")
}