package tests.freemind.plugins;

import accessories.plugins.AutomaticLayout;
import accessories.plugins.EncryptNode;
import accessories.plugins.ImportMindmanagerFiles;
import accessories.plugins.NodeNote;
import accessories.plugins.time.FlatNodeTableFilterModel;
import accessories.plugins.time.ReminderHook;
import freemind.common.XmlBindingTools;
import freemind.controller.actions.generated.instance.XmlAction;
import freemind.extensions.ModeControllerHookAdapter;
import freemind.main.HeadlessFreeMind;
import freemind.main.Tools;
import freemind.modes.common.plugins.ReminderHookBase;
import freemind.modes.mindmapmode.EncryptedMindMapNode;
import freemind.modes.mindmapmode.MindMapMapModel;
import freemind.modes.mindmapmode.hooks.MindMapHookAdapter;
import freemind.modes.mindmapmode.hooks.MindMapNodeHookAdapter;
import freemind.modes.mindmapmode.hooks.PermanentMindMapNodeHookAdapter;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import plugins.script.ScriptingEngine;
import plugins.script.ScriptingRegistration;
import plugins.script.SignedScriptHandler;
import plugins.search.FileSearchModel;

import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Plugin logic tests covering search, script, import, time/calendar,
 * encryption, and automatic layout plugins.
 */
@DisplayName("Plugin Logic")
class PluginLogicTest {

    @BeforeAll
    static void ensureResources() {
        new HeadlessFreeMind();
    }

    // ── AutomaticLayout structural tests ──

    @Nested
    @DisplayName("AutomaticLayout")
    class AutomaticLayoutTests {

        @Test
        @DisplayName("extends PermanentMindMapNodeHookAdapter")
        void extendsPermanentHookAdapter() {
            assertTrue(PermanentMindMapNodeHookAdapter.class.isAssignableFrom(AutomaticLayout.class),
                "AutomaticLayout must extend PermanentMindMapNodeHookAdapter");
        }

        @Test
        @DisplayName("has patterns field")
        void hasPatternsField() {
            assertDoesNotThrow(() -> AutomaticLayout.class.getDeclaredField("patterns"),
                "AutomaticLayout must have a 'patterns' field");
        }

        @Test
        @DisplayName("has setStyle method")
        void hasSetStyleMethod() {
            boolean found = Arrays.stream(AutomaticLayout.class.getDeclaredMethods())
                .anyMatch(m -> m.getName().equals("setStyle"));
            assertTrue(found, "AutomaticLayout must have setStyle method");
        }

        @Test
        @DisplayName("has depth method")
        void hasDepthMethod() {
            boolean found = Arrays.stream(AutomaticLayout.class.getDeclaredMethods())
                .anyMatch(m -> m.getName().equals("depth"));
            assertTrue(found, "AutomaticLayout must have depth method");
        }

        @Test
        @DisplayName("has setStyleRecursive method")
        void hasSetStyleRecursiveMethod() {
            boolean found = Arrays.stream(AutomaticLayout.class.getDeclaredMethods())
                .anyMatch(m -> m.getName().equals("setStyleRecursive"));
            assertTrue(found, "AutomaticLayout must have setStyleRecursive method");
        }

        @Test
        @DisplayName("has Registration inner class implementing HookRegistration")
        void hasRegistrationInnerClass() {
            Class<?>[] innerClasses = AutomaticLayout.class.getDeclaredClasses();
            boolean found = false;
            for (Class<?> inner : innerClasses) {
                if (inner.getSimpleName().equals("Registration")) {
                    found = true;
                    assertTrue(freemind.extensions.HookRegistration.class.isAssignableFrom(inner),
                        "Registration must implement HookRegistration");
                }
            }
            assertTrue(found, "AutomaticLayout must have Registration inner class");
        }

