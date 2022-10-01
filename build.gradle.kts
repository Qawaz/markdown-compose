import java.util.Properties
import java.io.FileInputStream

plugins {
    // this is necessary to avoid the plugins to be loaded multiple times
    // in each subproject's classloader
    kotlin("jvm") apply false
    kotlin("multiplatform") apply false
    kotlin("android") apply false
    id("com.android.application") apply false
    id("com.android.library") apply false
    id("org.jetbrains.compose") apply false
    id("org.jetbrains.dokka")
}

tasks.withType<org.jetbrains.dokka.gradle.DokkaMultiModuleTask>(){
    outputDirectory.set(rootProject.file("docs/api"))
}

val githubProperties = Properties()
kotlin.runCatching { githubProperties.load(FileInputStream(rootProject.file("github.properties"))) }

allprojects {
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://maven.pkg.jetbrains.space/public/p/compose/dev") }
        maven {
            name = "GithubPackages"
            url = uri("https://maven.pkg.github.com/Qawaz/compose-markdown")
            runCatching {
                credentials {
                    username = (githubProperties["gpr.usr"] ?: System.getenv("GPR_USER")).toString()
                    password = (githubProperties["gpr.key"] ?: System.getenv("GPR_API_KEY")).toString()
                }
            }.onFailure { it.printStackTrace() }
        }
    }
}

subprojects {

    version = property("version")!!

    plugins.withId("java") {
        extensions.findByType(JavaPluginExtension::class.java)?.apply {
            sourceCompatibility = JavaVersion.VERSION_11
            targetCompatibility = JavaVersion.VERSION_11

            withJavadocJar()
            withSourcesJar()
        }
    }

    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>() {
        kotlinOptions.jvmTarget = "11"
    }

}