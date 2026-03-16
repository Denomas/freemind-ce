package tests.freemind;

import freemind.common.XmlBindingTools;
import freemind.main.HeadlessFreeMind;
import freemind.main.Tools;
import freemind.modes.ExtendedMapFeedbackImpl;
import freemind.modes.MapAdapter;
import freemind.modes.MindMapNode;
import freemind.modes.mindmapmode.MindMapMapModel;
import freemind.modes.mindmapmode.MindMapNodeModel;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.io.TempDir;
import tests.freemind.testutil.MindMapGenerator;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Concurrency tests for FreeMind CE core operations.
 * Verifies thread safety of map creation, node manipulation,
 * serialization, and shared infrastructure.
 */
@DisplayName("Concurrency")
@Timeout(30)
class ConcurrencyTest {

    private static final String SIMPLE_MAP = "<map>"
        + "<node TEXT='Root'>"
        + "<node TEXT='Child1'/>"
        + "<node TEXT='Child2'/>"
        + "<node TEXT='Child3'/>"
        + "</node></map>";

    @BeforeAll
    static void ensureResources() {
        new HeadlessFreeMind();
    }

    /**
     * Creates a MindMapMapModel from an XML string.
     */
    private static MindMapMapModel createMapFromXml(String xml) throws Exception {
        ExtendedMapFeedbackImpl feedback = new ExtendedMapFeedbackImpl();
        MindMapMapModel map = new MindMapMapModel(feedback);
        feedback.setMap(map);
        Tools.StringReaderCreator reader = new Tools.StringReaderCreator(xml);
        MindMapNode root = map.loadTree(reader, MapAdapter.sDontAskInstance);
        map.setRoot(root);
        return map;
    }

    /**
     * Returns the XML string for a given map.
     */
    private static String getMapXml(MindMapMapModel map) throws IOException {
        StringWriter sw = new StringWriter();
        map.getFilteredXml(sw);
        return sw.toString();
    }

    // ── 1. Parallel map creation ──

    @Test
    @DisplayName("10 threads each create a MindMapMapModel without exception")
    void parallelMapCreation() throws Exception {
        int threadCount = 10;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch startGate = new CountDownLatch(1);
        List<Future<MindMapMapModel>> futures = new ArrayList<>();

        for (int i = 0; i < threadCount; i++) {
            futures.add(executor.submit(() -> {
                startGate.await();
                return createMapFromXml(SIMPLE_MAP);
            }));
        }

        startGate.countDown();

        for (Future<MindMapMapModel> future : futures) {
            MindMapMapModel map = future.get(10, TimeUnit.SECONDS);
            assertNotNull(map, "Map must not be null");
            assertNotNull(map.getRootNode(), "Root node must not be null");
        }

        executor.shutdown();
        assertTrue(executor.awaitTermination(10, TimeUnit.SECONDS));
    }

    // ── 2. Parallel node insertion ──

    @Test
    @DisplayName("10 threads add children to same parent - documents concurrent behavior")
    void parallelNodeInsertion() throws Exception {
        MindMapMapModel map = createMapFromXml(SIMPLE_MAP);
        MindMapNode root = map.getRootNode();
        int initialCount = root.getChildCount();
        int threadCount = 10;

        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch startGate = new CountDownLatch(1);
        List<Future<Boolean>> futures = new ArrayList<>();

        for (int t = 0; t < threadCount; t++) {
            final int threadIdx = t;
            futures.add(executor.submit(() -> {
                startGate.await();
                MindMapNodeModel child = new MindMapNodeModel(map);
                child.setUserObject("ThreadChild_" + threadIdx);
                root.insert(child, root.getChildCount());
                return true;
            }));
        }

        startGate.countDown();

        // Collect results - some threads may throw ConcurrentModificationException
        // which is expected behavior for unsynchronized node insertion
        int successCount = 0;
        int failCount = 0;
        for (Future<Boolean> future : futures) {
            try {
                future.get(10, TimeUnit.SECONDS);
                successCount++;
            } catch (ExecutionException e) {
                // ConcurrentModificationException or similar is documented behavior
                failCount++;
            }
        }

        // At least some insertions should succeed
        assertTrue(successCount > 0,
            "At least some concurrent insertions must succeed");
        // Total children should have increased
        assertTrue(root.getChildCount() > initialCount,
            "Root must have more children after concurrent insertion");

        executor.shutdown();
        assertTrue(executor.awaitTermination(10, TimeUnit.SECONDS));
    }

