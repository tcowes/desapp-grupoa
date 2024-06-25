import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    war
    id("org.springframework.boot") version "3.2.3"
    id("io.spring.dependency-management") version "1.1.4"
    id("org.sonarqube") version "4.3.1.3277"
    id("jacoco")
    kotlin("jvm") version "1.9.22"
    kotlin("plugin.spring") version "1.9.22"
    kotlin("plugin.jpa") version "1.9.22"
}

group = "ar.edu.unq.desapp.grupoa"
version = "0.0.1-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_17
}

repositories {
    mavenCentral()
}

val javaxValidationVersion = "2.0.1.Final"
val mySqlConnectorVersion = "8.0.33"
val springDocVersion = "2.2.0"
val githubFuelVersion = "2.3.1"
val jsonVersion = "20231013"
val hsqlVersion = "2.7.1"
val tngtechVersion = "1.0.1"
val jwtVersion = "0.9.1"
val jaxbApi = "2.3.1"
val caffeineVersion = "3.0.4"

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("io.jsonwebtoken:jjwt:${jwtVersion}")
    implementation("javax.xml.bind:jaxb-api:${jaxbApi}")
    implementation("io.micrometer:micrometer-registry-prometheus")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("javax.validation:validation-api:${javaxValidationVersion}")
    implementation("org.hibernate.validator:hibernate-validator")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("mysql:mysql-connector-java:${mySqlConnectorVersion}")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:${springDocVersion}")
    implementation("com.github.kittinunf.fuel:fuel:${githubFuelVersion}")
    implementation("org.json:json:${jsonVersion}")
    implementation("com.github.ben-manes.caffeine:caffeine:${caffeineVersion}")
    implementation("org.springframework.boot:spring-boot-starter-actuator")

    providedRuntime("org.springframework.boot:spring-boot-starter-tomcat")
    runtimeOnly("org.hsqldb:hsqldb:${hsqlVersion}")
    developmentOnly("org.springframework.boot:spring-boot-devtools")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.springframework.security:spring-security-test")
    testImplementation("com.tngtech.archunit:archunit:${tngtechVersion}")

}

sonar {
    properties {
        property("sonar.projectKey", "tcowes_unq-desapp-2024s1-grupo-a")
        property("sonar.organization", "tcowes")
        property("sonar.host.url", "https://sonarcloud.io")
        property(
            "sonar.coverage.jacoco.xmlReportPaths",
            "$buildDir\\backend-desapp_api\\reports\\jacoco\\test\\jacocoTestReport.xml"
        )
        property("sonar.coverage.exclusions", "**/service/integration/**,**/webservice/**")
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs += "-Xjsr305=strict"
        jvmTarget = "17"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}


tasks.jacocoTestReport {
    reports {
        xml.required.set(true)
        csv.required.set(false)
        html.required.set(false)
    }

    classDirectories.setFrom(
        files(classDirectories.files.map {
            fileTree(it).matching {
                exclude(
                    "/ar/edu/unq/desapp/grupoa/backenddesappapi/service/integration/**",
                    "/ar/edu/unq/desapp/grupoa/backenddesappapi/webservice/security/**"
                )
            }
        })
    )
}

jacoco {
    toolVersion = "0.8.8"
}
