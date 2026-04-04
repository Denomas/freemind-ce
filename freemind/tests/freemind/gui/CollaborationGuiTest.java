package tests.freemind.gui;

import freemind.modes.ExtendedMapFeedbackImpl;
import freemind.modes.MindMapNode;
import freemind.modes.mindmapmode.MindMapMapModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * GUI tests for collaboration-related model operations.
 * Tests map serialization, deserialization, independent model instances,
 * concurrent creation, copy/reload, and large map handling at the model
 * level (since actual socket/db collaboration cannot be tested headlessly).
 */
class CollaborationGuiTest extends GuiTestBase {

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
    void collaboration_mapCanBeSerialized() throws Exception {
        runOnEdt(() -> {
            createChildNode(mapFeedback, root, "SerializeMe");
        });

        String xml = saveMapToXml(map);

        assertThat(xml).isNotNull().isNotEmpty();
        assertThat(xml).contains("ROOT");
        assertThat(xml).contains("SerializeMe");
    }

    @Test
    void collaboration_mapCanBeDeserialized() throws Exception {
        runOnEdt(() -> {
            createChildNode(mapFeedback, root, "Child1");
            createChildNode(mapFeedback, root, "Child2");
        });

        String xml = saveMapToXml(map);
        MindMapNode reloaded = reloadMap(xml);

        assertThat(reloaded.getText()).isEqualTo("ROOT");
        assertThat(reloaded.getChildCount()).isEqualTo(2);
        assertNodeExists(reloaded, "Child1");
        assertNodeExists(reloaded, "Child2");
    }

    @Test
    void collaboration_multipleModelsIndependent() throws Exception {
        // Create 3 independent maps and modify each differently
        MapModelFixture fixture1 = createMapModel();
        MindMapNode root1 = loadMap(fixture1.model, "<map><node TEXT='Map1'/></map>");

        MapModelFixture fixture2 = createMapModel();
        MindMapNode root2 = loadMap(fixture2.model, "<map><node TEXT='Map2'/></map>");

        MapModelFixture fixture3 = createMapModel();
        MindMapNode root3 = loadMap(fixture3.model, "<map><node TEXT='Map3'/></map>");

        // Modify each independently
        runOnEdt(() -> {
            createChildNode(fixture1.feedback, root1, "Only_In_Map1");
            createChildNode(fixture2.feedback, root2, "Only_In_Map2");
            createChildNode(fixture3.feedback, root3, "Only_In_Map3");
        });

        // Verify each model retains its own state
        assertThat(root1.getText()).isEqualTo("Map1");
        assertThat(root1.getChildCount()).isEqualTo(1);
        assertNodeExists(root1, "Only_In_Map1");
        assertThat(findNodeByText(root1, "Only_In_Map2")).isNull();
        assertThat(findNodeByText(root1, "Only_In_Map3")).isNull();

        assertThat(root2.getText()).isEqualTo("Map2");
        assertThat(root2.getChildCount()).isEqualTo(1);
        assertNodeExists(root2, "Only_In_Map2");
        assertThat(findNodeByText(root2, "Only_In_Map1")).isNull();

        assertThat(root3.getText()).isEqualTo("Map3");
        assertThat(root3.getChildCount()).isEqualTo(1);
        assertNodeExists(root3, "Only_In_Map3");
        assertThat(findNodeByText(root3, "Only_In_Map1")).isNull();
    }

    @Test
    void collaboration_concurrentMapCreation() throws Exception {
        // Create 10 maps in sequence, verify all valid
        MapModelFixture[] fixtures = new MapModelFixture[10];
        MindMapNode[] roots = new MindMapNode[10];

        for (int i = 0; i < 10; i++) {
            fixtures[i] = createMapModel();
            roots[i] = loadMap(fixtures[i].model,
                "<map><node TEXT='ConcurrentMap_" + i + "'/></map>");
        }

        // Verify all maps are valid and independent
        for (int i = 0; i < 10; i++) {
            assertThat(roots[i]).isNotNull();
            assertThat(roots[i].getText()).isEqualTo("ConcurrentMap_" + i);
            assertThat(fixtures[i].model).isNotNull();
        }

        // Add a child to each and verify isolation
        for (int i = 0; i < 10; i++) {
            final int idx = i;
            runOnEdt(() -> createChildNode(fixtures[idx].feedback, roots[idx], "Child_" + idx));
        }

        for (int i = 0; i < 10; i++) {
            assertThat(roots[i].getChildCount()).isEqualTo(1);
            assertNodeExists(roots[i], "Child_" + i);
        }
    }

