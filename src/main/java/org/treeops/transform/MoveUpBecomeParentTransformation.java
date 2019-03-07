package org.treeops.transform;

import java.util.ArrayList;
import java.util.List;

import org.treeops.DataNode;
import org.treeops.utils.Utils;

public class MoveUpBecomeParentTransformation implements Transformation {

	private List<String> path = new ArrayList<>();

	public MoveUpBecomeParentTransformation(List<String> path) {
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

			for (DataNode f : found) {
				DataNode c = DataNode.copy(f, p.getParent(), parentIndex++);
				//p.getParent().getChildren().add(parentIndex++, c);
				DataNode.copy(p, c);
			}
		}

		List<DataNode> grandParents = DataNode.findList(root, Utils.withoutLast(parentPath));
		String parentName = Utils.last(parentPath);

		for (DataNode grandParent : grandParents) {
			grandParent.removeChildren(parentName);
		}

		return root;
	}

	@Override
	public String toString() {
		return "Move up become parent " + path;
	}

}
