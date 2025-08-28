plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("com.google.dagger.hilt.android")
    kotlin("kapt")
}

android {
    namespace = "com.steadymate.core.domain"
    compileSdk = Dependencies.compileSdk

    defaultConfig {
        minSdk = Dependencies.minSdk
        
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    // Core modules
    implementation(project(":core:common"))
    
    // Hilt
    implementation(Dependencies.Hilt.android)
    kapt(Dependencies.Hilt.compiler)
    
    // Coroutines
    implementation(Dependencies.Coroutines.core)
    
    // Kotlinx DateTime
    implementation(Dependencies.Kotlinx.datetime)
    
    // Testing
    testImplementation(Dependencies.Testing.junit5Jupiter)
    testImplementation(Dependencies.Testing.mockk)
    testImplementation(Dependencies.Testing.coroutinesTest)
    androidTestImplementation(Dependencies.Testing.junitAndroidX)
}
