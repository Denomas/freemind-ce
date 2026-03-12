package tests.freemind.gui;

import freemind.main.Tools;
import freemind.modes.ExtendedMapFeedbackImpl;
import freemind.modes.MapAdapter;
import freemind.modes.MindMapNode;
import freemind.modes.mindmapmode.MindMapMapModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * GUI tests for edit menu operations: undo/redo simulation,
 * clipboard simulation, node creation and deletion.
 */
class EditMenuGuiTest extends GuiTestBase {

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
    void edit_undoAfterNodeCreate() throws Exception {
        // Create a child, then "undo" by removing it
        MindMapNode child = mapFeedback.addNewNode(root, 0, true);
        mapFeedback.setNodeText(child, "To be undone");
        assertThat(root.getChildCount()).isEqualTo(1);

        // Simulate undo by removing the node
        mapFeedback.removeNodeFromParent(child);
        assertThat(root.getChildCount()).isEqualTo(0);
    }

    @Test
    void edit_multipleChildCreation() throws Exception {
        for (int i = 0; i < 5; i++) {
            MindMapNode child = mapFeedback.addNewNode(root, i, true);
            mapFeedback.setNodeText(child, "Child " + i);
        }
        assertThat(root.getChildCount()).isEqualTo(5);
        for (int i = 0; i < 5; i++) {
            MindMapNode child = (MindMapNode) root.getChildAt(i);
            assertThat(child.getText()).isEqualTo("Child " + i);
        }
    }

    @Test
    void edit_nodeTextChange() throws Exception {
        MindMapNode child = mapFeedback.addNewNode(root, 0, true);
        mapFeedback.setNodeText(child, "Original");
        assertThat(child.getText()).isEqualTo("Original");

        mapFeedback.setNodeText(child, "Changed");
        assertThat(child.getText()).isEqualTo("Changed");
    }

    @Test
    void edit_nodeTextChangeMultipleTimes() throws Exception {
        MindMapNode child = mapFeedback.addNewNode(root, 0, true);
        mapFeedback.setNodeText(child, "First");
        mapFeedback.setNodeText(child, "Second");
        mapFeedback.setNodeText(child, "Third");
        assertThat(child.getText()).isEqualTo("Third");
    }

    @Test
    void edit_copyNodeText() throws Exception {
        MindMapNode child = mapFeedback.addNewNode(root, 0, true);
        mapFeedback.setNodeText(child, "Copy me");
        // Simulate "copy" by reading getText()
        String copiedText = child.getText();
        assertThat(copiedText).isEqualTo("Copy me");
    }

    @Test
    void edit_createAndDeleteNode() throws Exception {
        MindMapNode child = mapFeedback.addNewNode(root, 0, true);
        mapFeedback.setNodeText(child, "Temporary");
        assertThat(root.getChildCount()).isEqualTo(1);

        mapFeedback.removeNodeFromParent(child);
        assertThat(root.getChildCount()).isEqualTo(0);
    }

    @Test
    void edit_deleteMiddleChild() throws Exception {
        MindMapNode first = mapFeedback.addNewNode(root, 0, true);
        mapFeedback.setNodeText(first, "First");
        MindMapNode middle = mapFeedback.addNewNode(root, 1, true);
        mapFeedback.setNodeText(middle, "Middle");
        MindMapNode last = mapFeedback.addNewNode(root, 2, true);
        mapFeedback.setNodeText(last, "Last");

        assertThat(root.getChildCount()).isEqualTo(3);

        mapFeedback.removeNodeFromParent(middle);
        assertThat(root.getChildCount()).isEqualTo(2);

        MindMapNode remaining0 = (MindMapNode) root.getChildAt(0);
        MindMapNode remaining1 = (MindMapNode) root.getChildAt(1);
        assertThat(remaining0.getText()).isEqualTo("First");
        assertThat(remaining1.getText()).isEqualTo("Last");
    }

    @Test
    void edit_selectAllSimulation() throws Exception {
        for (int i = 0; i < 5; i++) {
            MindMapNode child = mapFeedback.addNewNode(root, i, true);
            mapFeedback.setNodeText(child, "Item " + i);
        }

        // Simulate "select all" by iterating all children
        int selectedCount = 0;
        for (int i = 0; i < root.getChildCount(); i++) {
            MindMapNode child = (MindMapNode) root.getChildAt(i);
            assertThat(child).isNotNull();
            selectedCount++;
        }
        assertThat(selectedCount).isEqualTo(5);
    }

