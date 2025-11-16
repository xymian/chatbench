plugins {
    kotlin("jvm") version "2.2.0"
    kotlin("plugin.serialization") version "2.2.0"
}

group = "com.simulatedTez"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {

    implementation("io.ktor:ktor-client-okhttp:3.2.1")
    implementation("io.ktor:ktor-client-cio:3.2.1")
    implementation("io.ktor:ktor-client-content-negotiation:3.2.1")
    implementation("io.ktor:ktor-serialization-kotlinx-json:3.2.1")

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.9.0")

    implementation(project.files("libs/chat-engine-2.0.jar"))
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(19)
}