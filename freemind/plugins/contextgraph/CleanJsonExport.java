/*FreeMind - A Program for creating and viewing Mindmaps
 *Copyright (C) 2000-2026 Joerg Mueller, Daniel Polansky, Christian Foltin, Dimitri Polivaev and others.
 *See COPYING for Details
 *
 *This program is free software; you can redistribute it and/or
 *modify it under the terms of the GNU General Public License
 *as published by the Free Software Foundation; either version 2
 *of the License, or (at your option) any later version.
 *
 *This program is distributed in the hope that it will be useful,
 *but WITHOUT ANY WARRANTY; without even the implied warranty of
 *MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *GNU General Public License for more details.
 */

package plugins.contextgraph;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Vector;

import javax.swing.JOptionPane;

import freemind.extensions.ExportHook;
import freemind.main.Tools;
import freemind.modes.MindIcon;
import freemind.modes.MindMapArrowLink;
import freemind.modes.MindMapLink;
import freemind.modes.MindMapLinkRegistry;
import freemind.modes.MindMapNode;
import freemind.modes.attributes.Attribute;

/**
 * Exports the mind map as a clean JSON document enriched with notes, icons,
 * attributes, links, and cross-references. Parent nodes become object keys
 * and leaf nodes become string array items.
 * Designed for easy consumption by AI/LLM systems.
 */
public class CleanJsonExport extends ExportHook {

    private MindMapLinkRegistry linkRegistry;

