pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        maven("https://sdk.squareup.com/public/android")
//        maven ("https://maven.pkg.jetbrains.space/kotlin/p/kotlin/kotlin-dev/")
        gradlePluginPortal()
    }

}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
        maven ("https://sdk.squareup.com/public/android/")
//        maven ("https://maven.pkg.jetbrains.space/kotlin/p/kotlin/kotlin-dev/")
    }
}

rootProject.name = "TheraJet(Tab)"
include(":app")
include(":domain")
include(":data")
 