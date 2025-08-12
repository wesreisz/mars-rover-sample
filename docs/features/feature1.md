Story 1
Project Bootstrap

As a
developer starting the Mars Rovers console app

I want
a clean Java 17 Gradle project with quality gates and packaging set up

So that
we can iterate quickly with consistent formatting, tests, and a runnable fat JAR

Description
Initialize a Gradle (Kotlin DSL) Java 17 project with the required packages and tooling:

Packages: com.example.mars.app, com.example.mars.domain, com.example.mars.parse, com.example.mars.exec.

Plugins: Spotless (Google Java Format), Checkstyle (minimal rules), Shadow (fat JAR).

Dependencies: no production deps; test-only JUnit 5, AssertJ, Mockito (test scope).

Main.java prints a placeholder line to STDOUT.

README with build/run instructions.

Acceptance Criteria ✅
Scenario 1 – Build & Quality Gates
Given the repo is freshly cloned
When I run ./gradlew spotlessCheck check
Then the build succeeds with no violations

Scenario 2 – Runnable Fat JAR
Given the project is built
When I run ./gradlew shadowJar
Then build/libs/mars-rovers-all.jar is created
And java -jar build/libs/mars-rovers-all.jar prints "Mars Rovers App Initialized"

Definition of Done (DoD)

Gradle config targets Java 17; shadow JAR includes Main-Class.

Spotless/Checkstyle configured; both pass.

README explains build and run.


