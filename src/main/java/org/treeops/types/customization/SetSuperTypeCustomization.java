package org.treeops.types.customization;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.treeops.SchemaNode;
import org.treeops.types.CompositeType;
import org.treeops.types.Type;

public class SetSuperTypeCustomization extends Customization {
	private static final Logger LOG = LoggerFactory.getLogger(SetSuperTypeCustomization.class);

	private List<String> path;
	private String superType;

	public SetSuperTypeCustomization(List<String> path, String superType) {
		super();
		this.path = path;
		this.superType = superType;
	}

	public List<String> getPath() {
		return path;
	}

	public String getSuperType() {
		return superType;
	}

	@Override
	public String toString() {
		return "Set super type " + superType + " for " + path;
	}

	@Override
	public void process(SchemaNode rootSchema, List<Type> types) {
		SchemaNode schemaNode = rootSchema.find(getPath());
		if (schemaNode == null) {
			LOG.warn("unable to find schema node " + toString());
			return;
		}

		if (schemaNode.getData().isValueHolder()) {
			LOG.warn("schema node is a value holder " + toString());
			return;
		}

		CompositeType type = CompositeType.findCompositeTypeOnly(schemaNode.getPath(), types);
		if (type == null) {
			LOG.warn("unable to find composite type " + toString());
			return;
		}

		type.setSuperType(superType);
		if (CompositeType.findComposite(types, superType) == null) {
			types.add(new CompositeType(superType));
		}

	}

}
