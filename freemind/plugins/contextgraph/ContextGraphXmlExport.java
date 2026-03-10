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
import java.time.LocalDate;
import java.util.List;
import java.util.ListIterator;

import javax.swing.JOptionPane;

import freemind.extensions.ExportHook;
import freemind.main.Tools;
import freemind.modes.MindIcon;
import freemind.modes.MindIconEmoji;
import freemind.modes.MindMapNode;

public class ContextGraphXmlExport extends ExportHook {

	private int totalNodes;
	private int maxDepth;

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

			// Pre-scan for metadata
			totalNodes = 0;
			maxDepth = 0;
			countNodes(root, 0);

			String rootText = getPlainText(root);
			if (rootText == null) {
				rootText = "";
			}

			try (BufferedWriter writer = new BufferedWriter(
					new OutputStreamWriter(new FileOutputStream(chosenFile),
							StandardCharsets.UTF_8))) {
				writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
				writer.newLine();
				writer.write("<context-graph title=\""
						+ escapeXml(rootText) + "\" generated=\""
						+ LocalDate.now() + "\" total-nodes=\"" + totalNodes
						+ "\" max-depth=\"" + maxDepth
						+ "\" generator=\"FreeMind CE 1.1.0\">");
				writer.newLine();
				writeNode(writer, root, 0, "  ");
				writer.write("</context-graph>");
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
					e.getLocalizedMessage(), "Export Error",
					JOptionPane.ERROR_MESSAGE);
		} finally {
			getController().getFrame().setWaitingCursor(false);
		}
	}

	private void countNodes(MindMapNode node, int depth) {
		totalNodes++;
		if (depth > maxDepth) {
			maxDepth = depth;
		}
		for (ListIterator<MindMapNode> it = node.childrenUnfolded(); it
				.hasNext();) {
			countNodes(it.next(), depth + 1);
		}
	}

	private void writeNode(BufferedWriter writer, MindMapNode node, int level,
			String indent) throws Exception {
		String text = getPlainText(node);
		String nodeId = node.getObjectId(getController());

		StringBuilder attrs = new StringBuilder();
		if (nodeId != null) {
			attrs.append(" id=\"").append(escapeXml(nodeId)).append("\"");
		}
		attrs.append(" level=\"").append(level).append("\"");
		attrs.append(" text=\"").append(escapeXml(text != null ? text : ""))
				.append("\"");

		// Icons
		String icons = getIconString(node);
		if (!icons.isEmpty()) {
			attrs.append(" icons=\"").append(escapeXml(icons)).append("\"");
		}

		// Style attributes
		if (node.isBold()) {
			attrs.append(" style=\"bold\"");
		} else if (node.isItalic()) {
			attrs.append(" style=\"italic\"");
		}

		String note = node.getNoteText();
		String link = node.getLink();
		boolean hasChildren = node.getChildren() != null
				&& !node.getChildren().isEmpty();
		boolean hasNote = note != null && !note.trim().isEmpty();
		boolean hasLink = link != null && !link.trim().isEmpty();

		if (!hasChildren && !hasNote && !hasLink) {
			writer.write(indent + "<node" + attrs + "/>");
			writer.newLine();
			return;
		}

		writer.write(indent + "<node" + attrs + ">");
		writer.newLine();

		// Note
		if (hasNote) {
			writer.write(indent + "  <note><![CDATA[" + note + "]]></note>");
			writer.newLine();
		}

		// Link
		if (hasLink) {
			writer.write(indent + "  <link href=\"" + escapeXml(link) + "\"/>");
			writer.newLine();
		}

		// Children
		for (ListIterator<MindMapNode> it = node.childrenUnfolded(); it
				.hasNext();) {
			writeNode(writer, it.next(), level + 1, indent + "  ");
		}

		writer.write(indent + "</node>");
		writer.newLine();
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

	private String getIconString(MindMapNode node) {
		List<MindIcon> icons = node.getIcons();
		if (icons == null || icons.isEmpty()) {
			return "";
		}
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < icons.size(); i++) {
			if (i > 0) {
				sb.append(' ');
			}
			sb.append(MindIconEmoji.getEmoji(icons.get(i).getName()));
		}
		return sb.toString();
	}

	private String escapeXml(String text) {
		if (text == null) {
			return "";
		}
		return text.replace("&", "&amp;").replace("<", "&lt;")
				.replace(">", "&gt;").replace("\"", "&quot;")
				.replace("'", "&apos;");
	}
}
