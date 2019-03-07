package org.treeops.transform;

import java.util.ArrayList;
import java.util.List;

import org.treeops.DataNode;
import org.treeops.utils.Utils;

public class MoveSiblingsDownTransformation implements Transformation {

	private List<String> path = new ArrayList<>();

	public MoveSiblingsDownTransformation(List<String> path) {
		super();
		this.path = path;
	}

	@Override
	public DataNode transform(DataNode root) {
		String last = Utils.last(path);
		List<String> parentPath = Utils.withoutLast(path);

		List<DataNode> parents = DataNode.findList(root, parentPath);

		for (DataNode p : parents) {
			List<DataNode> childs = DataNode.children(p);
			List<DataNode> found = p.getChilds(last);
			for (DataNode c : found) {

				for (DataNode sibling : childs) {
					if (!sibling.getName().equals(last)) {
						DataNode.copy(sibling, c);
					}
				}
			}
			p.getChildren().clear();
			p.getChildren().addAll(found);

		}
		return root;
	}

	@Override
	public String toString() {
		return "Move siblings down " + path;
	}

}
