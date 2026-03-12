package tests.freemind.gui;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Utility for capturing screenshots of Swing components during GUI tests.
 * Screenshots are saved to build/test-screenshots/{timestamp}/ directory.
 */
public final class ScreenshotCapture {

    private static final String BASE_DIR = "build/test-screenshots";
    private static final DateTimeFormatter TIMESTAMP_FMT =
        DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
    private static String sessionDir;

    private ScreenshotCapture() {}

    private static synchronized String getSessionDir() {
        if (sessionDir == null) {
            sessionDir = BASE_DIR + "/" + LocalDateTime.now().format(TIMESTAMP_FMT);
            new File(sessionDir).mkdirs();
        }
        return sessionDir;
    }

    /**
     * Captures a screenshot of the given component.
     *
     * @param component the component to capture
     * @param filename  the output filename (without path, e.g. "NoteEditor_turkish.png")
     * @return the File where the screenshot was saved, or null on failure
     */
    public static File capture(Component component, String filename) {
        if (component == null || component.getWidth() <= 0 || component.getHeight() <= 0) {
            System.err.println("ScreenshotCapture: component has no size, skipping " + filename);
            return null;
        }

        BufferedImage image = new BufferedImage(
            component.getWidth(), component.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();
        try {
            component.print(g);
        } finally {
            g.dispose();
        }

        File outputFile = new File(getSessionDir(), filename);
        try {
            ImageIO.write(image, "png", outputFile);
            System.out.println("Screenshot saved: " + outputFile.getAbsolutePath());
            return outputFile;
        } catch (IOException e) {
            System.err.println("ScreenshotCapture: failed to write " + outputFile + ": " + e);
            return null;
        }
    }

    /**
     * Captures a full-screen screenshot using {@link Robot}.
     *
     * @param filename the output filename (without path)
     * @return the File where the screenshot was saved, or null on failure
     */
    public static File captureFullScreen(String filename) {
        try {
            Robot robot = new Robot();
            Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
            BufferedImage screenshot = robot.createScreenCapture(screenRect);
            File outputFile = new File(getSessionDir(), filename);
            ImageIO.write(screenshot, "png", outputFile);
            System.out.println("Full screen screenshot saved: " + outputFile.getAbsolutePath());
            return outputFile;
        } catch (Exception e) {
            System.err.println("ScreenshotCapture: failed to capture full screen: " + e.getMessage());
            return null;
        }
    }

    /**
     * Captures a screenshot of a specific Dialog window.
     *
     * @param dialog   the dialog to capture
     * @param filename the output filename (without path)
     * @return the File where the screenshot was saved, or null on failure
     */
    public static File captureDialog(Dialog dialog, String filename) {
        if (dialog == null || !dialog.isShowing()) {
            System.err.println("ScreenshotCapture: dialog not showing, skipping " + filename);
            return null;
        }
        return capture(dialog, filename);
    }

    /**
     * Renders a mind map tree structure as a PNG image.
     * Used when no GUI window is available (model-level tests).
     *
     * @param root     the root node of the tree
     * @param testName descriptive name shown in the image header
     * @param filename the output filename (without path)
     * @return the File where the screenshot was saved, or null on failure
     */
    public static File captureTreeState(freemind.modes.MindMapNode root, String testName, String filename) {
        if (root == null) {
            System.err.println("ScreenshotCapture: root is null, skipping " + filename);
            return null;
        }

        java.util.List<String> lines = new java.util.ArrayList<>();
        lines.add("Test: " + testName);
        lines.add("─".repeat(60));
        renderTree(root, "", true, lines);

        int lineHeight = 18;
        int padding = 16;
        int width = 700;
        int height = padding * 2 + lines.size() * lineHeight;

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();
        try {
            g.setRenderingHint(java.awt.RenderingHints.KEY_TEXT_ANTIALIASING,
                java.awt.RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g.setColor(Color.WHITE);
            g.fillRect(0, 0, width, height);

            g.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
            g.setColor(new Color(0x33, 0x33, 0x33));

            for (int i = 0; i < lines.size(); i++) {
                String line = lines.get(i);
                if (i == 0) {
                    g.setFont(new Font(Font.MONOSPACED, Font.BOLD, 14));
                    g.setColor(new Color(0x00, 0x66, 0xCC));
                } else if (i == 1) {
                    g.setColor(new Color(0x99, 0x99, 0x99));
                } else {
                    g.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
                    g.setColor(new Color(0x33, 0x33, 0x33));
                }
                g.drawString(line, padding, padding + (i + 1) * lineHeight);
            }
        } finally {
            g.dispose();
        }

        File outputFile = new File(getSessionDir(), filename);
        try {
            ImageIO.write(image, "png", outputFile);
            System.out.println("Tree state screenshot saved: " + outputFile.getAbsolutePath());
            return outputFile;
        } catch (IOException e) {
            System.err.println("ScreenshotCapture: failed to write " + outputFile + ": " + e);
            return null;
        }
    }

    private static void renderTree(freemind.modes.MindMapNode node, String prefix, boolean isLast,
            java.util.List<String> lines) {
        String connector = prefix.isEmpty() ? "" : (isLast ? "└── " : "├── ");
        String text = node.getText();
        if (text == null) text = "(null)";
        if (text.length() > 80) text = text.substring(0, 77) + "...";

        String noteMarker = "";
        try {
            if (node.getNoteText() != null && !node.getNoteText().isEmpty()) {
                noteMarker = " [note]";
            }
        } catch (Exception ignored) {}

        String linkMarker = "";
        try {
            if (node.getLink() != null && !node.getLink().isEmpty()) {
                linkMarker = " [link: " + node.getLink() + "]";
            }
        } catch (Exception ignored) {}

        lines.add(prefix + connector + text + noteMarker + linkMarker);

        String childPrefix = prefix + (prefix.isEmpty() ? "" : (isLast ? "    " : "│   "));
        int childCount = node.getChildCount();
        for (int i = 0; i < childCount; i++) {
            renderTree((freemind.modes.MindMapNode) node.getChildAt(i),
                childPrefix, i == childCount - 1, lines);
        }
    }

    /**
     * Builds a standard filename from test class and method name.
     */
    public static String filename(String testClass, String suffix) {
        return testClass + "_" + suffix + ".png";
    }
}
