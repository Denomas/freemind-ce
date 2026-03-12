/*
 * FreeMind CE - Root Build Configuration
 * Denomas Engineering - 2026
 *
 * Modern Java 21 + Gradle build system for FreeMind Classic Edition
 */

// Global configuration
allprojects {
    group = "com.denomas.freemind"
    version = "1.1.0"
}

// Shared repositories
subprojects {
    repositories {
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
}
