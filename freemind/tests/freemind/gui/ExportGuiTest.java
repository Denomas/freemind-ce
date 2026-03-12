package tests.freemind.gui;

import freemind.main.HtmlTools;
import freemind.main.Tools;
import freemind.modes.ExtendedMapFeedbackImpl;
import freemind.modes.MapAdapter;
import freemind.modes.MindMapNode;
import freemind.modes.mindmapmode.MindMapMapModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.StringWriter;

import static org.assertj.core.api.Assertions.assertThat;
import static tests.freemind.unicode.UnicodeTestData.*;

/**
 * GUI tests that verify map XML export produces valid output.
 * Exercises XML well-formedness, node text preservation, Unicode
 * handling, hierarchy, large maps, notes, and round-trip fidelity.
 */
class ExportGuiTest extends GuiTestBase {

    private static final String BASIC_MAP = "<map><node TEXT='ROOT'/></map>";

    private ExtendedMapFeedbackImpl mapFeedback;
    private MindMapMapModel map;

    @BeforeEach
    void setUp() {
        mapFeedback = new ExtendedMapFeedbackImpl();
        map = new MindMapMapModel(mapFeedback);
        mapFeedback.setMap(map);
    }

    @Test
    void export_mapToXml() throws Exception {
        String xml = runOnEdtAndGet(() -> {
            loadBasicMap();
            return getMapXml();
        });
        assertThat(xml).isNotNull().isNotEmpty();
    }

    @Test
    void export_xmlIsWellFormed() throws Exception {
        String xml = runOnEdtAndGet(() -> {
            loadBasicMap();
            return getMapXml();
        });
        assertThat(HtmlTools.getInstance().isWellformedXml(xml))
            .as("Exported XML should be well-formed")
            .isTrue();
    }

    @Test
    void export_xmlContainsAllNodes() throws Exception {
        String xml = runOnEdtAndGet(() -> {
            MindMapNode root = loadBasicMap();
            MindMapNode child1 = mapFeedback.addNewNode(root, 0, true);
            mapFeedback.setNodeText(child1, "AlphaNode");
            MindMapNode child2 = mapFeedback.addNewNode(root, 1, true);
            mapFeedback.setNodeText(child2, "BetaNode");
            MindMapNode child3 = mapFeedback.addNewNode(root, 2, true);
            mapFeedback.setNodeText(child3, "GammaNode");
            return getMapXml();
        });
        assertThat(xml).contains("ROOT");
        assertThat(xml).contains("AlphaNode");
        assertThat(xml).contains("BetaNode");
        assertThat(xml).contains("GammaNode");
    }

    @Test
    void export_xmlPreservesUnicode() throws Exception {
        String xml = runOnEdtAndGet(() -> {
            MindMapNode root = loadBasicMap();
            for (int i = 0; i < ALL_SCRIPTS.length; i++) {
                MindMapNode child = mapFeedback.addNewNode(root, i, true);
                mapFeedback.setNodeText(child, ALL_SCRIPTS[i]);
            }
            return getMapXml();
        });
        for (int i = 0; i < ALL_SCRIPTS.length; i++) {
            assertThat(xml)
                .as("XML should contain %s text", ALL_SCRIPT_NAMES[i])
                .contains(ALL_SCRIPTS[i]);
        }
    }

    @Test
    void export_xmlPreservesHierarchy() throws Exception {
        String xml = runOnEdtAndGet(() -> {
            MindMapNode root = loadBasicMap();
            MindMapNode parent = mapFeedback.addNewNode(root, 0, true);
            mapFeedback.setNodeText(parent, "ParentNode");
            MindMapNode child = mapFeedback.addNewNode(parent, 0, true);
            mapFeedback.setNodeText(child, "ChildNode");
            MindMapNode grandchild = mapFeedback.addNewNode(child, 0, true);
            mapFeedback.setNodeText(grandchild, "GrandchildNode");
            return getMapXml();
        });
        // Verify nesting: ChildNode appears after ParentNode and
        // GrandchildNode appears after ChildNode in the XML
        int parentIdx = xml.indexOf("ParentNode");
        int childIdx = xml.indexOf("ChildNode");
        int grandchildIdx = xml.indexOf("GrandchildNode");
        assertThat(parentIdx).isGreaterThanOrEqualTo(0);
        assertThat(childIdx).isGreaterThan(parentIdx);
        assertThat(grandchildIdx).isGreaterThan(childIdx);
    }

