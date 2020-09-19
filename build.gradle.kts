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

val packageDir = projectDir.resolve("package")
val packageIconDir = packageDir.resolve("icons")
val packageTaskMainClass = "$group.MainKt"
val packageTaskJdkDir = packageDir.resolve("jdk")
val packageTaskOutputDirectory = buildDir.resolve("package")
val packageTaskAssetsDirectory = buildDir.resolve("assets")

tasks.register<PackageTask>("packageForWindows") {
    dependsOn("build")
    platform = Platform.windows
    mainClass = packageTaskMainClass
    jdkPath = packageTaskJdkDir.resolve("windows")
    outputDirectory = packageTaskOutputDirectory.resolve("windows")
    assetsDir = packageTaskAssetsDirectory.apply { mkdirs() }
    isBundleJre = true
    winConfig.headerType = HeaderType.gui
    winConfig.isGenerateMsi = true
    winConfig.isGenerateSetup = true // https://jrsoftware.org/isdl.php
    winConfig.setupLanguages = linkedMapOf<String, String>(
        "Japan" to "compiler:Languages/Japanese.isl"
    )
    winConfig.isWrapJar = false
    winConfig.icoFile = packageIconDir.resolve("icon.ico")
}

tasks.register<PackageTask>("packageForMac") {
    dependsOn("build")
    platform = Platform.mac
    mainClass = packageTaskMainClass
    jdkPath = packageTaskJdkDir.resolve("mac").resolve("Contents").resolve("Home")
    outputDirectory = packageTaskOutputDirectory.resolve("mac")
    assetsDir = packageTaskAssetsDirectory.apply { mkdirs() }
    isBundleJre = true
    macConfig.isGenerateDmg = false
    macConfig.isGeneratePkg = true
    macConfig.icnsFile = packageIconDir.resolve("icon.icns")
}

tasks.register<PackageTask>("packageForLinux") {
    dependsOn("build")
    platform = Platform.linux
    mainClass = packageTaskMainClass
    jdkPath = packageTaskJdkDir.resolve("linux")
    outputDirectory = packageTaskOutputDirectory.resolve("linux")
    assetsDir = packageTaskAssetsDirectory.apply { mkdirs() }
    isBundleJre = true
    linuxConfig.pngFile = packageIconDir.resolve("icon.png")
}