    // ── 3. Parallel save to different files ──

    @Test
    @DisplayName("5 threads save same map to different files - no corruption")
    void parallelSaveToDifferentFiles(@TempDir Path tempDir) throws Exception {
        MindMapMapModel map = createMapFromXml(SIMPLE_MAP);
        int threadCount = 5;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch startGate = new CountDownLatch(1);
        List<Future<File>> futures = new ArrayList<>();

        for (int i = 0; i < threadCount; i++) {
            final int idx = i;
            futures.add(executor.submit(() -> {
                startGate.await();
                File file = tempDir.resolve("map_" + idx + ".mm").toFile();
                try (FileWriter writer = new FileWriter(file, StandardCharsets.UTF_8)) {
                    map.getXml(writer);
                }
                return file;
            }));
        }

        startGate.countDown();

        var factory = javax.xml.parsers.DocumentBuilderFactory.newInstance();
        for (Future<File> future : futures) {
            File file = future.get(10, TimeUnit.SECONDS);
            assertTrue(file.exists(), "Saved file must exist: " + file.getName());
            assertTrue(file.length() > 0, "Saved file must not be empty: " + file.getName());
            // Verify the file is valid XML
            assertDoesNotThrow(() -> factory.newDocumentBuilder().parse(file),
                "Saved file must be valid XML: " + file.getName());
        }

        executor.shutdown();
        assertTrue(executor.awaitTermination(10, TimeUnit.SECONDS));
    }

    // ── 4. Parallel XmlBindingTools marshaller/unmarshaller creation ──

    @Test
    @DisplayName("20 threads create marshallers and unmarshallers simultaneously")
    void parallelXmlBindingToolsMarshallerAndUnmarshaller() throws Exception {
        int threadCount = 20;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch startGate = new CountDownLatch(1);
        List<Future<Boolean>> futures = new ArrayList<>();

        for (int i = 0; i < threadCount; i++) {
            final boolean createMarshaller = (i % 2 == 0);
            futures.add(executor.submit(() -> {
                startGate.await();
                XmlBindingTools tools = XmlBindingTools.getInstance();
                if (createMarshaller) {
                    return tools.createMarshaller() != null;
                } else {
                    return tools.createUnmarshaller() != null;
                }
            }));
        }

        startGate.countDown();

        for (Future<Boolean> future : futures) {
            assertTrue(future.get(10, TimeUnit.SECONDS),
                "Concurrent marshaller/unmarshaller creation must succeed");
        }

        executor.shutdown();
        assertTrue(executor.awaitTermination(10, TimeUnit.SECONDS));
    }

    // ── 5. Parallel MindMapGenerator builds ──

    @Test
    @DisplayName("5 threads each build a medium map via MindMapGenerator")
    void parallelMindMapGeneratorBuilds() throws Exception {
        int threadCount = 5;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch startGate = new CountDownLatch(1);
        List<Future<MindMapMapModel>> futures = new ArrayList<>();

        for (int i = 0; i < threadCount; i++) {
            final long seed = 100 + i;
            futures.add(executor.submit(() -> {
                startGate.await();
                return MindMapGenerator.create()
                    .withNodes(50)
                    .withDepth(4)
                    .withWidth(4)
                    .withSeed(seed)
                    .build();
            }));
        }

        startGate.countDown();

        for (Future<MindMapMapModel> future : futures) {
            MindMapMapModel map = future.get(10, TimeUnit.SECONDS);
            assertNotNull(map, "Generated map must not be null");
            assertNotNull(map.getRootNode(), "Generated map root must not be null");
            assertTrue(MindMapGenerator.countNodes(map.getRootNode()) > 1,
                "Generated map must have multiple nodes");
        }

        executor.shutdown();
        assertTrue(executor.awaitTermination(10, TimeUnit.SECONDS));
    }

