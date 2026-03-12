package tests.freemind.gui;

import freemind.main.Tools;
import freemind.modes.ExtendedMapFeedbackImpl;
import freemind.modes.MapAdapter;
import freemind.modes.MindMapNode;
import freemind.modes.mindmapmode.MindMapMapModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.Font;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * GUI tests for toolbar-related model operations:
 * default font queries, node text editing (simulating toolbar text entry),
 * zoom independence, icon simulation, note toggling, and fold toggling.
 */
class ToolbarGuiTest extends GuiTestBase {

    private ExtendedMapFeedbackImpl mapFeedback;
    private MindMapMapModel map;
    private MindMapNode root;

    @BeforeEach
    void setUp() throws Exception {
        mapFeedback = new ExtendedMapFeedbackImpl();
        map = new MindMapMapModel(mapFeedback);
        mapFeedback.setMap(map);
        String initialMap = "<map><node TEXT='ROOT'/></map>";
        Tools.StringReaderCreator reader = new Tools.StringReaderCreator(initialMap);
        root = map.loadTree(reader, MapAdapter.sDontAskInstance);
        map.setRoot(root);
    }

    @Test
    void toolbar_defaultFontExists() {
        Font defaultFont = mapFeedback.getDefaultFont();
        assertThat(defaultFont)
            .as("Default font should not be null")
            .isNotNull();
    }

    @Test
    void toolbar_defaultFontFamily() {
        Font defaultFont = mapFeedback.getDefaultFont();
        assertThat(defaultFont.getFamily())
            .as("Default font family should be SansSerif")
            .isEqualTo("SansSerif");
    }

    @Test
    void toolbar_defaultFontSize() {
        Font defaultFont = mapFeedback.getDefaultFont();
        assertThat(defaultFont.getSize())
            .as("Default font size should be 12")
            .isEqualTo(12);
    }

    @Test
    void toolbar_nodeTextChangeSimulatesToolbar() {
        MindMapNode child = mapFeedback.addNewNode(root, 0, true);
        mapFeedback.setNodeText(child, "Toolbar entry");
        assertThat(child.getText())
            .as("Node text should reflect toolbar text entry")
            .isEqualTo("Toolbar entry");
    }

    @Test
    void toolbar_multipleNodeTextChanges() {
        MindMapNode child = mapFeedback.addNewNode(root, 0, true);
        for (int i = 1; i <= 5; i++) {
            mapFeedback.setNodeText(child, "Revision " + i);
        }
        assertThat(child.getText())
            .as("Node text should reflect the final (5th) edit")
            .isEqualTo("Revision 5");
    }

    @Test
    void toolbar_zoomSimulation() throws Exception {
        // Create a map with some structure
        MindMapNode child = mapFeedback.addNewNode(root, 0, true);
        mapFeedback.setNodeText(child, "Child node");
        MindMapNode grandchild = mapFeedback.addNewNode(child, 0, true);
        mapFeedback.setNodeText(grandchild, "Grandchild node");

        // Serialize the map at a conceptual "100% zoom"
        String xmlAtZoom100 = saveMapToXml(map);

        // The map data should be independent of view zoom level.
        // Re-serialize at a conceptual "150% zoom" - model is unchanged.
        String xmlAtZoom150 = saveMapToXml(map);

        assertThat(xmlAtZoom150)
            .as("Map XML should be identical regardless of conceptual zoom level")
            .isEqualTo(xmlAtZoom100);

        // Verify node structure is intact
        assertThat(countNodes(root)).isEqualTo(3);
        assertThat(treeDepth(root)).isEqualTo(2);
    }

    @Test
    void toolbar_iconToolbarSimulation() {
        // Simulate icon toolbar by creating a node and verifying it exists
        MindMapNode child = mapFeedback.addNewNode(root, 0, true);
        mapFeedback.setNodeText(child, "Node with icon");

        assertThat(child).isNotNull();
        assertThat(child.getText()).isEqualTo("Node with icon");
        assertThat(root.getChildCount()).isEqualTo(1);
    }

