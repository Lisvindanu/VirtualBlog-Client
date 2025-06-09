plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.hilt.android)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.virtualsblog.project"
    compileSdk = 35 // Required by androidx.compose.ui:ui-test-junit4-android:1.8.2

    buildFeatures {
        compose = true
        buildConfig = true // Aktifkan BuildConfig
    }

    defaultConfig {
        applicationId = "com.virtualsblog.project"
        minSdk = 24
        targetSdk = 35 // Should match compileSdk
        versionCode = 1
        versionName = "1.0"


        testInstrumentationRunner = "com.virtualsblog.project.CustomTestRunner"
        buildConfigField("String", "API_KEY", "\"${System.getenv("API_KEY") ?: project.findProperty("API_KEY")}\"")

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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
        // viewBinding = true // If you were using ViewBinding
        // dataBinding = true // If you were using DataBinding
    }
    composeOptions {
        // This line is usually not needed when the Kotlin Compose plugin version is aligned with your Kotlin version.
        // The plugin (version 2.1.20 here) will use a compatible compiler by default.
        // kotlinCompilerExtensionVersion = libs.versions.composeCompiler.get()
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            excludes += "/META-INF/LICENSE.md"
            excludes += "/META-INF/LICENSE.txt"
            excludes += "/META-INF/NOTICE.md"
            excludes += "/META-INF/NOTICE.txt"
            excludes += "/META-INF/ASL2.0"
            excludes += "/META-INF/LGPL2.1"
            excludes += "**/META-INF/LICENSE.md"
            excludes += "**/META-INF/LICENSE"
            excludes += "**/META-INF/NOTICE"
            excludes += "**/META-INF/DEPENDENCIES"
        }
    }
    // Lint options from your original file
    lint {
        disable.add("NullSafeMutableLiveData")
    }
}

dependencies {
    // Core & Lifecycle
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)

    // UI & Compose
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.material.icons.extended)

    // Pull to refresh - Material 1 implementation (androidx.compose.material.pullrefresh.*)
    implementation("androidx.compose.material:material")

    // Hilt - Dependency Injection
    implementation(libs.hilt.android)
//    implementation(libs.androidx.runner)
    ksp(libs.hilt.compiler) // Hilt's KSP compiler
    implementation(libs.hilt.navigation.compose) // Hilt integration with Jetpack Navigation Compose

    // Coroutines
    implementation(libs.kotlinx.coroutines.android)

    // Room - Local Database
    implementation(libs.room.runtime)
    implementation(libs.room.ktx) // Kotlin Extensions for Room
    ksp(libs.room.compiler) // Room's KSP compiler

    // Kotlinx Serialization
    implementation(libs.kotlinx.serialization.json)

    // DataStore - Preferences
    implementation(libs.datastore.preferences)

    // Image Loading - Coil
    implementation(libs.coil.compose)

    // Networking - Retrofit & OkHttp
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.gson) // Gson converter for Retrofit
    implementation(libs.okhttp) // OkHttp client
    implementation(libs.okhttp.logging.interceptor) // OkHttp logging interceptor

    // Add image picker and upload functionality
    implementation(libs.androidx.activity.compose.v182)

    // Unit Tests (run on local JVM)
    testImplementation(libs.junit) // Standard JUnit4
    testImplementation(libs.kotlinx.coroutines.test) // For testing coroutines
    testImplementation(libs.truth) // Google's Truth assertion library
    testImplementation(libs.mockito.core) // Mockito for mocking
    testImplementation(libs.mockito.kotlin) // Mockito-Kotlin integration
    testImplementation(libs.arch.core.testing) // For testing Architecture Components (LiveData, etc.)
    // testImplementation(libs.robolectric) // If using Robolectric

    // Android Instrumented Tests (run on an Android device or emulator)
    androidTestImplementation(libs.androidx.junit) // androidx.test.ext:junit (uses junitVersion from libs.versions.toml)
    // androidTestImplementation(libs.androidx.junit.ktx) // If you have androidx-junit-ktx alias
    androidTestImplementation(libs.androidx.espresso.core) // Espresso for UI testing (uses espressoCore from libs.versions.toml)
    androidTestImplementation(platform(libs.androidx.compose.bom)) // Align Compose test versions with BOM
    androidTestImplementation(libs.androidx.ui.test.junit4) // Base Compose UI testing
    androidTestImplementation(libs.androidx.ui.test.junit4.android) // Android specific Compose UI testing (uses uiTestJunit4Android)
    androidTestImplementation(libs.kotlinx.coroutines.test) // For testing coroutines in instrumented tests
    androidTestImplementation(libs.androidx.runner) // AndroidX Test Runner


    androidTestImplementation(libs.core.ktx)
    // Hilt Testing (for instrumented tests)
    androidTestImplementation(libs.hilt.android.testing)

//    androidTestImplementation(libs.androidx.appcompat)
    androidTestImplementation(libs.mockk.android)
    androidTestImplementation(libs.mockk.agent)

    // Jika perlu MockK untuk unit tests juga
    testImplementation(libs.mockk)

    kspAndroidTest(libs.hilt.compiler) // Hilt KSP compiler for AndroidTest

    // Debugging - Only included in debug builds
    debugImplementation(libs.androidx.ui.tooling) // Compose UI tooling for Layout Inspector, etc.
    debugImplementation(libs.androidx.ui.test.manifest) // Compose test manifest
    debugImplementation(libs.chucker) // Chucker for HTTP inspection
    // releaseImplementation(libs.chucker.no.op) // No-op version of Chucker for release builds
}
