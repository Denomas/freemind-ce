package tests.freemind;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Structural quality tests that verify architecture and code quality invariants
 * by scanning source files at test time.
 */
@DisplayName("Structural Quality Invariants")
class StructuralQualityTest {

    /**
     * Maximum allowed System.out/System.err references in production code.
     * This is a bounded count to prevent explosion -- the goal is to reduce over time.
     */
    private static final int MAX_SYSTEM_OUT_ERR_COUNT = 200;

    /**
     * Maximum allowed empty catch blocks in production code.
     */
    private static final int MAX_EMPTY_CATCH_BLOCKS = 200;

    /**
     * Maximum allowed TODO/FIXME comments in source code.
     */
    private static final int MAX_TODO_FIXME_COUNT = 300;

    @Test
    @DisplayName("System.out/System.err usage in production code is bounded")
    void systemOutErrBounded() throws IOException {
        Path root = findProjectRoot().resolve("freemind");
        int count = 0;

        try (Stream<Path> walk = Files.walk(root)) {
            List<Path> javaFiles = productionJavaFiles(walk);

            Pattern sysOutErr = Pattern.compile(
                    "System\\.(out|err)\\.(println|print|printf|write|format)\\(");

            for (Path file : javaFiles) {
                String content = Files.readString(file);
                String[] lines = content.split("\n");
                for (String line : lines) {
                    String trimmed = line.trim();
                    if (!trimmed.startsWith("//") && !trimmed.startsWith("*")
                            && sysOutErr.matcher(trimmed).find()) {
                        count++;
                    }
                }
            }
        }

        assertTrue(count <= MAX_SYSTEM_OUT_ERR_COUNT,
                "System.out/err usage count (" + count + ") exceeds bound ("
                        + MAX_SYSTEM_OUT_ERR_COUNT + "). Migrate to logger.");
    }

    @Test
    @DisplayName("Serializable classes have serialVersionUID")
    void serializableClassesHaveSerialVersionUID() throws IOException {
        Path root = findProjectRoot().resolve("freemind");
        List<String> violations = new ArrayList<>();

        Pattern implementsSerializable = Pattern.compile(
                "class\\s+\\w+.*\\bimplements\\b[^{]*\\bSerializable\\b");

        try (Stream<Path> walk = Files.walk(root)) {
            List<Path> javaFiles = productionJavaFiles(walk);

            for (Path file : javaFiles) {
                String content = Files.readString(file);
                if (implementsSerializable.matcher(content).find()
                        && !content.contains("serialVersionUID")) {
                    violations.add(root.getParent().relativize(file).toString());
                }
            }
        }

        assertTrue(violations.isEmpty(),
                "Serializable classes without serialVersionUID:\n"
                        + String.join("\n", violations));
    }

    @Test
    @DisplayName("Empty catch blocks are bounded")
    void emptyCatchBlocksBounded() throws IOException {
        Path root = findProjectRoot().resolve("freemind");
        int count = 0;

        // Match catch block followed by optional whitespace and closing brace
        Pattern emptyCatch = Pattern.compile(
                "catch\\s*\\([^)]*\\)\\s*\\{\\s*\\}");

        try (Stream<Path> walk = Files.walk(root)) {
            List<Path> javaFiles = productionJavaFiles(walk);

            for (Path file : javaFiles) {
                String content = Files.readString(file);
                Matcher matcher = emptyCatch.matcher(content);
                while (matcher.find()) {
                    count++;
                }
            }
        }

        assertTrue(count <= MAX_EMPTY_CATCH_BLOCKS,
                "Empty catch block count (" + count + ") exceeds bound ("
                        + MAX_EMPTY_CATCH_BLOCKS + "). Add logging or comments.");
    }

    @Test
    @DisplayName("freemind.properties is valid and loadable")
    void propertiesFileLoadable() throws IOException {
        Path propsFile = findProjectRoot()
                .resolve("freemind/freemind.properties");
        assertTrue(Files.exists(propsFile),
                "freemind.properties must exist");

        Properties props = new Properties();
        try (InputStream is = Files.newInputStream(propsFile)) {
            props.load(is);
        }

        assertFalse(props.isEmpty(),
                "freemind.properties must not be empty");
        // Verify some known keys exist
        assertNotNull(props.getProperty("el__max_default_window_height"),
                "Expected property 'el__max_default_window_height' not found");
    }

