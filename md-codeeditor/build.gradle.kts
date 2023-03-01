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
                api(project(":md-compose-core"))

                // Code Editor
                api("com.wakaztahir:codeeditor:3.1.1")

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
//                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.4")
                // Coil Compose
//                implementation("io.coil-kt:coil-compose:1.4.0")
            }
        }
        val desktopMain by getting {
            dependencies {

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
