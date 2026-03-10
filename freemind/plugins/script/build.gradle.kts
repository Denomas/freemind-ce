/*
 * FreeMind CE - Script Plugin Build Configuration
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

    // Groovy (scripting engine) - local JAR, version matches existing API usage
    implementation(files("groovy-all.jar"))
}

tasks.jar {
    archiveBaseName.set("scripting_plugin")
    archiveClassifier.set("")
}
