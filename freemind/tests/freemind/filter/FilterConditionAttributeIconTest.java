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

@DisplayName("Filter Conditions — Attribute and Icon")
class FilterConditionAttributeIconTest {

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
    // AttributeCompareCondition
    // =========================================================================
    @Nested
    @DisplayName("AttributeCompareCondition")
    class AttributeCompareConditionTests {
        @Test
        @DisplayName("IS_EQUAL_TO matches attribute with equal value")
        void equalTo() {
            Condition cond = factory.createAttributeCondition("priority", FILTER_IS_EQUAL_TO, "high", false);
            assertTrue(cond.checkNode(null, createNodeWithAttribute("node", "priority", "high")));
            assertFalse(cond.checkNode(null, createNodeWithAttribute("node", "priority", "low")));
        }

        @Test
        @DisplayName("returns false when attribute name does not match")
        void wrongAttributeName() {
            Condition cond = factory.createAttributeCondition("priority", FILTER_IS_EQUAL_TO, "high", false);
            assertFalse(cond.checkNode(null, createNodeWithAttribute("node", "status", "high")));
        }

        @Test
        @DisplayName("returns false when node has no attributes")
        void noAttributes() {
            Condition cond = factory.createAttributeCondition("priority", FILTER_IS_EQUAL_TO, "high", false);
            assertFalse(cond.checkNode(null, createNode("plain node")));
        }

        @Test
        @DisplayName("GT compares attribute values numerically")
        void greaterThan() {
            Condition cond = factory.createAttributeCondition("score", FILTER_GT, "5", false);
            assertTrue(cond.checkNode(null, createNodeWithAttribute("node", "score", "10")));
            assertFalse(cond.checkNode(null, createNodeWithAttribute("node", "score", "3")));
        }

        @Test
        @DisplayName("save produces valid XML")
        void saveXml() {
            Condition cond = factory.createAttributeCondition("attr1", FILTER_IS_EQUAL_TO, "val1", false);
            XMLElement child = saveCondition(cond);
            assertEquals("attribute_compare_condition", child.getName());
            assertEquals("attr1", child.getStringAttribute("attribute"));
            assertEquals("val1", child.getStringAttribute("value"));
        }

        @Test
        @DisplayName("round-trip preserves behavior")
        void roundTrip() {
            Condition original = factory.createAttributeCondition("color", FILTER_IS_EQUAL_TO, "red", false);
            XMLElement saved = saveCondition(original);
            Condition loaded = factory.loadCondition(saved);
            assertNotNull(loaded);
            assertTrue(loaded.checkNode(null, createNodeWithAttribute("n", "color", "red")));
            assertFalse(loaded.checkNode(null, createNodeWithAttribute("n", "color", "blue")));
        }

        @Test
        @DisplayName("createDesctiption returns non-null string")
        void descriptionNotNull() {
            Condition cond = new AttributeCompareCondition("attr", "val", false, 0, true);
            assertNotNull(cond.toString());
        }
    }

    // =========================================================================
    // AttributeExistsCondition
    // =========================================================================
    @Nested
    @DisplayName("AttributeExistsCondition")
    class AttributeExistsConditionTests {
        @Test
        @DisplayName("checkNode returns true when attribute exists")
        void attributeExists() {
            Condition cond = factory.createAttributeCondition("priority", FILTER_EXIST, null, false);
            assertTrue(cond.checkNode(null, createNodeWithAttribute("node", "priority", "high")));
        }

        @Test
        @DisplayName("checkNode returns false when attribute does not exist")
        void attributeNotExists() {
            Condition cond = factory.createAttributeCondition("priority", FILTER_EXIST, null, false);
            assertFalse(cond.checkNode(null, createNode("plain node")));
        }

        @Test
        @DisplayName("save produces valid XML")
        void saveXml() {
            Condition cond = new AttributeExistsCondition("myattr");
            XMLElement child = saveCondition(cond);
            assertEquals("attribute_exists_condition", child.getName());
            assertEquals("myattr", child.getStringAttribute("attribute"));
        }

        @Test
        @DisplayName("round-trip preserves behavior")
        void roundTrip() {
            Condition original = new AttributeExistsCondition("testattr");
            XMLElement saved = saveCondition(original);
            Condition loaded = factory.loadCondition(saved);
            assertNotNull(loaded);
            assertTrue(loaded.checkNode(null, createNodeWithAttribute("n", "testattr", "v")));
            assertFalse(loaded.checkNode(null, createNode("plain")));
        }

        @Test
        @DisplayName("createDesctiption returns non-null string")
        void descriptionNotNull() {
            assertNotNull(new AttributeExistsCondition("attr").toString());
        }
    }

