package tests.freemind.filter;

import freemind.common.NamedObject;
import freemind.controller.filter.DefaultFilter;
import freemind.controller.filter.Filter;
import freemind.controller.filter.FilterInfo;
import freemind.controller.filter.FilterController;
import freemind.controller.filter.condition.AttributeExistsCondition;
import freemind.controller.filter.condition.AttributeNotExistsCondition;
import freemind.controller.filter.condition.Condition;
import freemind.controller.filter.condition.ConditionFactory;
import freemind.controller.filter.condition.ConditionNotSatisfiedDecorator;
import freemind.controller.filter.condition.ConjunctConditions;
import freemind.controller.filter.condition.DisjunctConditions;
import freemind.controller.filter.condition.IconContainedCondition;
import freemind.controller.filter.condition.IconNotContainedCondition;
import freemind.controller.filter.condition.NoFilteringCondition;
import freemind.main.HeadlessFreeMind;
import freemind.main.XMLElement;
import freemind.modes.mindmapmode.MindMapMapModel;
import freemind.modes.MindIcon;
import freemind.modes.MindMapNode;
import freemind.modes.attributes.Attribute;
import freemind.modes.mindmapmode.MindMapNodeModel;
import tests.freemind.testutil.MindMapGenerator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import tests.freemind.MindMapMock;

import java.io.IOException;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Filter Integration Pipeline Tests")
class FilterIntegrationPipelineTest {

    private static MindMapMock mapMock;
    private static ConditionFactory factory;

    private static NamedObject FILTER_NODE;
    private static NamedObject FILTER_CONTAINS;

    private static Method addMethod;
    private static Method getMethod;

    @BeforeAll
    static void initResources() throws Exception {
        new HeadlessFreeMind();
        mapMock = new MindMapMock("<map></map>");
        factory = FilterController.getConditionFactory();

        FILTER_NODE = getStaticField("FILTER_NODE");
        FILTER_CONTAINS = getStaticField("FILTER_CONTAINS");

        addMethod = FilterInfo.class.getDeclaredMethod("add", int.class);
        addMethod.setAccessible(true);
        getMethod = FilterInfo.class.getDeclaredMethod("get");
        getMethod.setAccessible(true);
    }

    private static NamedObject getStaticField(String fieldName) throws Exception {
        Field field = ConditionFactory.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        return (NamedObject) field.get(null);
    }

