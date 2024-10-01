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
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven {
            url = uri("http://nexus.arashivision.com:9999/repository/maven-public/")
            isAllowInsecureProtocol = true
            credentials {
                username = "deployment"
                password = "test123"
            }
        }
    }
}

rootProject.name = "ONE X3 Streamer"
include(":app")
