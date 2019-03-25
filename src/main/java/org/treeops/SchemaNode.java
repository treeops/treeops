package org.treeops;

import java.util.List;

public class SchemaNode extends GenericNode<SchemaData> {

	public SchemaNode(GenericNode<SchemaData> parent, String name) {
		super(parent, name, new SchemaData());
	}

	public <T> String getIndexedPathToRoot(GenericNode<T> n) {
		if (n == null) {
			return "";
		}
		String prefix = (n.getParent() == null ? "" : (getIndexedPathToRoot(n.getParent()) + "/"));
		Integer index = index(n);
		return prefix + n.getName() + (index == null ? "" : ("[" + index.toString() + "]"));
	}

	private <T> Integer index(GenericNode<T> n) {
		if ((n.getParent() != null) && getSchema(n).getData().isList()) {
			List<GenericNode<T>> nodes = n.getParent().getChilds(n.getName());
			return nodes.indexOf(n) + 1;
		}
		return null;
	}

	private <T> SchemaNode getSchema(GenericNode<T> n) {
		if (n.getParent() == null) {
			//this object is a root schema
			return this;
		}
		return getSchema(n.getParent()).getChild(n.getName());
	}

}
