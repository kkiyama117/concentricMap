//apply(from = "dependencies.gradle.kts")
plugins {
    kotlin("jvm") version "1.3.70"
    // annotation
    kotlin("kapt") version "1.3.70"
    // application plugin
    application
}

group = "jp.hinatan"
version = "1.0-SNAPSHOT"

application {
//    mainClassName = "io.ktor.server.netty.EngineMain"
}

kapt {
    correctErrorTypes = true

    javacOptions {
        option("SomeJavacOption", "OptionValue")
    }

    arguments {
        arg("SomeKaptArgument", "ArgumentValue")
    }
}

repositories {
    jcenter()
}

val implementation by configurations
val testImplementation by configurations
val compileOnly by configurations

// Versions of plugins
val ktorVersion by extra { "1.3.2" }
val serializationVersion by extra { "0.20.0" }

dependencies {
    // implementation
    listOf(
        kotlin("stdlib-jdk8"),
        // ktor
        "io.ktor:ktor-server-netty:$ktorVersion",
        "org.jetbrains.kotlinx:kotlinx-serialization-runtime:$serializationVersion"

    ).forEach { implementation(it) }
    // kapt
    kapt("com.google.auto.value:auto-value:1.6.2")
//    testImplementation("y.z:x:1.0")
//    compileOnly("z.x:y:1.0")
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
}
