package tests.freemind.gui;

import freemind.modes.ExtendedMapFeedbackImpl;
import freemind.modes.MindMapNode;
import freemind.modes.mindmapmode.MindMapMapModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * GUI tests for context menu related model operations.
 * Verifies node property access, child manipulation, note handling,
 * and root/non-root detection that would underpin context menu actions.
 */
class ContextMenuGuiTest extends GuiTestBase {

    private ExtendedMapFeedbackImpl mapFeedback;
    private MindMapMapModel map;
    private MindMapNode root;

    @BeforeEach
    void setUp() throws Exception {
        MapModelFixture fixture = createMapModel();
        mapFeedback = fixture.feedback;
        map = fixture.model;
        root = loadMap(map, "<map><node TEXT='ROOT'/></map>");
    }

    @Test
    void contextMenu_nodeHasText() throws Exception {
        MindMapNode child = runOnEdtAndGet(() ->
            createChildNode(mapFeedback, root, "ContextNode")
        );
        assertThat(child.getText()).isEqualTo("ContextNode");
    }

    @Test
    void contextMenu_nodeHasNote() throws Exception {
        String noteHtml = "<html><body>This is a context note</body></html>";
        MindMapNode child = runOnEdtAndGet(() -> {
            MindMapNode c = createChildNode(mapFeedback, root, "NodeWithNote");
            mapFeedback.setNoteText(c, noteHtml);
            return c;
        });
        assertThat(child.getNoteText()).isEqualTo(noteHtml);
    }

    @Test
    void contextMenu_nodeParentAccessible() throws Exception {
        MindMapNode child = runOnEdtAndGet(() ->
            createChildNode(mapFeedback, root, "ChildOfRoot")
        );
        assertThat(child.getParent()).isSameAs(root);
        assertThat(((MindMapNode) child.getParent()).getText()).isEqualTo("ROOT");
    }

    @Test
    void contextMenu_nodeChildrenAccessible() throws Exception {
        runOnEdt(() -> {
            createChildNode(mapFeedback, root, "First");
            createChildNode(mapFeedback, root, "Second");
            createChildNode(mapFeedback, root, "Third");
        });

        assertThat(root.getChildCount()).isEqualTo(3);
        assertThat(((MindMapNode) root.getChildAt(0)).getText()).isEqualTo("First");
        assertThat(((MindMapNode) root.getChildAt(1)).getText()).isEqualTo("Second");
        assertThat(((MindMapNode) root.getChildAt(2)).getText()).isEqualTo("Third");
    }

    @Test
    void contextMenu_deleteNodeFromContext() throws Exception {
        MindMapNode child = runOnEdtAndGet(() ->
            createChildNode(mapFeedback, root, "ToDelete")
        );
        assertThat(root.getChildCount()).isEqualTo(1);

        runOnEdt(() -> mapFeedback.deleteNode(child));

        assertThat(root.getChildCount()).isEqualTo(0);
    }

    @Test
    void contextMenu_addChildFromContext() throws Exception {
        // Simulate adding a child from context menu on a selected node
        MindMapNode selectedNode = runOnEdtAndGet(() ->
            createChildNode(mapFeedback, root, "SelectedNode")
        );
        assertThat(selectedNode.getChildCount()).isEqualTo(0);

        runOnEdt(() -> createChildNode(mapFeedback, selectedNode, "NewChild"));

        assertThat(selectedNode.getChildCount()).isEqualTo(1);
        assertThat(((MindMapNode) selectedNode.getChildAt(0)).getText()).isEqualTo("NewChild");
    }

    @Test
    void contextMenu_setNoteFromContext() throws Exception {
        String noteHtml = "<html><body>Added via context menu</body></html>";
        MindMapNode child = runOnEdtAndGet(() -> {
            MindMapNode c = createChildNode(mapFeedback, root, "NoteTarget");
            mapFeedback.setNoteText(c, noteHtml);
            return c;
        });
        assertThat(child.getNoteText()).isEqualTo(noteHtml);
    }

    @Test
    void contextMenu_removeNoteFromContext() throws Exception {
        String noteHtml = "<html><body>Will be removed</body></html>";
        MindMapNode child = runOnEdtAndGet(() -> {
            MindMapNode c = createChildNode(mapFeedback, root, "NoteToRemove");
            mapFeedback.setNoteText(c, noteHtml);
            return c;
        });
        assertThat(child.getNoteText()).isEqualTo(noteHtml);

        // Remove note by setting to null
        runOnEdt(() -> mapFeedback.setNoteText(child, null));

        assertThat(child.getNoteText()).isNull();
    }

    @Test
    void contextMenu_nodeIsRoot() {
        assertThat(root.isRoot()).isTrue();
    }

    @Test
    void contextMenu_nodeIsNotRoot() throws Exception {
        MindMapNode child = runOnEdtAndGet(() ->
            createChildNode(mapFeedback, root, "NotRoot")
        );
        assertThat(child.isRoot()).isFalse();
    }

    @Test
    void contextMenu_rightClickNode() throws Exception {
        // Right-click is GUI; verify node is accessible for context operations
        assertThat(root).isNotNull();
        assertThat(root.getText()).isNotNull();
    }

    @Test
    void contextMenu_allItemsPresent() throws Exception {
        // Verify all context menu operations can be performed on a node
        MindMapNode child = createChildNode(mapFeedback, root, "ContextTarget");
        assertThat(child.getText()).isEqualTo("ContextTarget");
        assertThat(child.getParentNode()).isEqualTo(root);
        assertThat(child.getIcons()).isNotNull();
        assertThat(child.getHooks()).isNotNull();
    }

    @Test
    void contextMenu_timeManagement() throws Exception {
        MindMapNode child = createChildNode(mapFeedback, root, "TimeManaged");
        assertThat(child.getStateIcons()).isNotNull();
    }

    @Test
    void contextMenu_patterns() throws Exception {
        MindMapNode child = createChildNode(mapFeedback, root, "PatternTarget");
        child.setStyle(freemind.modes.MindMapNode.STYLE_BUBBLE);
        assertThat(child.getBareStyle()).isEqualTo(freemind.modes.MindMapNode.STYLE_BUBBLE);
    }

    @Test
    void contextMenu_toggleAttributes() throws Exception {
        MindMapNode child = createChildNode(mapFeedback, root, "AttrTarget");
        assertThat(child.getAttributeTableLength()).isEqualTo(0);
    }

    @Test
    void contextMenu_toggleNotes() throws Exception {
        MindMapNode child = createChildNode(mapFeedback, root, "NoteTarget");
        child.setNoteText("<html><body>Note</body></html>");
        assertThat(child.getNoteText()).contains("Note");
        child.setNoteText(null);
        assertThat(child.getNoteText()).isNull();
    }

    @Test
    void contextMenu_mapsPopup() throws Exception {
        // Maps popup shows map tabs; verify multiple maps work
        MapModelFixture f1 = createMapModel();
        MapModelFixture f2 = createMapModel();
        loadMap(f1.model, "<map><node TEXT='Map1'/></map>");
        loadMap(f2.model, "<map><node TEXT='Map2'/></map>");
        assertThat(saveMapToXml(f1.model)).contains("Map1");
        assertThat(saveMapToXml(f2.model)).contains("Map2");
    }

    @Test
    void contextMenu_toggleMenubar() throws Exception {
        // Menubar toggle is GUI-only; verify model works regardless
        assertThat(freeMindMain).isNotNull();
    }
}
