package org.treeops.csv;

import java.io.File;
import java.io.FileWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.treeops.DataNode;
import org.treeops.GenericNode;
import org.treeops.SchemaData;
import org.treeops.SchemaExtractor;
import org.treeops.SchemaNode;
import org.treeops.utils.Utils;

public class CsvWriter {

	public static void write(DataNode root, File file) throws Exception {
		table2file(table(root), file);
	}

	public static String write(DataNode root) throws Exception {
		return table2text(table(root));
	}

	public static Table table(DataNode root) {
		SchemaNode schema = SchemaExtractor.schema(root);
		List<String> columns = schema.flatten(false).stream().map(GenericNode::getPathToRoot).collect(Collectors.toList());
		Table table = new Table(columns, new ArrayList<>());
		for (DataNode r : DataNode.children(root)) {
			addRow(r, table, schema, columns);
		}
		return cleanup(table);
	}

	protected static void removeMandatoryChilds(SchemaNode schema, List<String> columns) {
		schema.flatten(false).stream().forEach(n -> {
			if (n.getData().isMandatory() && hasMandatoryChild(n)) {
				columns.remove(n.getPathToRoot());
			}
		});
	}

	private static boolean hasMandatoryChild(GenericNode<SchemaData> n) {
		return n.getChildren().stream().anyMatch(c -> c.getData().isMandatory());
	}

	private static Table cleanup(Table table) {
		simplifyNames(table);
		return table;
	}

	private static void simplifyNames(Table table) {
		Set<String> unique = new HashSet<>();
		table.setColumns(table.getColumns().stream().map(column -> uniqueName(column, unique)).collect(Collectors.toList()));
	}

	private static String uniqueName(String column, Set<String> unique) {
		List<String> path = Utils.fromPath(column);
		String last = Utils.last(path);
		return unique.add(last) ? last : column;
	}

	private static void addRow(DataNode rowNode, Table table, SchemaNode rootSchema, List<String> columns) {
		Map<String, String> path2val = new HashMap<>();
		populateValues(rowNode, rootSchema, path2val);
		table.addRow(columns.stream().map(c -> path2val.get(c)).collect(Collectors.toList()));
	}

	private static void populateValues(DataNode node, SchemaNode rootSchema, Map<String, String> path2val) {
		String path = node.getPathToRoot();
		if (node.getData().isValueHolder()) {
			if (node.getChildren().isEmpty()) {
				path2val.put(path, "");
			} else {
				path2val.put(path, node.getSingleChild().getName());
			}
		} else {
			path2val.put(path, node.getName());
			for (DataNode c : DataNode.children(node)) {
				populateValues(c, rootSchema, path2val);
			}
		}
	}

	public static void table2file(Table table, File outFile) throws Exception {
		try (FileWriter fw = new FileWriter(outFile); CSVPrinter csvPrinter = new CSVPrinter(fw, CSVFormat.EXCEL.withHeader(table.getColumns().toArray(new String[]{})))) {
			for (List<String> r : table.getRows()) {
				csvPrinter.printRecord(r);
			}
		}
	}

	public static String table2text(Table table) throws Exception {
		StringWriter sw = new StringWriter();
		try (CSVPrinter csvPrinter = new CSVPrinter(sw, CSVFormat.EXCEL.withHeader(table.getColumns().toArray(new String[]{})))) {
			for (List<String> r : table.getRows()) {
				csvPrinter.printRecord(r);
			}
		}
		return sw.toString();
	}

}
