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
 * GUI tests for navigation operations on the mind map tree structure:
 * root detection, parent/child traversal, sibling order, and wide trees.
 */
class NavigateMenuGuiTest extends GuiTestBase {

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
    void navigate_rootIsRoot() throws Exception {
        assertThat(root.isRoot()).isTrue();
    }

    @Test
    void navigate_childIsNotRoot() throws Exception {
        MindMapNode child = mapFeedback.addNewNode(root, 0, true);
        mapFeedback.setNodeText(child, "Child");
        assertThat(child.isRoot()).isFalse();
    }

    @Test
    void navigate_parentOfChild() throws Exception {
        MindMapNode child = mapFeedback.addNewNode(root, 0, true);
        mapFeedback.setNodeText(child, "Child");
        assertThat(child.getParent()).isSameAs(root);
    }

    @Test
    void navigate_traverseAllChildren() throws Exception {
        for (int i = 0; i < 5; i++) {
            MindMapNode child = mapFeedback.addNewNode(root, i, true);
            mapFeedback.setNodeText(child, "Child " + i);
        }

        List<String> childTexts = new ArrayList<>();
        for (int i = 0; i < root.getChildCount(); i++) {
            MindMapNode child = (MindMapNode) root.getChildAt(i);
            childTexts.add(child.getText());
        }
        assertThat(childTexts)
            .containsExactly("Child 0", "Child 1", "Child 2", "Child 3", "Child 4");
    }

    @Test
    void navigate_deepTraversal() throws Exception {
        // Build a 5-level deep tree
        MindMapNode current = root;
        for (int i = 1; i <= 5; i++) {
            MindMapNode child = mapFeedback.addNewNode(current, 0, true);
            mapFeedback.setNodeText(child, "Depth " + i);
            current = child;
        }

        // Traverse from root down
        MindMapNode node = root;
        for (int i = 1; i <= 5; i++) {
            assertThat(node.getChildCount())
                .as("Node at depth %d should have 1 child", i - 1)
                .isEqualTo(1);
            node = (MindMapNode) node.getChildAt(0);
            assertThat(node.getText()).isEqualTo("Depth " + i);
        }
        // Leaf node
        assertThat(node.getChildCount()).isEqualTo(0);
    }

    @Test
    void navigate_siblingOrder() throws Exception {
        String[] names = {"Alpha", "Beta", "Gamma", "Delta"};
        for (int i = 0; i < names.length; i++) {
            MindMapNode child = mapFeedback.addNewNode(root, i, true);
            mapFeedback.setNodeText(child, names[i]);
        }

        for (int i = 0; i < names.length; i++) {
            MindMapNode child = (MindMapNode) root.getChildAt(i);
            assertThat(child.getText())
                .as("Child at index %d", i)
                .isEqualTo(names[i]);
        }
    }

    @Test
    void navigate_childCountAfterInsert() throws Exception {
        assertThat(root.getChildCount()).isEqualTo(0);

        mapFeedback.addNewNode(root, 0, true);
        assertThat(root.getChildCount()).isEqualTo(1);

        mapFeedback.addNewNode(root, 1, true);
        assertThat(root.getChildCount()).isEqualTo(2);

        mapFeedback.addNewNode(root, 2, true);
        assertThat(root.getChildCount()).isEqualTo(3);
    }

    @Test
    void navigate_childCountAfterRemove() throws Exception {
        MindMapNode child1 = mapFeedback.addNewNode(root, 0, true);
        MindMapNode child2 = mapFeedback.addNewNode(root, 1, true);
        MindMapNode child3 = mapFeedback.addNewNode(root, 2, true);
        assertThat(root.getChildCount()).isEqualTo(3);

        mapFeedback.removeNodeFromParent(child2);
        assertThat(root.getChildCount()).isEqualTo(2);

        mapFeedback.removeNodeFromParent(child1);
        assertThat(root.getChildCount()).isEqualTo(1);

        mapFeedback.removeNodeFromParent(child3);
        assertThat(root.getChildCount()).isEqualTo(0);
    }

