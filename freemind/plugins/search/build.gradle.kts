/*
 * FreeMind CE - Search Plugin Build Configuration
 * Denomas Engineering - 2026
 *
 * TODO: Lucene 4.6.0 (2013) has known CVEs. While search is local-only (no network exposure),
 * upgrade to Lucene 9.x should be tracked as a follow-up.
 * See: https://github.com/denomas/freemind-ce/issues/TBD
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

    // Apache Lucene 4.6.0 - local JARs
    implementation(files("lucene-core-4.6.0.jar"))
    implementation(files("lucene-analyzers-common-4.6.0.jar"))
    implementation(files("lucene-queryparser-4.6.0.jar"))
}

tasks.jar {
    archiveBaseName.set("search_plugin")
    archiveClassifier.set("")
}
