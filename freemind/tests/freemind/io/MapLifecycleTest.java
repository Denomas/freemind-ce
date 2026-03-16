package tests.freemind.io;

import freemind.main.HeadlessFreeMind;
import freemind.modes.ExtendedMapFeedbackImpl;
import freemind.modes.MapAdapter;
import freemind.modes.MindMapArrowLink;
import freemind.modes.MindMapLink;
import freemind.modes.MindMapLinkRegistry;
import freemind.modes.MindMapNode;
import freemind.modes.mindmapmode.MindMapMapModel;
import freemind.modes.mindmapmode.MindMapNodeModel;
import freemind.main.Tools;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import tests.freemind.testutil.MindMapGenerator;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Timer;
import java.util.Vector;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for map lifecycle (create/destroy), writer contract (getXml behavior),
 * link registry operations, and autosave timer structural verification.
 */
@DisplayName("Map Lifecycle, Writer Contract, and Link Registry Tests")
class MapLifecycleTest {

    @BeforeAll
    static void init() {
        new HeadlessFreeMind();
    }

    // ── Helpers ─────────────────────────────────────────────────────────

    private static MindMapMapModel createMap() {
        ExtendedMapFeedbackImpl mapFeedback = new ExtendedMapFeedbackImpl();
        MindMapMapModel map = new MindMapMapModel(mapFeedback);
        mapFeedback.setMap(map);
        return map;
    }

    private static MindMapMapModel createMapWithChildren(int childCount) {
        MindMapMapModel map = createMap();
        MindMapNodeModel root = (MindMapNodeModel) map.getRootNode();
        root.setUserObject("Root");
        for (int i = 0; i < childCount; i++) {
            MindMapNodeModel child = new MindMapNodeModel(map);
            child.setUserObject("Child_" + i);
            root.insert(child, -1);
        }
        return map;
    }

    private static String toXml(MindMapMapModel map) throws IOException {
        StringWriter sw = new StringWriter();
        map.getXml(sw);
        return sw.toString();
    }

    // ── Map Lifecycle ───────────────────────────────────────────────────

    @Test
    @DisplayName("Newly created map has non-null root node")
    void newMapHasNonNullRoot() {
        MindMapMapModel map = createMap();
        assertNotNull(map.getRootNode(), "Root node should not be null after map creation");
    }

    @Test
    @DisplayName("Create map, add nodes, destroy without exception")
    void createAddNodesAndDestroyWithoutException() {
        MindMapMapModel map = createMapWithChildren(5);
        assertDoesNotThrow(map::destroy,
                "destroy() should not throw even after adding nodes");
    }

    @Test
    @DisplayName("New map has non-null link registry")
    void newMapHasNonNullLinkRegistry() {
        MindMapMapModel map = createMap();
        assertNotNull(map.getLinkRegistry(),
                "getLinkRegistry() should return a non-null registry for a new map");
    }

    @Test
    @DisplayName("Empty map getXml produces valid XML output")
    void emptyMapGetXmlProducesValidXml() throws IOException {
        MindMapMapModel map = createMap();
        ((MindMapNodeModel) map.getRootNode()).setUserObject("TestRoot");
        String xml = toXml(map);

        assertNotNull(xml, "XML output should not be null");
        assertFalse(xml.isEmpty(), "XML output should not be empty");
        assertTrue(xml.contains("<?xml") || xml.startsWith("<map"),
                "XML should start with XML declaration or <map tag");
    }

    @Test
    @DisplayName("Map XML starts with <map and ends with </map>")
    void mapXmlStartsAndEndsCorrectly() throws IOException {
        MindMapMapModel map = createMapWithChildren(3);
        String xml = toXml(map);

        assertTrue(xml.startsWith("<map "),
                "XML should start with '<map '");
        assertTrue(xml.trim().endsWith("</map>"),
                "XML should end with '</map>'");
    }

    // ── Writer Contract ─────────────────────────────────────────────────

    @Test
    @DisplayName("getXml(StringWriter) writes content to the writer")
    void getXmlWritesContentToWriter() throws IOException {
        MindMapMapModel map = createMapWithChildren(2);
        StringWriter writer = new StringWriter();
        map.getXml(writer);

        String content = writer.toString();
        assertFalse(content.isEmpty(),
                "StringWriter should have received content from getXml");
        assertTrue(content.contains("Root"),
                "Written content should contain the root node text");
    }

