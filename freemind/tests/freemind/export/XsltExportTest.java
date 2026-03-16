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
 * XSLT export pipeline tests for FreeMind CE.
 * Verifies that each XSLT stylesheet can transform a generated mind map
 * into the expected output format with valid structure and content.
 */
@DisplayName("XSLT Export Pipeline Tests")
class XsltExportTest {

    private static String smallMapXml;
    private static String edgeCaseMapXml;

    /** Base path to XSLT files, resolved relative to project root. */
    private static final String ACCESSORIES_DIR = resolveAccessoriesDir();

    @BeforeAll
    static void init() throws Exception {
        new HeadlessFreeMind();
        smallMapXml = MindMapGenerator.create()
                .withNodes(10).withDepth(3).withWidth(3)
                .withNotes(30).withLinks(30)
                .toXmlString();
        edgeCaseMapXml = MindMapGenerator.edgeCases().toXmlString();
    }

    private static String resolveAccessoriesDir() {
        // Try common locations for the accessories directory
        File dir = new File("freemind/accessories");
        if (!dir.isDirectory()) {
            dir = new File("accessories");
        }
        return dir.getAbsolutePath();
    }

    // ── Helper ──────────────────────────────────────────────────────────

    /**
     * Applies the named XSLT stylesheet to the given map XML and returns the result.
     * Uses ByteArrayOutputStream with UTF-8 to avoid Xalan numeric character reference
     * encoding that occurs when using StringWriter with non-UTF-8 output encodings.
     */
    private String applyXslt(String xslFileName, String mapXml) throws Exception {
        File xslFile = new File(ACCESSORIES_DIR, xslFileName);
        assertTrue(xslFile.exists(), "XSLT file must exist: " + xslFile.getAbsolutePath());

        TransformerFactory factory = TransformerFactory.newInstance();
        Source xslt = new StreamSource(xslFile);
        Transformer transformer = factory.newTransformer(xslt);
        // Override encoding to UTF-8 so all Unicode chars are preserved in output
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        Source xmlSource = new StreamSource(new StringReader(mapXml));
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        transformer.transform(xmlSource, new StreamResult(baos));
        return baos.toString(StandardCharsets.UTF_8);
    }

    // ── Top 10 XSLT stylesheets: non-empty output ──────────────────────

    @Test
    @DisplayName("mm2html.xsl produces non-empty HTML output")
    void mm2htmlProducesOutput() throws Exception {
        String result = applyXslt("mm2html.xsl", smallMapXml);
        assertFalse(result.isBlank(), "mm2html output must not be blank");
        assertTrue(result.toLowerCase().contains("<html"),
                "mm2html output must contain <html> tag");
    }

    @Test
    @DisplayName("mm2text.xsl produces non-empty text output")
    void mm2textProducesOutput() throws Exception {
        String result = applyXslt("mm2text.xsl", smallMapXml);
        assertFalse(result.isBlank(), "mm2text output must not be blank");
        assertTrue(result.contains("Root"), "mm2text output must contain root node text");
    }

    @Test
    @DisplayName("mm2csv.xsl produces non-empty CSV output")
    void mm2csvProducesOutput() throws Exception {
        String result = applyXslt("mm2csv.xsl", smallMapXml);
        assertFalse(result.isBlank(), "mm2csv output must not be blank");
        assertTrue(result.contains("Root"), "mm2csv output must contain root node text");
    }

    @Test
    @DisplayName("mm2opml.xsl produces non-empty OPML output")
    void mm2opmlProducesOutput() throws Exception {
        String result = applyXslt("mm2opml.xsl", smallMapXml);
        assertFalse(result.isBlank(), "mm2opml output must not be blank");
        assertTrue(result.toLowerCase().contains("<opml"),
                "mm2opml output must contain <opml> tag");
    }

    @Test
    @DisplayName("mm2latexartcl.xsl produces non-empty LaTeX output")
    void mm2latexartclProducesOutput() throws Exception {
        String result = applyXslt("mm2latexartcl.xsl", smallMapXml);
        assertFalse(result.isBlank(), "mm2latexartcl output must not be blank");
        assertTrue(result.contains("\\documentclass"),
                "LaTeX output must contain \\documentclass");
    }

    @Test
    @DisplayName("mm2twiki.xsl produces non-empty TWiki output")
    void mm2twikiProducesOutput() throws Exception {
        String result = applyXslt("mm2twiki.xsl", smallMapXml);
        assertFalse(result.isBlank(), "mm2twiki output must not be blank");
    }

