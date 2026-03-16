package tests.freemind;

import freemind.main.FreeMind;
import freemind.modes.MindMapNode;
import freemind.modes.mindmapmode.MindMapMapModel;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import tests.freemind.testutil.MindMapGenerator;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Backward compatibility tests that verify FreeMind can load maps with
 * various XML features. Uses inline XML strings to test each feature
 * independently without external file dependencies.
 */
@DisplayName("Backward Compatibility - Map Loading")
class BackwardCompatTest {

    @Test
    @DisplayName("Minimal map XML loads with correct root text")
    void minimalMapLoads() throws Exception {
        String xml = "<map version=\"1.1.0\"><node TEXT=\"Root\"/></map>";
        MindMapMapModel map = MindMapGenerator.loadFromXml(xml);

        MindMapNode root = map.getRootNode();
        assertNotNull(root, "Root node must not be null");
        assertEquals("Root", root.getText(),
                "Root node text must be 'Root'");
    }

    @Test
    @DisplayName("Map with children loads correct child count")
    void mapWithChildrenLoads() throws Exception {
        String xml = "<map version=\"1.1.0\">"
                + "<node TEXT=\"Root\">"
                + "  <node TEXT=\"Child1\"/>"
                + "  <node TEXT=\"Child2\"/>"
                + "  <node TEXT=\"Child3\"/>"
                + "</node>"
                + "</map>";
        MindMapMapModel map = MindMapGenerator.loadFromXml(xml);

        MindMapNode root = map.getRootNode();
        assertNotNull(root, "Root node must not be null");
        assertEquals(3, root.getChildCount(),
                "Root must have 3 children");
        assertEquals("Child1",
                ((MindMapNode) root.getChildAt(0)).getText());
        assertEquals("Child2",
                ((MindMapNode) root.getChildAt(1)).getText());
        assertEquals("Child3",
                ((MindMapNode) root.getChildAt(2)).getText());
    }

    @Test
    @DisplayName("Map with node attributes (CREATED, MODIFIED, ID) loads")
    void mapWithAttributesLoads() throws Exception {
        String xml = "<map version=\"1.1.0\">"
                + "<node TEXT=\"Root\" CREATED=\"1234567890\" "
                + "MODIFIED=\"1234567891\" ID=\"ID_12345678\">"
                + "</node>"
                + "</map>";
        MindMapMapModel map = MindMapGenerator.loadFromXml(xml);

        MindMapNode root = map.getRootNode();
        assertNotNull(root, "Root node must not be null");
        assertEquals("Root", root.getText());
    }

    @Test
    @DisplayName("Map with edge style loads")
    void mapWithEdgeStyleLoads() throws Exception {
        String xml = "<map version=\"1.1.0\">"
                + "<node TEXT=\"Root\">"
                + "  <node TEXT=\"Child\">"
                + "    <edge STYLE=\"bezier\"/>"
                + "  </node>"
                + "</node>"
                + "</map>";
        MindMapMapModel map = MindMapGenerator.loadFromXml(xml);

        MindMapNode root = map.getRootNode();
        MindMapNode child = (MindMapNode) root.getChildAt(0);
        assertNotNull(child.getEdge(),
                "Child node edge must not be null");
    }

    @Test
    @DisplayName("Map with cloud loads")
    void mapWithCloudLoads() throws Exception {
        String xml = "<map version=\"1.1.0\">"
                + "<node TEXT=\"Root\">"
                + "  <node TEXT=\"Cloudy\">"
                + "    <cloud/>"
                + "  </node>"
                + "</node>"
                + "</map>";
        MindMapMapModel map = MindMapGenerator.loadFromXml(xml);

        MindMapNode root = map.getRootNode();
        MindMapNode child = (MindMapNode) root.getChildAt(0);
        assertNotNull(child.getCloud(),
                "Child node cloud must not be null");
    }

    @Test
    @DisplayName("Map with icons loads")
    void mapWithIconsLoads() throws Exception {
        String xml = "<map version=\"1.1.0\">"
                + "<node TEXT=\"Root\">"
                + "  <node TEXT=\"Iconic\">"
                + "    <icon BUILTIN=\"button_ok\"/>"
                + "  </node>"
                + "</node>"
                + "</map>";
        MindMapMapModel map = MindMapGenerator.loadFromXml(xml);

        MindMapNode root = map.getRootNode();
        MindMapNode child = (MindMapNode) root.getChildAt(0);
        assertFalse(child.getIcons().isEmpty(),
                "Child node must have at least one icon");
        assertEquals("button_ok", child.getIcons().get(0).getName(),
                "Icon name must be 'button_ok'");
    }

    @Test
    @DisplayName("Map with arrow links loads")
    void mapWithArrowLinksLoads() throws Exception {
        String xml = "<map version=\"1.1.0\">"
                + "<node TEXT=\"Root\" ID=\"ID_1\">"
                + "  <node TEXT=\"Source\" ID=\"ID_2\">"
                + "    <arrowlink DESTINATION=\"ID_3\" "
                + "STARTINCLINATION=\"0;0;\" ENDINCLINATION=\"0;0;\" "
                + "STARTARROW=\"NONE\" ENDARROW=\"Default\"/>"
                + "  </node>"
                + "  <node TEXT=\"Target\" ID=\"ID_3\"/>"
                + "</node>"
                + "</map>";
        MindMapMapModel map = MindMapGenerator.loadFromXml(xml);

        MindMapNode root = map.getRootNode();
        assertNotNull(root, "Root node must not be null");
        assertEquals(2, root.getChildCount(),
                "Root must have 2 children (source and target)");
    }

    @Test
    @DisplayName("Map with richcontent notes loads")
    void mapWithRichContentLoads() throws Exception {
        String xml = "<map version=\"1.1.0\">"
                + "<node TEXT=\"Root\">"
                + "  <node TEXT=\"WithNote\">"
                + "    <richcontent TYPE=\"NOTE\">"
                + "      <html><head></head><body><p>A note</p></body></html>"
                + "    </richcontent>"
                + "  </node>"
                + "</node>"
                + "</map>";
        MindMapMapModel map = MindMapGenerator.loadFromXml(xml);

        MindMapNode root = map.getRootNode();
        MindMapNode child = (MindMapNode) root.getChildAt(0);
        assertNotNull(child.getNoteText(),
                "Child node note text must not be null");
    }

    @Test
    @DisplayName("Map with folded nodes loads")
    void mapWithFoldedNodesLoads() throws Exception {
        String xml = "<map version=\"1.1.0\">"
                + "<node TEXT=\"Root\">"
                + "  <node TEXT=\"Folded\" FOLDED=\"true\">"
                + "    <node TEXT=\"Hidden\"/>"
                + "  </node>"
                + "</node>"
                + "</map>";
        MindMapMapModel map = MindMapGenerator.loadFromXml(xml);

        MindMapNode root = map.getRootNode();
        MindMapNode child = (MindMapNode) root.getChildAt(0);
        assertTrue(child.isFolded(),
                "Folded node must report isFolded() = true");
        assertEquals(1, child.getChildCount(),
                "Folded node must still have its child");
    }

    @Test
    @DisplayName("FreeMind.XML_VERSION is 1.1.0")
    void versionStringPreserved() {
        assertEquals("1.1.0", FreeMind.XML_VERSION,
                "FreeMind.XML_VERSION must be '1.1.0'");
    }
}
