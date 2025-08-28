plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
}

android {
    namespace = "com.steadymate.core.ui"
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
    
    // Compose BOM
    val composeBom = platform(Dependencies.Compose.bom)
    implementation(composeBom)
    androidTestImplementation(composeBom)
    
    // AndroidX
    implementation(Dependencies.AndroidX.coreKtx)
    
    // Jetpack Compose
    implementation(Dependencies.Compose.ui)
    implementation(Dependencies.Compose.uiToolingPreview)
    implementation(Dependencies.Compose.material3)
    implementation(Dependencies.Compose.foundation)
    
    // Testing
    testImplementation(Dependencies.Testing.junit5Jupiter)
    androidTestImplementation(Dependencies.Testing.junitAndroidX)
    androidTestImplementation(Dependencies.Testing.composeUiTest)
    debugImplementation(Dependencies.Compose.uiTooling)
    debugImplementation(Dependencies.Compose.uiTestManifest)
}
