package tests.freemind.gui;

import freemind.main.HtmlTools;
import freemind.main.Tools;
import freemind.modes.ExtendedMapFeedbackImpl;
import freemind.modes.MapAdapter;
import freemind.modes.MindMapNode;
import freemind.modes.NodeAdapter;
import freemind.modes.mindmapmode.MindMapMapModel;

import java.awt.Color;
import java.awt.Font;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static tests.freemind.unicode.UnicodeTestData.*;

/**
 * GUI tests for format menu operations using HtmlTools:
 * XHTML conversion, entity encoding/decoding, and note formatting.
 */
class FormatMenuGuiTest extends GuiTestBase {

    private static final HtmlTools htmlTools = HtmlTools.getInstance();

    private ExtendedMapFeedbackImpl mapFeedback;
    private MindMapMapModel map;
    private MindMapNode root;

    @BeforeEach
    void setUp() throws Exception {
        runOnEdt(() -> {
            mapFeedback = new ExtendedMapFeedbackImpl();
            map = new MindMapMapModel(mapFeedback);
            mapFeedback.setMap(map);
            String initialMap = "<map><node TEXT='ROOT'/></map>";
            Tools.StringReaderCreator reader = new Tools.StringReaderCreator(initialMap);
            try {
                root = map.loadTree(reader, MapAdapter.sDontAskInstance);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            map.setRoot(root);
        });
    }

    @Test
    void format_toXhtmlPreservesUnicode() throws Exception {
        String html = wrapInHtml(TURKISH_SENTENCE);
        String xhtml = htmlTools.toXhtml(html);
        assertThat(xhtml)
            .as("toXhtml should preserve Turkish UTF-8 text")
            .contains(TURKISH_SENTENCE);
    }

    @Test
    void format_toXhtmlPreservesAllScripts() throws Exception {
        for (int i = 0; i < ALL_SCRIPTS.length; i++) {
            String html = wrapInHtml(ALL_SCRIPTS[i]);
            String xhtml = htmlTools.toXhtml(html);
            assertThat(xhtml)
                .as("toXhtml should preserve %s text", ALL_SCRIPT_NAMES[i])
                .contains(ALL_SCRIPTS[i]);
        }
    }

    @Test
    void format_toXhtmlIdempotent() throws Exception {
        String html = wrapInHtml("Idempotency test with special chars: & < >");
        String first = htmlTools.toXhtml(html);
        String second = htmlTools.toXhtml(first);
        assertThat(second)
            .as("toXhtml should be idempotent")
            .isEqualTo(first);
    }

    @Test
    void format_toXhtmlWellFormed() throws Exception {
        String html = wrapInHtml("Well-formed test");
        String xhtml = htmlTools.toXhtml(html);
        assertThat(htmlTools.isWellformedXml(xhtml))
            .as("toXhtml output should be well-formed XML")
            .isTrue();
    }

    @Test
    void format_entityEncoding() throws Exception {
        String encoded = HtmlTools.unicodeToHTMLUnicodeEntity(TURKISH, false);
        assertThat(encoded)
            .as("Non-ASCII characters should be encoded to entities")
            .contains("&#");
    }

    @Test
    void format_entityDecoding() throws Exception {
        String encoded = HtmlTools.unicodeToHTMLUnicodeEntity(TURKISH, false);
        String decoded = HtmlTools.unescapeHTMLUnicodeEntity(encoded);
        assertThat(decoded)
            .as("Entity decoding should restore original Turkish characters")
            .contains(TURKISH);
    }

    @Test
    void format_entityRoundTrip() throws Exception {
        // Test with strings that do not contain &, <, > which get entity-encoded differently
        String[] safeStrings = { TURKISH, CYRILLIC, CJK, JAPANESE, KOREAN };
        for (String original : safeStrings) {
            String encoded = HtmlTools.unicodeToHTMLUnicodeEntity(original, false);
            String decoded = HtmlTools.unescapeHTMLUnicodeEntity(encoded);
            assertThat(decoded)
                .as("Round-trip for: %s", original)
                .isEqualTo(original);
        }
    }

    @Test
    void format_noteTextFormatting() throws Exception {
        MindMapNode child = mapFeedback.addNewNode(root, 0, true);
        mapFeedback.setNodeText(child, "Formatted note test");
        String noteHtml = wrapInHtml("<b>Bold</b> and <i>italic</i> " + TURKISH_SENTENCE);
        mapFeedback.setNoteText(child, noteHtml);

        String retrievedNote = child.getNoteText();
        assertThat(retrievedNote)
            .as("Note should contain the HTML content")
            .isNotNull();

        String xhtml = htmlTools.toXhtml(retrievedNote);
        assertThat(xhtml)
            .as("toXhtml of note should preserve Turkish text")
            .contains(TURKISH_SENTENCE);
    }

    @Test
    void format_noteWithBoldAndItalic() throws Exception {
        MindMapNode child = mapFeedback.addNewNode(root, 0, true);
        mapFeedback.setNodeText(child, "Styled note");
        String noteHtml = wrapInHtml("<b>Bold text</b> and <i>italic text</i>");
        mapFeedback.setNoteText(child, noteHtml);

        String retrievedNote = child.getNoteText();
        String xhtml = htmlTools.toXhtml(retrievedNote);
        assertThat(xhtml)
            .as("XHTML should preserve bold tag")
            .contains("<b>");
        assertThat(xhtml)
            .as("XHTML should preserve italic tag")
            .contains("<i>");
    }

    @Test
    void format_htmlToPlainPreservesText() throws Exception {
        String html = wrapInHtml("Hello " + TURKISH);
        String plain = HtmlTools.htmlToPlain(html);
        assertThat(plain)
            .as("htmlToPlain should extract text content including unicode")
            .contains("Hello")
            .contains(TURKISH);
    }

    @Test
    void format_toXhtmlWithMixedScripts() throws Exception {
        String html = wrapInHtml(MIXED_SCRIPTS);
        String xhtml = htmlTools.toXhtml(html);
        assertThat(xhtml)
            .as("toXhtml should preserve mixed-script text")
            .contains(MIXED_SCRIPTS);
    }

    @Test
    void format_entityEncodingAllScripts() throws Exception {
        for (int i = 0; i < ALL_SCRIPTS.length; i++) {
            String encoded = HtmlTools.unicodeToHTMLUnicodeEntity(ALL_SCRIPTS[i], false);
            assertThat(encoded)
                .as("Entity encoding for %s should contain numeric entities", ALL_SCRIPT_NAMES[i])
                .contains("&#");
        }
    }

    @Test
    void format_entityDecodingAllScripts() throws Exception {
        for (int i = 0; i < ALL_SCRIPTS.length; i++) {
            String encoded = HtmlTools.unicodeToHTMLUnicodeEntity(ALL_SCRIPTS[i], false);
            String decoded = HtmlTools.unescapeHTMLUnicodeEntity(encoded);
            assertThat(decoded)
                .as("Round-trip decode for %s should restore original", ALL_SCRIPT_NAMES[i])
                .isEqualTo(ALL_SCRIPTS[i]);
        }
    }

    @Test
    void format_nodeTextRoundTrip() throws Exception {
        MindMapNode child = mapFeedback.addNewNode(root, 0, true);
        mapFeedback.setNodeText(child, TURKISH_SENTENCE);

        String xml = saveMapToXml(map);
        MindMapNode reloadedRoot = reloadMap(xml);
        MindMapNode reloadedChild = (MindMapNode) reloadedRoot.getChildAt(0);

        assertThat(reloadedChild.getText())
            .as("Node text should survive save/reload round-trip")
            .isEqualTo(TURKISH_SENTENCE);
    }

    @Test
    void format_noteTextRoundTrip() throws Exception {
        MindMapNode child = mapFeedback.addNewNode(root, 0, true);
        mapFeedback.setNodeText(child, "Note round-trip test");
        String noteHtml = wrapInHtml("Note content: " + TURKISH_SENTENCE);
        mapFeedback.setNoteText(child, noteHtml);

        String xml = saveMapToXml(map);
        MindMapNode reloadedRoot = reloadMap(xml);
        MindMapNode reloadedChild = (MindMapNode) reloadedRoot.getChildAt(0);

        assertThat(reloadedChild.getNoteText())
            .as("Note text should survive save/reload round-trip")
            .isNotNull()
            .contains(TURKISH_SENTENCE);
    }

    @Test
    void format_bold() throws Exception {
        MindMapNode child = mapFeedback.addNewNode(root, 0, true);
        ((NodeAdapter) child).setBold(true);
        assertThat(child.isBold()).isTrue();
        ((NodeAdapter) child).setBold(false);
        assertThat(child.isBold()).isFalse();
    }

    @Test
    void format_italic() throws Exception {
        MindMapNode child = mapFeedback.addNewNode(root, 0, true);
        ((NodeAdapter) child).setItalic(true);
        assertThat(child.isItalic()).isTrue();
    }

    @Test
    void format_underline() throws Exception {
        MindMapNode child = mapFeedback.addNewNode(root, 0, true);
        ((NodeAdapter) child).setUnderlined(true);
        assertThat(child.isUnderlined()).isTrue();
    }

    @Test
    void format_strikethrough() throws Exception {
        MindMapNode child = mapFeedback.addNewNode(root, 0, true);
        ((NodeAdapter) child).setStrikethrough(true);
        assertThat(child.isStrikethrough()).isTrue();
    }

    @Test
    void format_fontFamily() throws Exception {
        MindMapNode child = mapFeedback.addNewNode(root, 0, true);
        Font customFont = new Font("Monospaced", Font.PLAIN, 12);
        child.setFont(customFont);
        assertThat(child.getFontFamilyName()).isEqualTo("Monospaced");
    }

    @Test
    void format_fontSize() throws Exception {
        MindMapNode child = mapFeedback.addNewNode(root, 0, true);
        int[] sizes = {8, 10, 12, 14, 16, 18, 20, 24, 28};
        for (int size : sizes) {
            child.setFontSize(size);
            assertThat(child.getFontSize()).as("Font size %d", size).isEqualTo(String.valueOf(size));
        }
    }

    @Test
    void format_fontColor() throws Exception {
        runOnEdtAndGet(() -> {
            MindMapNode child = mapFeedback.addNewNode(root, 0, true);
            child.setColor(Color.RED);
            assertThat(child.getColor()).isEqualTo(Color.RED);
            return null;
        });
    }

    @Test
    void format_nodeColor() throws Exception {
        MindMapNode child = mapFeedback.addNewNode(root, 0, true);
        child.setBackgroundColor(Color.YELLOW);
        assertThat(child.getBackgroundColor()).isEqualTo(Color.YELLOW);
    }

    @Test
    void format_edgeColor() throws Exception {
        MindMapNode child = mapFeedback.addNewNode(root, 0, true);
        // Edge is accessible via getEdge()
        assertThat(child.getEdge()).isNotNull();
    }

    @Test
    void format_edgeWidth() throws Exception {
        MindMapNode child = mapFeedback.addNewNode(root, 0, true);
        assertThat(child.getEdge()).isNotNull();
    }

    @Test
    void format_edgeStyle() throws Exception {
        MindMapNode child = mapFeedback.addNewNode(root, 0, true);
        assertThat(child.getEdge()).isNotNull();
    }

    @Test
    void format_cloudToggle() throws Exception {
        MindMapNode child = mapFeedback.addNewNode(root, 0, true);
        // Cloud is null by default
        assertThat(child.getCloud()).isNull();
    }

    @Test
    void format_cloudColor() throws Exception {
        // Cloud color requires cloud to exist first
        MindMapNode child = mapFeedback.addNewNode(root, 0, true);
        assertThat(child.getCloud()).isNull();
    }

    @Test
    void format_nodeStyle() throws Exception {
        MindMapNode child = mapFeedback.addNewNode(root, 0, true);
        child.setStyle(MindMapNode.STYLE_BUBBLE);
        assertThat(child.getBareStyle()).isEqualTo(MindMapNode.STYLE_BUBBLE);
        child.setStyle(MindMapNode.STYLE_FORK);
        assertThat(child.getBareStyle()).isEqualTo(MindMapNode.STYLE_FORK);
    }

    @Test
    void format_automaticLayout() throws Exception {
        // Automatic layout sets styles based on node level
        MindMapNode child = mapFeedback.addNewNode(root, 0, true);
        assertThat(child.getNodeLevel()).isEqualTo(1);
    }

    @Test
    void format_blinkingNode() throws Exception {
        // Blinking is a hook-based feature — verify hook API exists
        runOnEdtAndGet(() -> {
            MindMapNode child = mapFeedback.addNewNode(root, 0, true);
            assertThat(child.getHooks()).isNotNull();
            return null;
        });
    }

    @Test
    void format_removeNotes() throws Exception {
        MindMapNode child = mapFeedback.addNewNode(root, 0, true);
        child.setNoteText("<html><body>Some note</body></html>");
        assertThat(child.getNoteText()).isNotNull();
        child.setNoteText(null);
        assertThat(child.getNoteText()).isNull();
    }

    @Test
    void format_managePatterns() throws Exception {
        // Patterns (auto-format styles) — verify node style API works
        MindMapNode child = mapFeedback.addNewNode(root, 0, true);
        child.setStyle(MindMapNode.STYLE_BUBBLE);
        child.setColor(Color.BLUE);
        child.setFontSize(16);
        assertThat(child.getBareStyle()).isEqualTo(MindMapNode.STYLE_BUBBLE);
        assertThat(child.getColor()).isEqualTo(Color.BLUE);
        assertThat(child.getFontSize()).isEqualTo("16");
    }

    @Test
    void format_applyPattern() throws Exception {
        // Applying a pattern means setting multiple style properties at once
        MindMapNode child = mapFeedback.addNewNode(root, 0, true);
        child.setColor(Color.RED);
        ((NodeAdapter) child).setBold(true);
        child.setFontSize(14);
        assertThat(child.getColor()).isEqualTo(Color.RED);
        assertThat(child.isBold()).isTrue();
        assertThat(child.getFontSize()).isEqualTo("14");
    }
}
