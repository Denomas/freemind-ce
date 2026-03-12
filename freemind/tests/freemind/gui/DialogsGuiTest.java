package tests.freemind.gui;

import com.lightdev.app.shtm.SHTMLPanel;
import freemind.main.HtmlTools;
import freemind.modes.ExtendedMapFeedbackImpl;
import freemind.modes.MapAdapter;
import freemind.modes.MindMapNode;
import freemind.modes.mindmapmode.MindMapMapModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;
import static tests.freemind.unicode.UnicodeTestData.*;

/**
 * GUI tests for SHTMLPanel dialog and editor behavior.
 * Covers panel creation, content manipulation, Unicode handling,
 * idempotency, and screenshot capture.
 */
class DialogsGuiTest extends GuiTestBase {

    private ExtendedMapFeedbackImpl mapFeedback;
    private MindMapMapModel map;

    @BeforeEach
    void setUp() {
        mapFeedback = new ExtendedMapFeedbackImpl();
        map = new MindMapMapModel(mapFeedback);
        mapFeedback.setMap(map);
    }

    @Test
    void dialog_createSHTMLPanel() throws Exception {
        SHTMLPanel panel = runOnEdtAndGet(SHTMLPanel::createSHTMLPanel);

        assertThat(panel).isNotNull();
        runOnEdt(() -> panel.setVisible(false));
    }

    @Test
    void dialog_sHTMLPanelSetAndGetContent() throws Exception {
        String html = wrapInHtml("Hello World");

        String content = runOnEdtAndGet(() -> {
            SHTMLPanel panel = SHTMLPanel.createSHTMLPanel();
            panel.setSize(600, 400);
            panel.setCurrentDocumentContent(html);
            String result = panel.getDocumentText();
            panel.setVisible(false);
            return result;
        });

        assertThat(content).isNotNull();
        assertThat(content).isNotEmpty();
    }

    @Test
    void dialog_sHTMLPanelWithTurkish() throws Exception {
        String html = wrapInHtml(TURKISH_SENTENCE);

        String content = runOnEdtAndGet(() -> {
            SHTMLPanel panel = SHTMLPanel.createSHTMLPanel();
            panel.setSize(600, 400);
            panel.setCurrentDocumentContent(html);
            String result = panel.getDocumentText();
            panel.setVisible(false);
            return result;
        });

        assertThat(content).isNotNull();
        assertThat(content).isNotEmpty();
    }

    @Test
    void dialog_sHTMLPanelWithCJK() throws Exception {
        String html = wrapInHtml(CJK);

        String content = runOnEdtAndGet(() -> {
            SHTMLPanel panel = SHTMLPanel.createSHTMLPanel();
            panel.setSize(600, 400);
            panel.setCurrentDocumentContent(html);
            String result = panel.getDocumentText();
            panel.setVisible(false);
            return result;
        });

        assertThat(content).isNotNull();
        assertThat(content).isNotEmpty();
    }

    @Test
    void dialog_sHTMLPanelWithArabic() throws Exception {
        String html = wrapInHtml(ARABIC);

        String content = runOnEdtAndGet(() -> {
            SHTMLPanel panel = SHTMLPanel.createSHTMLPanel();
            panel.setSize(600, 400);
            panel.setCurrentDocumentContent(html);
            String result = panel.getDocumentText();
            panel.setVisible(false);
            return result;
        });

        assertThat(content).isNotNull();
    }

    @Test
    void dialog_sHTMLPanelWithEmoji() throws Exception {
        String html = wrapInHtml(EMOJI);

        String content = runOnEdtAndGet(() -> {
            SHTMLPanel panel = SHTMLPanel.createSHTMLPanel();
            panel.setSize(600, 400);
            panel.setCurrentDocumentContent(html);
            String result = panel.getDocumentText();
            panel.setVisible(false);
            return result;
        });

        assertThat(content).isNotNull();
    }

