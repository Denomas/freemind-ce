package tests.freemind;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Source verification tests for code quality sweep (Phases A-H).
 * Scans source files to ensure code patterns are consistently applied.
 */
@DisplayName("Code Quality Sweep Verification")
class CodeQualityTest {

    @Test
    @DisplayName("No @SuppressWarnings(\"serial\") in non-generated code")
    void noSuppressWarningsSerial() throws IOException {
        List<String> violations = scanSourceFiles(
            "freemind",
            Pattern.compile("@SuppressWarnings\\(\"serial\"\\)"),
            true
        );
        assertTrue(violations.isEmpty(),
            "Found @SuppressWarnings(\"serial\") in:\n" + String.join("\n", violations));
    }

    @Test
    @DisplayName("No Integer.valueOf().intValue() boxing pattern")
    void noBoxingPattern() throws IOException {
        List<String> violations = scanSourceFiles(
            "freemind",
            Pattern.compile("Integer\\.valueOf\\([^)]+\\)\\.intValue\\(\\)" +
                "|Long\\.valueOf\\([^)]+\\)\\.longValue\\(\\)" +
                "|Double\\.valueOf\\([^)]+\\)\\.doubleValue\\(\\)"),
            true
        );
        assertTrue(violations.isEmpty(),
            "Found boxing pattern in:\n" + String.join("\n", violations));
    }

    @Test
    @DisplayName("No unparameterized getBytes() calls in non-generated code")
    void noDefaultEncodingGetBytes() throws IOException {
        Path root = findProjectRoot().resolve("freemind");
        List<String> violations = new ArrayList<>();

        try (Stream<Path> walk = Files.walk(root)) {
            List<Path> javaFiles = walk
                .filter(p -> p.toString().endsWith(".java"))
                .filter(p -> !p.toString().contains("generated-src"))
                .filter(p -> !p.toString().contains("tests"))
                .filter(Files::isRegularFile)
                .collect(Collectors.toList());

            for (Path file : javaFiles) {
                List<String> lines = Files.readAllLines(file);
                for (int i = 0; i < lines.size(); i++) {
                    String line = lines.get(i);
                    // Match .getBytes() without charset parameter
                    // Exclude .getBytes(StandardCharsets...) and .getBytes("...")
                    if (line.contains(".getBytes()") && !line.trim().startsWith("//")) {
                        violations.add(root.getParent().relativize(file) + ":" + (i + 1) + " " + line.trim());
                    }
                }
            }
        }
        assertTrue(violations.isEmpty(),
            "Found .getBytes() without explicit charset in:\n" + String.join("\n", violations));
    }

    @Test
    @DisplayName("No deprecated Class.newInstance() in non-generated code")
    void noDeprecatedClassNewInstance() throws IOException {
        Path root = findProjectRoot().resolve("freemind");
        List<String> violations = new ArrayList<>();

        try (Stream<Path> walk = Files.walk(root)) {
            List<Path> javaFiles = walk
                .filter(p -> p.toString().endsWith(".java"))
                .filter(p -> !p.toString().contains("generated-src"))
                .filter(Files::isRegularFile)
                .collect(Collectors.toList());

            Pattern deprecated = Pattern.compile(
                "(?:Class\\.forName\\([^)]+\\)\\.newInstance\\(\\)|hookClass\\.newInstance\\(\\))");

            for (Path file : javaFiles) {
                // Skip test files — they may contain the pattern in string literals/assertions
                if (file.toString().contains("tests")) continue;
                String content = Files.readString(file);
                var matcher = deprecated.matcher(content);
                while (matcher.find()) {
                    violations.add(root.getParent().relativize(file).toString());
                }
            }
        }
        assertTrue(violations.isEmpty(),
            "Found deprecated Class.newInstance() in:\n" + String.join("\n", violations));
    }

    @Test
    @DisplayName("No unsynchronized 'if (logger == null)' lazy init pattern")
    void noUnsynchronizedLoggerInit() throws IOException {
        Path root = findProjectRoot().resolve("freemind");
        List<String> violations = new ArrayList<>();

        try (Stream<Path> walk = Files.walk(root)) {
            List<Path> javaFiles = walk
                .filter(p -> p.toString().endsWith(".java"))
                .filter(p -> !p.toString().contains("generated-src"))
                .filter(Files::isRegularFile)
                .collect(Collectors.toList());

            Pattern loggerNull = Pattern.compile("if\\s*\\(\\s*logger\\s*==\\s*null\\s*\\)");

            for (Path file : javaFiles) {
                // FreeMind.java is exempt (bootstrap, single-threaded)
                if (file.getFileName().toString().equals("FreeMind.java")) continue;
                // Skip test files (may contain pattern in string literals/regex)
                if (file.toString().contains("tests")) continue;
                String content = Files.readString(file);
                // Skip commented lines
                String[] lines = content.split("\n");
                for (int i = 0; i < lines.length; i++) {
                    String line = lines[i].trim();
                    if (!line.startsWith("//") && loggerNull.matcher(line).find()) {
                        violations.add(root.getParent().relativize(file) + ":" + (i + 1));
                    }
                }
            }
        }
        assertTrue(violations.isEmpty(),
            "Found unsynchronized logger lazy init in:\n" + String.join("\n", violations));
    }

    @Test
    @DisplayName("No string literal mutex synchronization")
    void noStringLiteralMutex() throws IOException {
        List<String> violations = scanSourceFiles(
            "freemind",
            Pattern.compile("\"lockme\""),
            true
        );
        assertTrue(violations.isEmpty(),
            "Found string literal mutex in:\n" + String.join("\n", violations));
    }

    @Test
    @DisplayName("No deprecated Dialog.show() override in FilterComposerDialog")
    void noDeprecatedDialogShow() throws IOException {
        Path file = findProjectRoot()
            .resolve("freemind/freemind/controller/filter/FilterComposerDialog.java");
        String content = Files.readString(file);
        assertFalse(content.contains("super.show()"),
            "FilterComposerDialog must use setVisible() not deprecated show()");
        assertTrue(content.contains("super.setVisible("),
            "FilterComposerDialog must override setVisible(boolean)");
    }

    @Test
    @DisplayName("XmlBindingTools uses holder pattern (lock-free singleton)")
    void xmlBindingToolsUsesHolderPattern() throws IOException {
        Path file = findProjectRoot()
            .resolve("freemind/freemind/common/XmlBindingTools.java");
        String content = Files.readString(file);
        assertTrue(content.contains("Holder.INSTANCE"),
            "XmlBindingTools.getInstance() must use Holder pattern");
        assertFalse(content.contains("synchronized") && content.contains("getInstance"),
            "XmlBindingTools.getInstance() must not use synchronized");
        assertTrue(content.contains("private static class Holder"),
            "XmlBindingTools must have inner Holder class");
    }

    private List<String> scanSourceFiles(String baseDir, Pattern pattern,
            boolean excludeGenerated) throws IOException {
        Path root = findProjectRoot().resolve(baseDir);
        List<String> violations = new ArrayList<>();

        try (Stream<Path> walk = Files.walk(root)) {
            List<Path> javaFiles = walk
                .filter(p -> p.toString().endsWith(".java"))
                .filter(p -> !excludeGenerated || !p.toString().contains("generated-src"))
                .filter(Files::isRegularFile)
                .collect(Collectors.toList());

            for (Path file : javaFiles) {
                String content = Files.readString(file);
                if (pattern.matcher(content).find()) {
                    violations.add(root.getParent().relativize(file).toString());
                }
            }
        }
        return violations;
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