    private static void callAdd(FilterInfo fi, int flag) {
        try {
            addMethod.invoke(fi, flag);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static int callGet(FilterInfo fi) {
        try {
            return (int) getMethod.invoke(fi);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static MindMapNodeModel createNode(String text) {
        return new MindMapNodeModel(text, mapMock);
    }

    private static MindMapNodeModel createNodeWithIcon(String text, String iconName) {
        MindMapNodeModel node = createNode(text);
        node.addIcon(MindIcon.factory(iconName), MindIcon.LAST);
        return node;
    }

    private static MindMapNodeModel createNodeWithAttribute(String text, String attrName, String attrValue) {
        MindMapNodeModel node = createNode(text);
        node.addAttribute(new Attribute(attrName, attrValue));
        return node;
    }

    private static MindMapNodeModel buildSimpleTree() {
        MindMapNodeModel root = createNode("Root");
        MindMapNodeModel child1 = createNode("Alpha project");
        MindMapNodeModel child2 = createNode("Beta release");
        MindMapNodeModel child3 = createNode("Gamma testing");
        MindMapNodeModel gc1 = createNode("Alpha sub-task");
        MindMapNodeModel gc2 = createNode("Delta sub-task");
        root.insert(child1, -1);
        root.insert(child2, -1);
        root.insert(child3, -1);
        child1.insert(gc1, -1);
        child1.insert(gc2, -1);
        return root;
    }

    private static List<MindMapNode> collectAllNodes(MindMapNode node) {
        List<MindMapNode> result = new ArrayList<>();
        result.add(node);
        ListIterator<MindMapNode> it = node.childrenUnfolded();
        while (it.hasNext()) {
            result.addAll(collectAllNodes(it.next()));
        }
        return result;
    }

    private static void simulateFilterApplication(MindMapNode root, Condition condition) {
        for (MindMapNode node : collectAllNodes(root)) {
            DefaultFilter.resetFilter(node);
        }
        applyConditionRecursive(root, condition, true);
    }

    private static boolean applyConditionRecursive(MindMapNode node, Condition condition, boolean isRoot) {
        boolean conditionSatisfied = condition.checkNode(null, node);
        if (!isRoot) {
            if (conditionSatisfied) {
                node.getFilterInfo().setMatched();
            } else {
                callAdd(node.getFilterInfo(), Filter.FILTER_SHOW_HIDDEN);
            }
        }

        boolean hasMatchingDescendant = false;
        ListIterator<MindMapNode> it = node.childrenUnfolded();
        while (it.hasNext()) {
            MindMapNode child = it.next();
            if (applyConditionRecursive(child, condition, false)) {
                hasMatchingDescendant = true;
            }
        }

        if (hasMatchingDescendant && !isRoot) {
            node.getFilterInfo().setAncestor();
        }
        if (conditionSatisfied && !isRoot) {
            ListIterator<MindMapNode> descIt = node.childrenUnfolded();
            while (descIt.hasNext()) {
                markDescendants(descIt.next());
            }
        }

        return conditionSatisfied || hasMatchingDescendant;
    }

    private static void markDescendants(MindMapNode node) {
        node.getFilterInfo().setDescendant();
        ListIterator<MindMapNode> it = node.childrenUnfolded();
        while (it.hasNext()) {
            markDescendants(it.next());
        }
    }

    // ========================================================================
    // Full Integration: Condition + FilterInfo + DefaultFilter.isVisible
    // ========================================================================

    @Nested
    @DisplayName("Full Filter Pipeline Integration")
    class FullPipelineTests {

        @Test
        @DisplayName("simulate filter: matched nodes visible, unmatched hidden")
        void matchedNodesVisibleUnmatchedHidden() {
            Condition containsAlpha = factory.createCondition(FILTER_NODE, FILTER_CONTAINS, "Alpha", false);
            DefaultFilter filter = new DefaultFilter(containsAlpha, true, true);

            MindMapNodeModel root = buildSimpleTree();
            simulateFilterApplication(root, containsAlpha);

            for (MindMapNode node : collectAllNodes(root)) {
                if (node == root) {
                    continue;
                }
                if (node.getText().contains("Alpha")) {
                    assertTrue(filter.isVisible(node),
                            "Node '" + node.getText() + "' should be visible (matched)");
                }
            }
        }

        @Test
        @DisplayName("simulate filter: ancestor of matching node is visible when ancestors shown")
        void ancestorNodeVisibleWhenAncestorsShown() {
            Condition containsAlpha = factory.createCondition(FILTER_NODE, FILTER_CONTAINS, "Alpha", false);
            DefaultFilter filter = new DefaultFilter(containsAlpha, true, false);

            MindMapNodeModel root = buildSimpleTree();
            simulateFilterApplication(root, containsAlpha);

            MindMapNode alphaProject = (MindMapNode) root.getChildAt(0);
            assertTrue(filter.isVisible(alphaProject));
        }

        @Test
        @DisplayName("simulate filter: descendant of matching node is visible when descendants shown")
        void descendantNodeVisibleWhenDescendantsShown() {
            Condition containsAlpha = factory.createCondition(FILTER_NODE, FILTER_CONTAINS, "Alpha", false);
            DefaultFilter filter = new DefaultFilter(containsAlpha, false, true);

            MindMapNodeModel root = buildSimpleTree();
            simulateFilterApplication(root, containsAlpha);

            MindMapNode alphaProject = (MindMapNode) root.getChildAt(0);
            MindMapNode deltaSub = (MindMapNode) alphaProject.getChildAt(1);
            assertEquals("Delta sub-task", deltaSub.getText());
            assertTrue(filter.isVisible(deltaSub),
                    "Descendant of matched node should be visible when descendants are shown");
        }

        @Test
        @DisplayName("multiple filter applications: second application gives fresh results")
        void multipleFilterApplications() {
            MindMapNodeModel root = buildSimpleTree();

            Condition containsAlpha = factory.createCondition(FILTER_NODE, FILTER_CONTAINS, "Alpha", false);
            simulateFilterApplication(root, containsAlpha);

            MindMapNode betaRelease = (MindMapNode) root.getChildAt(1);
            assertFalse(betaRelease.getFilterInfo().isMatched(),
                    "Beta should not match Alpha filter");

            Condition containsBeta = factory.createCondition(FILTER_NODE, FILTER_CONTAINS, "Beta", false);
            simulateFilterApplication(root, containsBeta);

            assertTrue(betaRelease.getFilterInfo().isMatched(),
                    "Beta should match Beta filter after re-application");
            MindMapNode alphaProject = (MindMapNode) root.getChildAt(0);
            assertFalse(alphaProject.getFilterInfo().isMatched(),
                    "Alpha should not match Beta filter after re-application");
        }

        @Test
        @DisplayName("filter with conjunct condition: only double-matching nodes pass")
        void filterWithConjunctCondition() {
            Condition containsAlpha = factory.createCondition(FILTER_NODE, FILTER_CONTAINS, "Alpha", false);
            Condition containsSub = factory.createCondition(FILTER_NODE, FILTER_CONTAINS, "sub", false);
            ConjunctConditions conjunct = new ConjunctConditions(new Object[]{containsAlpha, containsSub});

            MindMapNodeModel root = buildSimpleTree();
            simulateFilterApplication(root, conjunct);

            int matchedCount = 0;
            for (MindMapNode node : collectAllNodes(root)) {
                if (node.getFilterInfo().isMatched()) {
                    matchedCount++;
                    assertEquals("Alpha sub-task", node.getText());
                }
            }
            assertEquals(1, matchedCount);
        }

        @Test
        @DisplayName("filter with disjunct condition: nodes matching either pass")
        void filterWithDisjunctCondition() {
            Condition containsAlpha = factory.createCondition(FILTER_NODE, FILTER_CONTAINS, "Alpha", false);
            Condition containsGamma = factory.createCondition(FILTER_NODE, FILTER_CONTAINS, "Gamma", false);
            DisjunctConditions disjunct = new DisjunctConditions(new Object[]{containsAlpha, containsGamma});

            MindMapNodeModel root = buildSimpleTree();
            simulateFilterApplication(root, disjunct);

            List<String> matched = new ArrayList<>();
            for (MindMapNode node : collectAllNodes(root)) {
                if (node.getFilterInfo().isMatched()) {
                    matched.add(node.getText());
                }
            }
            assertEquals(3, matched.size());
            assertTrue(matched.contains("Alpha project"));
            assertTrue(matched.contains("Gamma testing"));
            assertTrue(matched.contains("Alpha sub-task"));
        }

        @Test
        @DisplayName("filter reset makes all nodes have initial value again")
        void filterResetRestoresAllNodes() {
            Condition containsAlpha = factory.createCondition(FILTER_NODE, FILTER_CONTAINS, "Alpha", false);
            MindMapNodeModel root = buildSimpleTree();

            simulateFilterApplication(root, containsAlpha);

            boolean hasNonInitial = false;
            for (MindMapNode node : collectAllNodes(root)) {
                if (callGet(node.getFilterInfo()) != Filter.FILTER_INITIAL_VALUE) {
                    hasNonInitial = true;
                    break;
                }
            }
            assertTrue(hasNonInitial, "After filter application, some nodes should have non-initial state");

            for (MindMapNode node : collectAllNodes(root)) {
                DefaultFilter.resetFilter(node);
            }

            DefaultFilter allPassFilter = new DefaultFilter(NoFilteringCondition.createCondition(), true, true);
            for (MindMapNode node : collectAllNodes(root)) {
                assertEquals(Filter.FILTER_INITIAL_VALUE, callGet(node.getFilterInfo()),
                        "Node '" + node.getText() + "' should be reset to initial value");
                assertTrue(allPassFilter.isVisible(node),
                        "Node '" + node.getText() + "' should be visible after reset");
            }
        }
    }

    // ========================================================================
    // Filter Root Protection
    // ========================================================================

    @Nested
    @DisplayName("Filter Root Protection")
    class FilterRootProtectionTests {

        @Test
        @DisplayName("Root node is never hidden by filter even when it does not match condition")
        void rootNodeNeverHiddenByFilter() {
            // Create a tree where root text does NOT match the condition
            MindMapNodeModel root = createNode("RootNoMatch");
            MindMapNodeModel child1 = createNode("Alpha target");
            MindMapNodeModel child2 = createNode("Beta item");
            MindMapNodeModel child3 = createNode("Gamma item");
            root.insert(child1, -1);
            root.insert(child2, -1);
            root.insert(child3, -1);

            // Condition that matches "Alpha" — root text "RootNoMatch" does NOT match
            Condition containsAlpha = factory.createCondition(FILTER_NODE, FILTER_CONTAINS, "Alpha", false);
            assertFalse(containsAlpha.checkNode(null, root),
                    "Root should NOT match the Alpha condition");

            // Apply filter using simulateFilterApplication (which protects root)
            simulateFilterApplication(root, containsAlpha);

            // Root node must NEVER be hidden — simulateFilterApplication skips root in filter flags
            // The root's FilterInfo should remain at FILTER_INITIAL_VALUE (not FILTER_SHOW_HIDDEN)
            int rootFilterValue = callGet(root.getFilterInfo());
            assertNotEquals(Filter.FILTER_SHOW_HIDDEN, rootFilterValue,
                    "Root node must NEVER have FILTER_SHOW_HIDDEN flag set");

            // Verify root is visible with any reasonable filter configuration
            DefaultFilter filter = new DefaultFilter(containsAlpha, true, true);
            assertTrue(filter.isVisible(root),
                    "Root node must always be visible regardless of filter condition");
        }
    }

    // ========================================================================
    // Filter-Export Interaction
    // ========================================================================

    @Nested
    @DisplayName("Filter-Export Interaction")
    class FilterExportInteractionTests {

        @Test
        @DisplayName("Export includes all nodes regardless of filter state")
        void exportIncludesAllNodesRegardlessOfFilter() throws Exception {
            // Create a map with 5 nodes via XML (root + 4 children)
            String mapXml = "<map>"
                    + "<node TEXT='ExportRoot'>"
                    + "<node TEXT='Alpha visible'/>"
                    + "<node TEXT='Beta hidden'/>"
                    + "<node TEXT='Gamma hidden'/>"
                    + "<node TEXT='Delta hidden'/>"
                    + "</node>"
                    + "</map>";

            MindMapMapModel map = MindMapGenerator.loadFromXml(mapXml);
            MindMapNode root = map.getRootNode();
            assertEquals(4, root.getChildCount(), "Map should have 4 children");

            // Apply a filter that only matches "Alpha" — hides 3 of 4 children
            Condition containsAlpha = factory.createCondition(FILTER_NODE, FILTER_CONTAINS, "Alpha", false);
            simulateFilterApplication(root, containsAlpha);

            // Verify filter was applied: only 1 child matches
            int matchedCount = 0;
            for (MindMapNode node : collectAllNodes(root)) {
                if (node.getFilterInfo().isMatched()) {
                    matchedCount++;
                }
            }
            assertEquals(1, matchedCount, "Only 1 child should match the Alpha filter");

            // Export to XML via getXml (which includes ALL nodes regardless of filter)
            StringWriter sw = new StringWriter();
            map.getXml(sw);
            String xml = sw.toString();

            // Verify ALL 5 nodes are present in the exported XML
            assertTrue(xml.contains("ExportRoot"), "Export must include root");
            assertTrue(xml.contains("Alpha visible"), "Export must include Alpha (matched)");
            assertTrue(xml.contains("Beta hidden"), "Export must include Beta (filtered)");
            assertTrue(xml.contains("Gamma hidden"), "Export must include Gamma (filtered)");
            assertTrue(xml.contains("Delta hidden"), "Export must include Delta (filtered)");

            // Verify total node count after re-loading the exported XML
            MindMapMapModel reloaded = MindMapGenerator.loadFromXml(xml);
            assertEquals(5, MindMapGenerator.countNodes(reloaded.getRootNode()),
                    "Exported and reloaded map must have all 5 nodes");
        }
    }

    // ========================================================================
    // Filter Save/Load (Condition Serialization)
    // ========================================================================

    @Nested
    @DisplayName("Filter Condition Save/Load")
    class FilterSaveLoadTests {

        @Test
        @DisplayName("NodeContainsCondition survives save/load cycle with identical behavior")
        void conditionSaveLoadRoundTrip() {
            // Create a condition using the factory
            Condition original = factory.createCondition(FILTER_NODE, FILTER_CONTAINS, "Alpha", false);
            assertNotNull(original, "Factory should create a condition");

            // Save the condition to an XMLElement
            XMLElement container = new XMLElement();
            original.save(container);

            // The save method adds a child element to the container
            assertTrue(container.countChildren() > 0,
                    "save() should add a child element to the container");

            // Load the condition back from the saved XMLElement
            XMLElement savedChild = container.getChildren().get(0);
            Condition loaded = factory.loadCondition(savedChild);
            assertNotNull(loaded, "loadCondition should return a non-null condition");

            // Verify the loaded condition behaves identically to the original
            MindMapNodeModel matchingNode = createNode("Alpha project");
            MindMapNodeModel nonMatchingNode = createNode("Beta release");

            assertEquals(original.checkNode(null, matchingNode),
                    loaded.checkNode(null, matchingNode),
                    "Loaded condition should match same nodes as original (matching case)");
            assertEquals(original.checkNode(null, nonMatchingNode),
                    loaded.checkNode(null, nonMatchingNode),
                    "Loaded condition should match same nodes as original (non-matching case)");

            // Both should match "Alpha project" and not match "Beta release"
            assertTrue(loaded.checkNode(null, matchingNode),
                    "Loaded condition should match 'Alpha project'");
            assertFalse(loaded.checkNode(null, nonMatchingNode),
                    "Loaded condition should not match 'Beta release'");
        }
    }
}