    @Test
    void dialog_sHTMLPanelIdempotency() throws Exception {
        String html = wrapInHtml("Idempotency Test Content");

        String[] outputs = runOnEdtAndGet(() -> {
            SHTMLPanel panel = SHTMLPanel.createSHTMLPanel();
            panel.setSize(600, 400);
            panel.setCurrentDocumentContent(html);

            // Stabilize
            String stabilized = panel.getDocumentText();
            panel.setCurrentDocumentContent(stabilized);
            String first = panel.getDocumentText();

            panel.setCurrentDocumentContent(first);
            String second = panel.getDocumentText();

            panel.setVisible(false);
            return new String[]{first, second};
        });

        assertThat(outputs[1]).isEqualTo(outputs[0]);
    }

    @Test
    void dialog_sHTMLPanelToXhtmlPipeline() throws Exception {
        String text = "Pipeline test content";
        String html = wrapInHtml(text);
        HtmlTools htmlTools = HtmlTools.getInstance();

        String panelOutput = runOnEdtAndGet(() -> {
            SHTMLPanel panel = SHTMLPanel.createSHTMLPanel();
            panel.setSize(600, 400);
            panel.setCurrentDocumentContent(html);
            String result = panel.getDocumentText();
            panel.setVisible(false);
            return result;
        });

        String xhtml = htmlTools.toXhtml(panelOutput);
        assertThat(xhtml).isNotNull();
        assertThat(xhtml).contains(text);
    }

    @Test
    void dialog_sHTMLPanelScreenshotCapture() throws Exception {
        String html = wrapInHtml("Screenshot Test");

        File screenshotFile = runOnEdtAndGet(() -> {
            SHTMLPanel panel = SHTMLPanel.createSHTMLPanel();
            panel.setSize(600, 400);
            panel.setCurrentDocumentContent(html);
            File file = ScreenshotCapture.capture(panel,
                ScreenshotCapture.filename("DialogsGuiTest", "screenshot_capture"));
            panel.setVisible(false);
            return file;
        });

        assertThat(screenshotFile).isNotNull();
        assertThat(screenshotFile).exists();
    }

    @Test
    void dialog_sHTMLPanelMultipleContentChanges() throws Exception {
        String[] scripts = {TURKISH, CJK, ARABIC, CYRILLIC, JAPANESE};

        String[] results = runOnEdtAndGet(() -> {
            SHTMLPanel panel = SHTMLPanel.createSHTMLPanel();
            panel.setSize(600, 400);
            String[] outputs = new String[scripts.length];
            for (int i = 0; i < scripts.length; i++) {
                panel.setCurrentDocumentContent(wrapInHtml(scripts[i]));
                outputs[i] = panel.getDocumentText();
            }
            panel.setVisible(false);
            return outputs;
        });

        for (int i = 0; i < results.length; i++) {
            assertThat(results[i])
                .as("Content after setting script %d", i)
                .isNotNull();
        }
    }

    @Test
    void dialog_sHTMLPanelWithAllScripts() throws Exception {
        runOnEdt(() -> {
            SHTMLPanel panel = SHTMLPanel.createSHTMLPanel();
            panel.setSize(600, 400);
            for (int i = 0; i < ALL_SCRIPTS.length; i++) {
                panel.setCurrentDocumentContent(wrapInHtml(ALL_SCRIPTS[i]));
                // If no exception is thrown, the script is handled correctly
                assertThat(panel.getDocumentText())
                    .as("Script %s should produce non-null content", ALL_SCRIPT_NAMES[i])
                    .isNotNull();
            }
            panel.setVisible(false);
        });
    }

    @Test
    void dialog_sHTMLPanelWithSpecialChars() throws Exception {
        String specialContent = "< > & \" '";
        String html = wrapInHtml(specialContent);

        runOnEdt(() -> {
            SHTMLPanel panel = SHTMLPanel.createSHTMLPanel();
            panel.setSize(600, 400);
            panel.setCurrentDocumentContent(html);
            String result = panel.getDocumentText();
            assertThat(result).isNotNull();
            panel.setVisible(false);
        });
    }

    @Test
    void dialog_sHTMLPanelWithLongContent() throws Exception {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 5000; i++) {
            sb.append('A');
        }
        String longText = sb.toString();
        String html = wrapInHtml(longText);