    // ── 6. Parallel loadFromXml ──

    @Test
    @DisplayName("5 threads each load different XML maps concurrently")
    void parallelLoadFromXml() throws Exception {
        // Prepare 5 different XML strings
        String[] xmlMaps = new String[5];
        for (int i = 0; i < 5; i++) {
            StringBuilder sb = new StringBuilder("<map><node TEXT='Root_" + i + "'>");
            for (int j = 0; j < (i + 1) * 2; j++) {
                sb.append("<node TEXT='Child_").append(i).append("_").append(j).append("'/>");
            }
            sb.append("</node></map>");
            xmlMaps[i] = sb.toString();
        }

        int threadCount = 5;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch startGate = new CountDownLatch(1);
        List<Future<MindMapMapModel>> futures = new ArrayList<>();

        for (int i = 0; i < threadCount; i++) {
            final String xml = xmlMaps[i];
            futures.add(executor.submit(() -> {
                startGate.await();
                return MindMapGenerator.loadFromXml(xml);
            }));
        }

        startGate.countDown();

        for (int i = 0; i < threadCount; i++) {
            MindMapMapModel map = futures.get(i).get(10, TimeUnit.SECONDS);
            assertNotNull(map, "Loaded map must not be null");
            String rootText = map.getRootNode().getText();
            assertTrue(rootText.startsWith("Root_"),
                "Root text must start with Root_, got: " + rootText);
        }

        executor.shutdown();
        assertTrue(executor.awaitTermination(10, TimeUnit.SECONDS));
    }

    // ── Concurrent getXml on same map ──

    @Test
    @DisplayName("Concurrent getXml on same map produces valid output")
    void concurrentGetXmlOnSameMap() throws Exception {
        // Create a single shared map with enough nodes to exercise serialization
        StringBuilder xmlBuilder = new StringBuilder("<map><node TEXT='SharedRoot'>");
        for (int i = 0; i < 20; i++) {
            xmlBuilder.append("<node TEXT='Node_").append(i).append("'>");
            for (int j = 0; j < 3; j++) {
                xmlBuilder.append("<node TEXT='Sub_").append(i).append("_").append(j).append("'/>");
            }
            xmlBuilder.append("</node>");
        }
        xmlBuilder.append("</node></map>");
        MindMapMapModel sharedMap = createMapFromXml(xmlBuilder.toString());

        int threadCount = 5;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch startGate = new CountDownLatch(1);
        List<Future<String>> futures = new ArrayList<>();

        for (int i = 0; i < threadCount; i++) {
            futures.add(executor.submit(() -> {
                startGate.await();
                StringWriter sw = new StringWriter();
                sharedMap.getXml(sw);
                return sw.toString();
            }));
        }

        startGate.countDown();

        // All threads must produce valid, non-empty XML without corruption
        var factory = javax.xml.parsers.DocumentBuilderFactory.newInstance();
        String firstXml = null;
        for (int i = 0; i < threadCount; i++) {
            String xmlResult = futures.get(i).get(10, TimeUnit.SECONDS);
            assertNotNull(xmlResult, "XML output from thread " + i + " must not be null");
            assertFalse(xmlResult.isEmpty(), "XML output from thread " + i + " must not be empty");
            assertTrue(xmlResult.contains("<map"), "Output must contain <map element");
            assertTrue(xmlResult.contains("SharedRoot"), "Output must contain root text");

            // Parse to verify it's well-formed XML
            assertDoesNotThrow(
                () -> factory.newDocumentBuilder().parse(
                    new org.xml.sax.InputSource(new java.io.StringReader(xmlResult))),
                "XML from thread " + i + " must be well-formed");

            if (firstXml == null) {
                firstXml = xmlResult;
            } else {
                assertEquals(firstXml, xmlResult,
                    "All threads should produce identical XML from the same map");
            }
        }

        executor.shutdown();
        assertTrue(executor.awaitTermination(10, TimeUnit.SECONDS));
    }

