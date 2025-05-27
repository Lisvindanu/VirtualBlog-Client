plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.hilt.android) // Added for Hilt
    alias(libs.plugins.ksp)          // Added for KSP
    alias(libs.plugins.kotlin.serialization) // Added for Kotlinx Serialization
}

android {
    namespace = "com.virtualsblog.project"
    compileSdk = 35 // Using Android 15 Preview. Ensure your AGP/Kotlin/Studio supports this.
    // Consider `compileSdkPreview = "VanillaIceCream"` with appropriate AGP.
    // Or use 34 for the latest stable Android.

    defaultConfig {
        applicationId = "com.virtualsblog.project"
        minSdk = 24
        targetSdk = 35 // Matches compileSdk
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "com.virtualsblog.project.CustomTestRunner" // For Hilt UI tests (see note below)
        // or for basic tests: "androidx.test.runner.AndroidJUnitRunner"
        // If using Hilt for UI tests, you'll need a custom runner or HiltTestApplication.
    }

    buildTypes {
        release {
            isMinifyEnabled = false // Consider true for release builds
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11 // Consider JavaVersion.VERSION_17 for modern projects
        targetCompatibility = JavaVersion.VERSION_11 // Consider JavaVersion.VERSION_17 for modern projects
    }
    kotlinOptions {
        jvmTarget = "11" // Consider "17" if you update compileOptions
    }
    buildFeatures {
        compose = true
    }
    // It's good practice to define composeOptions if you are using Compose,
    // especially if you need to specify a particular Kotlin Compiler Extension version.
    // However, the BOM often handles this. If you face issues, you might need:
    // composeOptions {
    //     kotlinCompilerExtensionVersion = libs.versions.composeCompiler.get() // Define composeCompiler in libs.versions.toml
    // }
}

dependencies {
    // Core & Lifecycle
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)

    // Compose
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.compose.navigation) // Added for Navigation Compose
    implementation(libs.androidx.lifecycle.viewmodel.compose) // For ViewModel with Compose

    // Hilt (Dependency Injection)
    implementation(libs.hilt.android)
    implementation(libs.androidx.runner)
    implementation(libs.hilt.android.testing)
    implementation(libs.androidx.ui.test.junit4.android)
    ksp(libs.hilt.compiler)
    implementation(libs.hilt.navigation.compose) // Hilt integration with Navigation Compose

    // Coroutines
    implementation(libs.kotlinx.coroutines.android)

    // Room (Database) - Example usage with KSP
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)

    // Kotlinx Serialization (JSON)
    implementation(libs.kotlinx.serialization.json)

    // DataStore
    implementation(libs.datastore.preferences)

    // Image Loading
    implementation(libs.coil.compose)

    // Networking (Example)
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.gson)
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging.interceptor) // Useful for debugging

    // Unit Tests
    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.truth) // For fluent assertions
    testImplementation(libs.mockito.core)
    testImplementation(libs.mockito.kotlin)
    testImplementation(libs.arch.core.testing) // <-- CORRECTED LINE: For testing LiveData/ViewModels

    // Android Instrumented Tests (Espresso for Views & Compose UI Tests)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core) // For View-based UI tests
    //androidTestImplementation(libs.androidx.espresso.contrib) // If you need RecyclerView actions etc.
    //androidTestImplementation(libs.androidx.espresso.intents) // For testing intents
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4) // For Compose UI tests
    androidTestImplementation(libs.kotlinx.coroutines.test)
    implementation(libs.androidx.material.icons.extended)

    // Hilt Testing
    androidTestImplementation(libs.hilt.android.testing)
    kspAndroidTest(libs.hilt.compiler) // Hilt compiler for AndroidTest

    // Debugging
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    debugImplementation(libs.chucker) // HTTP inspector
}