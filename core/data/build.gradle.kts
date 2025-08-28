plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("com.google.dagger.hilt.android")
    kotlin("kapt")
}

android {
    namespace = "com.steadymate.core.data"
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
    implementation(project(":core:domain"))
    implementation(project(":core:common"))
    
    // AndroidX
    implementation(Dependencies.AndroidX.coreKtx)
    
    // Hilt
    implementation(Dependencies.Hilt.android)
    kapt(Dependencies.Hilt.compiler)
    
    // Room
    implementation(Dependencies.Room.runtime)
    implementation(Dependencies.Room.ktx)
    kapt(Dependencies.Room.compiler)
    
    // DataStore
    implementation(Dependencies.DataStore.preferences)
    implementation(Dependencies.DataStore.proto)
    
    // Coroutines
    implementation(Dependencies.Coroutines.core)
    implementation(Dependencies.Coroutines.android)
    
    // Testing
    testImplementation(Dependencies.Testing.junit5Jupiter)
    testImplementation(Dependencies.Testing.mockk)
    testImplementation(Dependencies.Testing.coroutinesTest)
    androidTestImplementation(Dependencies.Testing.junitAndroidX)
}
