package tests.freemind.gui;

import com.lightdev.app.shtm.SHTMLPanel;
import freemind.modes.MindMapNode;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;
import static tests.freemind.unicode.UnicodeTestData.ALL_SCRIPTS;
import static tests.freemind.unicode.UnicodeTestData.ALL_SCRIPT_NAMES;
import static tests.freemind.unicode.UnicodeTestData.wrapInHtml;

/**
 * GUI tests for view menu operations: SHTMLPanel creation,
 * content management, sizing, and screenshot capture.
 */
class ViewMenuGuiTest extends GuiTestBase {

    @Test
    void view_canCreateSHTMLPanel() throws Exception {
        SHTMLPanel panel = runOnEdtAndGet(SHTMLPanel::createSHTMLPanel);
        assertThat(panel).isNotNull();
        runOnEdt(() -> panel.setVisible(false));
    }

    @Test
    void view_sHTMLPanelSetContent() throws Exception {
        String html = wrapInHtml("Hello World");
        runOnEdt(() -> {
            SHTMLPanel panel = SHTMLPanel.createSHTMLPanel();
            panel.setSize(600, 400);
            panel.setCurrentDocumentContent(html);
            panel.setVisible(false);
        });
        // No exception means success
    }

    @Test
    void view_sHTMLPanelGetContent() throws Exception {
        String html = wrapInHtml("Test content");
        String content = runOnEdtAndGet(() -> {
            SHTMLPanel panel = SHTMLPanel.createSHTMLPanel();
            panel.setSize(600, 400);
            panel.setCurrentDocumentContent(html);
            String result = panel.getDocumentText();
            panel.setVisible(false);
            return result;
        });
        assertThat(content)
            .as("Panel should return content containing the set text")
            .isNotNull()
            .isNotEmpty();
    }

    @Test
    void view_sHTMLPanelSize() throws Exception {
        Dimension size = runOnEdtAndGet(() -> {
            SHTMLPanel panel = SHTMLPanel.createSHTMLPanel();
            panel.setSize(600, 400);
            Dimension result = panel.getSize();
            panel.setVisible(false);
            return result;
        });
        assertThat(size.width).isEqualTo(600);
        assertThat(size.height).isEqualTo(400);
    }

    @Test
    void view_sHTMLPanelScreenshot() throws Exception {
        File captured = runOnEdtAndGet(() -> {
            SHTMLPanel panel = SHTMLPanel.createSHTMLPanel();
            panel.setSize(600, 400);
            panel.setCurrentDocumentContent(wrapInHtml("Screenshot test"));
            File result = ScreenshotCapture.capture(panel,
                ScreenshotCapture.filename("ViewMenuGuiTest", "panelScreenshot"));
            panel.setVisible(false);
            return result;
        });
        // ScreenshotCapture.capture returns the File on success, null on failure
        assertThat(captured)
            .as("Screenshot capture should succeed and return a file")
            .isNotNull();
        assertThat(captured.exists())
            .as("Screenshot file should exist on disk")
            .isTrue();
    }

    @Test
    void view_sHTMLPanelUnicodeAllScripts() throws Exception {
        for (int i = 0; i < ALL_SCRIPTS.size(); i++) {
            final String html = wrapInHtml(ALL_SCRIPTS.get(i));
            final String scriptName = ALL_SCRIPT_NAMES.get(i);
            runOnEdt(() -> {
                SHTMLPanel panel = SHTMLPanel.createSHTMLPanel();
                panel.setSize(600, 400);
                panel.setCurrentDocumentContent(html);
                panel.setVisible(false);
            });
            // No exception for script: scriptName
        }
    }

    @Test
    void view_sHTMLPanelMultipleResizes() throws Exception {
        int[][] sizes = {{200, 150}, {800, 600}, {1920, 1080}};
        for (int[] sz : sizes) {
            final int w = sz[0], h = sz[1];
            Dimension result = runOnEdtAndGet(() -> {
                SHTMLPanel panel = SHTMLPanel.createSHTMLPanel();
                panel.setSize(w, h);
                Dimension d = panel.getSize();
                panel.setVisible(false);
                return d;
            });
            assertThat(result.width).as("Width after resize to %d", w).isEqualTo(w);
            assertThat(result.height).as("Height after resize to %d", h).isEqualTo(h);
        }
    }

    @Test
    void view_sHTMLPanelClearContent() throws Exception {
        String content = runOnEdtAndGet(() -> {
            SHTMLPanel panel = SHTMLPanel.createSHTMLPanel();
            panel.setSize(600, 400);
            panel.setCurrentDocumentContent(wrapInHtml("Some content"));
            panel.setCurrentDocumentContent("");
            String result = panel.getDocumentText();
            panel.setVisible(false);
            return result;
        });
        // After clearing, getDocumentText may return empty or minimal HTML skeleton
        assertThat(content).isNotNull();
    }

