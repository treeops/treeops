package org.treeops.ui.util;

import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.Rectangle;
import java.awt.Window;
import java.util.List;
import java.util.function.Function;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GuiUtils {
	private static final Logger LOG = LoggerFactory.getLogger(GuiUtils.class);

	public static JMenuItem menuItem(JMenu menu, String name, Runnable al) {
		JMenuItem menuItem = new JMenuItem(name);
		menuItem.addActionListener(e -> al.run());
		menu.add(menuItem);
		return menuItem;
	}

	public static void handleError(Window frame, Exception ex) {
		LOG.error("Error " + ex, ex);
		JOptionPane.showMessageDialog(frame, "Error " + ex);
	}

	public static void selectAndScrollToRow(int idx, JTable table) {
		table.scrollRectToVisible(new Rectangle(table.getCellRect(idx, 0, true)));
		table.getSelectionModel().setSelectionInterval(idx, idx);
	}

	public static void setSystemLookAndFeel() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void expandAllNodes(JTree tree) {
		int j = tree.getRowCount();
		int i = 0;
		while (i < j) {
			tree.expandRow(i);
			i += 1;
			j = tree.getRowCount();
		}
	}

	public static void addLabelTextRows(JLabel[] labels, JComponent[] fields, Container container) {
		GridBagConstraints c = new GridBagConstraints();
		c.anchor = GridBagConstraints.EAST;
		int numLabels = labels.length;

		for (int i = 0; i < numLabels; i++) {
			c.gridwidth = GridBagConstraints.RELATIVE;
			c.fill = GridBagConstraints.NONE;
			c.weightx = 0.0;
			container.add(labels[i], c);

			c.gridwidth = GridBagConstraints.REMAINDER;
			c.fill = GridBagConstraints.HORIZONTAL;
			c.weightx = 1.0;
			container.add(fields[i], c);
		}
	}

	public static void align(JTable table, int align, int column) {
		DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
		renderer.setHorizontalAlignment(align);
		table.getColumnModel().getColumn(column).setCellRenderer(renderer);
	}

	public static void widthMinAndPreferred(int idx, int preferredWidth, JTable table) {
		TableColumn c = table.getColumnModel().getColumn(idx);
		setMinAndPreferredWidth(preferredWidth, c);
	}

	public static void width(int idx, int preferredWidth, JTable table) {
		TableColumn c = table.getColumnModel().getColumn(idx);
		setWidth(preferredWidth, c);
	}

	public static void setMinAndPreferredWidth(int preferredWidth, TableColumn c) {
		c.setMinWidth(preferredWidth);
		c.setPreferredWidth(preferredWidth);
	}

	public static void setWidth(int preferredWidth, TableColumn c) {
		setMinAndPreferredWidth(preferredWidth, c);
		c.setMaxWidth(preferredWidth);
	}

	public static <T> T ask(JFrame frame, String defaultValue, String title, Function<String, T> function) {
		String input = JOptionPane.showInputDialog(frame, title, defaultValue);
		if ((input != null) && (input.length() > 0)) {
			return function.apply(input);
		}
		return null;
	}

	public static <T> T select(JFrame frame, List<String> choiceList, String message, String title, Function<String, T> function) {
		String[] choices = choiceList.toArray(new String[]{});
		String input = (String) JOptionPane.showInputDialog(frame, message, title, JOptionPane.QUESTION_MESSAGE, null, choices, choices.length > 0 ? choices[0] : null);
		if ((input != null) && (input.length() > 0)) {
			return function.apply(input);
		}
		return null;
	}

}
