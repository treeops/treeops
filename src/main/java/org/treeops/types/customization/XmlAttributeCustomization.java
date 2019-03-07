package org.treeops.types.customization;

import java.util.List;

import org.treeops.SchemaNode;
import org.treeops.types.Type;

public class XmlAttributeCustomization extends Customization {
	private List<String> path;

	public XmlAttributeCustomization(List<String> path) {
		super();
		this.path = path;
	}

	public List<String> getPath() {
		return path;
	}

	@Override
	public String toString() {
		return "XML attribute " + path;
	}

	@Override
	public void process(SchemaNode rootSchema, List<Type> types) {

	}

}
