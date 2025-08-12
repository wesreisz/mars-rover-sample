plugins {
    java
    application
    jacoco
    id("com.diffplug.spotless") version "6.25.0"
    id("checkstyle")
    // Defer applying Shadow when running under Gradle 9 to avoid `fileMode` incompatibility
    id("com.github.johnrengelman.shadow") version "8.1.1" apply false
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.2"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.assertj:assertj-core:3.26.0")
    testImplementation("org.mockito:mockito-core:5.11.0")
}

application {
    mainClass.set("com.example.mars.app.Main")
}

tasks.test {
    useJUnitPlatform()
}

spotless {
    java {
        googleJavaFormat("1.17.0")
        target("src/**/*.java")
    }
}

checkstyle {
    toolVersion = "10.12.4"
    // Minimal, permissive ruleset for now (can be tightened later)
    configFile = file("config/checkstyle/checkstyle.xml")
}

tasks.withType<Checkstyle> {
    reports {
        xml.required.set(false)
        html.required.set(true)
    }
}

tasks.jar {
    manifest {
        attributes["Main-Class"] = application.mainClass.get()
    }
}

// Apply Shadow only for Gradle < 9 to maintain compatibility
if (org.gradle.util.GradleVersion.current() < org.gradle.util.GradleVersion.version("9.0")) {
    pluginManager.apply("com.github.johnrengelman.shadow")
    tasks.named("shadowJar") {
        doFirst {
            println("Configuring shadowJar for fat JAR packaging")
        }
        (this as org.gradle.api.tasks.bundling.Jar).apply {
            archiveBaseName.set("mars-rovers")
            archiveVersion.set("")
            archiveClassifier.set("all")
            manifest {
                attributes["Main-Class"] = application.mainClass.get()
            }
        }
    }
}


