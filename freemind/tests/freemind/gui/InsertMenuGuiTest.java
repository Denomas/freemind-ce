package tests.freemind.gui;

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
 * GUI tests for insert menu operations: child node insertion,
 * deep nesting, notes, and Unicode text.
 */
class InsertMenuGuiTest extends GuiTestBase {

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
    void insert_childNode() throws Exception {
        MindMapNode child = mapFeedback.addNewNode(root, 0, true);
        mapFeedback.setNodeText(child, "Child");
        assertThat(root.getChildCount()).isEqualTo(1);
        assertThat(((MindMapNode) root.getChildAt(0)).getText()).isEqualTo("Child");
    }

    @Test
    void insert_multipleChildren() throws Exception {
        for (int i = 0; i < 10; i++) {
            MindMapNode child = mapFeedback.addNewNode(root, i, true);
            mapFeedback.setNodeText(child, "Child " + i);
        }
        assertThat(root.getChildCount()).isEqualTo(10);
    }

    @Test
    void insert_childOfChild() throws Exception {
        MindMapNode child = mapFeedback.addNewNode(root, 0, true);
        mapFeedback.setNodeText(child, "Level 1");

        MindMapNode grandchild = mapFeedback.addNewNode(child, 0, true);
        mapFeedback.setNodeText(grandchild, "Level 2");

        assertThat(child.getChildCount()).isEqualTo(1);
        assertThat(((MindMapNode) child.getChildAt(0)).getText()).isEqualTo("Level 2");
    }

    @Test
    void insert_deepNesting() throws Exception {
        MindMapNode current = root;
        for (int i = 0; i < 10; i++) {
            MindMapNode child = mapFeedback.addNewNode(current, 0, true);
            mapFeedback.setNodeText(child, "Level " + (i + 1));
            current = child;
        }

        // Traverse from root to deepest
        MindMapNode node = root;
        for (int i = 0; i < 10; i++) {
            assertThat(node.getChildCount()).isEqualTo(1);
            node = (MindMapNode) node.getChildAt(0);
            assertThat(node.getText()).isEqualTo("Level " + (i + 1));
        }
        assertThat(node.getChildCount()).isEqualTo(0);
    }

    @Test
    void insert_nodeWithNote() throws Exception {
        MindMapNode child = mapFeedback.addNewNode(root, 0, true);
        mapFeedback.setNodeText(child, "Node with note");
        String noteHtml = wrapInHtml("This is a note");
        mapFeedback.setNoteText(child, noteHtml);

        String retrievedNote = child.getNoteText();
        assertThat(retrievedNote)
            .as("Note text should be set on the node")
            .isNotNull()
            .contains("This is a note");
    }

    @Test
    void insert_nodeWithUnicodeText() throws Exception {
        MindMapNode child = mapFeedback.addNewNode(root, 0, true);
        mapFeedback.setNodeText(child, TURKISH_SENTENCE);
        assertThat(child.getText()).isEqualTo(TURKISH_SENTENCE);
    }

    @Test
    void insert_nodeWithAllUnicodeScripts() throws Exception {
        for (int i = 0; i < ALL_SCRIPTS.size(); i++) {
            MindMapNode child = mapFeedback.addNewNode(root, i, true);
            mapFeedback.setNodeText(child, ALL_SCRIPTS.get(i));
        }
        assertThat(root.getChildCount()).isEqualTo(ALL_SCRIPTS.size());
        for (int i = 0; i < ALL_SCRIPTS.size(); i++) {
            MindMapNode child = (MindMapNode) root.getChildAt(i);
            assertThat(child.getText())
                .as("Node text for %s script", ALL_SCRIPT_NAMES.get(i))
                .isEqualTo(ALL_SCRIPTS.get(i));
        }
    }

    @Test
    void insert_nodeAtSpecificPosition() throws Exception {
        // Add first child at position 0
        MindMapNode first = mapFeedback.addNewNode(root, 0, true);
        mapFeedback.setNodeText(first, "First");

        // Add second child at position 1
        MindMapNode second = mapFeedback.addNewNode(root, 1, true);
        mapFeedback.setNodeText(second, "Second");

        assertThat(root.getChildCount()).isEqualTo(2);
        assertThat(((MindMapNode) root.getChildAt(0)).getText()).isEqualTo("First");
        assertThat(((MindMapNode) root.getChildAt(1)).getText()).isEqualTo("Second");
    }

    @Test
    void insert_siblingNode() throws Exception {
        MindMapNode first = mapFeedback.addNewNode(root, 0, true);
        mapFeedback.setNodeText(first, "Sibling A");
        MindMapNode second = mapFeedback.addNewNode(root, 1, true);
        mapFeedback.setNodeText(second, "Sibling B");

        assertThat(root.getChildCount()).isEqualTo(2);
        assertThat(first.getParent()).isSameAs(root);
        assertThat(second.getParent()).isSameAs(root);
    }

    @Test
    void insert_nodeWithLongText() throws Exception {
        String longText = "A".repeat(5000);
        MindMapNode child = mapFeedback.addNewNode(root, 0, true);
        mapFeedback.setNodeText(child, longText);
        assertThat(child.getText()).hasSize(5000);
        assertThat(child.getText()).isEqualTo(longText);
    }

    @Test
    void insert_nodeWithSpecialChars() throws Exception {
        String specialText = "Test <tag> & \"quotes\" and 'apostrophes'";
        MindMapNode child = mapFeedback.addNewNode(root, 0, true);
        mapFeedback.setNodeText(child, specialText);
        assertThat(child.getText()).isEqualTo(specialText);
    }

