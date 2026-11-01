import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    kotlin("jvm") version "2.3.20"
    application
}

repositories { mavenCentral() }

dependencies {
    implementation("ai.koog:koog-agents:1.3.0")
    implementation("ai.koog:agents-test:1.3.0")
    testImplementation(kotlin("test"))
    runtimeOnly("ch.qos.logback:logback-classic:1.5.20")
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

kotlin { compilerOptions { jvmTarget.set(JvmTarget.JVM_17) } }
application { mainClass.set("lab.MainKt") }
