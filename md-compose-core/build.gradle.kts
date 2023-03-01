import org.jetbrains.compose.compose

plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose")
    id("com.android.library")
    kotlin("plugin.serialization")
    id("maven-publish")
//    id("plugin.multiplatform-resources")
}

group = "com.wakaztahir"
version = "1.0.2"

//multiplatformResources {
//    multiplatformResourcesPackage = "com.wakaztahir.markdowncompose" // required
//    multiplatformResourcesClassName = "Res" // optional, default MR
//    multiplatformResourcesVisibility = dev.icerock.gradle.MRVisibility.Internal // optional, default Public
//    iosBaseLocalizationRegion = "en" // optional, default "en"
//    multiplatformResourcesSourceSet = "commonMain"  // optional, default "commonMain"
//}

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

                // Code Editor
//                implementation("com.wakaztahir:codeeditor:3.1.1")

                // Reorderable For List
                implementation("com.qawaz:reorderable:0.9.7")

                // Link Previews Library
                implementation("com.wakaztahir:linkpreview:1.0.5")

                // Compose Helpers
                api("com.wakaztahir:compose-helpers:${property("compose.helpers.version")}")

                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.ui)
                compose("org.jetbrains.compose.ui:ui-util")
                @OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)
                implementation(compose.material3)

                // Kotlin Serialization
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:${property("serialization.version")}")
                implementation("com.wakaztahir.compose-icons:materialdesignicons:1.0.1")
                implementation("com.qawaz.android.colorpicker:compose-color-picker:0.6.1")
                implementation("com.wakaztahir:qawaz-logger:${property("qawaz.logger.version")}")
//                implementation("dev.icerock.moko:resources:0.20.1")


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
