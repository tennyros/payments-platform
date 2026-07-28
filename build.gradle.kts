import com.diffplug.gradle.spotless.SpotlessExtension
import org.gradle.testing.jacoco.tasks.JacocoReport

plugins {
    kotlin("jvm") version "2.4.10" apply false
    kotlin("plugin.spring") version "2.4.10" apply false
    id("org.springframework.boot") version "4.1.0" apply false
    id("io.spring.dependency-management") version "1.1.7" apply false
    id("com.diffplug.spotless") version "8.9.0" apply false
    id("org.sonarqube") version "7.3.1.8318"
    jacoco
}

apply(plugin = "com.diffplug.spotless")

allprojects {
    group = "com.payments"
    version = "0.1.0-SNAPSHOT"

    repositories {
        mavenCentral()
    }
}

configure<SpotlessExtension> {
    kotlin {
        target("**/*.kt")
        targetExclude("**/build/**", "**/.gradle/**")
        ktlint()
    }

    kotlinGradle {
        target("**/*.gradle.kts")
        targetExclude("**/build/**", "**/.gradle/**")
        ktlint()
    }

    format("misc") {
        target("*.md", "**/*.md", ".editorconfig", "**/*.yml", "**/*.yaml")
        trimTrailingWhitespace()
        endWithNewline()
    }
}

sonar {
    properties {
        property("sonar.projectKey", "payments-platform")
        property("sonar.projectName", "payments-platform")
        property("sonar.qualitygate.wait", true)
        property(
            "sonar.coverage.jacoco.xmlReportPaths",
            subprojects.joinToString(
                ",",
            ) { "${it.layout.buildDirectory.get().asFile.absolutePath}/reports/jacoco/test/jacocoTestReport.xml" },
        )
    }
}

subprojects {
    apply(plugin = "jacoco")

    tasks.withType<Test>().configureEach {
        useJUnitPlatform()
    }

    tasks.matching { it.name == "check" }.configureEach {
        dependsOn(rootProject.tasks.named("spotlessCheck"))
    }

    rootProject.tasks.named("sonar") {
        dependsOn(tasks.named("check"))
    }

    tasks.withType<Test>().configureEach {
        finalizedBy(tasks.named("jacocoTestReport"))
    }

    tasks.withType<JacocoReport>().configureEach {
        dependsOn(tasks.withType<Test>())

        reports {
            xml.required.set(true)
            html.required.set(true)
            csv.required.set(false)
        }
    }
}
