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
 * GUI tests for node CRUD (Create, Read, Update, Delete) operations.
 * Exercises addNewNode, setNodeText, deleteNode, setNoteText, getNoteText,
 * and verifies child count, position, and nesting.
 */
class NodeCrudGuiTest extends GuiTestBase {

    private static final String BASIC_MAP = "<map><node TEXT='ROOT'/></map>";

    private ExtendedMapFeedbackImpl mapFeedback;
    private MindMapMapModel map;
    private MindMapNode root;

    @BeforeEach
    void setUp() throws Exception {
        mapFeedback = new ExtendedMapFeedbackImpl();
        map = new MindMapMapModel(mapFeedback);
        mapFeedback.setMap(map);
        root = runOnEdtAndGet(() -> {
            Tools.StringReaderCreator reader = new Tools.StringReaderCreator(BASIC_MAP);
            MindMapNode r = map.loadTree(reader, MapAdapter.sDontAskInstance);
            map.setRoot(r);
            return r;
        });
    }

    @Test
    void node_createChild() throws Exception {
        MindMapNode child = runOnEdtAndGet(() ->
            mapFeedback.addNewNode(root, 0, true)
        );
        assertThat(child).isNotNull();
        assertThat(root.getChildCount()).isEqualTo(1);
    }

    @Test
    void node_createMultipleChildren() throws Exception {
        runOnEdt(() -> {
            for (int i = 0; i < 5; i++) {
                mapFeedback.addNewNode(root, i, true);
            }
        });
        assertThat(root.getChildCount()).isEqualTo(5);
        // Verify each position has a node
        for (int i = 0; i < 5; i++) {
            assertThat(root.getChildAt(i)).isNotNull();
        }
    }

    @Test
    void node_createSibling() throws Exception {
        runOnEdt(() -> {
            mapFeedback.addNewNode(root, 0, true);
            mapFeedback.addNewNode(root, 1, true);
        });
        assertThat(root.getChildCount()).isEqualTo(2);
        assertThat(root.getChildAt(0)).isNotNull();
        assertThat(root.getChildAt(1)).isNotNull();
    }

    @Test
    void node_setNodeText() throws Exception {
        MindMapNode child = runOnEdtAndGet(() -> {
            MindMapNode c = mapFeedback.addNewNode(root, 0, true);
            mapFeedback.setNodeText(c, "Hello World");
            return c;
        });
        assertThat(child.getText()).isEqualTo("Hello World");
    }

    @Test
    void node_deleteChild() throws Exception {
        runOnEdt(() -> {
            MindMapNode child = mapFeedback.addNewNode(root, 0, true);
            assertThat(root.getChildCount()).isEqualTo(1);
            mapFeedback.deleteNode(child);
        });
        assertThat(root.getChildCount()).isEqualTo(0);
    }

    @Test
    void node_createNestedChildren() throws Exception {
        MindMapNode grandchild = runOnEdtAndGet(() -> {
            MindMapNode child = mapFeedback.addNewNode(root, 0, true);
            mapFeedback.setNodeText(child, "Child");
            MindMapNode gc = mapFeedback.addNewNode(child, 0, true);
            mapFeedback.setNodeText(gc, "Grandchild");
            return gc;
        });
        // Verify depth: root -> child -> grandchild
        assertThat(grandchild.getText()).isEqualTo("Grandchild");
        assertThat(((MindMapNode) grandchild.getParent()).getText()).isEqualTo("Child");
        assertThat(((MindMapNode) grandchild.getParent().getParent()).getText()).isEqualTo("ROOT");
    }

    @Test
    void node_nodeCount() throws Exception {
        runOnEdt(() -> {
            for (int i = 0; i < 7; i++) {
                mapFeedback.addNewNode(root, i, true);
            }
        });
        assertThat(root.getChildCount()).isEqualTo(7);
    }

    @Test
    void node_nodePosition() throws Exception {
        runOnEdt(() -> {
            for (int i = 0; i < 3; i++) {
                MindMapNode child = mapFeedback.addNewNode(root, i, true);
                mapFeedback.setNodeText(child, "Node_" + i);
            }
        });
        for (int i = 0; i < 3; i++) {
            MindMapNode child = (MindMapNode) root.getChildAt(i);
            assertThat(child.getText()).isEqualTo("Node_" + i);
        }
    }

    @Test
    void node_setNoteText() throws Exception {
        String noteHtml = "<html><body>This is a note</body></html>";
        runOnEdt(() -> {
            MindMapNode child = mapFeedback.addNewNode(root, 0, true);
            mapFeedback.setNoteText(child, noteHtml);
        });
        MindMapNode child = (MindMapNode) root.getChildAt(0);
        assertThat(child.getNoteText()).isEqualTo(noteHtml);
    }

    @Test
    void node_getNoteText() throws Exception {
        String noteHtml = "<html><body>Test note content</body></html>";
        String retrievedNote = runOnEdtAndGet(() -> {
            MindMapNode child = mapFeedback.addNewNode(root, 0, true);
            mapFeedback.setNoteText(child, noteHtml);
            return child.getNoteText();
        });
        assertThat(retrievedNote).isEqualTo(noteHtml);
    }

    @Test
    void node_createAndRenameMultiple() throws Exception {
        runOnEdt(() -> {
            for (int i = 0; i < 5; i++) {
                MindMapNode child = mapFeedback.addNewNode(root, i, true);
                mapFeedback.setNodeText(child, "Original_" + i);
            }
            for (int i = 0; i < 5; i++) {
                MindMapNode child = (MindMapNode) root.getChildAt(i);
                mapFeedback.setNodeText(child, "Renamed_" + i);
            }
        });
        for (int i = 0; i < 5; i++) {
            MindMapNode child = (MindMapNode) root.getChildAt(i);
            assertThat(child.getText()).isEqualTo("Renamed_" + i);
        }
    }

