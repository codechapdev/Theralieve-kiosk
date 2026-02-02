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
        maven {
            url = uri("s3://denovo-android.s3.amazonaws.com")
            credentials(AwsCredentials::class) {
                accessKey = "AKIA26XMFQBITA36GRDR"
                secretKey = "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"
            }
        }
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
        maven {
            url = uri("s3://denovo-android.s3.amazonaws.com")
            credentials(AwsCredentials::class) {
                accessKey = "AKIA26XMFQBITA36GRDR"
                secretKey = "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"
            }
        }
//        maven ("https://maven.pkg.jetbrains.space/kotlin/p/kotlin/kotlin-dev/")
    }
}

rootProject.name = "TheraJet(Tab)"
include(":app")
include(":domain")
include(":data")
 