plugins {
    kotlin("jvm") version "1.9.23"
}

allprojects {
    apply(plugin = "org.jetbrains.kotlin.jvm")
    apply(plugin = "application")

    group = "in.rahulbagchi.lesson.di"
    version = "1.0-SNAPSHOT"

    repositories {
        mavenCentral()
    }

    tasks.withType<Test>().configureEach {
        useJUnitPlatform()
    }
}

kotlin {
    jvmToolchain(21)
}

