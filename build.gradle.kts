import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

buildscript {
    repositories {
        jcenter()
    }
    dependencies {
        classpath(kotlin("gradle-plugin"))
    }
}

// Versions of plugins
//This is necessary to make the version accessible in other places
val kotlinVersion: String? by extra {
    buildscript.configurations["classpath"]
        .resolvedConfiguration.firstLevelModuleDependencies
        .find { it.moduleName == "org.jetbrains.kotlin.jvm.gradle.plugin" }?.moduleVersion
}
val ktorVersion: String by project
val serializationVersion: String by project
val autoValueVersion: String by project
val ktlintVersion: String by project
val exposedVersion: String by project

//apply(from = "dependencies.gradle.kts")
plugins {
    val kotlinVersion = "1.3.70"
    kotlin("jvm") version kotlinVersion
    // annotation
    kotlin("kapt") version kotlinVersion
    kotlin("plugin.serialization") version kotlinVersion
    // application plugin
    application
    idea
    // create fat jar
    id("com.github.johnrengelman.shadow") version "5.2.0"
}

// versions
val buildVersions = mapOf(
    "major" to 1,
    "minor" to 2,
    "patch" to 1,
    "suffix" to "SNAPSHOT"
)

sourceSets {
    getByName("main").java.srcDirs("src")
    getByName("test").java.srcDirs("test")
    getByName("main").resources.srcDirs("resources")
    getByName("test").resources.srcDirs("testresources")
}

group = "jp.hinatan"
version = "${buildVersions["major"]}.${buildVersions["minor"]}.${buildVersions["patch"]}-${buildVersions["suffix"]}"

application {
//    mainClassName = "jp.hinatan.Main"
    mainClassName = "io.ktor.server.netty.EngineMain"
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
        // exposed (datanbase)
        "org.jetbrains.exposed:exposed-core:$exposedVersion",
        "org.jetbrains.exposed:exposed-dao:$exposedVersion",
        "org.jetbrains.exposed:exposed-jdbc:$exposedVersion",
        // ktor
        // server
        "io.ktor:ktor-server-core:$ktorVersion",
        "io.ktor:ktor-server-host-common:$ktorVersion",
//        "io.ktor:ktor-server-cio:$ktorVersion",
        "io.ktor:ktor-server-netty:$ktorVersion",
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

tasks.withType<Jar> {
    manifest {
        attributes(
            mapOf(
                "Main-Class" to application.mainClassName
            )
        )
    }
}

tasks.withType<ShadowJar> {
    mergeServiceFiles()
}
