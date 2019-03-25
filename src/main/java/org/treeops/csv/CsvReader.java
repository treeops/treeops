package org.treeops.csv;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.treeops.DataNode;

public class CsvReader {
	private static final CSVFormat CSV_FORMAT = CSVFormat.EXCEL.withFirstRecordAsHeader();
	public static final String ROOT = "root";
	public static final String ROW = "row";

	public static DataNode read(File file) throws Exception {
		return parse(readTable(file));
	}

	public static DataNode read(String text) throws Exception {
		return parse(readTable(text));
	}

	protected static Table readTable(File file) throws Exception {
		return parse(CSVParser.parse(file, StandardCharsets.UTF_8, CSV_FORMAT));
	}

	protected static Table readTable(String csvText) throws Exception {
		return parse(CSVParser.parse(csvText, CSV_FORMAT));
	}

	private static Table parse(CSVParser parser) {
		Table table = new Table();
		parser.getHeaderMap().forEach((k, v) -> table.getColumns().add(k));
		for (CSVRecord r : parser) {
			List<String> values = new ArrayList<>();
			table.addRow(values);
			Iterator<String> it = r.iterator();
			while (it.hasNext()) {
				values.add(it.next());
			}
		}

		return table;
	}

	public static DataNode parse(Table table) {
		DataNode root = new DataNode(ROOT);

		for (List<String> r : table.getRows()) {
			DataNode row = new DataNode(root, ROW);

			for (int i = 0; i < table.getColumns().size(); i++) {
				String column = table.getColumns().get(i);

				DataNode v = new DataNode(row, column);
				v.getData().setValueHolder(true);

				String value = r.get(i);
				new DataNode(v, value);
			}
		}
		return root;
	}

}