    @Test
    void edit_selectBranchSimulation() throws Exception {
        // Build tree: root -> A -> B, root -> C
        MindMapNode nodeA = mapFeedback.addNewNode(root, 0, true);
        mapFeedback.setNodeText(nodeA, "A");
        MindMapNode nodeB = mapFeedback.addNewNode(nodeA, 0, true);
        mapFeedback.setNodeText(nodeB, "B");
        MindMapNode nodeC = mapFeedback.addNewNode(root, 1, true);
        mapFeedback.setNodeText(nodeC, "C");

        // Count nodes under A (including A itself)
        int branchCount = countNodes(nodeA);
        assertThat(branchCount).isEqualTo(2);
    }

    @Test
    void edit_pasteSimulation() throws Exception {
        MindMapNode source = mapFeedback.addNewNode(root, 0, true);
        mapFeedback.setNodeText(source, "Copied content");

        // Simulate copy-paste by reading text and creating new node with same text
        String copiedText = source.getText();
        MindMapNode target = mapFeedback.addNewNode(root, 1, true);
        mapFeedback.setNodeText(target, copiedText);

        assertThat(target.getText()).isEqualTo("Copied content");
        assertThat(root.getChildCount()).isEqualTo(2);
    }

    @Test
    void edit_findSimulation() throws Exception {
        for (int i = 0; i < 10; i++) {
            MindMapNode child = mapFeedback.addNewNode(root, i, true);
            mapFeedback.setNodeText(child, "Node " + i);
        }

        MindMapNode found = findNodeByText(root, "Node 7");
        assertThat(found)
            .as("findNodeByText should locate 'Node 7'")
            .isNotNull();
        assertThat(found.getText()).isEqualTo("Node 7");
    }

    @Test
    void edit_findNoMatch() throws Exception {
        for (int i = 0; i < 10; i++) {
            MindMapNode child = mapFeedback.addNewNode(root, i, true);
            mapFeedback.setNodeText(child, "Node " + i);
        }

        MindMapNode found = findNodeByText(root, "NonExistent");
        assertThat(found)
            .as("findNodeByText should return null for non-existent text")
            .isNull();
    }

    @Test
    void edit_undoMultipleSteps() throws Exception {
        // Create 5 children
        MindMapNode[] children = new MindMapNode[5];
        for (int i = 0; i < 5; i++) {
            children[i] = mapFeedback.addNewNode(root, i, true);
            mapFeedback.setNodeText(children[i], "Step " + i);
        }
        assertThat(root.getChildCount()).isEqualTo(5);

        // Simulate undo by removing one at a time, verify count decreases
        for (int i = 4; i >= 0; i--) {
            mapFeedback.removeNodeFromParent(children[i]);
            assertThat(root.getChildCount())
                .as("After removing step %d, count should be %d", i, i)
                .isEqualTo(i);
        }
    }

    @Test
    void edit_nodeListSimulation() throws Exception {
        String[] texts = {"Alpha", "Beta", "Gamma", "Delta", "Epsilon"};
        for (int i = 0; i < texts.length; i++) {
            MindMapNode child = mapFeedback.addNewNode(root, i, true);
            mapFeedback.setNodeText(child, texts[i]);
        }

        // Collect all node texts into a list
        List<String> collectedTexts = new ArrayList<>();
        for (int i = 0; i < root.getChildCount(); i++) {
            MindMapNode child = (MindMapNode) root.getChildAt(i);
            collectedTexts.add(child.getText());
        }

        assertThat(collectedTexts).containsExactly("Alpha", "Beta", "Gamma", "Delta", "Epsilon");
    }

    @Test
    void edit_undo() throws Exception {
        MindMapNode child = createChildNode(mapFeedback, root, "ToUndo");
        assertThat(root.getChildCount()).isEqualTo(1);
        map.removeNodeFromParent(child);
        assertThat(root.getChildCount()).as("After undo simulation").isEqualTo(0);
    }

    @Test
    void edit_redo() throws Exception {
        MindMapNode child = createChildNode(mapFeedback, root, "ToRedo");
        map.removeNodeFromParent(child);
        // Redo = re-add
        MindMapNode redone = createChildNode(mapFeedback, root, "ToRedo");
        assertThat(root.getChildCount()).isEqualTo(1);
        assertThat(redone.getText()).isEqualTo("ToRedo");
    }

    @Test
    void edit_undoMultiple() throws Exception {
        MindMapNode c1 = createChildNode(mapFeedback, root, "U1");
        MindMapNode c2 = createChildNode(mapFeedback, root, "U2");
        MindMapNode c3 = createChildNode(mapFeedback, root, "U3");
        assertThat(root.getChildCount()).isEqualTo(3);
        map.removeNodeFromParent(c3);
        map.removeNodeFromParent(c2);
        map.removeNodeFromParent(c1);
        assertThat(root.getChildCount()).isEqualTo(0);
    }

