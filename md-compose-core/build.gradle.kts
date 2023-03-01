import org.jetbrains.compose.compose

plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose")
    id("com.android.library")
    kotlin("plugin.serialization")
    id("maven-publish")
}

group = "com.wakaztahir"
version = "1.0.2"

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

                // Multiplatform Markdown Parsing Library
                api("org.jetbrains:markdown:0.3.1")

                // Reorder-able For List
                api("com.qawaz:reorderable:0.9.7")

                // Link Previews Library
                implementation("com.wakaztahir:linkpreview:1.0.5")

                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.ui)
                compose("org.jetbrains.compose.ui:ui-util")
                @OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)
                implementation(compose.material3)

                // Kotlin Serialization
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:${property("serialization.version")}")
                implementation("com.wakaztahir.compose-icons:materialdesignicons:1.0.1")

                implementation("com.godaddy.android.colorpicker:compose-color-picker:0.7.0")


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
        maven("https://maven.pkg.github.com/Qawaz/timeline-android") {
            name = "TimelineGitHubPackages"
            credentials {
                username = (System.getenv("GPR_USER"))!!.toString()
                password = (System.getenv("GPR_API_KEY"))!!.toString()
            }
        }
    }
}
