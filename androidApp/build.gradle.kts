import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

plugins {
    alias(libs.plugins.com.android.application)
    alias(libs.plugins.org.jetbrains.kotlin.android)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.com.google.dagger.hilt.android)
    alias(libs.plugins.com.google.devtools.ksp)
    alias(libs.plugins.androidx.baselineprofile)
    id("kotlin-parcelize")
}
android {
    namespace = "com.m3u.androidApp"
    compileSdk = 35
    defaultConfig {
        applicationId = "net.tijorat.ipxtv"
        minSdk = 26
        targetSdk = 34
        versionCode = 12
        versionName = "1.1.2"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    flavorDimensions += setOf("channel", "codec")
    productFlavors {
        // Official
        create("stableChannel") {
            dimension = "channel"
            isDefault = true
        }
        // Github Workflow
        create("snapshotChannel") {
            dimension = "channel"
            versionNameSuffix =
                "-snapshot[${LocalDateTime.now().format(DateTimeFormatter.ofPattern("MMddHHmm"))}]"
            applicationIdSuffix = ".snapshot"
        }
        create("richCodec") {
            dimension = "codec"
            isDefault = true
        }
        create("liteCodec") {
            dimension = "codec"
            versionNameSuffix = "-lite"
        }
    }
    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            signingConfig = signingConfigs.getByName("debug")
        }
        all {
            isCrunchPngs = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    aaptOptions.cruncherEnabled = false
    splits {
        abi {
            val benchmark = project
                .properties
                .keys
                .find { it.contains("testInstrumentationRunnerArguments") } != null

            val snapshotChannel = gradle
                .startParameter
                .taskNames
                .find { it.contains("snapshotChannel", ignoreCase = true) } != null

            val richCodec = gradle
                .startParameter
                .taskNames
                .find { it.contains("richCodec", ignoreCase = true) } != null

            isEnable = !benchmark && !snapshotChannel && richCodec

            reset()
            include("x86", "x86_64", "arm64-v8a", "armeabi-v7a")
            isUniversalApk = true
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    packaging {
        resources.excludes += "META-INF/**"
    }
    applicationVariants.all {
        outputs
            .map { it as com.android.build.gradle.internal.api.ApkVariantOutputImpl }
            .forEach { output ->
                val abi = output.getFilter("ABI") ?: "universal"
                val versionName = output.versionNameOverride
                output.outputFileName = "${versionName}_$abi.apk"
            }
    }
}

hilt {
    enableAggregatingTask = true
}

baselineProfile {
    dexLayoutOptimization = true
    saveInSrc = true
    automaticGenerationDuringBuild = true
}

dependencies {
    implementation(project(":core"))
    implementation(project(":ui"))
    implementation(project(":feature:foryou"))
    implementation(project(":feature:films"))
    implementation(project(":feature:favorite"))
    implementation(project(":feature:setting"))
    implementation(project(":feature:playlist"))
    implementation(project(":feature:channel"))
    implementation(project(":feature:playlist-configuration"))
    implementation(project(":feature:crash"))
    implementation(libs.androidx.profileinstaller)
    "baselineProfile"(project(":baselineprofile"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.startup.runtime)

    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.process)

    implementation(libs.androidx.core.splashscreen)

    implementation(libs.google.dagger.hilt)
    ksp(libs.google.dagger.hilt.compiler)
    implementation(libs.androidx.hilt.navigation.compose)

    implementation(libs.androidx.work.runtime.ktx)
    ksp(libs.androidx.hilt.compiler)
    implementation(libs.androidx.hilt.work)

    implementation(libs.androidx.glance.appwidget)
    implementation(libs.androidx.glance.material3)

    debugImplementation(libs.squareup.leakcanary)

    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.google.zxing:core:3.5.0")

}
