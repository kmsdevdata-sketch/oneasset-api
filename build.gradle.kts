plugins {
    java
    id("org.springframework.boot") version "4.1.0"
    id("io.spring.dependency-management") version "1.1.7"

    id("com.diffplug.spotless") version "7.2.1"
    id("com.github.spotbugs") version "6.4.4"
    jacoco
}

group = "io.oneasset"
version = "0.0.1-SNAPSHOT"
description = "oneasset-api"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    // =========================
    // Spring Boot
    // =========================
    implementation("org.springframework.boot:spring-boot-starter-webmvc")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-security-oauth2-resource-server")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-actuator")

    // =========================
    // Persistence
    // =========================
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-flyway")
    runtimeOnly("org.flywaydb:flyway-database-postgresql")
    runtimeOnly("org.postgresql:postgresql")

    // =========================
    // Development
    // =========================
    developmentOnly("org.springframework.boot:spring-boot-devtools")

    // =========================
    // Lombok
    // =========================
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")

    testCompileOnly("org.projectlombok:lombok")
    testAnnotationProcessor("org.projectlombok:lombok")

    // =========================
    // Test
    // =========================
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

spotless {
    java {
        palantirJavaFormat().style("GOOGLE")

        target("src/**/*.java")

        removeUnusedImports() // 사용하지 않는 import 삭제

        trimTrailingWhitespace() // 줄 끝 공백 제거

        endWithNewline() // 파일 마지막 빈줄 추가
    }

    kotlinGradle {
        target("*.gradle.kts")

        ktlint()
    }
}

spotbugs {
    ignoreFailures.set(false) // 버그 발견시 빌드 실패

    effort.set(com.github.spotbugs.snom.Effort.MAX)

    reportLevel.set(com.github.spotbugs.snom.Confidence.MEDIUM)
}

tasks.withType<com.github.spotbugs.snom.SpotBugsTask>().configureEach {
    reports {
        create("html") {
            required.set(true)
        }

        create("xml") {
            required.set(false)
        }
    }
}

jacoco {
    toolVersion = "0.8.13"
}
tasks.test {
    useJUnitPlatform()
    finalizedBy(tasks.jacocoTestReport)
}
tasks.jacocoTestReport {
    dependsOn(tasks.test)

    reports {
        html.required.set(true)
        xml.required.set(true)
        csv.required.set(true)
    }
}
