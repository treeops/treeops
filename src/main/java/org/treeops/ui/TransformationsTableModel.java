package org.treeops.ui;

import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingUtilities;
import javax.swing.table.AbstractTableModel;

import org.treeops.transform.Transformation;

public class TransformationsTableModel extends AbstractTableModel {
	private final String[] COLUMNS = {"Transformation"};

	private List<Transformation> list = new ArrayList<>();

	@Override
	public int getRowCount() {
		if (list != null) {
			return list.size();
		}
		return 0;
	}

	@Override
	public int getColumnCount() {
		return COLUMNS.length;
	}

	public void setList(List<Transformation> list) {
		SwingUtilities.invokeLater(() -> {
			this.list = list;
			fireTableDataChanged();
		});
	}

	public List<Transformation> getList() {
		return list;
	}

	@Override
	public String getColumnName(int column) {
		return COLUMNS[column];
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		Transformation r = list.get(rowIndex);
		if (columnIndex == 0) {
			return r.toString();
		}
		return null;
	}
}
