plugins {
    kotlin("jvm") version "1.8.10"
}

group = "org.sleepy"
version = "1.0"

repositories {
    mavenCentral()
}

val jdbiVersion = "3.37.1"
val junitVersion = "5.9.2"

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.7.10")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
    implementation("io.dropwizard:dropwizard-core:2.1.1")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.13.1")
    implementation("net.jodah:failsafe:2.4.4")
    implementation("org.jdbi:jdbi3-core:$jdbiVersion")
    implementation("org.jdbi:jdbi3-kotlin:$jdbiVersion")

    implementation("io.github.crackthecodeabhi:kreds:0.8.1")


    testImplementation("org.junit.jupiter:junit-jupiter-api:$junitVersion")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$junitVersion")
    testImplementation("com.h2database:h2:2.1.214")
}

tasks.test {
    useJUnitPlatform()
}
