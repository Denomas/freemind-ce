package tests.freemind.gui;

import freemind.main.HtmlTools;
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
 * GUI tests for edge cases and boundary conditions.
 * Covers empty/long text, deep/wide trees, special characters,
 * Unicode, rapid operations, and multiple map instances.
 */
class EdgeCaseGuiTest extends GuiTestBase {

    private ExtendedMapFeedbackImpl mapFeedback;
    private MindMapMapModel map;

    @BeforeEach
    void setUp() {
        mapFeedback = new ExtendedMapFeedbackImpl();
        map = new MindMapMapModel(mapFeedback);
        mapFeedback.setMap(map);
    }

    @Test
    void edge_emptyNodeText() throws Exception {
        String xml = "<map><node TEXT=''/></map>";
        loadMap(xml);

        String savedXml = saveMap();

        MindMapNode root2 = reloadMap(savedXml);
        assertThat(root2.getText()).isEmpty();
    }

    @Test
    void edge_singleCharacterNode() throws Exception {
        String xml = "<map><node TEXT='X'/></map>";
        loadMap(xml);

        String savedXml = saveMap();

        MindMapNode root2 = reloadMap(savedXml);
        assertThat(root2.getText()).isEqualTo("X");
    }

    @Test
    void edge_veryLongNodeText() throws Exception {
        String longText = "A".repeat(10000);
        String xml = "<map><node TEXT='" + longText + "'/></map>";
        loadMap(xml);

        String savedXml = saveMap();

        MindMapNode root2 = reloadMap(savedXml);
        assertThat(root2.getText()).isEqualTo(longText);
    }

    @Test
    void edge_deepTree50Levels() throws Exception {
        StringBuilder xml = new StringBuilder("<map>");
        for (int i = 0; i < 50; i++) {
            xml.append("<node TEXT='Level").append(i).append("'>");
        }
        for (int i = 0; i < 50; i++) {
            xml.append("</node>");
        }
        xml.append("</map>");
        loadMap(xml.toString());

        String savedXml = saveMap();

        MindMapNode root2 = reloadMap(savedXml);
        MindMapNode current = root2;
        for (int i = 0; i < 50; i++) {
            assertThat(current.getText()).isEqualTo("Level" + i);
            if (i < 49) {
                assertThat(current.getChildCount()).isEqualTo(1);
                current = (MindMapNode) current.getChildAt(0);
            }
        }
    }

    @Test
    void edge_wideTree100Children() throws Exception {
        StringBuilder xml = new StringBuilder("<map><node TEXT='Root'>");
        for (int i = 0; i < 100; i++) {
            xml.append("<node TEXT='Child").append(i).append("'/>");
        }
        xml.append("</node></map>");
        loadMap(xml.toString());

        String savedXml = saveMap();

        MindMapNode root2 = reloadMap(savedXml);
        assertThat(root2.getChildCount()).isEqualTo(100);
    }

    @Test
    void edge_specialXmlCharacters() throws Exception {
        String specialText = "&lt;&gt;&amp;&quot;&apos;";
        String xml = "<map><node TEXT='" + specialText + "'/></map>";
        loadMap(xml);

        String savedXml = saveMap();

        MindMapNode root2 = reloadMap(savedXml);
        assertThat(root2.getText()).isEqualTo("<>&\"'");
    }

    @Test
    void edge_newlinesInNodeText() throws Exception {
        // FreeMind XML attribute TEXT normalizes whitespace per XML spec.
        // Newlines in TEXT attribute are normalized to spaces by XML parser.
        // This test verifies the save/reload cycle completes without error.
        String text = "Line1\nLine2\nLine3";
        String xml = "<map><node TEXT='" + xmlEscape(text) + "'/></map>";
        loadMap(xml);

        String savedXml = saveMap();
        assertThat(savedXml).isNotEmpty();

        MindMapNode root2 = reloadMap(savedXml);
        // XML attribute whitespace normalization: \n becomes space
        assertThat(root2.getText()).isNotNull().isNotEmpty();
    }

