package org.treeops.transform;

import java.util.ArrayList;
import java.util.List;

import org.treeops.DataNode;

public class AppendChildTransformation implements Transformation {

	private final List<String> path;
	private final List<String> childSpec;
	private final boolean deleteKeyNode;

	public AppendChildTransformation(List<String> path, List<String> childSpec, boolean deleteKeyNode) {
		super();
		this.path = path;
		this.childSpec = childSpec;
		this.deleteKeyNode = deleteKeyNode;
	}

	@Override
	public DataNode transform(DataNode root) {

		List<DataNode> found = DataNode.findList(root, path);

		for (DataNode f : found) {

			List<String> childSpecWithFound = new ArrayList<>();
			childSpecWithFound.add(f.getName());
			childSpecWithFound.addAll(childSpec);
			List<DataNode> keyNodes = DataNode.findList(f, childSpecWithFound);
			boolean first = true;
			for (DataNode keyNode : keyNodes) {
				if (keyNode.getData().isValueHolder() && (keyNode.getChildren().size() == 1)) {
					if (first) {
						f.setName(f.getName() + "_" + keyNode.getSingleChild().getName());
					}
					if (deleteKeyNode) {
						keyNode.getParent().getChildren().remove(keyNode);
					}
				}
				first = false;
			}
		}
		return root;
	}

	@Override
	public String toString() {
		return "Append Child Name " + path + " child " + childSpec + " " + deleteKeyNode;
	}

}
