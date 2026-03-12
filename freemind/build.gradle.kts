/*
 * FreeMind CE - Main Module Build Configuration
 * Denomas Engineering - 2026
 *
 * Modern Java 21 + Gradle + JAXB build system
 */

plugins {
    java
    application
    jacoco
    id("com.github.spotbugs") version "6.0.7"
    id("org.owasp.dependencycheck") version "9.0.9"
}

// ============================================================================
// Java Configuration
// ============================================================================

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks.withType<Javadoc> {
    options.encoding = "UTF-8"
}

tasks.processResources {
    filteringCharset = "UTF-8"
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    // Root-level resource files needed on classpath
    from(layout.projectDirectory.file("FlatLaf.properties"))
    from(layout.projectDirectory.file("freemind.properties"))
    from(layout.projectDirectory.file("patterns.xml"))
    from(layout.projectDirectory.file("patterns_updater.xslt"))
    from(layout.projectDirectory.file("mindmap_menus.xml"))
    from(layout.projectDirectory.file("dictionaries.properties"))
    from(layout.projectDirectory.file("dictionaries.cnf"))
    from(layout.projectDirectory.dir(".")) {
        include("dictionary_*.ortho")
        include("Resources_*.properties")
    }
    // Images and doc need to preserve directory prefix in classpath
    from(layout.projectDirectory.dir(".")) {
        include("images/**")
        include("doc/**/*.mm")
    }
    // Preserve freemind/ prefix for resources loaded via classpath with full package path
    from(layout.projectDirectory.dir(".")) {
        include("freemind/modes/**/*.xslt")
        include("freemind/modes/**/*.xml")
        include("accessories/plugins/**/*.xml")
        include("accessories/plugins/icons/**")
        include("accessories/**/*.xsl")
        include("accessories/**/*.xml")
        include("plugins/**/*.xml")
        exclude("**/build.xml")
        exclude("**/build_*.xml")
    }
}

application {
    mainClass.set("freemind.main.FreeMindStarter")
    applicationDefaultJvmArgs = listOf(
        "-Xms64m",
        "-Xmx512m",
        "-Xss8M",
        "-Dapple.laf.useScreenMenuBar=true",
        "--add-opens=java.desktop/java.awt=ALL-UNNAMED",
        "--add-opens=java.desktop/java.awt.event=ALL-UNNAMED",
        "--add-opens=java.desktop/javax.swing=ALL-UNNAMED",
        "--add-opens=java.desktop/javax.swing.text=ALL-UNNAMED",
        "--add-opens=java.base/java.lang=ALL-UNNAMED",
        "--add-opens=java.base/java.util=ALL-UNNAMED"
    )
}

// ============================================================================
// Version Catalog (Centralized Dependency Management)
// ============================================================================

// jpackage requires numeric-only version (no -CE suffix)
val jpackageVersion = project.version.toString().replace(Regex("-.*"), "")

val jaxbApiVersion = "2.3.1"
val jaxbImplVersion = "2.3.9"
val batikVersion = "1.17"
val fopVersion = "2.9"
val flatlafVersion = "3.4.1"
val jgoodiesVersion = "1.9.0"
val junitVersion = "4.13.2"

// ============================================================================
// Dependencies
// ============================================================================

// Separate configuration for XJC code generation tool
val xjc by configurations.creating

