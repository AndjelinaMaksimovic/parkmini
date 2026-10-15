pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.PREFER_SETTINGS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "parkmini"

// shared: models used by both sides (Kotlin Multiplatform), backend: Ktor server, android: Compose app
include(":shared", ":backend", ":android")
