/*
 * FreeMind CE - Context Graph Export Plugin Build Configuration
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
    compileOnly(project(":freemind"))
}

tasks.jar {
    archiveBaseName.set("contextgraph_plugin")
    archiveClassifier.set("")
}
