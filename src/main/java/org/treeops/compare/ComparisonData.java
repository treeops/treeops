package org.treeops.compare;

import org.treeops.DataNode;

public class ComparisonData {
	private DataNode n1;
	private DataNode n2;

	private boolean nodeSame = true;
	private boolean allChildrenSame = true;

	private boolean ignored;

	public ComparisonData(DataNode n1, DataNode n2, boolean nodeSame, boolean allChildrenSame, boolean ignored) {
		super();
		this.n1 = n1;
		this.n2 = n2;
		this.nodeSame = nodeSame;
		this.allChildrenSame = allChildrenSame;
		this.ignored = ignored;
	}

	public ComparisonData(DataNode n1, DataNode n2, boolean ignored) {
		super();
		this.n1 = n1;
		this.n2 = n2;
		this.ignored = ignored;
	}

	@Override
	public String toString() {
		return "ComparisonData [n1=" + n1 + ", n2=" + n2 + ", same=" + nodeSame + " allChildSame:" + allChildrenSame + ", ignored=" + ignored + "]";
	}

	public DataNode getN1() {
		return n1;
	}

	public void setN1(DataNode n1) {
		this.n1 = n1;
	}

	public DataNode getN2() {
		return n2;
	}

	public void setN2(DataNode n2) {
		this.n2 = n2;
	}

	public boolean isIgnored() {
		return ignored;
	}

	public boolean isNodeSame() {
		return nodeSame;
	}

	public void setNodeSame(boolean nodeSame) {
		this.nodeSame = nodeSame;
	}

	public boolean isAllChildrenSame() {
		return allChildrenSame;
	}

	public void setAllChildrenSame(boolean allChildrenSame) {
		this.allChildrenSame = allChildrenSame;
	}

	public void setIgnored(boolean ignored) {
		this.ignored = ignored;
	}

	public boolean isSameNodeAndChildren() {
		return nodeSame && allChildrenSame;
	}

}
