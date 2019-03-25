package org.treeops.transform;

import org.treeops.DataNode;

public class IdentityTransformation implements Transformation {

	@Override
	public DataNode transform(DataNode root) {
		return root;
	}

}
