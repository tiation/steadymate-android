# SteadyMate Android App

A fast and beautiful Android application built with the latest technologies.

## Tech Stack

- **Kotlin 1.9.22** - Modern programming language for Android
- **Jetpack Compose** - Modern UI toolkit with Material 3 design
- **Material 3** - Latest Material Design system
- **Gradle Kotlin DSL** - Type-safe build configuration
- **Target SDK 34** - Latest Android API level
- **Minimum SDK 24** - Android 7.0+ support

## Project Structure

```
steadymate/
├── app/
│   ├── src/main/
│   │   ├── java/com/steadymate/app/
│   │   │   ├── MainActivity.kt
│   │   │   └── ui/theme/
│   │   ├── res/
│   │   └── AndroidManifest.xml
│   ├── build.gradle.kts
│   └── proguard-rules.pro
├── gradle/wrapper/
├── build.gradle.kts
├── settings.gradle.kts
└── README.md
```

## Key Features

- ✨ **Modern UI**: Built with Jetpack Compose and Material 3
- 🎨 **Dynamic Theming**: Supports light/dark themes with dynamic colors (Android 12+)
- 🚀 **Performance**: Optimized with latest Kotlin compiler and Compose BOM
- 📱 **Responsive**: Adaptive design for all screen sizes
- 🔧 **Developer Experience**: Kotlin DSL for type-safe build configuration

## Dependencies

### Core Libraries
- `androidx.compose:compose-bom:2024.02.00` - Compose Bill of Materials
- `androidx.compose.material3:material3` - Material 3 components
- `androidx.core:core-ktx:1.12.0` - Core Android extensions
- `androidx.activity:activity-compose:1.8.2` - Activity integration with Compose

### Navigation & Architecture
- `androidx.navigation:navigation-compose:2.7.6` - Navigation component for Compose
- `androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0` - ViewModel integration

### Build Configuration
- Android Gradle Plugin: `8.2.2`
- Kotlin: `1.9.22`
- Compile SDK: `34`
- Target SDK: `34`
- Min SDK: `24`

## Getting Started

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd steadymate
   ```

2. **Open in Android Studio**
   - Open Android Studio
   - Select "Open an existing project"
   - Navigate to the project directory

3. **Build the project**
   ```bash
   ./gradlew build
   ```

4. **Run the app**
   - Connect an Android device or start an emulator
   - Click the "Run" button in Android Studio or use:
   ```bash
   ./gradlew installDebug
   ```

## Development

### Architecture
This project follows modern Android development best practices:
- **MVVM Architecture** with Compose integration
- **Material 3 Design System** for consistent UI
- **Single Activity** architecture with Compose navigation
- **Kotlin Coroutines** for asynchronous operations

### Theming
The app uses Material 3 theming with:
- Dynamic color support (Android 12+)
- Light and dark theme variants
- Consistent typography and spacing

## Requirements

- Android Studio Hedgehog | 2023.1.1 or later
- JDK 8 or later
- Android SDK with API level 34
- Gradle 8.7

## License

This project is part of the SteadyMate ecosystem.
