package tests.freemind.filter;

import freemind.common.NamedObject;
import freemind.controller.filter.FilterController;
import freemind.controller.filter.condition.AttributeCompareCondition;
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
import freemind.controller.filter.condition.SelectedViewCondition;
import freemind.main.HeadlessFreeMind;
import freemind.main.XMLElement;
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
import java.util.Vector;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Filter Conditions — Basic")
class FilterConditionBasicTest {

    private static ConditionFactory factory;
    private static MindMapMock mapMock;

    // ConditionFactory constants accessed via reflection (they are package-private)
    private static NamedObject FILTER_NODE;
    private static NamedObject FILTER_ICON;
    private static NamedObject FILTER_CONTAINS;
    private static NamedObject FILTER_NOT_CONTAINS;
    private static NamedObject FILTER_IS_EQUAL_TO;
    private static NamedObject FILTER_IS_NOT_EQUAL_TO;
    private static NamedObject FILTER_GT;
    private static NamedObject FILTER_GE;
    private static NamedObject FILTER_LT;
    private static NamedObject FILTER_LE;
    private static NamedObject FILTER_EXIST;
    private static NamedObject FILTER_DOES_NOT_EXIST;

    @BeforeAll
    static void ensureResources() throws Exception {
        new HeadlessFreeMind();
        factory = FilterController.getConditionFactory();
        mapMock = new MindMapMock("<map></map>");

        FILTER_NODE = getStaticField("FILTER_NODE");
        FILTER_ICON = getStaticField("FILTER_ICON");
        FILTER_CONTAINS = getStaticField("FILTER_CONTAINS");
        FILTER_NOT_CONTAINS = getStaticField("FILTER_NOT_CONTAINS");
        FILTER_IS_EQUAL_TO = getStaticField("FILTER_IS_EQUAL_TO");
        FILTER_IS_NOT_EQUAL_TO = getStaticField("FILTER_IS_NOT_EQUAL_TO");
        FILTER_GT = getStaticField("FILTER_GT");
        FILTER_GE = getStaticField("FILTER_GE");
        FILTER_LT = getStaticField("FILTER_LT");
        FILTER_LE = getStaticField("FILTER_LE");
        FILTER_EXIST = getStaticField("FILTER_EXIST");
        FILTER_DOES_NOT_EXIST = getStaticField("FILTER_DOES_NOT_EXIST");
    }

    private static NamedObject getStaticField(String fieldName) throws Exception {
        Field field = ConditionFactory.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        return (NamedObject) field.get(null);
    }

    private static MindMapNode createNode(String text) {
        return new MindMapNodeModel(text, mapMock);
    }

    private static MindMapNode createNodeWithAttribute(String text, String attrName, String attrValue) {
        MindMapNodeModel node = new MindMapNodeModel(text, mapMock);
        node.addAttribute(new Attribute(attrName, attrValue));
        return node;
    }

    private static MindMapNode createNodeWithIcon(String text, String iconName) {
        MindMapNodeModel node = new MindMapNodeModel(text, mapMock);
        node.addIcon(MindIcon.factory(iconName), MindIcon.LAST);
        return node;
    }

    private static XMLElement saveCondition(Condition condition) {
        XMLElement root = new XMLElement();
        root.setName("filter_conditions");
        condition.save(root);
        Vector<XMLElement> children = root.getChildren();
        assertFalse(children.isEmpty(), "save() must produce at least one child element");
        return children.get(0);
    }

    // =========================================================================
    // NodeContainsCondition
    // =========================================================================
    @Nested
    @DisplayName("NodeContainsCondition")
    class NodeContainsConditionTests {
        @Test
        @DisplayName("checkNode returns true when node text contains the value")
        void matchingNode() {
            Condition cond = factory.createCondition(FILTER_NODE, FILTER_CONTAINS, "hello", false);
            assertNotNull(cond);
            assertTrue(cond.checkNode(null, createNode("say hello world")));
        }

        @Test
        @DisplayName("checkNode returns false when node text does not contain the value")
        void nonMatchingNode() {
            Condition cond = factory.createCondition(FILTER_NODE, FILTER_CONTAINS, "missing", false);
            assertNotNull(cond);
            assertFalse(cond.checkNode(null, createNode("hello world")));
        }

        @Test
        @DisplayName("case-sensitive: uppercase value does not match lowercase text")
        void caseSensitive() {
            Condition cond = factory.createCondition(FILTER_NODE, FILTER_CONTAINS, "Hello", false);
            assertNotNull(cond);
            assertFalse(cond.checkNode(null, createNode("hello world")));
        }