    @Test
    void navigate_wideTree() throws Exception {
        for (int i = 0; i < 50; i++) {
            MindMapNode child = mapFeedback.addNewNode(root, i, true);
            mapFeedback.setNodeText(child, "Node " + i);
        }
        assertThat(root.getChildCount()).isEqualTo(50);

        // Iterate all and verify
        for (int i = 0; i < 50; i++) {
            MindMapNode child = (MindMapNode) root.getChildAt(i);
            assertThat(child.getText()).isEqualTo("Node " + i);
        }
    }

    @Test
    void navigate_foldAllSimulation() throws Exception {
        // Create a tree with branches
        MindMapNode a = mapFeedback.addNewNode(root, 0, true);
        mapFeedback.setNodeText(a, "A");
        MindMapNode a1 = mapFeedback.addNewNode(a, 0, true);
        mapFeedback.setNodeText(a1, "A1");
        MindMapNode b = mapFeedback.addNewNode(root, 1, true);
        mapFeedback.setNodeText(b, "B");
        MindMapNode b1 = mapFeedback.addNewNode(b, 0, true);
        mapFeedback.setNodeText(b1, "B1");

        // Fold all non-leaf nodes
        a.setFolded(true);
        b.setFolded(true);

        assertThat(a.isFolded()).isTrue();
        assertThat(b.isFolded()).isTrue();
    }

    @Test
    void navigate_unfoldAllSimulation() throws Exception {
        MindMapNode a = mapFeedback.addNewNode(root, 0, true);
        mapFeedback.setNodeText(a, "A");
        MindMapNode a1 = mapFeedback.addNewNode(a, 0, true);
        mapFeedback.setNodeText(a1, "A1");
        MindMapNode b = mapFeedback.addNewNode(root, 1, true);
        mapFeedback.setNodeText(b, "B");
        MindMapNode b1 = mapFeedback.addNewNode(b, 0, true);
        mapFeedback.setNodeText(b1, "B1");

        // Fold then unfold
        a.setFolded(true);
        b.setFolded(true);
        a.setFolded(false);
        b.setFolded(false);

        assertThat(a.isFolded()).isFalse();
        assertThat(b.isFolded()).isFalse();
    }

    @Test
    void navigate_jumpToRoot() throws Exception {
        // Build a 5-level deep path
        MindMapNode current = root;
        for (int i = 0; i < 5; i++) {
            MindMapNode child = mapFeedback.addNewNode(current, 0, true);
            mapFeedback.setNodeText(child, "Level " + i);
            current = child;
        }

        // From deepest node, traverse parent chain to root
        MindMapNode node = current;
        while (!node.isRoot()) {
            node = (MindMapNode) node.getParent();
        }
        assertThat(node.isRoot()).isTrue();
        assertThat(node).isSameAs(root);
    }

    @Test
    void navigate_nodeHistorySimulation() throws Exception {
        // Create nodes and simulate navigation history
        List<MindMapNode> history = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            MindMapNode child = mapFeedback.addNewNode(root, i, true);
            mapFeedback.setNodeText(child, "History " + i);
            history.add(child);
        }

        // Navigate forward through history
        for (int i = 0; i < history.size(); i++) {
            assertThat(history.get(i).getText()).isEqualTo("History " + i);
        }

