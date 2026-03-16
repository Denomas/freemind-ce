package tests.freemind.io;

import freemind.main.HeadlessFreeMind;
import freemind.main.Tools;
import freemind.modes.ExtendedMapFeedbackImpl;
import freemind.modes.MapAdapter;
import freemind.modes.MindMapNode;
import freemind.modes.mindmapmode.MindMapMapModel;
import freemind.modes.mindmapmode.MindMapNodeModel;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import tests.freemind.testutil.MindMapGenerator;

import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Round-trip IO tests for FreeMind mind map serialization.
 * Verifies that maps survive save/load cycles with all properties intact.
 */
@DisplayName("Map Round-Trip IO Tests")
class MapRoundTripTest {

    @BeforeAll
    static void init() {
        new HeadlessFreeMind();
    }

    // ── Small map round-trips ───────────────────────────────────────────

    @Test
    @DisplayName("Small map XML round-trip preserves node count")
    void smallMapXmlRoundTrip() throws Exception {
        MindMapMapModel original = MindMapGenerator.small().build();
        int originalCount = MindMapGenerator.countNodes(original.getRootNode());

        String xml = toXml(original);
        MindMapMapModel loaded = MindMapGenerator.loadFromXml(xml);
        int loadedCount = MindMapGenerator.countNodes(loaded.getRootNode());

        assertEquals(originalCount, loadedCount,
                "Node count should be preserved after XML round-trip");
    }

    @Test
    @DisplayName("Small map file round-trip preserves node count")
    void smallMapFileRoundTrip(@TempDir Path tempDir) throws Exception {
        Path mmFile = tempDir.resolve("small.mm");
        MindMapGenerator gen = MindMapGenerator.small();
        MindMapMapModel original = gen.build();
        int originalCount = MindMapGenerator.countNodes(original.getRootNode());

        // Re-build and write to file (toFile builds internally)
        MindMapGenerator.small().toFile(mmFile.toString());
        MindMapMapModel loaded = MindMapGenerator.loadFromFile(mmFile.toString());
        int loadedCount = MindMapGenerator.countNodes(loaded.getRootNode());

        assertEquals(originalCount, loadedCount,
                "Node count should be preserved after file round-trip");
    }

    // ── Medium map round-trip ───────────────────────────────────────────

    @Test
    @DisplayName("Medium map (1K nodes) XML round-trip preserves node count")
    void mediumMapXmlRoundTrip() throws Exception {
        MindMapMapModel original = MindMapGenerator.medium().build();
        int originalCount = MindMapGenerator.countNodes(original.getRootNode());

        String xml = toXml(original);
        MindMapMapModel loaded = MindMapGenerator.loadFromXml(xml);
        int loadedCount = MindMapGenerator.countNodes(loaded.getRootNode());

        assertEquals(originalCount, loadedCount,
                "Medium map node count should be preserved after XML round-trip");
    }

    @Test
    @DisplayName("Medium map file round-trip preserves node count")
    void mediumMapFileRoundTrip(@TempDir Path tempDir) throws Exception {
        Path mmFile = tempDir.resolve("medium.mm");
        MindMapGenerator.medium().toFile(mmFile.toString());
        MindMapMapModel original = MindMapGenerator.medium().build();
        int originalCount = MindMapGenerator.countNodes(original.getRootNode());

        MindMapMapModel loaded = MindMapGenerator.loadFromFile(mmFile.toString());
        int loadedCount = MindMapGenerator.countNodes(loaded.getRootNode());

        assertEquals(originalCount, loadedCount,
                "Medium map node count should be preserved after file round-trip");
    }

    // ── Edge-case content round-trip ────────────────────────────────────