dependencies {
    // JAXB (XML Binding) - replaces JiBX
    implementation("javax.xml.bind:jaxb-api:${jaxbApiVersion}")
    implementation("org.glassfish.jaxb:jaxb-runtime:${jaxbImplVersion}")

    // Activation (required for JAXB on JDK 11+)
    implementation("javax.activation:activation:1.1.1")

    // XJC tool (code generation only - not needed at runtime)
    xjc("org.glassfish.jaxb:jaxb-xjc:${jaxbImplVersion}")
    xjc("org.glassfish.jaxb:jaxb-runtime:${jaxbImplVersion}")

    // Look and Feel - FlatLaf (Modern, Cross-Platform)
    implementation("com.formdev:flatlaf:${flatlafVersion}")
    implementation("com.formdev:flatlaf-extras:${flatlafVersion}")

    // JGoodies (Forms and Look) - local JARs (1.8.0 has setDefaultDialogBorder API)
    implementation(files("lib/jgoodies-forms-1.8.0.jar"))
    implementation(files("lib/jgoodies-common-1.8.1.jar"))

    // JOrtho (Spell Checker) - local JAR, not on Maven Central
    implementation(files("lib/jortho.jar"))

    // SimplyHTML (WYSIWYG HTML Editor) - local JARs
    implementation(files("lib/SimplyHTML/SimplyHTML.jar"))
    implementation(files("lib/SimplyHTML/gnu-regexp-1.1.4.jar"))

    // Apache Xalan (XSLT Processing)
    implementation("xalan:xalan:2.7.3")
    implementation("xalan:serializer:2.7.3")
    implementation("xml-apis:xml-apis:1.4.01")
    implementation("xerces:xercesImpl:2.12.2")

    // Jsoup (HTML Parsing) - 1.10.3 matches existing NodeTraversor API usage
    implementation("org.jsoup:jsoup:1.10.3")

    // Plugin dependencies (local JARs)
    implementation(files("plugins/script/groovy-all.jar"))
    implementation(files("plugins/map/JMapViewer.jar"))
    implementation(files("plugins/search/lucene-core-4.6.0.jar"))
    implementation(files("plugins/search/lucene-analyzers-common-4.6.0.jar"))
    implementation(files("plugins/search/lucene-queryparser-4.6.0.jar"))
    implementation(files("plugins/collaboration/jabber/muse.jar"))
    implementation(files("plugins/collaboration/jabber/commons-logging.jar"))
    implementation(files("plugins/help/jhall.jar"))
    // LaTeX plugin (JLaTeXMath) — built as separate module :freemind:plugins:latex
    implementation("org.scilab.forge:jlatexmath:1.0.7")
    implementation(files("plugins/collaboration/database/hsqldb.jar"))

    // Batik (SVG Support) - for plugins
    implementation("org.apache.xmlgraphics:batik-all:${batikVersion}")

    // FOP (PDF Export) - for plugins
    implementation("org.apache.xmlgraphics:fop:${fopVersion}")

    // Logging (SLF4J + Logback)
    implementation("org.slf4j:slf4j-api:2.0.12")
    implementation("ch.qos.logback:logback-classic:1.4.14")

    // Testing - JUnit 5 with vintage engine for JUnit 3 backward compatibility
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.2")
    testImplementation("org.junit.vintage:junit-vintage-engine:5.10.2")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher:1.10.2")
    testImplementation("junit:junit:${junitVersion}")
    testImplementation("org.mockito:mockito-core:5.10.0")

    // Property-Based Testing (jqwik)
    testImplementation("net.jqwik:jqwik:1.8.2")
    testImplementation("net.jqwik:jqwik-engine:1.8.2")

    // Fluent Assertions (AssertJ)
    testImplementation("org.assertj:assertj-core:3.25.3")
}

// ============================================================================
// Source Sets - Preserve Original Directory Structure
// ============================================================================

sourceSets {
    main {
        java {
            setSrcDirs(listOf(
                "freemind",
                "accessories",
                "de",
                "generated-src",
                "plugins"
            ))
            // Exclude broken macOS Apple API file
            exclude("**/MacChanges.java")
            exclude("**/MacChanges.java.not_here")
            exclude("**/MacChanges.java.disabled")
            // Exclude Jabber collaboration plugin (broken API calls, unmaintained)
            exclude("**/jabber/**")
            // Exclude JApplet-based applet (JApplet removed in Java 21)
            exclude("**/FreeMindApplet.java")
            // Exclude SecurityManager classes (removed in Java 21, JEP 411)
            exclude("**/FreeMindSecurityManager.java")
            exclude("**/ScriptingSecurityManager.java")
        }
        resources {
            setSrcDirs(listOf(
                "freemind",
                "accessories"
            ))
            include("*.properties")
            include("**/*.xml")
            include("**/*.xslt")
            include("**/*.xsl")
            include("*.mm")
            exclude("**/build.xml")
            exclude("**/build_*.xml")
            include("**/*.png")
            include("**/*.jpg")
            include("**/*.gif")
            include("**/*.icns")
            include("dictionaries.properties")
            include("dictionary_*.ortho")
            include("patterns.xml")
            include("license*")
        }
    }
    test {
        java {
            setSrcDirs(listOf("tests"))
            // Exclude AllTests suite (JUnit 3 suite causes duplicate runs under JUnit 5 vintage)
            exclude("**/AllTests.java")
        }
        resources {
            setSrcDirs(listOf("tests"))
            include("**/*.mm")
            include("**/*.xml")
            include("**/*.properties")
        }
    }
}

// ============================================================================
// JAXB Code Generation Task
// ============================================================================

// xjc generates into a separate srcDir to avoid path conflicts
val generatedSrcDir = layout.projectDirectory.dir("generated-src")
val generatedJaxbDir = generatedSrcDir.dir("freemind/controller/actions/generated/instance")

