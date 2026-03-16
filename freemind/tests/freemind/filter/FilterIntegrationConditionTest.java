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

@DisplayName("Filter Integration Condition Tests")
class FilterIntegrationConditionTest {

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
}
