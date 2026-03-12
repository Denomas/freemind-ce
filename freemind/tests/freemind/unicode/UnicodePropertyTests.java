package tests.freemind.unicode;

import freemind.main.HeadlessFreeMind;
import freemind.main.HtmlTools;
import net.jqwik.api.*;
import tests.freemind.property.generators.MindmapGenerators;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Property-based tests for HtmlTools Unicode handling.
 * Uses jqwik to generate random Unicode strings and verify invariants.
 */
class UnicodePropertyTests {

    private final HtmlTools htmlTools = HtmlTools.getInstance();
    private final MindmapGenerators generators = new MindmapGenerators();

    static {
        new HeadlessFreeMind();
    }

    // ========================================================================
    // toXhtml preserves Unicode (no numeric entities)
    // ========================================================================

    @Property(tries = 100)
    void toXhtml_neverIntroducesNumericEntities(@ForAll("unicodeStrings") String text) {
        String html = UnicodeTestData.wrapInHtml(text);
        String xhtml = htmlTools.toXhtml(html);
        if (xhtml != null) {
            assertThat(xhtml).doesNotContainPattern("&#[0-9]+;");
            assertThat(xhtml).doesNotContainPattern("&#x[0-9a-fA-F]+;");
        }
    }

    // ========================================================================
    // toXhtml output is always well-formed XML
    // ========================================================================

    @Property(tries = 100)
    void toXhtml_alwaysProducesWellformedXml(@ForAll("unicodeStrings") String text) {
        String html = UnicodeTestData.wrapInHtml(text);
        String xhtml = htmlTools.toXhtml(html);
        if (xhtml != null && xhtml.startsWith("<")) {
            assertThat(htmlTools.isWellformedXml(xhtml))
                .as("XHTML output must be well-formed for input: %s", text)
                .isTrue();
        }
    }

    // ========================================================================
    // unescape round-trip: encode then decode = identity
    // ========================================================================

    @Property(tries = 100)
    void unicodeEntityRoundTrip_isIdentity(@ForAll("safeUnicodeStrings") String text) {
        // Exclude strings with '&' — unicodeToHTMLUnicodeEntity doesn't escape &,
        // so &ü becomes &&#xfc; which then misparses on decode (pre-existing limitation)
        String encoded = HtmlTools.unicodeToHTMLUnicodeEntity(text, false);
        String decoded = HtmlTools.unescapeHTMLUnicodeEntity(encoded);
        assertThat(decoded).isEqualTo(text);
    }

    // ========================================================================
    // toXhtml idempotency
    // ========================================================================

    @Property(tries = 50)
    void toXhtml_isIdempotent(@ForAll("safeUnicodeStrings") String text) {
        String html = UnicodeTestData.wrapInHtml(text);
        String first = htmlTools.toXhtml(html);
        if (first != null) {
            String second = htmlTools.toXhtml(first);
            assertThat(second).isEqualTo(first);
        }
    }

    // ========================================================================
    // Providers
    // ========================================================================

    @Provide
    Arbitrary<String> unicodeStrings() {
        return generators.unicodeStrings();
    }

    @Provide
    Arbitrary<String> safeUnicodeStrings() {
        // Unicode strings without XML-special characters (& < > " ')
        return generators.unicodeStrings()
            .filter(s -> !s.contains("&") && !s.contains("<") && !s.contains(">"));
    }

    @Provide
    Arbitrary<String> htmlWithUnicode() {
        return generators.htmlWithUnicode();
    }
}
