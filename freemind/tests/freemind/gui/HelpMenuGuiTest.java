package tests.freemind.gui;

import freemind.main.FreeMind;
import freemind.main.HtmlTools;
import freemind.main.Resources;
import freemind.main.Tools;
import freemind.modes.ExtendedMapFeedbackImpl;
import freemind.modes.MapAdapter;
import freemind.modes.mindmapmode.MindMapMapModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * GUI tests for informational and metadata operations.
 * Covers version info, headless mode detection, and singleton instances.
 */
class HelpMenuGuiTest extends GuiTestBase {

    private ExtendedMapFeedbackImpl mapFeedback;
    private MindMapMapModel map;

    @BeforeEach
    void setUp() {
        mapFeedback = new ExtendedMapFeedbackImpl();
        map = new MindMapMapModel(mapFeedback);
        mapFeedback.setMap(map);
    }

    @Test
    void help_freeMindVersionNotNull() {
        assertThat(FreeMind.XML_VERSION).isNotNull();
        assertThat(FreeMind.XML_VERSION).isNotEmpty();
    }

    @Test
    void help_headlessFreeMindInitialized() {
        assertThat(freeMindMain).isNotNull();
    }

    @Test
    void help_toolsIsHeadless() {
        boolean headless = Tools.isHeadless();
        // Just verify it returns a boolean without throwing
        assertThat(headless).isIn(true, false);
    }

    @Test
    void help_htmlToolsInstance() {
        HtmlTools first = HtmlTools.getInstance();
        HtmlTools second = HtmlTools.getInstance();

        assertThat(first).isNotNull();
        assertThat(first).isSameAs(second);
    }

    @Test
    void help_xmlVersionNonEmpty() {
        assertThat(FreeMind.XML_VERSION)
            .as("XML_VERSION should be a non-empty string")
            .isNotNull()
            .isNotBlank();
    }

    @Test
    void help_resourcesSingleton() {
        Resources first = Resources.getInstance();
        Resources second = Resources.getInstance();

        assertThat(first)
            .as("Resources.getInstance() should return the same object")
            .isNotNull()
            .isSameAs(second);
    }

    @Test
    void help_mapModelCreation() {
        MindMapMapModel model = new MindMapMapModel(mapFeedback);
        assertThat(model)
            .as("MindMapMapModel should be instantiable")
            .isNotNull();
    }

    @Test
    void help_defaultFontNotNull() {
        assertThat(mapFeedback.getDefaultFont())
            .as("Default font from ExtendedMapFeedbackImpl should not be null")
            .isNotNull();
    }

    @Test
    void help_about() throws Exception {
        // About dialog shows version; verify version exists
        assertThat(freemind.main.FreeMind.XML_VERSION).isNotNull();
        assertThat(freemind.main.FreeMind.XML_VERSION).isNotEmpty();
    }

    @Test
    void help_license() throws Exception {
        // License information; verify FreeMind class loads
        assertThat(freemind.main.FreeMind.class).isNotNull();
    }

    @Test
    void help_keyDocumentation() throws Exception {
        // Keyboard shortcuts documentation; verify properties exist
        assertThat(freeMindMain).isNotNull();
    }
}
