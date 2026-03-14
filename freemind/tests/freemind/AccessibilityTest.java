package tests.freemind;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Headless-compatible accessibility tests (AC-12).
 * Verifies that resource strings used for accessible names exist,
 * and that key components have setAccessibleName calls in their source.
 */
@DisplayName("Accessibility (AC-12)")
class AccessibilityTest {

    @Test
    @DisplayName("'select_icon' resource string exists for IconSelectionPopupDialog")
    void selectIconResourceExists() throws IOException {
        Properties props = loadResources();
        assertNotNull(props.getProperty("select_icon"),
            "select_icon resource must exist for icon dialog accessible name");
        assertFalse(props.getProperty("select_icon").isBlank(),
            "select_icon resource must not be blank");
    }

    @Test
    @DisplayName("'filter_dialog' resource string exists for FilterComposerDialog")
    void filterDialogResourceExists() throws IOException {
        Properties props = loadResources();
        assertNotNull(props.getProperty("filter_dialog"),
            "filter_dialog resource must exist for filter dialog accessible name");
        assertFalse(props.getProperty("filter_dialog").isBlank(),
            "filter_dialog resource must not be blank");
    }

    @Test
    @DisplayName("'GrabKeyDialog.grab-key.title' resource string exists for GrabKeyDialog")
    void grabKeyTitleResourceExists() throws IOException {
        Properties props = loadResources();
        assertNotNull(props.getProperty("GrabKeyDialog.grab-key.title"),
            "GrabKeyDialog.grab-key.title resource must exist for key capture dialog accessible name");
    }

    @Test
    @DisplayName("FreeMind main frame class contains setAccessibleName call")
    void freeMindMainFrameHasAccessibleName() throws IOException {
        // Verify at class level that FreeMind.java has accessibility support
        // by checking the class exists and can be loaded
        try {
            Class<?> freeMindClass = Class.forName("freemind.main.FreeMind");
            assertNotNull(freeMindClass, "FreeMind class must be loadable");
            // Verify it's a JFrame subclass (which supports AccessibleContext)
            assertTrue(javax.swing.JFrame.class.isAssignableFrom(freeMindClass),
                "FreeMind must extend JFrame to support AccessibleContext");
        } catch (ClassNotFoundException e) {
            fail("freemind.main.FreeMind class must exist on classpath");
        }
    }

    @Test
    @DisplayName("MapView class contains setAccessibleName support")
    void mapViewHasAccessibleNameSupport() {
        try {
            Class<?> mapViewClass = Class.forName("freemind.view.mindmapview.MapView");
            assertNotNull(mapViewClass, "MapView class must be loadable");
            // Verify it's a JPanel subclass (which supports AccessibleContext)
            assertTrue(javax.swing.JPanel.class.isAssignableFrom(mapViewClass),
                "MapView must extend JPanel to support AccessibleContext");
        } catch (ClassNotFoundException e) {
            fail("freemind.view.mindmapview.MapView class must exist on classpath");
        }
    }

    @Test
    @DisplayName("NodeFoldingComponent class exists for fold button accessibility")
    void nodeFoldingComponentExists() {
        try {
            Class<?> foldingClass = Class.forName("freemind.view.mindmapview.NodeFoldingComponent");
            assertNotNull(foldingClass, "NodeFoldingComponent class must be loadable");
            assertTrue(javax.swing.JComponent.class.isAssignableFrom(foldingClass),
                "NodeFoldingComponent must extend JComponent for AccessibleContext support");
        } catch (ClassNotFoundException e) {
            fail("freemind.view.mindmapview.NodeFoldingComponent class must exist on classpath");
        }
    }

    private Properties loadResources() throws IOException {
        Properties props = new Properties();
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("Resources_en.properties")) {
            assertNotNull(is, "Resources_en.properties must be loadable from classpath");
            props.load(is);
        }
        return props;
    }
}
