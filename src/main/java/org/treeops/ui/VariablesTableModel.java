package org.treeops.ui;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.treeops.types.TypeVariable;

public class VariablesTableModel extends AbstractTableModel {
	private static final String[] COLUMNS = {"Name", "Type", "Optional", "List"};

	private List<TypeVariable> list = new ArrayList<>();

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

	public void setList(List<TypeVariable> list) {
		this.list = list;
		fireTableDataChanged();
	}

	public List<TypeVariable> getList() {
		return list;
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {

		if ((columnIndex == 2) || (columnIndex == 3)) {
			return Boolean.class;
		}
		return super.getColumnClass(columnIndex);
	}

	@Override
	public String getColumnName(int column) {
		return COLUMNS[column];
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		TypeVariable r = list.get(rowIndex);
		if (columnIndex == 0) {
			return r.getName();
		} else if (columnIndex == 1) {
			return r.getType();
		} else if (columnIndex == 2) {
			return !r.isMandatory();
		} else if (columnIndex == 3) {
			return r.isCollection();
		}
		return null;
	}
}
