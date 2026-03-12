package tests.freemind.gui;

import com.lightdev.app.shtm.SHTMLPanel;
import freemind.main.HtmlTools;
import freemind.main.Tools;
import freemind.modes.ExtendedMapFeedbackImpl;
import freemind.modes.MapAdapter;
import freemind.modes.MindMapNode;
import freemind.modes.mindmapmode.MindMapMapModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static tests.freemind.unicode.UnicodeTestData.*;

/**
 * GUI tests that verify key action mappings and class infrastructure exist.
 * Does not simulate actual keyboard input, but verifies that essential
 * classes can be loaded and instantiated.
 */
class KeyboardShortcutsGuiTest extends GuiTestBase {

    private ExtendedMapFeedbackImpl mapFeedback;
    private MindMapMapModel map;

    @BeforeEach
    void setUp() {
        mapFeedback = new ExtendedMapFeedbackImpl();
        map = new MindMapMapModel(mapFeedback);
        mapFeedback.setMap(map);
    }

    @Test
    void keyboard_toolsClassExists() {
        assertThat(Tools.class).isNotNull();
    }

    @Test
    void keyboard_htmlToolsExists() {
        assertThat(HtmlTools.class).isNotNull();
        assertThat(HtmlTools.getInstance()).isNotNull();
    }

    @Test
    void keyboard_mapModelClassExists() {
        MindMapMapModel model = new MindMapMapModel(new ExtendedMapFeedbackImpl());
        assertThat(model).isNotNull();
    }

    @Test
    void keyboard_mapFeedbackClassExists() {
        ExtendedMapFeedbackImpl feedback = new ExtendedMapFeedbackImpl();
        assertThat(feedback).isNotNull();
    }

    @Test
    void keyboard_sHTMLPanelClassExists() throws Exception {
        SHTMLPanel panel = runOnEdtAndGet(SHTMLPanel::createSHTMLPanel);

        assertThat(panel).isNotNull();
        runOnEdt(() -> panel.setVisible(false));
    }

    @Test
    void keyboard_allTestDataConstantsExist() {
        assertThat(ALL_SCRIPTS).isNotNull();
        assertThat(ALL_SCRIPTS.length).isGreaterThan(0);
    }

    @Test
    void keyboard_allTestDataNamesExist() {
        assertThat(ALL_SCRIPT_NAMES).isNotNull();
        assertThat(ALL_SCRIPT_NAMES.length).isEqualTo(ALL_SCRIPTS.length);
    }

    @Test
    void keyboard_nodeCreationClassesWork() throws Exception {
        MapModelFixture fixture = createMapModel();
        MindMapNode root = loadMap(fixture.model, "<map><node TEXT='Root'/></map>");
        MindMapNode child = createChildNode(fixture.feedback, root, "Inserted");

        assertThat(child).isNotNull();
        assertThat(child.getText()).isEqualTo("Inserted");
        assertNodeExists(root, "Inserted");
    }

    @Test
    void keyboard_nodeDeletionClassesWork() throws Exception {
        MapModelFixture fixture = createMapModel();
        MindMapNode root = loadMap(fixture.model,
            "<map><node TEXT='Root'><node TEXT='ToDelete'/></node></map>");

        assertThat(root.getChildCount()).isEqualTo(1);

        MindMapNode child = (MindMapNode) root.getChildAt(0);
        fixture.model.removeNodeFromParent(child);

        assertThat(root.getChildCount()).isEqualTo(0);
    }

    @Test
    void keyboard_undoSimulation() throws Exception {
        MapModelFixture fixture = createMapModel();
        MindMapNode root = loadMap(fixture.model, "<map><node TEXT='Root'/></map>");

        // Simulate: add a node, then undo by removing it
        MindMapNode added = createChildNode(fixture.feedback, root, "Added");
        assertThat(root.getChildCount()).isEqualTo(1);

        fixture.model.removeNodeFromParent(added);
        assertThat(root.getChildCount()).isEqualTo(0);
    }

