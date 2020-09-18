import io.github.fvarrui.javapackager.gradle.PackageTask
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

tasks.register<PackageTask>("packageForWindows") {
    dependsOn("build")
    platform = Platform.windows
}

tasks.register<PackageTask>("packageForMac") {
    dependsOn("build")
    platform = Platform.mac
}

tasks.register<PackageTask>("packageForLinux") {
    dependsOn("build")
    platform = Platform.linux
}

tasks.register("packageForAllPlatforms") {
    dependsOn("packageForWindows", "packageForMac", "packageForLinux")
    group = "JavaPackager"
}