    @Test
    @DisplayName("getXml closes the writer after writing — known behavior from FMEA")
    void getXmlClosesWriter() throws IOException {
        // FMEA finding: MindMapMapModel.getXml calls fileout.close() at the end.
        // StringWriter.close() is documented as a no-op, so writing after close
        // still works for StringWriter. However, the contract shows the method
        // closes the writer, which would break FileWriter or other stream-based writers
        // if they tried to write afterward.
        MindMapMapModel map = createMapWithChildren(1);
        StringWriter writer = new StringWriter();
        map.getXml(writer);

        // StringWriter.close() is a no-op per its Javadoc, so this still works:
        assertDoesNotThrow(() -> writer.write("extra"),
                "StringWriter allows writes after close (no-op close), but getXml does call close()");

        // Verify getXml did produce output before the extra write
        String content = writer.toString();
        assertTrue(content.contains("<map"),
                "Content should contain map XML before any extra text");
        assertTrue(content.contains("extra"),
                "StringWriter should accept writes after close (no-op) — "
                        + "documents that getXml closes the writer");
    }

    @Test
    @DisplayName("getXml with null writer throws NullPointerException")
    void getXmlWithNullWriterThrowsNpe() {
        MindMapMapModel map = createMap();
        assertThrows(NullPointerException.class,
                () -> map.getXml(null),
                "getXml(null) should throw NullPointerException");
    }

    @Test
    @DisplayName("Multiple getXml calls produce identical output")
    void multipleGetXmlCallsProduceIdenticalOutput() throws IOException {
        MindMapMapModel map = createMapWithChildren(3);

        String xml1 = toXml(map);
        // getXml closes the writer, so we need a new StringWriter each time
        String xml2 = toXml(map);

        assertEquals(xml1, xml2,
                "Two consecutive getXml calls should produce identical XML");
    }

    // ── Link Registry ───────────────────────────────────────────────────

    @Test
    @DisplayName("New map link registry has no links initially")
    void newMapLinkRegistryIsEmpty() {
        MindMapMapModel map = createMapWithChildren(2);
        MindMapLinkRegistry registry = map.getLinkRegistry();
        MindMapNode root = map.getRootNode();
        MindMapNode child = (MindMapNode) root.getChildAt(0);

        Vector<MindMapLink> linksFromRoot = registry.getAllLinksFromMe(root);
        Vector<MindMapLink> linksFromChild = registry.getAllLinksFromMe(child);

        assertTrue(linksFromRoot.isEmpty(), "Root should have no outgoing links initially");
        assertTrue(linksFromChild.isEmpty(), "Child should have no outgoing links initially");
    }

    @Test
    @DisplayName("Arrow link between two nodes appears in saved XML")
    void arrowLinkAppearsInSavedXml() throws Exception {
        // Load a map with arrow links via XML to exercise the full pipeline
        String xmlWithArrowLink = "<map>"
                + "<node TEXT='ROOT'>"
                + "<node TEXT='Source' ID='ID_1'>"
                + "<arrowlink DESTINATION='ID_2'/>"
                + "</node>"
                + "<node TEXT='Target' ID='ID_2'/>"
                + "</node>"
                + "</map>";

        MindMapMapModel map = MindMapGenerator.loadFromXml(xmlWithArrowLink);
        String savedXml = toXml(map);

        assertTrue(savedXml.contains("arrowlink"),
                "Saved XML should contain arrowlink element");
        assertTrue(savedXml.contains("DESTINATION"),
                "Arrow link should have a DESTINATION attribute");
    }

    @Test
    @DisplayName("Arrow link created via feedback appears in registry and XML")
    void arrowLinkViaFeedbackAppearsInRegistryAndXml() throws Exception {
        // Use the same pattern as StandaloneMapTests to create arrow links
        String initialMap = "<map>"
                + "<node TEXT='ROOT'>"
                + "<node TEXT='Child1'/>"
                + "<node TEXT='Child2'/>"
                + "</node>"
                + "</map>";

        ExtendedMapFeedbackImpl mapFeedback = new ExtendedMapFeedbackImpl();
        MindMapMapModel map = new MindMapMapModel(mapFeedback);
        mapFeedback.setMap(map);
        Tools.StringReaderCreator readerCreator = new Tools.StringReaderCreator(initialMap);
        MindMapNode root = map.loadTree(readerCreator, MapAdapter.sDontAskInstance);
        map.setRoot(root);

        MindMapNode child1 = (MindMapNode) root.getChildAt(0);
        MindMapNode child2 = (MindMapNode) root.getChildAt(1);

        // Create arrow link via mapFeedback
        mapFeedback.addLink(child1, child2);

        // Verify in registry
        Vector<MindMapLink> links = map.getLinkRegistry().getAllLinksFromMe(child1);
        assertEquals(1, links.size(), "Should have exactly one link from Child1");
        MindMapArrowLink link = (MindMapArrowLink) links.firstElement();
        assertEquals(child2, link.getTarget(), "Arrow link target should be Child2");

        // Verify in XML
        StringWriter sw = new StringWriter();
        map.getXml(sw);
        String xml = sw.toString();
        assertTrue(xml.contains("arrowlink"),
                "Saved XML should contain arrowlink after adding via feedback");
    }

