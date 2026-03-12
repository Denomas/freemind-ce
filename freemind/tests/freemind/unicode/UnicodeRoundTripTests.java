package tests.freemind.unicode;

import freemind.main.FreeMind;
import freemind.main.HeadlessFreeMind;
import freemind.main.Tools;
import freemind.modes.ExtendedMapFeedbackImpl;
import freemind.modes.MapAdapter;
import freemind.modes.MindMapNode;
import freemind.modes.mindmapmode.MindMapMapModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringWriter;

import static org.assertj.core.api.Assertions.assertThat;
import static tests.freemind.unicode.UnicodeTestData.*;

/**
 * Integration tests for Unicode round-trip through map save/load cycle.
 * Uses ExtendedMapFeedbackImpl for headless map creation and manipulation.
 */
class UnicodeRoundTripTests {

    private ExtendedMapFeedbackImpl mapFeedback;
    private MindMapMapModel map;

    @BeforeEach
    void setUp() {
        new HeadlessFreeMind();
        mapFeedback = new ExtendedMapFeedbackImpl();
        map = new MindMapMapModel(mapFeedback);
        mapFeedback.setMap(map);
    }

    // ========================================================================
    // Node text round-trip
    // ========================================================================

    @Test
    void nodeText_unicodePreservedThroughSaveLoad() throws Exception {
        for (int i = 0; i < ALL_SCRIPTS.length; i++) {
            String script = ALL_SCRIPTS[i];
            String mapXml = "<map><node TEXT='" + xmlEscape(script) + "'/></map>";
            Tools.StringReaderCreator reader = new Tools.StringReaderCreator(mapXml);
            MindMapNode root = map.loadTree(reader, MapAdapter.sDontAskInstance);
            map.setRoot(root);

            String savedXml = getMapContents(map);
            assertThat(savedXml)
                .as("Saved XML should contain UTF-8 for %s", ALL_SCRIPT_NAMES[i])
                .contains(script);
        }
    }

    // ========================================================================
    // Note text round-trip (the primary bug scenario)
    // ========================================================================

    @Test
    void noteText_turkishPreservedThroughSetAndGet() throws Exception {
        String htmlNote = wrapInHtml(TURKISH_SENTENCE);
        MindMapNode root = createMapWithRoot();
        MindMapNode child = mapFeedback.addNewNode(root, 0, true);

        mapFeedback.setNoteText(child, htmlNote);
        String retrieved = child.getNoteText();

        assertThat(retrieved)
            .as("getNoteText should return UTF-8 Turkish, not entities")
            .contains("ğüşöçıİĞÜŞÖÇ");
        assertThat(retrieved).doesNotContain("&#287;");
        assertThat(retrieved).doesNotContain("&#x11f;");
    }

    @Test
    void noteText_allScripts_noEntitiesInSavedXml() throws Exception {
        MindMapNode root = createMapWithRoot();

        for (int i = 0; i < ALL_SCRIPTS.length; i++) {
            String htmlNote = wrapInHtml(ALL_SCRIPTS[i]);
            MindMapNode child = mapFeedback.addNewNode(root, i, true);
            mapFeedback.setNoteText(child, htmlNote);
        }

        String savedXml = getMapContents(map);

        for (int i = 0; i < ALL_SCRIPTS.length; i++) {
            assertThat(savedXml)
                .as("Saved XML should contain UTF-8 %s text directly", ALL_SCRIPT_NAMES[i])
                .contains(ALL_SCRIPTS[i]);
        }
    }

    @Test
    void noteText_roundTrip_saveAndReload() throws Exception {
        MindMapNode root = createMapWithRoot();
        MindMapNode child = mapFeedback.addNewNode(root, 0, true);

        String htmlNote = wrapInHtml(TURKISH_SENTENCE);
        mapFeedback.setNoteText(child, htmlNote);

        // Save
        String savedXml = getMapContents(map);
        assertThat(savedXml).contains("ğüşöçıİĞÜŞÖÇ");

        // Reload
        ExtendedMapFeedbackImpl mapFeedback2 = new ExtendedMapFeedbackImpl();
        MindMapMapModel map2 = new MindMapMapModel(mapFeedback2);
        mapFeedback2.setMap(map2);
        Tools.StringReaderCreator reader = new Tools.StringReaderCreator(savedXml);
        MindMapNode reloadedRoot = map2.loadTree(reader, MapAdapter.sDontAskInstance);
        map2.setRoot(reloadedRoot);

        // Verify note text preserved
        MindMapNode reloadedChild = (MindMapNode) reloadedRoot.getChildAt(0);
        String reloadedNote = reloadedChild.getNoteText();
        assertThat(reloadedNote)
            .as("Reloaded note should contain Turkish characters")
            .contains("ğüşöçıİĞÜŞÖÇ");
    }

    // ========================================================================
    // Legacy entity-encoded files should still load correctly
    // ========================================================================

    @Test
    void legacyEntityEncodedFile_loadsCorrectly() throws Exception {
        // Simulate a file saved with the old entity-encoding behavior
        String legacyXml = "<map version=\"" + FreeMind.XML_VERSION + "\">\n"
            + "<node TEXT=\"ROOT\">\n"
            + "<node TEXT=\"&#287;&#252;&#351;&#246;&#231;&#305;\"/>\n"
            + "</node>\n</map>\n";

        Tools.StringReaderCreator reader = new Tools.StringReaderCreator(legacyXml);
        MindMapNode root = map.loadTree(reader, MapAdapter.sDontAskInstance);
        map.setRoot(root);

        MindMapNode child = (MindMapNode) root.getChildAt(0);
        assertThat(child.getText()).isEqualTo("ğüşöçı");
    }

    // ========================================================================
    // Helpers
    // ========================================================================

    private MindMapNode createMapWithRoot() throws Exception {
        String initialMap = "<map><node TEXT='ROOT'/></map>";
        Tools.StringReaderCreator reader = new Tools.StringReaderCreator(initialMap);
        MindMapNode root = map.loadTree(reader, MapAdapter.sDontAskInstance);
        map.setRoot(root);
        return root;
    }

    private String getMapContents(MindMapMapModel map) throws IOException {
        StringWriter writer = new StringWriter();
        map.getFilteredXml(writer);
        return writer.toString();
    }

    private static String xmlEscape(String s) {
        return s.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("'", "&apos;")
                .replace("\"", "&quot;");
    }
}
