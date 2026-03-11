package plugins.latex;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.LineBorder;

import org.scilab.forge.jlatexmath.TeXConstants;
import org.scilab.forge.jlatexmath.TeXFormula;
import org.scilab.forge.jlatexmath.TeXIcon;

public class JZoomedHotEqn extends JComponent {
	private static double zoom = 1.0;
	private static String editorTitle = null;
	private LatexNodeHook model;
	private String equation;
	private TeXIcon texIcon;

	JZoomedHotEqn(LatexNodeHook model) {
		this.model = model;
		this.equation = model.getContent(null);
		setBorder(new LineBorder(Color.GRAY, 1));
		setOpaque(false);
		rebuildIcon();

		if (editorTitle == null) {
			editorTitle = model.getMindMapController().getText(
					"plugins/latex/LatexNodeHook.editorTitle");
		}

		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON1) {
					edit();
					e.consume();
				}
			}
		});
	}

	private void rebuildIcon() {
		try {
			TeXFormula formula = new TeXFormula(equation);
			texIcon = formula.createTeXIcon(TeXConstants.STYLE_DISPLAY, (float) (16 * zoom));
			texIcon.setInsets(new Insets(2, 2, 2, 2));
		} catch (Exception e) {
			try {
				TeXFormula errorFormula = new TeXFormula("\\text{" + escapeLatex(e.getMessage()) + "}");
				texIcon = errorFormula.createTeXIcon(TeXConstants.STYLE_DISPLAY, (float) (12 * zoom));
				texIcon.setInsets(new Insets(2, 2, 2, 2));
			} catch (Exception e2) {
				texIcon = null;
			}
		}
	}

	private static String escapeLatex(String text) {
		if (text == null) return "error";
		return text.replace("\\", "\\textbackslash ")
				.replace("{", "\\{")
				.replace("}", "\\}")
				.replace("_", "\\_")
				.replace("^", "\\^{}")
				.replace("#", "\\#")
				.replace("&", "\\&")
				.replace("%", "\\%")
				.replace("$", "\\$");
	}

	@Override
	public Dimension getPreferredSize() {
		if (texIcon != null) {
			return new Dimension(texIcon.getIconWidth(), texIcon.getIconHeight());
		}
		return new Dimension(50, 20);
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (texIcon != null) {
			Graphics2D g2 = (Graphics2D) g;
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
			texIcon.paintIcon(this, g2, 0, 0);
		}
	}

	private void edit() {
		JTextArea textArea = new JTextArea(equation);
		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);
		final JScrollPane editorScrollPane = new JScrollPane(textArea);
		editorScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		editorScrollPane.setPreferredSize(new Dimension(500, 160));
		JDialog edit = new JDialog(JOptionPane.getFrameForComponent(this),
				editorTitle, true);
		edit.getContentPane().add(editorScrollPane);
		edit.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		edit.pack();
		edit.setLocationRelativeTo(this);
		edit.setVisible(true);
		String eq = textArea.getText();
		model.setContent(null, eq);
	}

	public void setModel(LatexNodeHook model) {
		this.model = model;
		this.equation = model.getContent(null);
		rebuildIcon();
		revalidate();
		repaint();
	}

	public void setEquation(String equation) {
		this.equation = equation;
		rebuildIcon();
		revalidate();
		repaint();
	}

	public String getEquation() {
		return equation;
	}

	static void setZoom(double newZoom) {
		zoom = newZoom;
	}

	static double getZoom() {
		return zoom;
	}
}
