package tests.freemind.plugins;

import freemind.extensions.ExportHook;
import freemind.extensions.HookAdapter;
import freemind.extensions.ImportWizard;
import freemind.extensions.MindMapHook;
import freemind.extensions.ModeControllerHook;
import freemind.extensions.ModeControllerHookAdapter;
import freemind.extensions.NodeHook;
import freemind.extensions.NodeHookAdapter;
import freemind.extensions.PermanentNodeHook;
import freemind.main.HeadlessFreeMind;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.Writer;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

import javax.xml.parsers.DocumentBuilderFactory;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the FreeMind hook/plugin framework: class hierarchy,
 * interface contracts, and plugin descriptor validation.
 */
@DisplayName("Hook Framework")
class HookFrameworkTest {

    @BeforeAll
    static void ensureResources() {
        new HeadlessFreeMind();
    }

    // ── HookAdapter structural tests ──

    @Test
    @DisplayName("HookAdapter implements MindMapHook interface")
    void hookAdapterImplementsMindMapHook() {
        assertTrue(MindMapHook.class.isAssignableFrom(HookAdapter.class),
            "HookAdapter must implement MindMapHook");
    }

    @Test
    @DisplayName("HookAdapter is a concrete class (not abstract)")
    void hookAdapterIsConcrete() {
        assertFalse(Modifier.isAbstract(HookAdapter.class.getModifiers()),
            "HookAdapter must be concrete");
    }

    @Test
    @DisplayName("HookAdapter has a no-arg constructor")
    void hookAdapterHasNoArgConstructor() {
        assertDoesNotThrow(() -> HookAdapter.class.getDeclaredConstructor(),
            "HookAdapter must have a no-arg constructor");
    }

    @Test
    @DisplayName("HookAdapter declares all MindMapHook methods")
    void hookAdapterDeclaresAllMindMapHookMethods() {
        Set<String> hookMethods = Set.of(
            "getName", "setName", "setProperties", "getResourceString",
            "setController", "getPluginBaseClass", "setPluginBaseClass",
            "startupMapHook", "shutdownMapHook"
        );
        Set<String> adapterMethods = Arrays.stream(HookAdapter.class.getDeclaredMethods())
            .map(Method::getName)
            .collect(Collectors.toSet());
        for (String method : hookMethods) {
            assertTrue(adapterMethods.contains(method),
                "HookAdapter must declare method: " + method);
        }
    }

    // ── NodeHookAdapter tests ──

    @Test
    @DisplayName("NodeHookAdapter extends HookAdapter")
    void nodeHookAdapterExtendsHookAdapter() {
        assertTrue(HookAdapter.class.isAssignableFrom(NodeHookAdapter.class),
            "NodeHookAdapter must extend HookAdapter");
    }

    @Test
    @DisplayName("NodeHookAdapter implements NodeHook interface")
    void nodeHookAdapterImplementsNodeHook() {
        assertTrue(NodeHook.class.isAssignableFrom(NodeHookAdapter.class),
            "NodeHookAdapter must implement NodeHook");
    }

    @Test
    @DisplayName("NodeHookAdapter is abstract")
    void nodeHookAdapterIsAbstract() {
        assertTrue(Modifier.isAbstract(NodeHookAdapter.class.getModifiers()),
            "NodeHookAdapter must be abstract");
    }

    @Test
    @DisplayName("NodeHookAdapter has setNode, getNode, setMap, invoke methods")
    void nodeHookAdapterHasExpectedMethods() {
        Set<String> expected = Set.of("setNode", "getNode", "setMap", "invoke", "getMap");
        Set<String> actual = Arrays.stream(NodeHookAdapter.class.getDeclaredMethods())
            .map(Method::getName)
            .collect(Collectors.toSet());
        for (String method : expected) {
            assertTrue(actual.contains(method),
                "NodeHookAdapter must declare method: " + method);
        }
    }

    // ── ModeControllerHookAdapter tests ──

    @Test
    @DisplayName("ModeControllerHookAdapter extends HookAdapter")
    void modeControllerHookAdapterExtendsHookAdapter() {
        assertTrue(HookAdapter.class.isAssignableFrom(ModeControllerHookAdapter.class),
            "ModeControllerHookAdapter must extend HookAdapter");
    }