    @Test
    @DisplayName("Resources_en.properties is loadable")
    void resourceBundleLoadable() throws IOException {
        Path resourceFile = findProjectRoot()
                .resolve("freemind/Resources_en.properties");
        assertTrue(Files.exists(resourceFile),
                "Resources_en.properties must exist");

        Properties props = new Properties();
        try (InputStream is = Files.newInputStream(resourceFile)) {
            props.load(is);
        }

        assertFalse(props.isEmpty(),
                "Resources_en.properties must not be empty");
    }

    @Test
    @DisplayName("freemind_actions.xsd is well-formed XML")
    void xsdSchemaWellFormed() throws Exception {
        Path xsdFile = findProjectRoot()
                .resolve("freemind/freemind_actions.xsd");
        assertTrue(Files.exists(xsdFile),
                "freemind_actions.xsd must exist");

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        // This will throw if the XML is malformed
        assertDoesNotThrow(() -> builder.parse(xsdFile.toFile()),
                "freemind_actions.xsd must be well-formed XML");
    }

    @Test
    @DisplayName("jaxb-bindings.xjb is well-formed XML")
    void jaxbBindingsWellFormed() throws Exception {
        Path xjbFile = findProjectRoot()
                .resolve("freemind/jaxb-bindings.xjb");
        assertTrue(Files.exists(xjbFile),
                "jaxb-bindings.xjb must exist");

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        assertDoesNotThrow(() -> builder.parse(xjbFile.toFile()),
                "jaxb-bindings.xjb must be well-formed XML");
    }

    @Test
    @DisplayName("TODO/FIXME count is bounded")
    void todoFixmeCountBounded() throws IOException {
        Path root = findProjectRoot().resolve("freemind");
        int count = 0;

        Pattern todoFixme = Pattern.compile("(?i)\\b(TODO|FIXME)\\b");

        try (Stream<Path> walk = Files.walk(root)) {
            List<Path> javaFiles = walk
                    .filter(p -> p.toString().endsWith(".java"))
                    .filter(p -> !p.toString().contains("generated-src"))
                    .filter(Files::isRegularFile)
                    .collect(Collectors.toList());

            for (Path file : javaFiles) {
                String content = Files.readString(file);
                Matcher matcher = todoFixme.matcher(content);
                while (matcher.find()) {
                    count++;
                }
            }
        }

        assertTrue(count <= MAX_TODO_FIXME_COUNT,
                "TODO/FIXME count (" + count + ") exceeds bound ("
                        + MAX_TODO_FIXME_COUNT + "). Address or triage them.");
    }

