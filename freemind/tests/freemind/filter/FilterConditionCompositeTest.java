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

@DisplayName("Filter Conditions — Composite")
class FilterConditionCompositeTest {

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
    // ConjunctConditions (AND)
    // =========================================================================
    @Nested
    @DisplayName("ConjunctConditions (AND)")
    class ConjunctConditionsTests {
        @Test
        @DisplayName("returns true when all conditions satisfied")
        void allTrue() {
            Condition c = new ConjunctConditions(new Object[]{
                    factory.createCondition(FILTER_NODE, FILTER_CONTAINS, "hello", false),
                    factory.createCondition(FILTER_NODE, FILTER_CONTAINS, "world", false)});
            assertTrue(c.checkNode(null, createNode("hello world")));
        }

        @Test
        @DisplayName("returns false when one condition fails")
        void oneFalse() {
            Condition c = new ConjunctConditions(new Object[]{
                    factory.createCondition(FILTER_NODE, FILTER_CONTAINS, "hello", false),
                    factory.createCondition(FILTER_NODE, FILTER_CONTAINS, "missing", false)});
            assertFalse(c.checkNode(null, createNode("hello world")));
        }

        @Test
        @DisplayName("returns false when all conditions fail")
        void allFalse() {
            Condition c = new ConjunctConditions(new Object[]{
                    factory.createCondition(FILTER_NODE, FILTER_CONTAINS, "x", false),
                    factory.createCondition(FILTER_NODE, FILTER_CONTAINS, "y", false)});
            assertFalse(c.checkNode(null, createNode("hello world")));
        }

        @Test
        @DisplayName("save produces valid XML with child conditions")
        void saveXml() {
            Condition c = new ConjunctConditions(new Object[]{
                    factory.createCondition(FILTER_NODE, FILTER_CONTAINS, "a", false),
                    factory.createCondition(FILTER_NODE, FILTER_CONTAINS, "b", false)});
            XMLElement child = saveCondition(c);
            assertEquals("conjunct_condition", child.getName());
            assertEquals(2, child.getChildren().size());
        }

        @Test
        @DisplayName("round-trip preserves AND behavior")
        void roundTrip() {
            Condition original = new ConjunctConditions(new Object[]{
                    factory.createCondition(FILTER_NODE, FILTER_CONTAINS, "alpha", false),
                    factory.createCondition(FILTER_NODE, FILTER_CONTAINS, "beta", false)});
            Condition loaded = factory.loadCondition(saveCondition(original));
            assertTrue(loaded.checkNode(null, createNode("alpha beta")));
            assertFalse(loaded.checkNode(null, createNode("alpha only")));
        }
    }

    // =========================================================================
    // DisjunctConditions (OR)
    // =========================================================================
    @Nested
    @DisplayName("DisjunctConditions (OR)")
    class DisjunctConditionsTests {
        @Test
        @DisplayName("returns true when one condition satisfied")
        void oneTrue() {
            Condition c = new DisjunctConditions(new Object[]{
                    factory.createCondition(FILTER_NODE, FILTER_CONTAINS, "hello", false),
                    factory.createCondition(FILTER_NODE, FILTER_CONTAINS, "missing", false)});
            assertTrue(c.checkNode(null, createNode("hello world")));
        }

        @Test
        @DisplayName("returns false when all conditions fail")
        void allFalse() {
            Condition c = new DisjunctConditions(new Object[]{
                    factory.createCondition(FILTER_NODE, FILTER_CONTAINS, "x", false),
                    factory.createCondition(FILTER_NODE, FILTER_CONTAINS, "y", false)});
            assertFalse(c.checkNode(null, createNode("hello world")));
        }

        @Test
        @DisplayName("save produces valid XML with child conditions")
        void saveXml() {
            Condition c = new DisjunctConditions(new Object[]{
                    factory.createCondition(FILTER_NODE, FILTER_CONTAINS, "a", false),
                    factory.createCondition(FILTER_NODE, FILTER_CONTAINS, "b", false)});
            XMLElement child = saveCondition(c);
            assertEquals("disjunct_condition", child.getName());
            assertEquals(2, child.getChildren().size());
        }

        @Test
        @DisplayName("round-trip preserves OR behavior")
        void roundTrip() {
            Condition original = new DisjunctConditions(new Object[]{
                    factory.createCondition(FILTER_NODE, FILTER_CONTAINS, "alpha", false),
                    factory.createCondition(FILTER_NODE, FILTER_CONTAINS, "beta", false)});
            Condition loaded = factory.loadCondition(saveCondition(original));
            assertTrue(loaded.checkNode(null, createNode("alpha only")));
            assertFalse(loaded.checkNode(null, createNode("gamma only")));
        }
    }

