plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
    id("com.google.dagger.hilt.android")
    kotlin("kapt")
}

android {
    namespace = "com.steadymate.feature.home"
    compileSdk = Dependencies.compileSdk

    defaultConfig {
        minSdk = Dependencies.minSdk
        
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    
    kotlinOptions {
        jvmTarget = "11"
    }
    
    buildFeatures {
        compose = true
    }
}

dependencies {
    // Core modules
    implementation(project(":core:common"))
    implementation(project(":core:domain"))
    implementation(project(":core:ui"))
    
    // Compose BOM
    val composeBom = platform(Dependencies.Compose.bom)
    implementation(composeBom)
    androidTestImplementation(composeBom)
    
    // AndroidX
    implementation(Dependencies.AndroidX.coreKtx)
    implementation(Dependencies.AndroidX.lifecycleViewmodelCompose)
    
    // Jetpack Compose
    implementation(Dependencies.Compose.ui)
    implementation(Dependencies.Compose.uiToolingPreview)
    implementation(Dependencies.Compose.material3)
    
    // Navigation Compose
    implementation(Dependencies.Navigation.compose)
    implementation(Dependencies.Navigation.hilt)
    
    // Hilt
    implementation(Dependencies.Hilt.android)
    kapt(Dependencies.Hilt.compiler)
    
    // Testing
    testImplementation(Dependencies.Testing.junit5Jupiter)
    testImplementation(Dependencies.Testing.mockk)
    androidTestImplementation(Dependencies.Testing.junitAndroidX)
    androidTestImplementation(Dependencies.Testing.composeUiTest)
    debugImplementation(Dependencies.Compose.uiTooling)
    debugImplementation(Dependencies.Compose.uiTestManifest)
}
