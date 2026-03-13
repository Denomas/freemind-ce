package tests.freemind.unicode;

import freemind.main.HeadlessFreeMind;
import freemind.main.Tools;
import freemind.modes.ExtendedMapFeedbackImpl;
import freemind.modes.MapAdapter;
import freemind.modes.MindMapNode;
import freemind.modes.mindmapmode.MindMapMapModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.StringWriter;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static tests.freemind.unicode.UnicodeTestData.*;

class Utf8IntegrationTest {

    private ExtendedMapFeedbackImpl mapFeedback;
    private MindMapMapModel map;

    @BeforeEach
    void setUp() {
        new HeadlessFreeMind();
        mapFeedback = new ExtendedMapFeedbackImpl();
        map = new MindMapMapModel(mapFeedback);
        mapFeedback.setMap(map);
    }

    @Test
    void turkishText_roundTrip() throws Exception {
        String text = "İşçiler Öğrenci Çalışma";
        assertNodeTextRoundTrip(text);
    }

    @Test
    void russianText_roundTrip() throws Exception {
        assertNodeTextRoundTrip(CYRILLIC);
    }

    @Test
    void arabicText_roundTrip() throws Exception {
        assertNodeTextRoundTrip(ARABIC);
    }

    @Test
    void cjkText_roundTrip() throws Exception {
        assertNodeTextRoundTrip(CJK);
        assertNodeTextRoundTrip(JAPANESE);
    }

    @Test
    void emojiText_roundTrip() throws Exception {
        assertNodeTextRoundTrip(EMOJI);
    }

    @Test
    void mixedScripts_coexistAfterRoundTrip() throws Exception {
        MindMapNode root = createMapWithRoot();

        String[] texts = {"İşçiler", CYRILLIC, ARABIC, CJK, JAPANESE, EMOJI};
        for (int i = 0; i < texts.length; i++) {
            MindMapNode child = mapFeedback.addNewNode(root, i, true);
            mapFeedback.setNodeText(child, texts[i]);
        }

        String savedXml = getMapContents();

        for (String text : texts) {
            assertThat(savedXml)
                .as("Saved XML should contain: %s", text)
                .contains(text);
        }

        // Reload and verify
        ExtendedMapFeedbackImpl mf2 = new ExtendedMapFeedbackImpl();
        MindMapMapModel map2 = new MindMapMapModel(mf2);
        mf2.setMap(map2);
        MindMapNode reloaded = map2.loadTree(
            new Tools.StringReaderCreator(savedXml), MapAdapter.sDontAskInstance);
        map2.setRoot(reloaded);

        for (int i = 0; i < texts.length; i++) {
            MindMapNode child = (MindMapNode) reloaded.getChildAt(i);
            assertThat(child.getText())
                .as("Child %d text should be preserved", i)
                .isEqualTo(texts[i]);
        }
    }

    @Test
    void nodeAttributes_withUnicodeValues() throws Exception {
        // Test attributes with Unicode values via XML load
        String mapXml = "<map><node TEXT='ROOT'>"
            + "<node TEXT='Attributed'>"
            + "<attribute NAME='city' VALUE='İstanbul'/>"
            + "<attribute NAME='greeting' VALUE='Привет'/>"
            + "</node></node></map>";

        Tools.StringReaderCreator reader = new Tools.StringReaderCreator(mapXml);
        MindMapNode root = map.loadTree(reader, MapAdapter.sDontAskInstance);
        map.setRoot(root);

        MindMapNode child = (MindMapNode) root.getChildAt(0);
        // Verify attributes loaded
        assertThat(child.getAttributeTableLength()).isEqualTo(2);

        // Save and verify UTF-8
        String savedXml = getMapContents();
        assertThat(savedXml).contains("İstanbul");
        assertThat(savedXml).contains("Привет");
    }

    @Test
    void savedXml_isValidUtf8_noNumericEntities() throws Exception {
        MindMapNode root = createMapWithRoot();
        MindMapNode child = mapFeedback.addNewNode(root, 0, true);
        mapFeedback.setNodeText(child, TURKISH);

        String savedXml = getMapContents();

        // Should contain the actual UTF-8 characters
        assertThat(savedXml).contains(TURKISH);
        // Should NOT contain numeric entities for these characters
        assertThat(savedXml)
            .as("Should not contain numeric entities for BMP Turkish chars")
            .doesNotContain("&#287;")
            .doesNotContain("&#x11f;");

        // Verify the string is valid UTF-8 bytes
        byte[] bytes = savedXml.getBytes(StandardCharsets.UTF_8);
        String roundTripped = new String(bytes, StandardCharsets.UTF_8);
        assertThat(roundTripped).contains(TURKISH);
    }

    @Test
    void noteText_withUnicode_roundTrip() throws Exception {
        MindMapNode root = createMapWithRoot();
        MindMapNode child = mapFeedback.addNewNode(root, 0, true);

        String htmlNote = wrapInHtml("Привет мир 你好世界");
        mapFeedback.setNoteText(child, htmlNote);

        String savedXml = getMapContents();
        assertThat(savedXml).contains("Привет мир");
        assertThat(savedXml).contains("你好世界");

        // Reload
        ExtendedMapFeedbackImpl mf2 = new ExtendedMapFeedbackImpl();
        MindMapMapModel map2 = new MindMapMapModel(mf2);
        mf2.setMap(map2);
        MindMapNode reloaded = map2.loadTree(
            new Tools.StringReaderCreator(savedXml), MapAdapter.sDontAskInstance);
        map2.setRoot(reloaded);

        MindMapNode reloadedChild = (MindMapNode) reloaded.getChildAt(0);
        String note = reloadedChild.getNoteText();
        assertThat(note).contains("Привет мир");
        assertThat(note).contains("你好世界");
    }

    // --- Helpers ---

    private void assertNodeTextRoundTrip(String text) throws Exception {
        String mapXml = "<map><node TEXT='" + xmlEscape(text) + "'/></map>";
        Tools.StringReaderCreator reader = new Tools.StringReaderCreator(mapXml);
        MindMapNode root = map.loadTree(reader, MapAdapter.sDontAskInstance);
        map.setRoot(root);

        String savedXml = getMapContents();
        assertThat(savedXml)
            .as("Saved XML should contain UTF-8 text: %s", text)
            .contains(text);

        // Reload and verify
        ExtendedMapFeedbackImpl mf2 = new ExtendedMapFeedbackImpl();
        MindMapMapModel map2 = new MindMapMapModel(mf2);
        mf2.setMap(map2);
        MindMapNode reloaded = map2.loadTree(
            new Tools.StringReaderCreator(savedXml), MapAdapter.sDontAskInstance);
        map2.setRoot(reloaded);

        assertThat(reloaded.getText())
            .as("Reloaded text should match original")
            .isEqualTo(text);
    }

    private MindMapNode createMapWithRoot() throws Exception {
        String initialMap = "<map><node TEXT='ROOT'/></map>";
        Tools.StringReaderCreator reader = new Tools.StringReaderCreator(initialMap);
        MindMapNode root = map.loadTree(reader, MapAdapter.sDontAskInstance);
        map.setRoot(root);
        return root;
    }

    private String getMapContents() throws Exception {
        StringWriter writer = new StringWriter();
        map.getFilteredXml(writer);
        return writer.toString();
    }

    private static String xmlEscape(String s) {
        return s.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("'", "&apos;")
                .replace("\"", "&quot;");
    }
}
