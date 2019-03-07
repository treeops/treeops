package org.treeops.transform;

import java.util.ArrayList;
import java.util.List;

import org.treeops.DataNode;

public class IndexingTransformation implements Transformation {

	private List<String> path = new ArrayList<>();

	public IndexingTransformation(List<String> path) {
		super();
		this.path = path;
	}

	@Override
	public DataNode transform(DataNode root) {
		List<DataNode> found = DataNode.findList(root, path);

		int i = 0;
		DataNode currentParent = null;
		for (DataNode f : found) {

			i++;
			if ((currentParent == null) || (currentParent != f.getParent())) {
				currentParent = f.getParent();
				i = 1;
			}
			f.setName(f.getName() + i);
		}
		return root;

	}

	@Override
	public String toString() {
		return "Indexing " + path;
	}

}
