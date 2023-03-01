pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
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
    plugins {
        kotlin("jvm").version(extra["kotlin.version"] as String).apply(false)
        kotlin("android").version(extra["kotlin.version"] as String).apply(false)
        kotlin("multiplatform").version(extra["kotlin.version"] as String).apply(false)
        id("com.android.application").version(extra["agp.version"] as String).apply(false)
        id("com.android.library").version(extra["agp.version"] as String).apply(false)
        id("org.jetbrains.compose").version(extra["compose.version"] as String).apply(false)
        kotlin("plugin.serialization").version(extra["kotlin.version"] as String).apply(false)
    }
}
rootProject.name = "MarkdownCompose"

include(":demo:android")
include(":demo:desktop")
include(":demo:common")
include(":demo:web")

include(":md-compose-core")
include(":md-mathjax")
include(":md-codeeditor")
