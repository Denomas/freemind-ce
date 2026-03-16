package tests.freemind.io;

import freemind.main.HeadlessFreeMind;
import freemind.modes.mindmapmode.MindMapMapModel;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import tests.freemind.testutil.MindMapGenerator;

import java.io.IOException;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Performance tests for large mind map IO operations.
 * Validates that save/load operations complete within acceptable time bounds
 * and that memory usage stays within limits.
 */
@DisplayName("Large File Performance Tests")
@Tag("performance")
class LargeFilePerformanceTest {

    @BeforeAll
    static void init() {
        new HeadlessFreeMind();
    }

    @Test
    @DisplayName("Medium map (1K nodes) save completes under 5 seconds")
    void mediumMapSaveUnder5Seconds() throws IOException {
        MindMapMapModel map = MindMapGenerator.medium().build();
        StringWriter sw = new StringWriter();

        long start = System.nanoTime();
        map.getXml(sw);
        long elapsed = System.nanoTime() - start;

        double seconds = elapsed / 1_000_000_000.0;
        assertTrue(seconds < 5.0,
                "Medium map save took " + seconds + "s, expected < 5s");
    }

    @Test
    @DisplayName("Medium map (1K nodes) load completes under 5 seconds")
    void mediumMapLoadUnder5Seconds() throws Exception {
        String xml = MindMapGenerator.medium().toXmlString();

        long start = System.nanoTime();
        MindMapMapModel loaded = MindMapGenerator.loadFromXml(xml);
        long elapsed = System.nanoTime() - start;

        double seconds = elapsed / 1_000_000_000.0;
        assertTrue(seconds < 5.0,
                "Medium map load took " + seconds + "s, expected < 5s");
        assertNotNull(loaded.getRootNode());
    }

    @Test
    @DisplayName("Medium map XML output size is reasonable (> 10KB)")
    void mediumMapXmlSizeReasonable() throws IOException {
        String xml = MindMapGenerator.medium().toXmlString();

        assertTrue(xml.length() > 10_000,
                "Medium map XML should be > 10KB, got " + xml.length() + " chars");
    }

    @Test
    @DisplayName("Large map (100K nodes) build completes under 30 seconds")
    void largeMapBuildUnder30Seconds() {
        long start = System.nanoTime();
        MindMapMapModel map = MindMapGenerator.large().build();
        long elapsed = System.nanoTime() - start;

        double seconds = elapsed / 1_000_000_000.0;
        assertTrue(seconds < 30.0,
                "Large map build took " + seconds + "s, expected < 30s");
        assertNotNull(map.getRootNode());
    }

    @Test
    @DisplayName("Large map (100K nodes) node count is approximately 100K")
    void largeMapNodeCountApprox100K() {
        MindMapMapModel map = MindMapGenerator.large().build();
        int count = MindMapGenerator.countNodes(map.getRootNode());

        assertTrue(count >= 50_000,
                "Large map should have >= 50K nodes, got: " + count);
        assertTrue(count <= 150_000,
                "Large map should have <= 150K nodes, got: " + count);
    }

    @Test
    @DisplayName("Heap usage after medium map build is under 100MB")
    void mediumMapHeapUnder100MB() {
        Runtime rt = Runtime.getRuntime();
        rt.gc();
        long beforeUsed = rt.totalMemory() - rt.freeMemory();

        MindMapMapModel map = MindMapGenerator.medium().build();
        assertNotNull(map.getRootNode());

        long afterUsed = rt.totalMemory() - rt.freeMemory();
        long delta = afterUsed - beforeUsed;
        double deltaMB = delta / (1024.0 * 1024.0);

        assertTrue(deltaMB < 100.0,
                "Medium map heap delta should be < 100MB, got " + deltaMB + "MB");
    }

    @Test
    @DisplayName("Medium map getXml to StringWriter: output length > 50KB")
    void mediumMapXmlOutputLength() throws IOException {
        MindMapMapModel map = MindMapGenerator.medium().build();
        StringWriter sw = new StringWriter();
        map.getXml(sw);
        String xml = sw.toString();

        assertTrue(xml.length() > 50_000,
                "Medium map XML should be > 50KB, got " + xml.length() + " chars");
    }

    @Test
    @DisplayName("Round-trip timing: build + toXml + loadFromXml under 10s for 1K nodes")
    void roundTripTimingUnder10Seconds() throws Exception {
        long start = System.nanoTime();

        MindMapMapModel map = MindMapGenerator.medium().build();
        StringWriter sw = new StringWriter();
        map.getXml(sw);
        String xml = sw.toString();
        MindMapMapModel loaded = MindMapGenerator.loadFromXml(xml);

        long elapsed = System.nanoTime() - start;
        double seconds = elapsed / 1_000_000_000.0;

        assertTrue(seconds < 10.0,
                "Full round-trip took " + seconds + "s, expected < 10s");
        assertNotNull(loaded.getRootNode());
    }

    @Test
    @DisplayName("Multiple medium map builds don't accumulate memory excessively")
    void multipleBuildsNoMemoryAccumulation() {
        Runtime rt = Runtime.getRuntime();
        rt.gc();
        long baseline = rt.totalMemory() - rt.freeMemory();

        for (int i = 0; i < 5; i++) {
            MindMapMapModel map = MindMapGenerator.medium().build();
            assertNotNull(map.getRootNode());
        }

        rt.gc();
        long afterUsed = rt.totalMemory() - rt.freeMemory();
        long delta = afterUsed - baseline;
        double deltaMB = delta / (1024.0 * 1024.0);

        // After GC, retained memory should be reasonable
        // Only the last map and some overhead should remain
        assertTrue(deltaMB < 100.0,
                "After 5 medium map builds + GC, heap delta should be < 100MB, got " + deltaMB + "MB");
    }
}
