/*
 * FreeMind CE - LaTeX Plugin Build Configuration
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

    // JLaTeXMath (BSD license — replaces GPLv3 HotEqn)
    implementation("org.scilab.forge:jlatexmath:1.0.7")
    implementation("org.scilab.forge:jlatexmath-font-greek:1.0.7")
    implementation("org.scilab.forge:jlatexmath-font-cyrillic:1.0.7")
}

tasks.jar {
    archiveBaseName.set("latex_plugin")
    archiveClassifier.set("")
}