tasks.register<JavaExec>("generateJaxb") {
    description = "Generates JAXB annotated classes from XSD using xjc"
    group = "Generation"

    classpath = configurations["xjc"]
    mainClass.set("com.sun.tools.xjc.XJCFacade")

    doFirst {
        delete(generatedJaxbDir)
        generatedSrcDir.asFile.mkdirs()
    }

    args = listOf(
        "-d", generatedSrcDir.asFile.absolutePath,
        "-p", "freemind.controller.actions.generated.instance",
        "-extension",
        "-encoding", "UTF-8",
        "-no-header",
        layout.projectDirectory.file("freemind_actions.xsd").asFile.absolutePath
    )
}

// ============================================================================
// JAR Configuration
// ============================================================================

tasks.jar {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    archiveBaseName.set("freemind-ce")
    manifest {
        attributes(
            "Main-Class" to "freemind.main.FreeMindStarter",
            "Built-By" to "Denomas Engineering",
            "Implementation-Title" to "FreeMind Classic Edition",
            "Implementation-Version" to project.version,
            "Class-Path" to configurations.runtimeClasspath.get().files.joinToString(" ") { it.name }
        )
    }

    // Include resources from original locations
    from("freemind.properties")
    from("patterns.xml")
    from("patterns_updater.xslt")
    from("mindmap_menus.xml")
    from("dictionaries.properties")
    from(fileTree(".") { include("dictionary_*.ortho") })
    from(fileTree("images") { include("**/*") })
    from(fileTree("doc") { include("*.mm") })
}

// ============================================================================
// Distribution Configuration
// ============================================================================

tasks.distZip {
    archiveBaseName.set("freemind-ce")
}

tasks.distTar {
    archiveBaseName.set("freemind-ce")
}

// ============================================================================
// Custom Tasks for macOS Support
// ============================================================================

tasks.register<Copy>("prepareMacDist") {
    description = "Prepares distribution for macOS with proper key mappings"
    group = "Distribution"

    from("freemind.properties")
    into("build/distributions/mac")

    filter { line ->
        line.replace(Regex("(?i)^keystroke(.*)=(.*)\\bcontrol\\b"), "keystroke\$1=\$2meta")
            .replace(Regex("(?i)^keystroke(.*)=(.*)\\binsert\\b"), "keystroke\$1=\$2TAB")
    }
}

// ============================================================================
// Test Configuration
// ============================================================================

tasks.test {
    useJUnitPlatform()

    // Headless mode for CI compatibility (Swing tests throw HeadlessException without this)
    systemProperty("java.awt.headless", "true")

    testLogging {
        events("passed", "skipped", "failed")
        showStandardStreams = true
        exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
    }
}

// ============================================================================
// Code Coverage (JaCoCo)
// ============================================================================

tasks.jacocoTestReport {
    dependsOn(tasks.test)
    reports {
        xml.required.set(true)
        html.required.set(true)
    }
}

// ============================================================================
// Static Analysis (SpotBugs)
// ============================================================================

spotbugs {
    ignoreFailures.set(true)
    excludeFilter.set(file("config/spotbugs-exclude.xml"))
    // SpotBugs 6.x: configure report format via extension
    showProgress.set(false)
}

// Enable HTML reports for all SpotBugs tasks
afterEvaluate {
    tasks.withType(com.github.spotbugs.snom.SpotBugsTask::class.java).configureEach {
        reports.maybeCreate("html").apply {
            required.set(true)
        }
    }
}

// SpotBugs scans the main sourceSet classpath which includes plugin modules.
// The plugins directory is part of main sourceSet, so Gradle sees plugin outputs
// as implicit inputs to SpotBugs. Declare explicit dependencies on all plugin
// tasks that produce output in the shared directory to avoid validation errors.
tasks.matching { it.name.startsWith("spotbugs") }.configureEach {
    rootProject.subprojects.filter { it.path.startsWith(":freemind:plugins:") }.forEach { plugin ->
        plugin.tasks.matching {
            it.name in setOf(
                "compileJava", "compileTestJava", "processResources",
                "processTestResources", "classes", "testClasses", "jar", "test"
            )
        }.all {
            this@configureEach.dependsOn(this)
        }
    }
}

// ============================================================================
// Dependency Vulnerability Scanning (OWASP)
// ============================================================================

dependencyCheck {
    failBuildOnCVSS = 11f  // report only, never fail
    suppressionFile = "config/owasp-suppressions.xml"
    analyzers.assemblyEnabled = false
}