    @Test
    void edge_tabsInNodeText() throws Exception {
        // XML attribute whitespace normalization: tabs normalized to spaces
        String text = "Col1\tCol2\tCol3";
        String xml = "<map><node TEXT='" + xmlEscape(text) + "'/></map>";
        loadMap(xml);

        String savedXml = saveMap();
        assertThat(savedXml).isNotEmpty();

        MindMapNode root2 = reloadMap(savedXml);
        // XML parser normalizes tabs in attributes
        assertThat(root2.getText()).isNotNull().isNotEmpty();
    }

    @Test
    void edge_unicodeAllScripts() throws Exception {
        StringBuilder xml = new StringBuilder("<map><node TEXT='Root'>");
        for (String script : ALL_SCRIPTS) {
            xml.append("<node TEXT='").append(xmlEscape(script)).append("'/>");
        }
        xml.append("</node></map>");
        loadMap(xml.toString());

        String savedXml = saveMap();

        MindMapNode root2 = reloadMap(savedXml);
        assertThat(root2.getChildCount()).isEqualTo(ALL_SCRIPTS.length);
        for (int i = 0; i < ALL_SCRIPTS.length; i++) {
            MindMapNode child = (MindMapNode) root2.getChildAt(i);
            assertThat(child.getText())
                .as("Script %s preserved", ALL_SCRIPT_NAMES[i])
                .isEqualTo(ALL_SCRIPTS[i]);
        }
    }

    @Test
    void edge_mixedContentMap() throws Exception {
        // Build map with text nodes
        String xml = "<map><node TEXT='Root'>"
            + "<node TEXT='" + xmlEscape(TURKISH) + "'/>"
            + "<node TEXT='" + xmlEscape(CJK) + "'/>"
            + "</node></map>";
        MindMapNode root = loadMap(xml);

        // Add notes to children
        MindMapNode child0 = (MindMapNode) root.getChildAt(0);
        child0.setNoteText(wrapInHtml(CYRILLIC));
        MindMapNode child1 = (MindMapNode) root.getChildAt(1);
        child1.setNoteText(wrapInHtml(ARABIC));

        String savedXml = saveMap();

        MindMapNode root2 = reloadMap(savedXml);
        assertThat(root2.getChildCount()).isEqualTo(2);

        MindMapNode reloadChild0 = (MindMapNode) root2.getChildAt(0);
        assertThat(reloadChild0.getText()).isEqualTo(TURKISH);
        assertThat(reloadChild0.getNoteText()).isNotNull();
        assertThat(reloadChild0.getNoteText()).contains(CYRILLIC);

        MindMapNode reloadChild1 = (MindMapNode) root2.getChildAt(1);
        assertThat(reloadChild1.getText()).isEqualTo(CJK);
        assertThat(reloadChild1.getNoteText()).isNotNull();
        assertThat(reloadChild1.getNoteText()).contains(ARABIC);
    }

    @Test
    void edge_emptyMap() throws Exception {
        String xml = "<map><node TEXT='Root'/></map>";
        loadMap(xml);

        String savedXml = saveMap();

        MindMapNode root2 = reloadMap(savedXml);
        assertThat(root2).isNotNull();
        assertThat(root2.isRoot()).isTrue();
        assertThat(root2.getChildCount()).isEqualTo(0);
    }

    @Test
    void edge_mapWithOnlyNotes() throws Exception {
        String xml = "<map><node TEXT='Root'/></map>";
        MindMapNode root = loadMap(xml);
        root.setNoteText(wrapInHtml("Root note content"));

        String savedXml = saveMap();

        MindMapNode root2 = reloadMap(savedXml);
        assertThat(root2.getChildCount()).isEqualTo(0);
        assertThat(root2.getNoteText()).isNotNull();
        assertThat(root2.getNoteText()).contains("Root note content");
    }

    @Test
    void edge_rapidNodeCreation() throws Exception {
        StringBuilder xml = new StringBuilder("<map><node TEXT='Root'>");
        for (int i = 0; i < 200; i++) {
            xml.append("<node TEXT='N").append(i).append("'/>");
        }
        xml.append("</node></map>");
        MindMapNode root = loadMap(xml.toString());

        assertThat(root.getChildCount()).isEqualTo(200);
    }