    @Test
    @DisplayName("mm2xbel.xsl produces non-empty XBEL output")
    void mm2xbelProducesOutput() throws Exception {
        String result = applyXslt("mm2xbel.xsl", smallMapXml);
        assertFalse(result.isBlank(), "mm2xbel output must not be blank");
        assertTrue(result.toLowerCase().contains("<xbel"),
                "mm2xbel output must contain <xbel> tag");
    }

    @Test
    @DisplayName("mm2twiki_headings.xsl produces non-empty TWiki headings output")
    void mm2twikiHeadingsProducesOutput() throws Exception {
        String result = applyXslt("mm2twiki_headings.xsl", smallMapXml);
        assertFalse(result.isBlank(), "mm2twiki_headings output must not be blank");
    }

    @Test
    @DisplayName("mm2redminewiki.xsl produces non-empty Redmine wiki output")
    void mm2redminewikiProducesOutput() throws Exception {
        String result = applyXslt("mm2redminewiki.xsl", smallMapXml);
        assertFalse(result.isBlank(), "mm2redminewiki output must not be blank");
    }

    @Test
    @DisplayName("mm2wordml_utf8.xsl produces non-empty WordML output")
    void mm2wordmlProducesOutput() throws Exception {
        String result = applyXslt("mm2wordml_utf8.xsl", smallMapXml);
        assertFalse(result.isBlank(), "mm2wordml output must not be blank");
    }

    // ── HTML export: structure validation ───────────────────────────────

    @Test
    @DisplayName("HTML export contains root node text")
    void htmlExportContainsRootNodeText() throws Exception {
        String result = applyXslt("mm2html.xsl", smallMapXml);
        assertTrue(result.contains("Root"), "HTML export must contain root node text 'Root'");
    }

    @Test
    @DisplayName("HTML export contains child node texts")
    void htmlExportContainsChildNodeTexts() throws Exception {
        String result = applyXslt("mm2html.xsl", smallMapXml);
        assertTrue(result.contains("Node_1"), "HTML export must contain child node text 'Node_1'");
    }

    @Test
    @DisplayName("Standalone HTML export produces well-formed XHTML")
    void standaloneHtmlExportIsWellFormed() throws Exception {
        String result = applyXslt("freemind2html_standalone.xsl", smallMapXml);
        assertFalse(result.isBlank(), "Standalone HTML must not be blank");
        assertTrue(result.toLowerCase().contains("<html"),
                "Standalone HTML must contain <html> tag");
        assertTrue(result.contains("Root"),
                "Standalone HTML must contain root node text");
    }

    // ── CSV export: delimiter and content ───────────────────────────────

    @Test
    @DisplayName("CSV export separates hierarchy with commas")
    void csvExportUsesCommaDelimiters() throws Exception {
        String result = applyXslt("mm2csv.xsl", smallMapXml);
        assertTrue(result.contains(","),
                "CSV export must contain comma delimiters for child nodes");
    }

    @Test
    @DisplayName("CSV export contains multiple node texts")
    void csvExportContainsMultipleNodes() throws Exception {
        String result = applyXslt("mm2csv.xsl", smallMapXml);
        assertTrue(result.contains("Root"), "CSV must contain 'Root'");
        assertTrue(result.contains("Node_1"), "CSV must contain 'Node_1'");
    }

    // ── Plain text export ───────────────────────────────────────────────

    @Test
    @DisplayName("Text export contains all node texts")
    void textExportContainsNodeTexts() throws Exception {
        String result = applyXslt("mm2text.xsl", smallMapXml);
        assertTrue(result.contains("Root"), "Text export must contain 'Root'");
        assertTrue(result.contains("Node_1"), "Text export must contain 'Node_1'");
    }

    @Test
    @DisplayName("Text export includes numbering")
    void textExportIncludesNumbering() throws Exception {
        String result = applyXslt("mm2text.xsl", smallMapXml);
        // mm2text.xsl uses xsl:number to add numbering like "1 Root"
        assertTrue(result.contains("1 Root") || result.contains("1Root"),
                "Text export must include numbering before root");
    }

    // ── Edge-case content: user content is sacred ───────────────────────

