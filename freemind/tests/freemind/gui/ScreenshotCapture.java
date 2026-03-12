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
     * Builds a standard filename from test class and method name.
     */
    public static String filename(String testClass, String suffix) {
        return testClass + "_" + suffix + ".png";
    }
}
