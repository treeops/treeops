package org.treeops.transform;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.treeops.DataNode;
import org.treeops.utils.Utils;

public class RemoveDuplicatesTransformation implements Transformation {

	private List<String> path = new ArrayList<>();

	public RemoveDuplicatesTransformation(List<String> path) {
		super();
		this.path = path;
	}

	@Override
	public DataNode transform(DataNode root) {
		String last = Utils.last(path);
		List<String> parentPath = Utils.withoutLast(path);
		List<DataNode> parents = DataNode.findList(root, parentPath);
		for (DataNode p : parents) {

			List<DataNode> found = p.getChilds(last);
			Set<String> unique = new HashSet<>();
			for (int i = 0; i < found.size(); i++) {
				DataNode c = found.get(i);
				String text = DataNode.printElement(c);
				if (!unique.add(text)) {
					p.getChildren().remove(c);
				}
			}
		}
		return root;
	}

	@Override
	public String toString() {
		return "Remove dupliucates " + path;
	}

}
