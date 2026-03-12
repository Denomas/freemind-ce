package tests.freemind.gui;

import freemind.main.HeadlessFreeMind;
import freemind.main.Tools;
import freemind.modes.ExtendedMapFeedbackImpl;
import freemind.modes.MapAdapter;
import freemind.modes.MindMapNode;
import freemind.modes.mindmapmode.MindMapMapModel;
import org.assertj.swing.edt.FailOnThreadViolationRepaintManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.TestInfo;

import javax.swing.*;
import java.awt.*;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assumptions.assumeFalse;

/**
 * Abstract base class for all GUI tests.
 * <p>
 * Provides:
 * <ul>
 *   <li>HeadlessFreeMind initialization</li>
 *   <li>EDT thread violation detection via AssertJ Swing</li>
 *   <li>Automatic screenshot capture after each test</li>
 *   <li>Helper methods for common GUI and model operations</li>
 * </ul>
 * <p>
 * All subclasses are tagged with "gui" and will be excluded from
 * headless CI runs (unit test task) and included in GUI test task.
 */
@Tag("gui")
public abstract class GuiTestBase {

    protected static HeadlessFreeMind freeMindMain;

    @BeforeAll
    static void initGuiBase() {
        FailOnThreadViolationRepaintManager.install();
        freeMindMain = new HeadlessFreeMind();
    }

    @BeforeEach
    void checkNotHeadless() {
        assumeFalse(Tools.isHeadless(), "Skipping GUI test in headless mode");
    }

    @AfterEach
    void captureScreenshotAfterTest(TestInfo testInfo) {
        String testName = testInfo.getTestMethod()
            .map(m -> testInfo.getTestClass()
                .map(c -> c.getSimpleName() + "_" + m.getName())
                .orElse(m.getName()))
            .orElse("unknown");

        String filename = ScreenshotCapture.filename(testName, "after");

        // Try to capture active window first
        Window activeWindow = javax.swing.FocusManager.getCurrentManager().getActiveWindow();
        if (activeWindow != null && activeWindow.isShowing()) {
            ScreenshotCapture.capture(activeWindow, filename);
            return;
        }

        // Fall back to tree state screenshot from the map model
        MindMapNode mapRoot = getMapRootForScreenshot();
        if (mapRoot != null) {
            ScreenshotCapture.captureTreeState(mapRoot, testName, filename);
        }
    }

    /**
     * Returns the map root node for screenshot capture.
     * Subclasses should override this to expose their map root.
     * Returns null by default (no tree state screenshot).
     */
    protected MindMapNode getMapRootForScreenshot() {
        return null;
    }

    // ========================================================================
    // Helper Methods — EDT
    // ========================================================================

    /**
     * Runs the given action on the EDT and waits for it to complete.
     */
    protected static void runOnEdt(Runnable action) throws Exception {
        SwingUtilities.invokeAndWait(action);
    }

    /**
     * Runs the given action on the EDT and returns a result.
     */
    protected static <T> T runOnEdtAndGet(java.util.concurrent.Callable<T> action) throws Exception {
        final Object[] holder = new Object[1];
        final Exception[] exHolder = new Exception[1];
        SwingUtilities.invokeAndWait(() -> {
            try {
                holder[0] = action.call();
            } catch (Exception e) {
                exHolder[0] = e;
            }
        });
        if (exHolder[0] != null) throw exHolder[0];
        @SuppressWarnings("unchecked")
        T result = (T) holder[0];
        return result;
    }

