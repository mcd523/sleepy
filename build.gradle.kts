plugins {
    kotlin("jvm") version "1.8.22"
}

group = "org.sleepy"
version = "1.0"

repositories {
    mavenCentral()
}

val jdbiVersion = "3.37.1"
val junitVersion = "5.9.2"
val kotlinCoroutinesVersion = "1.6.4"

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.8.20")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlinCoroutinesVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:$kotlinCoroutinesVersion")
    implementation("io.dropwizard:dropwizard-core:4.0.0")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.14.2")
    implementation("org.jdbi:jdbi3-core:$jdbiVersion")
    implementation("org.jdbi:jdbi3-kotlin:$jdbiVersion")

    implementation("io.github.crackthecodeabhi:kreds:0.8.1")
    implementation("com.michael-bull.kotlin-retry:kotlin-retry:1.0.9")

    testImplementation("org.junit.jupiter:junit-jupiter-api:$junitVersion")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$junitVersion")
}

tasks.test {
    useJUnitPlatform()
}
