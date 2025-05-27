// This file configures repository settings for plugin resolution and dependency resolution.
// It also defines the project structure (included modules).

pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal() // For community Gradle plugins
    }
}

dependencyResolutionManagement {
    // FAIL_ON_PROJECT_REPOS will fail the build if any project declares a repository directly.
    // It encourages centralized repository declaration in settings.gradle.kts.
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google() // Google's Maven repository
        mavenCentral() // Maven Central repository
        // jcenter() // JCenter is deprecated and should be removed if present
        // maven { url = uri("https://jitpack.io") } // Example for JitPack if needed
    }
}

rootProject.name = "VirtualsBlog"
include(":app") // Includes the 'app' module in the build
