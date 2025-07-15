pluginManagement {
//    includeBuild("build-plugin")
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
        jcenter()
        mavenCentral()
        google()
        maven("https://jitpack.io")
        maven("https://maven.google.com")
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.PREFER_PROJECT)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "grouter_demo"
include("example:app")
include(":grouter")
include(":grouter-annotation")
include(":grouter-compiler")
include(":grouter-kotlinextensions")
include(":grouter-gradle-plugin")