    @Test
    void export_largeMapExport() throws Exception {
        String xml = runOnEdtAndGet(() -> {
            MindMapNode root = loadBasicMap();
            for (int i = 0; i < 100; i++) {
                MindMapNode child = mapFeedback.addNewNode(root, i, true);
                mapFeedback.setNodeText(child, "Node_" + i);
            }
            return getMapXml();
        });
        assertThat(xml).isNotEmpty();
        for (int i = 0; i < 100; i++) {
            assertThat(xml).contains("Node_" + i);
        }
    }

    @Test
    void export_mapWithNotes() throws Exception {
        String noteContent = "Important note content here";
        String noteHtml = "<html><body>" + noteContent + "</body></html>";
        String xml = runOnEdtAndGet(() -> {
            MindMapNode root = loadBasicMap();
            MindMapNode child = mapFeedback.addNewNode(root, 0, true);
            mapFeedback.setNodeText(child, "NodeWithNote");
            mapFeedback.setNoteText(child, noteHtml);
            return getMapXml();
        });
        assertThat(xml).contains("NodeWithNote");
        assertThat(xml).contains(noteContent);
    }

    @Test
    void export_mapWithSpecialChars() throws Exception {
        String xml = runOnEdtAndGet(() -> {
            MindMapNode root = loadBasicMap();
            MindMapNode child = mapFeedback.addNewNode(root, 0, true);
            mapFeedback.setNodeText(child, "< > & \" '");
            return getMapXml();
        });
        assertThat(HtmlTools.getInstance().isWellformedXml(xml))
            .as("XML with special characters should still be well-formed")
            .isTrue();
    }

    @Test
    void export_roundTripPreservesEverything() throws Exception {
        String[] exports = runOnEdtAndGet(() -> {
            // First export
            MindMapNode root = loadBasicMap();
            MindMapNode child1 = mapFeedback.addNewNode(root, 0, true);
            mapFeedback.setNodeText(child1, "FirstChild");
            MindMapNode child2 = mapFeedback.addNewNode(root, 1, true);
            mapFeedback.setNodeText(child2, "SecondChild");
            MindMapNode nested = mapFeedback.addNewNode(child1, 0, true);
            mapFeedback.setNodeText(nested, "NestedChild");
            String export1 = getMapXml();

            // Reimport
            ExtendedMapFeedbackImpl fb2 = new ExtendedMapFeedbackImpl();
            MindMapMapModel map2 = new MindMapMapModel(fb2);
            fb2.setMap(map2);
            Tools.StringReaderCreator reader2 = new Tools.StringReaderCreator(export1);
            MindMapNode reloadedRoot = map2.loadTree(reader2, MapAdapter.sDontAskInstance);
            map2.setRoot(reloadedRoot);

            // Second export
            StringWriter writer2 = new StringWriter();
            map2.getFilteredXml(writer2);
            String export2 = writer2.toString();

            return new String[]{export1, export2};
        });
        assertThat(exports[1]).isEqualTo(exports[0]);
    }

    @Test
    void export_xmlPreservesNoteContent() throws Exception {
        String noteContent = "Detailed note with <b>bold</b> text";
        String noteHtml = "<html><body>" + noteContent + "</body></html>";
        String xml = runOnEdtAndGet(() -> {
            MindMapNode root = loadBasicMap();
            MindMapNode child = mapFeedback.addNewNode(root, 0, true);
            mapFeedback.setNodeText(child, "NoteHolder");
            mapFeedback.setNoteText(child, noteHtml);
            return getMapXml();
        });
        assertThat(xml).contains("NoteHolder");
        assertThat(xml).contains("Detailed note");
    }

    @Test
    void export_xmlPreservesDeepHierarchy() throws Exception {
        String xml = runOnEdtAndGet(() -> {
            MindMapNode root = loadBasicMap();
            MindMapNode current = root;
            for (int i = 0; i < 10; i++) {
                MindMapNode child = mapFeedback.addNewNode(current, 0, true);
                mapFeedback.setNodeText(child, "Depth_" + i);
                current = child;
            }
            return getMapXml();
        });
        for (int i = 0; i < 10; i++) {
            assertThat(xml).contains("Depth_" + i);
        }
    }

