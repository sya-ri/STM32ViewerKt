import io.github.fvarrui.javapackager.gradle.PackageTask
import io.github.fvarrui.javapackager.model.HeaderType
import io.github.fvarrui.javapackager.model.Platform
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

buildscript {
    dependencies {
        classpath("io.github.fvarrui:javapackager:1.2.0")
    }
}

plugins {
    kotlin("jvm") version "1.4.10"
    id("org.openjfx.javafxplugin") version "0.0.9"
}

apply(plugin = "io.github.fvarrui.javapackager.plugin")

group = "me.syari.stm32.viewer"
version = "1.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation("no.tornado:tornadofx:1.7.20")
}

javafx {
    version = "15"
    modules = listOf("javafx.controls", "javafx.fxml")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "11"
}

val packageTaskMainClass = "$group.MainKt"
val packageTaskJdkDir = projectDir.resolve("jdk")
val packageTaskOutputDirectory = buildDir.resolve("package")

tasks.register<PackageTask>("packageForWindows") {
    dependsOn("build")
    platform = Platform.windows
    mainClass = packageTaskMainClass
    jdkPath = packageTaskJdkDir.resolve("windows")
    outputDirectory = packageTaskOutputDirectory.resolve("windows")
    assetsDir = outputDirectory.resolve("assets").apply { mkdirs() }
    isBundleJre = true
    winConfig.headerType = HeaderType.gui
}

tasks.register<PackageTask>("packageForMac") {
    dependsOn("build")
    platform = Platform.mac
    mainClass = packageTaskMainClass
    jdkPath = packageTaskJdkDir.resolve("mac").resolve("Contents").resolve("Home")
    outputDirectory = packageTaskOutputDirectory.resolve("mac")
    assetsDir = outputDirectory.resolve("assets").apply { mkdirs() }
    isBundleJre = true
    macConfig.isGenerateDmg = false
    macConfig.isGeneratePkg = true
}

tasks.register<PackageTask>("packageForLinux") {
    dependsOn("build")
    platform = Platform.linux
    mainClass = packageTaskMainClass
    jdkPath = packageTaskJdkDir.resolve("linux")
    outputDirectory = packageTaskOutputDirectory.resolve("linux")
    assetsDir = outputDirectory.resolve("assets").apply { mkdirs() }
    isBundleJre = true
}
