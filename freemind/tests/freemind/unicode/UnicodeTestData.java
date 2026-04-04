package tests.freemind.unicode;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Constants for Unicode test data across multiple scripts and edge cases.
 */
public final class UnicodeTestData {

    private UnicodeTestData() {}

    // Turkish
    public static final String TURKISH = "ğüşöçıİĞÜŞÖÇ";
    public static final String TURKISH_SENTENCE = "Türkçe karakterler: ğüşöçıİĞÜŞÖÇ";

    // German
    public static final String GERMAN = "äöüßÄÖÜ";
    public static final String GERMAN_SENTENCE = "Deutsche Umlaute: äöüß und Straße";

    // Cyrillic
    public static final String CYRILLIC = "Привет мир";

    // Arabic
    public static final String ARABIC = "مرحبا بالعالم";

    // Hebrew
    public static final String HEBREW = "שלום עולם";

    // CJK (Chinese)
    public static final String CJK = "你好世界";

    // Japanese
    public static final String JAPANESE = "こんにちは世界";

    // Korean
    public static final String KOREAN = "안녕하세요";

    // Thai
    public static final String THAI = "สวัสดีชาวโลก";

    // Devanagari (Hindi)
    public static final String DEVANAGARI = "नमस्ते दुनिया";

    // Emoji (basic, BMP-safe)
    public static final String EMOJI = "\u2728\u2764\u2602";

    // Mixed scripts
    public static final String MIXED_SCRIPTS = "Hello Привет 你好 مرحبا שלום";

    // All non-ASCII test strings for iteration
    public static final List<String> ALL_SCRIPTS = Collections.unmodifiableList(Arrays.asList(
        TURKISH, GERMAN, CYRILLIC, ARABIC, HEBREW,
        CJK, JAPANESE, KOREAN, THAI, DEVANAGARI, EMOJI, MIXED_SCRIPTS
    ));

    public static final List<String> ALL_SCRIPT_NAMES = Collections.unmodifiableList(Arrays.asList(
        "Turkish", "German", "Cyrillic", "Arabic", "Hebrew",
        "CJK", "Japanese", "Korean", "Thai", "Devanagari", "Emoji", "Mixed"
    ));

    // HTML-wrapped versions for note editor testing
    public static String wrapInHtml(String text) {
        return "<html>\n  <head>\n    \n  </head>\n  <body>\n    <p>\n      "
            + text + "\n    </p>\n  </body>\n</html>\n";
    }

    // Edge cases
    public static final String ENTITY_ENCODED_TURKISH = "&#287;&#252;&#351;&#246;&#231;&#305;";
    public static final String HEX_ENTITY_ENCODED_TURKISH = "&#x11f;&#xfc;&#x15f;&#xf6;&#xe7;&#x131;";

    // HTML entities that should be preserved (named entities)
    public static final String NAMED_ENTITIES = "&amp; &lt; &gt; &quot;";
}
