buildscript {
    repositories {
        gradlePluginPortal()
        maven {
            url = uri("https://plugins.gradle.org/m2/")
        }
    }
    dependencies {
        classpath("ca.islandora:isle-gradle-docker-plugin") {
            version {
                branch = "delete-cache-image"
            }
        }
    }
}

apply(plugin = "IsleDocker")