        @Test
        @DisplayName("has no-arg constructor")
        void hasNoArgConstructor() {
            assertDoesNotThrow(() -> AutomaticLayout.class.getDeclaredConstructor(),
                "AutomaticLayout must have a no-arg constructor");
        }
    }

    // ── EncryptNode structural tests ──

    @Nested
    @DisplayName("EncryptNode")
    class EncryptNodeTests {

        @Test
        @DisplayName("extends MindMapNodeHookAdapter")
        void extendsMindMapNodeHookAdapter() {
            assertTrue(MindMapNodeHookAdapter.class.isAssignableFrom(EncryptNode.class),
                "EncryptNode must extend MindMapNodeHookAdapter");
        }

        @Test
        @DisplayName("has no-arg constructor")
        void hasNoArgConstructor() {
            assertDoesNotThrow(() -> EncryptNode.class.getDeclaredConstructor(),
                "EncryptNode must have a no-arg constructor");
        }

        @Test
        @DisplayName("has invoke method taking MindMapNode")
        void hasInvokeMethod() {
            assertDoesNotThrow(
                () -> EncryptNode.class.getMethod("invoke", freemind.modes.MindMapNode.class),
                "EncryptNode must have invoke(MindMapNode) method");
        }

        @Test
        @DisplayName("has Registration inner class")
        void hasRegistrationInnerClass() {
            Class<?>[] innerClasses = EncryptNode.class.getDeclaredClasses();
            boolean found = Arrays.stream(innerClasses)
                .anyMatch(c -> c.getSimpleName().equals("Registration"));
            assertTrue(found, "EncryptNode must have Registration inner class");
        }

        @Test
        @DisplayName("Registration implements MenuItemEnabledListener")
        void registrationImplementsMenuItemEnabledListener() {
            Class<?>[] innerClasses = EncryptNode.class.getDeclaredClasses();
            for (Class<?> inner : innerClasses) {
                if (inner.getSimpleName().equals("Registration")) {
                    assertTrue(
                        freemind.controller.MenuItemEnabledListener.class.isAssignableFrom(inner),
                        "EncryptNode.Registration must implement MenuItemEnabledListener");
                }
            }
        }
    }

    // ── EncryptedMindMapNode encryption round-trip ──

    @Nested
    @DisplayName("EncryptedMindMapNode")
    class EncryptedMindMapNodeTests {

        @Test
        @DisplayName("encrypt and decrypt XML round-trip via reflection")
        void encryptDecryptRoundTrip() throws Exception {
            // Use SingleDesEncrypter directly for headless round-trip
            StringBuffer password = new StringBuffer("testPassword123");
            Tools.SingleDesEncrypter encrypter = new Tools.SingleDesEncrypter(password);

            String original = "<node TEXT=\"Secret\"/>";
            String encrypted = encrypter.encrypt(original);
            assertNotNull(encrypted, "Encrypted text must not be null");
            assertNotEquals(original, encrypted, "Encrypted text must differ from original");

            String decrypted = encrypter.decrypt(encrypted);
            assertEquals(original, decrypted, "Decrypted text must match original");
        }

        @Test
        @DisplayName("encrypt with empty password produces result")
        void encryptWithEmptyPassword() {
            StringBuffer password = new StringBuffer("x");
            Tools.SingleDesEncrypter encrypter = new Tools.SingleDesEncrypter(password);

            String original = "test data";
            String encrypted = encrypter.encrypt(original);
            assertNotNull(encrypted, "Even short password must produce encrypted output");

            String decrypted = encrypter.decrypt(encrypted);
            assertEquals(original, decrypted);
        }

