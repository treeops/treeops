package org.treeops.csv;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Table {
	private List<String> columns = new ArrayList<>();
	private List<List<String>> rows = new ArrayList<>();

	public Table(List<String> columns, List<List<String>> rows) {
		super();
		this.columns = columns;
		this.rows = rows;
	}

	public Table() {
		super();
	}

	public List<String> getColumns() {
		return columns;
	}

	public void setColumns(List<String> columns) {
		this.columns = columns;
	}

	public List<List<String>> getRows() {
		return rows;
	}

	public void setRows(List<List<String>> rows) {
		this.rows = rows;
	}

	public void addRow(List<String> row) {
		rows.add(row);
	}

	public void removeColumns(List<Integer> columnIndexes) {
		ArrayList<Integer> reversedIndexes = new ArrayList<>(columnIndexes);
		Collections.reverse(reversedIndexes);
		for (int columnIdx : reversedIndexes) {
			removeColumn(columnIdx);
		}
	}

	public void removeColumn(int columnIdx) {
		columns.remove(columnIdx);
		for (List<String> r : rows) {
			r.remove(columnIdx);
		}
	}

	public void addColumn(String title, List<String> columnValues) {
		columns.add(title);
		int i = 0;
		for (List<String> r : rows) {
			r.add(columnValues.get(i++));
		}
	}
}
