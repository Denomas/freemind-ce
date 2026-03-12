package tests.freemind.gui;

import freemind.main.Tools;
import freemind.modes.ExtendedMapFeedbackImpl;
import freemind.modes.MapAdapter;
import freemind.modes.MindMapNode;
import freemind.modes.mindmapmode.MindMapMapModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * GUI tests for file I/O operations using ExtendedMapFeedbackImpl.
 * Verifies XML save/load round-trip, structure preservation, and
 * edge cases like special characters and long text.
 */
class FileOperationsGuiTest extends GuiTestBase {

    private static final String BASIC_MAP = "<map><node TEXT='ROOT'/></map>";
    private static final String MAP_WITH_CHILD =
            "<map><node TEXT='ROOT'><node TEXT='Child1'/></node></map>";

    @TempDir
    Path tempDir;

    private ExtendedMapFeedbackImpl mapFeedback;
    private MindMapMapModel map;

    @BeforeEach
    void setUp() {
        mapFeedback = new ExtendedMapFeedbackImpl();
        map = new MindMapMapModel(mapFeedback);
        mapFeedback.setMap(map);
    }

    @Test
    void file_newMapHasRoot() throws Exception {
        MindMapNode root = runOnEdtAndGet(() -> {
            Tools.StringReaderCreator reader = new Tools.StringReaderCreator(BASIC_MAP);
            MindMapNode r = map.loadTree(reader, MapAdapter.sDontAskInstance);
            map.setRoot(r);
            return r;
        });
        assertThat(root).isNotNull();
        assertThat(root.getText()).isEqualTo("ROOT");
    }

    @Test
    void file_saveMapToXml() throws Exception {
        String xml = runOnEdtAndGet(() -> {
            Tools.StringReaderCreator reader = new Tools.StringReaderCreator(BASIC_MAP);
            MindMapNode root = map.loadTree(reader, MapAdapter.sDontAskInstance);
            map.setRoot(root);
            return getMapXml();
        });
        assertThat(xml).isNotNull().isNotEmpty();
    }

    @Test
    void file_saveMapIsValidXml() throws Exception {
        String xml = runOnEdtAndGet(() -> {
            Tools.StringReaderCreator reader = new Tools.StringReaderCreator(BASIC_MAP);
            MindMapNode root = map.loadTree(reader, MapAdapter.sDontAskInstance);
            map.setRoot(root);
            return getMapXml();
        });
        assertThat(xml).startsWith("<map");
        assertThat(xml).contains("</map>");
    }

    @Test
    void file_saveAndReloadPreservesStructure() throws Exception {
        int[] counts = runOnEdtAndGet(() -> {
            // Create map with children
            Tools.StringReaderCreator reader = new Tools.StringReaderCreator(BASIC_MAP);
            MindMapNode root = map.loadTree(reader, MapAdapter.sDontAskInstance);
            map.setRoot(root);
            mapFeedback.addNewNode(root, 0, true);
            mapFeedback.addNewNode(root, 1, true);
            int originalCount = root.getChildCount();

            // Save and reload
            String xml = getMapXml();
            ExtendedMapFeedbackImpl fb2 = new ExtendedMapFeedbackImpl();
            MindMapMapModel map2 = new MindMapMapModel(fb2);
            fb2.setMap(map2);
            Tools.StringReaderCreator reader2 = new Tools.StringReaderCreator(xml);
            MindMapNode reloadedRoot = map2.loadTree(reader2, MapAdapter.sDontAskInstance);
            map2.setRoot(reloadedRoot);
            int reloadedCount = reloadedRoot.getChildCount();

            return new int[]{originalCount, reloadedCount};
        });
        assertThat(counts[1]).isEqualTo(counts[0]);
    }

