package tests.freemind.testutil;

import freemind.main.HeadlessFreeMind;
import freemind.modes.ExtendedMapFeedbackImpl;
import freemind.modes.MindIcon;
import freemind.modes.MindMapNode;
import freemind.modes.mindmapmode.MindMapMapModel;
import freemind.modes.mindmapmode.MindMapNodeModel;

import freemind.main.Tools;
import freemind.modes.MapAdapter;

import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Random;

/**
 * Builder-pattern test data generator for MindMapMapModel instances.
 * Creates parameterized mind maps for unit, integration, and performance tests.
 */
public class MindMapGenerator {

    private int nodeCount = 10;
    private int maxDepth = 3;
    private int avgChildren = 3;
    private int attrCount = 0;
    private int iconCount = 0;
    private int notePercent = 0;
    private int linkPercent = 0;
    private boolean edgeCaseContent = false;
    private long seed = 42;

    private static final String[] SAMPLE_ICONS = {
            "button_ok", "button_cancel", "idea", "help",
            "messagebox_warning", "stop-sign", "info", "bookmark"
    };

    private MindMapGenerator() {
    }

    public static MindMapGenerator create() {
        return new MindMapGenerator();
    }

    /** Convenience: 10 nodes, depth 3 */
    public static MindMapGenerator small() {
        return create().withNodes(10).withDepth(3).withWidth(3);
    }

    /** Convenience: 1000 nodes, depth 6 */
    public static MindMapGenerator medium() {
        return create().withNodes(1_000).withDepth(6).withWidth(5);
    }

    /** Convenience: 100,000 nodes, depth 10 */
    public static MindMapGenerator large() {
        return create().withNodes(100_000).withDepth(10).withWidth(8);
    }

    public MindMapGenerator withNodes(int count) {
        this.nodeCount = count;
        return this;
    }

    public MindMapGenerator withDepth(int maxDepth) {
        this.maxDepth = maxDepth;
        return this;
    }

    public MindMapGenerator withWidth(int avgChildren) {
        this.avgChildren = avgChildren;
        return this;
    }

    public MindMapGenerator withAttributes(int attrCount) {
        this.attrCount = attrCount;
        return this;
    }

    public MindMapGenerator withIcons(int iconCount) {
        this.iconCount = iconCount;
        return this;
    }

    public MindMapGenerator withNotes(int notePercent) {
        this.notePercent = Math.max(0, Math.min(100, notePercent));
        return this;
    }

    public MindMapGenerator withLinks(int linkPercent) {
        this.linkPercent = Math.max(0, Math.min(100, linkPercent));
        return this;
    }

    public MindMapGenerator withEdgeCaseContent(boolean enabled) {
        this.edgeCaseContent = enabled;
        return this;
    }

    public MindMapGenerator withSeed(long seed) {
        this.seed = seed;
        return this;
    }

    /** Convenience: edge-case content for chaos/resilience testing */
    public static MindMapGenerator edgeCases() {
        return create().withNodes(20).withDepth(4).withWidth(3)
                .withEdgeCaseContent(true).withNotes(50).withLinks(50)
                .withAttributes(2).withIcons(1);
    }

    /**
     * Builds a MindMapMapModel with the configured parameters.
     * HeadlessFreeMind must be initialized before calling this.
     */
    public MindMapMapModel build() {
        new HeadlessFreeMind();
        Random rng = new Random(seed);

        ExtendedMapFeedbackImpl mapFeedback = new ExtendedMapFeedbackImpl();
        MindMapMapModel map = new MindMapMapModel(mapFeedback);
        mapFeedback.setMap(map);
        MindMapNodeModel root = (MindMapNodeModel) map.getRootNode();
        root.setUserObject("Root");

        int[] created = {1}; // count including root
        buildChildren(root, map, rng, 1, created);

        return map;
    }

    private void buildChildren(MindMapNodeModel parent, MindMapMapModel map,
                               Random rng, int currentDepth, int[] created) {
        if (currentDepth >= maxDepth || created[0] >= nodeCount) {
            return;
        }

        int children = Math.max(1, avgChildren + rng.nextInt(3) - 1);
        for (int i = 0; i < children && created[0] < nodeCount; i++) {
            MindMapNodeModel child = new MindMapNodeModel(map);
            child.setUserObject("Node_" + created[0]);
            created[0]++;

            decorateNode(child, rng, created[0]);
            parent.insert(child, -1);

            buildChildren(child, map, rng, currentDepth + 1, created);
        }
    }

