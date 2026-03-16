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

@DisplayName("Filter Integration Basic Tests")
class FilterIntegrationBasicTest {

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
}