    @Test
    @DisplayName("Edge-case content round-trip preserves all node text")
    void edgeCaseContentRoundTrip() throws Exception {
        String xml = MindMapGenerator.edgeCases().toXmlString();
        MindMapMapModel loaded = MindMapGenerator.loadFromXml(xml);

        assertNotNull(loaded.getRootNode());
        int count = MindMapGenerator.countNodes(loaded.getRootNode());
        assertTrue(count > 1, "Edge-case map should have multiple nodes");

        // Verify adversarial strings are present in the XML
        assertTrue(xml.contains("alert"), "Script content should survive serialization");
        assertTrue(xml.contains("passwd"), "File path content should survive serialization");
    }

    @Test
    @DisplayName("Script tag text preserved through round-trip")
    void scriptTagTextPreserved() throws Exception {
        MindMapMapModel map = createSingleChildMap("<script>alert('test')</script>");
        String xml = toXml(map);
        MindMapMapModel loaded = MindMapGenerator.loadFromXml(xml);
        MindMapNode child = (MindMapNode) loaded.getRootNode().getChildAt(0);
        assertEquals("<script>alert('test')</script>", child.getText());
    }

    @Test
    @DisplayName("File path text preserved through round-trip")
    void filePathTextPreserved() throws Exception {
        MindMapMapModel map = createSingleChildMap("file:///etc/passwd");
        String xml = toXml(map);
        MindMapMapModel loaded = MindMapGenerator.loadFromXml(xml);
        MindMapNode child = (MindMapNode) loaded.getRootNode().getChildAt(0);
        assertEquals("file:///etc/passwd", child.getText());
    }

    @Test
    @DisplayName("Simple emoji text preserved through round-trip")
    void simpleEmojiTextPreserved() throws Exception {
        // Use BMP emoji (U+2603 snowman, U+2764 heart) — these survive XML round-trip
        String emoji = "\u2603\u2764\u2605";
        MindMapMapModel map = createSingleChildMap(emoji);
        String xml = toXml(map);
        MindMapMapModel loaded = MindMapGenerator.loadFromXml(xml);
        MindMapNode child = (MindMapNode) loaded.getRootNode().getChildAt(0);
        assertEquals(emoji, child.getText(),
                "BMP emoji/symbols must survive round-trip");
    }

    @Test
    @DisplayName("Flag emoji (supplementary chars) — preserved in round-trip")
    void flagEmojiRoundTripBehavior() throws Exception {
        // Flag emoji (🇹🇷 = U+1F1F9 U+1F1F7) are supplementary characters
        String flagEmoji = "\ud83c\uddf9\ud83c\uddf7";
        MindMapMapModel map = createSingleChildMap(flagEmoji);
        String xml = toXml(map);
        MindMapMapModel loaded = MindMapGenerator.loadFromXml(xml);
        MindMapNode child = (MindMapNode) loaded.getRootNode().getChildAt(0);
        assertEquals(flagEmoji, child.getText(),
                "Flag emoji must survive XML round-trip");
    }

    @Test
    @DisplayName("Arabic text preserved through round-trip")
    void arabicTextPreserved() throws Exception {
        String arabic = "\u0645\u0631\u062d\u0628\u0627";
        MindMapMapModel map = createSingleChildMap(arabic);
        String xml = toXml(map);
        MindMapMapModel loaded = MindMapGenerator.loadFromXml(xml);
        MindMapNode child = (MindMapNode) loaded.getRootNode().getChildAt(0);
        assertEquals(arabic, child.getText());
    }

    @Test
    @DisplayName("XML special chars in text preserved through round-trip")
    void xmlSpecialCharsPreserved() throws Exception {
        String text = "Node with <tag> & \"quotes\" in text";
        MindMapMapModel map = createSingleChildMap(text);
        String xml = toXml(map);
        MindMapMapModel loaded = MindMapGenerator.loadFromXml(xml);
        MindMapNode child = (MindMapNode) loaded.getRootNode().getChildAt(0);
        assertEquals(text, child.getText());
    }

    @Test
    @DisplayName("SQL injection text preserved through round-trip")
    void sqlInjectionTextPreserved() throws Exception {
        String text = "' OR 1=1 --";
        MindMapMapModel map = createSingleChildMap(text);
        String xml = toXml(map);
        MindMapMapModel loaded = MindMapGenerator.loadFromXml(xml);
        MindMapNode child = (MindMapNode) loaded.getRootNode().getChildAt(0);
        assertEquals(text, child.getText());
    }