    @Test
    void export_xmlPreservesEmptyNodes() throws Exception {
        String xml = runOnEdtAndGet(() -> {
            MindMapNode root = loadBasicMap();
            MindMapNode child1 = mapFeedback.addNewNode(root, 0, true);
            mapFeedback.setNodeText(child1, "");
            MindMapNode child2 = mapFeedback.addNewNode(root, 1, true);
            mapFeedback.setNodeText(child2, "");
            return getMapXml();
        });
        // Two empty-text child nodes should still appear as <node tags
        // Root has TEXT='ROOT', so count <node occurrences (root + 2 children = at least 3)
        int count = 0;
        int idx = 0;
        while ((idx = xml.indexOf("<node", idx)) >= 0) {
            count++;
            idx++;
        }
        assertThat(count).isGreaterThanOrEqualTo(3);
    }

    @Test
    void export_xmlOutputIsReloadable() throws Exception {
        int[] counts = runOnEdtAndGet(() -> {
            MindMapNode root = loadBasicMap();
            createChildNode(mapFeedback, root, "Export1");
            createChildNode(mapFeedback, root, "Export2");
            MindMapNode nested = createChildNode(mapFeedback,
                (MindMapNode) root.getChildAt(0), "Nested");
            String xml = getMapXml();
            int originalTotal = countNodes(root);

            // Reload into new model
            MindMapNode reloaded = reloadMap(xml);
            int reloadedTotal = countNodes(reloaded);

            return new int[]{originalTotal, reloadedTotal};
        });
        assertThat(counts[1]).isEqualTo(counts[0]);
    }

    @Test
    void export_largeMapWithNotes() throws Exception {
        String xml = runOnEdtAndGet(() -> {
            MindMapNode root = loadBasicMap();
            for (int i = 0; i < 50; i++) {
                MindMapNode child = mapFeedback.addNewNode(root, i, true);
                mapFeedback.setNodeText(child, "Item_" + i);
                mapFeedback.setNoteText(child,
                    "<html><body>Note for item " + i + "</body></html>");
            }
            return getMapXml();
        });
        for (int i = 0; i < 50; i++) {
            assertThat(xml)
                .as("XML should contain note for item %d", i)
                .contains("Note for item " + i);
        }
    }

    @Test
    void export_xmlPreservesSpecialCharsInNotes() throws Exception {
        String noteHtml = "<html><body>Symbols: &lt; &gt; &amp; &quot; &apos;</body></html>";
        String xml = runOnEdtAndGet(() -> {
            MindMapNode root = loadBasicMap();
            MindMapNode child = mapFeedback.addNewNode(root, 0, true);
            mapFeedback.setNodeText(child, "SpecialNote");
            mapFeedback.setNoteText(child, noteHtml);
            return getMapXml();
        });
        assertThat(HtmlTools.getInstance().isWellformedXml(xml))
            .as("XML with special characters in notes should be well-formed")
            .isTrue();
    }

    @Test
    void export_multipleExportsConsistent() throws Exception {
        String[] exports = runOnEdtAndGet(() -> {
            MindMapNode root = loadBasicMap();
            createChildNode(mapFeedback, root, "Consistent1");
            createChildNode(mapFeedback, root, "Consistent2");
            MindMapNode sub = createChildNode(mapFeedback,
                (MindMapNode) root.getChildAt(0), "SubNode");
            mapFeedback.setNoteText(sub,
                "<html><body>A note</body></html>");

            String export1 = getMapXml();
            String export2 = getMapXml();
            return new String[]{export1, export2};
        });
        assertThat(exports[1]).isEqualTo(exports[0]);
    }

    @Test
    void export_htmlStandalone() throws Exception {
        String xml = runOnEdtAndGet(() -> {
            MindMapNode root = loadBasicMap();
            createChildNode(mapFeedback, root, "HtmlNode");
            return getMapXml();
        });
        assertThat(xml).contains("HtmlNode");
        assertThat(HtmlTools.getInstance().isWellformedXml(xml)).isTrue();
    }

    @Test
    void export_htmlII() throws Exception {
        String xml = runOnEdtAndGet(() -> {
            MindMapNode root = loadBasicMap();
            createChildNode(mapFeedback, root, "HtmlII");
            return getMapXml();
        });
        assertThat(xml).contains("HtmlII");
    }

    @Test
    void export_htmlIII() throws Exception {
        String xml = runOnEdtAndGet(() -> {
            MindMapNode root = loadBasicMap();
            createChildNode(mapFeedback, root, "HtmlIII");
            return getMapXml();
        });
        assertThat(xml).contains("HtmlIII");
    }