    // =========================================================================
    // ConditionNotSatisfiedDecorator (NOT)
    // =========================================================================
    @Nested
    @DisplayName("ConditionNotSatisfiedDecorator (NOT)")
    class ConditionNotSatisfiedDecoratorTests {
        @Test
        @DisplayName("inverts true to false")
        void invertTrue() {
            Condition inner = factory.createCondition(FILTER_NODE, FILTER_CONTAINS, "hello", false);
            assertFalse(new ConditionNotSatisfiedDecorator(inner).checkNode(null, createNode("hello world")));
        }

        @Test
        @DisplayName("inverts false to true")
        void invertFalse() {
            Condition inner = factory.createCondition(FILTER_NODE, FILTER_CONTAINS, "missing", false);
            assertTrue(new ConditionNotSatisfiedDecorator(inner).checkNode(null, createNode("hello world")));
        }

        @Test
        @DisplayName("double negation restores original result")
        void doubleNegation() {
            Condition inner = factory.createCondition(FILTER_NODE, FILTER_CONTAINS, "hello", false);
            Condition notNot = new ConditionNotSatisfiedDecorator(new ConditionNotSatisfiedDecorator(inner));
            MindMapNode node = createNode("hello world");
            assertEquals(inner.checkNode(null, node), notNot.checkNode(null, node));
        }

        @Test
        @DisplayName("save wraps inner condition in XML")
        void saveXml() {
            Condition inner = factory.createCondition(FILTER_NODE, FILTER_CONTAINS, "test", false);
            XMLElement child = saveCondition(new ConditionNotSatisfiedDecorator(inner));
            assertEquals("negate_condition", child.getName());
            assertEquals(1, child.getChildren().size());
        }

        @Test
        @DisplayName("round-trip preserves NOT behavior")
        void roundTrip() {
            Condition inner = factory.createCondition(FILTER_NODE, FILTER_CONTAINS, "alpha", false);
            Condition original = new ConditionNotSatisfiedDecorator(inner);
            Condition loaded = factory.loadCondition(saveCondition(original));
            assertNotNull(loaded);
            assertFalse(loaded.checkNode(null, createNode("alpha beta")));
            assertTrue(loaded.checkNode(null, createNode("gamma")));
        }
    }

    // =========================================================================
    // NoFilteringCondition
    // =========================================================================
    @Nested
    @DisplayName("NoFilteringCondition")
    class NoFilteringConditionTests {
        @Test
        @DisplayName("checkNode always returns true")
        void alwaysTrue() {
            Condition cond = NoFilteringCondition.createCondition();
            assertTrue(cond.checkNode(null, createNode("anything")));
            assertTrue(cond.checkNode(null, createNode("")));
        }

        @Test
        @DisplayName("createCondition returns singleton")
        void singleton() {
            assertSame(NoFilteringCondition.createCondition(), NoFilteringCondition.createCondition());
        }

        @Test
        @DisplayName("save produces no child elements")
        void saveEmpty() {
            XMLElement root = new XMLElement();
            root.setName("test");
            NoFilteringCondition.createCondition().save(root);
            assertTrue(root.getChildren().isEmpty());
        }

        @Test
        @DisplayName("toString returns non-null string")
        void toStringNotNull() {
            assertNotNull(NoFilteringCondition.createCondition().toString());
        }
    }

    // =========================================================================
    // SelectedViewCondition
    // =========================================================================
    @Nested
    @DisplayName("SelectedViewCondition")
    class SelectedViewConditionTests {
        @Test
        @DisplayName("CreateCondition returns singleton")
        void singleton() {
            assertSame(SelectedViewCondition.CreateCondition(), SelectedViewCondition.CreateCondition());
        }

        @Test
        @DisplayName("save produces no child elements")
        void saveEmpty() {
            XMLElement root = new XMLElement();
            root.setName("test");
            SelectedViewCondition.CreateCondition().save(root);
            assertTrue(root.getChildren().isEmpty());
        }

        @Test
        @DisplayName("toString returns non-null string")
        void toStringNotNull() {
            assertNotNull(SelectedViewCondition.CreateCondition().toString());
        }
    }

    // =========================================================================
    // ConditionFactory
    // =========================================================================
    @Nested
    @DisplayName("ConditionFactory")
    class ConditionFactoryTests {
        @Test
        @DisplayName("createCondition for each comparison operator")
        void createNodeCompareConditions() {
            NamedObject[] ops = {FILTER_IS_EQUAL_TO, FILTER_IS_NOT_EQUAL_TO, FILTER_GT, FILTER_GE, FILTER_LT, FILTER_LE};
            for (NamedObject op : ops) {
                assertNotNull(factory.createCondition(FILTER_NODE, op, "val", false), "failed for: " + op.getName());
            }
        }

