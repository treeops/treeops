package org.treeops.transform;

import java.util.List;

import org.treeops.DataNode;

public interface Transformation {
	DataNode transform(DataNode root);

	static DataNode runAll(List<Transformation> list, DataNode root) {
		DataNode current = root;
		for (Transformation t : list) {
			current = t.transform(current);
		}
		return current;
	}
}
