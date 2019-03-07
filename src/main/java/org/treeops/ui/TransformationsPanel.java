package org.treeops.ui;

import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.function.Consumer;

import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;

public class TransformationsPanel extends JPanel {
	private TransformationsTableModel tableModel = new TransformationsTableModel();
	private JTable table = new JTable(tableModel);
	private final Consumer<Integer> deleteListener;

	public TransformationsPanel(Consumer<Integer> deleteListener) {
		super();
		this.deleteListener = deleteListener;
		this.setLayout(new BorderLayout());
		this.add(new JScrollPane(table), BorderLayout.CENTER);

		selectRowOnClick();
		initMenu();
	}

	private void selectRowOnClick() {
		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent event) {
				int currentRow = table.rowAtPoint(event.getPoint());
				if ((currentRow > -1) && (currentRow < tableModel.getList().size())) {
					table.setRowSelectionInterval(currentRow, currentRow);
				}
			}
		});
	}

	private void initMenu() {
		JPopupMenu popupMenu = new JPopupMenu();
		{
			JMenuItem menuItem = new JMenuItem("Delete");
			menuItem.addActionListener(e -> delete());
			popupMenu.add(menuItem);
		}
		table.setComponentPopupMenu(popupMenu);
	}

	private void delete() {
		if (table.getSelectedRow() < 0) {
			return;
		}
		deleteListener.accept(table.getSelectedRow());
	}

	public TransformationsTableModel getTableModel() {
		return tableModel;
	}

	public JTable getTable() {
		return table;
	}

}
