package org.treeops.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.EnumMap;
import java.util.List;
import java.util.function.Supplier;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import org.treeops.DataNode;
import org.treeops.csv.CsvWriter;
import org.treeops.formats.Format;
import org.treeops.transform.Transformation;
import org.treeops.ui.util.GuiUtils;

public class OutputPanel extends JPanel {
	private Supplier<DataNode> dataSupplier;
	private Supplier<List<Transformation>> transformationsSupplier;

	private TitledBorder border = BorderFactory.createTitledBorder("Output");
	private EnumMap<Format, JTextArea> format2TextArea = new EnumMap<>(Format.class);

	private JTabbedPane tabbedPanel = new JTabbedPane();
	private JButton refreshButton = new JButton("Refresh");
	private JCheckBox synchronizeCheckBox = new JCheckBox("Synchronize");
	private JTree tree = new JTree();

	private TableModel tableModel = new TableModel();
	private JTable table = new JTable(tableModel);

	public OutputPanel(Supplier<DataNode> dataSupplier, Supplier<List<Transformation>> transformationsSupplier) {
		super();
		this.dataSupplier = dataSupplier;
		this.transformationsSupplier = transformationsSupplier;
		setBorder(border);
		setLayout(new BorderLayout());

		for (Format format : Format.values()) {
			JTextArea ta = new JTextArea();
			format2TextArea.put(format, ta);
			tabbedPanel.add(new JScrollPane(ta), format.name());
		}
		tabbedPanel.add(new JScrollPane(table), "Table");
		tabbedPanel.add(new JScrollPane(tree), "Tree");

		JPanel buttonPanel = new JPanel(new FlowLayout());
		refreshButton.addActionListener(e -> refresh());
		buttonPanel.add(refreshButton);

		buttonPanel.add(synchronizeCheckBox);
		add(tabbedPanel, BorderLayout.CENTER);
		add(buttonPanel, BorderLayout.NORTH);
		needRefresh();

		synchronizeCheckBox.addActionListener(e -> synchronizedChanged());

		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
	}

	public void setSynchronized(boolean syncrhonized) {
		synchronizeCheckBox.setSelected(syncrhonized);
	}

	public void synchronizedChanged() {
		if (synchronizeCheckBox.isSelected()) {
			refresh();
		}
	}

	public void refresh() {
		DataNode dataRoot = dataSupplier.get();
		List<Transformation> transformations = transformationsSupplier.get();
		try {
			for (Format format : Format.values()) {
				JTextArea ta = format2TextArea.get(format);
				ta.setText(format.getProcessor().write(dataRoot, transformations));
			}

			tree.setModel(new DefaultTreeModel(dataNode(dataRoot)));
			GuiUtils.expandAllNodes(tree);

			tableModel.setTable(CsvWriter.table(dataRoot), table);
			upToDate();

		} catch (Exception ex) {
			GuiUtils.handleError(SwingUtilities.getWindowAncestor(this), ex);
		}

	}

	private static DefaultMutableTreeNode dataNode(DataNode n) {
		if (n.getData().isValueHolder() && (n.getChildren().size() == 1)) {

			return new DefaultMutableTreeNode(n.getName() + ": " + n.getSingleChild().getName());
		}

		DefaultMutableTreeNode guiNode = new DefaultMutableTreeNode(n.getName());
		for (DataNode c : DataNode.children(n)) {
			guiNode.add(dataNode(c));
		}
		return guiNode;
	}

	public void upToDate() {
		refreshButton.setEnabled(false);
	}

	public void needRefresh() {
		if (synchronizeCheckBox.isSelected()) {
			refresh();
		} else {
			refreshButton.setEnabled(true);
		}
	}

}
