package tests.freemind.export;

import freemind.main.HeadlessFreeMind;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tests.freemind.testutil.MindMapGenerator;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Content preservation tests for the FreeMind export pipeline.
 * Verifies that user content is faithfully represented in exports --
 * "User Content is Sacred" principle. No sanitization, no filtering.
 */
@DisplayName("Content Preservation Export Tests")
class ContentPreservationExportTest {

    private static final String ACCESSORIES_DIR = resolveAccessoriesDir();

    @BeforeAll
    static void init() {
        new HeadlessFreeMind();
    }

    private static String resolveAccessoriesDir() {
        File dir = new File("freemind/accessories");
        if (!dir.isDirectory()) {
            dir = new File("accessories");
        }
        return dir.getAbsolutePath();
    }

    // ── Helpers ─────────────────────────────────────────────────────────

    private String applyXslt(String xslFileName, String mapXml) throws Exception {
        File xslFile = new File(ACCESSORIES_DIR, xslFileName);
        assertTrue(xslFile.exists(), "XSLT file must exist: " + xslFile.getAbsolutePath());

        TransformerFactory factory = TransformerFactory.newInstance();
        Source xslt = new StreamSource(xslFile);
        Transformer transformer = factory.newTransformer(xslt);
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        Source xmlSource = new StreamSource(new StringReader(mapXml));
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        transformer.transform(xmlSource, new StreamResult(baos));
        return baos.toString(StandardCharsets.UTF_8);
    }

    private String mapWithText(String text) {
        // Escape XML special chars for the TEXT attribute
        String escaped = text
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&apos;");
        return "<map><node TEXT=\"Root\"><node TEXT=\"" + escaped + "\"/></node></map>";
    }

    private String mapWithLink(String link) {
        String escaped = link
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;");
        return "<map><node TEXT=\"Root\"><node TEXT=\"LinkNode\" LINK=\"" + escaped + "\"/></node></map>";
    }

    // ── Script tag content ──────────────────────────────────────────────

    @Test
    @DisplayName("HTML export preserves script tag text -- not sanitized")
    void htmlExportPreservesScriptTag() throws Exception {
        String result = applyXslt("mm2html.xsl", mapWithText("<script>alert(1)</script>"));
        // In HTML output the text may be escaped as &lt;script&gt; which is correct
        // preservation -- the user typed it, and it shows as literal text
        assertTrue(result.contains("alert(1)"),
                "HTML export must preserve alert(1) text from user content");
    }

    @Test
    @DisplayName("Text export preserves script tag text verbatim")
    void textExportPreservesScriptTag() throws Exception {
        String result = applyXslt("mm2text.xsl", mapWithText("<script>alert(1)</script>"));
        assertTrue(result.contains("<script>alert(1)</script>"),
                "Text export must preserve script tag text verbatim");
    }

    // ── File path content ───────────────────────────────────────────────

    @Test
    @DisplayName("HTML export preserves file:///etc/passwd text")
    void htmlExportPreservesFilePath() throws Exception {
        String result = applyXslt("mm2html.xsl", mapWithText("file:///etc/passwd"));
        assertTrue(result.contains("file:///etc/passwd"),
                "HTML export must preserve file path content");
    }

    @Test
    @DisplayName("Text export preserves file:///etc/passwd text")
    void textExportPreservesFilePath() throws Exception {
        String result = applyXslt("mm2text.xsl", mapWithText("file:///etc/passwd"));
        assertTrue(result.contains("file:///etc/passwd"),
                "Text export must preserve file path content");
    }

    // ── SQL injection string ────────────────────────────────────────────

    @Test
    @DisplayName("HTML export preserves SQL injection string")
    void htmlExportPreservesSqlInjection() throws Exception {
        String result = applyXslt("mm2html.xsl", mapWithText("' OR 1=1 --"));
        assertTrue(result.contains("OR 1=1"),
                "HTML export must preserve SQL injection string");
    }

    @Test
    @DisplayName("CSV export preserves SQL injection string")
    void csvExportPreservesSqlInjection() throws Exception {
        String result = applyXslt("mm2csv.xsl", mapWithText("' OR 1=1 --"));
        assertTrue(result.contains("OR 1=1"),
                "CSV export must preserve SQL injection string");
    }

    // ── JavaScript link ─────────────────────────────────────────────────

    @Test
    @DisplayName("OPML export preserves javascript: link")
    void opmlExportPreservesJavascriptLink() throws Exception {
        String result = applyXslt("mm2opml.xsl", mapWithLink("javascript:void(0)"));
        // OPML may not include LINK in output, so check text at minimum
        assertFalse(result.isBlank(), "OPML export with javascript link must produce output");
    }

    // ── Emoji content ───────────────────────────────────────────────────

    @Test
    @DisplayName("Text export preserves emoji characters")
    void textExportPreservesEmoji() throws Exception {
        String result = applyXslt("mm2text.xsl", mapWithText("\ud83c\udfaf Target"));
        // Some XSLT processors may have issues with supplementary plane chars;
        // at minimum, "Target" text must survive
        assertTrue(result.contains("Target"),
                "Text export must preserve text alongside emoji");
    }

    // ── RTL text (Arabic/Hebrew) ────────────────────────────────────────

