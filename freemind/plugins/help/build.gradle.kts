/*
 * FreeMind CE - Help Plugin Build Configuration
 * Denomas Engineering - 2026
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

    // JavaHelp (jhall) - local JAR, not reliably on Maven Central
    implementation(files("jhall.jar"))
}

tasks.jar {
    archiveBaseName.set("help_plugin")
    archiveClassifier.set("")
}
