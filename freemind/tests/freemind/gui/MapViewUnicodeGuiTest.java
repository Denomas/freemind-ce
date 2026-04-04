package tests.freemind.gui;

import freemind.main.Tools;
import freemind.modes.ExtendedMapFeedbackImpl;
import freemind.modes.MapAdapter;
import freemind.modes.MindMapNode;
import freemind.modes.mindmapmode.MindMapMapModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.StringWriter;

import static org.assertj.core.api.Assertions.assertThat;
import static tests.freemind.unicode.UnicodeTestData.*;

/**
 * GUI test for map view with Unicode nodes.
 * Creates maps with Unicode node text, saves/loads them,
 * and verifies text preservation through the cycle.
 *
 * Extends GuiTestBase for shared infrastructure.
 */
class MapViewUnicodeGuiTest extends GuiTestBase {

    private ExtendedMapFeedbackImpl mapFeedback;
    private MindMapMapModel map;

    @BeforeEach
    void setUp() {
        mapFeedback = new ExtendedMapFeedbackImpl();
        map = new MindMapMapModel(mapFeedback);
        mapFeedback.setMap(map);
    }

    @Test
    void mapSaveLoad_preservesUnicodeNodeText() throws Exception {
        // Build a map with all script nodes
        StringBuilder mapXml = new StringBuilder("<map><node TEXT='ROOT'>");
        for (int i = 0; i < ALL_SCRIPTS.size(); i++) {
            mapXml.append("<node TEXT='").append(xmlEscape(ALL_SCRIPTS.get(i))).append("'/>");
        }
        mapXml.append("</node></map>");

        // Load
        Tools.StringReaderCreator reader = new Tools.StringReaderCreator(mapXml.toString());
        MindMapNode root = map.loadTree(reader, MapAdapter.sDontAskInstance);
        map.setRoot(root);

        // Save
        String savedXml = getMapContents();

        // Verify all scripts present as UTF-8
        for (int i = 0; i < ALL_SCRIPTS.size(); i++) {
            assertThat(savedXml)
                .as("Saved map should contain %s text", ALL_SCRIPT_NAMES.get(i))
                .contains(ALL_SCRIPTS.get(i));
        }

        // Reload
        ExtendedMapFeedbackImpl mapFeedback2 = new ExtendedMapFeedbackImpl();
        MindMapMapModel map2 = new MindMapMapModel(mapFeedback2);
        mapFeedback2.setMap(map2);
        reader = new Tools.StringReaderCreator(savedXml);
        MindMapNode reloadedRoot = map2.loadTree(reader, MapAdapter.sDontAskInstance);
        map2.setRoot(reloadedRoot);

        // Verify all children preserved
        for (int i = 0; i < ALL_SCRIPTS.size(); i++) {
            MindMapNode child = (MindMapNode) reloadedRoot.getChildAt(i);
            assertThat(child.getText())
                .as("Reloaded %s node text", ALL_SCRIPT_NAMES.get(i))
                .isEqualTo(ALL_SCRIPTS.get(i));
        }
    }

    @Test
    void mapSaveLoad_preservesUnicodeNotes() throws Exception {
        // Create map with root
        String initialMap = "<map><node TEXT='ROOT'/></map>";
        Tools.StringReaderCreator reader = new Tools.StringReaderCreator(initialMap);
        MindMapNode root = map.loadTree(reader, MapAdapter.sDontAskInstance);
        map.setRoot(root);

        // Add children with Unicode notes
        for (int i = 0; i < ALL_SCRIPTS.size(); i++) {
            MindMapNode child = mapFeedback.addNewNode(root, i, true);
            mapFeedback.setNodeText(child, ALL_SCRIPT_NAMES.get(i));
            mapFeedback.setNoteText(child, wrapInHtml(ALL_SCRIPTS.get(i)));
        }

        // Save
        String savedXml = getMapContents();

        // Verify notes contain UTF-8
        for (int i = 0; i < ALL_SCRIPTS.size(); i++) {
            assertThat(savedXml)
                .as("Saved XML should contain %s note text", ALL_SCRIPT_NAMES.get(i))
                .contains(ALL_SCRIPTS.get(i));
        }
    }

    // ========================================================================
    // Helpers
    // ========================================================================

    private String getMapContents() throws Exception {
        StringWriter writer = new StringWriter();
        map.getFilteredXml(writer);
        return writer.toString();
    }

    @Override
    protected MindMapNode getMapRootForScreenshot() {
        return map != null ? map.getRootNode() : null;
    }
}
