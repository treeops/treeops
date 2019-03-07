package org.treeops.transform;

import java.util.ArrayList;
import java.util.List;

import org.treeops.DataNode;
import org.treeops.utils.Utils;

public class MoveUpTransformation implements Transformation {

	private List<String> path = new ArrayList<>();

	public MoveUpTransformation(List<String> path) {
		super();
		this.path = path;
	}

	@Override
	public DataNode transform(DataNode root) {
		String last = Utils.last(path);
		List<String> parentPath = Utils.withoutLast(path);
		List<DataNode> parents = DataNode.findList(root, parentPath);
		for (DataNode p : parents) {
			int parentIndex = p.indexInParent();
			List<DataNode> found = p.getChilds(last);
			p.getChildren().removeAll(found);
			for (DataNode c : found) {
				c.setParent(p.getParent());
				p.getParent().getChildren().add(parentIndex++, c);
			}
		}
		return root;
	}

	@Override
	public String toString() {
		return "Move up " + path;
	}

}