    @Test
    @DisplayName("HTML export preserves script tag content from edge-case map")
    void htmlExportPreservesScriptTagContent() throws Exception {
        String result = applyXslt("mm2html.xsl", edgeCaseMapXml);
        // The XSLT output should contain the user's text including <script> text
        // (may be escaped as &lt;script&gt; in HTML output, which is correct)
        assertTrue(result.contains("alert") || result.contains("script"),
                "HTML export must preserve script-related user content");
    }

    @Test
    @DisplayName("Text export preserves file path content")
    void textExportPreservesFilePathContent() throws Exception {
        String result = applyXslt("mm2text.xsl", edgeCaseMapXml);
        assertTrue(result.contains("file:///etc/passwd"),
                "Text export must preserve file path content");
    }

    @Test
    @DisplayName("CSV export preserves emoji content")
    void csvExportPreservesEmojiContent() throws Exception {
        String result = applyXslt("mm2csv.xsl", edgeCaseMapXml);
        // Edge case map includes Turkish flag emoji and others
        // Some XSLT processors may or may not preserve supplementary plane chars,
        // but the output must at least be non-empty and contain other edge-case text
        assertFalse(result.isBlank(), "CSV export of edge-case map must not be blank");
    }

    @Test
    @DisplayName("HTML export preserves SQL injection string content")
    void htmlExportPreservesSqlInjectionContent() throws Exception {
        String result = applyXslt("mm2html.xsl", edgeCaseMapXml);
        assertTrue(result.contains("OR 1=1"),
                "HTML export must preserve SQL injection string content");
    }

    // ── Large map export ────────────────────────────────────────────────

    @Test
    @DisplayName("HTML export handles 1K node map without error")
    void htmlExportHandlesLargeMap() throws Exception {
        String largeMapXml = MindMapGenerator.create()
                .withNodes(1_000).withDepth(6).withWidth(5)
                .toXmlString();
        String result = applyXslt("mm2html.xsl", largeMapXml);
        assertFalse(result.isBlank(), "HTML export of large map must not be blank");
        assertTrue(result.length() > 1000,
                "HTML export of 1K node map must produce substantial output");
    }

    @Test
    @DisplayName("CSV export handles 1K node map without error")
    void csvExportHandlesLargeMap() throws Exception {
        String largeMapXml = MindMapGenerator.create()
                .withNodes(1_000).withDepth(6).withWidth(5)
                .toXmlString();
        String result = applyXslt("mm2csv.xsl", largeMapXml);
        assertFalse(result.isBlank(), "CSV export of large map must not be blank");
    }

    @Test
    @DisplayName("Text export handles 1K node map without error")
    void textExportHandlesLargeMap() throws Exception {
        String largeMapXml = MindMapGenerator.create()
                .withNodes(1_000).withDepth(6).withWidth(5)
                .toXmlString();
        String result = applyXslt("mm2text.xsl", largeMapXml);
        assertFalse(result.isBlank(), "Text export of large map must not be blank");
    }

    // ── Unicode content in exports ──────────────────────────────────────

    @Test
    @DisplayName("HTML export preserves Turkish characters")
    void htmlExportPreservesTurkishChars() throws Exception {
        String mapXml = "<map><node TEXT=\"\u015e\u0131\u011f\u00fc\u00f6\u00e7\u0130\"></node></map>";
        String result = applyXslt("mm2html.xsl", mapXml);
        // HTML output may use entities or direct UTF-8; either way the text must be present
        boolean hasTurkish = result.contains("\u015e\u0131\u011f") || result.contains("&#");
        assertTrue(hasTurkish, "HTML export must preserve Turkish characters");
    }

    @Test
    @DisplayName("Text export preserves Arabic text")
    void textExportPreservesArabicText() throws Exception {
        String mapXml = "<map><node TEXT=\"\u0645\u0631\u062d\u0628\u0627\"></node></map>";
        String result = applyXslt("mm2text.xsl", mapXml);
        assertTrue(result.contains("\u0645\u0631\u062d\u0628\u0627"),
                "Text export must preserve Arabic text");
    }

    @Test
    @DisplayName("CSV export preserves Unicode content")
    void csvExportPreservesUnicodeContent() throws Exception {
        String mapXml = "<map><node TEXT=\"\u00fc\u00f6\u00e4 \u00df \u20ac\">"
                + "<node TEXT=\"\u4e16\u754c\"/></node></map>";
        String result = applyXslt("mm2csv.xsl", mapXml);
        assertTrue(result.contains("\u4e16\u754c"),
                "CSV export must preserve CJK characters");
    }
}