        @Test
        @DisplayName("createCondition returns null for unknown attribute")
        void createUnknownAttribute() {
            assertNull(factory.createCondition(NamedObject.literal("unknown"), FILTER_CONTAINS, "val", false));
        }

        @Test
        @DisplayName("createAttributeCondition for FILTER_EXIST")
        void createAttrExists() {
            assertTrue(factory.createAttributeCondition("attr", FILTER_EXIST, null, false) instanceof AttributeExistsCondition);
        }

        @Test
        @DisplayName("createAttributeCondition for FILTER_DOES_NOT_EXIST")
        void createAttrNotExists() {
            assertTrue(factory.createAttributeCondition("attr", FILTER_DOES_NOT_EXIST, null, false) instanceof AttributeNotExistsCondition);
        }

        @Test
        @DisplayName("createAttributeCondition for each comparison operator")
        void createAttrCompareConditions() {
            NamedObject[] ops = {FILTER_IS_EQUAL_TO, FILTER_IS_NOT_EQUAL_TO, FILTER_GT, FILTER_GE, FILTER_LT, FILTER_LE};
            for (NamedObject op : ops) {
                assertTrue(factory.createAttributeCondition("attr", op, "val", false) instanceof AttributeCompareCondition);
            }
        }

        @Test
        @DisplayName("getNodeConditionNames returns 7 operators")
        void nodeConditionNames() {
            assertEquals(7, factory.getNodeConditionNames().length);
        }

        @Test
        @DisplayName("getIconConditionNames returns 2 operators")
        void iconConditionNames() {
            assertEquals(2, factory.getIconConditionNames().length);
        }

        @Test
        @DisplayName("getAttributeConditionNames returns 8 operators")
        void attributeConditionNames() {
            assertEquals(8, factory.getAttributeConditionNames().length);
        }

        @Test
        @DisplayName("loadCondition for each condition type")
        void loadAllConditionTypes() {
            XMLElement e1 = new XMLElement(); e1.setName("node_contains_condition"); e1.setAttribute("value", "test");
            assertNotNull(factory.loadCondition(e1));

            XMLElement e2 = new XMLElement(); e2.setName("ignore_case_node_contains_condition"); e2.setAttribute("value", "test");
            assertNotNull(factory.loadCondition(e2));

            XMLElement e3 = new XMLElement(); e3.setName("node_compare_condition");
            e3.setAttribute("value", "test"); e3.setAttribute("ignore_case", "false");
            e3.setIntAttribute("comparation_result", 0); e3.setAttribute("succeed", "true");
            assertNotNull(factory.loadCondition(e3));

            XMLElement e4 = new XMLElement(); e4.setName("icon_contained_condition"); e4.setAttribute("icon", "bk");
            assertNotNull(factory.loadCondition(e4));

            XMLElement e5 = new XMLElement(); e5.setName("icon_not_contained_condition"); e5.setAttribute("icon", "bk");
            assertNotNull(factory.loadCondition(e5));

            XMLElement e6 = new XMLElement(); e6.setName("attribute_compare_condition");
            e6.setAttribute("attribute", "a"); e6.setAttribute("value", "v"); e6.setAttribute("ignore_case", "false");
            e6.setIntAttribute("comparation_result", 0); e6.setAttribute("succeed", "true");
            assertNotNull(factory.loadCondition(e6));

            XMLElement e7 = new XMLElement(); e7.setName("attribute_exists_condition"); e7.setAttribute("attribute", "a");
            assertNotNull(factory.loadCondition(e7));

            XMLElement e8 = new XMLElement(); e8.setName("attribute_not_exists_condition"); e8.setAttribute("attribute", "a");
            assertNotNull(factory.loadCondition(e8));
        }

        @Test
        @DisplayName("loadCondition for negate_condition")
        void loadNegateCondition() {
            XMLElement root = new XMLElement(); root.setName("negate_condition");
            XMLElement inner = new XMLElement(); inner.setName("node_contains_condition"); inner.setAttribute("value", "t");
            root.addChild(inner);
            assertNotNull(factory.loadCondition(root));
        }

        @Test
        @DisplayName("loadCondition for conjunct_condition")
        void loadConjunctCondition() {
            XMLElement root = new XMLElement(); root.setName("conjunct_condition");
            XMLElement c1 = new XMLElement(); c1.setName("node_contains_condition"); c1.setAttribute("value", "a");
            XMLElement c2 = new XMLElement(); c2.setName("node_contains_condition"); c2.setAttribute("value", "b");
            root.addChild(c1); root.addChild(c2);
            assertNotNull(factory.loadCondition(root));
        }

