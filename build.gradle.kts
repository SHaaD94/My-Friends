import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

buildscript {
    repositories {
        maven {
            url = uri("https://plugins.gradle.org/m2/")
        }
    }
    dependencies {
        classpath("io.quarkus:gradle-application-plugin:2.5.1.Final")
    }
}

plugins {
    kotlin("jvm") version "1.5.10"
    id ("io.quarkus") version "2.5.1.Final"
    id("org.jetbrains.kotlin.plugin.allopen") version "1.5.10"
    application
}

group = "com.github.shaad"
version = "1.0"

object Versions {
    const val kotlinVersion = "1.5.10"
    const val quarkusVersion = "2.5.1.Final"
    const val kotlinxCoroutinesVersion = "1.5.1"
}

apply(plugin = "io.quarkus")

repositories {
    mavenCentral()
}

dependencies {
    implementation(enforcedPlatform("io.quarkus:quarkus-bom:${Versions.quarkusVersion}"))
    implementation("io.quarkus:quarkus-resteasy-jackson")
    implementation("io.quarkus:quarkus-rest-client")
    implementation("io.quarkus:quarkus-kotlin")
    implementation("io.quarkus:quarkus-config-yaml")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")


    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation(enforcedPlatform("org.jetbrains.kotlinx:kotlinx-coroutines-bom:${Versions.kotlinxCoroutinesVersion}"))

    testImplementation("io.quarkus:quarkus-junit5")
    testImplementation("io.rest-assured:rest-assured")
    testImplementation(kotlin("test"))
}

allOpen {
    annotation("javax.ws.rs.Path")
    annotation("javax.enterprise.context.ApplicationScoped")
    annotation("io.quarkus.test.junit.QuarkusTest")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "11"
    kotlinOptions.freeCompilerArgs += "-Xopt-in=kotlin.RequiresOptIn"
    kotlinOptions.javaParameters = true
}

application {
    mainClass.set("com.github.shaad.myfriends.MainKt")
}