    @Test
    @DisplayName("ModeControllerHookAdapter implements ModeControllerHook")
    void modeControllerHookAdapterImplementsModeControllerHook() {
        assertTrue(ModeControllerHook.class.isAssignableFrom(ModeControllerHookAdapter.class),
            "ModeControllerHookAdapter must implement ModeControllerHook");
    }

    @Test
    @DisplayName("ModeControllerHookAdapter is concrete (not abstract)")
    void modeControllerHookAdapterIsConcrete() {
        assertFalse(Modifier.isAbstract(ModeControllerHookAdapter.class.getModifiers()),
            "ModeControllerHookAdapter must be concrete");
    }

    // ── ExportHook tests ──

    @Test
    @DisplayName("ExportHook extends ModeControllerHookAdapter")
    void exportHookExtendsModeControllerHookAdapter() {
        assertTrue(ModeControllerHookAdapter.class.isAssignableFrom(ExportHook.class),
            "ExportHook must extend ModeControllerHookAdapter");
    }

    @Test
    @DisplayName("ExportHook transitively implements MindMapHook")
    void exportHookImplementsMindMapHook() {
        assertTrue(MindMapHook.class.isAssignableFrom(ExportHook.class),
            "ExportHook must transitively implement MindMapHook");
    }

    @Test
    @DisplayName("ExportHook has static chooseImageFile method")
    void exportHookHasChooseImageFileMethod() throws Exception {
        Method method = ExportHook.class.getDeclaredMethod(
            "chooseImageFile", String.class, String.class, String.class,
            freemind.modes.ModeController.class);
        assertTrue(Modifier.isStatic(method.getModifiers()),
            "chooseImageFile must be static");
        assertTrue(Modifier.isPublic(method.getModifiers()),
            "chooseImageFile must be public");
    }

    // ── ImportWizard tests ──

    @Test
    @DisplayName("ImportWizard has buildClassList method")
    void importWizardHasBuildClassListMethod() {
        assertDoesNotThrow(() -> ImportWizard.class.getDeclaredMethod("buildClassList"),
            "ImportWizard must have buildClassList method");
    }

    @Test
    @DisplayName("ImportWizard has addClassesFromZip method")
    void importWizardHasAddClassesFromZipMethod() {
        assertDoesNotThrow(
            () -> ImportWizard.class.getDeclaredMethod(
                "addClassesFromZip", java.util.Vector.class, File.class),
            "ImportWizard must have addClassesFromZip method");
    }

    @Test
    @DisplayName("ImportWizard has addClassesFromDir method")
    void importWizardHasAddClassesFromDirMethod() {
        assertDoesNotThrow(
            () -> ImportWizard.class.getDeclaredMethod(
                "addClassesFromDir", java.util.Vector.class, File.class, File.class, int.class),
            "ImportWizard must have addClassesFromDir method");
    }

    @Test
    @DisplayName("ImportWizard lookFor field is .xml")
    void importWizardLookForFieldIsXml() {
        ImportWizard wizard = new ImportWizard();
        assertEquals(".xml", wizard.lookFor,
            "ImportWizard.lookFor must be .xml");
    }

    // ── PermanentNodeHook interface tests ──

    @Test
    @DisplayName("PermanentNodeHook extends NodeHook")
    void permanentNodeHookExtendsNodeHook() {
        assertTrue(NodeHook.class.isAssignableFrom(PermanentNodeHook.class),
            "PermanentNodeHook must extend NodeHook");
    }

    @Test
    @DisplayName("PermanentNodeHook has save and loadFrom methods")
    void permanentNodeHookHasSaveAndLoadMethods() {
        Set<String> methods = Arrays.stream(PermanentNodeHook.class.getDeclaredMethods())
            .map(Method::getName)
            .collect(Collectors.toSet());
        assertTrue(methods.contains("save"), "PermanentNodeHook must declare save");
        assertTrue(methods.contains("loadFrom"), "PermanentNodeHook must declare loadFrom");
    }

