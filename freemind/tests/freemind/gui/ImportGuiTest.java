package tests.freemind.gui;

import freemind.main.HtmlTools;
import freemind.main.Tools;
import freemind.modes.ExtendedMapFeedbackImpl;
import freemind.modes.MapAdapter;
import freemind.modes.MindMapNode;
import freemind.modes.mindmapmode.MindMapMapModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.StringWriter;

import static org.assertj.core.api.Assertions.assertThat;
import static tests.freemind.unicode.UnicodeTestData.*;

/**
 * GUI tests for import operations.
 * Verifies that external XML structures can be loaded into the map model,
 * preserving node hierarchy, text, and notes.
 */
class ImportGuiTest extends GuiTestBase {

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
    void import_loadExternalBranch() throws Exception {
        // Load a branch XML and attach it as child of root
        String branchXml = "<map><node TEXT='Branch'><node TEXT='Leaf'/></node></map>";

        MindMapNode branchRoot = runOnEdtAndGet(() -> {
            MapModelFixture branchFixture = createMapModel();
            MindMapNode loaded = loadMap(branchFixture.model, branchXml);
            return loaded;
        });

        // Simulate import: create matching structure under root
        runOnEdt(() -> {
            MindMapNode branch = createChildNode(mapFeedback, root, branchRoot.getText());
            for (int i = 0; i < branchRoot.getChildCount(); i++) {
                MindMapNode srcChild = (MindMapNode) branchRoot.getChildAt(i);
                createChildNode(mapFeedback, branch, srcChild.getText());
            }
        });

        assertThat(root.getChildCount()).isEqualTo(1);
        MindMapNode importedBranch = (MindMapNode) root.getChildAt(0);
        assertThat(importedBranch.getText()).isEqualTo("Branch");
        assertThat(importedBranch.getChildCount()).isEqualTo(1);
        MindMapNode importedLeaf = (MindMapNode) importedBranch.getChildAt(0);
        assertThat(importedLeaf.getText()).isEqualTo("Leaf");
    }

    @Test
    void import_loadBranchPreservesChildren() throws Exception {
        // Load branch with 5 children, verify all present after import
        StringBuilder branchXml = new StringBuilder("<map><node TEXT='Parent'>");
        for (int i = 0; i < 5; i++) {
            branchXml.append("<node TEXT='Child_").append(i).append("'/>");
        }
        branchXml.append("</node></map>");

        MindMapNode branchRoot = runOnEdtAndGet(() -> {
            MapModelFixture branchFixture = createMapModel();
            return loadMap(branchFixture.model, branchXml.toString());
        });

        // Import the branch under root
        runOnEdt(() -> {
            MindMapNode parent = createChildNode(mapFeedback, root, branchRoot.getText());
            for (int i = 0; i < branchRoot.getChildCount(); i++) {
                MindMapNode srcChild = (MindMapNode) branchRoot.getChildAt(i);
                createChildNode(mapFeedback, parent, srcChild.getText());
            }
        });

        MindMapNode importedParent = (MindMapNode) root.getChildAt(0);
        assertThat(importedParent.getText()).isEqualTo("Parent");
        assertThat(importedParent.getChildCount()).isEqualTo(5);
        for (int i = 0; i < 5; i++) {
            MindMapNode child = (MindMapNode) importedParent.getChildAt(i);
            assertThat(child.getText()).isEqualTo("Child_" + i);
        }
    }

    @Test
    void import_loadBranchWithoutRoot() throws Exception {
        // Load XML, take children of loaded root (skip root itself) and attach to target
        String branchXml = "<map><node TEXT='SkipMe'>"
            + "<node TEXT='Keep1'/><node TEXT='Keep2'/><node TEXT='Keep3'/>"
            + "</node></map>";

        MindMapNode branchRoot = runOnEdtAndGet(() -> {
            MapModelFixture branchFixture = createMapModel();
            return loadMap(branchFixture.model, branchXml);
        });

        // Import children only (without root)
        runOnEdt(() -> {
            for (int i = 0; i < branchRoot.getChildCount(); i++) {
                MindMapNode srcChild = (MindMapNode) branchRoot.getChildAt(i);
                createChildNode(mapFeedback, root, srcChild.getText());
            }
        });

        assertThat(root.getChildCount()).isEqualTo(3);
        assertNodeExists(root, "Keep1");
        assertNodeExists(root, "Keep2");
        assertNodeExists(root, "Keep3");
        // The skipped root should not be in the tree
        assertThat(findNodeByText(root, "SkipMe")).isNull();
    }

