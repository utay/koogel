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

    implementation("org.jsoup:jsoup:1.11.3")
    implementation("org.slf4j:slf4j-log4j12:1.7.25")
    testCompile("junit:junit:4.11")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}