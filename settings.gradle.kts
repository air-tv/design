pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "AirTvDesign"
include(":app")

listOf(
    "../stremio-addon-client",
    "../iptv",
).map(::file)
    .filter { it.resolve("settings.gradle.kts").isFile }
    .forEach { includeBuild(it) }