    @Test
    @DisplayName("Plugin XML descriptors reference existing classes")
    void pluginXmlClassesExist() throws Exception {
        Path root = findProjectRoot().resolve("freemind");
        List<String> violations = new ArrayList<>();

        // Gather all plugin XML files from both accessories/plugins and plugins/
        List<Path> pluginXmls = new ArrayList<>();
        Path accessoriesPlugins = root.resolve("accessories/plugins");
        Path pluginsDir = root.resolve("plugins");

        if (Files.isDirectory(accessoriesPlugins)) {
            try (Stream<Path> walk = Files.walk(accessoriesPlugins)) {
                pluginXmls.addAll(walk
                        .filter(p -> p.toString().endsWith(".xml"))
                        .filter(p -> !p.getFileName().toString().startsWith("build"))
                        .filter(Files::isRegularFile)
                        .collect(Collectors.toList()));
            }
        }
        if (Files.isDirectory(pluginsDir)) {
            try (Stream<Path> walk = Files.walk(pluginsDir)) {
                pluginXmls.addAll(walk
                        .filter(p -> p.toString().endsWith(".xml"))
                        .filter(p -> !p.getFileName().toString().startsWith("build"))
                        // Exclude help doc files
                        .filter(p -> !p.toString().contains("/help/doc/"))
                        .filter(Files::isRegularFile)
                        .collect(Collectors.toList()));
            }
        }

        assertFalse(pluginXmls.isEmpty(), "Should find plugin XML descriptors");

        // Extract class_name attributes from plugin_action and plugin_registration
        Pattern classNameAttr = Pattern.compile(
                "class_name=\"([^\"]+)\"");

        // Known mode class names to skip (these are mode identifiers, not plugin classes)
        List<String> modeClasses = List.of(
                "freemind.modes.mindmapmode",
                "freemind.modes.browsemode",
                "freemind.modes.filemode");

        for (Path xmlFile : pluginXmls) {
            String content = Files.readString(xmlFile);
            Matcher matcher = classNameAttr.matcher(content);
            while (matcher.find()) {
                String className = matcher.group(1);
                // Skip mode class references
                if (modeClasses.contains(className)) {
                    continue;
                }
                // Convert dotted class name to path, check if .java file exists
                String relativePath = className.replace('.', '/') + ".java";
                Path javaFile = root.resolve(relativePath);
                // Also check with $ for inner classes
                String outerClass = className.contains("$")
                        ? className.substring(0, className.indexOf('$')).replace('.', '/') + ".java"
                        : null;
                if (!Files.exists(javaFile)
                        && (outerClass == null || !Files.exists(root.resolve(outerClass)))) {
                    violations.add(root.getParent().relativize(xmlFile)
                            + " -> " + className);
                }
            }
        }

        assertTrue(violations.isEmpty(),
                "Plugin XMLs reference non-existent classes:\n"
                        + String.join("\n", violations));
    }

    /**
     * Maximum allowed test classes missing @DisplayName annotation.
     * Goal is to reduce this to 0 over time.
     */
    private static final int MAX_MISSING_DISPLAY_NAME = 30;

    @Test
    @DisplayName("Test classes without @DisplayName are bounded")
    void testClassesDisplayNameBounded() throws IOException {
        Path testRoot = findProjectRoot().resolve("freemind/tests");
        List<String> violations = new ArrayList<>();

        if (!Files.isDirectory(testRoot)) {
            return;
        }

        try (Stream<Path> walk = Files.walk(testRoot)) {
            List<Path> testFiles = walk
                    .filter(p -> p.toString().endsWith(".java"))
                    .filter(Files::isRegularFile)
                    .collect(Collectors.toList());

            for (Path file : testFiles) {
                String content = Files.readString(file);
                // Check if file contains @Test annotation (JUnit 5)
                if (content.contains("@Test")) {
                    // Find the class declaration line and check if @DisplayName
                    // appears in the lines preceding it
                    String[] lines = content.split("\n");
                    boolean foundDisplayName = false;
                    for (int i = 0; i < lines.length; i++) {
                        String line = lines[i].trim();
                        if (line.contains("@DisplayName")) {
                            foundDisplayName = true;
                        }
                        // Match class declaration (not inner classes)
                        if (line.matches("(public\\s+)?class\\s+\\w+.*\\{?")
                                && !line.contains("static class")
                                && !line.contains("private class")) {
                            if (!foundDisplayName) {
                                violations.add(testRoot.getParent()
                                        .relativize(file).toString());
                            }
                            break;
                        }
                    }
                }
            }
        }

        assertTrue(violations.size() <= MAX_MISSING_DISPLAY_NAME,
                "Test classes without @DisplayName (" + violations.size()
                        + ") exceeds bound (" + MAX_MISSING_DISPLAY_NAME
                        + "). Add @DisplayName to:\n"
                        + String.join("\n", violations));
    }

    // --- Utility methods ---

    private List<Path> productionJavaFiles(Stream<Path> walk) {
        return walk
                .filter(p -> p.toString().endsWith(".java"))
                .filter(p -> !p.toString().contains("generated-src"))
                .filter(p -> !p.toString().contains("tests"))
                .filter(Files::isRegularFile)
                .collect(Collectors.toList());
    }

    private Path findProjectRoot() {
        Path current = Paths.get(System.getProperty("user.dir"));
        while (current != null) {
            if (Files.exists(current.resolve("settings.gradle.kts"))) {
                return current;
            }
            current = current.getParent();
        }
        return Paths.get(System.getProperty("user.dir"));
    }
}
