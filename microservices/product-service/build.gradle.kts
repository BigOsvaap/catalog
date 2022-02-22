import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.springframework.boot") version "2.6.2"
	id("io.spring.dependency-management") version "1.0.11.RELEASE"
	kotlin("jvm") version "1.6.10"
	kotlin("plugin.spring") version "1.6.10"
	kotlin("plugin.allopen") version "1.6.10"
	kotlin("kapt") version "1.6.10"
}

allOpen {
	annotation("javax.persistence.Entity")
	annotation("javax.persistence.Embeddable")
	annotation("javax.persistence.MappedSuperclass")
}

group = "com.bigosvaap.microservices.core.product"
version = "1.0.0-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_11

repositories {
	mavenCentral()
}

val mapStructVersion = "1.4.2.Final"
val testContainersVersion = "1.16.2"

dependencies {
	implementation(project(":api"))
	implementation(project(":util"))

	implementation("org.mapstruct:mapstruct:$mapStructVersion")
	kapt("org.mapstruct:mapstruct-processor:$mapStructVersion")
	compileOnly("org.mapstruct:mapstruct-processor:$mapStructVersion")
	annotationProcessor("org.mapstruct:mapstruct-processor:$mapStructVersion")
	testAnnotationProcessor("org.mapstruct:mapstruct-processor:$mapStructVersion")

	implementation(platform("org.testcontainers:testcontainers-bom:$testContainersVersion"))
	testImplementation ("org.testcontainers:testcontainers")
	testImplementation ("org.testcontainers:junit-jupiter")
	testImplementation ("org.testcontainers:mongodb")

	implementation("org.springframework.boot:spring-boot-starter-data-mongodb")
	implementation("org.springframework.boot:spring-boot-starter-actuator")
	implementation("org.springframework.boot:spring-boot-starter-webflux")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("io.projectreactor:reactor-test")
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs = listOf("-Xjsr305=strict")
		jvmTarget = "11"
	}
}

tasks.getByName<Jar>("jar") {
	enabled = false
}

tasks.withType<Test> {
	useJUnitPlatform()
}
