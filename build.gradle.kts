buildscript {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}

plugins {
    kotlin("jvm").apply(false)
    kotlin("multiplatform").apply(false)
    kotlin("android").apply(false)
    id("com.android.application").apply(false)
    id("com.android.library").apply(false)
    id("org.jetbrains.compose").apply(false)
    id("maven-publish")
}

fun RepositoryHandler.githubPackages() {
    maven("https://maven.pkg.github.com/Qawaz/markdown-compose") {
        name = "GithubPackages"
        try {
            credentials {
                username = (System.getenv("GPR_USER")).toString()
                password = (System.getenv("GPR_API_KEY")).toString()
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }
}

subprojects {
    apply(plugin = "maven-publish")
    publishing {
        repositories {
            githubPackages()
        }
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        githubPackages()
    }
}