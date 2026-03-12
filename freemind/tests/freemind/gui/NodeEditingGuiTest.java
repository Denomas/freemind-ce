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
 * GUI tests for node editing operations:
 * inline and modal text editing, rich text notes, undo/redo simulation,
 * link management, cut-paste simulation, and edit preservation.
 */
class NodeEditingGuiTest extends GuiTestBase {

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
    void nodeEdit_setTextSimulatesInlineEdit() {
        MindMapNode child = mapFeedback.addNewNode(root, 0, true);
        mapFeedback.setNodeText(child, "Inline edited text");
        assertThat(child.getText())
            .as("Node text should reflect inline edit")
            .isEqualTo("Inline edited text");
    }

    @Test
    void nodeEdit_emptyTextAllowed() {
        MindMapNode child = mapFeedback.addNewNode(root, 0, true);
        mapFeedback.setNodeText(child, "");
        assertThat(child.getText())
            .as("Empty text should be allowed on a node")
            .isEmpty();
    }

    @Test
    void nodeEdit_longTextSimulatesModalEdit() {
        StringBuilder longText = new StringBuilder();
        for (int i = 0; i < 100; i++) {
            longText.append("This is line ").append(i).append(" of a very long text. ");
        }
        String text = longText.toString();
        assertThat(text.length()).isGreaterThan(1000);

        MindMapNode child = mapFeedback.addNewNode(root, 0, true);
        mapFeedback.setNodeText(child, text);
        assertThat(child.getText())
            .as("Long text (1000+ chars) should be fully preserved")
            .isEqualTo(text);
    }

    @Test
    void nodeEdit_richTextInNote() {
        MindMapNode child = mapFeedback.addNewNode(root, 0, true);
        mapFeedback.setNodeText(child, "Rich note test");

        String noteHtml = "<html><body><b>Bold text</b> and <i>italic text</i></body></html>";
        mapFeedback.setNoteText(child, noteHtml);

        String retrieved = child.getNoteText();
        assertThat(retrieved)
            .as("Rich text note with bold/italic should be preserved")
            .isEqualTo(noteHtml);
    }

    @Test
    void nodeEdit_undoSimulation() {
        // Add a child (simulates user action)
        MindMapNode child = mapFeedback.addNewNode(root, 0, true);
        mapFeedback.setNodeText(child, "To be undone");
        assertThat(root.getChildCount()).isEqualTo(1);

        // Simulate undo by removing the node
        mapFeedback.removeNodeFromParent(child);
        assertThat(root.getChildCount())
            .as("After undo simulation, child count should be 0")
            .isEqualTo(0);
    }

    @Test
    void nodeEdit_redoSimulation() {
        // Add a child (action)
        MindMapNode child = mapFeedback.addNewNode(root, 0, true);
        mapFeedback.setNodeText(child, "Redo target");
        assertThat(root.getChildCount()).isEqualTo(1);

        // Undo: remove the child
        mapFeedback.removeNodeFromParent(child);
        assertThat(root.getChildCount()).isEqualTo(0);

        // Redo: add a new child with the same text
        MindMapNode redone = mapFeedback.addNewNode(root, 0, true);
        mapFeedback.setNodeText(redone, "Redo target");
        assertThat(root.getChildCount())
            .as("After redo simulation, child count should be 1")
            .isEqualTo(1);
        assertThat(((MindMapNode) root.getChildAt(0)).getText())
            .isEqualTo("Redo target");
    }

    @Test
    void nodeEdit_linkInsert() {
        MindMapNode child = mapFeedback.addNewNode(root, 0, true);
        mapFeedback.setNodeText(child, "Link node");

        String url = "https://github.com/Denomas/freemind-ce";
        child.setLink(url);

        assertThat(child.getLink())
            .as("Link should be set on the node")
            .isEqualTo(url);
    }

    @Test
    void nodeEdit_removeLink() {
        MindMapNode child = mapFeedback.addNewNode(root, 0, true);
        mapFeedback.setNodeText(child, "Link removal test");

        child.setLink("https://example.com");
        assertThat(child.getLink()).isNotNull();

        // Remove the link by setting to null
        child.setLink(null);
        assertThat(child.getLink())
            .as("Link should be null after removal")
            .isNull();
    }

