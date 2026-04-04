import java.time.Duration

/*
 * FreeMind CE - Main Module Build Configuration
 * Denomas - 2026
 *
 * Modern Java 21 + Gradle + JAXB build system
 */

plugins {
    java
    application
    jacoco
    id("com.github.spotbugs") version "6.4.8"
    id("org.owasp.dependencycheck") version "12.2.0"
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

// Generate version.properties from Gradle project version (synced by release-please)
val generateVersionProperties by tasks.registering {
    description = "Generates version.properties with the current project version"
    group = "Generation"
    val outputDir = layout.buildDirectory.dir("generated-resources")
    outputs.dir(outputDir)
    doLast {
        val dir = outputDir.get().asFile
        dir.mkdirs()
        File(dir, "version.properties").writeText("freemind.version=${project.version}\n")
    }
}

tasks.processResources {
    dependsOn(generateVersionProperties)
    from(generateVersionProperties.map { it.outputs.files })
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

// jpackage requires numeric-only version (strips any suffix like -SNAPSHOT)
val jpackageVersion = project.version.toString().replace(Regex("-.*"), "")

val jaxbApiVersion = "2.3.1"
val jaxbImplVersion = "2.3.9"
val batikVersion = "1.19"
val fopVersion = "2.11"
val flatlafVersion = "3.7.1"
val jgoodiesFormsVersion = "1.9.0"
val jgoodiesCommonVersion = "1.8.1"
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

    // JGoodies (Forms and Look) - 1.8.0 has setDefaultDialogBorder API
    implementation("com.jgoodies:jgoodies-forms:${jgoodiesFormsVersion}")
    implementation("com.jgoodies:jgoodies-common:${jgoodiesCommonVersion}")

    // JOrtho (Spell Checker) - local JAR, not on Maven Central
    implementation(files("lib/jortho.jar"))

    // SimplyHTML (WYSIWYG HTML Editor) - local JARs
    implementation(files("lib/SimplyHTML/SimplyHTML.jar"))
    implementation(files("lib/SimplyHTML/gnu-regexp-1.1.4.jar"))

    // Apache Xalan (XSLT Processing)
    implementation("xalan:xalan:2.7.3")
    implementation("xalan:serializer:2.7.3")
    implementation("xml-apis:xml-apis:2.0.2")
    implementation("xerces:xercesImpl:2.12.2")

    // Jsoup (HTML Parsing) - 1.10.3 matches existing NodeTraversor API usage
    implementation("org.jsoup:jsoup:1.10.3")

    // Plugin dependencies
    implementation("org.codehaus.groovy:groovy-all:3.0.25")
    implementation(files("plugins/map/JMapViewer.jar"))
    implementation("org.apache.lucene:lucene-core:10.4.0")
    implementation("org.apache.lucene:lucene-analysis-common:10.4.0")
    implementation("org.apache.lucene:lucene-queryparser:10.4.0")
    implementation(files("plugins/help/jhall.jar"))
    // LaTeX plugin (JLaTeXMath) — built as separate module :freemind:plugins:latex
    implementation("org.scilab.forge:jlatexmath:1.0.7")

    // Batik (SVG Support) - for plugins
    implementation("org.apache.xmlgraphics:batik-all:${batikVersion}")

    // FOP (PDF Export) - for plugins
    implementation("org.apache.xmlgraphics:fop:${fopVersion}")

    // Logging (SLF4J + Logback)
    implementation("org.slf4j:slf4j-api:2.0.17")
    implementation("ch.qos.logback:logback-classic:1.5.32")

    // Testing - JUnit 5 with vintage engine for JUnit 3 backward compatibility
    testImplementation("org.junit.jupiter:junit-jupiter:6.0.3")
    testImplementation("org.junit.vintage:junit-vintage-engine:6.0.3")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher:6.0.3")
    testImplementation("junit:junit:${junitVersion}")
    testImplementation("org.mockito:mockito-core:5.23.0")

    // Property-Based Testing (jqwik)
    testImplementation("net.jqwik:jqwik:1.9.3")
    testImplementation("net.jqwik:jqwik-engine:1.9.3")

    // Fuzz Testing (Jazzer via JUnit integration)
    testImplementation("com.code-intelligence:jazzer-api:0.30.0")
    testImplementation("com.code-intelligence:jazzer-junit:0.30.0")

    // Fluent Assertions (AssertJ)
    testImplementation("org.assertj:assertj-core:3.27.7")

    // AssertJ Swing (GUI testing with screenshots)
    testImplementation("org.assertj:assertj-swing-junit:3.17.1")
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
            // Exclude Database collaboration plugin (hsqldb removed, unmaintained)
            exclude("**/database/**")
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
            "Built-By" to "Denomas",
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
    useJUnitPlatform {
        excludeTags("gui")
    }

    // Headless mode for CI compatibility (Swing tests throw HeadlessException without this)
    systemProperty("java.awt.headless", "true")

    testLogging {
        events("passed", "skipped", "failed")
        showStandardStreams = true
        exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
    }
}

tasks.register<Test>("testGui") {
    description = "Runs GUI tests with screenshot capture (requires display)"
    group = "Verification"

    testClassesDirs = sourceSets["test"].output.classesDirs
    classpath = sourceSets["test"].runtimeClasspath

    useJUnitPlatform {
        includeTags("gui")
    }

    // GUI tests need a real display — not headless
    systemProperty("java.awt.headless", "false")

    // Always re-run GUI tests — never use Gradle cache.
    // GUI tests depend on platform-specific rendering (fonts, display server)
    // so cached results from another runner or Java version are not reliable.
    outputs.upToDateWhen { false }

    testLogging {
        events("passed", "skipped", "failed")
        showStandardStreams = true
        exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
    }
}

tasks.register<Test>("testPerformance") {
    description = "Run performance tests (large file, timing)"
    group = "verification"
    useJUnitPlatform {
        includeTags("performance")
    }
    jvmArgs("-Xmx512m")
    systemProperty("java.awt.headless", "true")
    maxHeapSize = "512m"
    timeout.set(Duration.ofMinutes(10))
}

tasks.register<Test>("testChaos") {
    description = "Run chaos/resilience tests"
    group = "verification"
    useJUnitPlatform {
        includeTags("chaos")
    }
    systemProperty("java.awt.headless", "true")
    timeout.set(Duration.ofMinutes(5))
}

tasks.register<JavaExec>("showcaseScreenshots") {
    description = "Launches FreeMind CE with showcase mindmaps and captures full-screen desktop screenshots"
    group = "Verification"

    classpath = sourceSets["test"].runtimeClasspath
    mainClass.set("tests.freemind.gui.ShowcaseScreenshotCapture")
    systemProperty("java.awt.headless", "false")

    // Run from project root so relative paths to .mm files work
    workingDir = rootProject.projectDir

    // Always re-run
    outputs.upToDateWhen { false }
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

tasks.named<JacocoCoverageVerification>("jacocoTestCoverageVerification") {
    violationRules {
        rule {
            limit {
                minimum = "0.20".toBigDecimal()
            }
        }
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

// Copy doc/ into jpackage input directory (after installDist, before jpackage)
// jpackage --input maps lib/ → app/ in installed app.
// getFreemindBaseDir() resolves to <install>/app/ for jpackage'd apps.
// So doc/ must be inside lib/doc/ → becomes app/doc/ after installation.
val copyDocForJpackage by tasks.registering(Copy::class) {
    description = "Copies doc/ folder into jpackage input directory"
    group = "Distribution"
    dependsOn(tasks.installDist)
    from("doc")
    into("build/install/freemind/lib/doc")
    doLast {
        require(file("build/install/freemind/lib/doc/freemind.mm").exists()) {
            "doc/freemind.mm not found in jpackage input — Help menu will be broken!"
        }
        require(file("build/install/freemind/lib/doc/FM_Key_Mappings_Quick_Guide.pdf").exists()) {
            "FM_Key_Mappings_Quick_Guide.pdf not found in jpackage input — Key Documentation will be broken!"
        }
        require(file("build/install/freemind/lib/doc/FM_Key_Mappings_Quick_Guide_ru.pdf").exists()) {
            "Locale-specific PDFs not found in jpackage input — localized Help will be broken!"
        }
    }
}

tasks.register<Exec>("jpackageMac") {
    description = "Creates macOS .dmg package"
    group = "Distribution"
    dependsOn(copyDocForJpackage)
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
        "--vendor", "Denomas",
        "--file-associations", "file-associations.properties",
        "--java-options", "-Xms64m",
        "--java-options", "-Xmx512m",
        "--java-options", "-Xss8M",
        "--java-options", "-Dapple.laf.useScreenMenuBar=true",
        "--java-options", "--add-opens=java.desktop/java.awt=ALL-UNNAMED",
        "--java-options", "--add-opens=java.desktop/java.awt.event=ALL-UNNAMED",
        "--java-options", "--add-opens=java.desktop/javax.swing=ALL-UNNAMED",
        "--java-options", "--add-opens=java.desktop/javax.swing.text=ALL-UNNAMED",
        "--java-options", "--add-opens=java.base/java.lang=ALL-UNNAMED",
        "--java-options", "--add-opens=java.base/java.util=ALL-UNNAMED"
    )
}

tasks.register<Exec>("jpackageWin") {
    description = "Creates Windows .exe package"
    group = "Distribution"
    dependsOn(copyDocForJpackage)
    doFirst { mkdir("build/jpackage") }

    commandLine(
        "jpackage",
        "--type", "exe",
        "--dest", "build/jpackage",
        "--name", "FreeMind-CE",
        "--input", "build/install/freemind/lib",
        "--main-jar", "freemind-ce-${project.version}.jar",
        "--main-class", "freemind.main.FreeMindStarter",
        "--icon", "images/FreeMindWindowIcon.ico",
        "--app-version", jpackageVersion,
        "--vendor", "Denomas",
        "--file-associations", "file-associations.properties",
        "--win-shortcut",
        "--win-menu",
        "--win-menu-group", "FreeMind CE",
        "--java-options", "-Xms64m",
        "--java-options", "-Xmx512m",
        "--java-options", "-Xss8M",
        "--java-options", "--add-opens=java.desktop/java.awt=ALL-UNNAMED",
        "--java-options", "--add-opens=java.desktop/java.awt.event=ALL-UNNAMED",
        "--java-options", "--add-opens=java.desktop/javax.swing=ALL-UNNAMED",
        "--java-options", "--add-opens=java.desktop/javax.swing.text=ALL-UNNAMED",
        "--java-options", "--add-opens=java.base/java.lang=ALL-UNNAMED",
        "--java-options", "--add-opens=java.base/java.util=ALL-UNNAMED"
    )
}

tasks.register<Exec>("jpackageLinux") {
    description = "Creates Linux .deb package"
    group = "Distribution"
    dependsOn(copyDocForJpackage)
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
        "--vendor", "Denomas",
        "--file-associations", "file-associations.properties",
        "--java-options", "-Xms64m",
        "--java-options", "-Xmx512m",
        "--java-options", "-Xss8M",
        "--java-options", "--add-opens=java.desktop/java.awt=ALL-UNNAMED",
        "--java-options", "--add-opens=java.desktop/java.awt.event=ALL-UNNAMED",
        "--java-options", "--add-opens=java.desktop/javax.swing=ALL-UNNAMED",
        "--java-options", "--add-opens=java.desktop/javax.swing.text=ALL-UNNAMED",
        "--java-options", "--add-opens=java.base/java.lang=ALL-UNNAMED",
        "--java-options", "--add-opens=java.base/java.util=ALL-UNNAMED"
    )
}

tasks.register<Exec>("jpackageLinuxRpm") {
    description = "Creates Linux .rpm package"
    group = "Distribution"
    dependsOn(copyDocForJpackage)
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
        "--vendor", "Denomas",
        "--file-associations", "file-associations.properties",
        "--java-options", "-Xms64m",
        "--java-options", "-Xmx512m",
        "--java-options", "-Xss8M",
        "--java-options", "--add-opens=java.desktop/java.awt=ALL-UNNAMED",
        "--java-options", "--add-opens=java.desktop/java.awt.event=ALL-UNNAMED",
        "--java-options", "--add-opens=java.desktop/javax.swing=ALL-UNNAMED",
        "--java-options", "--add-opens=java.desktop/javax.swing.text=ALL-UNNAMED",
        "--java-options", "--add-opens=java.base/java.lang=ALL-UNNAMED",
        "--java-options", "--add-opens=java.base/java.util=ALL-UNNAMED"
    )
}

tasks.register<Exec>("jpackageWinMsi") {
    description = "Creates Windows .msi installer package"
    group = "Distribution"
    dependsOn(copyDocForJpackage)
    doFirst { mkdir("build/jpackage") }

    commandLine(
        "jpackage",
        "--type", "msi",
        "--dest", "build/jpackage",
        "--name", "FreeMind-CE",
        "--input", "build/install/freemind/lib",
        "--main-jar", "freemind-ce-${project.version}.jar",
        "--main-class", "freemind.main.FreeMindStarter",
        "--icon", "images/FreeMindWindowIcon.ico",
        "--app-version", jpackageVersion,
        "--vendor", "Denomas",
        "--file-associations", "file-associations.properties",
        "--win-per-user-install",
        "--win-shortcut",
        "--win-menu",
        "--win-menu-group", "FreeMind CE",
        "--win-shortcut-prompt",
        "--java-options", "-Xms64m",
        "--java-options", "-Xmx512m",
        "--java-options", "-Xss8M",
        "--java-options", "--add-opens=java.desktop/java.awt=ALL-UNNAMED",
        "--java-options", "--add-opens=java.desktop/java.awt.event=ALL-UNNAMED",
        "--java-options", "--add-opens=java.desktop/javax.swing=ALL-UNNAMED",
        "--java-options", "--add-opens=java.desktop/javax.swing.text=ALL-UNNAMED",
        "--java-options", "--add-opens=java.base/java.lang=ALL-UNNAMED",
        "--java-options", "--add-opens=java.base/java.util=ALL-UNNAMED"
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