    @Test
    void edit_selectAll() throws Exception {
        createChildNode(mapFeedback, root, "S1");
        createChildNode(mapFeedback, root, "S2");
        createChildNode(mapFeedback, root, "S3");
        int total = countNodes(root);
        assertThat(total).isEqualTo(4);
    }

    @Test
    void edit_selectBranch() throws Exception {
        MindMapNode branch = createChildNode(mapFeedback, root, "Branch");
        createChildNode(mapFeedback, branch, "B1");
        createChildNode(mapFeedback, branch, "B2");
        assertThat(countNodes(branch)).isEqualTo(3);
    }

    @Test
    void edit_copy() throws Exception {
        MindMapNode child = createChildNode(mapFeedback, root, "CopyTarget");
        String copiedText = child.getText();
        assertThat(copiedText).isEqualTo("CopyTarget");
    }

    @Test
    void edit_copySingle() throws Exception {
        MindMapNode parent = createChildNode(mapFeedback, root, "Parent");
        createChildNode(mapFeedback, parent, "Child");
        // Copy single = only parent text, not children
        String text = parent.getText();
        assertThat(text).isEqualTo("Parent");
        assertThat(parent.getChildCount()).isEqualTo(1);
    }

    @Test
    void edit_paste() throws Exception {
        MindMapNode source = createChildNode(mapFeedback, root, "Source");
        String copiedText = source.getText();
        MindMapNode pasted = createChildNode(mapFeedback, root, copiedText);
        assertThat(pasted.getText()).isEqualTo("Source");
        assertThat(root.getChildCount()).isEqualTo(2);
    }

    @Test
    void edit_pasteAsPlainText() throws Exception {
        // Plain text paste strips formatting — verify plain text extraction
        MindMapNode child = createChildNode(mapFeedback, root, "PlainPaste");
        String plain = child.getPlainTextContent();
        assertThat(plain).isNotNull();
    }

    @Test
    void edit_pasteAsClone() throws Exception {
        MindMapNode original = createChildNode(mapFeedback, root, "Original");
        // Clone simulation: create node with same text
        MindMapNode clone = createChildNode(mapFeedback, root, original.getText());
        assertThat(clone.getText()).isEqualTo(original.getText());
    }

    @Test
    void edit_find() throws Exception {
        createChildNode(mapFeedback, root, "FindMe");
        createChildNode(mapFeedback, root, "NotThis");
        MindMapNode found = findNodeByText(root, "FindMe");
        assertThat(found).isNotNull();
        assertThat(found.getText()).isEqualTo("FindMe");
    }

    @Test
    void edit_findNext() throws Exception {
        createChildNode(mapFeedback, root, "Item1");
        createChildNode(mapFeedback, root, "Item2");
        createChildNode(mapFeedback, root, "Item3");
        // Find successive nodes
        assertThat(findNodeByText(root, "Item1")).isNotNull();
        assertThat(findNodeByText(root, "Item2")).isNotNull();
        assertThat(findNodeByText(root, "Item3")).isNotNull();
    }

    @Test
    void edit_nodeList() throws Exception {
        for (int i = 0; i < 10; i++) {
            createChildNode(mapFeedback, root, "ListItem" + i);
        }
        assertThat(root.getChildCount()).isEqualTo(10);
    }

    @Test
    void edit_searchInMap() throws Exception {
        MindMapNode deep = createChildNode(mapFeedback, root, "Surface");
        createChildNode(mapFeedback, deep, "DeepSearch");
        MindMapNode found = findNodeByText(root, "DeepSearch");
        assertThat(found).isNotNull();
    }

    @Test
    void edit_searchMultipleMaps() throws Exception {
        MapModelFixture f1 = createMapModel();
        MapModelFixture f2 = createMapModel();
        MindMapNode r1 = loadMap(f1.model, "<map><node TEXT='Map1Root'><node TEXT='Target1'/></node></map>");
        MindMapNode r2 = loadMap(f2.model, "<map><node TEXT='Map2Root'><node TEXT='Target2'/></node></map>");
        assertThat(findNodeByText(r1, "Target1")).isNotNull();
        assertThat(findNodeByText(r2, "Target2")).isNotNull();
    }

    @Override
    protected MindMapNode getMapRootForScreenshot() {
        return root;
    }
}
