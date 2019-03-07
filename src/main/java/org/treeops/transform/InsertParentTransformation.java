package org.treeops.transform;

import java.util.List;

import org.treeops.DataNode;
import org.treeops.utils.Utils;

public class InsertParentTransformation implements Transformation {

	private final List<String> path;
	private final String name;

	public InsertParentTransformation(List<String> path, String name) {
		super();
		this.path = path;
		this.name = name;
	}

	@Override
	public DataNode transform(DataNode root) {
		String last = Utils.last(path);
		List<String> parentPath = Utils.withoutLast(path);

		if (parentPath.isEmpty()) {
			DataNode newRoot = new DataNode(name);
			root.addToParent(newRoot);
			return newRoot;
		} else {

			List<DataNode> parents = DataNode.findList(root, parentPath);
			for (DataNode p : parents) {
				List<DataNode> found = p.getChilds(last);
				int firstIndexInParent = found.get(0).indexInParent();
				p.getChildren().removeAll(found);

				DataNode newParent = new DataNode(name);

				newParent.setParent(p);
				p.getChildren().add(firstIndexInParent, newParent);

				for (DataNode c : found) {
					c.setParent(newParent);
					newParent.getChildren().add(c);

				}
			}
			return root;
		}

	}

	@Override
	public String toString() {
		return "Insert parent " + name + " at " + path;
	}

}