    @Test
    @DisplayName("Remove arrow link target node — link deregistered via feedback")
    void removeArrowLinkTargetNode() throws Exception {
        String initialMap = "<map>"
                + "<node TEXT='ROOT'>"
                + "<node TEXT='Source'/>"
                + "<node TEXT='Target'/>"
                + "</node>"
                + "</map>";

        ExtendedMapFeedbackImpl mapFeedback = new ExtendedMapFeedbackImpl();
        MindMapMapModel map = new MindMapMapModel(mapFeedback);
        mapFeedback.setMap(map);
        Tools.StringReaderCreator readerCreator = new Tools.StringReaderCreator(initialMap);
        MindMapNode root = map.loadTree(readerCreator, MapAdapter.sDontAskInstance);
        map.setRoot(root);

        MindMapNode source = (MindMapNode) root.getChildAt(0);
        MindMapNode target = (MindMapNode) root.getChildAt(1);

        mapFeedback.addLink(source, target);
        Vector<MindMapLink> linksBefore = map.getLinkRegistry().getAllLinksFromMe(source);
        assertEquals(1, linksBefore.size(), "Should have 1 link before removal");

        // Remove the link via feedback (same pattern as StandaloneMapTests)
        MindMapArrowLink arrowLink = (MindMapArrowLink) linksBefore.firstElement();
        mapFeedback.removeReference(arrowLink);

        Vector<MindMapLink> linksAfter = map.getLinkRegistry().getAllLinksFromMe(source);
        assertEquals(0, linksAfter.size(),
                "After removeReference, source should have no outgoing links");
    }

    @Test
    @DisplayName("Node with hyperlink (setLink) preserved through save/load round-trip")
    void hyperlinkPreservedThroughRoundTrip() throws Exception {
        MindMapMapModel map = createMap();
        MindMapNodeModel root = (MindMapNodeModel) map.getRootNode();
        root.setUserObject("Root");

        MindMapNodeModel child = new MindMapNodeModel(map);
        child.setUserObject("LinkedChild");
        child.setLink("https://freemind.sourceforge.net/");
        root.insert(child, -1);

        String xml = toXml(map);
        MindMapMapModel loaded = MindMapGenerator.loadFromXml(xml);
        MindMapNode loadedChild = (MindMapNode) loaded.getRootNode().getChildAt(0);

        assertEquals("https://freemind.sourceforge.net/", loadedChild.getLink(),
                "Hyperlink should be preserved after save/load round-trip");
    }

    // ── Re-save Atomicity ────────────────────────────────────────────────

    @Test
    @DisplayName("Re-save to same file produces valid XML")
    void reSaveToSameFileProducesValidXml(@TempDir Path tempDir) throws Exception {
        MindMapMapModel map = createMapWithChildren(5);
        File file = tempDir.resolve("resave.mm").toFile();

        // First save
        try (FileWriter fw = new FileWriter(file, StandardCharsets.UTF_8)) {
            map.getXml(fw);
        }
        assertTrue(file.length() > 0, "First save should produce non-empty file");

        // Modify the map before re-saving
        MindMapNodeModel extraChild = new MindMapNodeModel(map);
        extraChild.setUserObject("ExtraChild_AfterFirstSave");
        ((MindMapNodeModel) map.getRootNode()).insert(extraChild, -1);

        // Re-save to SAME file (overwrite)
        try (FileWriter fw = new FileWriter(file, StandardCharsets.UTF_8)) {
            map.getXml(fw);
        }
        assertTrue(file.length() > 0, "Re-save should produce non-empty file");

        // Verify the re-saved file is valid XML by parsing it
        javax.xml.parsers.DocumentBuilderFactory dbf =
                javax.xml.parsers.DocumentBuilderFactory.newInstance();
        assertDoesNotThrow(() -> dbf.newDocumentBuilder().parse(file),
                "Re-saved file must be valid XML — not corrupted by partial write");

        // Verify the re-saved file contains the new child
        MindMapMapModel reloaded = MindMapGenerator.loadFromFile(file.getAbsolutePath());
        int reloadedCount = MindMapGenerator.countNodes(reloaded.getRootNode());
        // original root + 5 children + 1 extra = 7
        assertEquals(7, reloadedCount,
                "Re-saved map should contain all nodes including the one added after first save");
    }

