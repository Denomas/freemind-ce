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
import freemind.modes.MindMapCloud;
import freemind.modes.MindMapEdge;
import freemind.modes.MindMapNode;
import freemind.modes.attributes.Attribute;
import plugins.latex.LatexNodeHook;

public class JsonExport extends ExportHook {

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

			try (BufferedWriter writer = new BufferedWriter(
					new OutputStreamWriter(new FileOutputStream(chosenFile),
							StandardCharsets.UTF_8))) {
				writer.write("{");
				writer.newLine();
				writer.write("  \"version\": \"1.0\",");
				writer.newLine();
				writer.write("  \"generator\": \"FreeMind CE\",");
				writer.newLine();
				writer.write("  \"root\": ");
				writeNode(writer, root, "  ");
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
					e.getLocalizedMessage(), "Export Error",
					JOptionPane.ERROR_MESSAGE);
		} finally {
			getController().getFrame().setWaitingCursor(false);
		}
	}

	private void writeNode(BufferedWriter writer, MindMapNode node,
			String indent) throws Exception {
		String text = getPlainText(node);
		String nodeId = node.getObjectId(getController());
		String link = node.getLink();
		String noteText = node.getNoteText();
		List<MindIcon> icons = node.getIcons();
		List<MindMapNode> children = node.getChildren();

		writer.write("{");
		writer.newLine();

		// Text
		writer.write(indent + "  \"text\": " + jsonString(text));

		// ID
		if (nodeId != null) {
			writer.write(",");
			writer.newLine();
			writer.write(indent + "  \"id\": " + jsonString(nodeId));
		}

		// Folded
		if (node.isFolded()) {
			writer.write(",");
			writer.newLine();
			writer.write(indent + "  \"folded\": true");
		}

		// Link
		if (link != null && !link.trim().isEmpty()) {
			writer.write(",");
			writer.newLine();
			writer.write(indent + "  \"link\": " + jsonString(link));
		}

		// Note
		if (noteText != null && !noteText.trim().isEmpty()) {
			writer.write(",");
			writer.newLine();
			writer.write(indent + "  \"note\": " + jsonString(noteText));
		}

		// Rich content (original HTML)
		String rawText = node.getText();
		if (rawText != null && (rawText.startsWith("<html>") || rawText.startsWith("<HTML>"))) {
			writer.write(",");
			writer.newLine();
			writer.write(indent + "  \"richContent\": " + jsonString(rawText));
		}

		// Icons
		if (icons != null && !icons.isEmpty()) {
			writer.write(",");
			writer.newLine();
			writer.write(indent + "  \"icons\": [");
			for (int i = 0; i < icons.size(); i++) {
				if (i > 0) {
					writer.write(", ");
				}
				writer.write(jsonString(icons.get(i).getName()));
			}
			writer.write("]");
		}

		// Style
		if (node.hasStyle()) {
			writer.write(",");
			writer.newLine();
			writer.write(indent + "  \"style\": " + jsonString(node.getStyle()));
		}

		// Font styling
		if (node.isBold()) {
			writer.write(",");
			writer.newLine();
			writer.write(indent + "  \"bold\": true");
		}
		if (node.isItalic()) {
			writer.write(",");
			writer.newLine();
			writer.write(indent + "  \"italic\": true");
		}

		// Color
		if (node.getColor() != null) {
			writer.write(",");
			writer.newLine();
			writer.write(indent + "  \"color\": " + jsonString(Tools.colorToXml(node.getColor())));
		}
		if (node.getBackgroundColor() != null) {
			writer.write(",");
			writer.newLine();
			writer.write(indent + "  \"backgroundColor\": " + jsonString(Tools.colorToXml(node.getBackgroundColor())));
		}

		// Edge
		MindMapEdge edge = node.getEdge();
		if (edge != null && edge.hasStyle()) {
			writer.write(",");
			writer.newLine();
			writer.write(indent + "  \"edge\": {");
			if (edge.getColor() != null) {
				writer.write("\"color\": " + jsonString(Tools.colorToXml(edge.getColor())));
				if (edge.getStyle() != null) {
					writer.write(", ");
				}
			}
			if (edge.getStyle() != null) {
				writer.write("\"style\": " + jsonString(edge.getStyle()));
			}
			writer.write("}");
		}

		// Cloud
		MindMapCloud cloud = node.getCloud();
		if (cloud != null) {
			writer.write(",");
			writer.newLine();
			writer.write(indent + "  \"cloud\": {");
			if (cloud.getColor() != null) {
				writer.write("\"color\": " + jsonString(Tools.colorToXml(cloud.getColor())));
			}
			writer.write("}");
		}

		// Attributes
		List<Attribute> attributes = node.getAttributes();
		if (attributes != null && !attributes.isEmpty()) {
			writer.write(",");
			writer.newLine();
			writer.write(indent + "  \"attributes\": {");
			for (int i = 0; i < attributes.size(); i++) {
				if (i > 0) {
					writer.write(", ");
				}
				Attribute attr = attributes.get(i);
				writer.write(jsonString(attr.getName()) + ": " + jsonString(attr.getValue()));
			}
			writer.write("}");
		}

		// LaTeX equations (from hooks)
		String latexEquation = getLatexEquation(node);
		if (latexEquation != null) {
			writer.write(",");
			writer.newLine();
			writer.write(indent + "  \"latex\": " + jsonString(latexEquation));
		}

		// Children
		if (children != null && !children.isEmpty()) {
			writer.write(",");
			writer.newLine();
			writer.write(indent + "  \"children\": [");
			writer.newLine();
			boolean first = true;
			for (ListIterator<MindMapNode> it = node.childrenUnfolded(); it.hasNext();) {
				if (!first) {
					writer.write(",");
					writer.newLine();
				}
				writer.write(indent + "    ");
				writeNode(writer, it.next(), indent + "    ");
				first = false;
			}
			writer.newLine();
			writer.write(indent + "  ]");
		}

		writer.newLine();
		writer.write(indent + "}");
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
			return "";
		}
		if (text.startsWith("<html>") || text.startsWith("<HTML>")) {
			text = text.replaceAll("<[^>]+>", "");
			text = text.replace("&amp;", "&").replace("&lt;", "<")
					.replace("&gt;", ">").replace("&quot;", "\"")
					.replace("&nbsp;", " ");
		}
		return text.trim();
	}

	private static String jsonString(String value) {
		if (value == null) {
			return "null";
		}
		StringBuilder sb = new StringBuilder("\"");
		for (int i = 0; i < value.length(); i++) {
			char c = value.charAt(i);
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
			case '\b':
				sb.append("\\b");
				break;
			case '\f':
				sb.append("\\f");
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