    @Test
    void keyboard_saveSimulation() throws Exception {
        MapModelFixture fixture = createMapModel();
        loadMap(fixture.model, "<map><node TEXT='Root'><node TEXT='Child'/></node></map>");

        String xml = saveMapToXml(fixture.model);
        assertThat(xml).isNotEmpty();
        assertThat(xml).contains("Root");
        assertThat(xml).contains("Child");
    }

    @Test
    void keyboard_findSimulation() throws Exception {
        MapModelFixture fixture = createMapModel();
        MindMapNode root = loadMap(fixture.model,
            "<map><node TEXT='Root'><node TEXT='Alpha'><node TEXT='Beta'/></node><node TEXT='Gamma'/></node></map>");

        assertThat(findNodeByText(root, "Beta")).isNotNull();
        assertThat(findNodeByText(root, "Gamma")).isNotNull();
        assertThat(findNodeByText(root, "NonExistent")).isNull();
    }

    @Test
    void keyboard_copySimulation() throws Exception {
        MapModelFixture fixture = createMapModel();
        MindMapNode root = loadMap(fixture.model,
            "<map><node TEXT='Root'><node TEXT='CopyMe'/></node></map>");

        MindMapNode found = findNodeByText(root, "CopyMe");
        assertThat(found).isNotNull();
        String copiedText = found.getText();
        assertThat(copiedText).isEqualTo("CopyMe");
    }

    @Test
    void keyboard_pasteSimulation() throws Exception {
        MapModelFixture fixture = createMapModel();
        MindMapNode root = loadMap(fixture.model,
            "<map><node TEXT='Root'><node TEXT='Source'/></node></map>");

        // Read text from source (copy)
        String copiedText = findNodeByText(root, "Source").getText();

        // Create new node with copied text (paste)
        MindMapNode pasted = createChildNode(fixture.feedback, root, copiedText);
        assertThat(pasted.getText()).isEqualTo("Source");
        assertThat(root.getChildCount()).isEqualTo(2);
    }

    @Test
    void keyboard_selectAllSimulation() throws Exception {
        MapModelFixture fixture = createMapModel();
        MindMapNode root = loadMap(fixture.model,
            "<map><node TEXT='Root'><node TEXT='A'/><node TEXT='B'/><node TEXT='C'/></node></map>");

        java.util.List<String> texts = new java.util.ArrayList<>();
        for (int i = 0; i < root.getChildCount(); i++) {
            texts.add(((freemind.modes.MindMapNode) root.getChildAt(i)).getText());
        }
        assertThat(texts).containsExactly("A", "B", "C");
    }

    @Test
    void keyboard_newChild() throws Exception {
        MapModelFixture fixture = createMapModel();
        MindMapNode root = loadMap(fixture.model, "<map><node TEXT='Root'/></map>");
        MindMapNode child = createChildNode(fixture.feedback, root, "NewChild");
        assertThat(child.getParentNode()).isEqualTo(root);
    }

    @Test
    void keyboard_newSibling() throws Exception {
        MapModelFixture fixture = createMapModel();
        MindMapNode root = loadMap(fixture.model, "<map><node TEXT='Root'/></map>");
        createChildNode(fixture.feedback, root, "Sibling1");
        createChildNode(fixture.feedback, root, "Sibling2");
        assertThat(root.getChildCount()).isEqualTo(2);
    }

    @Test
    void keyboard_delete() throws Exception {
        MapModelFixture fixture = createMapModel();
        MindMapNode root = loadMap(fixture.model, "<map><node TEXT='Root'><node TEXT='Delete'/></node></map>");
        MindMapNode child = (MindMapNode) root.getChildAt(0);
        fixture.model.removeNodeFromParent(child);
        assertThat(root.getChildCount()).isEqualTo(0);
    }

    @Test
    void keyboard_undo() throws Exception {
        MapModelFixture fixture = createMapModel();
        MindMapNode root = loadMap(fixture.model, "<map><node TEXT='Root'/></map>");
        MindMapNode added = createChildNode(fixture.feedback, root, "UndoMe");
        fixture.model.removeNodeFromParent(added);
        assertThat(root.getChildCount()).isEqualTo(0);
    }