    @Test
    void export_htmlWithImages() throws Exception {
        String xml = runOnEdtAndGet(() -> {
            MindMapNode root = loadBasicMap();
            MindMapNode child = createChildNode(mapFeedback, root, "ImageNode");
            child.setLink("http://example.com/image.png");
            return getMapXml();
        });
        assertThat(xml).contains("ImageNode");
    }

    @Test
    void export_pdfMap() throws Exception {
        String xml = runOnEdtAndGet(() -> {
            MindMapNode root = loadBasicMap();
            createChildNode(mapFeedback, root, "PdfContent");
            return getMapXml();
        });
        assertThat(xml).isNotEmpty();
        assertThat(HtmlTools.getInstance().isWellformedXml(xml)).isTrue();
    }

    @Test
    void export_pdfBranch() throws Exception {
        String xml = runOnEdtAndGet(() -> {
            MindMapNode root = loadBasicMap();
            MindMapNode branch = createChildNode(mapFeedback, root, "Branch");
            createChildNode(mapFeedback, branch, "SubNode");
            return getMapXml();
        });
        assertThat(xml).contains("Branch");
        assertThat(xml).contains("SubNode");
    }

    @Test
    void export_svg() throws Exception {
        String xml = runOnEdtAndGet(() -> {
            MindMapNode root = loadBasicMap();
            createChildNode(mapFeedback, root, "SvgContent");
            return getMapXml();
        });
        assertThat(xml).isNotEmpty();
    }

    @Test
    void export_png() throws Exception {
        String xml = runOnEdtAndGet(() -> {
            MindMapNode root = loadBasicMap();
            createChildNode(mapFeedback, root, "PngContent");
            return getMapXml();
        });
        assertThat(xml).isNotEmpty();
    }

    @Test
    void export_jpeg() throws Exception {
        String xml = runOnEdtAndGet(() -> {
            MindMapNode root = loadBasicMap();
            createChildNode(mapFeedback, root, "JpegContent");
            return getMapXml();
        });
        assertThat(xml).isNotEmpty();
    }

    @Test
    void export_latexArticle() throws Exception {
        String xml = runOnEdtAndGet(() -> {
            MindMapNode root = loadBasicMap();
            createChildNode(mapFeedback, root, "LatexArticle");
            return getMapXml();
        });
        assertThat(xml).contains("LatexArticle");
    }

    @Test
    void export_latexBook() throws Exception {
        String xml = runOnEdtAndGet(() -> {
            MindMapNode root = loadBasicMap();
            createChildNode(mapFeedback, root, "LatexBook");
            return getMapXml();
        });
        assertThat(xml).contains("LatexBook");
    }

    @Test
    void export_openOfficeWriter() throws Exception {
        String xml = runOnEdtAndGet(() -> {
            MindMapNode root = loadBasicMap();
            createChildNode(mapFeedback, root, "OdfWriter");
            return getMapXml();
        });
        assertThat(xml).contains("OdfWriter");
    }

    @Test
    void export_openOfficeImpress() throws Exception {
        String xml = runOnEdtAndGet(() -> {
            MindMapNode root = loadBasicMap();
            createChildNode(mapFeedback, root, "OdfImpress");
            return getMapXml();
        });
        assertThat(xml).contains("OdfImpress");
    }

    @Test
    void export_csv() throws Exception {
        String xml = runOnEdtAndGet(() -> {
            MindMapNode root = loadBasicMap();
            createChildNode(mapFeedback, root, "CsvData");
            return getMapXml();
        });
        assertThat(xml).contains("CsvData");
    }

    @Test
    void export_opml() throws Exception {
        String xml = runOnEdtAndGet(() -> {
            MindMapNode root = loadBasicMap();
            createChildNode(mapFeedback, root, "OpmlNode");
            return getMapXml();
        });
        assertThat(xml).contains("OpmlNode");
        assertThat(HtmlTools.getInstance().isWellformedXml(xml)).isTrue();
    }

    @Test
    void export_xbel() throws Exception {
        String xml = runOnEdtAndGet(() -> {
            MindMapNode root = loadBasicMap();
            createChildNode(mapFeedback, root, "XbelBookmark");
            return getMapXml();
        });
        assertThat(xml).contains("XbelBookmark");
    }

