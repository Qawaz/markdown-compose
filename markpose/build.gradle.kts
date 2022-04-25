import java.util.Properties
import java.io.FileInputStream

plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose")
    id("maven-publish")
    id("com.android.library")
}

group = "com.wakaztahir"
version = property("version") as String

android {
    compileSdk = 31
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    defaultConfig {
        minSdk = 21
        targetSdk = 31
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

kotlin {
    android {
        publishLibraryVariants("release")
    }
    jvm("desktop") {
        compilations.all {
            kotlinOptions.jvmTarget = "11"
        }
    }
    sourceSets {
        val commonMain by getting {
            dependencies {

                // Compose
                api(compose.runtime)
                api(compose.foundation)
                api(compose.material)

                // Code Editor
                implementation("com.wakaztahir:codeeditor:3.0.0")

                // Markdown Processing
                implementation("org.jetbrains:markdown:0.3.1")

                // Reorderable For List
                implementation("org.burnoutcrew.composereorderable:reorderable:0.7.4")

                // Link Previews Library
                implementation("com.wakaztahir:linkpreview:1.0.1")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        val androidMain by getting {
            dependencies {
                // For Displaying Latex
                implementation("com.wakaztahir:mathjax:3.0.1")

                // For Image Painter
                implementation("io.coil-kt:coil-compose:1.4.0")
            }
        }
        val androidTest by getting {
            dependencies {
                implementation("junit:junit:4.13.2")
            }
        }
        val desktopMain by getting {
            dependencies {
                api(compose.preview)

                // For Displaying Latex
                implementation("org.scilab.forge:jlatexmath:1.0.7")
            }
        }
        val desktopTest by getting
    }
}

val githubProperties = Properties()
try { githubProperties.load(FileInputStream(rootProject.file("github.properties"))) }catch(e : Exception) { e.printStackTrace() }

afterEvaluate {
    publishing {
        repositories {
            maven {
                name = "GithubPackages"
                url = uri("https://maven.pkg.github.com/codeckle/compose-markdown")

                runCatching {
                    credentials {
                        username = (githubProperties["gpr.usr"] ?: System.getenv("GPR_USER")).toString()
                        password = (githubProperties["gpr.key"] ?: System.getenv("GPR_API_KEY")).toString()
                    }
                }.onFailure { it.printStackTrace() }
            }
        }
    }
}