    // ── 7. Parallel getXml ──

    @Test
    @DisplayName("5 threads each call getXml on different maps producing valid XML")
    void parallelGetXml() throws Exception {
        // Create 5 independent maps
        List<MindMapMapModel> maps = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            String xml = "<map><node TEXT='Map_" + i + "'>"
                + "<node TEXT='A'/><node TEXT='B'/>"
                + "</node></map>";
            maps.add(createMapFromXml(xml));
        }

        int threadCount = 5;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch startGate = new CountDownLatch(1);
        List<Future<String>> futures = new ArrayList<>();

        for (int i = 0; i < threadCount; i++) {
            final MindMapMapModel map = maps.get(i);
            futures.add(executor.submit(() -> {
                startGate.await();
                return getMapXml(map);
            }));
        }

        startGate.countDown();

        for (int i = 0; i < threadCount; i++) {
            String xmlResult = futures.get(i).get(10, TimeUnit.SECONDS);
            assertNotNull(xmlResult, "XML output must not be null");
            assertTrue(xmlResult.contains("<map"), "Output must contain <map element");
            assertTrue(xmlResult.contains("Map_" + i),
                "Output must contain the correct map identifier");
        }

        executor.shutdown();
        assertTrue(executor.awaitTermination(10, TimeUnit.SECONDS));
    }

    // ── 8. Thread safety of HeadlessFreeMind ──

    @Test
    @DisplayName("10 threads call new HeadlessFreeMind() without exception")
    void parallelHeadlessFreeMindCreation() throws Exception {
        int threadCount = 10;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch startGate = new CountDownLatch(1);
        List<Future<HeadlessFreeMind>> futures = new ArrayList<>();

        for (int i = 0; i < threadCount; i++) {
            futures.add(executor.submit(() -> {
                startGate.await();
                return new HeadlessFreeMind();
            }));
        }

        startGate.countDown();

        for (Future<HeadlessFreeMind> future : futures) {
            HeadlessFreeMind instance = future.get(10, TimeUnit.SECONDS);
            assertNotNull(instance, "HeadlessFreeMind instance must not be null");
        }

        executor.shutdown();
        assertTrue(executor.awaitTermination(10, TimeUnit.SECONDS));
    }

    // ── 9. Parallel node text mutation ──

    @Test
    @DisplayName("10 threads set text on different nodes of the same map")
    void parallelNodeTextMutation() throws Exception {
        // Create a map with enough child nodes for each thread
        StringBuilder xmlBuilder = new StringBuilder("<map><node TEXT='Root'>");
        for (int i = 0; i < 10; i++) {
            xmlBuilder.append("<node TEXT='Node_").append(i).append("'/>");
        }
        xmlBuilder.append("</node></map>");
        MindMapMapModel map = createMapFromXml(xmlBuilder.toString());
        MindMapNode root = map.getRootNode();

        int threadCount = 10;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch startGate = new CountDownLatch(1);
        List<Future<Boolean>> futures = new ArrayList<>();

        for (int i = 0; i < threadCount; i++) {
            final int idx = i;
            final MindMapNode node = (MindMapNode) root.getChildAt(idx);
            futures.add(executor.submit(() -> {
                startGate.await();
                String newText = "Modified_" + idx;
                node.setUserObject(newText);
                return newText.equals(node.getText());
            }));
        }

        startGate.countDown();

        for (Future<Boolean> future : futures) {
            assertTrue(future.get(10, TimeUnit.SECONDS),
                "Text mutation on distinct nodes must succeed");
        }

        // Verify all nodes have been modified
        for (int i = 0; i < threadCount; i++) {
            MindMapNode child = (MindMapNode) root.getChildAt(i);
            assertEquals("Modified_" + i, child.getText(),
                "Node " + i + " text must be updated");
        }

        executor.shutdown();
        assertTrue(executor.awaitTermination(10, TimeUnit.SECONDS));
    }

    // ── 10. CountDownLatch synchronized start verification ──

    @Test
    @DisplayName("CountDownLatch ensures true concurrent start across 10 threads")
    void countDownLatchSynchronizedStart() throws Exception {
        int threadCount = 10;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch readyLatch = new CountDownLatch(threadCount);
        CountDownLatch startGate = new CountDownLatch(1);
        ConcurrentLinkedQueue<Long> startTimes = new ConcurrentLinkedQueue<>();

        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                readyLatch.countDown();
                try {
                    startGate.await();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
                startTimes.add(System.nanoTime());
                // Do some work: create a map
                try {
                    createMapFromXml(SIMPLE_MAP);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
        }

        // Wait until all threads are ready
        assertTrue(readyLatch.await(5, TimeUnit.SECONDS),
            "All threads must be ready within 5 seconds");

        // Release all threads simultaneously
        startGate.countDown();

        executor.shutdown();
        assertTrue(executor.awaitTermination(10, TimeUnit.SECONDS));

        // Verify all threads recorded a start time
        assertEquals(threadCount, startTimes.size(),
            "All threads must have recorded a start time");

        // Verify that start times are close together (within 50ms spread)
        long minTime = startTimes.stream().mapToLong(Long::longValue).min().orElse(0);
        long maxTime = startTimes.stream().mapToLong(Long::longValue).max().orElse(0);
        long spreadMs = (maxTime - minTime) / 1_000_000;
        assertTrue(spreadMs < 50,
            "Thread start times should be within 50ms spread, was " + spreadMs + "ms");
    }

    // ── 11. Parallel map creation with different seed values ──

    @Test
    @DisplayName("10 threads create maps with different seeds - all independent")
    void parallelMapCreationDifferentSeeds() throws Exception {
        int threadCount = 10;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch startGate = new CountDownLatch(1);
        List<Future<Integer>> futures = new ArrayList<>();

        for (int i = 0; i < threadCount; i++) {
            final long seed = i * 1000;
            futures.add(executor.submit(() -> {
                startGate.await();
                MindMapMapModel map = MindMapGenerator.create()
                    .withNodes(20)
                    .withDepth(3)
                    .withWidth(3)
                    .withSeed(seed)
                    .build();
                return MindMapGenerator.countNodes(map.getRootNode());
            }));
        }

        startGate.countDown();

        for (Future<Integer> future : futures) {
            int count = future.get(10, TimeUnit.SECONDS);
            assertTrue(count > 0, "Each map must have at least one node");
        }

        executor.shutdown();
        assertTrue(executor.awaitTermination(10, TimeUnit.SECONDS));
    }

    // ── 12. Parallel XML round-trip ──

    @Test
    @DisplayName("5 threads perform XML round-trip: create, serialize, deserialize")
    void parallelXmlRoundTrip() throws Exception {
        int threadCount = 5;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch startGate = new CountDownLatch(1);
        List<Future<Boolean>> futures = new ArrayList<>();

        for (int i = 0; i < threadCount; i++) {
            final int idx = i;
            futures.add(executor.submit(() -> {
                startGate.await();
                // Create a map
                String xml = "<map><node TEXT='RT_" + idx + "'>"
                    + "<node TEXT='SubA'/><node TEXT='SubB'/>"
                    + "</node></map>";
                MindMapMapModel map1 = createMapFromXml(xml);

                // Serialize
                String serialized = getMapXml(map1);

                // Deserialize
                MindMapMapModel map2 = MindMapGenerator.loadFromXml(serialized);

                // Verify root text
                return map2.getRootNode().getText().equals("RT_" + idx);
            }));
        }

        startGate.countDown();

        for (Future<Boolean> future : futures) {
            assertTrue(future.get(10, TimeUnit.SECONDS),
                "XML round-trip must preserve root node text");
        }

        executor.shutdown();
        assertTrue(executor.awaitTermination(10, TimeUnit.SECONDS));
    }

    // ── 13. Parallel save to files and reload ──

    @Test
    @DisplayName("5 threads save maps to files and reload - files are valid")
    void parallelSaveAndReload(@TempDir Path tempDir) throws Exception {
        int threadCount = 5;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch startGate = new CountDownLatch(1);
        List<Future<Path>> futures = new ArrayList<>();

        for (int i = 0; i < threadCount; i++) {
            final int idx = i;
            futures.add(executor.submit(() -> {
                startGate.await();
                Path file = tempDir.resolve("reload_" + idx + ".mm");
                MindMapGenerator.create()
                    .withNodes(15)
                    .withDepth(3)
                    .withWidth(3)
                    .withSeed(idx)
                    .toFile(file.toString());
                return file;
            }));
        }

        startGate.countDown();

        for (Future<Path> future : futures) {
            Path file = future.get(10, TimeUnit.SECONDS);
            assertTrue(Files.exists(file), "Saved file must exist");
            // Reload and verify
            MindMapMapModel reloaded = MindMapGenerator.loadFromFile(file.toString());
            assertNotNull(reloaded.getRootNode(), "Reloaded map must have root");
        }

        executor.shutdown();
        assertTrue(executor.awaitTermination(10, TimeUnit.SECONDS));
    }

    // ── 14. Parallel ExtendedMapFeedbackImpl creation ──

    @Test
    @DisplayName("10 threads each create ExtendedMapFeedbackImpl and MindMapMapModel")
    void parallelFeedbackAndModelCreation() throws Exception {
        int threadCount = 10;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch startGate = new CountDownLatch(1);
        List<Future<Boolean>> futures = new ArrayList<>();

        for (int i = 0; i < threadCount; i++) {
            futures.add(executor.submit(() -> {
                startGate.await();
                ExtendedMapFeedbackImpl feedback = new ExtendedMapFeedbackImpl();
                MindMapMapModel map = new MindMapMapModel(feedback);
                feedback.setMap(map);
                return map.getRootNode() != null;
            }));
        }

        startGate.countDown();

        for (Future<Boolean> future : futures) {
            assertTrue(future.get(10, TimeUnit.SECONDS),
                "Concurrent feedback+model creation must succeed");
        }

        executor.shutdown();
        assertTrue(executor.awaitTermination(10, TimeUnit.SECONDS));
    }

    // ── 15. Parallel node attribute mutation on different nodes ──

    @Test
    @DisplayName("10 threads add attributes to different nodes concurrently")
    void parallelNodeAttributeMutation() throws Exception {
        StringBuilder xmlBuilder = new StringBuilder("<map><node TEXT='Root'>");
        for (int i = 0; i < 10; i++) {
            xmlBuilder.append("<node TEXT='AttrNode_").append(i).append("'/>");
        }
        xmlBuilder.append("</node></map>");
        MindMapMapModel map = createMapFromXml(xmlBuilder.toString());
        MindMapNode root = map.getRootNode();

        int threadCount = 10;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch startGate = new CountDownLatch(1);
        List<Future<Boolean>> futures = new ArrayList<>();

        for (int i = 0; i < threadCount; i++) {
            final int idx = i;
            final MindMapNode node = (MindMapNode) root.getChildAt(idx);
            futures.add(executor.submit(() -> {
                startGate.await();
                node.addAttribute(
                    new freemind.modes.attributes.Attribute("key_" + idx, "val_" + idx));
                return node.getAttributeTableLength() > 0;
            }));
        }

        startGate.countDown();

        for (Future<Boolean> future : futures) {
            assertTrue(future.get(10, TimeUnit.SECONDS),
                "Attribute addition on distinct nodes must succeed");
        }

        executor.shutdown();
        assertTrue(executor.awaitTermination(10, TimeUnit.SECONDS));
    }
}
