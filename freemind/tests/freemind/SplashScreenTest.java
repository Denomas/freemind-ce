package tests.freemind;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Verifies that the splash screen has CE branding (AC-11).
 *
 * Uses source code and class verification (headless-compatible) because
 * FreeMindSplashModern requires a FreeMind instance with full Swing context.
 */
@DisplayName("Splash Screen CE Branding (AC-11)")
class SplashScreenTest {

    @Test
    @DisplayName("FreeMindSplashModern class exists and extends JFrame")
    void splashClassExistsAndExtendsJFrame() {
        try {
            Class<?> splashClass = Class.forName("freemind.main.FreeMindSplashModern");
            assertNotNull(splashClass);
            assertTrue(javax.swing.JFrame.class.isAssignableFrom(splashClass),
                "FreeMindSplashModern must extend JFrame");
        } catch (ClassNotFoundException e) {
            fail("freemind.main.FreeMindSplashModern class must exist");
        }
    }

    @Test
    @DisplayName("Splash window title is 'FreeMind CE'")
    void splashTitleContainsCE() throws IOException {
        String source = readSplashSource();
        assertTrue(source.contains("super(\"FreeMind CE\")"),
            "Splash constructor must set title to 'FreeMind CE'");
    }

    @Test
    @DisplayName("Splash draws 'Classic Edition' subtitle")
    void splashDrawsClassicEditionSubtitle() throws IOException {
        String source = readSplashSource();
        assertTrue(source.contains("Classic Edition"),
            "Splash must draw 'Classic Edition' subtitle via Graphics2D");
    }

    @Test
    @DisplayName("Splash draws version text")
    void splashDrawsVersionText() throws IOException {
        String source = readSplashSource();
        assertTrue(source.contains("getFreemindVersion"),
            "Splash must draw version text from FreeMind.getFreemindVersion()");
    }

    @Test
    @DisplayName("Splash has serialVersionUID (no @SuppressWarnings)")
    void splashHasSerialVersionUID() throws IOException {
        String source = readSplashSource();
        assertTrue(source.contains("serialVersionUID"),
            "FreeMindSplashModern must define serialVersionUID");
        assertFalse(source.contains("@SuppressWarnings(\"serial\")"),
            "FreeMindSplashModern must not use @SuppressWarnings(\"serial\")");
    }

    @Test
    @DisplayName("Splash has AccessibleContext for screen readers")
    void splashHasAccessibleContext() throws IOException {
        String source = readSplashSource();
        assertTrue(source.contains("setAccessibleName"),
            "FreeMindSplashModern must set accessible name for screen readers");
    }

    private String readSplashSource() throws IOException {
        Path path = findProjectRoot().resolve(
            "freemind/freemind/main/FreeMindSplashModern.java");
        assertTrue(Files.exists(path),
            "FreeMindSplashModern.java must exist at: " + path);
        return Files.readString(path);
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