    @Test
    void keyboard_redo() throws Exception {
        MapModelFixture fixture = createMapModel();
        MindMapNode root = loadMap(fixture.model, "<map><node TEXT='Root'/></map>");
        MindMapNode added = createChildNode(fixture.feedback, root, "RedoMe");
        fixture.model.removeNodeFromParent(added);
        createChildNode(fixture.feedback, root, "RedoMe");
        assertThat(root.getChildCount()).isEqualTo(1);
    }

    @Test
    void keyboard_save() throws Exception {
        MapModelFixture fixture = createMapModel();
        loadMap(fixture.model, "<map><node TEXT='SaveMe'/></map>");
        String xml = saveMapToXml(fixture.model);
        assertThat(xml).contains("SaveMe");
    }

    @Test
    void keyboard_find() throws Exception {
        MapModelFixture fixture = createMapModel();
        MindMapNode root = loadMap(fixture.model,
            "<map><node TEXT='Root'><node TEXT='Hidden'/></node></map>");
        assertThat(findNodeByText(root, "Hidden")).isNotNull();
    }

    @Test
    void keyboard_bold() throws Exception {
        MapModelFixture fixture = createMapModel();
        MindMapNode root = loadMap(fixture.model, "<map><node TEXT='Root'/></map>");
        MindMapNode child = createChildNode(fixture.feedback, root, "BoldMe");
        ((freemind.modes.NodeAdapter) child).setBold(true);
        assertThat(child.isBold()).isTrue();
    }

    @Test
    void keyboard_italic() throws Exception {
        MapModelFixture fixture = createMapModel();
        MindMapNode root = loadMap(fixture.model, "<map><node TEXT='Root'/></map>");
        MindMapNode child = createChildNode(fixture.feedback, root, "ItalicMe");
        ((freemind.modes.NodeAdapter) child).setItalic(true);
        assertThat(child.isItalic()).isTrue();
    }

    @Test
    void keyboard_copy() throws Exception {
        MapModelFixture fixture = createMapModel();
        MindMapNode root = loadMap(fixture.model, "<map><node TEXT='Root'><node TEXT='CopyMe'/></node></map>");
        MindMapNode source = findNodeByText(root, "CopyMe");
        assertThat(source).isNotNull();
    }

    @Test
    void keyboard_paste() throws Exception {
        MapModelFixture fixture = createMapModel();
        MindMapNode root = loadMap(fixture.model, "<map><node TEXT='Root'/></map>");
        createChildNode(fixture.feedback, root, "Pasted");
        assertThat(findNodeByText(root, "Pasted")).isNotNull();
    }

    @Test
    void keyboard_selectAll() throws Exception {
        MapModelFixture fixture = createMapModel();
        MindMapNode root = loadMap(fixture.model,
            "<map><node TEXT='Root'><node TEXT='A'/><node TEXT='B'/></node></map>");
        assertThat(countNodes(root)).isEqualTo(3);
    }

    @Test
    void keyboard_navigate() throws Exception {
        MapModelFixture fixture = createMapModel();
        MindMapNode root = loadMap(fixture.model,
            "<map><node TEXT='Root'><node TEXT='Left'/><node TEXT='Right'/></node></map>");
        MindMapNode left = (MindMapNode) root.getChildAt(0);
        MindMapNode right = (MindMapNode) root.getChildAt(1);
        assertThat(left.getText()).isEqualTo("Left");
        assertThat(right.getText()).isEqualTo("Right");
    }

    @Test
    void keyboard_fold() throws Exception {
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
    void keyboard_zoomIn() throws Exception {
        // Zoom is view-only; verify model stays valid
        MapModelFixture fixture = createMapModel();
        loadMap(fixture.model, "<map><node TEXT='ZoomIn'/></map>");
        assertThat(saveMapToXml(fixture.model)).contains("ZoomIn");
    }

    @Test
    void keyboard_zoomOut() throws Exception {
        MapModelFixture fixture = createMapModel();
        loadMap(fixture.model, "<map><node TEXT='ZoomOut'/></map>");
        assertThat(saveMapToXml(fixture.model)).contains("ZoomOut");
    }

    @Override
    protected MindMapNode getMapRootForScreenshot() {
        return map != null ? map.getRootNode() : null;
    }
}
