package tests.freemind;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Verifies Phase D null guard fixes in Controller.java and
 * Phase G FilterComposerDialog setVisible() refactor.
 */
@DisplayName("Controller Null Guards & API Migration")
class ControllerNullGuardTest {

    @Test
    @DisplayName("DocumentationAction has null guard for browsemode_initial_map resource")
    void documentationActionHasNullGuard() throws IOException {
        Path file = findProjectRoot()
            .resolve("freemind/freemind/controller/Controller.java");
        String content = Files.readString(file);

        // The DocumentationAction inner class must check for null before new File(map)
        assertTrue(content.contains("if (map == null)"),
            "DocumentationAction must have null guard for map resource string");
        assertTrue(content.contains("browsemode_initial_map resource not configured") ||
                   content.contains("browsemode_initial_map not configured"),
            "DocumentationAction must log warning when resource is null");
    }

    @Test
    @DisplayName("KeyDocumentationAction has null guard for pdfKeyDocLocation resource")
    void keyDocumentationActionHasNullGuard() throws IOException {
        Path file = findProjectRoot()
            .resolve("freemind/freemind/controller/Controller.java");
        String content = Files.readString(file);

        assertTrue(content.contains("if (urlText == null)"),
            "KeyDocumentationAction must have null guard for urlText resource string");
        assertTrue(content.contains("pdfKeyDocLocation resource not configured") ||
                   content.contains("pdfKeyDocLocation not configured"),
            "KeyDocumentationAction must log warning when resource is null");
    }

    @Test
    @DisplayName("FreeMindMapController has null check for getReverseLookup result")
    void freeMindMapControllerReverseLookupNullCheck() throws IOException {
        Path file = findProjectRoot()
            .resolve("freemind/plugins/map/FreeMindMapController.java");
        String content = Files.readString(file);

        assertTrue(content.contains("reverseLookup != null"),
            "FreeMindMapController must null-check getReverseLookup() result before calling getListResultList()");
    }

    @Test
    @DisplayName("ClonePasteAction uses correct String key for mCloneIdsMap.remove()")
    void clonePasteActionUsesCorrectRemoveKey() throws IOException {
        Path file = findProjectRoot()
            .resolve("freemind/accessories/plugins/ClonePasteAction.java");
        String content = Files.readString(file);

        assertTrue(content.contains("mCloneIdsMap.remove(pCloneId)"),
            "ClonePasteAction must use pCloneId (String) key, not cloneSet (HashSet)");
        assertFalse(content.contains("mCloneIdsMap.remove(cloneSet)"),
            "ClonePasteAction must NOT pass HashSet to HashMap.remove()");
    }

    @Test
    @DisplayName("FilterComposerDialog overrides setVisible(boolean), not show()")
    void filterComposerDialogUsesSetVisible() throws IOException {
        Path file = findProjectRoot()
            .resolve("freemind/freemind/controller/filter/FilterComposerDialog.java");
        String content = Files.readString(file);

        assertTrue(content.contains("public void setVisible(boolean"),
            "FilterComposerDialog must override setVisible(boolean)");
        assertFalse(content.contains("public void show()"),
            "FilterComposerDialog must NOT override deprecated show()");
    }

    @Test
    @DisplayName("FilterToolbar calls setVisible(true), not show()")
    void filterToolbarCallsSetVisible() throws IOException {
        Path file = findProjectRoot()
            .resolve("freemind/freemind/controller/filter/FilterToolbar.java");
        String content = Files.readString(file);

        assertTrue(content.contains("getFilterDialog().setVisible(true)"),
            "FilterToolbar must call setVisible(true) not show()");
        assertFalse(content.contains("getFilterDialog().show()"),
            "FilterToolbar must NOT call deprecated show()");
    }

    @Test
    @DisplayName("ModesCreator uses getDeclaredConstructor().newInstance()")
    void modesCreatorUsesModernReflection() throws IOException {
        Path file = findProjectRoot()
            .resolve("freemind/freemind/modes/ModesCreator.java");
        String content = Files.readString(file);

        assertTrue(content.contains("getDeclaredConstructor().newInstance()"),
            "ModesCreator must use getDeclaredConstructor().newInstance()");
        assertFalse(content.contains("Class.forName(modeName).newInstance()"),
            "ModesCreator must NOT use deprecated Class.newInstance()");
    }

    @Test
    @DisplayName("MindMapHookFactory uses getDeclaredConstructor().newInstance()")
    void hookFactoryUsesModernReflection() throws IOException {
        Path file = findProjectRoot()
            .resolve("freemind/freemind/modes/mindmapmode/hooks/MindMapHookFactory.java");
        String content = Files.readString(file);

        assertTrue(content.contains("getDeclaredConstructor().newInstance()"),
            "MindMapHookFactory must use getDeclaredConstructor().newInstance()");
        assertFalse(content.contains("hookClass.newInstance()"),
            "MindMapHookFactory must NOT use deprecated Class.newInstance()");
    }

    @Test
    @DisplayName("MindMapHookFactory logs uncompiled plugins at FINE level, not WARNING")
    void hookFactoryLogsPluginsAtFineLevel() throws IOException {
        Path file = findProjectRoot()
            .resolve("freemind/freemind/modes/mindmapmode/hooks/MindMapHookFactory.java");
        String content = Files.readString(file);

        assertTrue(content.contains("logger.fine(\"Plugin registration class not found"),
            "MindMapHookFactory must log uncompiled plugins at FINE level");
        assertFalse(content.contains("logger.warning(\"Plugin registration class not found"),
            "MindMapHookFactory must NOT log uncompiled plugins at WARNING level");
    }

    private Path findProjectRoot() {
        Path current = Paths.get(System.getProperty("user.dir"));
        while (current != null) {
            if (Files.exists(current.resolve("settings.gradle.kts"))) {
                return current;
            }
            current = current.getParent();
        }
        return Paths.get(System.getProperty("user.dir"));
    }
}
