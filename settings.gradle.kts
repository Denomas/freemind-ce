/*
 * FreeMind CE - Modern Gradle Settings
 * Denomas Engineering - 2026
 */

pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}

rootProject.name = "freemind-ce"

// Include freemind as the main module
include(":freemind")

// Plugin modules (organized by category)
include(":freemind:plugins:svg")
include(":freemind:plugins:script")
include(":freemind:plugins:map")
include(":freemind:plugins:search")
include(":freemind:plugins:help")
include(":freemind:plugins:collaboration:socket")
include(":freemind:plugins:contextgraph")

project(":freemind").projectDir = file("freemind")
project(":freemind:plugins:svg").projectDir = file("freemind/plugins/svg")
project(":freemind:plugins:script").projectDir = file("freemind/plugins/script")
project(":freemind:plugins:map").projectDir = file("freemind/plugins/map")
project(":freemind:plugins:search").projectDir = file("freemind/plugins/search")
project(":freemind:plugins:help").projectDir = file("freemind/plugins/help")
project(":freemind:plugins:collaboration:socket").projectDir = file("freemind/plugins/collaboration/socket")
project(":freemind:plugins:contextgraph").projectDir = file("freemind/plugins/contextgraph")
