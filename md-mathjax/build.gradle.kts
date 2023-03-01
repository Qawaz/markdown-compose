plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose")
    id("com.android.library")
    kotlin("plugin.serialization")
    id("maven-publish")
}

group = "com.wakaztahir"
version = property("version") as String

kotlin {
    android {
        publishLibraryVariants("release")
    }
    jvm("desktop") {
        compilations.all {
            kotlinOptions.jvmTarget = "1.8"
        }
    }
    js(IR) {
        browser()
        binaries.executable()
    }
    sourceSets {
        val commonMain by getting {
            dependencies {

                api(project(":md-compose-core"))

                // Multiplatform Markdown Parsing Library
//                api("org.jetbrains:markdown:0.3.1")

                // Code Editor
//                implementation("com.wakaztahir:codeeditor:3.1.1")

                // Reorderable For List
//                implementation("com.qawaz:reorderable:0.9.7")

                // Link Previews Library
//                implementation("com.wakaztahir:linkpreview:1.0.5")

                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.ui)
                @OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)
                implementation(compose.material3)

                implementation("com.wakaztahir.compose-icons:materialdesignicons:1.0.1")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        val androidMain by getting {
            dependencies {
                // Coroutines
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.4")
                // Coil Compose
                implementation("io.coil-kt:coil-compose:1.4.0")
                // Math Jax for Android
                implementation("com.wakaztahir:mathjax:3.0.1")
            }
        }
        val desktopMain by getting {
            dependencies {
                implementation("org.scilab.forge:jlatexmath:1.0.7")
            }
        }
        named("jsMain") {
            dependencies {
                api(compose.web.core)
            }
        }
    }
}

android {
    compileSdk = 33
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    sourceSets["main"].apply {
        res.srcDir(File(buildDir, "generated/moko/androidMain/res"))
    }
    defaultConfig {
        minSdk = 21
    }
}

publishing {
    repositories {
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
}
