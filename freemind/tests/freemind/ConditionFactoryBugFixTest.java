package tests.freemind;

import freemind.controller.filter.condition.ConditionFactory;
import freemind.main.HeadlessFreeMind;
import freemind.common.NamedObject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Regression tests for Phase D correctness bug fix:
 * ConditionFactory.FILTER_EXIST and FILTER_DOES_NOT_EXIST were declared
 * as String but compared with NamedObject.equals() — always returned false.
 *
 * Fixed by changing declarations from String to NamedObject.
 */
@DisplayName("ConditionFactory FILTER_EXIST Bug Fix")
class ConditionFactoryBugFixTest {

    @BeforeAll
    static void ensureResources() {
        new HeadlessFreeMind();
    }

    @Test
    @DisplayName("FILTER_EXIST is NamedObject, not String")
    void filterExistIsNamedObject() throws Exception {
        Field field = ConditionFactory.class.getDeclaredField("FILTER_EXIST");
        field.setAccessible(true);
        Object value = field.get(null);
        assertTrue(value instanceof NamedObject,
            "FILTER_EXIST must be NamedObject for equals() comparison, found: " +
            value.getClass().getSimpleName());
    }

    @Test
    @DisplayName("FILTER_DOES_NOT_EXIST is NamedObject, not String")
    void filterDoesNotExistIsNamedObject() throws Exception {
        Field field = ConditionFactory.class.getDeclaredField("FILTER_DOES_NOT_EXIST");
        field.setAccessible(true);
        Object value = field.get(null);
        assertTrue(value instanceof NamedObject,
            "FILTER_DOES_NOT_EXIST must be NamedObject, found: " +
            value.getClass().getSimpleName());
    }

    @Test
    @DisplayName("FILTER_EXIST equals itself (identity check for NamedObject)")
    void filterExistEqualsItself() throws Exception {
        Field field = ConditionFactory.class.getDeclaredField("FILTER_EXIST");
        field.setAccessible(true);
        Object value = field.get(null);
        assertTrue(value.equals(value),
            "FILTER_EXIST must equal itself");
    }

    @Test
    @DisplayName("FILTER_EXIST has non-null, non-empty translated name")
    void filterExistHasTranslation() throws Exception {
        Field field = ConditionFactory.class.getDeclaredField("FILTER_EXIST");
        field.setAccessible(true);
        Object value = field.get(null);
        String text = value.toString();
        assertNotNull(text, "FILTER_EXIST.toString() must not be null");
        assertFalse(text.isEmpty(), "FILTER_EXIST.toString() must not be empty");
    }

    @Test
    @DisplayName("FILTER_DOES_NOT_EXIST has non-null, non-empty translated name")
    void filterDoesNotExistHasTranslation() throws Exception {
        Field field = ConditionFactory.class.getDeclaredField("FILTER_DOES_NOT_EXIST");
        field.setAccessible(true);
        Object value = field.get(null);
        String text = value.toString();
        assertNotNull(text, "FILTER_DOES_NOT_EXIST.toString() must not be null");
        assertFalse(text.isEmpty(), "FILTER_DOES_NOT_EXIST.toString() must not be empty");
    }

    @Test
    @DisplayName("getAttributeConditionNames() includes FILTER_EXIST and FILTER_DOES_NOT_EXIST")
    void attributeConditionNamesIncludeExistFilters() throws Exception {
        ConditionFactory factory = new ConditionFactory();
        NamedObject[] names = factory.getAttributeConditionNames();

        Field existField = ConditionFactory.class.getDeclaredField("FILTER_EXIST");
        existField.setAccessible(true);
        Object filterExist = existField.get(null);

        Field notExistField = ConditionFactory.class.getDeclaredField("FILTER_DOES_NOT_EXIST");
        notExistField.setAccessible(true);
        Object filterDoesNotExist = notExistField.get(null);

        boolean foundExist = false;
        boolean foundNotExist = false;
        for (NamedObject name : names) {
            if (name.equals(filterExist)) foundExist = true;
            if (name.equals(filterDoesNotExist)) foundNotExist = true;
        }

        assertTrue(foundExist,
            "getAttributeConditionNames() must include FILTER_EXIST as NamedObject");
        assertTrue(foundNotExist,
            "getAttributeConditionNames() must include FILTER_DOES_NOT_EXIST as NamedObject");
    }
}
