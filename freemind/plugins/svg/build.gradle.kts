/*
 * FreeMind CE - SVG Plugin Build Configuration
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

val batikVersion = "1.17"
val fopVersion = "2.9"

dependencies {
    // Compile-only dependency on main freemind module
    compileOnly(project(":freemind"))

    // Apache Batik (SVG)
    implementation("org.apache.xmlgraphics:batik-all:${batikVersion}")

    // Apache FOP (PDF)
    implementation("org.apache.xmlgraphics:fop:${fopVersion}")
    implementation("org.apache.xmlgraphics:xmlgraphics-commons:2.9")

    // Rhino (JavaScript Engine for Batik)
    implementation("org.mozilla:rhino:1.7.14")

    // XML Commons
    implementation("xml-apis:xml-apis-ext:1.3.04")
}

tasks.jar {
    archiveBaseName.set("svg_plugin")
    archiveClassifier.set("")
}
