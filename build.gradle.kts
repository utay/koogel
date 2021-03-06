import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.3.10"
    application
}

group = "mti"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    jcenter()
}

application {
    mainClassName = "main.MainKt"
}

dependencies {
    compile(kotlin("stdlib-jdk8"))

    implementation("org.jsoup:jsoup:1.11.3")
    implementation("org.slf4j:slf4j-log4j12:1.7.25")
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.3.10")
    implementation("com.sparkjava:spark-kotlin:1.0.0-alpha")
    implementation("com.google.code.gson:gson:2.8.5")
    implementation("com.mashape.unirest:unirest-java:1.4.9")

    testCompile("junit:junit:4.11")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}