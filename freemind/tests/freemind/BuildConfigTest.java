package tests.freemind;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Verifies build configuration consistency across all Gradle files (AC-10).
 *
 * Scans ALL .gradle.kts files to ensure vendor name, header comments,
 * and other metadata are consistent.
 */
@DisplayName("Build Configuration Consistency (AC-10)")
class BuildConfigTest {

    @Test
    @DisplayName("No .gradle.kts file contains 'Denomas Engineering'")
    void noGradleFileContainsDenomasEngineering() throws IOException {
        Path root = findProjectRoot();
        List<Path> gradleFiles;
        try (Stream<Path> walk = Files.walk(root)) {
            gradleFiles = walk
                .filter(p -> p.toString().endsWith(".gradle.kts"))
                .filter(Files::isRegularFile)
                .collect(Collectors.toList());
        }

        assertFalse(gradleFiles.isEmpty(),
            "At least one .gradle.kts file must exist in the project");

        StringBuilder violations = new StringBuilder();
        for (Path file : gradleFiles) {
            String content = Files.readString(file);
            if (content.contains("Denomas Engineering")) {
                violations.append("\n  - ")
                    .append(root.relativize(file));
            }
        }

        assertTrue(violations.isEmpty(),
            "Found 'Denomas Engineering' (should be 'Denomas') in:" + violations);
    }

    @Test
    @DisplayName("Root build.gradle.kts uses 'Denomas' as vendor")
    void rootBuildGradleHasDenomas() throws IOException {
        Path root = findProjectRoot();
        String content = Files.readString(root.resolve("build.gradle.kts"));
        assertFalse(content.contains("Denomas Engineering"),
            "Root build.gradle.kts must use 'Denomas', not 'Denomas Engineering'");
    }

    @Test
    @DisplayName("Module build.gradle.kts uses 'Denomas' as vendor")
    void moduleBuildGradleHasDenomas() throws IOException {
        Path root = findProjectRoot();
        Path moduleBuild = root.resolve("freemind/build.gradle.kts");
        assertTrue(Files.exists(moduleBuild), "freemind/build.gradle.kts must exist");
        String content = Files.readString(moduleBuild);
        assertTrue(content.contains("\"Denomas\""),
            "freemind/build.gradle.kts must contain vendor name 'Denomas'");
        assertFalse(content.contains("Denomas Engineering"),
            "freemind/build.gradle.kts must use 'Denomas', not 'Denomas Engineering'");
    }

    @Test
    @DisplayName("All plugin build.gradle.kts files use 'Denomas'")
    void pluginBuildFilesUseDenomas() throws IOException {
        Path root = findProjectRoot();
        Path pluginsDir = root.resolve("freemind/plugins");
        if (!Files.isDirectory(pluginsDir)) {
            return; // no plugins directory
        }

        List<Path> pluginBuildFiles;
        try (Stream<Path> walk = Files.walk(pluginsDir)) {
            pluginBuildFiles = walk
                .filter(p -> p.getFileName().toString().equals("build.gradle.kts"))
                .filter(Files::isRegularFile)
                .collect(Collectors.toList());
        }

        StringBuilder violations = new StringBuilder();
        for (Path file : pluginBuildFiles) {
            String content = Files.readString(file);
            if (content.contains("Denomas Engineering")) {
                violations.append("\n  - ")
                    .append(root.relativize(file));
            }
        }

        assertTrue(violations.isEmpty(),
            "Found 'Denomas Engineering' in plugin build files:" + violations);
    }

    @Test
    @DisplayName("gradle.properties uses 'Denomas' not 'Denomas Engineering'")
    void gradlePropertiesUseDenomas() throws IOException {
        Path root = findProjectRoot();
        Path propsFile = root.resolve("gradle.properties");
        if (!Files.exists(propsFile)) {
            return; // gradle.properties is optional
        }
        String content = Files.readString(propsFile);
        assertFalse(content.contains("Denomas Engineering"),
            "gradle.properties must use 'Denomas', not 'Denomas Engineering'");
    }

    @Test
    @DisplayName("settings.gradle.kts uses 'Denomas' not 'Denomas Engineering'")
    void settingsGradleUseDenomas() throws IOException {
        Path root = findProjectRoot();
        String content = Files.readString(root.resolve("settings.gradle.kts"));
        assertFalse(content.contains("Denomas Engineering"),
            "settings.gradle.kts must use 'Denomas', not 'Denomas Engineering'");
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