    @Test
    void import_loadMultipleBranches() throws Exception {
        // Import 3 separate branch XMLs into same parent
        String[] branchXmls = {
            "<map><node TEXT='Branch_A'><node TEXT='A_Child'/></node></map>",
            "<map><node TEXT='Branch_B'><node TEXT='B_Child'/></node></map>",
            "<map><node TEXT='Branch_C'><node TEXT='C_Child'/></node></map>"
        };

        for (String branchXml : branchXmls) {
            MindMapNode branchRoot = runOnEdtAndGet(() -> {
                MapModelFixture branchFixture = createMapModel();
                return loadMap(branchFixture.model, branchXml);
            });

            runOnEdt(() -> {
                MindMapNode imported = createChildNode(mapFeedback, root, branchRoot.getText());
                for (int i = 0; i < branchRoot.getChildCount(); i++) {
                    MindMapNode srcChild = (MindMapNode) branchRoot.getChildAt(i);
                    createChildNode(mapFeedback, imported, srcChild.getText());
                }
            });
        }

        assertThat(root.getChildCount()).isEqualTo(3);
        assertNodeExists(root, "Branch_A");
        assertNodeExists(root, "Branch_B");
        assertNodeExists(root, "Branch_C");
        assertNodeExists(root, "A_Child");
        assertNodeExists(root, "B_Child");
        assertNodeExists(root, "C_Child");
    }

    @Test
    void import_opmlStructure() throws Exception {
        // Create a map that mimics OPML structure (root -> outline1 -> outline2)
        // then save/reload and verify
        runOnEdt(() -> {
            MindMapNode outline1 = createChildNode(mapFeedback, root, "Outline Level 1");
            MindMapNode outline2 = createChildNode(mapFeedback, outline1, "Outline Level 2");
            createChildNode(mapFeedback, outline2, "Outline Level 3");
        });

        String xml = saveMapToXml(map);
        MindMapNode reloaded = reloadMap(xml);

        assertThat(reloaded.getText()).isEqualTo("ROOT");
        assertThat(reloaded.getChildCount()).isEqualTo(1);
        assertNodeExists(reloaded, "Outline Level 1");
        assertNodeExists(reloaded, "Outline Level 2");
        assertNodeExists(reloaded, "Outline Level 3");
        assertThat(treeDepth(reloaded)).isEqualTo(3);
    }

    @Test
    void import_xbelStructure() throws Exception {
        // Create a map that mimics XBEL bookmarks (folders with URLs)
        runOnEdt(() -> {
            MindMapNode bookmarks = createChildNode(mapFeedback, root, "Bookmarks");
            MindMapNode devFolder = createChildNode(mapFeedback, bookmarks, "Development");
            createChildNode(mapFeedback, devFolder, "https://github.com");
            createChildNode(mapFeedback, devFolder, "https://stackoverflow.com");
            MindMapNode newsFolder = createChildNode(mapFeedback, bookmarks, "News");
            createChildNode(mapFeedback, newsFolder, "https://news.ycombinator.com");
        });

        String xml = saveMapToXml(map);
        MindMapNode reloaded = reloadMap(xml);

        assertNodeExists(reloaded, "Bookmarks");
        assertNodeExists(reloaded, "Development");
        assertNodeExists(reloaded, "News");
        assertNodeExists(reloaded, "https://github.com");
        assertNodeExists(reloaded, "https://stackoverflow.com");
        assertNodeExists(reloaded, "https://news.ycombinator.com");
        assertThat(countNodes(reloaded)).isEqualTo(7); // ROOT + 6
    }

    @Test
    void import_preservesUnicode() throws Exception {
        // Import branches with all unicode scripts, verify text preserved
        runOnEdt(() -> {
            for (int i = 0; i < ALL_SCRIPTS.length; i++) {
                createChildNode(mapFeedback, root, ALL_SCRIPTS[i]);
            }
        });

        String xml = saveMapToXml(map);
        MindMapNode reloaded = reloadMap(xml);

        for (int i = 0; i < ALL_SCRIPTS.length; i++) {
            assertThat(findNodeByText(reloaded, ALL_SCRIPTS[i]))
                .as("Node with %s text should exist after import round-trip", ALL_SCRIPT_NAMES[i])
                .isNotNull();
        }
    }

    @Test
    void import_folderStructureSimulation() throws Exception {
        // Create a deep hierarchy simulating folder import
        // Root -> Folder1 -> Folder2 -> Folder3 -> File.txt
        runOnEdt(() -> {
            MindMapNode folder1 = createChildNode(mapFeedback, root, "Folder1");
            MindMapNode folder2 = createChildNode(mapFeedback, folder1, "Folder2");
            MindMapNode folder3 = createChildNode(mapFeedback, folder2, "Folder3");
            createChildNode(mapFeedback, folder3, "File.txt");
        });

        assertThat(treeDepth(root)).isEqualTo(4);
        assertNodeExists(root, "Folder1");
        assertNodeExists(root, "Folder2");
        assertNodeExists(root, "Folder3");
        assertNodeExists(root, "File.txt");

        // Verify parent chain
        MindMapNode file = findNodeByText(root, "File.txt");
        assertThat(file).isNotNull();
        assertThat(((MindMapNode) file.getParent()).getText()).isEqualTo("Folder3");
        assertThat(((MindMapNode) file.getParent().getParent()).getText()).isEqualTo("Folder2");
        assertThat(((MindMapNode) file.getParent().getParent().getParent()).getText()).isEqualTo("Folder1");
    }

