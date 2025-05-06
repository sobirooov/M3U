plugins {
    alias(libs.plugins.com.android.library)
    alias(libs.plugins.org.jetbrains.kotlin.android)
    alias(libs.plugins.com.google.devtools.ksp)
    alias(libs.plugins.com.google.dagger.hilt.android)
    alias(libs.plugins.compose.compiler)
}

android {
    namespace = "com.m3u.feature.films"
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
    }
    packaging {
        resources.excludes += "META-INF/**"
    }
}

dependencies {
    // Core and UI dependencies
    implementation(project(":core"))
    implementation(project(":ui"))

    // AndroidX
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose)

    // Hilt for Dependency Injection
    implementation(libs.google.dagger.hilt)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.androidx.foundation.android)
    ksp(libs.google.dagger.hilt.compiler)

    // Retrofit for API calls
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation ("com.squareup.okhttp3:logging-interceptor:5.0.0-alpha.11")

    implementation("com.google.zxing:core:3.5.0")

    // Coil for image loading
    implementation("io.coil-kt:coil-compose:2.0.0")

    // WebView for embedded YouTube trailers
    implementation("androidx.webkit:webkit:1.6.0")

    // Add YouTube Player dependency
    implementation("com.pierfrancescosoffritti.androidyoutubeplayer:core:12.1.0")

    // Compose Material3
    implementation(libs.androidx.compose.material3)
}