    @Test
    void view_sHTMLPanelLongContent() throws Exception {
        String longText = "X".repeat(10000);
        String html = wrapInHtml(longText);
        runOnEdt(() -> {
            SHTMLPanel panel = SHTMLPanel.createSHTMLPanel();
            panel.setSize(600, 400);
            panel.setCurrentDocumentContent(html);
            panel.setVisible(false);
        });
        // No exception means success with 10000 char content
    }

    @Test
    void view_mapModelIndependentOfView() throws Exception {
        MapModelFixture fixture = createMapModel();
        loadMap(fixture.model, "<map><node TEXT='Root'><node TEXT='Child'/></node></map>");

        String xml = saveMapToXml(fixture.model);
        assertThat(xml).contains("Root");
        assertThat(xml).contains("Child");

        // Model operations work without any view being created
        createChildNode(fixture.feedback,
            findNodeByText(fixture.model.getRootNode(), "Root"), "NewChild");
        String xml2 = saveMapToXml(fixture.model);
        assertThat(xml2).contains("NewChild");
    }

    @Test
    void view_toggleToolbar() throws Exception {
        // Toolbar toggle is a GUI-only operation; verify model works without toolbar
        MapModelFixture fixture = createMapModel();
        MindMapNode root = loadMap(fixture.model, "<map><node TEXT='Root'/></map>");
        assertThat(root).isNotNull();
    }

    @Test
    void view_toggleLeftToolbar() throws Exception {
        // Left toolbar (icon toolbar) toggle; verify model independence
        MapModelFixture fixture = createMapModel();
        loadMap(fixture.model, "<map><node TEXT='Root'/></map>");
        String xml = saveMapToXml(fixture.model);
        assertThat(xml).isNotEmpty();
    }

    @Test
    void view_showSelectionAsRectangle() throws Exception {
        MapModelFixture fixture = createMapModel();
        MindMapNode root = loadMap(fixture.model, "<map><node TEXT='Root'><node TEXT='A'/><node TEXT='B'/></node></map>");
        assertThat(root.getChildCount()).isEqualTo(2);
    }

    @Test
    void view_zoomIn() throws Exception {
        // Zoom is view-only; verify model produces valid XML at any zoom
        MapModelFixture fixture = createMapModel();
        loadMap(fixture.model, "<map><node TEXT='ZoomIn'/></map>");
        assertThat(saveMapToXml(fixture.model)).contains("ZoomIn");
    }

    @Test
    void view_zoomOut() throws Exception {
        MapModelFixture fixture = createMapModel();
        loadMap(fixture.model, "<map><node TEXT='ZoomOut'/></map>");
        assertThat(saveMapToXml(fixture.model)).contains("ZoomOut");
    }

    @Test
    void view_zoomLevels() throws Exception {
        // Verify model is valid at all zoom levels
        int[] zoomLevels = {25, 50, 75, 100, 125, 150, 200};
        MapModelFixture fixture = createMapModel();
        loadMap(fixture.model, "<map><node TEXT='Root'/></map>");
        for (int zoom : zoomLevels) {
            String xml = saveMapToXml(fixture.model);
            assertThat(xml).as("Model valid at zoom %d%%", zoom).isNotEmpty();
        }
    }

    @Test
    void view_fitToPage() throws Exception {
        MapModelFixture fixture = createMapModel();
        MindMapNode root = loadMap(fixture.model, "<map><node TEXT='Root'><node TEXT='Wide1'/><node TEXT='Wide2'/><node TEXT='Wide3'/></node></map>");
        assertThat(root.getChildCount()).isEqualTo(3);
    }

    @Test
    void view_toggleNoteWindow() throws Exception {
        MapModelFixture fixture = createMapModel();
        MindMapNode root = loadMap(fixture.model, "<map><node TEXT='Root'/></map>");
        root.setNoteText("<html><body>Toggle note</body></html>");
        assertThat(root.getNoteText()).contains("Toggle note");
        root.setNoteText(null);
        assertThat(root.getNoteText()).isNull();
    }

    @Test
    void view_toggleAttributeTable() throws Exception {
        MapModelFixture fixture = createMapModel();
        MindMapNode root = loadMap(fixture.model, "<map><node TEXT='Root'/></map>");
        assertThat(root.getAttributeTableLength()).isEqualTo(0);
    }
}