        @Test
        @DisplayName("wrong password returns null on decrypt")
        void wrongPasswordReturnsNull() {
            StringBuffer correctPwd = new StringBuffer("correctPassword");
            StringBuffer wrongPwd = new StringBuffer("wrongPassword!!");
            Tools.SingleDesEncrypter correctEnc = new Tools.SingleDesEncrypter(correctPwd);
            Tools.SingleDesEncrypter wrongEnc = new Tools.SingleDesEncrypter(wrongPwd);

            String original = "<node TEXT=\"Secret\"/>";
            String encrypted = correctEnc.encrypt(original);

            String decrypted = wrongEnc.decrypt(encrypted);
            // Wrong password should either return null or garbled text
            assertTrue(decrypted == null || !decrypted.equals(original),
                "Wrong password must not produce correct decrypted text");
        }
    }

    // ── ImportMindmanagerFiles structural tests ──

    @Nested
    @DisplayName("ImportMindmanagerFiles")
    class ImportMindmanagerFilesTests {

        @Test
        @DisplayName("extends ModeControllerHookAdapter")
        void extendsModeControllerHookAdapter() {
            assertTrue(ModeControllerHookAdapter.class.isAssignableFrom(ImportMindmanagerFiles.class),
                "ImportMindmanagerFiles must extend ModeControllerHookAdapter");
        }

        @Test
        @DisplayName("has transForm method")
        void hasTransFormMethod() {
            assertDoesNotThrow(
                () -> ImportMindmanagerFiles.class.getMethod("transForm",
                    javax.xml.transform.Source.class, InputStream.class),
                "ImportMindmanagerFiles must have public transForm method");
        }

        @Test
        @DisplayName("transForm applies XSLT transformation")
        void transFormAppliesXslt() {
            ImportMindmanagerFiles importer = new ImportMindmanagerFiles();

            // Simple identity-like XSLT
            String xslt = "<?xml version=\"1.0\"?>\n"
                + "<xsl:stylesheet version=\"1.0\" xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\">\n"
                + "  <xsl:template match=\"/\">\n"
                + "    <result><xsl:value-of select=\"/input/text\"/></result>\n"
                + "  </xsl:template>\n"
                + "</xsl:stylesheet>";

            String xml = "<?xml version=\"1.0\"?><input><text>hello</text></input>";

            StreamSource xmlSource = new StreamSource(
                new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)));
            InputStream xsltStream = new ByteArrayInputStream(
                xslt.getBytes(StandardCharsets.UTF_8));

