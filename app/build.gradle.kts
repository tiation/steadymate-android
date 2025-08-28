plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.dagger.hilt.android")
    id("com.google.protobuf")
    kotlin("kapt")
    kotlin("plugin.serialization")
}

android {
    namespace = "com.steadymate.app"
    compileSdk = Dependencies.compileSdk

    defaultConfig {
        applicationId = "com.steadymate.app"
        minSdk = Dependencies.minSdk
        targetSdk = Dependencies.targetSdk
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
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
    
    composeOptions {
        kotlinCompilerExtensionVersion = Dependencies.kotlinCompilerExtension
    }
    
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

// Protobuf configuration for DataStore
protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:${Dependencies.protobuf}"
    }
    generateProtoTasks {
        all().forEach { task ->
            task.builtins {
                create("java") {
                    option("lite")
                }
            }
        }
    }
}

dependencies {
    // Compose BOM - manages all Compose library versions
    val composeBom = platform(Dependencies.Compose.bom)
    implementation(composeBom)
    androidTestImplementation(composeBom)

    // Core AndroidX
    implementation(Dependencies.AndroidX.coreKtx)
    implementation(Dependencies.AndroidX.appcompat)
    implementation(Dependencies.AndroidX.lifecycleRuntimeKtx)
    implementation(Dependencies.AndroidX.lifecycleViewmodelCompose)
    implementation(Dependencies.AndroidX.lifecycleRuntimeCompose)
    implementation(Dependencies.AndroidX.activityCompose)

    // Jetpack Compose
    implementation(Dependencies.Compose.ui)
    implementation(Dependencies.Compose.uiToolingPreview)
    implementation(Dependencies.Compose.material3)
    implementation(Dependencies.Compose.material3WindowSizeClass)
    implementation(Dependencies.Compose.runtime)
    implementation(Dependencies.Compose.runtimeLivedata)
    implementation(Dependencies.Compose.foundation)
    
    // Navigation Compose
    implementation(Dependencies.Navigation.compose)
    implementation(Dependencies.Navigation.hilt)
    
    // Accompanist
    implementation(Dependencies.Accompanist.systemUiController)
    implementation(Dependencies.Accompanist.permissions)
    
    // Hilt - Dependency Injection
    implementation(Dependencies.Hilt.android)
    kapt(Dependencies.Hilt.compiler)
    implementation(Dependencies.Hilt.work)
    
    // Room - Database
    implementation(Dependencies.Room.runtime)
    implementation(Dependencies.Room.ktx)
    implementation(Dependencies.Room.paging)
    kapt(Dependencies.Room.compiler)
    
    // DataStore
    implementation(Dependencies.DataStore.preferences)
    implementation(Dependencies.DataStore.proto)
    implementation(Dependencies.DataStore.protobufJavalite)
    
    // WorkManager
    implementation(Dependencies.WorkManager.ktx)
    implementation(Dependencies.WorkManager.hilt)
    
    // Coroutines & Flow
    implementation(Dependencies.Coroutines.core)
    implementation(Dependencies.Coroutines.android)
    
    // Kotlinx DateTime
    implementation(Dependencies.Kotlinx.datetime)
    
    // Kotlinx Serialization
    implementation(Dependencies.Kotlinx.serialization)
    
    // Charts - Vico
    implementation(Dependencies.Charts.vicoCore)
    implementation(Dependencies.Charts.vicoCompose)
    implementation(Dependencies.Charts.vicoComposeM3)

    // Testing - JUnit 5
    testImplementation(Dependencies.Testing.junit5Jupiter)
    testImplementation(Dependencies.Testing.junit5Vintage)
    testImplementation(Dependencies.Testing.mockk)
    testImplementation(Dependencies.Testing.turbine)
    testImplementation(Dependencies.Testing.coroutinesTest)
    
    // Android Testing
    androidTestImplementation(Dependencies.Testing.junit5AndroidTest)
    androidTestImplementation(Dependencies.Testing.junit5AndroidRunner)
    androidTestImplementation(Dependencies.Testing.junitAndroidX)
    androidTestImplementation(Dependencies.Testing.mockkAndroid)
    androidTestImplementation(Dependencies.Testing.espressoCore)
    androidTestImplementation(Dependencies.Testing.testRunner)
    androidTestImplementation(Dependencies.Testing.testRules)
    androidTestImplementation(Dependencies.Testing.composeUiTest)
    androidTestImplementation(Dependencies.Testing.uiAutomator)
    
    // WorkManager Testing
    androidTestImplementation(Dependencies.WorkManager.testing)
    
    // Room Testing
    testImplementation(Dependencies.Room.testing)
    
    // Debug tools
    debugImplementation(Dependencies.Compose.uiTooling)
    debugImplementation(Dependencies.Compose.uiTestManifest)
}
