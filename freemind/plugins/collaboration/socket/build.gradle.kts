/*
 * FreeMind CE - Collaboration/Socket Plugin Build Configuration
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
    // Uses standard java.net for socket communication - no external deps needed
}

tasks.jar {
    archiveBaseName.set("collaboration_socket_plugin")
    archiveClassifier.set("")
}