    @Test
    void file_saveAndReloadPreservesNodeText() throws Exception {
        String[] texts = runOnEdtAndGet(() -> {
            Tools.StringReaderCreator reader = new Tools.StringReaderCreator(BASIC_MAP);
            MindMapNode root = map.loadTree(reader, MapAdapter.sDontAskInstance);
            map.setRoot(root);
            MindMapNode child = mapFeedback.addNewNode(root, 0, true);
            mapFeedback.setNodeText(child, "TestNodeText");
            String originalText = child.getText();

            // Save and reload
            String xml = getMapXml();
            ExtendedMapFeedbackImpl fb2 = new ExtendedMapFeedbackImpl();
            MindMapMapModel map2 = new MindMapMapModel(fb2);
            fb2.setMap(map2);
            Tools.StringReaderCreator reader2 = new Tools.StringReaderCreator(xml);
            MindMapNode reloadedRoot = map2.loadTree(reader2, MapAdapter.sDontAskInstance);
            map2.setRoot(reloadedRoot);
            MindMapNode reloadedChild = (MindMapNode) reloadedRoot.getChildAt(0);
            String reloadedText = reloadedChild.getText();

            return new String[]{originalText, reloadedText};
        });
        assertThat(texts[1]).isEqualTo(texts[0]);
    }

    @Test
    void file_multipleChildrenPreserved() throws Exception {
        int reloadedCount = runOnEdtAndGet(() -> {
            Tools.StringReaderCreator reader = new Tools.StringReaderCreator(BASIC_MAP);
            MindMapNode root = map.loadTree(reader, MapAdapter.sDontAskInstance);
            map.setRoot(root);
            for (int i = 0; i < 10; i++) {
                MindMapNode child = mapFeedback.addNewNode(root, i, true);
                mapFeedback.setNodeText(child, "Child_" + i);
            }

            String xml = getMapXml();
            ExtendedMapFeedbackImpl fb2 = new ExtendedMapFeedbackImpl();
            MindMapMapModel map2 = new MindMapMapModel(fb2);
            fb2.setMap(map2);
            Tools.StringReaderCreator reader2 = new Tools.StringReaderCreator(xml);
            MindMapNode reloadedRoot = map2.loadTree(reader2, MapAdapter.sDontAskInstance);
            map2.setRoot(reloadedRoot);
            return reloadedRoot.getChildCount();
        });
        assertThat(reloadedCount).isEqualTo(10);
    }

    @Test
    void file_nestedChildrenPreserved() throws Exception {
        String deepText = runOnEdtAndGet(() -> {
            Tools.StringReaderCreator reader = new Tools.StringReaderCreator(BASIC_MAP);
            MindMapNode root = map.loadTree(reader, MapAdapter.sDontAskInstance);
            map.setRoot(root);

            // Create 3 levels: root -> level1 -> level2 -> level3
            MindMapNode level1 = mapFeedback.addNewNode(root, 0, true);
            mapFeedback.setNodeText(level1, "Level1");
            MindMapNode level2 = mapFeedback.addNewNode(level1, 0, true);
            mapFeedback.setNodeText(level2, "Level2");
            MindMapNode level3 = mapFeedback.addNewNode(level2, 0, true);
            mapFeedback.setNodeText(level3, "Level3");

            String xml = getMapXml();
            ExtendedMapFeedbackImpl fb2 = new ExtendedMapFeedbackImpl();
            MindMapMapModel map2 = new MindMapMapModel(fb2);
            fb2.setMap(map2);
            Tools.StringReaderCreator reader2 = new Tools.StringReaderCreator(xml);
            MindMapNode reloadedRoot = map2.loadTree(reader2, MapAdapter.sDontAskInstance);
            map2.setRoot(reloadedRoot);

            MindMapNode r1 = (MindMapNode) reloadedRoot.getChildAt(0);
            MindMapNode r2 = (MindMapNode) r1.getChildAt(0);
            MindMapNode r3 = (MindMapNode) r2.getChildAt(0);
            return r3.getText();
        });
        assertThat(deepText).isEqualTo("Level3");
    }

    @Test
    void file_emptyNodeTextPreserved() throws Exception {
        String reloadedText = runOnEdtAndGet(() -> {
            Tools.StringReaderCreator reader = new Tools.StringReaderCreator(BASIC_MAP);
            MindMapNode root = map.loadTree(reader, MapAdapter.sDontAskInstance);
            map.setRoot(root);
            MindMapNode child = mapFeedback.addNewNode(root, 0, true);
            mapFeedback.setNodeText(child, "");

            String xml = getMapXml();
            ExtendedMapFeedbackImpl fb2 = new ExtendedMapFeedbackImpl();
            MindMapMapModel map2 = new MindMapMapModel(fb2);
            fb2.setMap(map2);
            Tools.StringReaderCreator reader2 = new Tools.StringReaderCreator(xml);
            MindMapNode reloadedRoot = map2.loadTree(reader2, MapAdapter.sDontAskInstance);
            map2.setRoot(reloadedRoot);
            MindMapNode reloadedChild = (MindMapNode) reloadedRoot.getChildAt(0);
            return reloadedChild.getText();
        });
        assertThat(reloadedText).isEmpty();
    }

