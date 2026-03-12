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
 * Exports the mind map as a clean YAML document enriched with notes, icons,
 * attributes, links, and cross-references. Parent nodes become keys and
 * leaf nodes become list items.
 * Designed for easy consumption by AI/LLM systems.
 */
public class CleanYamlExport extends ExportHook {

    private MindMapLinkRegistry linkRegistry;

    @Override
    public void startupMapHook() {
        super.startupMapHook();

        File chosenFile = chooseFile("yaml",
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
                writer.write(yamlKey(rootText) + ":");
                writer.newLine();
                writeMetadata(writer, root, "  ");
                writeChildren(writer, root, "  ");
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

    private void writeChildren(BufferedWriter writer, MindMapNode parent,
            String indent) throws Exception {
        List<MindMapNode> children = getChildList(parent);

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

        if (allLeaves) {
            for (MindMapNode child : children) {
                String text = getPlainText(child);
                if (text == null || text.isEmpty()) {
                    continue;
                }
                writer.write(indent + "- " + yamlValue(text));
                writer.newLine();
            }
        } else {
            for (MindMapNode child : children) {
                String text = getPlainText(child);
                if (text == null || text.isEmpty()) {
                    continue;
                }
                boolean hasChildren = child.getChildren() != null
                        && !child.getChildren().isEmpty();
                boolean hasRich = hasRichContent(child);

                if (hasChildren || hasRich) {
                    writer.write(indent + yamlKey(text) + ":");
                    writer.newLine();
                    writeMetadata(writer, child, indent + "  ");
                    writeChildren(writer, child, indent + "  ");
                } else {
                    writer.write(indent + "- " + yamlValue(text));
                    writer.newLine();
                }
            }
        }
    }

    private void writeMetadata(BufferedWriter writer, MindMapNode node,
            String indent) throws Exception {
        writeIcons(writer, node, indent);
        writeAttributes(writer, node, indent);
        writeNote(writer, node, indent);
        writeLink(writer, node, indent);
        writeArrowLinks(writer, node, indent);
        writeEmphasis(writer, node, indent);
        writeCloud(writer, node, indent);
    }

    private void writeIcons(BufferedWriter writer, MindMapNode node,
            String indent) throws Exception {
        List<MindIcon> icons = node.getIcons();
        if (icons == null || icons.isEmpty()) {
            return;
        }
        StringBuilder sb = new StringBuilder();
        for (MindIcon icon : icons) {
            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append(icon.getName());
        }
        writer.write(indent + "_icons: [" + sb.toString() + "]");
        writer.newLine();
    }

    private void writeAttributes(BufferedWriter writer, MindMapNode node,
            String indent) throws Exception {
        List<Attribute> attrs = node.getAttributes();
        if (attrs == null || attrs.isEmpty()) {
            return;
        }
        for (Attribute attr : attrs) {
            writer.write(indent + yamlKey("_attr:" + attr.getName()) + ": "
                    + yamlValue(attr.getValue()));
            writer.newLine();
        }
    }

    private void writeNote(BufferedWriter writer, MindMapNode node,
            String indent) throws Exception {
        String note = node.getNoteText();
        if (note == null || note.isEmpty()) {
            return;
        }
        String plainNote = stripHtml(note);
        if (plainNote.isEmpty()) {
            return;
        }
        String singleLine = plainNote.replace("\n", " ").replace("\r", "")
                .trim();
        writer.write(indent + "_note: " + yamlValue(singleLine));
        writer.newLine();
    }

    private void writeLink(BufferedWriter writer, MindMapNode node,
            String indent) throws Exception {
        String link = node.getLink();
        if (link == null || link.isEmpty()) {
            return;
        }
        writer.write(indent + "_link: " + yamlValue(link));
        writer.newLine();
    }

    private void writeArrowLinks(BufferedWriter writer, MindMapNode node,
            String indent) throws Exception {
        if (linkRegistry == null) {
            return;
        }
        Vector<MindMapLink> links = linkRegistry.getAllLinksFromMe(node);
        if (links == null || links.isEmpty()) {
            return;
        }
        List<String> refs = new ArrayList<>();
        for (MindMapLink link : links) {
            if (link instanceof MindMapArrowLink) {
                MindMapNode target = link.getTarget();
                if (target != null) {
                    String targetText = getPlainText(target);
                    if (targetText != null && !targetText.isEmpty()) {
                        refs.add(targetText);
                    }
                }
            }
        }
        if (!refs.isEmpty()) {
            writer.write(indent + "_references:");
            writer.newLine();
            for (String ref : refs) {
                writer.write(indent + "  - " + yamlValue(ref));
                writer.newLine();
            }
        }
    }

    private void writeEmphasis(BufferedWriter writer, MindMapNode node,
            String indent) throws Exception {
        List<String> styles = new ArrayList<>();
        if (node.isBold()) {
            styles.add("bold");
        }
        if (node.isItalic()) {
            styles.add("italic");
        }
        if (node.isUnderlined()) {
            styles.add("underlined");
        }
        if (node.isStrikethrough()) {
            styles.add("strikethrough");
        }
        if (!styles.isEmpty()) {
            writer.write(indent + "_emphasis: ["
                    + String.join(", ", styles) + "]");
            writer.newLine();
        }
    }

    private void writeCloud(BufferedWriter writer, MindMapNode node,
            String indent) throws Exception {
        if (node.getCloud() != null) {
            writer.write(indent + "_grouped: true");
            writer.newLine();
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

    private String yamlKey(String text) {
        if (needsQuoting(text)) {
            return "\"" + text.replace("\\", "\\\\").replace("\"", "\\\"")
                    + "\"";
        }
        return text;
    }

    private String yamlValue(String text) {
        if (needsQuoting(text)) {
            return "\"" + text.replace("\\", "\\\\").replace("\"", "\\\"")
                    + "\"";
        }
        return text;
    }

    private boolean needsQuoting(String text) {
        if (text.isEmpty()) {
            return true;
        }
        if (text.contains(": ") || text.contains("#") || text.contains("\"")
                || text.contains("'") || text.contains("\\")
                || text.startsWith("- ") || text.startsWith("* ")
                || text.startsWith("& ") || text.startsWith("! ")
                || text.startsWith("% ") || text.startsWith("@ ")
                || text.startsWith("{") || text.startsWith("[")) {
            return true;
        }
        String lower = text.toLowerCase();
        if (lower.equals("true") || lower.equals("false")
                || lower.equals("null") || lower.equals("yes")
                || lower.equals("no") || lower.equals("on")
                || lower.equals("off")) {
            return true;
        }
        try {
            Double.parseDouble(text);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