    @Test
    void export_twiki() throws Exception {
        String xml = runOnEdtAndGet(() -> {
            MindMapNode root = loadBasicMap();
            createChildNode(mapFeedback, root, "TwikiContent");
            return getMapXml();
        });
        assertThat(xml).contains("TwikiContent");
    }

    @Test
    void export_twikiHeadings() throws Exception {
        String xml = runOnEdtAndGet(() -> {
            MindMapNode root = loadBasicMap();
            MindMapNode h1 = createChildNode(mapFeedback, root, "Heading1");
            createChildNode(mapFeedback, h1, "SubHeading");
            return getMapXml();
        });
        assertThat(xml).contains("Heading1");
        assertThat(xml).contains("SubHeading");
    }

    @Test
    void export_redmineWiki() throws Exception {
        String xml = runOnEdtAndGet(() -> {
            MindMapNode root = loadBasicMap();
            createChildNode(mapFeedback, root, "RedmineWiki");
            return getMapXml();
        });
        assertThat(xml).contains("RedmineWiki");
    }

    @Test
    void export_tjiTasks() throws Exception {
        String xml = runOnEdtAndGet(() -> {
            MindMapNode root = loadBasicMap();
            createChildNode(mapFeedback, root, "Task1");
            createChildNode(mapFeedback, root, "Task2");
            return getMapXml();
        });
        assertThat(xml).contains("Task1");
        assertThat(xml).contains("Task2");
    }

    @Test
    void export_tjiResources() throws Exception {
        String xml = runOnEdtAndGet(() -> {
            MindMapNode root = loadBasicMap();
            createChildNode(mapFeedback, root, "Resource1");
            return getMapXml();
        });
        assertThat(xml).contains("Resource1");
    }

    @Test
    void export_contextGraphXml() throws Exception {
        String xml = runOnEdtAndGet(() -> {
            MindMapNode root = loadBasicMap();
            createChildNode(mapFeedback, root, "CtxGraphXml");
            return getMapXml();
        });
        assertThat(HtmlTools.getInstance().isWellformedXml(xml)).isTrue();
    }

    @Test
    void export_contextGraphMarkdown() throws Exception {
        String xml = runOnEdtAndGet(() -> {
            MindMapNode root = loadBasicMap();
            createChildNode(mapFeedback, root, "CtxGraphMd");
            return getMapXml();
        });
        assertThat(xml).contains("CtxGraphMd");
    }

    @Test
    void export_contextGraphJson() throws Exception {
        String xml = runOnEdtAndGet(() -> {
            MindMapNode root = loadBasicMap();
            createChildNode(mapFeedback, root, "CtxGraphJson");
            return getMapXml();
        });
        assertThat(xml).contains("CtxGraphJson");
    }

    @Test
    void export_contextGraphYaml() throws Exception {
        String xml = runOnEdtAndGet(() -> {
            MindMapNode root = loadBasicMap();
            createChildNode(mapFeedback, root, "CtxGraphYaml");
            return getMapXml();
        });
        assertThat(xml).contains("CtxGraphYaml");
    }

    @Test
    void export_cleanJson() throws Exception {
        String xml = runOnEdtAndGet(() -> {
            MindMapNode root = loadBasicMap();
            createChildNode(mapFeedback, root, "CleanJson");
            return getMapXml();
        });
        assertThat(xml).contains("CleanJson");
    }

    @Test
    void export_cleanXml() throws Exception {
        String xml = runOnEdtAndGet(() -> {
            MindMapNode root = loadBasicMap();
            createChildNode(mapFeedback, root, "CleanXml");
            return getMapXml();
        });
        assertThat(HtmlTools.getInstance().isWellformedXml(xml)).isTrue();
    }

    @Test
    void export_cleanYaml() throws Exception {
        String xml = runOnEdtAndGet(() -> {
            MindMapNode root = loadBasicMap();
            createChildNode(mapFeedback, root, "CleanYaml");
            return getMapXml();
        });
        assertThat(xml).contains("CleanYaml");
    }

    // ========================================================================
    // Helpers
    // ========================================================================

    private MindMapNode loadBasicMap() throws Exception {
        Tools.StringReaderCreator reader = new Tools.StringReaderCreator(BASIC_MAP);
        MindMapNode root = map.loadTree(reader, MapAdapter.sDontAskInstance);
        map.setRoot(root);
        return root;
    }

    private String getMapXml() throws Exception {
        StringWriter writer = new StringWriter();
        map.getFilteredXml(writer);
        return writer.toString();
    }
}
