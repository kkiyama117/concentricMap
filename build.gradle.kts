import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

//apply(from = "dependencies.gradle.kts")
plugins {
    kotlin("jvm") version "1.3.70"
    // annotation
    kotlin("kapt") version "1.3.70"
    // application plugin
    application
}

group = "jp.hinatan"
version = "1.0.1-SNAPSHOT"

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
    jcenter()
}

val implementation by configurations
val testImplementation by configurations
val compileOnly by configurations

// Versions of plugins
val ktorVersion by extra { "1.3.2" }
val serializationVersion by extra { "0.20.0" }
val autoValueVersion by extra { "1.7" }
val ktlintVersion by extra { "0.35.0" }

dependencies {
    // implementation
    listOf(
        // kotlin modules
        kotlin("stdlib-jdk8"),
        kotlin("reflect"),
        // kotlin options
        "org.jetbrains.kotlinx:kotlinx-serialization-runtime:$serializationVersion",
        // ktor
        "io.ktor:ktor-server-netty:$ktorVersion"
    ).forEach { implementation(it) }
    // kapt
    kapt("com.google.auto.value:auto-value:$autoValueVersion")
//    testImplementation("y.z:x:1.0")
//    compileOnly("z.x:y:1.0")
}

// tasks
val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions.jvmTarget = "1.8"
val compileTestKotlin: KotlinCompile by tasks
compileTestKotlin.kotlinOptions.jvmTarget = "1.8"

/*
 * KtLint setting (below)
 */
val ktlint: Configuration by configurations.creating

dependencies {
    ktlint("com.pinterest:ktlint:$ktlintVersion")
}

tasks.register<JavaExec>("ktlint") {
    group = "verification"
    description = "Check Kotlin code style."
    classpath = configurations.getByName("ktlint")
    main = "com.pinterest.ktlint.Main"
    args = listOf("src/**/*.kt")
}
tasks.register<JavaExec>("ktlintFormat") {
    group = "verification"
    description = "Fix Kotlin code style deviations."
    classpath = configurations.getByName("ktlint")
    main = "com.pinterest.ktlint.Main"
    args = listOf("-F", "src/**/*.kt")
}