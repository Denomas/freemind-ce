package tests.freemind.unicode;

import freemind.main.HtmlTools;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static tests.freemind.unicode.UnicodeTestData.*;

/**
 * Unit tests for HtmlTools Unicode handling.
 * Verifies that toXhtml(), unescapeHTMLUnicodeEntity(), htmlToPlain(),
 * and toHtml() correctly preserve UTF-8 characters across all scripts.
 */
class HtmlUnicodeTests {

    private final HtmlTools htmlTools = HtmlTools.getInstance();

    @BeforeAll
    static void init() {
        new freemind.main.HeadlessFreeMind();
    }

    // ========================================================================
    // toXhtml() preserves UTF-8
    // ========================================================================

    @Test
    void toXhtml_preservesTurkishCharacters() {
        String html = wrapInHtml(TURKISH);
        String xhtml = htmlTools.toXhtml(html);
        assertThat(xhtml).isNotNull();
        assertThat(xhtml).contains(TURKISH);
        assertThat(xhtml).doesNotContain("&#287;");
        assertThat(xhtml).doesNotContain("&#x11f;");
    }

    @Test
    void toXhtml_preservesGermanCharacters() {
        String html = wrapInHtml(GERMAN);
        String xhtml = htmlTools.toXhtml(html);
        assertThat(xhtml).isNotNull();
        assertThat(xhtml).contains(GERMAN);
    }

    @Test
    void toXhtml_preservesCyrillicCharacters() {
        String html = wrapInHtml(CYRILLIC);
        String xhtml = htmlTools.toXhtml(html);
        assertThat(xhtml).isNotNull();
        assertThat(xhtml).contains(CYRILLIC);
    }

    @Test
    void toXhtml_preservesArabicCharacters() {
        String html = wrapInHtml(ARABIC);
        String xhtml = htmlTools.toXhtml(html);
        assertThat(xhtml).isNotNull();
        assertThat(xhtml).contains(ARABIC);
    }

    @Test
    void toXhtml_preservesCJKCharacters() {
        String html = wrapInHtml(CJK);
        String xhtml = htmlTools.toXhtml(html);
        assertThat(xhtml).isNotNull();
        assertThat(xhtml).contains(CJK);
    }

    @Test
    void toXhtml_preservesJapaneseCharacters() {
        String html = wrapInHtml(JAPANESE);
        String xhtml = htmlTools.toXhtml(html);
        assertThat(xhtml).isNotNull();
        assertThat(xhtml).contains(JAPANESE);
    }

    @Test
    void toXhtml_preservesKoreanCharacters() {
        String html = wrapInHtml(KOREAN);
        String xhtml = htmlTools.toXhtml(html);
        assertThat(xhtml).isNotNull();
        assertThat(xhtml).contains(KOREAN);
    }

    @Test
    void toXhtml_preservesMixedScripts() {
        String html = wrapInHtml(MIXED_SCRIPTS);
        String xhtml = htmlTools.toXhtml(html);
        assertThat(xhtml).isNotNull();
        assertThat(xhtml).contains("Hello");
        assertThat(xhtml).contains("Привет");
        assertThat(xhtml).contains("你好");
    }

    @Test
    void toXhtml_allScripts_noNumericEntities() {
        for (int i = 0; i < ALL_SCRIPTS.length; i++) {
            String html = wrapInHtml(ALL_SCRIPTS[i]);
            String xhtml = htmlTools.toXhtml(html);
            assertThat(xhtml)
                .as("Script: %s", ALL_SCRIPT_NAMES[i])
                .isNotNull();
            // Should not contain numeric HTML entities for non-ASCII chars
            assertThat(xhtml)
                .as("Script %s should not have numeric entities", ALL_SCRIPT_NAMES[i])
                .doesNotContainPattern("&#[0-9]+;");
        }
    }

    @Test
    void toXhtml_outputIsWellformedXml() {
        for (String script : ALL_SCRIPTS) {
            String html = wrapInHtml(script);
            String xhtml = htmlTools.toXhtml(html);
            assertThat(xhtml).isNotNull();
            assertThat(htmlTools.isWellformedXml(xhtml))
                .as("XHTML should be well-formed for: %s", script)
                .isTrue();
        }
    }

    // ========================================================================
    // toXhtml() idempotency
    // ========================================================================

    @Test
    void toXhtml_isIdempotent() {
        for (String script : ALL_SCRIPTS) {
            String html = wrapInHtml(script);
            String first = htmlTools.toXhtml(html);
            assertThat(first).isNotNull();
            String second = htmlTools.toXhtml(first);
            assertThat(second)
                .as("toXhtml should be idempotent for: %s", script)
                .isEqualTo(first);
        }
    }

    // ========================================================================
    // unescapeHTMLUnicodeEntity() correctness
    // ========================================================================

    @Test
    void unescape_decodesDecimalEntities() {
        String result = HtmlTools.unescapeHTMLUnicodeEntity(ENTITY_ENCODED_TURKISH);
        assertThat(result).isEqualTo("ğüşöçı");
    }

    @Test
    void unescape_decodesHexEntities() {
        String result = HtmlTools.unescapeHTMLUnicodeEntity(HEX_ENTITY_ENCODED_TURKISH);
        assertThat(result).isEqualTo("ğüşöçı");
    }

    @Test
    void unescape_preservesNamedEntities() {
        String result = HtmlTools.unescapeHTMLUnicodeEntity(NAMED_ENTITIES);
        assertThat(result).isEqualTo(NAMED_ENTITIES);
    }

    @Test
    void unescape_preservesPlainUTF8() {
        for (String script : ALL_SCRIPTS) {
            String result = HtmlTools.unescapeHTMLUnicodeEntity(script);
            assertThat(result)
                .as("Unescaping plain UTF-8 should be identity for: %s", script)
                .isEqualTo(script);
        }
    }

    // ========================================================================
    // unicodeToHTMLUnicodeEntity / unescapeHTMLUnicodeEntity round-trip
    // ========================================================================

    @Test
    void encodeAndDecode_roundTrip() {
        for (String script : ALL_SCRIPTS) {
            String encoded = HtmlTools.unicodeToHTMLUnicodeEntity(script, false);
            String decoded = HtmlTools.unescapeHTMLUnicodeEntity(encoded);
            assertThat(decoded)
                .as("Round-trip should preserve: %s", script)
                .isEqualTo(script);
        }
    }

    // ========================================================================
    // htmlToPlain() with Unicode
    // ========================================================================

    @Test
    void htmlToPlain_preservesUnicodeText() {
        for (String script : ALL_SCRIPTS) {
            String html = wrapInHtml(script);
            String plain = HtmlTools.htmlToPlain(html);
            assertThat(plain)
                .as("htmlToPlain should preserve: %s", script)
                .contains(script);
        }
    }

    // ========================================================================
    // toHtml() preserves Unicode
    // ========================================================================

    @Test
    void toHtml_preservesUnicodeInXhtml() {
        for (String script : ALL_SCRIPTS) {
            String html = wrapInHtml(script);
            String xhtml = htmlTools.toXhtml(html);
            assertThat(xhtml).isNotNull();
            String backToHtml = htmlTools.toHtml(xhtml);
            assertThat(backToHtml)
                .as("toHtml should preserve Unicode for: %s", script)
                .contains(script);
        }
    }
}
