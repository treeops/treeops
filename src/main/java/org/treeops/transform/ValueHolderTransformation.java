package org.treeops.transform;

import java.util.ArrayList;
import java.util.List;

import org.treeops.DataNode;

public class ValueHolderTransformation implements Transformation {

	private List<String> path = new ArrayList<>();
	private boolean toValueHolderOrToStructural;

	public ValueHolderTransformation(List<String> path, boolean toValueHolderOrToStructural) {
		super();
		this.path = path;
		this.toValueHolderOrToStructural = toValueHolderOrToStructural;
	}

	@Override
	public DataNode transform(DataNode root) {
		List<DataNode> found = DataNode.findList(root, path);

		if (toValueHolderOrToStructural) {
			for (DataNode f : found) {
				if (f.getChildren().size() > 1) {
					throw new RuntimeException("more than one child " + f.getPathToRoot());
				}

				if ((f.getChildren().size() == 1) && !f.getSingleChild().getChildren().isEmpty()) {
					throw new RuntimeException("value node child should be leaf " + f.getPathToRoot());
				}
			}
		}

		for (DataNode f : found) {
			f.getData().setValueHolder(toValueHolderOrToStructural);
		}

		return root;
	}

	@Override
	public String toString() {
		return (toValueHolderOrToStructural ? "To Value holder " : "To structural ") + path;
	}

}
