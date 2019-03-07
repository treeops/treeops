package org.treeops.transform;

import java.util.ArrayList;
import java.util.List;

import org.treeops.DataNode;

public class DeleteTransformation implements Transformation {

	private List<String> path = new ArrayList<>();

	public DeleteTransformation(List<String> path) {
		super();
		this.path = path;
	}

	@Override
	public DataNode transform(DataNode root) {
		List<DataNode> found = DataNode.findList(root, path);
		for (DataNode f : found) {
			if (root == f) {
				return deleteRoot(root);
			}

			if (f.getParent() != null) {
				boolean removed = f.getParent().getChildren().remove(f);
				if (!removed) {
					throw new RuntimeException("unable to remove " + f);
				}
			}
		}
		return root;
	}

	private DataNode deleteRoot(DataNode root) {
		if (root.getChildren().size() == 1) {
			DataNode child = root.getChild(0);
			child.setParent(null);
			return child;

		}
		return root;
	}

	@Override
	public String toString() {
		return "Delete " + path;
	}

}