    private static final String[] EDGE_CASE_TEXTS = {
            "", // empty text
            "<script>alert('test')</script>", // script tag — legitimate user content
            "file:///etc/passwd", // file path — security checklist item
            "' OR 1=1 --", // SQL — user may document injection tests
            "Node with <tag> & \"quotes\" in text", // XML special chars
            "\ud83c\uddf9\ud83c\uddf7\ud83c\udfaf\ud83e\uddea", // emoji: flag + target + test tube
            "\u0645\u0631\u062d\u0628\u0627", // Arabic: مرحبا
            "\u05e9\u05dc\u05d5\u05dd", // Hebrew: שלום
            "A".repeat(10_000), // very long string
            "Line1\nLine2\nLine3\n\n\nMultiple blanks",
    };

    private void decorateNode(MindMapNodeModel node, Random rng, int index) {
        // Edge-case content: adversarial text for chaos/resilience testing
        if (edgeCaseContent) {
            String text = EDGE_CASE_TEXTS[index % EDGE_CASE_TEXTS.length];
            if (!text.isEmpty()) {
                node.setUserObject(text);
            }
        }

        // Icons
        for (int i = 0; i < iconCount && i < SAMPLE_ICONS.length; i++) {
            node.addIcon(MindIcon.factory(SAMPLE_ICONS[i]), MindIcon.LAST);
        }

        // Notes
        if (notePercent > 0 && rng.nextInt(100) < notePercent) {
            String noteContent = edgeCaseContent
                    ? "<html><body><script>alert('note')</script> file:///etc/shadow Node " + index + "</body></html>"
                    : "<html><body>Note for node " + index + "</body></html>";
            node.setNoteText(noteContent);
        }

        // Links
        if (linkPercent > 0 && rng.nextInt(100) < linkPercent) {
            String link = edgeCaseContent
                    ? (index % 3 == 0 ? "javascript:void(0)" : index % 3 == 1 ? "file:///etc/passwd" : "https://example.com/q?a=1&b=<tag>")
                    : "https://example.com/node/" + index;
            node.setLink(link);
        }

        // Attributes
        for (int i = 0; i < attrCount; i++) {
            String attrValue = edgeCaseContent
                    ? "value with <xml> & \"special\" chars"
                    : "value_" + index + "_" + i;
            node.addAttribute(
                    new freemind.modes.attributes.Attribute("attr_" + i, attrValue));
        }
    }

    /**
     * Builds the map and writes it to an .mm file.
     */
    public void toFile(String path) throws IOException {
        MindMapMapModel map = build();
        try (Writer writer = new FileWriter(path, java.nio.charset.StandardCharsets.UTF_8)) {
            map.getXml(writer);
        }
    }

    /**
     * Builds the map and returns the XML string.
     */
    public String toXmlString() throws IOException {
        MindMapMapModel map = build();
        StringWriter sw = new StringWriter();
        map.getXml(sw);
        return sw.toString();
    }

    /**
     * Loads a MindMapMapModel from an .mm file on disk.
     * Useful for round-trip tests: generate → save → load → verify.
     */
    public static MindMapMapModel loadFromFile(String path) throws Exception {
        new HeadlessFreeMind();
        ExtendedMapFeedbackImpl mapFeedback = new ExtendedMapFeedbackImpl();
        MindMapMapModel map = new MindMapMapModel(mapFeedback);
        mapFeedback.setMap(map);
        Tools.StringReaderCreator readerCreator = new Tools.StringReaderCreator(
                java.nio.file.Files.readString(java.nio.file.Path.of(path)));
        MindMapNode root = map.loadTree(readerCreator, MapAdapter.sDontAskInstance);
        map.setRoot(root);
        return map;
    }

    /**
     * Loads a MindMapMapModel from an XML string.
     */
    public static MindMapMapModel loadFromXml(String xml) throws Exception {
        new HeadlessFreeMind();
        ExtendedMapFeedbackImpl mapFeedback = new ExtendedMapFeedbackImpl();
        MindMapMapModel map = new MindMapMapModel(mapFeedback);
        mapFeedback.setMap(map);
        Tools.StringReaderCreator readerCreator = new Tools.StringReaderCreator(xml);
        MindMapNode root = map.loadTree(readerCreator, MapAdapter.sDontAskInstance);
        map.setRoot(root);
        return map;
    }

    /**
     * Counts all nodes in a map recursively.
     */
    public static int countNodes(MindMapNode node) {
        int count = 1;
        for (int i = 0; i < node.getChildCount(); i++) {
            count += countNodes((MindMapNode) node.getChildAt(i));
        }
        return count;
    }
}
