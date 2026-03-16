import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    java
    id("org.springframework.boot") version "4.0.3"
    id("io.spring.dependency-management") version "1.1.7"
    id("org.graalvm.buildtools.native") version "0.10.4"
    id("com.gradleup.shadow") version "9.0.0"
}

group = "org.local"
version = "0.0.1-SNAPSHOT"
description = "spring-aws-lambda"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
}

repositories {
    mavenCentral()
}

extra["springCloudVersion"] = "2025.1.1"

dependencies {
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.cloud:spring-cloud-function-adapter-aws")
    implementation("com.amazonaws:aws-lambda-java-core:1.4.0")
    implementation("com.amazonaws:aws-lambda-java-events:3.14.0")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:${property("springCloudVersion")}")
    }
}

tasks.named<ShadowJar>("shadowJar") {
    archiveClassifier.set("aws")
    mergeServiceFiles()
    append("META-INF/spring.handlers")
    append("META-INF/spring.schemas")
    append("META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports")
    append("META-INF/spring/org.springframework.context.ApplicationContextInitializer.imports")
    exclude("META-INF/LICENSE*", "META-INF/NOTICE*", "META-INF/license*", "META-INF/notice*", "META-INF/license/**", "META-INF/maven/**", "META-INF/native-image/**")
}

graalvmNative {
    binaries {
        named("main") {
            imageName.set("bootstrap")
            buildArgs.addAll(listOf(
                "--initialize-at-run-time=org.joda.time",
                "--initialize-at-run-time=tools.jackson.datatype.joda",
                "--initialize-at-run-time=org.springframework.cloud.function.adapter.aws",
                "--enable-url-protocols=http",
                "-H:+AddAllCharsets"
            ))
        }
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}