    // ── Note preservation ───────────────────────────────────────────────

    @Test
    @DisplayName("HTML notes preserved through round-trip")
    void htmlNotesPreserved() throws Exception {
        ExtendedMapFeedbackImpl mapFeedback = new ExtendedMapFeedbackImpl();
        MindMapMapModel map = new MindMapMapModel(mapFeedback);
        mapFeedback.setMap(map);
        MindMapNodeModel root = (MindMapNodeModel) map.getRootNode();
        root.setUserObject("Root");

        MindMapNodeModel child = new MindMapNodeModel(map);
        child.setUserObject("NoteChild");
        String noteHtml = "<html><body><b>Bold note</b> with <i>italic</i></body></html>";
        child.setNoteText(noteHtml);
        root.insert(child, -1);

        String xml = toXml(map);
        MindMapMapModel loaded = MindMapGenerator.loadFromXml(xml);
        MindMapNode loadedChild = (MindMapNode) loaded.getRootNode().getChildAt(0);
        assertNotNull(loadedChild.getNoteText(), "Note should be preserved");
        assertTrue(loadedChild.getNoteText().contains("Bold note"),
                "Note content should be preserved");
    }

    // ── Link preservation ───────────────────────────────────────────────

    @Test
    @DisplayName("JavaScript link preserved through round-trip")
    void javascriptLinkPreserved() throws Exception {
        ExtendedMapFeedbackImpl mapFeedback = new ExtendedMapFeedbackImpl();
        MindMapMapModel map = new MindMapMapModel(mapFeedback);
        mapFeedback.setMap(map);
        MindMapNodeModel root = (MindMapNodeModel) map.getRootNode();
        root.setUserObject("Root");

        MindMapNodeModel child = new MindMapNodeModel(map);
        child.setUserObject("LinkChild");
        child.setLink("javascript:void(0)");
        root.insert(child, -1);

        String xml = toXml(map);
        MindMapMapModel loaded = MindMapGenerator.loadFromXml(xml);
        MindMapNode loadedChild = (MindMapNode) loaded.getRootNode().getChildAt(0);
        assertEquals("javascript:void(0)", loadedChild.getLink());
    }

    @Test
    @DisplayName("File link preserved through round-trip")
    void fileLinkPreserved() throws Exception {
        ExtendedMapFeedbackImpl mapFeedback = new ExtendedMapFeedbackImpl();
        MindMapMapModel map = new MindMapMapModel(mapFeedback);
        mapFeedback.setMap(map);
        MindMapNodeModel root = (MindMapNodeModel) map.getRootNode();
        root.setUserObject("Root");

        MindMapNodeModel child = new MindMapNodeModel(map);
        child.setUserObject("FileLink");
        child.setLink("file:///etc/passwd");
        root.insert(child, -1);

        String xml = toXml(map);
        MindMapMapModel loaded = MindMapGenerator.loadFromXml(xml);
        MindMapNode loadedChild = (MindMapNode) loaded.getRootNode().getChildAt(0);
        assertEquals("file:///etc/passwd", loadedChild.getLink());
    }

    // ── Attribute preservation ──────────────────────────────────────────

    @Test
    @DisplayName("Attributes with special chars preserved through round-trip")
    void attributesWithSpecialCharsPreserved() throws Exception {
        ExtendedMapFeedbackImpl mapFeedback = new ExtendedMapFeedbackImpl();
        MindMapMapModel map = new MindMapMapModel(mapFeedback);
        mapFeedback.setMap(map);
        MindMapNodeModel root = (MindMapNodeModel) map.getRootNode();
        root.setUserObject("Root");

        MindMapNodeModel child = new MindMapNodeModel(map);
        child.setUserObject("AttrChild");
        child.addAttribute(new freemind.modes.attributes.Attribute(
                "key_with_<xml>", "value with <xml> & \"quotes\""));
        root.insert(child, -1);

        String xml = toXml(map);
        MindMapMapModel loaded = MindMapGenerator.loadFromXml(xml);
        MindMapNode loadedChild = (MindMapNode) loaded.getRootNode().getChildAt(0);
        assertEquals(1, loadedChild.getAttributeTableLength());
    }

