import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

buildscript {
    repositories {
        maven {
            url = uri("https://plugins.gradle.org/m2/")
            mavenCentral()
            gradlePluginPortal()
            url = uri("https://raw.githubusercontent.com/graalvm/native-build-tools/snapshots")
        }
    }
    dependencies {
        classpath("io.quarkus:gradle-application-plugin:2.5.1.Final")
    }
}

plugins {
    kotlin("jvm") version "1.5.32"
    id("io.quarkus") version "2.5.1.Final"
    id("org.graalvm.buildtools.native") version "0.9.8"
    id("org.jetbrains.kotlin.plugin.allopen") version "1.5.10"
    id("org.openapi.generator") version "4.2.2"
    application
}

group = "com.github.shaad"
version = "1.0"

object Versions {
    const val kotlinVersion = "1.5.32"
    const val quarkusVersion = "2.5.1.Final"
}

apply(plugin = "io.quarkus")

repositories {
    mavenCentral()
}

dependencies {
    implementation(enforcedPlatform("io.quarkus:quarkus-bom:${Versions.quarkusVersion}"))
    implementation("io.quarkus:quarkus-resteasy-jackson")
    implementation("io.quarkus:quarkus-rest-client")
    implementation("io.quarkus:quarkus-smallrye-openapi")
    implementation("io.quarkus:quarkus-kotlin")
    implementation("io.quarkus:quarkus-config-yaml")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("io.quarkus:quarkus-swagger-ui")

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
