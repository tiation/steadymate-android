plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.steadymate.core.common"
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
    // AndroidX
    implementation(Dependencies.AndroidX.coreKtx)
    
    // Coroutines
    implementation(Dependencies.Coroutines.core)
    implementation(Dependencies.Coroutines.android)
    
    // Kotlinx DateTime
    implementation(Dependencies.Kotlinx.datetime)
    
    // Testing
    testImplementation(Dependencies.Testing.junit5Jupiter)
    testImplementation(Dependencies.Testing.coroutinesTest)
    androidTestImplementation(Dependencies.Testing.junitAndroidX)
}
