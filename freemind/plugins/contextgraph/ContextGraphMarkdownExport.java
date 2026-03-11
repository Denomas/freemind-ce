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
import java.util.List;
import java.util.ListIterator;

import javax.swing.JOptionPane;

import freemind.extensions.ExportHook;
import freemind.extensions.PermanentNodeHook;
import freemind.main.Tools;
import freemind.modes.MindIcon;
import freemind.modes.MindIconEmoji;
import freemind.modes.MindMapNode;
import plugins.latex.LatexNodeHook;

public class ContextGraphMarkdownExport extends ExportHook {

	@Override
	public void startupMapHook() {
		super.startupMapHook();

		File chosenFile = chooseFile("md",
				getTranslatableResourceString("file_description"), null);
		if (chosenFile == null) {
			return;
		}

		try {
			getController().getFrame().setWaitingCursor(true);

			MindMapNode root = getController().getMap().getRootNode();

			try (BufferedWriter writer = new BufferedWriter(
					new OutputStreamWriter(new FileOutputStream(chosenFile),
							StandardCharsets.UTF_8))) {
				writeNode(writer, root, 0);
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

	private void writeNode(BufferedWriter writer, MindMapNode node, int level)
			throws Exception {
		String text = getPlainText(node);
		if (text == null || text.trim().isEmpty()) {
			// Still traverse children even if this node is empty
			writeChildren(writer, node, level);
			return;
		}

		String iconPrefix = getIconPrefix(node);
		String linkSuffix = getLinkSuffix(node);

		if (level <= 5) {
			// Heading levels: # through ######
			String hashes = "#".repeat(level + 1);
			writer.write(hashes + " " + iconPrefix + text + linkSuffix);
		} else {
			// Bulleted list with indentation for deep nodes
			int indent = (level - 6) * 2;
			String spaces = " ".repeat(indent);
			writer.write(spaces + "- " + iconPrefix + text + linkSuffix);
		}
		writer.newLine();

		// Write note as blockquote
		String note = node.getNoteText();
		if (note != null && !note.trim().isEmpty()) {
			String plainNote = stripHtml(note);
			if (!plainNote.trim().isEmpty()) {
				for (String line : plainNote.split("\\r?\\n")) {
					writer.write("> " + line);
					writer.newLine();
				}
			}
		}

		// Write LaTeX equations
		String latexEquation = getLatexEquation(node);
		if (latexEquation != null) {
			writer.write("$$" + latexEquation + "$$");
			writer.newLine();
		}

		writer.newLine();
		writeChildren(writer, node, level);
	}

	private void writeChildren(BufferedWriter writer, MindMapNode node,
			int level) throws Exception {
		for (ListIterator<MindMapNode> it = node.childrenUnfolded(); it
				.hasNext();) {
			writeNode(writer, it.next(), level + 1);
		}
	}

	private String getLatexEquation(MindMapNode node) {
		for (PermanentNodeHook hook : node.getActivatedHooks()) {
			if (hook instanceof LatexNodeHook) {
				return ((LatexNodeHook) hook).getContent(null);
			}
		}
		return null;
	}

	private String getPlainText(MindMapNode node) {
		String text = node.getText();
		if (text == null) {
			return null;
		}
		// Strip HTML if present
		if (text.startsWith("<html>") || text.startsWith("<HTML>")) {
			text = stripHtml(text);
		}
		// Convert HTML bold/italic to Markdown
		text = text.replace("<b>", "**").replace("</b>", "**");
		text = text.replace("<B>", "**").replace("</B>", "**");
		text = text.replace("<i>", "*").replace("</i>", "*");
		text = text.replace("<I>", "*").replace("</I>", "*");
		return text.trim();
	}

	private String stripHtml(String html) {
		if (html == null) {
			return "";
		}
		// Remove HTML tags but preserve bold/italic markers first
		String result = html;
		result = result.replace("<b>", "**").replace("</b>", "**");
		result = result.replace("<B>", "**").replace("</B>", "**");
		result = result.replace("<i>", "*").replace("</i>", "*");
		result = result.replace("<I>", "*").replace("</I>", "*");
		result = result.replace("<br>", "\n").replace("<br/>", "\n")
				.replace("<BR>", "\n");
		result = result.replace("<p>", "\n").replace("</p>", "")
				.replace("<P>", "\n").replace("</P>", "");
		result = result.replaceAll("<[^>]+>", "");
		result = result.replace("&amp;", "&").replace("&lt;", "<")
				.replace("&gt;", ">").replace("&quot;", "\"")
				.replace("&nbsp;", " ");
		return result.trim();
	}

	private String getIconPrefix(MindMapNode node) {
		List<MindIcon> icons = node.getIcons();
		if (icons == null || icons.isEmpty()) {
			return "";
		}
		StringBuilder sb = new StringBuilder();
		for (MindIcon icon : icons) {
			sb.append(MindIconEmoji.getEmoji(icon.getName())).append(' ');
		}
		return sb.toString();
	}

	private String getLinkSuffix(MindMapNode node) {
		String link = node.getLink();
		if (link == null || link.trim().isEmpty()) {
			return "";
		}
		return " [link](" + link + ")";
	}
}
