val gcp_project = project.property("gcp_project") ?: "undefined"
val commit_sha = project.property("commit_sha") ?: "undefined"

plugins {
    java
    jacoco
    id("org.barfuin.gradle.jacocolog") version "3.1.0"
    id("org.springframework.boot") version "3.0.6"
    id("com.google.cloud.tools.jib") version "3.3.2"
    id("com.google.cloud.artifactregistry.gradle-plugin") version "2.2.1"
    id("io.spring.dependency-management") version "1.1.0"
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

jib {
    from {
        image = "eclipse-temurin:17-jre-alpine"
    }
    to {
        image = "localhost:5000/lasso-docker/ei2-twitter-service"
        tags = setOf("latest")
    }
    extraDirectories {
        paths {
            path {
                setFrom(file("../bin"))
            }
        }
        permissions.set(mapOf(
            "/wait-for-it.sh" to "755"
        ))
    }
}

tasks.named<Test>("test") {
    useJUnitPlatform()
    testLogging {
        showStackTraces = true
        exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
    }

    finalizedBy(tasks.jacocoTestReport)
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)
}

val intTest by sourceSets.creating {
    compileClasspath += sourceSets.main.get().output
    runtimeClasspath += sourceSets.main.get().output
}

mapOf(
    intTest.implementationConfigurationName to configurations.testImplementation,
    intTest.runtimeOnlyConfigurationName to configurations.testRuntimeOnly,
    intTest.compileOnlyConfigurationName to configurations.testCompileOnly,
    intTest.annotationProcessorConfigurationName to configurations.testAnnotationProcessor,
).forEach { (k, v) -> configurations[k].extendsFrom(v.get()) }

val intTestTask = tasks.register<Test>("intTest") {
    description = "Runs integration tests."
    group = "verification"
    useJUnitPlatform()
    testLogging {
        showStackTraces = true
        exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
    }

    testClassesDirs = intTest.output.classesDirs
    classpath = sourceSets["intTest"].runtimeClasspath

    shouldRunAfter(tasks.test)
}

tasks.check {
    dependsOn(intTestTask)
}

apply(plugin = "io.spring.dependency-management")

dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:2022.0.2")
        mavenBom("com.google.cloud:spring-cloud-gcp-dependencies:4.3.0")
        mavenBom("io.grpc:grpc-bom:1.55.1")  //TODO remove from template after spring bom upgrades to this version or above
    }
}

dependencies {
    compileOnly("org.projectlombok:lombok")
    compileOnly("org.springframework.boot:spring-boot-devtools")
    annotationProcessor("org.projectlombok:lombok")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")

    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.cloud:spring-cloud-context")
    implementation("org.springframework.cloud:spring-cloud-starter-bootstrap")
    implementation("com.google.cloud:spring-cloud-gcp-starter-secretmanager")
    implementation("com.google.cloud:spring-cloud-gcp-starter-trace")
    implementation("com.google.cloud:spring-cloud-gcp-starter-bigquery")
    implementation("com.google.auth:google-auth-library-oauth2-http")

    implementation("com.google.cloud:google-cloud-logging")
    implementation("com.google.cloud:google-cloud-logging-logback")

    implementation("org.postgresql:postgresql:42.6.0")
    implementation("com.google.cloud.sql:postgres-socket-factory:1.11.2")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-jdbc")

    implementation("io.lassomarketing.ei2:ei2-dbconfig:1.2.1")
    implementation("io.lassomarketing.ei2:ei2-logging:1.2.1")
    implementation("io.lassomarketing.ei2:ei2-common:1.3.2")

    implementation("commons-io:commons-io:2.12.0")

    runtimeOnly("org.liquibase:liquibase-core")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

repositories {
    mavenCentral()
    maven {
        url = uri("artifactregistry://us-central1-maven.pkg.dev/lasso-prod-264521/ei2-maven-repository")
    }
}
