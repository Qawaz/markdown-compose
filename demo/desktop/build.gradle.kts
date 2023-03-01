plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose")
}

kotlin {
    jvm("desktop")
    sourceSets {
        val desktopMain by getting {
            dependencies {
                implementation(compose.desktop.currentOs)
                implementation(project(":demo:common"))
            }
        }
    }
}

compose.desktop {
    application {
        mainClass = "com.mylibrary.demo.MainKt"
    }
}
