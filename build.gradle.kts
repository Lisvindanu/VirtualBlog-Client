// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.hilt.android) apply false // Added for Hilt
    alias(libs.plugins.ksp) apply false          // Added for KSP
    alias(libs.plugins.kotlin.serialization) apply false // Added for Kotlinx Serialization
}