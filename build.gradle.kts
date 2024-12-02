plugins {
    kotlin("jvm") version "2.1.0"
}

group = "com.github.rhappe.aoc"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.github.jsoberg:Kotlin-AoC-API:1.0")

    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}