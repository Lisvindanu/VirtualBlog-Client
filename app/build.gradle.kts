import java.util.Properties
import java.io.FileInputStream

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
    compileSdk = 35

    buildFeatures {
        compose = true
        buildConfig = true
    }

    // SIGNING CONFIG untuk Release
    signingConfigs {
        create("release") {
            storeFile = file("../my-release-key.keystore") // Path ke keystore
            storePassword = "DevGuerilla"
            keyAlias = "my-key-alias"
            keyPassword = "DevGuerilla"
        }
    }

    defaultConfig {
        applicationId = "com.virtualsblog.project"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0.0"

        testInstrumentationRunner = "com.virtualsblog.project.CustomTestRunner"

        // API Key configuration
        val localProperties = Properties()
        val localPropertiesFile = rootProject.file("local.properties")
        if (localPropertiesFile.exists()) {
            localProperties.load(FileInputStream(localPropertiesFile))
        }

        val apiKey = localProperties.getProperty("API_KEY") ?:
        System.getenv("API_KEY") ?:
        "NpeW7lQ2SlZUCC9mI4G7E26NMRtoK8mW"

        buildConfigField("String", "API_KEY", "\"$apiKey\"")
    }

    buildTypes {
        debug {
            isDebuggable = true
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-debug"

            val localProperties = Properties()
            val localPropertiesFile = rootProject.file("local.properties")
            if (localPropertiesFile.exists()) {
                localProperties.load(FileInputStream(localPropertiesFile))
            }

            val apiKey = localProperties.getProperty("API_KEY") ?:
            System.getenv("API_KEY") ?:
            "NpeW7lQ2SlZUCC9mI4G7E26NMRtoK8mW"

            buildConfigField("String", "API_KEY", "\"$apiKey\"")
        }

        release {
            isMinifyEnabled = true
            isShrinkResources = true
            isDebuggable = false

            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )

            // GUNAKAN SIGNING CONFIG
            signingConfig = signingConfigs.getByName("release")

            // API Key untuk production
            val apiKey = System.getenv("PROD_API_KEY") ?:
            System.getenv("API_KEY") ?:
            "NpeW7lQ2SlZUCC9mI4G7E26NMRtoK8mW"

            buildConfigField("String", "API_KEY", "\"$apiKey\"")
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
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.14"
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

            // JUnit conflict fixes
            excludes += "**/META-INF/LICENSE-notice.md"
            excludes += "/META-INF/LICENSE-notice.md"
            excludes += "**/META-INF/LICENSE-notice.txt"
            excludes += "/META-INF/LICENSE-notice.txt"
            excludes += "**/META-INF/junit-platform.properties"
            excludes += "**/META-INF/io.netty.versions.properties"
            excludes += "**/META-INF/gradle-plugins/**"
            excludes += "**/META-INF/native-image/**"
            excludes += "**/META-INF/*.kotlin_module"
            excludes += "**/META-INF/versions/**"
            excludes += "**/META-INF/maven/**"
            excludes += "**/META-INF/services/**"
            excludes += "**/META-INF/extensions.idx"
            excludes += "**/META-INF/INDEX.LIST"
            excludes += "**/OSGI-INF/**"
        }
    }
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

    // Pull to refresh
    implementation("androidx.compose.material:material")

    // Hilt - Dependency Injection
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.hilt.navigation.compose)

    // Coroutines
    implementation(libs.kotlinx.coroutines.android)

    // Room - Local Database
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)

    // Kotlinx Serialization
    implementation(libs.kotlinx.serialization.json)

    // DataStore - Preferences
    implementation(libs.datastore.preferences)

    // Image Loading - Coil
    implementation(libs.coil.compose)

    // Networking - Retrofit & OkHttp
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.gson)
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging.interceptor)

    // Image picker
    implementation(libs.androidx.activity.compose.v182)

    // Unit Tests
    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.truth)
    testImplementation(libs.mockito.core)
    testImplementation(libs.mockito.kotlin)
    testImplementation(libs.arch.core.testing)

    // Android Instrumented Tests
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    androidTestImplementation(libs.androidx.ui.test.junit4.android)
    androidTestImplementation(libs.kotlinx.coroutines.test)
    androidTestImplementation(libs.androidx.runner)
    androidTestImplementation(libs.core.ktx)
    androidTestImplementation(libs.hilt.android.testing)
    androidTestImplementation(libs.mockk.android)
    androidTestImplementation(libs.mockk.agent)
    testImplementation(libs.mockk)
    kspAndroidTest(libs.hilt.compiler)

    // Debugging
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    debugImplementation(libs.chucker)
}