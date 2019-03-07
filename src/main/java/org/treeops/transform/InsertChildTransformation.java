package org.treeops.transform;

import java.util.ArrayList;
import java.util.List;

import org.treeops.DataNode;

public class InsertChildTransformation implements Transformation {

	private List<String> path = new ArrayList<>();
	private String name;

	public InsertChildTransformation(List<String> path, String name) {
		super();
		this.path = path;
		this.name = name;
	}

	@Override
	public DataNode transform(DataNode root) {
		List<DataNode> found = DataNode.findList(root, path);
		for (DataNode f : found) {
			new DataNode(f, name);
		}
		return root;
	}

	@Override
	public String toString() {
		return "Insert child " + path;
	}

}
