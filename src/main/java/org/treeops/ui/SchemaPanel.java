package org.treeops.ui;

import static org.treeops.ui.util.GuiUtils.menuItem;

import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.function.Consumer;

import javax.swing.JMenu;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;

import org.treeops.SchemaNode;
import org.treeops.transform.Transformation;
import org.treeops.ui.transform.TransformationAction;
import org.treeops.ui.util.GuiUtils;

public class SchemaPanel extends JPanel {
	private SchemaTableModel tableModel = new SchemaTableModel();
	private JTable table = new JTable(tableModel);
	private final List<TransformationAction> transformationActions;
	private final List<TransformationAction> customizationActions;
	private String lastSearch = "";

	private Consumer<List<String>> showTypeListener;
	private Consumer<Transformation> transformationAddedListener;

	public SchemaPanel(List<TransformationAction> transformationActions, List<TransformationAction> customizationActions) {
		super();
		this.transformationActions = transformationActions;
		this.customizationActions = customizationActions;
		this.setLayout(new BorderLayout());
		this.add(new JScrollPane(table), BorderLayout.CENTER);

		GuiUtils.widthMinAndPreferred(0, 200, table);
		GuiUtils.width(1, 70, table);
		GuiUtils.width(2, 70, table);
		GuiUtils.width(3, 60, table);
		GuiUtils.width(4, 50, table);
		GuiUtils.widthMinAndPreferred(5, 200, table);

		initMenu();

		GuiUtils.align(table, SwingConstants.CENTER, 1);
		GuiUtils.align(table, SwingConstants.RIGHT, 2);
		GuiUtils.align(table, SwingConstants.RIGHT, 3);
		GuiUtils.align(table, SwingConstants.CENTER, 4);

		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent event) {
				int currentRow = table.rowAtPoint(event.getPoint());
				table.setRowSelectionInterval(currentRow, currentRow);
			}

		});
	}

	public void setShowTypeListener(Consumer<List<String>> showTypeListener) {
		this.showTypeListener = showTypeListener;
	}

	public void setTransformationAddedListener(Consumer<Transformation> transformationAddedListener) {
		this.transformationAddedListener = transformationAddedListener;
	}

	private void initMenu() {
		JPopupMenu popupMenu = new JPopupMenu();
		JMenu transfromationsMenu = new JMenu("Transformations");
		popupMenu.add(transfromationsMenu);
		for (TransformationAction a : transformationActions) {
			menuItem(transfromationsMenu, a.getName(), () -> addTransformation(a));
		}
		JMenu typesMenu = new JMenu("Types");
		popupMenu.add(typesMenu);

		menuItem(typesMenu, "Show Type", () -> showType());
		for (TransformationAction a : customizationActions) {
			menuItem(typesMenu, a.getName(), () -> addTransformation(a));
		}
		table.setComponentPopupMenu(popupMenu);
	}

	private void showType() {
		SchemaNode selectedRow = getSelectedRow();
		if (selectedRow != null) {
			showTypeListener.accept(selectedRow.getPath());
		}
	}

	public void addTransformation(TransformationAction a) {
		SchemaNode selectedRow = getSelectedRow();
		if (selectedRow != null) {
			Transformation t = a.create(selectedRow);
			if (t != null) {
				transformationAddedListener.accept(t);
			}
		}
	}

	private SchemaNode getSelectedRow() {
		int r = table.getSelectedRow();
		if (r < 0) {
			return null;
		}
		return tableModel.getList().get(r);
	}

	public SchemaTableModel getTableModel() {
		return tableModel;
	}

	public void selectRootIfNothingSelected() {
		if ((table.getSelectedRow() < 0) && (tableModel.getList().size() > 0)) {
			table.getSelectionModel().setSelectionInterval(0, 0);
		}
	}

	public void resetFindSchemaNode() {
		this.lastSearch = null;
		table.getSelectionModel().clearSelection();
	}

	public boolean find(String text) {
		int current = table.getSelectedRow();
		boolean found = find(text, current);
		if ((current >= 0) && !found) {
			return find(text, -1);
		}
		return false;
	}

	private boolean find(String text, int current) {
		int idx = 0;
		for (SchemaNode sn : getTableModel().getList()) {
			if ((idx > current) && (sn.getPathToRoot().contains(text))) {
				GuiUtils.selectAndScrollToRow(idx, table);
				return true;
			}
			idx++;
		}
		return false;
	}

	public void findSchemaNode() {
		String text = JOptionPane.showInputDialog("Enter text to search", lastSearch);
		if ((text != null) && (text != "")) {
			lastSearch = text;
			find(text);
		}
	}

}
