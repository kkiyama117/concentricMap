import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

//apply(from = "dependencies.gradle.kts")
plugins {
    kotlin("jvm") version "1.3.70"
    // annotation
    kotlin("kapt") version "1.3.70"
    kotlin("plugin.serialization") version "1.3.70"
    // application plugin
    application
    idea
}

// versions
val buildVersions = mapOf(
    "major" to 1,
    "minor" to 0,
    "patch" to 3,
    "suffix" to "SNAPSHOT"
)
val kotlinVersion: String by project
// Versions of plugins
val ktorVersion: String by project
val serializationVersion by extra { "0.20.0" }
val autoValueVersion by extra { "1.7" }
val ktlintVersion by extra { "0.35.0" }

buildscript {
    repositories {
        jcenter()
    }
    dependencies {
        classpath(kotlin("gradle-plugin"))
    }
}

sourceSets {
    getByName("main").java.srcDirs("src")
    getByName("test").java.srcDirs("test")
    getByName("main").resources.srcDirs("resources")
    getByName("test").resources.srcDirs("testresources")
}

group = "jp.hinatan"
version = "${buildVersions["major"]}.${buildVersions["minor"]}.${buildVersions["patch"]}-${buildVersions["suffix"]}"

application {
    mainClassName = "io.ktor.server.cio.EngineMain"
}

// setting for kapt (annotation)
kapt {
    correctErrorTypes = true
    javacOptions {
        option("SomeJavacOption", "OptionValue")
    }
    arguments {
        arg("SomeKaptArgument", "ArgumentValue")
    }
}

application {
//    mainClassName = "io.ktor.server.netty.EngineMain"
}

// setting for kapt (annotation)
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
    mavenCentral()
    jcenter()
    listOf(
        "https://kotlin.bintray.com/ktor",
        "https://kotlin.bintray.com/kotlin-js-wrappers",
        "https://kotlin.bintray.com/kotlinx"
    ).forEach { maven(url = it) }
}

val implementation by configurations
val testImplementation by configurations
val compileOnly by configurations
val ktlint: Configuration by configurations.creating

dependencies {
    // implementation
    listOf(
        // kotlin modules
        kotlin("stdlib-jdk8"),
        kotlin("reflect"),
        // kotlin options
        // logging
        "ch.qos.logback:logback-classic:1.2.1",
        // ktor
        // server
        "io.ktor:ktor-server-core:$ktorVersion",
        "io.ktor:ktor-server-host-common:$ktorVersion",
        "io.ktor:ktor-server-cio:$ktorVersion",
        // html
        "io.ktor:ktor-html-builder:$ktorVersion",
        // json
        "io.ktor:ktor-serialization:$ktorVersion",
        // locations
        "io.ktor:ktor-locations:$ktorVersion",
        // websockets
        "io.ktor:ktor-websockets:$ktorVersion",
        // Auth
        "io.ktor:ktor-auth:$ktorVersion",
        "io.ktor:ktor-auth-jwt:$ktorVersion",

        // client
        "io.ktor:ktor-client-core:$ktorVersion",
        "io.ktor:ktor-client-core-jvm:$ktorVersion",
        "io.ktor:ktor-client-auth-jvm:$ktorVersion",
        "io.ktor:ktor-client-json-jvm:$ktorVersion",
        "io.ktor:ktor-client-serialization-jvm:$ktorVersion",
        // "org.jetbrains.kotlinx:kotlinx-serialization-runtime:$serializationVersion",
        "io.ktor:ktor-client-cio:$ktorVersion",
        "io.ktor:ktor-client-websockets:$ktorVersion",
        "io.ktor:ktor-client-logging-jvm:$ktorVersion"
    ).forEach { implementation(it) }

    // kapt
    kapt("com.google.auto.value:auto-value:$autoValueVersion")

    // test dependencies
    listOf(
        "io.ktor:ktor-server-tests:$ktorVersion",
        "io.ktor:ktor-client-mock:$ktorVersion",
        "io.ktor:ktor-client-mock-jvm:$ktorVersion"
    ).forEach { testImplementation(it) }
    // ktlint
    ktlint("com.pinterest:ktlint:$ktlintVersion")
}

// tasks
val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions.jvmTarget = "1.8"
val compileTestKotlin: KotlinCompile by tasks
compileTestKotlin.kotlinOptions.jvmTarget = "1.8"

/*
 * KtLint setting (below)
 */
tasks.register<JavaExec>("ktlint") {
    group = "verification"
    description = "Check Kotlin code style."
    classpath = configurations.getByName("ktlint")
    main = "com.pinterest.ktlint.Main"
    args = listOf("src/**/*.kt")
}
// Force format
tasks.register<JavaExec>("ktlintFormat") {
    group = "verification"
    description = "Fix Kotlin code style deviations."
    classpath = configurations.getByName("ktlint")
    main = "com.pinterest.ktlint.Main"
    args = listOf("-F", "src/**/*.kt")
}