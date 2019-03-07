package org.treeops;

public class DataNode extends GenericNode<ParseData> {

	public DataNode(DataNode parent, String name, ParseData data) {
		super(parent, name, data);
	}

	public DataNode(DataNode parent, String name) {
		super(parent, name, new ParseData());
	}

	public DataNode(String name) {
		super(null, name, new ParseData());
	}

	public static DataNode valueNode(DataNode parent, String name, String value) {
		DataNode n = new DataNode(parent, name, new ParseData(true));
		if (value != null) {
			new DataNode(n, value);
		}
		return n;
	}

	public static DataNode copy(DataNode n, DataNode newParent) {
		return copy(n, newParent, -1);
	}

	public static DataNode copy(DataNode n, DataNode newParent, int indexInParent) {
		DataNode c = new DataNode(newParent, n.getName(), n.getData().copy());
		for (DataNode child : DataNode.children(n)) {
			copy(child, c);
		}
		return c;
	}

}
