/*
 * FreeMind CE - Search Plugin Build Configuration
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

    // Apache Lucene 9.12.3 - local JARs
    implementation(files("lucene-core-9.12.3.jar"))
    implementation(files("lucene-analysis-common-9.12.3.jar"))
    implementation(files("lucene-queryparser-9.12.3.jar"))
}

tasks.jar {
    archiveBaseName.set("search_plugin")
    archiveClassifier.set("")
}