    /**
     * Waits for a specified number of milliseconds (for UI stabilization).
     */
    protected static void waitMs(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Waits for EDT events to be processed.
     */
    protected static void flushEdt() throws Exception {
        SwingUtilities.invokeAndWait(() -> {});
    }

    // ========================================================================
    // Helper Methods — Screenshot
    // ========================================================================

    /**
     * Captures a screenshot of the given component with a descriptive name.
     */
    protected void captureScreenshot(Component component, String name) {
        ScreenshotCapture.capture(component, ScreenshotCapture.filename(
            getClass().getSimpleName(), name));
    }

    /**
     * Captures a full-screen screenshot.
     */
    protected void captureFullScreen(String name) {
        try {
            Robot robot = new Robot();
            Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
            java.awt.image.BufferedImage screenshot = robot.createScreenCapture(screenRect);
            java.io.File outputFile = new java.io.File(
                "build/test-screenshots/" + ScreenshotCapture.filename(
                    getClass().getSimpleName(), name + "_fullscreen"));
            outputFile.getParentFile().mkdirs();
            javax.imageio.ImageIO.write(screenshot, "png", outputFile);
            System.out.println("Full screen screenshot saved: " + outputFile.getAbsolutePath());
        } catch (Exception e) {
            System.err.println("Failed to capture full screen: " + e.getMessage());
        }
    }

    // ========================================================================
    // Helper Methods — Map Model Operations
    // ========================================================================

    /**
     * Creates a new map model with feedback, ready for testing.
     */
    protected static MapModelFixture createMapModel() {
        ExtendedMapFeedbackImpl feedback = new ExtendedMapFeedbackImpl();
        MindMapMapModel model = new MindMapMapModel(feedback);
        feedback.setMap(model);
        return new MapModelFixture(feedback, model);
    }

    /**
     * Loads a map from XML string into the given model.
     */
    protected static MindMapNode loadMap(MindMapMapModel model, String xml) throws Exception {
        Tools.StringReaderCreator reader = new Tools.StringReaderCreator(xml);
        MindMapNode root = model.loadTree(reader, MapAdapter.sDontAskInstance);
        model.setRoot(root);
        return root;
    }

    /**
     * Saves a map model to XML string.
     */
    protected static String saveMapToXml(MindMapMapModel model) throws Exception {
        StringWriter writer = new StringWriter();
        model.getFilteredXml(writer);
        return writer.toString();
    }

    /**
     * Saves a map model to a file on disk.
     */
    protected static void saveMapTo(MindMapMapModel model, Path path) throws Exception {
        String xml = saveMapToXml(model);
        Files.writeString(path, xml, StandardCharsets.UTF_8);
    }

    /**
     * Creates a child node with the given text under the parent.
     */
    protected static MindMapNode createChildNode(ExtendedMapFeedbackImpl feedback,
            MindMapNode parent, String text) {
        int index = parent.getChildCount();
        MindMapNode child = feedback.addNewNode(parent, index, true);
        feedback.setNodeText(child, text);
        return child;
    }

    /**
     * Asserts that a node with the given text exists somewhere in the tree.
     */
    protected static void assertNodeExists(MindMapNode root, String text) {
        assertThat(findNodeByText(root, text))
            .as("Node with text '%s' should exist in the tree", text)
            .isNotNull();
    }

    /**
     * Finds a node by text in the tree (depth-first search).
     * Returns null if not found.
     */
    protected static MindMapNode findNodeByText(MindMapNode node, String text) {
        if (text.equals(node.getText())) {
            return node;
        }
        for (int i = 0; i < node.getChildCount(); i++) {
            MindMapNode found = findNodeByText((MindMapNode) node.getChildAt(i), text);
            if (found != null) {
                return found;
            }
        }
        return null;
    }

    /**
     * Counts total nodes in a tree (including root).
     */
    protected static int countNodes(MindMapNode node) {
        int count = 1;
        for (int i = 0; i < node.getChildCount(); i++) {
            count += countNodes((MindMapNode) node.getChildAt(i));
        }
        return count;
    }

    /**
     * Returns the depth of the tree (root = 0).
     */
    protected static int treeDepth(MindMapNode node) {
        int maxChildDepth = -1;
        for (int i = 0; i < node.getChildCount(); i++) {
            int childDepth = treeDepth((MindMapNode) node.getChildAt(i));
            if (childDepth > maxChildDepth) {
                maxChildDepth = childDepth;
            }
        }
        return maxChildDepth + 1;
    }

    /**
     * Simulates opening a menu path. In headless mode, this is a no-op stub
     * that can be expanded when FrameFixture-based testing is available.
     */
    protected static void openMenu(String... path) {
        // Stub: HeadlessFreeMind does not create a real menu bar.
        // When FrameFixture support is added, this will navigate the menu hierarchy.
    }

    /**
     * Simulates clicking a toolbar button by name. In headless mode, this is a no-op stub.
     */
    protected static void clickToolbar(String buttonName) {
        // Stub: HeadlessFreeMind does not create a real toolbar.
    }

    /**
     * Simulates typing text into the currently selected node.
     * In headless mode, this directly sets the node text via the model API.
     */
    protected static void typeInNode(MindMapNode node, ExtendedMapFeedbackImpl feedback, String text) {
        feedback.setNodeText(node, text);
    }

    /**
     * Simulates waiting for a dialog to appear. In headless mode, this is a no-op stub.
     */
    protected static void waitForDialog(String dialogTitle) {
        // Stub: HeadlessFreeMind does not create real dialogs.
    }

    /**
     * Escapes XML special characters in a string.
     */
    protected static String xmlEscape(String s) {
        return s.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("'", "&apos;")
                .replace("\"", "&quot;");
    }

    /**
     * Reloads a map from XML into a fresh model and returns the root.
     */
    protected static MindMapNode reloadMap(String xml) throws Exception {
        MapModelFixture fixture = createMapModel();
        return loadMap(fixture.model, xml);
    }

    /**
     * Holds a map feedback and model pair for test setup convenience.
     */
    protected static class MapModelFixture {
        public final ExtendedMapFeedbackImpl feedback;
        public final MindMapMapModel model;

        MapModelFixture(ExtendedMapFeedbackImpl feedback, MindMapMapModel model) {
            this.feedback = feedback;
            this.model = model;
        }
    }
}
