object Dependencies {
    // Android SDK & Build Tools
    const val compileSdk = 34
    const val minSdk = 24
    const val targetSdk = 34
    
    // Kotlin & Android Versions
    const val kotlinVersion = "2.0.21"
    const val kotlinCompilerExtension = "1.5.15"
    const val agpVersion = "8.8.0"
    
    // Compose BOM - manages all Compose library versions
    const val composeBom = "2024.02.00"
    
    // AndroidX Core
    const val coreKtx = "1.12.0"
    const val lifecycleKtx = "2.7.0"
    const val activityCompose = "1.8.2"
    const val appcompat = "1.6.1"
    
    // Navigation
    const val navigationCompose = "2.7.6"
    const val navigationHilt = "1.1.0"
    
    // Accompanist
    const val accompanist = "0.32.0"
    
    // Hilt (Dependency Injection)
    const val hilt = "2.48"
    const val hiltNavigationCompose = "1.1.0"
    
    // Room (Database)
    const val room = "2.6.1"
    
    // DataStore
    const val datastore = "1.0.0"
    const val protobuf = "3.24.4"
    const val protobufPlugin = "0.9.4"
    
    // WorkManager
    const val workManager = "2.9.0"
    const val workManagerHilt = "1.1.0"
    
    // Coroutines & Flow
    const val coroutines = "1.7.3"
    
    // Kotlinx DateTime
    const val kotlinxDatetime = "0.5.0"
    
    // Kotlinx Serialization
    const val kotlinxSerialization = "1.7.3"
    
    // Charts
    const val vico = "1.13.1"
    
    // Testing
    const val junit5 = "5.10.1"
    const val junitVintage = "5.10.1"
    const val junitAndroidX = "1.1.5"
    const val mockk = "1.13.8"
    const val turbine = "1.0.0"
    const val espresso = "3.5.1"
    const val testRunner = "1.5.2"
    const val testRules = "1.5.0"
    
    // Compose Libraries
    object Compose {
        const val bom = "androidx.compose:compose-bom:2024.02.00"
        const val ui = "androidx.compose.ui:ui"
        const val uiToolingPreview = "androidx.compose.ui:ui-tooling-preview"
        const val uiTooling = "androidx.compose.ui:ui-tooling"
        const val uiTestManifest = "androidx.compose.ui:ui-test-manifest"
        const val uiTestJunit4 = "androidx.compose.ui:ui-test-junit4"
        const val material3 = "androidx.compose.material3:material3"
        const val material3WindowSizeClass = "androidx.compose.material3:material3-window-size-class"
        const val runtime = "androidx.compose.runtime:runtime"
        const val runtimeLivedata = "androidx.compose.runtime:runtime-livedata"
        const val foundation = "androidx.compose.foundation:foundation"
    }
    
    // AndroidX Libraries
    object AndroidX {
        const val coreKtx = "androidx.core:core-ktx:1.12.0"
        const val appcompat = "androidx.appcompat:appcompat:1.6.1"
        const val lifecycleRuntimeKtx = "androidx.lifecycle:lifecycle-runtime-ktx:2.7.0"
        const val lifecycleViewmodelCompose = "androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0"
        const val lifecycleRuntimeCompose = "androidx.lifecycle:lifecycle-runtime-compose:2.7.0"
        const val activityCompose = "androidx.activity:activity-compose:1.8.2"
    }
    
    // Navigation
    object Navigation {
        const val compose = "androidx.navigation:navigation-compose:2.7.6"
        const val hilt = "androidx.hilt:hilt-navigation-compose:1.1.0"
    }
    
    // Accompanist
    object Accompanist {
        const val systemUiController = "com.google.accompanist:accompanist-systemuicontroller:0.32.0"
        const val permissions = "com.google.accompanist:accompanist-permissions:0.32.0"
    }
    
    // Hilt
    object Hilt {
        const val android = "com.google.dagger:hilt-android:2.48"
        const val compiler = "com.google.dagger:hilt-compiler:2.48"
        const val work = "androidx.hilt:hilt-work:1.1.0"
    }
    
    // Room
    object Room {
        const val runtime = "androidx.room:room-runtime:2.6.1"
        const val ktx = "androidx.room:room-ktx:2.6.1"
        const val compiler = "androidx.room:room-compiler:2.6.1"
        const val paging = "androidx.room:room-paging:2.6.1"
        const val testing = "androidx.room:room-testing:2.6.1"
    }
    
    // DataStore
    object DataStore {
        const val preferences = "androidx.datastore:datastore-preferences:1.0.0"
        const val proto = "androidx.datastore:datastore:1.0.0"
        const val protobufJavalite = "com.google.protobuf:protobuf-javalite:3.24.4"
    }
    
    // WorkManager
    object WorkManager {
        const val ktx = "androidx.work:work-runtime-ktx:2.9.0"
        const val hilt = "androidx.hilt:hilt-work:1.1.0"
        const val testing = "androidx.work:work-testing:2.9.0"
    }
    
    // Coroutines
    object Coroutines {
        const val core = "org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3"
        const val android = "org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3"
        const val test = "org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3"
    }
    
    // Kotlinx
    object Kotlinx {
        const val datetime = "org.jetbrains.kotlinx:kotlinx-datetime:0.5.0"
        const val serialization = "org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3"
    }
    
    // Charts
    object Charts {
        const val vicoCore = "com.patrykandpatrick.vico:core:1.13.1"
        const val vicoCompose = "com.patrykandpatrick.vico:compose:1.13.1"
        const val vicoComposeM3 = "com.patrykandpatrick.vico:compose-m3:1.13.1"
    }
    
    // Testing
    object Testing {
        const val junit5Jupiter = "org.junit.jupiter:junit-jupiter:5.10.1"
        const val junit5Vintage = "org.junit.vintage:junit-vintage-engine:5.10.1"
        const val junit5AndroidTest = "de.mannodermaus.junit5:android-test-core:1.4.0"
        const val junit5AndroidRunner = "de.mannodermaus.junit5:android-test-runner:1.4.0"
        const val junitAndroidX = "androidx.test.ext:junit:1.1.5"
        const val mockk = "io.mockk:mockk:1.13.8"
        const val mockkAndroid = "io.mockk:mockk-android:1.13.8"
        const val turbine = "app.cash.turbine:turbine:1.0.0"
        const val coroutinesTest = "org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3"
        
        // UI Testing
        const val composeUiTest = "androidx.compose.ui:ui-test-junit4"
        const val espressoCore = "androidx.test.espresso:espresso-core:3.5.1"
        const val testRunner = "androidx.test:runner:1.5.2"
        const val testRules = "androidx.test:rules:1.5.0"
        const val uiAutomator = "androidx.test.uiautomator:uiautomator:2.2.0"
    }
}