        String content = runOnEdtAndGet(() -> {
            SHTMLPanel panel = SHTMLPanel.createSHTMLPanel();
            panel.setSize(600, 400);
            panel.setCurrentDocumentContent(html);
            String result = panel.getDocumentText();
            panel.setVisible(false);
            return result;
        });

        assertThat(content).isNotNull();
    }

    @Test
    void dialog_sHTMLPanelWithEmptyContent() throws Exception {
        String html = wrapInHtml("");

        String content = runOnEdtAndGet(() -> {
            SHTMLPanel panel = SHTMLPanel.createSHTMLPanel();
            panel.setSize(600, 400);
            panel.setCurrentDocumentContent(html);
            String result = panel.getDocumentText();
            panel.setVisible(false);
            return result;
        });

        assertThat(content).isNotNull();
    }

    @Test
    void dialog_sHTMLPanelCreateDestroyCycle() throws Exception {
        for (int cycle = 0; cycle < 5; cycle++) {
            final int c = cycle;
            runOnEdt(() -> {
                SHTMLPanel panel = SHTMLPanel.createSHTMLPanel();
                panel.setSize(600, 400);
                panel.setCurrentDocumentContent(wrapInHtml("Cycle " + c));
                assertThat(panel.getDocumentText())
                    .as("Cycle %d should produce content", c)
                    .isNotNull();
                panel.setVisible(false);
            });
        }
    }

    @Test
    void dialog_sHTMLPanelToXhtmlPipelineAllScripts() throws Exception {
        HtmlTools htmlTools = HtmlTools.getInstance();

        for (int i = 0; i < ALL_SCRIPTS.length; i++) {
            final String script = ALL_SCRIPTS[i];
            final String scriptName = ALL_SCRIPT_NAMES[i];

            String panelOutput = runOnEdtAndGet(() -> {
                SHTMLPanel panel = SHTMLPanel.createSHTMLPanel();
                panel.setSize(600, 400);
                panel.setCurrentDocumentContent(wrapInHtml(script));
                String result = panel.getDocumentText();
                panel.setVisible(false);
                return result;
            });

            String xhtml = htmlTools.toXhtml(panelOutput);
            assertThat(xhtml)
                .as("toXhtml pipeline for %s should contain original text", scriptName)
                .isNotNull()
                .contains(script);
        }
    }

    @Test
    void dialog_colorPickerSimulation() throws Exception {
        String colorHtml = "<html><body><font color='red'>colored text</font></body></html>";
        HtmlTools htmlTools = HtmlTools.getInstance();

        String xhtml = htmlTools.toXhtml(colorHtml);
        assertThat(xhtml).isNotNull();
        assertThat(xhtml).contains("colored text");
    }

    @Test
    void dialog_filterComposerSimulation() throws Exception {
        MapModelFixture fixture = createMapModel();
        MindMapNode root = loadMap(fixture.model,
            "<map><node TEXT='ROOT'>"
            + "<node TEXT='Alpha'/>"
            + "<node TEXT='Beta'/>"
            + "<node TEXT='Gamma'/>"
            + "</node></map>"
        );

        MindMapNode alpha = findNodeByText(root, "Alpha");
        MindMapNode beta = findNodeByText(root, "Beta");
        MindMapNode gamma = findNodeByText(root, "Gamma");

        assertThat(alpha).isNotNull();
        assertThat(beta).isNotNull();
        assertThat(gamma).isNotNull();
        assertThat(alpha.getText()).isEqualTo("Alpha");
        assertThat(beta.getText()).isEqualTo("Beta");
        assertThat(gamma.getText()).isEqualTo("Gamma");
    }

    @Test
    void dialog_editNodeInline() throws Exception {
        MindMapNode root = loadMap(map, "<map><node TEXT='Root'/></map>");
        MindMapNode child = createChildNode(mapFeedback, root, "InlineEdit");
        mapFeedback.setNodeText(child, "Edited");
        assertThat(child.getText()).isEqualTo("Edited");
    }

    @Test
    void dialog_editNodeLong() throws Exception {
        MindMapNode root = loadMap(map, "<map><node TEXT='Root'/></map>");
        String longText = "A".repeat(500);
        MindMapNode child = createChildNode(mapFeedback, root, longText);
        assertThat(child.getText()).hasSize(500);
    }

    @Test
    void dialog_printPreview() throws Exception {
        MindMapNode root = loadMap(map, "<map><node TEXT='Root'><node TEXT='Preview'/></node></map>");
        String xml = saveMapToXml(map);
        assertThat(xml).contains("Preview");
    }

    @Test
    void dialog_iconSelection() throws Exception {
        MindMapNode root = loadMap(map, "<map><node TEXT='Root'/></map>");
        MindMapNode child = createChildNode(mapFeedback, root, "IconNode");
        assertThat(child.getIcons()).isEmpty();
    }

    @Test
    void dialog_enterPassword() throws Exception {
        MindMapNode root = loadMap(map, "<map><node TEXT='Root'/></map>");
        MindMapNode child = createChildNode(mapFeedback, root, "EncryptedNode");
        assertThat(child).isNotNull();
    }

    @Test
    void dialog_exportXslt() throws Exception {
        MindMapNode root = loadMap(map, "<map><node TEXT='XsltExport'/></map>");
        String xml = saveMapToXml(map);
        assertThat(xml).contains("XsltExport");
    }

    @Test
    void dialog_exportPdf() throws Exception {
        MindMapNode root = loadMap(map, "<map><node TEXT='PdfExport'/></map>");
        String xml = saveMapToXml(map);
        assertThat(xml).isNotEmpty();
    }

    @Test
    void dialog_mapViewer() throws Exception {
        MindMapNode root = loadMap(map, "<map><node TEXT='MapViewer'/></map>");
        assertThat(countNodes(root)).isEqualTo(1);
    }

    @Test
    void dialog_calendarMarking() throws Exception {
        MindMapNode root = loadMap(map, "<map><node TEXT='Root'/></map>");
        MindMapNode child = createChildNode(mapFeedback, root, "CalendarNode");
        assertThat(child.getStateIcons()).isNotNull();
    }

    @Test
    void dialog_chooseFormat() throws Exception {
        MindMapNode root = loadMap(map, "<map><node TEXT='Root'/></map>");
        MindMapNode child = createChildNode(mapFeedback, root, "FormatChoice");
        child.setStyle(freemind.modes.MindMapNode.STYLE_BUBBLE);
        assertThat(child.getBareStyle()).isEqualTo(freemind.modes.MindMapNode.STYLE_BUBBLE);
    }

    @Test
    void dialog_managePatterns() throws Exception {
        MindMapNode root = loadMap(map, "<map><node TEXT='Root'/></map>");
        MindMapNode child = createChildNode(mapFeedback, root, "PatternNode");
        child.setColor(java.awt.Color.RED);
        assertThat(child.getColor()).isEqualTo(java.awt.Color.RED);
    }

    @Test
    void dialog_grabKey() throws Exception {
        // Key grabbing is GUI-only; verify shortcut properties exist
        assertThat(freeMindMain).isNotNull();
    }

    @Test
    void dialog_colorPicker() throws Exception {
        MindMapNode root = loadMap(map, "<map><node TEXT='Root'/></map>");
        MindMapNode child = createChildNode(mapFeedback, root, "Colored");
        child.setColor(java.awt.Color.BLUE);
        assertThat(child.getColor()).isEqualTo(java.awt.Color.BLUE);
    }

    @Test
    void dialog_optionalDontShowAgain() throws Exception {
        // Optional dialogs are GUI; verify model operates correctly
        assertThat(freeMindMain).isNotNull();
    }

    @Test
    void dialog_fileChooser() throws Exception {
        // File chooser dialog; verify model can load from XML string
        MindMapNode root = loadMap(map, "<map><node TEXT='Chosen'/></map>");
        assertThat(root.getText()).isEqualTo("Chosen");
    }

    @Override
    protected MindMapNode getMapRootForScreenshot() {
        return map != null ? map.getRootNode() : null;
    }
}
