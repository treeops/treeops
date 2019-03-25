package org.treeops.transform;

import java.util.ArrayList;
import java.util.List;

import org.treeops.DataNode;
import org.treeops.utils.Utils;

public class MoveToSiblingTransformation implements Transformation {

	private List<String> path = new ArrayList<>();
	private List<String> siblingPath = new ArrayList<>();

	public MoveToSiblingTransformation(List<String> path, List<String> siblingPath) {
		super();
		this.path = path;
		this.siblingPath = siblingPath;
	}

	@Override
	public DataNode transform(DataNode root) {
		String last = Utils.last(path);
		List<String> parentPath = Utils.withoutLast(path);

		List<DataNode> parents = DataNode.findList(root, parentPath);

		List<String> siblingParentPath = siblingPath.subList(0, parentPath.size());
		if (!parentPath.toString().equals(siblingParentPath.toString())) {
			throw new RuntimeException("not a sibling " + path + " " + siblingPath);
		}

		for (DataNode p : parents) {
			List<DataNode> siblingNodes = DataNode.findList(p, siblingPath.subList(parentPath.size() - 1, siblingPath.size()));
			List<DataNode> found = p.getChilds(last);
			p.getChildren().removeAll(found);
			for (DataNode c : found) {
				for (DataNode sibling : siblingNodes) {
					DataNode.copy(c, sibling);
				}
			}
		}
		return root;
	}

	@Override
	public String toString() {
		return "Move to sibling " + path + " sibling " + siblingPath;
	}

}