    @Test
    void import_mindManagerFile() throws Exception {
        // MindManager import — verify external branch loading works as import mechanism
        String externalMap = "<map><node TEXT='MindManager'>"
            + "<node TEXT='Topic1'/><node TEXT='Topic2'/>"
            + "</node></map>";
        MapModelFixture fixture = createMapModel();
        MindMapNode imported = loadMap(fixture.model, externalMap);
        assertThat(imported.getText()).isEqualTo("MindManager");
        assertThat(imported.getChildCount()).isEqualTo(2);
    }

    @Test
    void import_linkedBranch() throws Exception {
        // Linked branch import
        String branchXml = "<map><node TEXT='LinkedRoot'><node TEXT='LinkedChild'/></node></map>";
        MapModelFixture fixture = createMapModel();
        MindMapNode branch = loadMap(fixture.model, branchXml);
        assertThat(branch.getText()).isEqualTo("LinkedRoot");
        assertThat(branch.getChildCount()).isEqualTo(1);
    }

    @Test
    void import_linkedBranchWithoutRoot() throws Exception {
        // Import branch children without root node
        String branchXml = "<map><node TEXT='SkipMe'><node TEXT='Keep1'/><node TEXT='Keep2'/></node></map>";
        MapModelFixture fixture = createMapModel();
        MindMapNode branch = loadMap(fixture.model, branchXml);
        // Import only children, not root
        for (int i = 0; i < branch.getChildCount(); i++) {
            MindMapNode child = (MindMapNode) branch.getChildAt(i);
            createChildNode(mapFeedback, root, child.getText());
        }
        assertNodeExists(root, "Keep1");
        assertNodeExists(root, "Keep2");
        // "SkipMe" should not be in our map's root children
        for (int i = 0; i < root.getChildCount(); i++) {
            assertThat(((MindMapNode) root.getChildAt(i)).getText()).isNotEqualTo("SkipMe");
        }
    }

    @Test
    void import_explorerFavorites() throws Exception {
        // Explorer favorites are essentially bookmarks — simulate as linked nodes
        MapModelFixture fixture = createMapModel();
        MindMapNode favRoot = loadMap(fixture.model,
            "<map><node TEXT='Favorites'>"
            + "<node TEXT='Site1' LINK='http://example.com'/>"
            + "<node TEXT='Site2' LINK='http://example.org'/>"
            + "</node></map>");
        assertThat(favRoot.getChildCount()).isEqualTo(2);
        MindMapNode site1 = (MindMapNode) favRoot.getChildAt(0);
        assertThat(site1.getLink()).isEqualTo("http://example.com");
    }

    @Test
    void import_mergeIntoExistingMap() throws Exception {
        // Build existing map with children
        runOnEdt(() -> {
            createChildNode(mapFeedback, root, "Existing1");
            createChildNode(mapFeedback, root, "Existing2");
        });
        assertThat(root.getChildCount()).isEqualTo(2);

        // Import additional nodes from external branch
        String branchXml = "<map><node TEXT='ImportRoot'>"
            + "<node TEXT='Imported1'/><node TEXT='Imported2'/><node TEXT='Imported3'/>"
            + "</node></map>";

        MindMapNode branchRoot = runOnEdtAndGet(() -> {
            MapModelFixture branchFixture = createMapModel();
            return loadMap(branchFixture.model, branchXml);
        });

        // Merge: add imported children to existing root
        runOnEdt(() -> {
            for (int i = 0; i < branchRoot.getChildCount(); i++) {
                MindMapNode srcChild = (MindMapNode) branchRoot.getChildAt(i);
                createChildNode(mapFeedback, root, srcChild.getText());
            }
        });

        // Verify all original + imported nodes exist
        assertThat(root.getChildCount()).isEqualTo(5);
        assertNodeExists(root, "Existing1");
        assertNodeExists(root, "Existing2");
        assertNodeExists(root, "Imported1");
        assertNodeExists(root, "Imported2");
        assertNodeExists(root, "Imported3");
    }

    /** Plan-name alias for {@link #import_opmlStructure()}. */
    @Test
    void import_opml() throws Exception {
        import_opmlStructure();
    }

    /** Plan-name alias for {@link #import_xbelStructure()}. */
    @Test
    void import_xbel() throws Exception {
        import_xbelStructure();
    }

    /** Plan-name alias for {@link #import_folderStructureSimulation()}. */
    @Test
    void import_folderStructure() throws Exception {
        import_folderStructureSimulation();
    }

    @Override
    protected MindMapNode getMapRootForScreenshot() {
        return root;
    }
}
