plugins {
    kotlin("jvm") version "2.1.0"
}

group = "com.github.rhappe.aoc"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://jitpack.io")
}

dependencies {
    implementation("com.github.jsoberg:Kotlin-AoC-API:1.0")
    implementation("com.github.jsoberg:Kotlin-AoC-Utilities:2024.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.1.0")

    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}