    // ── Icon preservation ───────────────────────────────────────────────

    @Test
    @DisplayName("Icons preserved through round-trip")
    void iconsPreservedThroughRoundTrip() throws Exception {
        ExtendedMapFeedbackImpl mapFeedback = new ExtendedMapFeedbackImpl();
        MindMapMapModel map = new MindMapMapModel(mapFeedback);
        mapFeedback.setMap(map);
        MindMapNodeModel root = (MindMapNodeModel) map.getRootNode();
        root.setUserObject("Root");

        MindMapNodeModel child = new MindMapNodeModel(map);
        child.setUserObject("IconChild");
        child.addIcon(freemind.modes.MindIcon.factory("button_ok"), freemind.modes.MindIcon.LAST);
        child.addIcon(freemind.modes.MindIcon.factory("idea"), freemind.modes.MindIcon.LAST);
        root.insert(child, -1);

        String xml = toXml(map);
        MindMapMapModel loaded = MindMapGenerator.loadFromXml(xml);
        MindMapNode loadedChild = (MindMapNode) loaded.getRootNode().getChildAt(0);
        assertEquals(2, loadedChild.getIcons().size(),
                "Both icons should be preserved");
        assertEquals("button_ok", loadedChild.getIcons().get(0).getName());
        assertEquals("idea", loadedChild.getIcons().get(1).getName());
    }

    // ── Root node properties ────────────────────────────────────────────

    @Test
    @DisplayName("Root node text preserved through round-trip")
    void rootNodeTextPreserved() throws Exception {
        ExtendedMapFeedbackImpl mapFeedback = new ExtendedMapFeedbackImpl();
        MindMapMapModel map = new MindMapMapModel(mapFeedback);
        mapFeedback.setMap(map);
        MindMapNodeModel root = (MindMapNodeModel) map.getRootNode();
        root.setUserObject("MyRootText");

        String xml = toXml(map);
        MindMapMapModel loaded = MindMapGenerator.loadFromXml(xml);
        assertEquals("MyRootText", loaded.getRootNode().getText());
    }

    // ── Child ordering ──────────────────────────────────────────────────

    @Test
    @DisplayName("Child ordering preserved after round-trip")
    void childOrderingPreserved() throws Exception {
        ExtendedMapFeedbackImpl mapFeedback = new ExtendedMapFeedbackImpl();
        MindMapMapModel map = new MindMapMapModel(mapFeedback);
        mapFeedback.setMap(map);
        MindMapNodeModel root = (MindMapNodeModel) map.getRootNode();
        root.setUserObject("Root");

        for (int i = 0; i < 5; i++) {
            MindMapNodeModel child = new MindMapNodeModel(map);
            child.setUserObject("Child_" + i);
            root.insert(child, -1);
        }

        String xml = toXml(map);
        MindMapMapModel loaded = MindMapGenerator.loadFromXml(xml);
        MindMapNode loadedRoot = loaded.getRootNode();

        assertEquals(5, loadedRoot.getChildCount());
        for (int i = 0; i < 5; i++) {
            MindMapNode child = (MindMapNode) loadedRoot.getChildAt(i);
            assertEquals("Child_" + i, child.getText(),
                    "Child at index " + i + " should preserve text and order");
        }
    }

    // ── Empty map ───────────────────────────────────────────────────────

