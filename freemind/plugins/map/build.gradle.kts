/*
 * FreeMind CE - Map Plugin Build Configuration
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

    // JMapViewer (OpenStreetMap) - local JAR
    implementation(files("JMapViewer.jar"))
}

tasks.jar {
    archiveBaseName.set("map_plugin")
    archiveClassifier.set("")
}
