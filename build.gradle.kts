import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    id("org.springframework.boot") version "2.2.0.RELEASE"
    id("io.spring.dependency-management") version "1.0.8.RELEASE"
    kotlin("jvm") version "1.3.50"
    kotlin("plugin.spring") version "1.3.50"
    id("com.palantir.docker") version "0.22.1"
}

group = "ovh.rwx.geoip"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_1_8

val developmentOnly by configurations.creating
configurations {
    runtimeClasspath {
        extendsFrom(developmentOnly)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
    }
    compile("com.maxmind.geoip2:geoip2:2.12.0")
    compile("dnsjava:dnsjava:2.1.9")
    compile("org.codehaus.plexus:plexus-archiver:4.2.1")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "1.8"
    }
}

task<Copy>("unpack") {
    val bootJar = tasks.getByName<BootJar>("bootJar")
    dependsOn(bootJar)
    from(zipTree(bootJar.outputs.files.singleFile))
    into("build/dependency")
}

docker {
    val archiveBaseName = tasks.getByName<BootJar>("bootJar").archiveBaseName.get()
    name = "registry.docker.rwx.ovh/${project.group}/$archiveBaseName"
    copySpec.from(tasks.getByName<Copy>("unpack").outputs).into("dependency")
    buildArgs(mapOf("DEPENDENCY" to "dependency"))
}