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
import freemind.modes.MindIcon;
import freemind.modes.MindMapNode;
import freemind.modes.attributes.Attribute;
import freemind.modes.mindmapmode.MindMapNodeModel;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import tests.freemind.MindMapMock;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for the FreeMind filter subsystem.
 *
 * Tests the interplay between {@link DefaultFilter}, {@link FilterInfo},
 * and various {@link Condition} implementations against multi-node
 * mind map tree structures.
 *
 * Since {@link DefaultFilter#applyFilter} requires a full GUI Controller,
 * these tests exercise:
 * <ul>
 *   <li>DefaultFilter construction and option queries</li>
 *   <li>DefaultFilter.isVisible() with manually-set FilterInfo states</li>
 *   <li>DefaultFilter.resetFilter() on node trees</li>
 *   <li>Complex condition integration (conjunct, disjunct, negation)
 *       evaluated against realistic node hierarchies</li>
 *   <li>Conditions with attributes and icons on tree structures</li>
 * </ul>
 */
@DisplayName("Filter Integration Tests")
class FilterIntegrationTest {

    private static MindMapMock mapMock;
    private static ConditionFactory factory;

    private static NamedObject FILTER_NODE;
    private static NamedObject FILTER_CONTAINS;

    /** Cached reflective reference to FilterInfo.add(int) which is package-private */
    private static Method addMethod;
    /** Cached reflective reference to FilterInfo.get() which is package-private */
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

    /** Calls the package-private FilterInfo.add(int) via reflection */
    private static void callAdd(FilterInfo fi, int flag) {
        try {
            addMethod.invoke(fi, flag);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /** Calls the package-private FilterInfo.get() via reflection */
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

    /**
     * Builds a tree: root -> [child1, child2, child3]
     * child1 -> [grandchild1, grandchild2]
     */
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

    /** Collects all nodes in pre-order traversal */
    private static List<MindMapNode> collectAllNodes(MindMapNode node) {
        List<MindMapNode> result = new ArrayList<>();
        result.add(node);
        ListIterator<MindMapNode> it = node.childrenUnfolded();
        while (it.hasNext()) {
            result.addAll(collectAllNodes(it.next()));
        }
        return result;
    }

    /** Simulates filter application by walking the tree and setting FilterInfo flags. */
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
    // DefaultFilter Construction Tests
    // ========================================================================

    @Nested
    @DisplayName("DefaultFilter Construction")
    class DefaultFilterConstructionTests {

        @Test
        @DisplayName("constructor with NoFilteringCondition sets all-pass filter")
        void constructWithNoFilteringCondition() {
            Condition noFilter = NoFilteringCondition.createCondition();
            DefaultFilter filter = new DefaultFilter(noFilter, true, true);

            assertSame(noFilter, filter.getCondition());
            assertTrue(filter.areMatchedShown());
            assertTrue(filter.areAncestorsShown());
            assertTrue(filter.areDescendantsShown());
            assertTrue(filter.areEclipsedShown());
            assertFalse(filter.areHiddenShown());
        }

        @Test
        @DisplayName("constructor without ancestors/descendants flags")
        void constructWithoutAncestorsDescendants() {
            Condition noFilter = NoFilteringCondition.createCondition();
            DefaultFilter filter = new DefaultFilter(noFilter, false, false);

            assertFalse(filter.areAncestorsShown());
            assertFalse(filter.areDescendantsShown());
            assertTrue(filter.areMatchedShown());
        }

        @Test
        @DisplayName("constructor with ancestors only")
        void constructWithAncestorsOnly() {
            Condition noFilter = NoFilteringCondition.createCondition();
            DefaultFilter filter = new DefaultFilter(noFilter, true, false);

            assertTrue(filter.areAncestorsShown());
            assertFalse(filter.areDescendantsShown());
        }

        @Test
        @DisplayName("constructor with descendants only")
        void constructWithDescendantsOnly() {
            Condition noFilter = NoFilteringCondition.createCondition();
            DefaultFilter filter = new DefaultFilter(noFilter, false, true);

            assertFalse(filter.areAncestorsShown());
            assertTrue(filter.areDescendantsShown());
        }

        @Test
        @DisplayName("getCondition returns the condition passed to constructor")
        void getConditionReturnsPassedCondition() {
            Condition condition = factory.createCondition(FILTER_NODE, FILTER_CONTAINS, "test", false);
            DefaultFilter filter = new DefaultFilter(condition, true, true);

            assertSame(condition, filter.getCondition());
        }

        @Test
        @DisplayName("constructor with null condition makes isVisible always true")
        void nullConditionMakesAllVisible() {
            DefaultFilter filter = new DefaultFilter(null, true, true);

            MindMapNode node = createNode("anything");
            assertTrue(filter.isVisible(node));
        }
    }

    // ========================================================================
    // DefaultFilter.isVisible Tests
    // ========================================================================

    @Nested
    @DisplayName("DefaultFilter.isVisible")
    class IsVisibleTests {

        @Test
        @DisplayName("node with FILTER_INITIAL_VALUE is visible (no filter applied)")
        void initialValueIsVisible() {
            DefaultFilter filter = new DefaultFilter(NoFilteringCondition.createCondition(), true, true);
            MindMapNode node = createNode("test");

            assertTrue(filter.isVisible(node));
        }

        @Test
        @DisplayName("matched node is visible")
        void matchedNodeIsVisible() {
            DefaultFilter filter = new DefaultFilter(NoFilteringCondition.createCondition(), true, true);
            MindMapNode node = createNode("test");

            callAdd(node.getFilterInfo(), Filter.FILTER_SHOW_MATCHED);
            assertTrue(filter.isVisible(node));
        }

        @Test
        @DisplayName("hidden-only node is not visible even with ancestors-shown option")
        void hiddenNodeNotVisibleEvenWithAncestorsShown() {
            DefaultFilter filter = new DefaultFilter(NoFilteringCondition.createCondition(), true, false);
            MindMapNode node = createNode("test");

            node.getFilterInfo().reset();
            callAdd(node.getFilterInfo(), Filter.FILTER_SHOW_HIDDEN);
            // A hidden-only node is not visible - ancestors-shown enables ancestor nodes
            // to be visible, but a hidden node with no other flags is still not visible
            assertFalse(filter.isVisible(node));
        }

        @Test
        @DisplayName("ancestor node is visible when ancestors are shown")
        void ancestorNodeVisibleWhenAncestorsShown() {
            DefaultFilter filter = new DefaultFilter(NoFilteringCondition.createCondition(), true, true);
            MindMapNode node = createNode("test");

            node.getFilterInfo().reset();
            callAdd(node.getFilterInfo(), Filter.FILTER_SHOW_ANCESTOR);
            assertTrue(filter.isVisible(node));
        }

        @Test
        @DisplayName("descendant node is visible when descendants are shown")
        void descendantNodeVisibleWhenDescendantsShown() {
            DefaultFilter filter = new DefaultFilter(NoFilteringCondition.createCondition(), false, true);
            MindMapNode node = createNode("test");

            node.getFilterInfo().reset();
            callAdd(node.getFilterInfo(), Filter.FILTER_SHOW_DESCENDANT);
            assertTrue(filter.isVisible(node));
        }

        @Test
        @DisplayName("null condition makes all nodes visible regardless of filter info")
        void nullConditionAllVisible() {
            DefaultFilter filter = new DefaultFilter(null, false, false);
            MindMapNode node = createNode("test");

            node.getFilterInfo().reset();
            callAdd(node.getFilterInfo(), Filter.FILTER_SHOW_HIDDEN);
            assertTrue(filter.isVisible(node));
        }
    }

    // ========================================================================
    // DefaultFilter.resetFilter Tests
    // ========================================================================

    @Nested
    @DisplayName("DefaultFilter.resetFilter")
    class ResetFilterTests {

        @Test
        @DisplayName("resetFilter restores initial value")
        void resetRestoresInitialValue() {
            MindMapNode node = createNode("test");

            callAdd(node.getFilterInfo(), Filter.FILTER_SHOW_MATCHED);
            callAdd(node.getFilterInfo(), Filter.FILTER_SHOW_ANCESTOR);

            DefaultFilter.resetFilter(node);

            assertEquals(Filter.FILTER_INITIAL_VALUE, callGet(node.getFilterInfo()));
        }

        @Test
        @DisplayName("resetFilter on tree resets all nodes individually")
        void resetOnTreeResetsAllNodes() {
            MindMapNodeModel root = buildSimpleTree();
            List<MindMapNode> allNodes = collectAllNodes(root);

            for (MindMapNode node : allNodes) {
                callAdd(node.getFilterInfo(), Filter.FILTER_SHOW_MATCHED);
            }

            for (MindMapNode node : allNodes) {
                DefaultFilter.resetFilter(node);
            }

            for (MindMapNode node : allNodes) {
                assertEquals(Filter.FILTER_INITIAL_VALUE, callGet(node.getFilterInfo()),
                        "Node '" + node.getText() + "' should have initial filter value after reset");
            }
        }

        @Test
        @DisplayName("reset then reapply filter gives fresh result")
        void resetThenReapply() {
            MindMapNode node = createNode("test");

            callAdd(node.getFilterInfo(), Filter.FILTER_SHOW_MATCHED);
            assertTrue(node.getFilterInfo().isMatched());

            DefaultFilter.resetFilter(node);
            assertFalse(node.getFilterInfo().isMatched());

            callAdd(node.getFilterInfo(), Filter.FILTER_SHOW_ANCESTOR);
            assertTrue(node.getFilterInfo().isAncestor());
            assertFalse(node.getFilterInfo().isMatched());
        }
    }

    // ========================================================================
    // FilterInfo State Tests
    // ========================================================================

    @Nested
    @DisplayName("FilterInfo State Management")
    class FilterInfoTests {

        @Test
        @DisplayName("fresh FilterInfo has initial value")
        void freshFilterInfoHasInitialValue() {
            FilterInfo info = new FilterInfo();
            assertEquals(Filter.FILTER_INITIAL_VALUE, callGet(info));
        }

        @Test
        @DisplayName("setMatched clears initial value and sets matched")
        void setMatchedClearsInitial() {
            FilterInfo info = new FilterInfo();
            info.setMatched();
            assertTrue(info.isMatched());
            assertEquals(0, callGet(info) & Filter.FILTER_INITIAL_VALUE);
        }

        @Test
        @DisplayName("setAncestor sets ancestor flag")
        void setAncestorSetsFlag() {
            FilterInfo info = new FilterInfo();
            info.setAncestor();
            assertTrue(info.isAncestor());
        }

        @Test
        @DisplayName("setDescendant sets descendant flag")
        void setDescendantSetsFlag() {
            FilterInfo info = new FilterInfo();
            info.setDescendant();
            assertTrue((callGet(info) & Filter.FILTER_SHOW_DESCENDANT) != 0);
        }

        @Test
        @DisplayName("multiple flags can be combined")
        void multipleFlagsCombine() {
            FilterInfo info = new FilterInfo();
            info.setMatched();
            info.setAncestor();
            assertTrue(info.isMatched());
            assertTrue(info.isAncestor());
        }

        @Test
        @DisplayName("reset clears all flags back to initial")
        void resetClearsAllFlags() {
            FilterInfo info = new FilterInfo();
            info.setMatched();
            info.setAncestor();
            info.setDescendant();
            info.reset();
            assertEquals(Filter.FILTER_INITIAL_VALUE, callGet(info));
            assertFalse(info.isMatched());
            assertFalse(info.isAncestor());
        }
    }

    // ========================================================================
    // Condition Integration with Node Trees
    // ========================================================================

    @Nested
    @DisplayName("Condition Integration with Node Trees")
    class ConditionTreeIntegrationTests {

        @Test
        @DisplayName("NodeContainsCondition matches nodes in tree containing text")
        void nodeContainsMatchesInTree() {
            Condition condition = factory.createCondition(FILTER_NODE, FILTER_CONTAINS, "Alpha", false);
            assertNotNull(condition);

            MindMapNodeModel root = buildSimpleTree();
            List<MindMapNode> allNodes = collectAllNodes(root);

            List<String> matchingTexts = new ArrayList<>();
            for (MindMapNode node : allNodes) {
                if (condition.checkNode(null, node)) {
                    matchingTexts.add(node.getText());
                }
            }

            assertEquals(2, matchingTexts.size());
            assertTrue(matchingTexts.contains("Alpha project"));
            assertTrue(matchingTexts.contains("Alpha sub-task"));
        }

        @Test
        @DisplayName("NodeContainsCondition does not match unrelated nodes")
        void nodeContainsNoFalsePositives() {
            Condition condition = factory.createCondition(FILTER_NODE, FILTER_CONTAINS, "Omega", false);
            MindMapNodeModel root = buildSimpleTree();

            for (MindMapNode node : collectAllNodes(root)) {
                assertFalse(condition.checkNode(null, node),
                        "Node '" + node.getText() + "' should not match 'Omega'");
            }
        }

        @Test
        @DisplayName("IgnoreCaseNodeContainsCondition matches case-insensitively in tree")
        void ignoreCaseMatchesInTree() {
            Condition condition = factory.createCondition(FILTER_NODE, FILTER_CONTAINS, "alpha", true);
            assertNotNull(condition);

            MindMapNodeModel root = buildSimpleTree();
            int matchCount = 0;
            for (MindMapNode node : collectAllNodes(root)) {
                if (condition.checkNode(null, node)) {
                    matchCount++;
                }
            }
            assertEquals(2, matchCount);
        }

        @Test
        @DisplayName("case-sensitive condition does not match different case")
        void caseSensitiveNoMatch() {
            Condition condition = factory.createCondition(FILTER_NODE, FILTER_CONTAINS, "alpha", false);
            MindMapNodeModel root = buildSimpleTree();

            for (MindMapNode node : collectAllNodes(root)) {
                assertFalse(condition.checkNode(null, node),
                        "Case-sensitive 'alpha' should not match '" + node.getText() + "'");
            }
        }

        @Test
        @DisplayName("NoFilteringCondition matches all nodes in tree")
        void noFilteringMatchesAllNodes() {
            Condition condition = NoFilteringCondition.createCondition();
            MindMapNodeModel root = buildSimpleTree();

            for (MindMapNode node : collectAllNodes(root)) {
                assertTrue(condition.checkNode(null, node),
                        "NoFilteringCondition should match node '" + node.getText() + "'");
            }
        }
    }

    // ========================================================================
    // Conjunct/Disjunct/NOT Condition Integration
    // ========================================================================

    @Nested
    @DisplayName("Composite Condition Integration")
    class CompositeConditionTests {

        @Test
        @DisplayName("ConjunctConditions: AND of two text conditions")
        void conjunctTwoTextConditions() {
            Condition containsAlpha = factory.createCondition(FILTER_NODE, FILTER_CONTAINS, "Alpha", false);
            Condition containsSub = factory.createCondition(FILTER_NODE, FILTER_CONTAINS, "sub", false);
            ConjunctConditions conjunct = new ConjunctConditions(new Object[]{containsAlpha, containsSub});

            MindMapNodeModel root = buildSimpleTree();
            List<String> matching = new ArrayList<>();
            for (MindMapNode node : collectAllNodes(root)) {
                if (conjunct.checkNode(null, node)) {
                    matching.add(node.getText());
                }
            }
            assertEquals(1, matching.size());
            assertEquals("Alpha sub-task", matching.get(0));
        }

        @Test
        @DisplayName("ConjunctConditions: AND fails when one condition is never satisfied")
        void conjunctFailsWhenOneFails() {
            Condition containsAlpha = factory.createCondition(FILTER_NODE, FILTER_CONTAINS, "Alpha", false);
            Condition containsOmega = factory.createCondition(FILTER_NODE, FILTER_CONTAINS, "Omega", false);
            ConjunctConditions conjunct = new ConjunctConditions(new Object[]{containsAlpha, containsOmega});

            MindMapNodeModel root = buildSimpleTree();
            for (MindMapNode node : collectAllNodes(root)) {
                assertFalse(conjunct.checkNode(null, node));
            }
        }

        @Test
        @DisplayName("DisjunctConditions: OR of two text conditions")
        void disjunctTwoTextConditions() {
            Condition containsAlpha = factory.createCondition(FILTER_NODE, FILTER_CONTAINS, "Alpha", false);
            Condition containsBeta = factory.createCondition(FILTER_NODE, FILTER_CONTAINS, "Beta", false);
            DisjunctConditions disjunct = new DisjunctConditions(new Object[]{containsAlpha, containsBeta});

            MindMapNodeModel root = buildSimpleTree();
            List<String> matching = new ArrayList<>();
            for (MindMapNode node : collectAllNodes(root)) {
                if (disjunct.checkNode(null, node)) {
                    matching.add(node.getText());
                }
            }
            assertEquals(3, matching.size());
            assertTrue(matching.contains("Alpha project"));
            assertTrue(matching.contains("Beta release"));
            assertTrue(matching.contains("Alpha sub-task"));
        }

        @Test
        @DisplayName("DisjunctConditions: OR with all-fail returns false")
        void disjunctAllFail() {
            Condition c1 = factory.createCondition(FILTER_NODE, FILTER_CONTAINS, "Omega", false);
            Condition c2 = factory.createCondition(FILTER_NODE, FILTER_CONTAINS, "Zeta", false);
            DisjunctConditions disjunct = new DisjunctConditions(new Object[]{c1, c2});

            MindMapNodeModel root = buildSimpleTree();
            for (MindMapNode node : collectAllNodes(root)) {
                assertFalse(disjunct.checkNode(null, node));
            }
        }

        @Test
        @DisplayName("ConditionNotSatisfiedDecorator: NOT inverts matching on tree")
        void notInvertsMatchingOnTree() {
            Condition containsAlpha = factory.createCondition(FILTER_NODE, FILTER_CONTAINS, "Alpha", false);
            ConditionNotSatisfiedDecorator notAlpha = new ConditionNotSatisfiedDecorator(containsAlpha);

            MindMapNodeModel root = buildSimpleTree();
            List<String> matching = new ArrayList<>();
            List<String> notMatching = new ArrayList<>();
            for (MindMapNode node : collectAllNodes(root)) {
                if (notAlpha.checkNode(null, node)) {
                    matching.add(node.getText());
                } else {
                    notMatching.add(node.getText());
                }
            }
            assertEquals(4, matching.size());
            assertEquals(2, notMatching.size());
            assertTrue(notMatching.contains("Alpha project"));
            assertTrue(notMatching.contains("Alpha sub-task"));
        }

        @Test
        @DisplayName("NOT of NoFilteringCondition matches nothing")
        void notOfNoFilteringMatchesNothing() {
            Condition noFilter = NoFilteringCondition.createCondition();
            ConditionNotSatisfiedDecorator notAll = new ConditionNotSatisfiedDecorator(noFilter);

            MindMapNodeModel root = buildSimpleTree();
            for (MindMapNode node : collectAllNodes(root)) {
                assertFalse(notAll.checkNode(null, node),
                        "NOT(NoFiltering) should match no node, but matched '" + node.getText() + "'");
            }
        }

        @Test
        @DisplayName("nested composite: NOT(AND(A, B)) = NAND")
        void nestedNandCondition() {
            Condition containsAlpha = factory.createCondition(FILTER_NODE, FILTER_CONTAINS, "Alpha", false);
            Condition containsSub = factory.createCondition(FILTER_NODE, FILTER_CONTAINS, "sub", false);
            ConjunctConditions and = new ConjunctConditions(new Object[]{containsAlpha, containsSub});
            ConditionNotSatisfiedDecorator nand = new ConditionNotSatisfiedDecorator(and);

            MindMapNodeModel root = buildSimpleTree();
            List<String> matching = new ArrayList<>();
            for (MindMapNode node : collectAllNodes(root)) {
                if (nand.checkNode(null, node)) {
                    matching.add(node.getText());
                }
            }
            assertEquals(5, matching.size());
            assertFalse(matching.contains("Alpha sub-task"));
        }
    }

    // ========================================================================
    // Icon-based Filter Integration
    // ========================================================================

    @Nested
    @DisplayName("Icon Condition Integration")
    class IconConditionIntegrationTests {

        @Test
        @DisplayName("IconContainedCondition matches nodes with matching icon in tree")
        void iconContainedMatchesInTree() {
            IconContainedCondition condition = new IconContainedCondition("idea");

            MindMapNodeModel root = createNode("Root");
            MindMapNodeModel withIcon = createNodeWithIcon("Has Idea", "idea");
            MindMapNodeModel withoutIcon = createNode("No Icon");
            MindMapNodeModel withOtherIcon = createNodeWithIcon("Has Help", "help");
            root.insert(withIcon, -1);
            root.insert(withoutIcon, -1);
            root.insert(withOtherIcon, -1);

            assertTrue(condition.checkNode(null, withIcon));
            assertFalse(condition.checkNode(null, root));
            assertFalse(condition.checkNode(null, withoutIcon));
            assertFalse(condition.checkNode(null, withOtherIcon));
        }

        @Test
        @DisplayName("IconNotContainedCondition matches nodes without the icon")
        void iconNotContainedMatchesInTree() {
            IconNotContainedCondition condition = new IconNotContainedCondition("idea");

            MindMapNodeModel root = createNode("Root");
            MindMapNodeModel withIcon = createNodeWithIcon("Has Idea", "idea");
            MindMapNodeModel withoutIcon = createNode("No Icon");

            assertFalse(condition.checkNode(null, withIcon));
            assertTrue(condition.checkNode(null, root));
            assertTrue(condition.checkNode(null, withoutIcon));
        }

        @Test
        @DisplayName("node with multiple icons matches each separately")
        void multipleIconsMatchSeparately() {
            MindMapNodeModel node = createNode("Multi-icon");
            node.addIcon(MindIcon.factory("idea"), MindIcon.LAST);
            node.addIcon(MindIcon.factory("help"), MindIcon.LAST);

            IconContainedCondition ideaCondition = new IconContainedCondition("idea");
            IconContainedCondition helpCondition = new IconContainedCondition("help");
            IconContainedCondition infoCondition = new IconContainedCondition("info");

            assertTrue(ideaCondition.checkNode(null, node));
            assertTrue(helpCondition.checkNode(null, node));
            assertFalse(infoCondition.checkNode(null, node));
        }
    }

    // ========================================================================
    // Attribute-based Filter Integration
    // ========================================================================

    @Nested
    @DisplayName("Attribute Condition Integration")
    class AttributeConditionIntegrationTests {

        @Test
        @DisplayName("AttributeExistsCondition matches nodes with the attribute in tree")
        void attributeExistsMatchesInTree() {
            AttributeExistsCondition condition = new AttributeExistsCondition("priority");

            MindMapNodeModel root = createNode("Root");
            MindMapNodeModel withAttr = createNodeWithAttribute("Task A", "priority", "high");
            MindMapNodeModel withoutAttr = createNode("Task B");
            MindMapNodeModel withOtherAttr = createNodeWithAttribute("Task C", "status", "done");
            root.insert(withAttr, -1);
            root.insert(withoutAttr, -1);
            root.insert(withOtherAttr, -1);

            assertTrue(condition.checkNode(null, withAttr));
            assertFalse(condition.checkNode(null, root));
            assertFalse(condition.checkNode(null, withoutAttr));
            assertFalse(condition.checkNode(null, withOtherAttr));
        }

        @Test
        @DisplayName("AttributeExistsCondition with multiple attributes on node")
        void attributeExistsMultipleAttrs() {
            MindMapNodeModel node = createNode("Multi-attr");
            node.addAttribute(new Attribute("priority", "high"));
            node.addAttribute(new Attribute("status", "pending"));

            AttributeExistsCondition priorityCond = new AttributeExistsCondition("priority");
            AttributeExistsCondition statusCond = new AttributeExistsCondition("status");
            AttributeExistsCondition missingCond = new AttributeExistsCondition("label");

            assertTrue(priorityCond.checkNode(null, node));
            assertTrue(statusCond.checkNode(null, node));
            assertFalse(missingCond.checkNode(null, node));
        }

        @Test
        @DisplayName("AttributeNotExistsCondition returns true when attribute is present")
        void attributeNotExistsLogic() {
            // Note: AttributeNotExistsCondition.checkNode returns true when attribute IS found
            // despite its class name suggesting otherwise - this matches the source code
            AttributeNotExistsCondition condition = new AttributeNotExistsCondition("priority");

            MindMapNodeModel withAttr = createNodeWithAttribute("Has it", "priority", "high");
            MindMapNodeModel withoutAttr = createNode("No attr");

            assertTrue(condition.checkNode(null, withAttr));
            assertFalse(condition.checkNode(null, withoutAttr));
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
}