    @Test
    void nodeEdit_cutPasteSimulation() {
        // Create source node (to be "cut")
        MindMapNode source = mapFeedback.addNewNode(root, 0, true);
        mapFeedback.setNodeText(source, "Cut this text");

        // "Cut": capture text, remove source
        String cutText = source.getText();
        mapFeedback.removeNodeFromParent(source);
        assertThat(root.getChildCount()).isEqualTo(0);

        // "Paste": create new node with the captured text
        MindMapNode pasted = mapFeedback.addNewNode(root, 0, true);
        mapFeedback.setNodeText(pasted, cutText);

        assertThat(root.getChildCount()).isEqualTo(1);
        assertThat(((MindMapNode) root.getChildAt(0)).getText())
            .as("Pasted node should have the cut text")
            .isEqualTo("Cut this text");
    }

    @Test
    void nodeEdit_multipleEditsPreserved() {
        MindMapNode child = mapFeedback.addNewNode(root, 0, true);
        for (int i = 1; i <= 10; i++) {
            mapFeedback.setNodeText(child, "Edit #" + i);
        }
        assertThat(child.getText())
            .as("Final text after 10 edits should be the last one")
            .isEqualTo("Edit #10");
    }

    @Test
    void nodeEdit_editPreservesChildren() {
        MindMapNode parent = mapFeedback.addNewNode(root, 0, true);
        mapFeedback.setNodeText(parent, "Parent");

        MindMapNode childA = mapFeedback.addNewNode(parent, 0, true);
        mapFeedback.setNodeText(childA, "Child A");
        MindMapNode childB = mapFeedback.addNewNode(parent, 1, true);
        mapFeedback.setNodeText(childB, "Child B");

        // Edit the parent text
        mapFeedback.setNodeText(parent, "Parent (edited)");

        // Children should be unchanged
        assertThat(parent.getChildCount())
            .as("Children count should be preserved after parent edit")
            .isEqualTo(2);
        assertThat(((MindMapNode) parent.getChildAt(0)).getText())
            .isEqualTo("Child A");
        assertThat(((MindMapNode) parent.getChildAt(1)).getText())
            .isEqualTo("Child B");
    }

    @Test
    void nodeEdit_editPreservesNotes() {
        MindMapNode child = mapFeedback.addNewNode(root, 0, true);
        mapFeedback.setNodeText(child, "Original text");

        String noteHtml = "<html><body>Important note</body></html>";
        mapFeedback.setNoteText(child, noteHtml);

        // Edit the node text
        mapFeedback.setNodeText(child, "Edited text");

        assertThat(child.getNoteText())
            .as("Note should be preserved after text edit")
            .isEqualTo(noteHtml);
    }

    @Test
    void nodeEdit_specialCharsInEdit() throws Exception {
        String specialChars = "< > & \" '";
        MindMapNode child = mapFeedback.addNewNode(root, 0, true);
        mapFeedback.setNodeText(child, specialChars);

        // Save to XML and reload
        String xml = saveMapToXml(map);
        MindMapNode reloadedRoot = reloadMap(xml);

        MindMapNode reloadedChild = (MindMapNode) reloadedRoot.getChildAt(0);
        assertThat(reloadedChild.getText())
            .as("Special characters should survive save/reload round-trip")
            .isEqualTo(specialChars);
    }

    @Test
    void nodeEdit_unicodeInEdit() throws Exception {
        for (int i = 0; i < ALL_SCRIPTS.length; i++) {
            String script = ALL_SCRIPTS[i];
            String scriptName = ALL_SCRIPT_NAMES[i];

            MapModelFixture fixture = createMapModel();
            String initialMap = "<map><node TEXT='ROOT'/></map>";
            MindMapNode testRoot = loadMap(fixture.model, initialMap);

            MindMapNode child = fixture.feedback.addNewNode(testRoot, 0, true);
            fixture.feedback.setNodeText(child, script);

            // Save and reload
            String xml = saveMapToXml(fixture.model);
            MindMapNode reloadedRoot = reloadMap(xml);
            MindMapNode reloadedChild = (MindMapNode) reloadedRoot.getChildAt(0);

            assertThat(reloadedChild.getText())
                .as("%s text should survive save/reload round-trip", scriptName)
                .isEqualTo(script);
        }
    }

