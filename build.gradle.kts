// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version Dependencies.agpVersion apply false
    id("org.jetbrains.kotlin.android") version Dependencies.kotlinVersion apply false
    id("org.jetbrains.kotlin.plugin.compose") version Dependencies.kotlinVersion apply false
    id("com.android.library") version Dependencies.agpVersion apply false
    id("com.google.dagger.hilt.android") version Dependencies.hilt apply false
    id("com.google.protobuf") version Dependencies.protobufPlugin apply false
    kotlin("kapt") version Dependencies.kotlinVersion apply false
    kotlin("plugin.serialization") version Dependencies.kotlinVersion apply false
    id("com.google.gms.google-services") version "4.4.3" apply false
    id("com.google.firebase.crashlytics") version "3.0.6" apply false
}

tasks.register("clean", Delete::class) {
    delete(rootProject.layout.buildDirectory)
}
