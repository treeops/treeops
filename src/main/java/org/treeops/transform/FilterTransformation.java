package org.treeops.transform;

import java.util.ArrayList;
import java.util.List;

import org.treeops.DataNode;
import org.treeops.utils.Utils;

public class FilterTransformation implements Transformation {

	private List<String> path = new ArrayList<>();

	public FilterTransformation(List<String> path) {
		super();
		this.path = path;
	}

	@Override
	public DataNode transform(DataNode root) {
		List<String> pathTail = Utils.tail(path, 1);

		List<DataNode> firstLevelElements = new ArrayList<>(root.getChildren());
		for (DataNode f : firstLevelElements) {
			if (DataNode.findList(f, pathTail).isEmpty()) {
				root.getChildren().remove(f);
			}
		}
		return root;
	}

	@Override
	public String toString() {
		return "Filter " + path;
	}

}