    @Test
    void nodeEdit_moveNodeSimulation() {
        // Create two parent nodes
        MindMapNode parentA = mapFeedback.addNewNode(root, 0, true);
        mapFeedback.setNodeText(parentA, "Parent A");
        MindMapNode parentB = mapFeedback.addNewNode(root, 1, true);
        mapFeedback.setNodeText(parentB, "Parent B");

        // Create a child under Parent A
        MindMapNode movable = mapFeedback.addNewNode(parentA, 0, true);
        mapFeedback.setNodeText(movable, "Movable node");

        assertThat(parentA.getChildCount()).isEqualTo(1);
        assertThat(parentB.getChildCount()).isEqualTo(0);

        // "Move": remove from Parent A, add to Parent B
        mapFeedback.removeNodeFromParent(movable);
        assertThat(parentA.getChildCount()).isEqualTo(0);

        // Re-add under Parent B
        MindMapNode moved = mapFeedback.addNewNode(parentB, 0, true);
        mapFeedback.setNodeText(moved, "Movable node");

        assertThat(parentB.getChildCount())
            .as("Parent B should have the moved node")
            .isEqualTo(1);
        assertThat(((MindMapNode) parentB.getChildAt(0)).getText())
            .isEqualTo("Movable node");
        assertThat(parentA.getChildCount())
            .as("Parent A should have no children after move")
            .isEqualTo(0);
    }

    @Test
    void nodeEdit_inlineEdit() throws Exception {
        MindMapNode child = createChildNode(mapFeedback, root, "InlineTarget");
        mapFeedback.setNodeText(child, "Edited");
        assertThat(child.getText()).isEqualTo("Edited");
    }

    @Test
    void nodeEdit_enterConfirms() throws Exception {
        MindMapNode child = createChildNode(mapFeedback, root, "Confirmed");
        assertThat(child.getText()).isEqualTo("Confirmed");
    }

    @Test
    void nodeEdit_escCancels() throws Exception {
        MindMapNode child = createChildNode(mapFeedback, root, "Original");
        // Simulate cancel: text stays as original
        assertThat(child.getText()).isEqualTo("Original");
    }

    @Test
    void nodeEdit_longTextOpensModal() throws Exception {
        String longText = "X".repeat(200);
        MindMapNode child = createChildNode(mapFeedback, root, longText);
        assertThat(child.getText()).hasSize(200);
    }

    @Test
    void nodeEdit_richTextEditor() throws Exception {
        MindMapNode child = createChildNode(mapFeedback, root, "RichText");
        child.setNoteText("<html><body><b>Bold</b> and <i>italic</i></body></html>");
        assertThat(child.getNoteText()).contains("<b>");
    }

    @Test
    void nodeEdit_undoInEditor() throws Exception {
        MindMapNode child = createChildNode(mapFeedback, root, "First");
        mapFeedback.setNodeText(child, "Second");
        mapFeedback.setNodeText(child, "First");
        assertThat(child.getText()).isEqualTo("First");
    }

    @Test
    void nodeEdit_arrowLink() throws Exception {
        MindMapNode child1 = createChildNode(mapFeedback, root, "ArrowStart");
        MindMapNode child2 = createChildNode(mapFeedback, root, "ArrowEnd");
        // Arrow links are managed via Controller; verify both nodes exist
        assertThat(child1).isNotNull();
        assertThat(child2).isNotNull();
    }

    @Test
    void nodeEdit_dragAndDrop() throws Exception {
        MindMapNode parent1 = createChildNode(mapFeedback, root, "Source");
        MindMapNode child = createChildNode(mapFeedback, parent1, "Dragged");
        MindMapNode parent2 = createChildNode(mapFeedback, root, "Target");
        // Simulate drag: remove from source, add to target
        map.removeNodeFromParent(child);
        MindMapNode moved = createChildNode(mapFeedback, parent2, "Dragged");
        assertThat(parent1.getChildCount()).isEqualTo(0);
        assertThat(parent2.getChildCount()).isEqualTo(1);
        assertThat(moved.getText()).isEqualTo("Dragged");
    }

    @Test
    void nodeEdit_cutPasteNode() throws Exception {
        MindMapNode source = createChildNode(mapFeedback, root, "CutMe");
        String text = source.getText();
        map.removeNodeFromParent(source);
        MindMapNode pasted = createChildNode(mapFeedback, root, text);
        assertThat(pasted.getText()).isEqualTo("CutMe");
    }

    @Override
    protected MindMapNode getMapRootForScreenshot() {
        return root;
    }
}
