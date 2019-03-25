package org.treeops.compare;

import org.treeops.DataNode;
import org.treeops.GenericNode;

public class ComparisonResult extends GenericNode<ComparisonData> {

	public ComparisonResult(GenericNode<ComparisonData> parent, String name, ComparisonData data) {
		super(parent, name, data);
	}

	public boolean ignored() {
		if (getData().isIgnored()) {
			return true;
		}

		for (ComparisonResult c : children(this)) {
			if (c.ignored()) {
				return true;
			}
		}
		return false;
	}

	public String getValue(boolean leftOrRight) {
		DataNode n = leftOrRight ? getData().getN1() : getData().getN2();
		DataNode other = leftOrRight ? getData().getN2() : getData().getN1();
		if ((n == null) && (other != null) && !other.getData().isValueHolder()) {
			return "missing";
		}

		if ((n != null) && n.getData().isValueHolder() && !n.getChildren().isEmpty()) {
			return n.getSingleChild().getName();
		}

		return "";
	}

	public String getLeftValue() {
		return getValue(true);
	}

	public String getRightValue() {
		return getValue(false);
	}

}
