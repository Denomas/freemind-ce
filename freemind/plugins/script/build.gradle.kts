/*
 * FreeMind CE - Script Plugin Build Configuration
 * Denomas - 2026
 */

plugins {
    java
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

dependencies {
    // Compile-only dependency on main freemind module
    compileOnly(project(":freemind"))

    // Groovy (scripting engine)
    implementation("org.codehaus.groovy:groovy-all:3.0.25")
}

tasks.jar {
    archiveBaseName.set("scripting_plugin")
    archiveClassifier.set("")
}