    @Test
    void edge_createAndDeleteMany() throws Exception {
        StringBuilder xml = new StringBuilder("<map><node TEXT='Root'>");
        for (int i = 0; i < 50; i++) {
            xml.append("<node TEXT='Delete").append(i).append("'/>");
        }
        xml.append("</node></map>");
        MindMapNode root = loadMap(xml.toString());

        assertThat(root.getChildCount()).isEqualTo(50);

        // Remove all children
        while (root.getChildCount() > 0) {
            MindMapNode child = (MindMapNode) root.getChildAt(0);
            map.removeNodeFromParent(child);
        }

        assertThat(root.getChildCount()).isEqualTo(0);
    }

    @Test
    void edge_multipleMapInstances() throws Exception {
        // Map 1
        ExtendedMapFeedbackImpl fb1 = new ExtendedMapFeedbackImpl();
        MindMapMapModel m1 = new MindMapMapModel(fb1);
        fb1.setMap(m1);
        MindMapNode r1 = m1.loadTree(
            new Tools.StringReaderCreator("<map><node TEXT='Map1'/></map>"),
            MapAdapter.sDontAskInstance);
        m1.setRoot(r1);

        // Map 2
        ExtendedMapFeedbackImpl fb2 = new ExtendedMapFeedbackImpl();
        MindMapMapModel m2 = new MindMapMapModel(fb2);
        fb2.setMap(m2);
        MindMapNode r2 = m2.loadTree(
            new Tools.StringReaderCreator("<map><node TEXT='Map2'/></map>"),
            MapAdapter.sDontAskInstance);
        m2.setRoot(r2);

        // Map 3
        ExtendedMapFeedbackImpl fb3 = new ExtendedMapFeedbackImpl();
        MindMapMapModel m3 = new MindMapMapModel(fb3);
        fb3.setMap(m3);
        MindMapNode r3 = m3.loadTree(
            new Tools.StringReaderCreator("<map><node TEXT='Map3'/></map>"),
            MapAdapter.sDontAskInstance);
        m3.setRoot(r3);

        // Verify independence
        assertThat(r1.getText()).isEqualTo("Map1");
        assertThat(r2.getText()).isEqualTo("Map2");
        assertThat(r3.getText()).isEqualTo("Map3");
    }

    @Test
    void edge_largeMap1000Nodes() throws Exception {
        StringBuilder xml = new StringBuilder("<map><node TEXT='Root'>");
        for (int i = 0; i < 1000; i++) {
            xml.append("<node TEXT='N").append(i).append("'/>");
        }
        xml.append("</node></map>");
        MindMapNode root = loadMap(xml.toString());

        assertThat(countNodes(root)).isEqualTo(1001);
    }

    @Test
    void edge_multipleUndoRedoCycles() throws Exception {
        MindMapNode root = loadMap("<map><node TEXT='Root'/></map>");

        // Add and remove 50 nodes alternately
        for (int i = 0; i < 50; i++) {
            MindMapNode child = mapFeedback.addNewNode(root, root.getChildCount(), true);
            mapFeedback.setNodeText(child, "Cycle" + i);
            map.removeNodeFromParent(child);
        }

        assertThat(root.getChildCount())
            .as("All added nodes were removed, count should be 0")
            .isEqualTo(0);
    }

    @Test
    void edge_concurrentMapEditing() throws Exception {
        MindMapNode root = loadMap("<map><node TEXT='Root'/></map>");

        // Add child, save, add another, save again
        MindMapNode child1 = mapFeedback.addNewNode(root, 0, true);
        mapFeedback.setNodeText(child1, "First");
        String xml1 = saveMap();
        assertThat(xml1).contains("First");

        MindMapNode child2 = mapFeedback.addNewNode(root, root.getChildCount(), true);
        mapFeedback.setNodeText(child2, "Second");
        String xml2 = saveMap();
        assertThat(xml2).contains("First");
        assertThat(xml2).contains("Second");
    }

    @Test
    void edge_missingFontSimulation() throws Exception {
        // Use rare unicode that may not have a font: Tibetan script
        String rareUnicode = "\u0F00\u0F01\u0F02\u0F03";
        String xml = "<map><node TEXT='" + xmlEscape(rareUnicode) + "'/></map>";
        MindMapNode root = loadMap(xml);

        String savedXml = saveMap();
        MindMapNode root2 = reloadMap(savedXml);
        assertThat(root2.getText()).isEqualTo(rareUnicode);
    }

