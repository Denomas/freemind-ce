package tests.freemind.gui;

import com.lightdev.app.shtm.SHTMLPanel;
import freemind.main.HtmlTools;
import org.junit.jupiter.api.Test;

import javax.swing.*;

import static org.assertj.core.api.Assertions.assertThat;
import static tests.freemind.unicode.UnicodeTestData.*;

/**
 * GUI test for the note editor (SHTMLPanel) Unicode rendering.
 * Creates an SHTMLPanel, sets content with various scripts,
 * captures screenshots, and verifies that the full pipeline
 * (SHTMLPanel -> toXhtml -> saved XML) preserves UTF-8.
 *
 * Extends GuiTestBase for shared infrastructure.
 *
 * Note: SHTMLPanel's getDocumentText() uses Swing's HTMLEditorKit
 * which encodes non-ASCII to entities. The fix in toXhtml() decodes
 * these back to UTF-8 during save. So we verify the full pipeline.
 */
class NoteEditorUnicodeGuiTest extends GuiTestBase {

    private static final HtmlTools htmlTools = HtmlTools.getInstance();

    @Test
    void noteEditor_rendersAndPreservesTurkish() throws Exception {
        verifyScript(TURKISH_SENTENCE, "turkish");
    }

    @Test
    void noteEditor_rendersAndPreservesCJK() throws Exception {
        verifyScript(CJK, "cjk");
    }

    @Test
    void noteEditor_rendersAndPreservesArabic() throws Exception {
        verifyScript(ARABIC, "arabic");
    }

    @Test
    void noteEditor_rendersAndPreservesCyrillic() throws Exception {
        verifyScript(CYRILLIC, "cyrillic");
    }

    @Test
    void noteEditor_rendersAndPreservesJapanese() throws Exception {
        verifyScript(JAPANESE, "japanese");
    }

    @Test
    void noteEditor_rendersAndPreservesKorean() throws Exception {
        verifyScript(KOREAN, "korean");
    }

    @Test
    void noteEditor_rendersAndPreservesEmoji() throws Exception {
        verifyScript(EMOJI, "emoji");
    }

    @Test
    void noteEditor_rendersAndPreservesMixedScripts() throws Exception {
        verifyScript(MIXED_SCRIPTS, "mixed");
    }

    @Test
    void noteEditor_idempotency_allScripts() throws Exception {
        for (int i = 0; i < ALL_SCRIPTS.length; i++) {
            String scriptName = ALL_SCRIPT_NAMES[i];
            String htmlContent = wrapInHtml(ALL_SCRIPTS[i]);
            final SHTMLPanel[] panelHolder = new SHTMLPanel[1];
            final String[] outputs = new String[2];

            SwingUtilities.invokeAndWait(() -> {
                panelHolder[0] = SHTMLPanel.createSHTMLPanel();
                panelHolder[0].setSize(600, 400);
                panelHolder[0].setCurrentDocumentContent(htmlContent);
                // Stabilize: SHTMLPanel may alter structure on first round
                String stabilized = panelHolder[0].getDocumentText();
                panelHolder[0].setCurrentDocumentContent(stabilized);
                outputs[0] = panelHolder[0].getDocumentText();

                panelHolder[0].setCurrentDocumentContent(outputs[0]);
                outputs[1] = panelHolder[0].getDocumentText();
                panelHolder[0].setVisible(false);
            });

            assertThat(outputs[1])
                .as("Idempotency for %s", scriptName)
                .isEqualTo(outputs[0]);
        }
    }

    // ========================================================================
    // Helpers
    // ========================================================================

    private void verifyScript(String text, String scriptName) throws Exception {
        String htmlContent = wrapInHtml(text);
        final SHTMLPanel[] panelHolder = new SHTMLPanel[1];
        final String[] outputHolder = new String[1];

        SwingUtilities.invokeAndWait(() -> {
            panelHolder[0] = SHTMLPanel.createSHTMLPanel();
            panelHolder[0].setSize(600, 400);
            panelHolder[0].setCurrentDocumentContent(htmlContent);
            outputHolder[0] = panelHolder[0].getDocumentText();
        });

        // Capture screenshot for visual evidence
        SwingUtilities.invokeAndWait(() -> {
            ScreenshotCapture.capture(panelHolder[0],
                ScreenshotCapture.filename("NoteEditor", scriptName));
            panelHolder[0].setVisible(false);
        });

        // SHTMLPanel's getDocumentText() may entity-encode non-ASCII (Swing behavior).
        // The critical assertion: after toXhtml() processes the panel output,
        // the result must contain UTF-8 text (not entities).
        String xhtml = htmlTools.toXhtml(outputHolder[0]);
        assertThat(xhtml)
            .as("toXhtml(panel output) should contain UTF-8 %s text", scriptName)
            .isNotNull()
            .contains(text);
    }
}
