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
 * Exports the mind map as a clean, human-readable XML document enriched with
 * notes, icons, attributes, links, and cross-references.
 * Parent node texts become tag names, leaf nodes become plain text content.
 * Designed for easy consumption by AI/LLM systems.
 */
public class CleanXmlExport extends ExportHook {

    private MindMapLinkRegistry linkRegistry;

    @Override
    public void startupMapHook() {
        super.startupMapHook();

        File chosenFile = chooseFile("xml",
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
                String tag = toTagName(rootText);
                writer.write("<" + tag + ">");
                writer.newLine();
                writeMetadata(writer, root, "    ");
                writeChildren(writer, root, "    ");
                writer.write("</" + tag + ">");
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

    private void writeChildren(BufferedWriter writer, MindMapNode parent,
            String indent) throws Exception {
        for (ListIterator<MindMapNode> it = parent.childrenUnfolded();
                it.hasNext();) {
            MindMapNode child = it.next();
            String text = getPlainText(child);
            if (text == null || text.isEmpty()) {
                continue;
            }

            boolean hasChildren = child.getChildren() != null
                    && !child.getChildren().isEmpty();
            boolean hasRichContent = hasRichContent(child);

            if (hasChildren || hasRichContent) {
                String tag = toTagName(text);
                writer.write(indent + "<" + tag + ">");
                writer.newLine();
                writeMetadata(writer, child, indent + "    ");
                writeChildren(writer, child, indent + "    ");
                writer.write(indent + "</" + tag + ">");
                writer.newLine();
            } else {
                writer.write(indent + escapeXml(text));
                writer.newLine();
            }
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
        if (hasArrowLinks(node)) {
            return true;
        }
        if (node.getCloud() != null) {
            return true;
        }
        return false;
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
        writer.write(indent + "[" + escapeXml(sb.toString()) + "]");
        writer.newLine();
    }

    private void writeAttributes(BufferedWriter writer, MindMapNode node,
            String indent) throws Exception {
        List<Attribute> attrs = node.getAttributes();
        if (attrs == null || attrs.isEmpty()) {
            return;
        }
        for (Attribute attr : attrs) {
            writer.write(indent + escapeXml(attr.getName()) + ": "
                    + escapeXml(attr.getValue()));
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
        String[] lines = plainNote.split("\n");
        for (String line : lines) {
            String trimmed = line.trim();
            if (!trimmed.isEmpty()) {
                writer.write(indent + "* " + escapeXml(trimmed));
                writer.newLine();
            }
        }
    }

    private void writeLink(BufferedWriter writer, MindMapNode node,
            String indent) throws Exception {
        String link = node.getLink();
        if (link == null || link.isEmpty()) {
            return;
        }
        writer.write(indent + "=> " + escapeXml(link));
        writer.newLine();
    }

    private boolean hasArrowLinks(MindMapNode node) {
        if (linkRegistry == null) {
            return false;
        }
        Vector<MindMapLink> links = linkRegistry.getAllLinksFromMe(node);
        return links != null && !links.isEmpty();
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
        for (MindMapLink link : links) {
            if (link instanceof MindMapArrowLink) {
                MindMapNode target = link.getTarget();
                if (target != null) {
                    String targetText = getPlainText(target);
                    if (targetText != null && !targetText.isEmpty()) {
                        writer.write(indent + "~> " + escapeXml(targetText));
                        writer.newLine();
                    }
                }
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
            writer.write(indent + "(" + String.join(", ", styles) + ")");
            writer.newLine();
        }
    }

    private void writeCloud(BufferedWriter writer, MindMapNode node,
            String indent) throws Exception {
        if (node.getCloud() != null) {
            writer.write(indent + "{grouped}");
            writer.newLine();
        }
    }

    private String toTagName(String text) {
        if (text == null || text.isEmpty()) {
            return "node";
        }
        // Preserve original text including spaces for maximum readability.
        // This produces non-standard XML but is intended for AI/LLM consumption.
        return text.replace("&", "&amp;").replace("<", "").replace(">", "");
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

    private String escapeXml(String text) {
        if (text == null) {
            return "";
        }
        return text.replace("&", "&amp;").replace("<", "&lt;")
                .replace(">", "&gt;");
    }
}
