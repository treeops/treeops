package org.treeops;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SchemaExtractor {

	private static final int NUM_VALUES = 30;

	public static SchemaNode schema(DataNode n) {
		SchemaNode s = newSchemaNode(n.getName(), null);
		Map<String, SchemaNode> path2SchemaNode = new HashMap<>();
		path2SchemaNode.put(n.getName(), s);
		addChildren(n, path2SchemaNode);
		process(n, path2SchemaNode);
		return s;
	}

	private static void process(DataNode n, Map<String, SchemaNode> path2SchemaNode) {
		String path = n.getPathToRoot();
		SchemaNode s = path2SchemaNode.get(path);

		for (SchemaNode sc : SchemaNode.children(s)) {
			List<DataNode> matching = new ArrayList<>();
			for (DataNode c : DataNode.children(n)) {
				if (c.getName().equals(sc.getName())) {
					matching.add(c);
				}
			}
			if (matching.isEmpty()) {
				sc.getData().setMandatory(false);
			}
			if (matching.size() > sc.getData().getMaxOccurs()) {
				sc.getData().setMaxOccurs(matching.size());
			}

		}

		for (DataNode c : DataNode.children(n)) {
			if (!c.getData().isValueHolder()) {
				process(c, path2SchemaNode);
			}
		}
	}

	private static SchemaNode newSchemaNode(String name, SchemaNode p) {
		SchemaNode s = new SchemaNode(p, name);
		s.getData().setTotal(1);
		return s;
	}

	private static void addChildren(DataNode n, Map<String, SchemaNode> path2SchemaNode) {
		SchemaNode parent = path2SchemaNode.get(n.getPathToRoot());

		for (DataNode c : DataNode.children(n)) {
			String path = c.getPathToRoot();
			SchemaNode s = path2SchemaNode.get(path);
			if (s == null) {
				s = newSchemaNode(c.getName(), parent);
				path2SchemaNode.put(path, s);
			} else {
				s.getData().setTotal(s.getData().getTotal() + 1);
			}

			if (c.getData().isValueHolder()) {
				s.getData().setValueHolder(true);
				if (!c.getChildren().isEmpty()) {
					addValue(s, c.getSingleChild().getName());
				}
			} else {
				addChildren(c, path2SchemaNode);
			}
		}
	}

	private static void addValue(SchemaNode s, String value) {
		if (s.getData().getValues().size() < NUM_VALUES) {
			s.getData().getValues().add(value);
		}
	}

	public static SchemaNode mergeSchemas(DataNode r1, DataNode r2) {
		SchemaNode s1 = schema(r1);
		SchemaNode s2 = schema(r2);
		return mergeCommon(s1, s2);
	}

	private static SchemaNode mergeCommon(SchemaNode s1, SchemaNode s2) {
		s1.getData().merge(s2.getData());

		for (SchemaNode c : SchemaNode.children(s1)) {
			SchemaNode other = s2.getChild(c.getName());
			if (other != null) {
				mergeCommon(c, other);
			} else {

				c.getData().setMandatory(false);
			}
		}

		for (SchemaNode c : SchemaNode.children(s2)) {
			SchemaNode other = s1.getChild(c.getName());
			if (other == null) {
				s1.getChildren().add(c);
				c.setParent(s1);
				c.getData().setMandatory(false);
			}
		}
		return s1;
	}

}
