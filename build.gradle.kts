buildscript {
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
        maven { url = uri("https://oss.sonatype.org/content/repositories/snapshots/") }
        // Add for BlackReflection
        maven { url = uri("https://raw.githubusercontent.com/CodingGay/BlackReflection/repo") }
    }

    dependencies {
        classpath("org.lsposed.lsparanoid:org.lsposed.lsparanoid.gradle.plugin:0.5.2")
    }
}

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.jetbrains.kotlin.android) apply false
    alias(libs.plugins.jetbrains.kotlin.kapt) apply false
    alias(libs.plugins.hilt.android) apply false
}

tasks.register<Delete>("clean") {
    delete(rootProject.layout.buildDirectory)
}