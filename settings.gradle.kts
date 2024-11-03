pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        maven("https://storage.zego.im/maven")  // <- Add this line.
        maven("https://www.jitpack.io")  // <- Add this line.
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        maven("https://storage.zego.im/maven")  // <- Add this line.
        maven("https://www.jitpack.io")  // <- Add this line.
        mavenCentral()
    }
}

rootProject.name = "streamer"
include(":app")
 