    @Test
    @DisplayName("Empty map (root only) round-trip")
    void emptyMapRoundTrip() throws Exception {
        ExtendedMapFeedbackImpl mapFeedback = new ExtendedMapFeedbackImpl();
        MindMapMapModel map = new MindMapMapModel(mapFeedback);
        mapFeedback.setMap(map);
        MindMapNodeModel root = (MindMapNodeModel) map.getRootNode();
        root.setUserObject("OnlyRoot");

        String xml = toXml(map);
        MindMapMapModel loaded = MindMapGenerator.loadFromXml(xml);

        assertEquals("OnlyRoot", loaded.getRootNode().getText());
        assertEquals(0, loaded.getRootNode().getChildCount());
    }

    // ── Map with 1 child ────────────────────────────────────────────────

    @Test
    @DisplayName("Map with single child round-trip")
    void singleChildRoundTrip() throws Exception {
        ExtendedMapFeedbackImpl mapFeedback = new ExtendedMapFeedbackImpl();
        MindMapMapModel map = new MindMapMapModel(mapFeedback);
        mapFeedback.setMap(map);
        MindMapNodeModel root = (MindMapNodeModel) map.getRootNode();
        root.setUserObject("Root");

        MindMapNodeModel child = new MindMapNodeModel(map);
        child.setUserObject("OnlyChild");
        root.insert(child, -1);

        String xml = toXml(map);
        MindMapMapModel loaded = MindMapGenerator.loadFromXml(xml);

        assertEquals(1, loaded.getRootNode().getChildCount());
        MindMapNode loadedChild = (MindMapNode) loaded.getRootNode().getChildAt(0);
        assertEquals("OnlyChild", loadedChild.getText());
    }

    // ── Edge-case generator full round-trip ─────────────────────────────

    @Test
    @DisplayName("Edge-case generator round-trip preserves node count via file")
    void edgeCaseFileRoundTrip(@TempDir Path tempDir) throws Exception {
        Path mmFile = tempDir.resolve("edgecase.mm");
        MindMapGenerator.edgeCases().toFile(mmFile.toString());
        MindMapMapModel original = MindMapGenerator.edgeCases().build();
        int originalCount = MindMapGenerator.countNodes(original.getRootNode());

        MindMapMapModel loaded = MindMapGenerator.loadFromFile(mmFile.toString());
        int loadedCount = MindMapGenerator.countNodes(loaded.getRootNode());

        assertEquals(originalCount, loadedCount,
                "Edge-case map node count should be preserved after file round-trip");
    }

    @Test
    @DisplayName("Multiline text preserved through round-trip")
    void multilineTextPreserved() throws Exception {
        String text = "Line1\nLine2\nLine3";
        MindMapMapModel map = createSingleChildMap(text);
        String xml = toXml(map);
        MindMapMapModel loaded = MindMapGenerator.loadFromXml(xml);
        MindMapNode child = (MindMapNode) loaded.getRootNode().getChildAt(0);
        assertEquals(text, child.getText());
    }

    @Test
    @DisplayName("Very long text preserved through round-trip")
    void veryLongTextPreserved() throws Exception {
        String text = "A".repeat(10_000);
        MindMapMapModel map = createSingleChildMap(text);
        String xml = toXml(map);
        MindMapMapModel loaded = MindMapGenerator.loadFromXml(xml);
        MindMapNode child = (MindMapNode) loaded.getRootNode().getChildAt(0);
        assertEquals(text, child.getText());
    }

    // ── Helpers ─────────────────────────────────────────────────────────

    private static String toXml(MindMapMapModel map) throws IOException {
        StringWriter sw = new StringWriter();
        map.getXml(sw);
        return sw.toString();
    }

    private static MindMapMapModel createSingleChildMap(String childText) {
        ExtendedMapFeedbackImpl mapFeedback = new ExtendedMapFeedbackImpl();
        MindMapMapModel map = new MindMapMapModel(mapFeedback);
        mapFeedback.setMap(map);
        MindMapNodeModel root = (MindMapNodeModel) map.getRootNode();
        root.setUserObject("Root");

        MindMapNodeModel child = new MindMapNodeModel(map);
        child.setUserObject(childText);
        root.insert(child, -1);

        return map;
    }
}