        @Test
        @DisplayName("save produces valid XML with correct name and value attributes")
        void saveProducesValidXml() {
            Condition cond = factory.createCondition(FILTER_NODE, FILTER_CONTAINS, "test", false);
            XMLElement child = saveCondition(cond);
            assertEquals("node_contains_condition", child.getName());
            assertEquals("test", child.getStringAttribute("value"));
        }

        @Test
        @DisplayName("round-trip: save then load preserves behavior")
        void roundTrip() {
            Condition original = factory.createCondition(FILTER_NODE, FILTER_CONTAINS, "roundtrip", false);
            XMLElement saved = saveCondition(original);
            Condition loaded = factory.loadCondition(saved);
            assertNotNull(loaded);
            MindMapNode matching = createNode("test roundtrip here");
            MindMapNode nonMatching = createNode("no match");
            assertEquals(original.checkNode(null, matching), loaded.checkNode(null, matching));
            assertEquals(original.checkNode(null, nonMatching), loaded.checkNode(null, nonMatching));
        }

        @Test
        @DisplayName("createDesctiption returns non-null string")
        void descriptionNotNull() {
            Condition cond = factory.createCondition(FILTER_NODE, FILTER_CONTAINS, "test", false);
            assertNotNull(cond.toString());
            assertFalse(cond.toString().isEmpty());
        }

        @Test
        @DisplayName("empty value returns null from factory")
        void emptyValueReturnsNull() {
            Condition cond = factory.createCondition(FILTER_NODE, FILTER_CONTAINS, "", false);
            assertNull(cond, "Empty value should return null for contains condition");
        }
    }

    // =========================================================================
    // IgnoreCaseNodeContainsCondition
    // =========================================================================
    @Nested
    @DisplayName("IgnoreCaseNodeContainsCondition")
    class IgnoreCaseNodeContainsConditionTests {
        @Test
        @DisplayName("checkNode matches regardless of case")
        void caseInsensitiveMatch() {
            Condition cond = factory.createCondition(FILTER_NODE, FILTER_CONTAINS, "HELLO", true);
            assertNotNull(cond);
            assertTrue(cond.checkNode(null, createNode("hello world")));
        }

        @Test
        @DisplayName("checkNode returns false when text does not contain the value")
        void noMatch() {
            Condition cond = factory.createCondition(FILTER_NODE, FILTER_CONTAINS, "MISSING", true);
            assertNotNull(cond);
            assertFalse(cond.checkNode(null, createNode("hello world")));
        }

        @Test
        @DisplayName("save produces XML with ignore_case name")
        void saveXml() {
            Condition cond = factory.createCondition(FILTER_NODE, FILTER_CONTAINS, "test", true);
            XMLElement child = saveCondition(cond);
            assertEquals("ignore_case_node_contains_condition", child.getName());
        }

        @Test
        @DisplayName("round-trip preserves case-insensitive behavior")
        void roundTrip() {
            Condition original = factory.createCondition(FILTER_NODE, FILTER_CONTAINS, "MixedCase", true);
            XMLElement saved = saveCondition(original);
            Condition loaded = factory.loadCondition(saved);
            assertNotNull(loaded);
            assertTrue(loaded.checkNode(null, createNode("mixedcase is here")));
        }

        @Test
        @DisplayName("createDesctiption returns non-null string")
        void descriptionNotNull() {
            Condition cond = factory.createCondition(FILTER_NODE, FILTER_CONTAINS, "test", true);
            assertNotNull(cond.toString());
        }
    }

    // =========================================================================
    // NodeCompareCondition
    // =========================================================================
    @Nested
    @DisplayName("NodeCompareCondition")
    class NodeCompareConditionTests {
        @Test
        @DisplayName("IS_EQUAL_TO matches equal string values")
        void equalTo() {
            Condition cond = factory.createCondition(FILTER_NODE, FILTER_IS_EQUAL_TO, "hello", false);
            assertTrue(cond.checkNode(null, createNode("hello")));
            assertFalse(cond.checkNode(null, createNode("world")));
        }

        @Test
        @DisplayName("IS_NOT_EQUAL_TO matches non-equal values")
        void notEqualTo() {
            Condition cond = factory.createCondition(FILTER_NODE, FILTER_IS_NOT_EQUAL_TO, "hello", false);
            assertFalse(cond.checkNode(null, createNode("hello")));
            assertTrue(cond.checkNode(null, createNode("world")));
        }