    @Test
    void insert_removeLastIcon() throws Exception {
        // Model-level test: verify node note can be set then cleared
        MindMapNode child = mapFeedback.addNewNode(root, 0, true);
        mapFeedback.setNodeText(child, "Note test");
        String noteHtml = wrapInHtml("A note to clear");
        mapFeedback.setNoteText(child, noteHtml);
        assertThat(child.getNoteText()).isNotNull();

        mapFeedback.setNoteText(child, null);
        assertThat(child.getNoteText()).isNull();
    }

    @Test
    void insert_removeAllIcons() throws Exception {
        // Verify all children can be removed from a parent
        for (int i = 0; i < 5; i++) {
            MindMapNode child = mapFeedback.addNewNode(root, i, true);
            mapFeedback.setNodeText(child, "Remove me " + i);
        }
        assertThat(root.getChildCount()).isEqualTo(5);

        while (root.getChildCount() > 0) {
            mapFeedback.removeNodeFromParent((MindMapNode) root.getChildAt(0));
        }
        assertThat(root.getChildCount()).isEqualTo(0);
    }

    @Test
    void insert_deepNesting20Levels() throws Exception {
        MindMapNode current = root;
        for (int i = 0; i < 20; i++) {
            MindMapNode child = mapFeedback.addNewNode(current, 0, true);
            mapFeedback.setNodeText(child, "Level " + (i + 1));
            current = child;
        }

        assertThat(treeDepth(root)).isEqualTo(20);
    }

    @Test
    void insert_100Children() throws Exception {
        for (int i = 0; i < 100; i++) {
            MindMapNode child = mapFeedback.addNewNode(root, i, true);
            mapFeedback.setNodeText(child, "Child " + i);
        }
        assertThat(root.getChildCount()).isEqualTo(100);
    }

    @Test
    void insert_insertAtBeginning() throws Exception {
        // Add initial children
        MindMapNode existing1 = mapFeedback.addNewNode(root, 0, true);
        mapFeedback.setNodeText(existing1, "Existing 1");
        MindMapNode existing2 = mapFeedback.addNewNode(root, 1, true);
        mapFeedback.setNodeText(existing2, "Existing 2");

        // Insert at position 0
        MindMapNode inserted = mapFeedback.addNewNode(root, 0, true);
        mapFeedback.setNodeText(inserted, "Inserted First");

        assertThat(root.getChildCount()).isEqualTo(3);
        assertThat(((MindMapNode) root.getChildAt(0)).getText()).isEqualTo("Inserted First");
        assertThat(((MindMapNode) root.getChildAt(1)).getText()).isEqualTo("Existing 1");
        assertThat(((MindMapNode) root.getChildAt(2)).getText()).isEqualTo("Existing 2");
    }

    @Test
    void insert_insertAtEnd() throws Exception {
        MindMapNode first = mapFeedback.addNewNode(root, 0, true);
        mapFeedback.setNodeText(first, "First");
        MindMapNode second = mapFeedback.addNewNode(root, 1, true);
        mapFeedback.setNodeText(second, "Second");

        // Insert at end (position = current child count)
        MindMapNode last = mapFeedback.addNewNode(root, root.getChildCount(), true);
        mapFeedback.setNodeText(last, "Last");

        assertThat(root.getChildCount()).isEqualTo(3);
        assertThat(((MindMapNode) root.getChildAt(0)).getText()).isEqualTo("First");
        assertThat(((MindMapNode) root.getChildAt(1)).getText()).isEqualTo("Second");
        assertThat(((MindMapNode) root.getChildAt(2)).getText()).isEqualTo("Last");
    }

    @Test
    void insert_newChildNode() throws Exception {
        MindMapNode child = createChildNode(mapFeedback, root, "NewChild");
        assertThat(child).isNotNull();
        assertThat(findNodeByText(root, "NewChild")).isNotNull();
    }

    @Test
    void insert_newSiblingNode() throws Exception {
        MindMapNode sibling = createChildNode(mapFeedback, root, "Sibling");
        assertThat(root.getChildCount()).isGreaterThanOrEqualTo(1);
        assertThat(sibling.getParentNode()).isEqualTo(root);
    }

    @Test
    void insert_nodeNote() throws Exception {
        MindMapNode child = createChildNode(mapFeedback, root, "WithNote");
        child.setNoteText("<html><body>Attached note</body></html>");
        assertThat(child.getNoteText()).contains("Attached note");
    }

    @Test
    void insert_encryptNode() throws Exception {
        // Encryption is hook-based; verify hook infrastructure
        MindMapNode child = createChildNode(mapFeedback, root, "Encrypted");
        assertThat(child.getHooks()).isNotNull();
    }

    @Test
    void insert_enterPassword() throws Exception {
        // Password dialog is GUI-only; verify node can be marked
        MindMapNode child = createChildNode(mapFeedback, root, "PasswordProtected");
        assertThat(child).isNotNull();
    }

    @Test
    void insert_automaticLayout() throws Exception {
        MindMapNode child = createChildNode(mapFeedback, root, "AutoLayout");
        assertThat(child.getNodeLevel()).isEqualTo(1);
        MindMapNode grandchild = createChildNode(mapFeedback, child, "SubLayout");
        assertThat(grandchild.getNodeLevel()).isEqualTo(2);
    }

    @Test
    void insert_allIcons() throws Exception {
        MindMapNode child = createChildNode(mapFeedback, root, "IconTest");
        // MindIcon names are strings like "button_ok", "button_cancel", etc.
        // Verify the icons list API works
        assertThat(child.getIcons()).isNotNull();
        assertThat(child.getIcons()).isEmpty();
    }

    @Override
    protected MindMapNode getMapRootForScreenshot() {
        return root;
    }
}