// ============================================================================
// JPackage Task (Modern Java Packaging)
// ============================================================================

tasks.register<Exec>("jpackageMac") {
    description = "Creates macOS .dmg package"
    group = "Distribution"
    dependsOn(tasks.installDist)
    doFirst { mkdir("build/jpackage") }

    commandLine(
        "jpackage",
        "--type", "dmg",
        "--dest", "build/jpackage",
        "--name", "FreeMind-CE",
        "--input", "build/install/freemind/lib",
        "--main-jar", "freemind-ce-${project.version}.jar",
        "--main-class", "freemind.main.FreeMindStarter",
        "--icon", "images/FreeMindWindowIconModern.icns",
        "--app-version", jpackageVersion,
        "--vendor", "Denomas Engineering",
        "--file-associations", "file-associations.properties",
        "--java-options", "-Xms64m",
        "--java-options", "-Xmx512m",
        "--java-options", "-Dapple.laf.useScreenMenuBar=true"
    )
}

tasks.register<Exec>("jpackageWin") {
    description = "Creates Windows .exe package"
    group = "Distribution"
    dependsOn(tasks.installDist)
    doFirst { mkdir("build/jpackage") }

    commandLine(
        "jpackage",
        "--type", "exe",
        "--dest", "build/jpackage",
        "--name", "FreeMind-CE",
        "--input", "build/install/freemind/lib",
        "--main-jar", "freemind-ce-${project.version}.jar",
        "--main-class", "freemind.main.FreeMindStarter",
        "--app-version", jpackageVersion,
        "--vendor", "Denomas Engineering",
        "--file-associations", "file-associations.properties",
        "--java-options", "-Xms64m",
        "--java-options", "-Xmx512m"
    )
}

tasks.register<Exec>("jpackageLinux") {
    description = "Creates Linux .deb package"
    group = "Distribution"
    dependsOn(tasks.installDist)
    doFirst { mkdir("build/jpackage") }

    commandLine(
        "jpackage",
        "--type", "deb",
        "--dest", "build/jpackage",
        "--name", "freemind-ce",
        "--input", "build/install/freemind/lib",
        "--main-jar", "freemind-ce-${project.version}.jar",
        "--main-class", "freemind.main.FreeMindStarter",
        "--icon", "images/FreeMindWindowIcon.png",
        "--app-version", jpackageVersion,
        "--vendor", "Denomas Engineering",
        "--file-associations", "file-associations.properties",
        "--java-options", "-Xms64m",
        "--java-options", "-Xmx512m"
    )
}

tasks.register<Exec>("jpackageLinuxRpm") {
    description = "Creates Linux .rpm package"
    group = "Distribution"
    dependsOn(tasks.installDist)
    doFirst { mkdir("build/jpackage") }

    commandLine(
        "jpackage",
        "--type", "rpm",
        "--dest", "build/jpackage",
        "--name", "freemind-ce",
        "--input", "build/install/freemind/lib",
        "--main-jar", "freemind-ce-${project.version}.jar",
        "--main-class", "freemind.main.FreeMindStarter",
        "--icon", "images/FreeMindWindowIcon.png",
        "--app-version", jpackageVersion,
        "--vendor", "Denomas Engineering",
        "--file-associations", "file-associations.properties",
        "--java-options", "-Xms64m",
        "--java-options", "-Xmx512m"
    )
}

tasks.register<Exec>("jpackageWinMsi") {
    description = "Creates Windows .msi installer package"
    group = "Distribution"
    dependsOn(tasks.installDist)
    doFirst { mkdir("build/jpackage") }

    commandLine(
        "jpackage",
        "--type", "msi",
        "--dest", "build/jpackage",
        "--name", "FreeMind-CE",
        "--input", "build/install/freemind/lib",
        "--main-jar", "freemind-ce-${project.version}.jar",
        "--main-class", "freemind.main.FreeMindStarter",
        "--app-version", jpackageVersion,
        "--vendor", "Denomas Engineering",
        "--file-associations", "file-associations.properties",
        "--win-per-user-install",
        "--win-menu",
        "--win-shortcut-prompt",
        "--java-options", "-Xms64m",
        "--java-options", "-Xmx512m"
    )
}

// ============================================================================
// Documentation Tasks
// ============================================================================

tasks.javadoc {
    source = sourceSets["main"].allJava
    classpath = sourceSets["main"].runtimeClasspath
    destinationDir = file("build/docs/javadoc")

    (options as StandardJavadocDocletOptions).apply {
        encoding = "UTF-8"
        addStringOption("Xdoclint:all,-missing", "-quiet")
    }
}