        @Test
        @DisplayName("loadCondition for disjunct_condition")
        void loadDisjunctCondition() {
            XMLElement root = new XMLElement(); root.setName("disjunct_condition");
            XMLElement c1 = new XMLElement(); c1.setName("node_contains_condition"); c1.setAttribute("value", "a");
            XMLElement c2 = new XMLElement(); c2.setName("node_contains_condition"); c2.setAttribute("value", "b");
            root.addChild(c1); root.addChild(c2);
            assertNotNull(factory.loadCondition(root));
        }

        @Test
        @DisplayName("loadCondition returns null for unknown condition name")
        void loadUnknownCondition() {
            XMLElement e = new XMLElement(); e.setName("unknown_condition_type");
            assertNull(factory.loadCondition(e));
        }
    }

    // =========================================================================
    // Complex / Integration scenarios
    // =========================================================================
    @Nested
    @DisplayName("Complex Filter Scenarios")
    class ComplexFilterTests {
        @Test
        @DisplayName("NOT(AND(contains 'a', contains 'b'))")
        void notAndCombination() {
            Condition and = new ConjunctConditions(new Object[]{
                    factory.createCondition(FILTER_NODE, FILTER_CONTAINS, "a", false),
                    factory.createCondition(FILTER_NODE, FILTER_CONTAINS, "b", false)});
            Condition notAnd = new ConditionNotSatisfiedDecorator(and);
            assertTrue(notAnd.checkNode(null, createNode("only a")));
            assertFalse(notAnd.checkNode(null, createNode("a and b")));
        }

        @Test
        @DisplayName("OR(AND(x,y), z) combination")
        void orAndCombination() {
            Condition andXY = new ConjunctConditions(new Object[]{
                    factory.createCondition(FILTER_NODE, FILTER_CONTAINS, "xxx", false),
                    factory.createCondition(FILTER_NODE, FILTER_CONTAINS, "yyy", false)});
            Condition or = new DisjunctConditions(new Object[]{andXY,
                    factory.createCondition(FILTER_NODE, FILTER_CONTAINS, "zzz", false)});
            assertTrue(or.checkNode(null, createNode("xxx yyy")));
            assertTrue(or.checkNode(null, createNode("zzz here")));
            assertFalse(or.checkNode(null, createNode("xxx alone")));
        }

        @Test
        @DisplayName("nested NOT(NOT(condition)) round-trip through XML")
        void nestedNotRoundTrip() {
            Condition inner = factory.createCondition(FILTER_NODE, FILTER_CONTAINS, "deep", false);
            Condition notNot = new ConditionNotSatisfiedDecorator(new ConditionNotSatisfiedDecorator(inner));
            Condition loaded = factory.loadCondition(saveCondition(notNot));
            MindMapNode node = createNode("deep test");
            assertEquals(inner.checkNode(null, node), loaded.checkNode(null, node));
        }

        @Test
        @DisplayName("complex round-trip: OR(node contains, icon contained)")
        void complexRoundTrip() {
            Condition or = new DisjunctConditions(new Object[]{
                    factory.createCondition(FILTER_NODE, FILTER_CONTAINS, "special", false),
                    new IconContainedCondition("button_ok")});
            Condition loaded = factory.loadCondition(saveCondition(or));
            assertTrue(loaded.checkNode(null, createNode("special text")));
            assertTrue(loaded.checkNode(null, createNodeWithIcon("other", "button_ok")));
            assertFalse(loaded.checkNode(null, createNode("plain")));
        }

        @Test
        @DisplayName("attribute LT/LE/GE with numeric values")
        void attributeNumericComparison() {
            Condition lt = factory.createAttributeCondition("score", FILTER_LT, "10", false);
            Condition le = factory.createAttributeCondition("score", FILTER_LE, "10", false);
            Condition ge = factory.createAttributeCondition("score", FILTER_GE, "10", false);
            assertTrue(lt.checkNode(null, createNodeWithAttribute("n", "score", "5")));
            assertFalse(lt.checkNode(null, createNodeWithAttribute("n", "score", "10")));
            assertTrue(le.checkNode(null, createNodeWithAttribute("n", "score", "10")));
            assertFalse(le.checkNode(null, createNodeWithAttribute("n", "score", "15")));
            assertTrue(ge.checkNode(null, createNodeWithAttribute("n", "score", "10")));
            assertFalse(ge.checkNode(null, createNodeWithAttribute("n", "score", "5")));
        }
    }
}
