import java.io.FileInputStream
import java.io.InputStreamReader
import java.util.Properties

plugins {
    kotlin("multiplatform") version "1.9.21"
}

group = "hu.tothlp"
version = getLocalProperty("version")

repositories {
    mavenCentral()
}

kotlin {
    val hostOs = System.getProperty("os.name")
    val arch = System.getProperty("os.arch")
    val isMingwX64 = hostOs.startsWith("Windows")
    val nativeTarget = when {
        hostOs == "Mac OS X" -> if (arch == "x86_64" ) macosX64("native") else macosArm64("native")
        hostOs == "Linux" -> linuxX64("native")
        isMingwX64 -> mingwX64("native")
        else -> throw GradleException("Host OS is not supported in Kotlin/Native.")
    }

    nativeTarget.apply {
        binaries {
            executable {
                entryPoint = "hu.tothlp.sshanyi.main"
            }
        }
    }
    sourceSets {
        val nativeMain by getting {
            dependencies {
                implementation("com.github.ajalt.clikt:clikt:4.2.0")
                implementation("com.squareup.okio:okio:3.6.0")
            }
        }

        val nativeTest by getting
    }
}

tasks.withType<Wrapper> {
    gradleVersion = "8.1.1"
    distributionType = Wrapper.DistributionType.BIN
}

fun getLocalProperty(key: String, file: String = "src/nativeMain/resources/version.properties"): Any {
    val properties = Properties()
    File(file).takeIf{it.isFile}?.let {
        InputStreamReader(FileInputStream(it), Charsets.UTF_8).use { reader ->
            properties.load(reader)
        }
    } ?: error("File from not found")

    return properties.getProperty(key)
}