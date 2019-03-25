package org.treeops.ui;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

import org.treeops.SchemaNode;
import org.treeops.compare.ComparisonResult;
import org.treeops.ui.util.GuiUtils;

public class CompareTableModel extends AbstractTableModel {
	private static final String[] COLUMNS = {"Path", "Left", "Right", "Result"};

	private List<ComparisonResult> list = new ArrayList<>();
	private SchemaNode schema;

	public void setWidths(JTable table) {
		GuiUtils.width(3, 70, table);
	}

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

	public synchronized List<ComparisonResult> getList() {
		return list;
	}

	public synchronized void setList(List<ComparisonResult> list, SchemaNode schema) {
		this.list = list;
		this.schema = schema;
		fireTableDataChanged();
	}

	@Override
	public String getColumnName(int column) {
		return COLUMNS[column];
	}

	@Override
	public synchronized Object getValueAt(int rowIndex, int columnIndex) {
		ComparisonResult r = list.get(rowIndex);
		if (columnIndex == 0) {
			return schema.getIndexedPathToRoot(r);
		} else if (columnIndex == 1) {
			return r.getLeftValue();
		} else if (columnIndex == 2) {
			return r.getRightValue();
		} else if (columnIndex == 3) {
			if (r.getData().isNodeSame()) {
				return "";
			}
			return r.getData().isIgnored() ? "ignored" : "different";
		}
		return null;
	}

}
