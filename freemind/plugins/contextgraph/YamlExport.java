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

public class YamlExport extends ExportHook {

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

			try (BufferedWriter writer = new BufferedWriter(
					new OutputStreamWriter(new FileOutputStream(chosenFile),
							StandardCharsets.UTF_8))) {
				writer.write("version: \"1.0\"");
				writer.newLine();
				writer.write("generator: \"FreeMind CE\"");
				writer.newLine();
				writer.write("root:");
				writer.newLine();
				writeNode(writer, root, "  ");
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

	private void writeNode(BufferedWriter writer, MindMapNode node,
			String indent) throws Exception {
		String text = getPlainText(node);
		String nodeId = node.getObjectId(getController());
		String link = node.getLink();
		String noteText = node.getNoteText();
		List<MindIcon> icons = node.getIcons();
		List<MindMapNode> children = node.getChildren();

		writer.write(indent + "text: " + yamlString(text));
		writer.newLine();

		if (nodeId != null) {
			writer.write(indent + "id: " + yamlString(nodeId));
			writer.newLine();
		}

		if (node.isFolded()) {
			writer.write(indent + "folded: true");
			writer.newLine();
		}

		if (link != null && !link.trim().isEmpty()) {
			writer.write(indent + "link: " + yamlString(link));
			writer.newLine();
		}

		if (noteText != null && !noteText.trim().isEmpty()) {
			writer.write(indent + "note: " + yamlString(noteText));
			writer.newLine();
		}

		String rawText = node.getText();
		if (rawText != null && (rawText.startsWith("<html>") || rawText.startsWith("<HTML>"))) {
			writer.write(indent + "richContent: " + yamlString(rawText));
			writer.newLine();
		}

		if (icons != null && !icons.isEmpty()) {
			writer.write(indent + "icons:");
			writer.newLine();
			for (MindIcon icon : icons) {
				writer.write(indent + "  - " + yamlString(icon.getName()));
				writer.newLine();
			}
		}

		if (node.hasStyle()) {
			writer.write(indent + "style: " + yamlString(node.getStyle()));
			writer.newLine();
		}

		if (node.isBold()) {
			writer.write(indent + "bold: true");
			writer.newLine();
		}
		if (node.isItalic()) {
			writer.write(indent + "italic: true");
			writer.newLine();
		}

		if (node.getColor() != null) {
			writer.write(indent + "color: " + yamlString(Tools.colorToXml(node.getColor())));
			writer.newLine();
		}
		if (node.getBackgroundColor() != null) {
			writer.write(indent + "backgroundColor: " + yamlString(Tools.colorToXml(node.getBackgroundColor())));
			writer.newLine();
		}

		MindMapEdge edge = node.getEdge();
		if (edge != null && edge.hasStyle()) {
			writer.write(indent + "edge:");
			writer.newLine();
			if (edge.getColor() != null) {
				writer.write(indent + "  color: " + yamlString(Tools.colorToXml(edge.getColor())));
				writer.newLine();
			}
			if (edge.getStyle() != null) {
				writer.write(indent + "  style: " + yamlString(edge.getStyle()));
				writer.newLine();
			}
		}

		MindMapCloud cloud = node.getCloud();
		if (cloud != null) {
			writer.write(indent + "cloud:");
			writer.newLine();
			if (cloud.getColor() != null) {
				writer.write(indent + "  color: " + yamlString(Tools.colorToXml(cloud.getColor())));
				writer.newLine();
			}
		}

		List<Attribute> attributes = node.getAttributes();
		if (attributes != null && !attributes.isEmpty()) {
			writer.write(indent + "attributes:");
			writer.newLine();
			for (Attribute attr : attributes) {
				writer.write(indent + "  " + yamlKey(attr.getName()) + ": " + yamlString(attr.getValue()));
				writer.newLine();
			}
		}

		String latexEquation = getLatexEquation(node);
		if (latexEquation != null) {
			writer.write(indent + "latex: " + yamlString(latexEquation));
			writer.newLine();
		}

		if (children != null && !children.isEmpty()) {
			writer.write(indent + "children:");
			writer.newLine();
			for (ListIterator<MindMapNode> it = node.childrenUnfolded(); it.hasNext();) {
				writer.write(indent + "  -");
				writer.newLine();
				writeNode(writer, it.next(), indent + "    ");
			}
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

	private static String yamlString(String value) {
		if (value == null) {
			return "null";
		}
		if (value.isEmpty()) {
			return "\"\"";
		}
		// Quote if contains special YAML characters
		if (needsQuoting(value)) {
			return "\"" + value.replace("\\", "\\\\")
					.replace("\"", "\\\"")
					.replace("\n", "\\n")
					.replace("\r", "\\r")
					.replace("\t", "\\t") + "\"";
		}
		return value;
	}

	private static boolean needsQuoting(String value) {
		if (value.isEmpty()) {
			return true;
		}
		char first = value.charAt(0);
		if (first == '#' || first == '&' || first == '*' || first == '!'
				|| first == '|' || first == '>' || first == '\''
				|| first == '"' || first == '{' || first == '['
				|| first == '%' || first == '@' || first == '`') {
			return true;
		}
		if ("true".equals(value) || "false".equals(value)
				|| "null".equals(value) || "yes".equals(value)
				|| "no".equals(value)) {
			return true;
		}
		for (int i = 0; i < value.length(); i++) {
			char c = value.charAt(i);
			if (c == ':' || c == '\n' || c == '\r' || c == '"'
					|| c == '\\' || c == '\t') {
				return true;
			}
		}
		return false;
	}

	private static String yamlKey(String key) {
		if (needsQuoting(key)) {
			return "\"" + key.replace("\\", "\\\\").replace("\"", "\\\"") + "\"";
		}
		return key;
	}
}
