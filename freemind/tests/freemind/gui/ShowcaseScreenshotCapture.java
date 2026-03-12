package tests.freemind.gui;

import freemind.main.FreeMind;
import freemind.main.FreeMindStarter;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Standalone program that launches the real FreeMind application,
 * opens showcase mindmap files one by one, takes full-screen desktop
 * screenshots of each, and exits.
 * <p>
 * This proves end-to-end functionality: the full application window
 * renders on the OS desktop with menus, toolbars, icons, nodes,
 * edges, clouds, colors, and all visual features.
 * <p>
 * Usage: run via Gradle task {@code showcaseScreenshots}.
 * Screenshots are saved to {@code build/test-screenshots/showcase/}.
 */
public class ShowcaseScreenshotCapture {

    private static final String OUTPUT_DIR = "freemind/build/test-screenshots/showcase";
    private static final int RENDER_WAIT_MS = 3000;
    private static final int POST_LOAD_WAIT_MS = 2000;

    public static void main(String[] args) {
        System.out.println("=== FreeMind CE Showcase Screenshot Capture ===");

        // Ensure ~/.freemind/ directory exists for logging and patterns
        File freemindDir = new File(System.getProperty("user.home"), ".freemind");
        freemindDir.mkdirs();

        File outputDir = new File(OUTPUT_DIR);
        outputDir.mkdirs();

        // Collect .mm files to showcase
        String[] showcaseFiles = findShowcaseFiles();
        if (showcaseFiles.length == 0) {
            System.err.println("No .mm showcase files found!");
            System.exit(1);
        }

        System.out.println("Found " + showcaseFiles.length + " showcase files.");

        // Launch FreeMind with the first file, then open remaining files
        try {
            launchAndCapture(showcaseFiles, outputDir);
        } catch (Exception e) {
            System.err.println("Showcase capture failed: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }

        System.out.println("=== Showcase capture complete ===");
        System.exit(0);
    }

    private static String[] findShowcaseFiles() {
        java.util.List<String> files = new java.util.ArrayList<>();

        // Primary showcase: our custom feature-rich map
        addIfExists(files, "freemind/tests/resources/showcase/showcase-full-features.mm");

        // FreeMind documentation maps — rich real-world examples
        addIfExists(files, "freemind/doc/freemind.mm");

        // Feature example maps
        addIfExists(files, "admin/docs/features/0_9_0/examples/work_example.mm");
        addIfExists(files, "admin/docs/features/0_9_0/examples/Bugs.mm");
        addIfExists(files, "admin/docs/features/0_9_0/examples/presentationmap.mm");
        addIfExists(files, "admin/docs/features/1_0_0/Wohnungssuche.mm");
        addIfExists(files, "admin/docs/features/0_9_0/examples/Node_Numberer.mm");
        addIfExists(files, "admin/docs/features/0_9_0/examples/sorting_test.mm");
        addIfExists(files, "admin/docs/features/0_8_0/Features 0.8.0.mm");
        addIfExists(files, "admin/docs/features/0_9_0/Features 0.9.0.mm");

        return files.toArray(new String[0]);
    }

    private static void addIfExists(java.util.List<String> files, String path) {
        File f = new File(path);
        if (f.exists() && f.canRead()) {
            files.add(f.getAbsolutePath());
        }
    }

    private static void launchAndCapture(String[] mmFiles, File outputDir) throws Exception {
        // Start FreeMind with the first file
        String[] startArgs = { mmFiles[0] };

        // Initialize properties the same way FreeMindStarter does
        Properties defaultProps = loadDefaultProperties();
        Properties userProps = new Properties(defaultProps);
        // Override to disable last-map loading and port conflicts
        userProps.setProperty("load_last_map", "false");
        userProps.setProperty("load_last_maps_and_layout", "false");
        userProps.setProperty("single_instance", "false");
        userProps.setProperty("don_t_open_port", "true");
        // Set window size
        userProps.setProperty("appwindow_width", "1400");
        userProps.setProperty("appwindow_height", "900");
        userProps.setProperty("appwindow_x", "50");
        userProps.setProperty("appwindow_y", "50");

        File tempAutoProps = File.createTempFile("freemind_showcase_", ".properties");
        tempAutoProps.deleteOnExit();

        // Launch on EDT
        SwingUtilities.invokeAndWait(() -> {
            try {
                FreeMind.main(startArgs, defaultProps, userProps, tempAutoProps);
            } catch (Exception e) {
                System.err.println("Failed to start FreeMind: " + e.getMessage());
            }
        });

        // Wait for rendering
        System.out.println("Waiting for initial render...");
        Thread.sleep(RENDER_WAIT_MS);

        // Capture first file
        captureDesktopScreenshot(outputDir, mmFiles[0]);

        // Find the FreeMind JFrame
        FreeMind frame = findFreeMindFrame();
        if (frame == null) {
            System.err.println("Could not find FreeMind window!");
            return;
        }

        // Open and capture remaining files
        for (int i = 1; i < mmFiles.length; i++) {
            final String filePath = mmFiles[i];
            System.out.println("Opening: " + filePath);

            SwingUtilities.invokeAndWait(() -> {
                try {
                    frame.getController().getModeController().load(new File(filePath));
                } catch (Exception e) {
                    System.err.println("Failed to load " + filePath + ": " + e.getMessage());
                }
            });

            Thread.sleep(POST_LOAD_WAIT_MS);
            captureDesktopScreenshot(outputDir, filePath);
        }

        // Final cleanup
        SwingUtilities.invokeLater(() -> {
            frame.setVisible(false);
            frame.dispose();
        });
    }

    private static Properties loadDefaultProperties() {
        Properties props = new Properties();
        try {
            File propsFile = new File("freemind/freemind.properties");
            if (propsFile.exists()) {
                try (FileInputStream fis = new FileInputStream(propsFile)) {
                    props.load(fis);
                }
            } else {
                // Try classpath
                var is = ShowcaseScreenshotCapture.class.getResourceAsStream("/freemind.properties");
                if (is != null) {
                    props.load(is);
                    is.close();
                }
            }
        } catch (IOException e) {
            System.err.println("Warning: could not load default properties: " + e.getMessage());
        }
        return props;
    }

    private static FreeMind findFreeMindFrame() {
        for (Frame frame : Frame.getFrames()) {
            if (frame instanceof FreeMind && frame.isVisible()) {
                return (FreeMind) frame;
            }
        }
        return null;
    }

    private static void captureDesktopScreenshot(File outputDir, String mmFilePath) {
        String baseName = new File(mmFilePath).getName().replace(".mm", "");
        // Sanitize filename
        baseName = baseName.replaceAll("[^a-zA-Z0-9_.-]", "_");

        try {
            FreeMind frame = findFreeMindFrame();
            if (frame != null && frame.isVisible()) {
                Rectangle bounds = frame.getBounds();
                Robot robot = new Robot();

                // Window screenshot as JPEG (much smaller for CI summary embedding)
                BufferedImage windowShot = robot.createScreenCapture(bounds);
                File windowFile = new File(outputDir, "window_showcase_" + baseName + ".jpg");
                writeJpeg(windowShot, windowFile, 0.75f);
                System.out.println("  Window screenshot: " + windowFile.getAbsolutePath()
                        + " (" + (windowFile.length() / 1024) + " KB)");

                // Desktop screenshot as PNG (full quality, for artifact download)
                Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
                BufferedImage desktopShot = robot.createScreenCapture(screenRect);
                File desktopFile = new File(outputDir, "desktop_showcase_" + baseName + ".png");
                ImageIO.write(desktopShot, "png", desktopFile);
                System.out.println("  Desktop screenshot: " + desktopFile.getAbsolutePath()
                        + " (" + (desktopFile.length() / 1024) + " KB)");
            } else {
                System.err.println("  FreeMind window not visible for: " + mmFilePath);
            }
        } catch (Exception e) {
            System.err.println("  Screenshot failed for " + mmFilePath + ": " + e.getMessage());
        }
    }

    private static void writeJpeg(BufferedImage image, File file, float quality) throws IOException {
        // Convert to RGB (JPEG doesn't support alpha)
        BufferedImage rgb = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
        rgb.createGraphics().drawImage(image, 0, 0, null);

        var writers = ImageIO.getImageWritersByFormatName("jpeg");
        if (writers.hasNext()) {
            var writer = writers.next();
            var param = writer.getDefaultWriteParam();
            param.setCompressionMode(javax.imageio.ImageWriteParam.MODE_EXPLICIT);
            param.setCompressionQuality(quality);
            try (var out = ImageIO.createImageOutputStream(file)) {
                writer.setOutput(out);
                writer.write(null, new javax.imageio.IIOImage(rgb, null, null), param);
            }
            writer.dispose();
        }
    }
}
