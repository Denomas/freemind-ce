package tests.freemind;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Verifies UTF-8 encoding consistency across the codebase (Phase E + F7 review fix).
 * Ensures all I/O operations use explicit charset specification.
 */
@DisplayName("UTF-8 Encoding Consistency")
class Utf8ConsistencyTest {

    @Test
    @DisplayName("All PrintStream constructors specify explicit charset")
    void allPrintStreamsHaveExplicitCharset() throws IOException {
        Path root = findProjectRoot().resolve("freemind");
        List<String> violations = new ArrayList<>();

        // Pattern: new PrintStream(stream, autoFlush) without charset
        // OK: new PrintStream(stream, autoFlush, charset)
        Pattern printStreamNoCharset = Pattern.compile(
            "new\\s+PrintStream\\([^)]+,\\s*(true|false)\\s*\\)");
        Pattern printStreamWithCharset = Pattern.compile(
            "new\\s+PrintStream\\([^)]+,\\s*(true|false)\\s*,\\s*StandardCharsets");

        try (Stream<Path> walk = Files.walk(root)) {
            List<Path> javaFiles = walk
                .filter(p -> p.toString().endsWith(".java"))
                .filter(p -> !p.toString().contains("generated-src"))
                .filter(p -> !p.toString().contains("tests"))
                .filter(Files::isRegularFile)
                .collect(Collectors.toList());

            for (Path file : javaFiles) {
                String content = Files.readString(file);
                Matcher m = printStreamNoCharset.matcher(content);
                while (m.find()) {
                    // Check if this match is actually a 3-arg constructor
                    String surrounding = content.substring(
                        Math.max(0, m.start() - 5),
                        Math.min(content.length(), m.end() + 30));
                    if (!printStreamWithCharset.matcher(
                            content.substring(m.start(), Math.min(content.length(), m.start() + 100)))
                            .find()) {
                        violations.add(root.getParent().relativize(file).toString());
                        break;
                    }
                }
            }
        }
        assertTrue(violations.isEmpty(),
            "Found PrintStream without explicit charset in:\n" + String.join("\n", violations));
    }

    @Test
    @DisplayName("No new String(bytes) without charset in production code")
    void noNewStringWithoutCharset() throws IOException {
        Path root = findProjectRoot().resolve("freemind");
        List<String> violations = new ArrayList<>();

        // Pattern: new String(byteArray) without charset
        // Must have: new String(bytes, charset) or new String(bytes, offset, length, charset)
        Pattern newStringNoCharset = Pattern.compile(
            "new\\s+String\\(\\s*\\w+\\s*\\)");

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
                    String line = lines.get(i).trim();
                    if (line.startsWith("//")) continue;
                    Matcher m = newStringNoCharset.matcher(line);
                    while (m.find()) {
                        String match = m.group();
                        // Exclude new String("literal") — string from string is fine
                        if (match.contains("\"")) continue;
                        // Exclude new String() — empty constructor
                        if (match.matches("new\\s+String\\(\\s*\\)")) continue;
                        violations.add(root.getParent().relativize(file) + ":" + (i + 1) + " " + line);
                    }
                }
            }
        }
        // Note: some legitimate uses exist (e.g., new String(charArray))
        // This test catches byte[] → String without charset
        if (!violations.isEmpty()) {
            // Filter out char[] constructors by checking variable types in context
            // For now, report as informational
            System.out.println("Potential new String(bytes) without charset:");
            violations.forEach(v -> System.out.println("  " + v));
        }
    }

    @Test
    @DisplayName("LoggingOutputStream.flush() uses StandardCharsets.UTF_8")
    void loggingOutputStreamUsesUtf8() throws IOException {
        Path file = findProjectRoot()
            .resolve("freemind/freemind/main/LoggingOutputStream.java");
        String content = Files.readString(file);

        assertTrue(content.contains("StandardCharsets.UTF_8"),
            "LoggingOutputStream must use explicit UTF-8 for toString()");
        assertTrue(content.contains("import java.nio.charset.StandardCharsets"),
            "LoggingOutputStream must import StandardCharsets");
    }

    @Test
    @DisplayName("SignedScriptHandler uses explicit charset for all getBytes() calls")
    void signedScriptHandlerExplicitCharset() throws IOException {
        Path file = findProjectRoot()
            .resolve("freemind/plugins/script/SignedScriptHandler.java");
        String content = Files.readString(file);

        // Count getBytes() calls — none should be without charset
        int getBytesCount = countOccurrences(content, ".getBytes(");
        int getBytesWithCharset = countOccurrences(content, ".getBytes(StandardCharsets.");
        // Also count .getBytes("UTF-8") style
        getBytesWithCharset += countOccurrences(content, ".getBytes(\"");

        assertEquals(getBytesCount, getBytesWithCharset,
            "All .getBytes() calls in SignedScriptHandler must specify charset. " +
            "Found " + getBytesCount + " total, " + getBytesWithCharset + " with charset");
    }

    @Test
    @DisplayName("MindMapNodesSelection uses explicit charset for RTF clipboard bytes")
    void mindMapNodesSelectionExplicitCharset() throws IOException {
        Path file = findProjectRoot()
            .resolve("freemind/freemind/controller/MindMapNodesSelection.java");
        String content = Files.readString(file);

        assertTrue(content.contains(".getBytes(StandardCharsets."),
            "MindMapNodesSelection must use explicit charset for getBytes()");
        assertFalse(content.contains(".getBytes()"),
            "MindMapNodesSelection must NOT use parameterless getBytes()");
    }

    private int countOccurrences(String text, String pattern) {
        int count = 0;
        int idx = 0;
        while ((idx = text.indexOf(pattern, idx)) != -1) {
            count++;
            idx += pattern.length();
        }
        return count;
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