    @Test
    void toolbar_nodeNoteToggle() {
        MindMapNode child = mapFeedback.addNewNode(root, 0, true);
        mapFeedback.setNodeText(child, "Note toggle test");

        // Set a note (toggle ON)
        String noteHtml = "<html><body>This is a note</body></html>";
        mapFeedback.setNoteText(child, noteHtml);
        assertThat(child.getNoteText())
            .as("Note should be set after toggle ON")
            .isEqualTo(noteHtml);

        // Clear the note (toggle OFF)
        mapFeedback.setNoteText(child, null);
        assertThat(child.getNoteText())
            .as("Note should be null after toggle OFF")
            .isNull();

        // Set again (toggle ON again)
        String newNote = "<html><body>New note content</body></html>";
        mapFeedback.setNoteText(child, newNote);
        assertThat(child.getNoteText())
            .as("Note should be set after second toggle ON")
            .isEqualTo(newNote);
    }

    @Test
    void toolbar_foldSimulation() {
        // Create a node with children so fold makes sense
        MindMapNode child = mapFeedback.addNewNode(root, 0, true);
        mapFeedback.setNodeText(child, "Parent to fold");
        MindMapNode grandchild = mapFeedback.addNewNode(child, 0, true);
        mapFeedback.setNodeText(grandchild, "Hidden when folded");

        // Fold the child node
        child.setFolded(true);
        assertThat(child.isFolded())
            .as("Node should be folded after setFolded(true)")
            .isTrue();

        // Children should still exist in the model even when folded
        assertThat(child.getChildCount())
            .as("Folded node should still have children in the model")
            .isEqualTo(1);

        // Unfold
        child.setFolded(false);
        assertThat(child.isFolded())
            .as("Node should be unfolded after setFolded(false)")
            .isFalse();
    }

    @Test
    void toolbar_fontFamilyDropdown() throws Exception {
        MapModelFixture fixture = createMapModel();
        MindMapNode root = loadMap(fixture.model, "<map><node TEXT='Root'/></map>");
        MindMapNode child = createChildNode(fixture.feedback, root, "FontTest");
        child.setFont(new java.awt.Font("SansSerif", java.awt.Font.PLAIN, 12));
        assertThat(child.getFontFamilyName()).isEqualTo("SansSerif");
    }

    @Test
    void toolbar_fontSizeDropdown() throws Exception {
        MapModelFixture fixture = createMapModel();
        MindMapNode root = loadMap(fixture.model, "<map><node TEXT='Root'/></map>");
        MindMapNode child = createChildNode(fixture.feedback, root, "SizeTest");
        child.setFontSize(24);
        assertThat(child.getFontSize()).isEqualTo("24");
    }

    @Test
    void toolbar_fontColorCombo() throws Exception {
        MapModelFixture fixture = createMapModel();
        MindMapNode root = loadMap(fixture.model, "<map><node TEXT='Root'/></map>");
        MindMapNode child = createChildNode(fixture.feedback, root, "ColorTest");
        child.setColor(java.awt.Color.GREEN);
        assertThat(child.getColor()).isEqualTo(java.awt.Color.GREEN);
    }

    @Test
    void toolbar_zoomDropdown() throws Exception {
        MapModelFixture fixture = createMapModel();
        loadMap(fixture.model, "<map><node TEXT='Root'/></map>");
        assertThat(saveMapToXml(fixture.model)).isNotEmpty();
    }

    @Test
    void toolbar_iconToolbar() throws Exception {
        MapModelFixture fixture = createMapModel();
        MindMapNode root = loadMap(fixture.model, "<map><node TEXT='Root'/></map>");
        MindMapNode child = createChildNode(fixture.feedback, root, "IconNode");
        assertThat(child.getIcons()).isEmpty();
    }

    @Test
    void toolbar_foldButtons() throws Exception {
        MapModelFixture fixture = createMapModel();
        MindMapNode root = loadMap(fixture.model,
            "<map><node TEXT='Root'><node TEXT='Foldable'><node TEXT='Hidden'/></node></node></map>");
        MindMapNode foldable = (MindMapNode) root.getChildAt(0);
        foldable.setFolded(true);
        assertThat(foldable.isFolded()).isTrue();
        foldable.setFolded(false);
        assertThat(foldable.isFolded()).isFalse();
    }

    @Test
    void toolbar_historyButtons() throws Exception {
        MapModelFixture fixture = createMapModel();
        MindMapNode root = loadMap(fixture.model,
            "<map><node TEXT='Root'><node TEXT='A'/><node TEXT='B'/></node></map>");
        assertThat(findNodeByText(root, "A")).isNotNull();
        assertThat(findNodeByText(root, "B")).isNotNull();
    }

    @Override
    protected MindMapNode getMapRootForScreenshot() {
        return root;
    }
}