    // =========================================================================
    // AttributeNotExistsCondition
    // =========================================================================
    @Nested
    @DisplayName("AttributeNotExistsCondition")
    class AttributeNotExistsConditionTests {
        @Test
        @DisplayName("factory creates correct type for FILTER_DOES_NOT_EXIST")
        void factoryCreatesCorrectType() {
            Condition cond = factory.createAttributeCondition("priority", FILTER_DOES_NOT_EXIST, null, false);
            assertNotNull(cond);
            assertTrue(cond instanceof AttributeNotExistsCondition);
        }

        @Test
        @DisplayName("save produces valid XML")
        void saveXml() {
            Condition cond = new AttributeNotExistsCondition("myattr");
            XMLElement child = saveCondition(cond);
            assertEquals("attribute_not_exists_condition", child.getName());
            assertEquals("myattr", child.getStringAttribute("attribute"));
        }

        @Test
        @DisplayName("round-trip preserves behavior")
        void roundTrip() {
            Condition original = new AttributeNotExistsCondition("testattr");
            XMLElement saved = saveCondition(original);
            Condition loaded = factory.loadCondition(saved);
            assertNotNull(loaded);
            assertEquals(original.checkNode(null, createNodeWithAttribute("n", "testattr", "v")),
                    loaded.checkNode(null, createNodeWithAttribute("n", "testattr", "v")));
        }

        @Test
        @DisplayName("createDesctiption returns non-null string")
        void descriptionNotNull() {
            assertNotNull(new AttributeNotExistsCondition("attr").toString());
        }
    }

    // =========================================================================
    // IconContainedCondition
    // =========================================================================
    @Nested
    @DisplayName("IconContainedCondition")
    class IconContainedConditionTests {
        @Test
        @DisplayName("checkNode returns true when icon is present")
        void iconPresent() {
            assertTrue(new IconContainedCondition("button_ok").checkNode(null, createNodeWithIcon("node", "button_ok")));
        }

        @Test
        @DisplayName("checkNode returns false when icon is not present")
        void iconNotPresent() {
            assertFalse(new IconContainedCondition("button_ok").checkNode(null, createNode("no icon")));
        }

        @Test
        @DisplayName("checkNode returns false when different icon is present")
        void differentIcon() {
            assertFalse(new IconContainedCondition("button_ok").checkNode(null, createNodeWithIcon("node", "button_cancel")));
        }

        @Test
        @DisplayName("factory creates correct type")
        void factoryCreation() {
            Condition cond = factory.createCondition(FILTER_ICON, FILTER_CONTAINS, "bookmark", false);
            assertTrue(cond instanceof IconContainedCondition);
        }

        @Test
        @DisplayName("save produces valid XML")
        void saveXml() {
            XMLElement child = saveCondition(new IconContainedCondition("idea"));
            assertEquals("icon_contained_condition", child.getName());
            assertEquals("idea", child.getStringAttribute("icon"));
        }

        @Test
        @DisplayName("round-trip preserves behavior")
        void roundTrip() {
            Condition original = new IconContainedCondition("help");
            Condition loaded = factory.loadCondition(saveCondition(original));
            assertNotNull(loaded);
            assertTrue(loaded.checkNode(null, createNodeWithIcon("n", "help")));
            assertFalse(loaded.checkNode(null, createNode("plain")));
        }
    }

    // =========================================================================
    // IconNotContainedCondition
    // =========================================================================
    @Nested
    @DisplayName("IconNotContainedCondition")
    class IconNotContainedConditionTests {
        @Test
        @DisplayName("checkNode returns true when icon is NOT present")
        void iconNotPresent() {
            assertTrue(new IconNotContainedCondition("button_ok").checkNode(null, createNode("no icon")));
        }

        @Test
        @DisplayName("checkNode returns false when icon IS present")
        void iconPresent() {
            assertFalse(new IconNotContainedCondition("button_ok").checkNode(null, createNodeWithIcon("node", "button_ok")));
        }

        @Test
        @DisplayName("factory creates correct type")
        void factoryCreation() {
            assertTrue(factory.createCondition(FILTER_ICON, FILTER_NOT_CONTAINS, "bookmark", false) instanceof IconNotContainedCondition);
        }

        @Test
        @DisplayName("save produces valid XML")
        void saveXml() {
            XMLElement child = saveCondition(new IconNotContainedCondition("idea"));
            assertEquals("icon_not_contained_condition", child.getName());
            assertEquals("idea", child.getStringAttribute("icon"));
        }

        @Test
        @DisplayName("round-trip preserves behavior")
        void roundTrip() {
            Condition original = new IconNotContainedCondition("stop");
            Condition loaded = factory.loadCondition(saveCondition(original));
            assertNotNull(loaded);
            assertFalse(loaded.checkNode(null, createNodeWithIcon("n", "stop")));
            assertTrue(loaded.checkNode(null, createNode("plain")));
        }
    }
}