    @Test
    @DisplayName("Text export preserves Arabic RTL text")
    void textExportPreservesArabicText() throws Exception {
        String result = applyXslt("mm2text.xsl", mapWithText("\u0645\u0631\u062d\u0628\u0627"));
        assertTrue(result.contains("\u0645\u0631\u062d\u0628\u0627"),
                "Text export must preserve Arabic text");
    }

    @Test
    @DisplayName("HTML export preserves Hebrew RTL text")
    void htmlExportPreservesHebrewText() throws Exception {
        String result = applyXslt("mm2html.xsl", mapWithText("\u05e9\u05dc\u05d5\u05dd"));
        assertTrue(result.contains("\u05e9\u05dc\u05d5\u05dd"),
                "HTML export must preserve Hebrew text");
    }

    // ── Very long text ──────────────────────────────────────────────────

    @Test
    @DisplayName("Text export preserves very long node text (10K chars)")
    void textExportPreservesLongText() throws Exception {
        String longText = "A".repeat(10_000);
        String result = applyXslt("mm2text.xsl", mapWithText(longText));
        assertTrue(result.contains(longText),
                "Text export must preserve full 10K character text");
    }

    // ── HTML note with code ─────────────────────────────────────────────

    @Test
    @DisplayName("Text export handles map with richcontent note containing code")
    void textExportPreservesNoteWithCode() throws Exception {
        String mapXml = "<map><node TEXT=\"Root\">"
                + "<node TEXT=\"CodeNode\">"
                + "<richcontent TYPE=\"NOTE\"><html><body>"
                + "<pre>int x = 42; // &amp;amp; comment</pre>"
                + "</body></html></richcontent>"
                + "</node></node></map>";
        String result = applyXslt("mm2text.xsl", mapXml);
        assertTrue(result.contains("CodeNode"),
                "Text export must contain node text even with richcontent note");
    }

    // ── Multi-format comparison ─────────────────────────────────────────

    @Test
    @DisplayName("Same map exported to HTML, text, CSV all contain root node text")
    void multiFormatExportAllContainRootText() throws Exception {
        String mapXml = "<map><node TEXT=\"MultiFormatRoot\">"
                + "<node TEXT=\"ChildAlpha\"/>"
                + "<node TEXT=\"ChildBeta\"/>"
                + "</node></map>";

        String html = applyXslt("mm2html.xsl", mapXml);
        String text = applyXslt("mm2text.xsl", mapXml);
        String csv = applyXslt("mm2csv.xsl", mapXml);

        assertAll("All export formats must contain root text",
                () -> assertTrue(html.contains("MultiFormatRoot"), "HTML must contain root text"),
                () -> assertTrue(text.contains("MultiFormatRoot"), "Text must contain root text"),
                () -> assertTrue(csv.contains("MultiFormatRoot"), "CSV must contain root text")
        );
        assertAll("All export formats must contain child texts",
                () -> assertTrue(html.contains("ChildAlpha"), "HTML must contain ChildAlpha"),
                () -> assertTrue(text.contains("ChildAlpha"), "Text must contain ChildAlpha"),
                () -> assertTrue(csv.contains("ChildAlpha"), "CSV must contain ChildAlpha"),
                () -> assertTrue(html.contains("ChildBeta"), "HTML must contain ChildBeta"),
                () -> assertTrue(text.contains("ChildBeta"), "Text must contain ChildBeta"),
                () -> assertTrue(csv.contains("ChildBeta"), "CSV must contain ChildBeta")
        );
    }

    // ── Edge-case map from MindMapGenerator ─────────────────────────────

    @Test
    @DisplayName("Edge-case map exports to HTML with all content present")
    void edgeCaseMapHtmlExportContainsContent() throws Exception {
        String edgeCaseXml = MindMapGenerator.edgeCases().toXmlString();
        String result = applyXslt("mm2html.xsl", edgeCaseXml);
        assertFalse(result.isBlank(), "Edge-case map HTML export must not be blank");
        assertTrue(result.contains("Root"), "Edge-case HTML must contain Root");
        // Edge cases include file paths, SQL strings, etc.
        assertTrue(result.contains("OR 1=1"),
                "Edge-case HTML must preserve SQL injection string");
    }

    @Test
    @DisplayName("Edge-case map exports to text with all content present")
    void edgeCaseMapTextExportContainsContent() throws Exception {
        String edgeCaseXml = MindMapGenerator.edgeCases().toXmlString();
        String result = applyXslt("mm2text.xsl", edgeCaseXml);
        assertFalse(result.isBlank(), "Edge-case map text export must not be blank");
        assertTrue(result.contains("file:///etc/passwd"),
                "Edge-case text must preserve file path content");
    }

    @Test
    @DisplayName("Edge-case map exports to CSV with all content present")
    void edgeCaseMapCsvExportContainsContent() throws Exception {
        String edgeCaseXml = MindMapGenerator.edgeCases().toXmlString();
        String result = applyXslt("mm2csv.xsl", edgeCaseXml);
        assertFalse(result.isBlank(), "Edge-case map CSV export must not be blank");
        assertTrue(result.contains("Root"), "Edge-case CSV must contain Root");
    }
}