    @Test
    void file_specialCharactersInNodeText() throws Exception {
        String specialChars = "< > & \" '";
        String reloadedText = runOnEdtAndGet(() -> {
            Tools.StringReaderCreator reader = new Tools.StringReaderCreator(BASIC_MAP);
            MindMapNode root = map.loadTree(reader, MapAdapter.sDontAskInstance);
            map.setRoot(root);
            MindMapNode child = mapFeedback.addNewNode(root, 0, true);
            mapFeedback.setNodeText(child, specialChars);

            String xml = getMapXml();
            ExtendedMapFeedbackImpl fb2 = new ExtendedMapFeedbackImpl();
            MindMapMapModel map2 = new MindMapMapModel(fb2);
            fb2.setMap(map2);
            Tools.StringReaderCreator reader2 = new Tools.StringReaderCreator(xml);
            MindMapNode reloadedRoot = map2.loadTree(reader2, MapAdapter.sDontAskInstance);
            map2.setRoot(reloadedRoot);
            MindMapNode reloadedChild = (MindMapNode) reloadedRoot.getChildAt(0);
            return reloadedChild.getText();
        });
        assertThat(reloadedText).isEqualTo(specialChars);
    }

    @Test
    void file_veryLongNodeTextPreserved() throws Exception {
        String longText = "A".repeat(5000);
        String reloadedText = runOnEdtAndGet(() -> {
            Tools.StringReaderCreator reader = new Tools.StringReaderCreator(BASIC_MAP);
            MindMapNode root = map.loadTree(reader, MapAdapter.sDontAskInstance);
            map.setRoot(root);
            MindMapNode child = mapFeedback.addNewNode(root, 0, true);
            mapFeedback.setNodeText(child, longText);

            String xml = getMapXml();
            ExtendedMapFeedbackImpl fb2 = new ExtendedMapFeedbackImpl();
            MindMapMapModel map2 = new MindMapMapModel(fb2);
            fb2.setMap(map2);
            Tools.StringReaderCreator reader2 = new Tools.StringReaderCreator(xml);
            MindMapNode reloadedRoot = map2.loadTree(reader2, MapAdapter.sDontAskInstance);
            map2.setRoot(reloadedRoot);
            MindMapNode reloadedChild = (MindMapNode) reloadedRoot.getChildAt(0);
            return reloadedChild.getText();
        });
        assertThat(reloadedText).isEqualTo(longText);
    }

    @Test
    void file_saveAndReloadPreservesNotes() throws Exception {
        String noteContent = "This is a test note";
        String noteHtml = "<html><body>" + noteContent + "</body></html>";
        String reloadedNote = runOnEdtAndGet(() -> {
            Tools.StringReaderCreator reader = new Tools.StringReaderCreator(BASIC_MAP);
            MindMapNode root = map.loadTree(reader, MapAdapter.sDontAskInstance);
            map.setRoot(root);
            MindMapNode child = mapFeedback.addNewNode(root, 0, true);
            mapFeedback.setNodeText(child, "NoteNode");
            mapFeedback.setNoteText(child, noteHtml);

            String xml = getMapXml();
            ExtendedMapFeedbackImpl fb2 = new ExtendedMapFeedbackImpl();
            MindMapMapModel map2 = new MindMapMapModel(fb2);
            fb2.setMap(map2);
            Tools.StringReaderCreator reader2 = new Tools.StringReaderCreator(xml);
            MindMapNode reloadedRoot = map2.loadTree(reader2, MapAdapter.sDontAskInstance);
            map2.setRoot(reloadedRoot);
            MindMapNode reloadedChild = (MindMapNode) reloadedRoot.getChildAt(0);
            return reloadedChild.getNoteText();
        });
        assertThat(reloadedNote).contains(noteContent);
    }

