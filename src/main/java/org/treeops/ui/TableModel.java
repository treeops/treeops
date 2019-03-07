package org.treeops.ui;

import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

import org.treeops.csv.Table;

public class TableModel extends AbstractTableModel {

	private Table table;

	public void setTable(Table table, JTable jtable) {
		this.table = table;
		fireTableStructureChanged();

		updateColumnSizes(jtable);
	}

	private void updateColumnSizes(JTable jtable) {
		for (int i = 0; i < table.getColumns().size(); i++) {

			jtable.getColumnModel().getColumn(i).setPreferredWidth(200);
		}
	}

	@Override
	public int getRowCount() {
		if (table == null) {
			return 0;
		}
		return table.getRows().size();
	}

	@Override
	public int getColumnCount() {
		if (table == null) {
			return 0;
		}
		return table.getColumns().size();
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		if (table != null) {
			return table.getRows().get(rowIndex).get(columnIndex);
		}
		return null;
	}

	@Override
	public String getColumnName(int column) {
		if (table == null) {
			return "";
		}
		return table.getColumns().get(column);
	}

}