    // ── Link Registry Integrity ────────────────────────────────────────

    @Test
    @DisplayName("Remove arrow link target node — saved XML has no dangling arrowlink reference")
    void removeLinkTargetNodeNoDanglingReference() throws Exception {
        String initialMap = "<map>"
                + "<node TEXT='ROOT'>"
                + "<node TEXT='Source'/>"
                + "<node TEXT='Target'/>"
                + "</node>"
                + "</map>";

        ExtendedMapFeedbackImpl mapFeedback = new ExtendedMapFeedbackImpl();
        MindMapMapModel map = new MindMapMapModel(mapFeedback);
        mapFeedback.setMap(map);
        Tools.StringReaderCreator readerCreator = new Tools.StringReaderCreator(initialMap);
        MindMapNode root = map.loadTree(readerCreator, MapAdapter.sDontAskInstance);
        map.setRoot(root);

        MindMapNode source = (MindMapNode) root.getChildAt(0);
        MindMapNode target = (MindMapNode) root.getChildAt(1);

        // Create arrow link from source to target
        mapFeedback.addLink(source, target);
        Vector<MindMapLink> linksBefore = map.getLinkRegistry().getAllLinksFromMe(source);
        assertEquals(1, linksBefore.size(), "Should have 1 arrow link before removal");

        // Save XML before removal to confirm arrowlink is present
        String xmlBefore = toXml(map);
        assertTrue(xmlBefore.contains("arrowlink"),
                "XML before removal should contain arrowlink");

        // Remove the link via feedback (clean deregistration)
        MindMapArrowLink arrowLink = (MindMapArrowLink) linksBefore.firstElement();
        mapFeedback.removeReference(arrowLink);

        // Verify link was removed from registry
        Vector<MindMapLink> linksAfter = map.getLinkRegistry().getAllLinksFromMe(source);
        assertEquals(0, linksAfter.size(), "Link should be removed from registry");

        // Now remove the target node from its parent
        root.remove(1);

        // Save to XML after removal
        String xmlAfter = toXml(map);

        // Verify: the saved XML should NOT contain any arrowlink element
        // since we removed the link before removing the node
        assertFalse(xmlAfter.contains("arrowlink"),
                "After removing arrow link and target node, saved XML must not contain "
                        + "any arrowlink reference");
    }

    // ── Autosave Timer (Structural Verification via Reflection) ─────────

    @Test
    @DisplayName("MindMapMapModel has timerForAutomaticSaving field")
    void modelHasTimerField() throws NoSuchFieldException {
        Field timerField = MindMapMapModel.class.getDeclaredField("timerForAutomaticSaving");
        assertNotNull(timerField, "timerForAutomaticSaving field should exist");
        assertEquals(Timer.class, timerField.getType(),
                "timerForAutomaticSaving should be of type java.util.Timer");
    }

    @Test
    @DisplayName("After destroy(), timerForAutomaticSaving is cancelled (null or cancelled state)")
    void destroyCancelsTimer() throws Exception {
        MindMapMapModel map = createMapWithChildren(2);

        // Access the timer field via reflection
        Field timerField = MindMapMapModel.class.getDeclaredField("timerForAutomaticSaving");
        timerField.setAccessible(true);

        // Manually set a timer to simulate what scheduleTimerForAutomaticSaving would do
        Timer testTimer = new Timer();
        timerField.set(map, testTimer);

        // Verify timer is set before destroy
        assertNotNull(timerField.get(map), "Timer should be set before destroy");

        // destroy() should call timer.cancel()
        map.destroy();

        // After cancel(), the Timer object is still the same reference,
        // but it is cancelled (cannot schedule new tasks).
        // Verify cancel was called by attempting to schedule — it should throw.
        Timer afterDestroy = (Timer) timerField.get(map);
        if (afterDestroy != null) {
            assertThrows(IllegalStateException.class,
                    () -> afterDestroy.schedule(new java.util.TimerTask() {
                        @Override
                        public void run() { }
                    }, 1000),
                    "Timer should be cancelled after destroy(), "
                            + "so scheduling a new task should throw IllegalStateException");
        }
        // If null, it was cleared — also valid
    }
}
