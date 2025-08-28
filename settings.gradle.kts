pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "SteadyMate"
include(":app")

// Core modules - shared across features
include(":core:common")
include(":core:data")
include(":core:domain")
include(":core:ui")

// Feature modules - specific app features
include(":feature:home")
include(":feature:checkin")
