package org.treeops.ui;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.treeops.types.CompositeType;
import org.treeops.types.EnumType;
import org.treeops.types.Type;

public class TypesTableModel extends AbstractTableModel {
	private static final String[] COLUMNS = {"Type", "Description", "Vars", "Used"};

	private List<Type> list = new ArrayList<>();

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

	public void setList(List<Type> list) {
		this.list = list;
		fireTableDataChanged();
	}

	public List<Type> getList() {
		return list;
	}

	@Override
	public String getColumnName(int column) {
		return COLUMNS[column];
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		Type r = list.get(rowIndex);
		CompositeType compositeType = compositeType(r);

		if (columnIndex == 0) {
			return r.getName();
		} else if (columnIndex == 1) {

			if (compositeType != null) {
				String res = "composite";
				if ((compositeType.getSuperType() != null)) {
					res = "extends " + compositeType.getSuperType() + " ";
				}
				return res;
			} else if (r instanceof EnumType) {
				return "Enum: " + String.join(",", ((EnumType) r).getValues());
			} else {
				return "";
			}
		} else if (columnIndex == 2) {
			if (compositeType != null) {
				return compositeType.getVariables().size();
			}
			return "";

		} else if (columnIndex == 3) {
			return r.getPaths().size();
		}

		return null;
	}

	private CompositeType compositeType(Type r) {
		CompositeType compositeType = null;
		if (r instanceof CompositeType) {
			compositeType = (CompositeType) r;
		}
		return compositeType;
	}
}
