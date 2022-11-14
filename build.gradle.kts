import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.7.10"
}

group = "org.capturecoop"
version = "1.0.3"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.capturecoop:CCUtils:1.9.4") //CaptureCoop Common Utils
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}