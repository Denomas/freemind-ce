package tests.freemind;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for default property values in freemind.properties,
 * gradle build configuration, and resource bundles.
 */
@DisplayName("Default Properties")
class DefaultPropertiesTest {

    // ========================================================================
    // AC-5c: use_split_pane=false default
    // ========================================================================

    @Nested
    @DisplayName("freemind.properties defaults")
    class FreeMindPropertiesDefaults {

        @Test
        @DisplayName("use_split_pane defaults to false (AC-5c)")
        void useSplitPaneDefaultIsFalse() throws IOException {
            Properties props = loadFreeMindProperties();
            assertEquals("false", props.getProperty("use_split_pane"),
                "use_split_pane must default to false to hide the split pane");
        }

        @Test
        @DisplayName("initial_mode defaults to MindMap")
        void initialModeDefaultIsMindMap() throws IOException {
            Properties props = loadFreeMindProperties();
            assertEquals("MindMap", props.getProperty("initial_mode"),
                "initial_mode must default to MindMap");
        }

        @Test
        @DisplayName("language defaults to automatic")
        void languageDefaultIsAutomatic() throws IOException {
            Properties props = loadFreeMindProperties();
            assertEquals("automatic", props.getProperty("language"),
                "language must default to automatic");
        }

        @Test
        @DisplayName("webDocuLocation exists and starts with https:// (AC-8)")
        void webDocuLocationExistsAndIsHttps() throws IOException {
            Properties props = loadFreeMindProperties();
            String webDocu = props.getProperty("webDocuLocation");
            assertNotNull(webDocu, "webDocuLocation property must exist");
            assertTrue(webDocu.startsWith("https://"),
                "webDocuLocation must start with https:// but was: " + webDocu);
        }

        @Test
        @DisplayName("webDocuLocation points to freemind-ce repository")
        void webDocuLocationPointsToFreeMindCe() throws IOException {
            Properties props = loadFreeMindProperties();
            String webDocu = props.getProperty("webDocuLocation");
            assertNotNull(webDocu, "webDocuLocation property must exist");
            assertTrue(webDocu.contains("freemind-ce"),
                "webDocuLocation must reference freemind-ce but was: " + webDocu);
        }

        private Properties loadFreeMindProperties() throws IOException {
            Properties props = new Properties();
            try (InputStream is = getClass().getClassLoader().getResourceAsStream("freemind.properties")) {
                assertNotNull(is, "freemind.properties must be loadable from classpath");
                props.load(is);
            }
            return props;
        }
    }

    // ========================================================================
    // AC-10: Vendor name "Denomas" not "Denomas Engineering"
    // ========================================================================

    @Nested
    @DisplayName("Vendor name (AC-10)")
    class VendorName {

        @Test
        @DisplayName("build.gradle.kts vendor is 'Denomas' without 'Engineering'")
        void vendorDoesNotContainEngineering() throws IOException {
            // Read build.gradle.kts from project root to find --vendor arguments
            Path buildFile = findProjectRoot().resolve("freemind/build.gradle.kts");
            assertTrue(Files.exists(buildFile),
                "freemind/build.gradle.kts must exist at: " + buildFile);

            String content = Files.readString(buildFile);
            // The vendor is specified as: "--vendor", "Denomas",
            assertTrue(content.contains("\"Denomas\""),
                "build.gradle.kts must contain vendor name 'Denomas'");
            assertFalse(content.contains("Denomas Engineering"),
                "Vendor name must be 'Denomas', not 'Denomas Engineering'");
        }

        private Path findProjectRoot() {
            // Walk up from CWD to find the project root (contains settings.gradle.kts)
            Path current = Paths.get(System.getProperty("user.dir"));
            while (current != null) {
                if (Files.exists(current.resolve("settings.gradle.kts"))) {
                    return current;
                }
                current = current.getParent();
            }
            // Fallback: assume CWD is project root
            return Paths.get(System.getProperty("user.dir"));
        }
    }

    // ========================================================================
    // AC-9: About dialog CE credits
    // ========================================================================

    @Nested
    @DisplayName("About dialog credits (AC-9)")
    class AboutDialogCredits {

        @Test
        @DisplayName("about_text contains 'FreeMind CE'")
        void aboutTextContainsFreeMindCe() throws IOException {
            String aboutText = getAboutText();
            assertTrue(aboutText.contains("FreeMind CE"),
                "about_text must mention 'FreeMind CE'");
        }

        @Test
        @DisplayName("about_text contains 'Tolga Karatas'")
        void aboutTextContainsTolgaKaratas() throws IOException {
            String aboutText = getAboutText();
            assertTrue(aboutText.contains("Tolga Karatas"),
                "about_text must credit 'Tolga Karatas'");
        }

        @Test
        @DisplayName("about_text contains 'Denomas'")
        void aboutTextContainsDenomas() throws IOException {
            String aboutText = getAboutText();
            assertTrue(aboutText.contains("Denomas"),
                "about_text must mention 'Denomas'");
        }

        @Test
        @DisplayName("about_text contains GitHub URL with 'freemind-ce'")
        void aboutTextContainsGitHubUrl() throws IOException {
            String aboutText = getAboutText();
            assertTrue(aboutText.contains("freemind-ce"),
                "about_text must contain GitHub URL with 'freemind-ce'");
        }

        private String getAboutText() throws IOException {
            Properties props = new Properties();
            try (InputStream is = getClass().getClassLoader().getResourceAsStream("Resources_en.properties")) {
                assertNotNull(is, "Resources_en.properties must be loadable from classpath");
                props.load(is);
            }
            String aboutText = props.getProperty("about_text");
            assertNotNull(aboutText, "about_text property must exist in Resources_en.properties");
            return aboutText;
        }
    }
}