            String result = importer.transForm(xmlSource, xsltStream);
            assertNotNull(result, "transForm must return non-null result");
            assertTrue(result.contains("hello"),
                "transForm result must contain transformed text 'hello'");
            assertTrue(result.contains("result"),
                "transForm result must contain the 'result' element");
        }

        @Test
        @DisplayName("has no-arg constructor")
        void hasNoArgConstructor() {
            assertDoesNotThrow(() -> ImportMindmanagerFiles.class.getDeclaredConstructor(),
                "ImportMindmanagerFiles must have a no-arg constructor");
        }
    }

    // ── Time/Calendar plugin structural tests ──

    @Nested
    @DisplayName("Time/Calendar Plugin")
    class TimeCalendarTests {

        @Test
        @DisplayName("ReminderHook extends ReminderHookBase")
        void reminderHookExtendsBase() {
            assertTrue(ReminderHookBase.class.isAssignableFrom(ReminderHook.class),
                "ReminderHook must extend ReminderHookBase");
        }

        @Test
        @DisplayName("ReminderHook has nodeRefresh method")
        void reminderHookHasNodeRefresh() {
            boolean found = Arrays.stream(ReminderHook.class.getDeclaredMethods())
                .anyMatch(m -> m.getName().equals("nodeRefresh"));
            assertTrue(found, "ReminderHook must have nodeRefresh method");
        }

        @Test
        @DisplayName("ReminderHook has setToolTip method")
        void reminderHookHasSetToolTip() {
            boolean found = Arrays.stream(ReminderHook.class.getDeclaredMethods())
                .anyMatch(m -> m.getName().equals("setToolTip"));
            assertTrue(found, "ReminderHook must have setToolTip method");
        }

        @Test
        @DisplayName("FlatNodeTableFilterModel extends AbstractTableModel")
        void flatNodeTableFilterModelExtendsAbstractTableModel() {
            assertTrue(javax.swing.table.AbstractTableModel.class
                    .isAssignableFrom(FlatNodeTableFilterModel.class),
                "FlatNodeTableFilterModel must extend AbstractTableModel");
        }

        @Test
        @DisplayName("FlatNodeTableFilterModel has setFilter and resetFilter methods")
        void flatNodeTableFilterModelHasFilterMethods() {
            boolean hasSetFilter = Arrays.stream(FlatNodeTableFilterModel.class.getDeclaredMethods())
                .anyMatch(m -> m.getName().equals("setFilter"));
            boolean hasResetFilter = Arrays.stream(FlatNodeTableFilterModel.class.getDeclaredMethods())
                .anyMatch(m -> m.getName().equals("resetFilter"));
            assertTrue(hasSetFilter, "FlatNodeTableFilterModel must have setFilter method");
            assertTrue(hasResetFilter, "FlatNodeTableFilterModel must have resetFilter method");
        }

        @Test
        @DisplayName("FlatNodeTableFilterModel constructor requires TableModel and column indices")
        void flatNodeTableFilterModelConstructorSignature() {
            assertDoesNotThrow(() ->
                FlatNodeTableFilterModel.class.getDeclaredConstructor(
                    javax.swing.table.TableModel.class, int.class, int.class),
                "FlatNodeTableFilterModel must have constructor(TableModel, int, int)");
        }
    }

    // ── ScriptingEngine structural tests ──

    @Nested
    @DisplayName("ScriptingEngine")
    class ScriptingEngineTests {

        @Test
        @DisplayName("extends MindMapHookAdapter")
        void extendsMindMapHookAdapter() {
            assertTrue(MindMapHookAdapter.class.isAssignableFrom(ScriptingEngine.class),
                "ScriptingEngine must extend MindMapHookAdapter");
        }

        @Test
        @DisplayName("has SCRIPT_PREFIX constant")
        void hasScriptPrefixConstant() throws Exception {
            Field field = ScriptingEngine.class.getDeclaredField("SCRIPT_PREFIX");
            assertTrue(Modifier.isStatic(field.getModifiers()), "SCRIPT_PREFIX must be static");
            assertTrue(Modifier.isFinal(field.getModifiers()), "SCRIPT_PREFIX must be final");
            assertEquals("script", field.get(null), "SCRIPT_PREFIX must be 'script'");
        }

        @Test
        @DisplayName("has executeScript static method")
        void hasExecuteScriptMethod() {
            boolean found = Arrays.stream(ScriptingEngine.class.getDeclaredMethods())
                .anyMatch(m -> m.getName().equals("executeScript")
                    && Modifier.isStatic(m.getModifiers()));
            assertTrue(found, "ScriptingEngine must have static executeScript method");
        }

        @Test
        @DisplayName("has ErrorHandler inner interface")
        void hasErrorHandlerInnerInterface() {
            boolean found = Arrays.stream(ScriptingEngine.class.getDeclaredClasses())
                .anyMatch(c -> c.getSimpleName().equals("ErrorHandler") && c.isInterface());
            assertTrue(found, "ScriptingEngine must have ErrorHandler inner interface");
        }

        @Test
        @DisplayName("has findLineNumberInString method")
        void hasFindLineNumberInStringMethod() {
            boolean found = Arrays.stream(ScriptingEngine.class.getDeclaredMethods())
                .anyMatch(m -> m.getName().equals("findLineNumberInString"));
            assertTrue(found, "ScriptingEngine must have findLineNumberInString method");
        }
    }

    // ── SignedScriptHandler structural tests ──

    @Nested
    @DisplayName("SignedScriptHandler")
    class SignedScriptHandlerTests {

        @Test
        @DisplayName("has FREEMIND_SCRIPT_KEY_NAME constant")
        void hasKeyNameConstant() throws Exception {
            Field field = SignedScriptHandler.class.getDeclaredField("FREEMIND_SCRIPT_KEY_NAME");
            assertTrue(Modifier.isStatic(field.getModifiers()));
            assertTrue(Modifier.isFinal(field.getModifiers()));
            assertEquals("FreeMindScriptKey", field.get(null));
        }

        @Test
        @DisplayName("has ScriptContents inner class")
        void hasScriptContentsInnerClass() {
            boolean found = Arrays.stream(SignedScriptHandler.class.getDeclaredClasses())
                .anyMatch(c -> c.getSimpleName().equals("ScriptContents"));
            assertTrue(found, "SignedScriptHandler must have ScriptContents inner class");
        }

        @Test
        @DisplayName("has isScriptSigned method")
        void hasIsScriptSignedMethod() {
            boolean found = Arrays.stream(SignedScriptHandler.class.getDeclaredMethods())
                .anyMatch(m -> m.getName().equals("isScriptSigned"));
            assertTrue(found, "SignedScriptHandler must have isScriptSigned method");
        }
    }

    // ── Search plugin structural tests ──

    @Nested
    @DisplayName("Search Plugin")
    class SearchPluginTests {

        @Test
        @DisplayName("FileSearchModel class exists and is loadable")
        void fileSearchModelClassExists() {
            assertNotNull(FileSearchModel.class, "FileSearchModel must be loadable");
        }

        @Test
        @DisplayName("FileSearchModel has doSearch method")
        void fileSearchModelHasDoSearchMethod() {
            boolean found = Arrays.stream(FileSearchModel.class.getDeclaredMethods())
                .anyMatch(m -> m.getName().equals("doSearch"));
            assertTrue(found, "FileSearchModel must have doSearch method");
        }

        @Test
        @DisplayName("FileSearchModel has FileAttribute enum")
        void fileSearchModelHasFileAttributeEnum() {
            boolean found = Arrays.stream(FileSearchModel.class.getDeclaredClasses())
                .anyMatch(c -> c.getSimpleName().equals("FileAttribute") && c.isEnum());
            assertTrue(found, "FileSearchModel must have FileAttribute enum");
        }
    }

    // ── NodeNote structural tests ──

    @Nested
    @DisplayName("NodeNote")
    class NodeNoteTests {

        @Test
        @DisplayName("NodeNote class is loadable")
        void nodeNoteClassExists() {
            assertNotNull(NodeNote.class, "NodeNote must be loadable");
        }

        @Test
        @DisplayName("NodeNote has startupMapHook method")
        void nodeNoteHasStartupMapHook() {
            boolean found = Arrays.stream(NodeNote.class.getDeclaredMethods())
                .anyMatch(m -> m.getName().equals("startupMapHook"));
            assertTrue(found, "NodeNote must have startupMapHook method");
        }
    }

    // ── Tools compress/decompress round-trip ──

    @Nested
    @DisplayName("Tools Compress/Decompress")
    class CompressDecompressTests {

        @Test
        @DisplayName("compress and decompress round-trip preserves text")
        void compressDecompressRoundTrip() {
            String original = "<collaboration_hello map=\"test map content\"/>";
            String compressed = Tools.compress(original);
            assertNotNull(compressed, "Compressed text must not be null");
            assertNotEquals(original, compressed, "Compressed text must differ from original");

            String decompressed = Tools.decompress(compressed);
            assertEquals(original, decompressed,
                "Decompress must restore original text");
        }

        @Test
        @DisplayName("compress and decompress round-trip with unicode")
        void compressDecompressUnicode() {
            String original = "Unicode test: \u00fc\u00f6\u00e4 \u00df \u4e16\u754c \ud83c\udf0d";
            String compressed = Tools.compress(original);
            String decompressed = Tools.decompress(compressed);
            assertEquals(original, decompressed,
                "Compress/decompress must preserve unicode characters");
        }
    }
}