    @Override
    public void startupMapHook() {
        super.startupMapHook();

        File chosenFile = chooseFile("json",
                getTranslatableResourceString("file_description"), null);
        if (chosenFile == null) {
            return;
        }

        try {
            getController().getFrame().setWaitingCursor(true);

            MindMapNode root = getController().getMap().getRootNode();
            linkRegistry = getController().getMap().getLinkRegistry();

            String rootText = getPlainText(root);
            if (rootText == null || rootText.isEmpty()) {
                rootText = "mindmap";
            }

            try (BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(new FileOutputStream(chosenFile),
                            StandardCharsets.UTF_8))) {
                writer.write("{");
                writer.newLine();
                writer.write("    " + jsonString(rootText) + ": ");
                writeNodeValue(writer, root, "    ");
                writer.newLine();
                writer.write("}");
                writer.newLine();
            }

            if (Tools.safeEquals(getResourceString("load_file"), "true")) {
                getController().getFrame()
                        .openDocument(Tools.fileToUrl(chosenFile));
            }
        } catch (Exception e) {
            freemind.main.Resources.getInstance().logException(e);
            JOptionPane.showMessageDialog(
                    getController().getFrame().getContentPane(),
                    e.getLocalizedMessage(), getResourceString("export_error"),
                    JOptionPane.ERROR_MESSAGE);
        } finally {
            getController().getFrame().setWaitingCursor(false);
        }
    }

    private void writeNodeValue(BufferedWriter writer, MindMapNode node,
            String indent) throws Exception {
        List<MindMapNode> children = getChildList(node);
        boolean hasMetadata = hasRichContent(node);

        if (children.isEmpty() && !hasMetadata) {
            writer.write("null");
            return;
        }

        boolean allLeaves = true;
        for (MindMapNode child : children) {
            if (child.getChildren() != null
                    && !child.getChildren().isEmpty()) {
                allLeaves = false;
                break;
            }
            if (hasRichContent(child)) {
                allLeaves = false;
                break;
            }
        }

        if (allLeaves && !hasMetadata) {
            writer.write("[");
            writer.newLine();
            for (int i = 0; i < children.size(); i++) {
                String text = getPlainText(children.get(i));
                if (text == null || text.isEmpty()) {
                    continue;
                }
                writer.write(indent + "    " + jsonString(text));
                if (i < children.size() - 1) {
                    writer.write(",");
                }
                writer.newLine();
            }
            writer.write(indent + "]");
        } else {
            writer.write("{");
            writer.newLine();
            String childIndent = indent + "    ";

            int totalItems = 0;
            List<String> metaLines = new ArrayList<>();
            collectMetadataEntries(metaLines, node, childIndent);
            List<MindMapNode> validChildren = new ArrayList<>();
            for (MindMapNode child : children) {
                String text = getPlainText(child);
                if (text != null && !text.isEmpty()) {
                    validChildren.add(child);
                }
            }
            totalItems = metaLines.size() + validChildren.size();
            int written = 0;

            for (String metaLine : metaLines) {
                writer.write(metaLine);
                written++;
                if (written < totalItems) {
                    writer.write(",");
                }
                writer.newLine();
            }

            for (MindMapNode child : validChildren) {
                String text = getPlainText(child);
                writer.write(childIndent + jsonString(text) + ": ");
                writeNodeValue(writer, child, childIndent);
                written++;
                if (written < totalItems) {
                    writer.write(",");
                }
                writer.newLine();
            }
            writer.write(indent + "}");
        }
    }

    private void collectMetadataEntries(List<String> entries,
            MindMapNode node, String indent) {
        List<MindIcon> icons = node.getIcons();
        if (icons != null && !icons.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            sb.append(indent).append("\"_icons\": [");
            for (int i = 0; i < icons.size(); i++) {
                if (i > 0) {
                    sb.append(", ");
                }
                sb.append(jsonString(icons.get(i).getName()));
            }
            sb.append("]");
            entries.add(sb.toString());
        }

        List<Attribute> attrs = node.getAttributes();
        if (attrs != null && !attrs.isEmpty()) {
            for (Attribute attr : attrs) {
                entries.add(indent + jsonString("_attr:" + attr.getName())
                        + ": " + jsonString(attr.getValue()));
            }
        }

        String note = node.getNoteText();
        if (note != null && !note.isEmpty()) {
            String plainNote = stripHtml(note);
            if (!plainNote.isEmpty()) {
                entries.add(indent + "\"_note\": " + jsonString(plainNote));
            }
        }

        String link = node.getLink();
        if (link != null && !link.isEmpty()) {
            entries.add(indent + "\"_link\": " + jsonString(link));
        }

        if (linkRegistry != null) {
            Vector<MindMapLink> links = linkRegistry.getAllLinksFromMe(node);
            if (links != null && !links.isEmpty()) {
                List<String> refs = new ArrayList<>();
                for (MindMapLink ml : links) {
                    if (ml instanceof MindMapArrowLink) {
                        MindMapNode target = ml.getTarget();
                        if (target != null) {
                            String targetText = getPlainText(target);
                            if (targetText != null && !targetText.isEmpty()) {
                                refs.add(jsonString(targetText));
                            }
                        }
                    }
                }
                if (!refs.isEmpty()) {
                    entries.add(indent + "\"_references\": ["
                            + String.join(", ", refs) + "]");
                }
            }
        }

        List<String> styles = new ArrayList<>();
        if (node.isBold()) {
            styles.add("\"bold\"");
        }
        if (node.isItalic()) {
            styles.add("\"italic\"");
        }
        if (node.isUnderlined()) {
            styles.add("\"underlined\"");
        }
        if (node.isStrikethrough()) {
            styles.add("\"strikethrough\"");
        }
        if (!styles.isEmpty()) {
            entries.add(indent + "\"_emphasis\": ["
                    + String.join(", ", styles) + "]");
        }

        if (node.getCloud() != null) {
            entries.add(indent + "\"_grouped\": true");
        }
    }

    private boolean hasRichContent(MindMapNode node) {
        if (node.getNoteText() != null && !node.getNoteText().isEmpty()) {
            return true;
        }
        if (node.getIcons() != null && !node.getIcons().isEmpty()) {
            return true;
        }
        if (node.getAttributes() != null && !node.getAttributes().isEmpty()) {
            return true;
        }
        if (node.getLink() != null && !node.getLink().isEmpty()) {
            return true;
        }
        if (linkRegistry != null) {
            Vector<MindMapLink> links = linkRegistry.getAllLinksFromMe(node);
            if (links != null && !links.isEmpty()) {
                return true;
            }
        }
        if (node.getCloud() != null) {
            return true;
        }
        return false;
    }

    private List<MindMapNode> getChildList(MindMapNode node) {
        List<MindMapNode> children = new ArrayList<>();
        for (ListIterator<MindMapNode> it = node.childrenUnfolded();
                it.hasNext();) {
            children.add(it.next());
        }
        return children;
    }

    private String getPlainText(MindMapNode node) {
        String text = node.getText();
        if (text == null) {
            return null;
        }
        if (text.startsWith("<html>") || text.startsWith("<HTML>")) {
            text = text.replaceAll("<[^>]+>", "");
            text = text.replace("&amp;", "&").replace("&lt;", "<")
                    .replace("&gt;", ">").replace("&quot;", "\"")
                    .replace("&nbsp;", " ");
        }
        return text.trim();
    }

    private String stripHtml(String text) {
        if (text == null) {
            return "";
        }
        String result = text.replaceAll("<[^>]+>", "");
        result = result.replace("&amp;", "&").replace("&lt;", "<")
                .replace("&gt;", ">").replace("&quot;", "\"")
                .replace("&nbsp;", " ");
        result = resolveNumericEntities(result);
        return result.trim();
    }

    private String resolveNumericEntities(String text) {
        StringBuilder sb = new StringBuilder();
        int i = 0;
        while (i < text.length()) {
            if (text.charAt(i) == '&' && i + 2 < text.length()
                    && text.charAt(i + 1) == '#') {
                int semi = text.indexOf(';', i + 2);
                if (semi > 0 && semi - i < 10) {
                    String num = text.substring(i + 2, semi);
                    try {
                        int code = num.startsWith("x")
                                ? Integer.parseInt(num.substring(1), 16)
                                : Integer.parseInt(num);
                        sb.append((char) code);
                        i = semi + 1;
                        continue;
                    } catch (NumberFormatException e) {
                        // not a valid entity, fall through
                    }
                }
            }
            sb.append(text.charAt(i));
            i++;
        }
        return sb.toString();
    }

    private String jsonString(String text) {
        if (text == null) {
            return "null";
        }
        StringBuilder sb = new StringBuilder("\"");
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            switch (c) {
                case '"':
                    sb.append("\\\"");
                    break;
                case '\\':
                    sb.append("\\\\");
                    break;
                case '\n':
                    sb.append("\\n");
                    break;
                case '\r':
                    sb.append("\\r");
                    break;
                case '\t':
                    sb.append("\\t");
                    break;
                default:
                    if (c < 0x20) {
                        sb.append(String.format("\\u%04x", (int) c));
                    } else {
                        sb.append(c);
                    }
            }
        }
        sb.append("\"");
        return sb.toString();
    }
}