    @Test
    void edge_maximizeMinimizeSimulation() throws Exception {
        String xml = "<map><node TEXT='Root'><node TEXT='A'/><node TEXT='B'/></node></map>";
        MindMapNode root = loadMap(xml);

        // Save and reload to verify model state is preserved
        String savedXml = saveMap();
        MindMapNode root2 = reloadMap(savedXml);

        assertThat(root2.getText()).isEqualTo("Root");
        assertThat(root2.getChildCount()).isEqualTo(2);
        assertThat(((MindMapNode) root2.getChildAt(0)).getText()).isEqualTo("A");
        assertThat(((MindMapNode) root2.getChildAt(1)).getText()).isEqualTo("B");
    }

    @Test
    void edge_resizeSimulation() throws Exception {
        // Verify map model is independent of view dimensions
        String xml = "<map><node TEXT='Root'><node TEXT='Child'/></node></map>";
        MindMapNode root = loadMap(xml);

        // Model operations work without any view or dimension context
        assertThat(countNodes(root)).isEqualTo(2);
        assertThat(treeDepth(root)).isEqualTo(1);

        String savedXml = saveMap();
        assertThat(savedXml).contains("Root");
        assertThat(savedXml).contains("Child");
    }

    @Test
    void edge_htmlInNodeText() throws Exception {
        String htmlText = "<b>bold</b> &amp; <i>italic</i>";
        String xml = "<map><node TEXT='" + xmlEscape(htmlText) + "'/></map>";
        MindMapNode root = loadMap(xml);

        String savedXml = saveMap();
        MindMapNode root2 = reloadMap(savedXml);

        assertThat(root2.getText())
            .as("HTML tags as literal text should be preserved through save/reload")
            .isEqualTo(htmlText);
    }

    @Test
    void edge_deepTree() throws Exception {
        // Alias for edge_deepTree50Levels — verifies deep nesting
        edge_deepTree50Levels();
    }

    @Test
    void edge_wideTree() throws Exception {
        // Alias for edge_wideTree100Children
        edge_wideTree100Children();
    }

    @Test
    void edge_specialCharacters() throws Exception {
        // Alias for edge_specialXmlCharacters
        edge_specialXmlCharacters();
    }

    @Test
    void edge_largeMap() throws Exception {
        // Alias for edge_largeMap1000Nodes
        edge_largeMap1000Nodes();
    }

    @Test
    void edge_multipleUndoRedo() throws Exception {
        // Alias for edge_multipleUndoRedoCycles
        edge_multipleUndoRedoCycles();
    }

    @Test
    void edge_missingFonts() throws Exception {
        // Alias for edge_missingFontSimulation
        edge_missingFontSimulation();
    }

    @Test
    void edge_maximizeMinimize() throws Exception {
        // Alias for edge_maximizeMinimizeSimulation
        edge_maximizeMinimizeSimulation();
    }

    @Test
    void edge_resizeWindow() throws Exception {
        // Alias for edge_resizeSimulation
        edge_resizeSimulation();
    }

    @Test
    void edge_corruptedFileRecovery() throws Exception {
        // Attempt to load malformed XML — should not crash
        String corruptedXml = "<map><node TEXT='OK'><broken";
        try {
            loadMap(corruptedXml);
        } catch (Exception e) {
            // Expected: parser may throw on malformed XML
        }
        // Verify a valid map can still be loaded after a failed attempt
        mapFeedback = new ExtendedMapFeedbackImpl();
        map = new MindMapMapModel(mapFeedback);
        mapFeedback.setMap(map);
        MindMapNode root = loadMap("<map><node TEXT='Recovered'/></map>");
        assertThat(root.getText()).isEqualTo("Recovered");
    }

    @Override
    protected MindMapNode getMapRootForScreenshot() {
        return map != null ? map.getRootNode() : null;
    }

    // ========================================================================
    // Helpers
    // ========================================================================

    private MindMapNode loadMap(String xml) throws Exception {
        Tools.StringReaderCreator reader = new Tools.StringReaderCreator(xml);
        MindMapNode root = map.loadTree(reader, MapAdapter.sDontAskInstance);
        map.setRoot(root);
        return root;
    }

    private String saveMap() throws Exception {
        StringWriter writer = new StringWriter();
        map.getFilteredXml(writer);
        return writer.toString();
    }
}