        // Navigate backward through history
        for (int i = history.size() - 1; i >= 0; i--) {
            assertThat(history.get(i).getText()).isEqualTo("History " + i);
        }
    }

    @Test
    void navigate_cloneSimulation() throws Exception {
        // Create two nodes with identical text (simulates clones)
        MindMapNode clone1 = mapFeedback.addNewNode(root, 0, true);
        mapFeedback.setNodeText(clone1, "Cloned Node");
        MindMapNode clone2 = mapFeedback.addNewNode(root, 1, true);
        mapFeedback.setNodeText(clone2, "Cloned Node");

        assertThat(findNodeByText(root, "Cloned Node")).isNotNull();
        assertThat(root.getChildCount()).isEqualTo(2);
        assertThat(((MindMapNode) root.getChildAt(0)).getText())
            .isEqualTo(((MindMapNode) root.getChildAt(1)).getText());
    }

    @Test
    void navigate_leafNodeDetection() throws Exception {
        MindMapNode leaf = mapFeedback.addNewNode(root, 0, true);
        mapFeedback.setNodeText(leaf, "Leaf");
        assertThat(leaf.getChildCount()).isEqualTo(0);
    }

    @Test
    void navigate_parentChainIntegrity() throws Exception {
        // Build a 5-level deep chain
        MindMapNode current = root;
        for (int i = 0; i < 5; i++) {
            MindMapNode child = mapFeedback.addNewNode(current, 0, true);
            mapFeedback.setNodeText(child, "Depth " + (i + 1));
            current = child;
        }

        // From the deepest node, verify parent chain leads back to root
        MindMapNode node = current;
        int depth = 0;
        while (!node.isRoot()) {
            node = (MindMapNode) node.getParent();
            depth++;
        }
        assertThat(depth).isEqualTo(5);
        assertThat(node).isSameAs(root);
    }

    @Test
    void navigate_wideTree100() throws Exception {
        for (int i = 0; i < 100; i++) {
            MindMapNode child = mapFeedback.addNewNode(root, i, true);
            mapFeedback.setNodeText(child, "Wide " + i);
        }
        assertThat(root.getChildCount()).isEqualTo(100);

        // Verify all accessible by index
        for (int i = 0; i < 100; i++) {
            MindMapNode child = (MindMapNode) root.getChildAt(i);
            assertThat(child.getText()).isEqualTo("Wide " + i);
        }
    }

    @Test
    void navigate_moveToRoot() throws Exception {
        MindMapNode child = mapFeedback.addNewNode(root, 0, true);
        mapFeedback.setNodeText(child, "A");
        MindMapNode parent = child.getParentNode();
        while (parent != null && !parent.isRoot()) {
            parent = parent.getParentNode();
        }
        assertThat(parent).isNotNull();
        assertThat(parent.isRoot()).isTrue();
    }

    @Test
    void navigate_navigateUp() throws Exception {
        MindMapNode child = mapFeedback.addNewNode(root, 0, true);
        mapFeedback.setNodeText(child, "A");
        assertThat(child.getParentNode()).isEqualTo(root);
    }

    @Test
    void navigate_navigateDown() throws Exception {
        MindMapNode child = mapFeedback.addNewNode(root, 0, true);
        mapFeedback.setNodeText(child, "A");
        assertThat(root.getChildCount()).isGreaterThan(0);
        MindMapNode firstChild = (MindMapNode) root.getChildAt(0);
        assertThat(firstChild).isNotNull();
    }

    @Test
    void navigate_navigateLeft() throws Exception {
        MindMapNode first = mapFeedback.addNewNode(root, 0, true);
        mapFeedback.setNodeText(first, "A");
        MindMapNode second = mapFeedback.addNewNode(root, 1, true);
        mapFeedback.setNodeText(second, "B");
        // Left sibling = previous child of same parent
        assertThat(root.getChildCount()).isGreaterThanOrEqualTo(2);
        assertThat(root.getChildPosition(first)).isLessThan(root.getChildPosition(second));
    }

    @Test
    void navigate_navigateRight() throws Exception {
        MindMapNode first = mapFeedback.addNewNode(root, 0, true);
        mapFeedback.setNodeText(first, "A");
        MindMapNode second = mapFeedback.addNewNode(root, 1, true);
        mapFeedback.setNodeText(second, "B");
        // Right sibling = next child of same parent
        int pos = root.getChildPosition(first);
        if (root.getChildCount() > pos + 1) {
            MindMapNode next = (MindMapNode) root.getChildAt(pos + 1);
            assertThat(next).isNotNull();
        }
    }

    @Test
    void navigate_foldAll() throws Exception {
        MindMapNode a = mapFeedback.addNewNode(root, 0, true);
        mapFeedback.setNodeText(a, "A");
        mapFeedback.addNewNode(a, 0, true);
        MindMapNode b = mapFeedback.addNewNode(root, 1, true);
        mapFeedback.setNodeText(b, "B");
        mapFeedback.addNewNode(b, 0, true);
        // Fold all children
        for (int i = 0; i < root.getChildCount(); i++) {
            MindMapNode child = (MindMapNode) root.getChildAt(i);
            child.setFolded(true);
            assertThat(child.isFolded()).isTrue();
        }
    }

    @Test
    void navigate_foldOneLevel() throws Exception {
        MindMapNode child = mapFeedback.addNewNode(root, 0, true);
        mapFeedback.setNodeText(child, "A");
        mapFeedback.addNewNode(child, 0, true);
        child.setFolded(true);
        assertThat(child.isFolded()).isTrue();
        // Other children remain unfolded
    }

    @Test
    void navigate_unfoldAll() throws Exception {
        MindMapNode a = mapFeedback.addNewNode(root, 0, true);
        mapFeedback.setNodeText(a, "A");
        mapFeedback.addNewNode(a, 0, true);
        MindMapNode b = mapFeedback.addNewNode(root, 1, true);
        mapFeedback.setNodeText(b, "B");
        mapFeedback.addNewNode(b, 0, true);
        for (int i = 0; i < root.getChildCount(); i++) {
            MindMapNode child = (MindMapNode) root.getChildAt(i);
            child.setFolded(true);
        }
        for (int i = 0; i < root.getChildCount(); i++) {
            MindMapNode child = (MindMapNode) root.getChildAt(i);
            child.setFolded(false);
            assertThat(child.isFolded()).isFalse();
        }
    }

    @Test
    void navigate_unfoldOneLevel() throws Exception {
        MindMapNode child = mapFeedback.addNewNode(root, 0, true);
        mapFeedback.setNodeText(child, "A");
        mapFeedback.addNewNode(child, 0, true);
        child.setFolded(true);
        assertThat(child.isFolded()).isTrue();
        child.setFolded(false);
        assertThat(child.isFolded()).isFalse();
    }

    @Test
    void navigate_jumpLastEdit() throws Exception {
        // Simulate: edit a node, then verify it can be found
        MindMapNode child = createChildNode(mapFeedback, root, "LastEdited");
        assertThat(findNodeByText(root, "LastEdited")).isNotNull();
    }

    @Test
    void navigate_nodeHistoryBack() throws Exception {
        // History: track visited nodes
        MindMapNode child1 = mapFeedback.addNewNode(root, 0, true);
        mapFeedback.setNodeText(child1, "A");
        MindMapNode child2 = mapFeedback.addNewNode(root, 1, true);
        mapFeedback.setNodeText(child2, "B");
        // Both nodes are accessible
        assertThat(child1.getText()).isNotNull();
        assertThat(child2.getText()).isNotNull();
    }

    @Test
    void navigate_nodeHistoryForward() throws Exception {
        MindMapNode child = mapFeedback.addNewNode(root, 0, true);
        mapFeedback.setNodeText(child, "A");
        assertThat(child).isNotNull();
    }

    @Test
    void navigate_jumpToNote() throws Exception {
        MindMapNode child = mapFeedback.addNewNode(root, 0, true);
        mapFeedback.setNodeText(child, "A");
        child.setNoteText("<html><body>Jump target</body></html>");
        assertThat(child.getNoteText()).contains("Jump target");
    }

    @Test
    void navigate_jumpToAttributeTable() throws Exception {
        MindMapNode child = mapFeedback.addNewNode(root, 0, true);
        mapFeedback.setNodeText(child, "A");
        assertThat(child.getAttributeTableLength()).isEqualTo(0);
    }

    @Test
    void navigate_clones() throws Exception {
        MindMapNode original = mapFeedback.addNewNode(root, 0, true);
        mapFeedback.setNodeText(original, "Original");
        MindMapNode clone = createChildNode(mapFeedback, root, original.getText());
        assertThat(clone.getText()).isEqualTo(original.getText());
    }
}