    @Test
    void collaboration_mapCopyPreservesStructure() throws Exception {
        // Build a map with structure
        runOnEdt(() -> {
            MindMapNode parent = createChildNode(mapFeedback, root, "Parent");
            createChildNode(mapFeedback, parent, "ChildA");
            createChildNode(mapFeedback, parent, "ChildB");
            createChildNode(mapFeedback, root, "Sibling");
        });

        // Save to XML
        String xml = saveMapToXml(map);

        // Reload into 2 different models
        MapModelFixture copy1Fixture = createMapModel();
        MindMapNode copy1Root = loadMap(copy1Fixture.model, xml);

        MapModelFixture copy2Fixture = createMapModel();
        MindMapNode copy2Root = loadMap(copy2Fixture.model, xml);

        // Verify both copies have the same structure
        assertThat(countNodes(copy1Root)).isEqualTo(countNodes(copy2Root));
        assertThat(treeDepth(copy1Root)).isEqualTo(treeDepth(copy2Root));

        assertNodeExists(copy1Root, "Parent");
        assertNodeExists(copy1Root, "ChildA");
        assertNodeExists(copy1Root, "ChildB");
        assertNodeExists(copy1Root, "Sibling");

        assertNodeExists(copy2Root, "Parent");
        assertNodeExists(copy2Root, "ChildA");
        assertNodeExists(copy2Root, "ChildB");
        assertNodeExists(copy2Root, "Sibling");
    }

    @Test
    void collaboration_modifyAfterReload() throws Exception {
        // Build initial map
        runOnEdt(() -> {
            createChildNode(mapFeedback, root, "Original");
        });

        // Save, reload into new model
        String xml = saveMapToXml(map);
        MapModelFixture reloadFixture = createMapModel();
        MindMapNode reloadedRoot = loadMap(reloadFixture.model, xml);

        // Modify the reloaded map
        runOnEdt(() -> {
            createChildNode(reloadFixture.feedback, reloadedRoot, "AddedAfterReload");
        });

        // Save again
        String xml2 = saveMapToXml(reloadFixture.model);
        MindMapNode finalRoot = reloadMap(xml2);

        // Verify both original and new nodes exist
        assertThat(finalRoot.getChildCount()).isEqualTo(2);
        assertNodeExists(finalRoot, "Original");
        assertNodeExists(finalRoot, "AddedAfterReload");
    }

    @Test
    void collaboration_largeMapSerialization() throws Exception {
        // Create map with 200 nodes (root + 20 parents x 10 children each = 201 total)
        runOnEdt(() -> {
            for (int i = 0; i < 20; i++) {
                MindMapNode parent = createChildNode(mapFeedback, root, "Parent_" + i);
                for (int j = 0; j < 10; j++) {
                    createChildNode(mapFeedback, parent, "Child_" + i + "_" + j);
                }
            }
        });

        int originalCount = countNodes(root);
        assertThat(originalCount).isEqualTo(221); // 1 root + 20 parents + 200 children

        // Serialize and deserialize
        String xml = saveMapToXml(map);
        assertThat(xml).isNotEmpty();

        MindMapNode reloaded = reloadMap(xml);
        int reloadedCount = countNodes(reloaded);

        assertThat(reloadedCount).isEqualTo(originalCount);

        // Spot-check some nodes
        assertNodeExists(reloaded, "Parent_0");
        assertNodeExists(reloaded, "Parent_19");
        assertNodeExists(reloaded, "Child_0_0");
        assertNodeExists(reloaded, "Child_19_9");
    }

    @Test
    void collaboration_socketMaster() throws Exception {
        // Socket collaboration requires network; verify model can serialize for sharing
        MapModelFixture fixture = createMapModel();
        loadMap(fixture.model, "<map><node TEXT='MasterMap'/></map>");
        String xml = saveMapToXml(fixture.model);
        assertThat(xml).contains("MasterMap");
    }

    @Test
    void collaboration_socketSlave() throws Exception {
        // Slave receives and loads map from master
        String masterXml = "<map><node TEXT='SharedMap'><node TEXT='SharedChild'/></node></map>";
        MindMapNode root = reloadMap(masterXml);
        assertThat(root.getText()).isEqualTo("SharedMap");
        assertThat(root.getChildCount()).isEqualTo(1);
    }

    @Test
    void collaboration_socketPublish() throws Exception {
        MapModelFixture fixture = createMapModel();
        MindMapNode root = loadMap(fixture.model, "<map><node TEXT='Published'/></map>");
        createChildNode(fixture.feedback, root, "Content");
        String xml = saveMapToXml(fixture.model);
        assertThat(xml).contains("Published");
        assertThat(xml).contains("Content");
    }

    @Test
    void collaboration_databaseMaster() throws Exception {
        MapModelFixture fixture = createMapModel();
        loadMap(fixture.model, "<map><node TEXT='DbMaster'/></map>");
        String xml = saveMapToXml(fixture.model);
        // Verify map can be serialized for database storage
        assertThat(xml).isNotEmpty();
        assertThat(xml).contains("DbMaster");
    }

    @Test
    void collaboration_databaseSlave() throws Exception {
        // Simulate loading from database
        String dbContent = "<map><node TEXT='DbSlave'><node TEXT='SyncedNode'/></node></map>";
        MindMapNode root = reloadMap(dbContent);
        assertThat(root.getText()).isEqualTo("DbSlave");
        assertNodeExists(root, "SyncedNode");
    }

    @Override
    protected MindMapNode getMapRootForScreenshot() {
        return root;
    }
}