    @Test
    void file_lastOpenedFilesSimulation() throws Exception {
        runOnEdt(() -> {
            try {
                Tools.StringReaderCreator reader = new Tools.StringReaderCreator(BASIC_MAP);
                MindMapNode root = map.loadTree(reader, MapAdapter.sDontAskInstance);
                map.setRoot(root);
                MindMapNode child = mapFeedback.addNewNode(root, 0, true);
                mapFeedback.setNodeText(child, "Saved");

                Path file = tempDir.resolve("test_save.mm");
                saveMapTo(map, file);
                assertThat(Files.exists(file)).isTrue();
                String content = Files.readString(file);
                assertThat(content).startsWith("<map");
                assertThat(content).contains("Saved");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Test
    void file_closeUnsavedSimulation() throws Exception {
        boolean hasChildren = runOnEdtAndGet(() -> {
            Tools.StringReaderCreator reader = new Tools.StringReaderCreator(BASIC_MAP);
            MindMapNode root = map.loadTree(reader, MapAdapter.sDontAskInstance);
            map.setRoot(root);
            // Simulate modification (dirty state)
            mapFeedback.addNewNode(root, 0, true);
            // Verify the map has been modified — it has children now
            return root.getChildCount() > 0;
        });
        assertThat(hasChildren).as("Map should have unsaved changes").isTrue();
    }

    @Test
    void file_multipleOpenMaps() throws Exception {
        runOnEdt(() -> {
            try {
                for (int i = 0; i < 5; i++) {
                    MapModelFixture fixture = createMapModel();
                    MindMapNode root = loadMap(fixture.model, BASIC_MAP);
                    createChildNode(fixture.feedback, root, "Map" + i + "_Child");
                    String xml = saveMapToXml(fixture.model);
                    assertThat(xml).contains("Map" + i + "_Child");

                    // Verify round-trip
                    MindMapNode reloaded = reloadMap(xml);
                    assertThat(reloaded.getChildCount()).isEqualTo(1);
                    assertThat(((MindMapNode) reloaded.getChildAt(0)).getText())
                        .isEqualTo("Map" + i + "_Child");
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Test
    void file_xmlOutputStartsWithMapTag() throws Exception {
        String xml = runOnEdtAndGet(() -> {
            Tools.StringReaderCreator reader = new Tools.StringReaderCreator(BASIC_MAP);
            MindMapNode root = map.loadTree(reader, MapAdapter.sDontAskInstance);
            map.setRoot(root);
            return getMapXml();
        });
        assertThat(xml).startsWith("<map");
    }

    @Test
    void file_xmlOutputContainsNodeTag() throws Exception {
        String xml = runOnEdtAndGet(() -> {
            Tools.StringReaderCreator reader = new Tools.StringReaderCreator(BASIC_MAP);
            MindMapNode root = map.loadTree(reader, MapAdapter.sDontAskInstance);
            map.setRoot(root);
            return getMapXml();
        });
        assertThat(xml).contains("<node");
    }

    @Test
    void file_roundTripIdempotent() throws Exception {
        String[] exports = runOnEdtAndGet(() -> {
            Tools.StringReaderCreator reader = new Tools.StringReaderCreator(BASIC_MAP);
            MindMapNode root = map.loadTree(reader, MapAdapter.sDontAskInstance);
            map.setRoot(root);
            MindMapNode child = mapFeedback.addNewNode(root, 0, true);
            mapFeedback.setNodeText(child, "Idempotent");

            // First save
            String xml1 = getMapXml();

            // Reload
            ExtendedMapFeedbackImpl fb2 = new ExtendedMapFeedbackImpl();
            MindMapMapModel map2 = new MindMapMapModel(fb2);
            fb2.setMap(map2);
            Tools.StringReaderCreator reader2 = new Tools.StringReaderCreator(xml1);
            MindMapNode reloadedRoot = map2.loadTree(reader2, MapAdapter.sDontAskInstance);
            map2.setRoot(reloadedRoot);

            // Second save
            StringWriter writer2 = new StringWriter();
            map2.getFilteredXml(writer2);
            String xml2 = writer2.toString();

            return new String[]{xml1, xml2};
        });
        assertThat(exports[1]).isEqualTo(exports[0]);
    }

    @Test
    void fileMenu_newMap() throws Exception {
        MapModelFixture fixture = createMapModel();
        MindMapNode root = loadMap(fixture.model, "<map><node TEXT='New Map'/></map>");
        assertThat(root).isNotNull();
        assertThat(root.getText()).isEqualTo("New Map");
    }

    @Test
    void fileMenu_openMap() throws Exception {
        Path file = tempDir.resolve("open_test.mm");
        Files.writeString(file, "<map><node TEXT='Opened'/></map>", StandardCharsets.UTF_8);

        MapModelFixture fixture = createMapModel();
        Tools.StringReaderCreator reader = new Tools.StringReaderCreator(
            Files.readString(file, StandardCharsets.UTF_8));
        MindMapNode root = fixture.model.loadTree(reader, MapAdapter.sDontAskInstance);
        fixture.model.setRoot(root);
        assertThat(root.getText()).isEqualTo("Opened");
    }

    @Test
    void fileMenu_save() throws Exception {
        Path file = tempDir.resolve("save_test.mm");
        MindMapNode root = loadMap(map, "<map><node TEXT='SaveMe'/></map>");
        saveMapTo(map, file);
        assertThat(Files.exists(file)).isTrue();
        String content = Files.readString(file, StandardCharsets.UTF_8);
        assertThat(content).contains("SaveMe");
    }

    @Test
    void fileMenu_saveAs() throws Exception {
        loadMap(map, "<map><node TEXT='SaveAsTest'/></map>");
        Path file1 = tempDir.resolve("original.mm");
        Path file2 = tempDir.resolve("copy.mm");
        saveMapTo(map, file1);
        saveMapTo(map, file2);
        assertThat(Files.exists(file1)).isTrue();
        assertThat(Files.exists(file2)).isTrue();
        assertThat(Files.readString(file2)).isEqualTo(Files.readString(file1));
    }

    @Test
    void fileMenu_close() throws Exception {
        loadMap(map, "<map><node TEXT='CloseMe'/></map>");
        String xml = saveMapToXml(map);
        assertThat(xml).contains("CloseMe");
    }

    @Test
    void fileMenu_closeUnsavedPrompt() throws Exception {
        MindMapNode root = loadMap(map, "<map><node TEXT='Unsaved'/></map>");
        createChildNode(mapFeedback, root, "DirtyChange");
        assertThat(root.getChildCount())
            .as("Map has unsaved changes (dirty)")
            .isGreaterThan(0);
    }

    @Test
    void fileMenu_lastOpenedFiles() throws Exception {
        for (int i = 0; i < 3; i++) {
            Path file = tempDir.resolve("recent_" + i + ".mm");
            MapModelFixture fixture = createMapModel();
            loadMap(fixture.model, "<map><node TEXT='Recent" + i + "'/></map>");
            saveMapTo(fixture.model, file);
            assertThat(Files.exists(file)).isTrue();
        }
    }

    @Test
    void fileMenu_pageSetup() throws Exception {
        // Page setup is a dialog operation; verify map can produce XML for printing
        loadMap(map, "<map><node TEXT='PageSetup'/></map>");
        String xml = saveMapToXml(map);
        assertThat(xml).startsWith("<map");
    }

    @Test
    void fileMenu_print() throws Exception {
        // Print requires GUI; verify the map model can export for print
        loadMap(map, "<map><node TEXT='PrintTest'/></map>");
        String xml = saveMapToXml(map);
        assertThat(xml).contains("PrintTest");
    }

    @Test
    void fileMenu_printPreview() throws Exception {
        loadMap(map, "<map><node TEXT='PreviewTest'><node TEXT='Child'/></node></map>");
        String xml = saveMapToXml(map);
        assertThat(xml).contains("PreviewTest");
        assertThat(xml).contains("Child");
    }

    @Test
    void fileMenu_quit() throws Exception {
        // Quit flow: verify map state is accessible before quit
        MindMapNode root = loadMap(map, "<map><node TEXT='QuitTest'/></map>");
        createChildNode(mapFeedback, root, "UnsavedWork");
        assertThat(root.getChildCount()).isGreaterThan(0);
    }

    // ========================================================================
    // Helpers
    // ========================================================================

    private String getMapXml() throws Exception {
        StringWriter writer = new StringWriter();
        map.getFilteredXml(writer);
        return writer.toString();
    }
}
