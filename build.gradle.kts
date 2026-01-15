// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.jetbrainsKotlinAndroid) apply false
    alias(libs.plugins.googleGmsGoogleServices) apply false
    alias(libs.plugins.googleFirebaseCrashlytics) apply false
}
buildscript {
    repositories {
        google()
        mavenCentral()
    }

    dependencies {
        classpath("com.android.tools.build:gradle:8.6.0") // Use the appropriate version
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.9.0") // Use the appropriate version
        classpath("com.google.dagger:hilt-android-gradle-plugin:2.48") // Use the latest version
    }
}




