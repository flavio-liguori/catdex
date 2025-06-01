plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("com.google.gms.google-services")
}

// Exclure com.intellij:annotations partout et forcer la version JetBrains
configurations.all {
    exclude(group = "com.intellij", module = "annotations")
    resolutionStrategy {
        force("org.jetbrains:annotations:23.0.0")
    }
}

android {
    namespace = "com.example.catdex"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.catdex"
        minSdk = 24
        targetSdk = 35
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
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    // Firebase (BOM + modules sans version explicite)
    implementation(platform("com.google.firebase:firebase-bom:32.3.1"))
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.firebase:firebase-firestore-ktx") {
        exclude(group = "com.google.firebase", module = "firebase-common")
    }
    implementation("com.google.firebase:firebase-storage-ktx") {
        exclude(group = "com.google.firebase", module = "firebase-common")
    }
    implementation("com.google.firebase:firebase-database-ktx") {
        exclude(group = "com.google.firebase", module = "firebase-common")
    }

    // AndroidX Core & Lifecycle & Compose Activity
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)

    // Compose BOM
    implementation(platform(libs.androidx.compose.bom))

    // Compose UI (core, graphics, tooling-preview)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)

    // Compose Foundation (BOM gère la version)
    implementation("androidx.compose.foundation:foundation")

    // Material 3
    implementation(libs.androidx.material3)

    // Material Icons Extended (BOM gère la version)
    implementation("androidx.compose.material:material-icons-extended")

    // AppCompat
    implementation(libs.androidx.appcompat)

    // Coil pour Jetpack Compose
    implementation("io.coil-kt:coil-compose:2.4.0")

    // Navigation Compose
    implementation("androidx.navigation:navigation-compose:2.5.3")

    // Accompanist Pager
    implementation("com.google.accompanist:accompanist-pager:0.34.0")

    // CameraX
    implementation("androidx.camera:camera-core:1.2.3")
    implementation("androidx.camera:camera-camera2:1.2.3")
    implementation("androidx.camera:camera-lifecycle:1.2.3")
    implementation("androidx.camera:camera-view:1.2.3")

    // Média (ex. Media3)
    implementation(libs.androidx.media3.common.ktx)

    // Room Compiler
    implementation(libs.androidx.room.compiler)
    implementation(libs.androidx.compilercommon)
    implementation(libs.play.services.location)
    implementation(libs.play.services.maps)

    // Tests
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)

    // Debug / Tooling
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}
