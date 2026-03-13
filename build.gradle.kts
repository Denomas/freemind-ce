/*
 * FreeMind CE - Root Build Configuration
 * Denomas - 2026
 *
 * Modern Java 21 + Gradle build system for FreeMind Classic Edition
 */

// Global configuration
allprojects {
    group = "com.denomas.freemind"
    version = "1.3.0" // x-release-please-version
}

// Shared repositories
subprojects {
    repositories {
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
}
