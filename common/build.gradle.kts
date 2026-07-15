plugins {
    kotlin("jvm")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

kotlin {
    jvmToolchain(21)
}

dependencies {
    implementation("jakarta.validation:jakarta.validation-api:3.1.1")
    implementation("org.springframework:spring-context:7.0.3")
    implementation("tools.jackson.module:jackson-module-kotlin:3.0.4")

    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