        @Test
        @DisplayName("GT compares strings lexicographically")
        void greaterThan() {
            Condition cond = factory.createCondition(FILTER_NODE, FILTER_GT, "b", false);
            assertTrue(cond.checkNode(null, createNode("c")));
            assertFalse(cond.checkNode(null, createNode("a")));
            assertFalse(cond.checkNode(null, createNode("b")));
        }

        @Test
        @DisplayName("LT compares strings lexicographically")
        void lessThan() {
            Condition cond = factory.createCondition(FILTER_NODE, FILTER_LT, "b", false);
            assertTrue(cond.checkNode(null, createNode("a")));
            assertFalse(cond.checkNode(null, createNode("c")));
        }

        @Test
        @DisplayName("GE matches equal or greater values")
        void greaterEqual() {
            Condition cond = factory.createCondition(FILTER_NODE, FILTER_GE, "b", false);
            assertTrue(cond.checkNode(null, createNode("b")));
            assertTrue(cond.checkNode(null, createNode("c")));
            assertFalse(cond.checkNode(null, createNode("a")));
        }

        @Test
        @DisplayName("LE matches equal or lesser values")
        void lessEqual() {
            Condition cond = factory.createCondition(FILTER_NODE, FILTER_LE, "b", false);
            assertTrue(cond.checkNode(null, createNode("b")));
            assertTrue(cond.checkNode(null, createNode("a")));
            assertFalse(cond.checkNode(null, createNode("c")));
        }

        @Test
        @DisplayName("numeric comparison: integer values compared numerically")
        void numericComparison() {
            Condition cond = factory.createCondition(FILTER_NODE, FILTER_GT, "5", false);
            assertTrue(cond.checkNode(null, createNode("10")));
            assertFalse(cond.checkNode(null, createNode("3")));
        }

        @Test
        @DisplayName("IS_EQUAL_TO with ignore case matches different casing")
        void equalToIgnoreCase() {
            Condition cond = factory.createCondition(FILTER_NODE, FILTER_IS_EQUAL_TO, "Hello", true);
            assertTrue(cond.checkNode(null, createNode("hello")));
        }

        @Test
        @DisplayName("save produces valid XML")
        void saveXml() {
            Condition cond = factory.createCondition(FILTER_NODE, FILTER_IS_EQUAL_TO, "test", false);
            XMLElement child = saveCondition(cond);
            assertEquals("node_compare_condition", child.getName());
            assertEquals("test", child.getStringAttribute("value"));
        }

        @Test
        @DisplayName("round-trip preserves comparison behavior")
        void roundTrip() {
            Condition original = factory.createCondition(FILTER_NODE, FILTER_GT, "mid", false);
            XMLElement saved = saveCondition(original);
            Condition loaded = factory.loadCondition(saved);
            assertNotNull(loaded);
            assertEquals(original.checkNode(null, createNode("xyz")), loaded.checkNode(null, createNode("xyz")));
            assertEquals(original.checkNode(null, createNode("abc")), loaded.checkNode(null, createNode("abc")));
        }

        @Test
        @DisplayName("createDesctiption returns non-null string")
        void descriptionNotNull() {
            Condition cond = factory.createCondition(FILTER_NODE, FILTER_IS_EQUAL_TO, "test", false);
            assertNotNull(cond.toString());
        }
    }

    // =========================================================================
    // CompareConditionAdapter
    // =========================================================================
    @Nested
    @DisplayName("CompareConditionAdapter")
    class CompareConditionAdapterTests {
        @Test
        @DisplayName("integer values compared numerically not lexicographically")
        void integerComparison() {
            Condition cond = factory.createCondition(FILTER_NODE, FILTER_GT, "5", false);
            assertTrue(cond.checkNode(null, createNode("10")));
        }

        @Test
        @DisplayName("non-numeric values fall back to string comparison")
        void stringFallback() {
            Condition cond = factory.createCondition(FILTER_NODE, FILTER_GT, "apple", false);
            assertTrue(cond.checkNode(null, createNode("banana")));
            assertFalse(cond.checkNode(null, createNode("aardvark")));
        }

        @Test
        @DisplayName("saveAttributes includes value and ignore_case")
        void saveAttributes() {
            Condition cond = factory.createCondition(FILTER_NODE, FILTER_IS_EQUAL_TO, "test", true);
            XMLElement child = saveCondition(cond);
            assertEquals("test", child.getStringAttribute("value"));
            assertEquals("true", child.getStringAttribute("ignore_case"));
        }
    }
}