    @Test
    @DisplayName("PermanentNodeHook has lifecycle callback methods")
    void permanentNodeHookHasLifecycleCallbacks() {
        Set<String> expected = Set.of(
            "onFocusNode", "onLostFocusNode", "onViewCreatedHook", "onViewRemovedHook",
            "onUpdateNodeHook", "onAddChild", "onNewChild", "onRemoveChild",
            "onUpdateChildrenHook", "saveHtml"
        );
        Set<String> actual = Arrays.stream(PermanentNodeHook.class.getDeclaredMethods())
            .map(Method::getName)
            .collect(Collectors.toSet());
        for (String method : expected) {
            assertTrue(actual.contains(method),
                "PermanentNodeHook must declare: " + method);
        }
    }

    // ── MindMapHook interface tests ──

    @Test
    @DisplayName("MindMapHook is an interface")
    void mindMapHookIsInterface() {
        assertTrue(MindMapHook.class.isInterface(),
            "MindMapHook must be an interface");
    }

    @Test
    @DisplayName("MindMapHook declares getName, setController, startup, shutdown")
    void mindMapHookDeclaresExpectedMethods() {
        Set<String> expected = Set.of(
            "getName", "setName", "setController", "startupMapHook", "shutdownMapHook"
        );
        Set<String> actual = Arrays.stream(MindMapHook.class.getDeclaredMethods())
            .map(Method::getName)
            .collect(Collectors.toSet());
        for (String method : expected) {
            assertTrue(actual.contains(method),
                "MindMapHook must declare: " + method);
        }
    }

    @Test
    @DisplayName("MindMapHook contains PluginBaseClassSearcher inner interface")
    void mindMapHookHasPluginBaseClassSearcherInnerInterface() {
        boolean found = false;
        for (Class<?> inner : MindMapHook.class.getDeclaredClasses()) {
            if (inner.getSimpleName().equals("PluginBaseClassSearcher") && inner.isInterface()) {
                found = true;
            }
        }
        assertTrue(found, "MindMapHook must contain PluginBaseClassSearcher inner interface");
    }

    // ── Plugin XML descriptor validation ──

    @Test
    @DisplayName("Plugin XML descriptors in accessories/plugins/ are well-formed XML")
    void pluginXmlDescriptorsAreWellFormedXml() {
        File pluginDir = new File("accessories/plugins");
        assertTrue(pluginDir.isDirectory(), "accessories/plugins directory must exist");
        File[] xmlFiles = pluginDir.listFiles((dir, name) ->
            name.endsWith(".xml") && !name.equals("build.xml"));
        assertNotNull(xmlFiles, "Must be able to list plugin XML files");
        assertTrue(xmlFiles.length > 0, "At least one plugin XML descriptor must exist");

        var factory = DocumentBuilderFactory.newInstance();
        int validCount = 0;
        for (File xmlFile : xmlFiles) {
            assertDoesNotThrow(() -> factory.newDocumentBuilder().parse(xmlFile),
                "Plugin XML must be well-formed: " + xmlFile.getName());
            validCount++;
        }
        assertTrue(validCount >= 10,
            "Expected at least 10 plugin XML descriptors, found " + validCount);
    }

    @Test
    @DisplayName("Plugin XML descriptors in plugins/ directory are well-formed XML")
    void pluginXmlDescriptorsInPluginsDirAreWellFormedXml() {
        File pluginDir = new File("plugins");
        assertTrue(pluginDir.isDirectory(), "plugins directory must exist");
        File[] xmlFiles = pluginDir.listFiles((dir, name) ->
            name.endsWith(".xml") && !name.startsWith("build"));
        assertNotNull(xmlFiles, "Must be able to list plugin XML files");
        assertTrue(xmlFiles.length > 0, "At least one plugin XML descriptor must exist");

        var factory = DocumentBuilderFactory.newInstance();
        for (File xmlFile : xmlFiles) {
            assertDoesNotThrow(() -> factory.newDocumentBuilder().parse(xmlFile),
                "Plugin XML must be well-formed: " + xmlFile.getName());
        }
    }

    // ── Hook registration / plugin directory structure ──

    @Test
    @DisplayName("Plugin directories accessories/plugins and plugins/ exist")
    void pluginDirectoriesExist() {
        assertTrue(new File("accessories/plugins").isDirectory(),
            "freemind/accessories/plugins directory must exist");
        assertTrue(new File("plugins").isDirectory(),
            "freemind/plugins directory must exist");
    }
}
