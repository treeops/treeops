package org.treeops.ui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;

import org.treeops.SchemaNode;
import org.treeops.compare.ComparisonResult;
import org.treeops.formats.FileInput;
import org.treeops.formats.Format;
import org.treeops.ui.util.GuiUtils;

public class ComparePanel extends JPanel {
	private JLabel inputLabel = new JLabel();
	private JLabel resultLabel = new JLabel(" ");

	private CardLayout resultMarksCardLayout = new CardLayout();
	private JPanel resultMarks = new JPanel(resultMarksCardLayout);
	private static final String SAME = "SAME";
	private static final String DIFF = "DIFF";
	private static final String OTHER = "OTHER";

	private CompareTableModel compareTableModel = new CompareTableModel();
	private JTable table = new JTable(compareTableModel);
	private Consumer<Format> compareToListener;
	private Consumer<List<String>> ignoreListener;

	public ComparePanel() {
		setLayout(new BorderLayout());

		JPanel buttons = new JPanel(new FlowLayout());
		JPanel labels = new JPanel(new FlowLayout());

		JPanel labelsAndButtons = new JPanel();
		labelsAndButtons.setLayout(new BoxLayout(labelsAndButtons, BoxLayout.PAGE_AXIS));
		labelsAndButtons.add(buttons);
		labelsAndButtons.add(labels);

		add(labelsAndButtons, BorderLayout.NORTH);
		add(new JScrollPane(table), BorderLayout.CENTER);

		labels.add(inputLabel);
		labels.add(resultLabel);
		labels.add(resultMarks);

		for (Format format : Format.values()) {
			JButton button = new JButton("Compare with " + format.name() + "...");
			button.addActionListener(e -> compareToListener.accept(format));
			buttons.add(button);
		}
		initContextMenu();
		initIcons();
		compareTableModel.setWidths(table);
	}

	private void initIcons() {
		try {
			resultMarks.add(new JLabel(new ImageIcon(ImageIO.read(ComparePanel.class.getResourceAsStream("/icons/ic_done_black_24dp.png")))), SAME);
			resultMarks.add(new JLabel(new ImageIcon(ImageIO.read(ComparePanel.class.getResourceAsStream("/icons/ic_highlight_off_black_24dp.png")))), DIFF);
			resultMarks.add(new JLabel(), OTHER);
		} catch (Exception ex) {
			GuiUtils.handleError(SwingUtilities.getWindowAncestor(this), ex);
		}
	}

	private void initContextMenu() {
		GuiUtils.selectRowOnClick(table);

		JPopupMenu popupMenu = new JPopupMenu();
		popupMenu.add(GuiUtils.menuItem("Ignore", () -> ignore()));

		table.setComponentPopupMenu(popupMenu);
	}

	private void ignore() {
		int selected = table.getSelectedRow();
		if (selected > -1) {
			ComparisonResult r = compareTableModel.getList().get(selected);
			ignoreListener.accept(r.getPath());
		}
	}

	public void setCompareToListener(Consumer<Format> compareToListener) {
		this.compareToListener = compareToListener;
	}

	public void setIgnoreListener(Consumer<List<String>> ignoreListener) {
		this.ignoreListener = ignoreListener;
	}

	public void update(FileInput fileInput, List<ComparisonResult> list, SchemaNode schema) {
		inputLabel.setText("" + fileInput.getFormat() + " " + fileInput.description());
		compareTableModel.setList(list, schema);

		ComparisonResult main = list.get(0);
		resultLabel.setText(resultText(main));

		resultMarksCardLayout.show(resultMarks, main.getData().isSameNodeAndChildren() ? SAME : DIFF);

	}

	private String resultText(ComparisonResult main) {
		if (main.getData().isSameNodeAndChildren()) {
			return main.ignored() ? "Same" : "Identical";
		}
		return "Different";
	}

	public void cleanComparison() {
		inputLabel.setText("");
		resultLabel.setText("");

		compareTableModel.setList(new ArrayList<>(), null);
		resultMarksCardLayout.show(resultMarks, OTHER);

	}
}
