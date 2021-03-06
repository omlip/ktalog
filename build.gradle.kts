import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val logback_version: String by project
val ktor_version: String by project
val kotlin_version: String by project
val exposed_version: String = "0.29.1"
val kodein_jvm_version: String = "7.1.0"
val hikari_version: String = "3.4.5"
val junit_version: String = "5.7.0"
val mariadb_driver_version: String = "2.7.1"
val h2_driver_version: String = "1.4.200"
val kotlinx_serialization_json_version = "1.0.1"
val kotlin_logging_jvm_version = "2.0.2"

plugins {
    application
    kotlin("jvm") version "1.4.31"
    kotlin("plugin.serialization") version "1.4.31"
    id("com.github.johnrengelman.shadow") version "5.2.0"
}

group = "io.devolan.ktalog"
version = "0.0.1"

kotlin.sourceSets["main"].kotlin.srcDirs("src")
kotlin.sourceSets["test"].kotlin.srcDirs("test")

sourceSets["main"].resources.srcDirs("resources")
sourceSets["test"].resources.srcDirs("testresources")

application {
    mainClassName = "io.ktor.server.netty.EngineMain"
}

tasks.withType<KotlinCompile> { kotlinOptions.jvmTarget = "11" }
tasks.withType<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar> {
    manifest {
        attributes(
            mapOf(
                "Main-Class" to "io.ktor.server.netty.EngineMain"
            )
        )
    }
}

val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions.useIR = true

repositories {
    mavenLocal()
    mavenCentral()
    jcenter()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlin_version")
    implementation("ch.qos.logback:logback-classic:$logback_version")

    implementation("io.ktor:ktor-server-netty:$ktor_version")
    implementation("io.ktor:ktor-server-core:$ktor_version")
    implementation("io.ktor:ktor-freemarker:$ktor_version")
    implementation("io.ktor:ktor-serialization:$ktor_version")
    implementation("io.ktor:ktor-server-host-common:$ktor_version")
    implementation("io.ktor:ktor-auth:$ktor_version")
    implementation("io.ktor:ktor-auth-jwt:$ktor_version")

    implementation("io.github.microutils:kotlin-logging-jvm:$kotlin_logging_jvm_version") {
        exclude(group = "org.jetbrains.kotlin", module = "kotlin-stdlib-common")
    }

    implementation("com.zaxxer:HikariCP:$hikari_version")
    implementation("org.mariadb.jdbc:mariadb-java-client:$mariadb_driver_version")
    implementation("com.h2database:h2:$h2_driver_version")

    implementation("org.jetbrains.exposed:exposed-core:$exposed_version")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposed_version")
    implementation("org.jetbrains.exposed:exposed-java-time:$exposed_version")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$kotlinx_serialization_json_version")

    implementation("org.kodein.di:kodein-di-jvm:$kodein_jvm_version")

    testImplementation("io.ktor:ktor-server-tests:$ktor_version")
    testImplementation("io.ktor:ktor-server-test-host:$ktor_version")
    testImplementation("org.junit.jupiter:junit-jupiter:$junit_version")

}
