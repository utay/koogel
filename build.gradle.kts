import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.3.10"
    application
}

group = "mti"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

application {
    mainClassName = "main.MainKt"
}

dependencies {
    compile(kotlin("stdlib-jdk8"))
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}