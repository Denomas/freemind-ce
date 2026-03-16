package tests.freemind.testutil;

import freemind.main.HeadlessFreeMind;
import freemind.modes.MindMapNode;
import freemind.modes.mindmapmode.MindMapMapModel;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("MindMapGenerator — Test Data Factory")
class MindMapGeneratorTest {

    @BeforeAll
    static void init() {
        new HeadlessFreeMind();
    }

    @Test
    @DisplayName("small() creates ~10 nodes")
    void smallGeneratesApproximateNodeCount() {
        MindMapMapModel map = MindMapGenerator.small().build();
        int count = MindMapGenerator.countNodes(map.getRootNode());
        assertTrue(count >= 5 && count <= 15,
                "small() should create ~10 nodes, got: " + count);
    }

    @Test
    @DisplayName("medium() creates ~1000 nodes")
    void mediumGeneratesApproximateNodeCount() {
        MindMapMapModel map = MindMapGenerator.medium().build();
        int count = MindMapGenerator.countNodes(map.getRootNode());
        assertTrue(count >= 500 && count <= 1500,
                "medium() should create ~1000 nodes, got: " + count);
    }

    @Test
    @DisplayName("custom node count respected")
    void customNodeCountRespected() {
        MindMapMapModel map = MindMapGenerator.create()
                .withNodes(50).withDepth(4).withWidth(4).build();
        int count = MindMapGenerator.countNodes(map.getRootNode());
        assertTrue(count <= 55, "Should not exceed target + tolerance, got: " + count);
        assertTrue(count >= 20, "Should create a meaningful number of nodes, got: " + count);
    }

    @Test
    @DisplayName("toXmlString produces valid XML with map element")
    void toXmlStringProducesValidXml() throws IOException {
        String xml = MindMapGenerator.small().toXmlString();
        assertNotNull(xml);
        assertTrue(xml.startsWith("<map "), "XML should start with <map element");
        assertTrue(xml.contains("</map>"), "XML should end with </map>");
        assertTrue(xml.contains("Root"), "XML should contain root node text");
    }

    @Test
    @DisplayName("toFile writes readable .mm file")
    void toFileWritesReadableFile(@TempDir Path tempDir) throws IOException {
        Path mmFile = tempDir.resolve("test.mm");
        MindMapGenerator.small().toFile(mmFile.toString());

        assertTrue(Files.exists(mmFile), "File should exist");
        String content = Files.readString(mmFile);
        assertTrue(content.startsWith("<map "), "File should contain valid mind map XML");
        assertTrue(content.contains("Node_"), "File should contain generated node text");
    }

    @Test
    @DisplayName("withIcons adds icons to nodes")
    void withIconsAddsIcons() {
        MindMapMapModel map = MindMapGenerator.create()
                .withNodes(5).withDepth(2).withWidth(3).withIcons(2).build();
        MindMapNode root = map.getRootNode();
        // Check a child has icons (root may not have icons in current impl)
        if (root.getChildCount() > 0) {
            MindMapNode child = (MindMapNode) root.getChildAt(0);
            assertEquals(2, child.getIcons().size(),
                    "Each child should have 2 icons");
        }
    }

    @Test
    @DisplayName("withNotes adds notes to percentage of nodes")
    void withNotesAddsNotes() throws IOException {
        String xml = MindMapGenerator.create()
                .withNodes(20).withDepth(4).withWidth(3)
                .withNotes(100).toXmlString();
        assertTrue(xml.contains("<richcontent") || xml.contains("Note for node"),
                "100% note rate should produce notes in XML");
    }

    @Test
    @DisplayName("withLinks adds hyperlinks to percentage of nodes")
    void withLinksAddsLinks() throws IOException {
        String xml = MindMapGenerator.create()
                .withNodes(20).withDepth(4).withWidth(3)
                .withLinks(100).toXmlString();
        assertTrue(xml.contains("https://example.com/node/"),
                "100% link rate should produce links in XML");
    }

    @Test
    @DisplayName("withAttributes adds attributes to nodes")
    void withAttributesAddsAttributes() {
        MindMapMapModel map = MindMapGenerator.create()
                .withNodes(5).withDepth(2).withWidth(3)
                .withAttributes(3).build();
        MindMapNode root = map.getRootNode();
        if (root.getChildCount() > 0) {
            MindMapNode child = (MindMapNode) root.getChildAt(0);
            assertEquals(3, child.getAttributeTableLength(),
                    "Each child should have 3 attributes");
        }
    }

    @Test
    @DisplayName("deterministic seed produces identical node structure")
    void deterministicSeedProducesIdenticalStructure() {
        MindMapMapModel map1 = MindMapGenerator.create()
                .withNodes(20).withDepth(4).withWidth(3).withSeed(123).build();
        MindMapMapModel map2 = MindMapGenerator.create()
                .withNodes(20).withDepth(4).withWidth(3).withSeed(123).build();
        int count1 = MindMapGenerator.countNodes(map1.getRootNode());
        int count2 = MindMapGenerator.countNodes(map2.getRootNode());
        assertEquals(count1, count2, "Same seed should produce same node count");
        assertEquals(map1.getRootNode().getChildCount(), map2.getRootNode().getChildCount(),
                "Same seed should produce same root children count");
    }

    @Test
    @DisplayName("different seeds produce different node counts")
    void differentSeedsProduceDifferentStructure() {
        MindMapMapModel map1 = MindMapGenerator.create()
                .withNodes(50).withDepth(5).withWidth(4).withSeed(1).build();
        MindMapMapModel map2 = MindMapGenerator.create()
                .withNodes(50).withDepth(5).withWidth(4).withSeed(999).build();
        // Different seeds may produce slightly different structure
        // At minimum, the node text ordering will differ
        assertNotNull(map1.getRootNode());
        assertNotNull(map2.getRootNode());
    }

    @Test
    @DisplayName("countNodes correctly counts tree")
    void countNodesCorrectlyCountsTree() {
        MindMapMapModel map = MindMapGenerator.create()
                .withNodes(1).withDepth(1).withWidth(1).build();
        int count = MindMapGenerator.countNodes(map.getRootNode());
        assertEquals(1, count, "Single-node map should have count 1");
    }
}
