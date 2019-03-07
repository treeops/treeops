package org.treeops.transform;

import java.util.ArrayList;
import java.util.List;

import org.treeops.DataNode;

public class InsertChildForAllMatchingRegexTransformation implements Transformation {

	private List<String> path = new ArrayList<>();
	private String regularExpression;
	private String valueNodeName;

	public InsertChildForAllMatchingRegexTransformation(List<String> path, String regularExpression, String valueNodeName) {
		super();
		this.path = path;
		this.regularExpression = regularExpression;
		this.valueNodeName = valueNodeName;
	}

	@Override
	public DataNode transform(DataNode root) {
		List<DataNode> found = DataNode.findList(root, path);
		for (DataNode f : found) {
			List<DataNode> matching = new ArrayList<>();
			find(f, matching);
			for (DataNode m : matching) {

				if (m.getData().isValueHolder()) {
					m.getData().setValueHolder(false);
					if (m.getChildren().size() > 0) {
						DataNode valueNode = DataNode.valueNode(null, "value", null);
						for (DataNode c : DataNode.children(m)) {
							c.addToParent(valueNode);
						}
						m.getChildren().clear();
						valueNode.addToParent(m);
					}
				}
				DataNode.valueNode(m, valueNodeName, m.getName());
			}
		}
		return root;
	}

	private void find(DataNode n, List<DataNode> found) {
		if (n.getName().matches(regularExpression)) {
			found.add(n);
			return;
		}

		for (DataNode c : DataNode.children(n)) {
			find(c, found);
		}
	}

	@Override
	public String toString() {
		return "Insert child for all nodes matching regex " + path + " regEx:" + regularExpression + " valueNodename:" + valueNodeName;
	}

}
