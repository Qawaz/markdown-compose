import org.jetbrains.compose.compose

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
    androidTarget {
        publishLibraryVariants("release")
    }
    jvm("desktop") {
        compilations.all {
            kotlinOptions.jvmTarget = "17"
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
                api("com.qawaz:reorderable:0.9.8")

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

                implementation("com.qawaz.colorpicker:color-picker:${property("colorpicker.version")}")


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
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.2")
                // Coil Compose
                implementation("io.coil-kt:coil-compose:2.3.0")
            }
        }
        val desktopMain by getting {
            dependencies {

            }
        }
        val jsMain by getting {
            dependencies {

            }
        }
    }
}

android {
    namespace = "com.wakaztahir.markdowncompose.core"
    compileSdk = 34
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    defaultConfig {
        minSdk = 21
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}