    @Test
    void node_deleteAllChildren() throws Exception {
        runOnEdt(() -> {
            for (int i = 0; i < 10; i++) {
                mapFeedback.addNewNode(root, i, true);
            }
            assertThat(root.getChildCount()).isEqualTo(10);
            // Delete from last to first to avoid index shifting
            for (int i = 9; i >= 0; i--) {
                mapFeedback.deleteNode((MindMapNode) root.getChildAt(i));
            }
        });
        assertThat(root.getChildCount()).isEqualTo(0);
    }

    @Test
    void node_deleteAndReAdd() throws Exception {
        runOnEdt(() -> {
            MindMapNode child = mapFeedback.addNewNode(root, 0, true);
            mapFeedback.setNodeText(child, "Ephemeral");
            mapFeedback.deleteNode(child);
            MindMapNode newChild = mapFeedback.addNewNode(root, 0, true);
            mapFeedback.setNodeText(newChild, "Ephemeral");
        });
        assertThat(root.getChildCount()).isEqualTo(1);
        assertThat(((MindMapNode) root.getChildAt(0)).getText()).isEqualTo("Ephemeral");
    }

    @Test
    void node_nestedDeletion() throws Exception {
        runOnEdt(() -> {
            MindMapNode child = mapFeedback.addNewNode(root, 0, true);
            mapFeedback.setNodeText(child, "Child");
            MindMapNode grandchild = mapFeedback.addNewNode(child, 0, true);
            mapFeedback.setNodeText(grandchild, "Grandchild");
            // Deleting the child should remove the grandchild too
            mapFeedback.deleteNode(child);
        });
        assertThat(root.getChildCount()).isEqualTo(0);
    }

    @Test
    void node_moveChildBetweenParents() throws Exception {
        runOnEdt(() -> {
            MindMapNode parent1 = mapFeedback.addNewNode(root, 0, true);
            mapFeedback.setNodeText(parent1, "Parent1");
            MindMapNode parent2 = mapFeedback.addNewNode(root, 1, true);
            mapFeedback.setNodeText(parent2, "Parent2");

            MindMapNode child = mapFeedback.addNewNode(parent1, 0, true);
            mapFeedback.setNodeText(child, "MovingChild");

            // Move: remove from parent1, insert into parent2
            parent1.remove(0);
            parent2.insert(child, 0);
        });

        MindMapNode parent1 = (MindMapNode) root.getChildAt(0);
        MindMapNode parent2 = (MindMapNode) root.getChildAt(1);
        assertThat(parent1.getChildCount()).isEqualTo(0);
        assertThat(parent2.getChildCount()).isEqualTo(1);
        assertThat(((MindMapNode) parent2.getChildAt(0)).getText()).isEqualTo("MovingChild");
    }

    @Test
    void node_noteWithUnicode() throws Exception {
        runOnEdt(() -> {
            for (int i = 0; i < ALL_SCRIPTS.size(); i++) {
                MindMapNode child = mapFeedback.addNewNode(root, i, true);
                String noteHtml = "<html><body>" + ALL_SCRIPTS.get(i) + "</body></html>";
                mapFeedback.setNoteText(child, noteHtml);
            }
        });

        for (int i = 0; i < ALL_SCRIPTS.size(); i++) {
            MindMapNode child = (MindMapNode) root.getChildAt(i);
            String expectedNote = "<html><body>" + ALL_SCRIPTS.get(i) + "</body></html>";
            assertThat(child.getNoteText())
                .as("Note for script %s should be preserved", ALL_SCRIPT_NAMES.get(i))
                .isEqualTo(expectedNote);
        }
    }

    @Test
    void node_nodeTextWithMaxLength() throws Exception {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 10000; i++) {
            sb.append('X');
        }
        String longText = sb.toString();

        MindMapNode child = runOnEdtAndGet(() -> {
            MindMapNode c = mapFeedback.addNewNode(root, 0, true);
            mapFeedback.setNodeText(c, longText);
            return c;
        });

        assertThat(child.getText()).isEqualTo(longText);
    }

    @Test
    void node_multipleNotesOnDifferentNodes() throws Exception {
        String[] notes = {"Note A", "Note B", "Note C", "Note D", "Note E"};

        runOnEdt(() -> {
            for (int i = 0; i < notes.length; i++) {
                MindMapNode child = mapFeedback.addNewNode(root, i, true);
                mapFeedback.setNodeText(child, "Node_" + i);
                String noteHtml = "<html><body>" + notes[i] + "</body></html>";
                mapFeedback.setNoteText(child, noteHtml);
            }
        });

        for (int i = 0; i < notes.length; i++) {
            MindMapNode child = (MindMapNode) root.getChildAt(i);
            String expectedNote = "<html><body>" + notes[i] + "</body></html>";
            assertThat(child.getNoteText())
                .as("Note on node %d", i)
                .isEqualTo(expectedNote);
        }
    }

    @Test
    void node_clearNoteText() throws Exception {
        MindMapNode child = runOnEdtAndGet(() -> {
            MindMapNode c = mapFeedback.addNewNode(root, 0, true);
            mapFeedback.setNoteText(c, "<html><body>Temporary note</body></html>");
            mapFeedback.setNoteText(c, null);
            return c;
        });

        assertThat(child.getNoteText()).isNull();
    }

    @Test
    void node_rootNodeText() {
        assertThat(root.getText()).isEqualTo("ROOT");
    }

    @Override
    protected MindMapNode getMapRootForScreenshot() {
        return root;
    }
}
