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

includeBuild("../app") {
    name = "air-app-build"
    dependencySubstitution {
        substitute(module("com.getair:air-app-shared")).using(project(":shared"))
    }
}

listOf(
    "../air",
    "../stremio-addon-client",
    "../iptv",
    "../video",
).map(::file)
    .filter { it.resolve("settings.gradle.kts").isFile }
    .forEach { includeBuild(it) }
