plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    application
}

group = "parkmini"
version = "1.0"

application {
    mainClass.set("com.v1.parkmini.ApplicationKt")
}

val ktorVersion = "3.6.0"

dependencies {
    implementation(project(":shared"))

    implementation("io.ktor:ktor-server-core:$ktorVersion")
    implementation("io.ktor:ktor-server-netty:$ktorVersion")
    implementation("io.ktor:ktor-server-content-negotiation:$ktorVersion")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")
    implementation("io.ktor:ktor-server-websockets:$ktorVersion")
    implementation("ch.qos.logback:logback-classic:1.5.18")
}
