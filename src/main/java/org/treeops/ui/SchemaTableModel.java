package org.treeops.ui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.treeops.SchemaNode;

public class SchemaTableModel extends AbstractTableModel {
	private final String[] COLUMNS = {"Path", "Optional", "Occurs", "Total", "Type", "Values"};

	private List<SchemaNode> list = new ArrayList<>();

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

	public List<SchemaNode> getList() {
		return list;
	}

	public void setList(List<SchemaNode> list) {
		this.list = list;
		fireTableDataChanged();
	}

	@Override
	public String getColumnName(int column) {
		return COLUMNS[column];
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		SchemaNode r = list.get(rowIndex);
		return schemaNodeValue(columnIndex, true, r);
	}

	public static Object schemaNodeValue(int columnIndex, boolean includePath, SchemaNode r) {
		if (!includePath) {
			columnIndex++;
		}
		if (columnIndex == 0) {
			return r.getPathToRoot();
		} else if (columnIndex == 1) {
			return r.getData().isMandatory() ? "" : "?";
		} else if (columnIndex == 2) {
			return r.getData().getMaxOccurs() > 1 ? ("" + r.getData().getMaxOccurs()) : "";
		} else if (columnIndex == 3) {
			return "" + r.getData().getTotal();
		} else if (columnIndex == 4) {
			return r.getData().isValueHolder() ? "Value" : (r.getChildren().isEmpty() ? "Leaf" : "");
		} else if (columnIndex == 5) {
			return truncateText(r.getData().getValues(), 100);
		}
		return null;
	}

	private static String truncateText(Collection<String> values, int maxChars) {
		if (values.isEmpty()) {
			return "";
		}
		return "" + values;
	}

}
