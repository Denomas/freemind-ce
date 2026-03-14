package tests.freemind;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests that FlatLaf.properties is available on the classpath root
 * and contains expected UI customization keys (AC-5a).
 */
@DisplayName("FlatLaf.properties auto-loading")
class FlatLafPropertiesTest {

    @Test
    @DisplayName("FlatLaf.properties exists on classpath root")
    void flatLafPropertiesExistsOnClasspath() {
        assertNotNull(
            getClass().getClassLoader().getResource("FlatLaf.properties"),
            "FlatLaf.properties must be present at classpath root for FlatLaf auto-loading"
        );
    }

    @Test
    @DisplayName("FlatLaf.properties is loadable as a Properties file")
    void flatLafPropertiesIsLoadable() throws IOException {
        Properties props = loadFlatLafProperties();
        assertFalse(props.isEmpty(), "FlatLaf.properties must not be empty");
    }

    @Test
    @DisplayName("Button.arc rounding property is defined")
    void buttonArcPropertyDefined() throws IOException {
        Properties props = loadFlatLafProperties();
        assertNotNull(props.getProperty("Button.arc"),
            "Button.arc must be defined for rounded buttons");
    }

    @Test
    @DisplayName("ScrollBar.thumbArc property is defined")
    void scrollBarThumbArcPropertyDefined() throws IOException {
        Properties props = loadFlatLafProperties();
        assertNotNull(props.getProperty("ScrollBar.thumbArc"),
            "ScrollBar.thumbArc must be defined for rounded scrollbar thumbs");
    }

    @Test
    @DisplayName("Component.arc rounding property is defined")
    void componentArcPropertyDefined() throws IOException {
        Properties props = loadFlatLafProperties();
        assertNotNull(props.getProperty("Component.arc"),
            "Component.arc must be defined for general component rounding");
    }

    @Test
    @DisplayName("Component.focusWidth property is defined")
    void componentFocusWidthPropertyDefined() throws IOException {
        Properties props = loadFlatLafProperties();
        assertNotNull(props.getProperty("Component.focusWidth"),
            "Component.focusWidth must be defined for focus indicator styling");
    }

    @Test
    @DisplayName("Tree.showDefaultIcons property is defined")
    void treeShowDefaultIconsPropertyDefined() throws IOException {
        Properties props = loadFlatLafProperties();
        assertNotNull(props.getProperty("Tree.showDefaultIcons"),
            "Tree.showDefaultIcons must be defined for mind map tree panel");
    }

    @Test
    @DisplayName("MenuItem.selectionArc property is defined")
    void menuItemSelectionArcPropertyDefined() throws IOException {
        Properties props = loadFlatLafProperties();
        assertNotNull(props.getProperty("MenuItem.selectionArc"),
            "MenuItem.selectionArc must be defined for menu styling");
    }

    private Properties loadFlatLafProperties() throws IOException {
        Properties props = new Properties();
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("FlatLaf.properties")) {
            assertNotNull(is, "FlatLaf.properties must be loadable from classpath");
            props.load(is);
        }
        return props;
    }
}
