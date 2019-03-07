package org.treeops.transform;

import java.util.ArrayList;
import java.util.List;

import org.treeops.DataNode;

public class RenameTransformation implements Transformation {

	private List<String> path = new ArrayList<>();
	private String newName;

	public RenameTransformation(List<String> path, String newName) {
		super();
		this.path = path;
		this.newName = newName;
	}

	@Override
	public DataNode transform(DataNode root) {
		List<DataNode> found = DataNode.findList(root, path);
		for (DataNode f : found) {
			f.setName(newName);
		}
		return root;
	}

	@Override
	public String toString() {
		return "Rename " + newName + " " + path;
	}

}
