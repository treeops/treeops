package org.treeops.types.customization;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.treeops.SchemaNode;
import org.treeops.types.CompositeType;
import org.treeops.types.Type;
import org.treeops.types.TypeVariable;
import org.treeops.utils.Utils;

public class MoveToSuperTypeCustomization extends Customization {
	private static final Logger LOG = LoggerFactory.getLogger(MoveToSuperTypeCustomization.class);

	private List<String> path;

	public MoveToSuperTypeCustomization(List<String> path) {
		super();
		this.path = path;
	}

	public List<String> getPath() {
		return path;
	}

	@Override
	public String toString() {
		return "Move to super for " + path;
	}

	@Override
	public void process(SchemaNode rootSchema, List<Type> types) {
		SchemaNode schemaNode = rootSchema.find(getPath());
		if (schemaNode == null) {
			LOG.warn("unable to find schema node " + toString());
			return;
		}

		if (schemaNode.getParent() == null) {
			LOG.warn("not applicable to root " + toString());
			return;
		}

		CompositeType type = CompositeType.findCompositeTypeForValueNode(schemaNode.getPath(), types);
		if (type == null) {
			LOG.warn("unable to find parent type " + toString() + " " + type);
			return;
		}

		TypeVariable variable = type.getVariable(Utils.last(getPath()));
		if (variable == null) {
			LOG.warn("unable to find variable " + toString());
			return;
		}

		if (type.getSuperType() == null) {
			LOG.warn("no super type " + toString());
			return;
		}

		CompositeType superType = CompositeType.findComposite(types, type.getSuperType());
		if (superType == null) {
			LOG.warn("super type not found " + toString());
			return;
		}

		type.getVariables().remove(variable);
		if (superType.getVariable(Utils.last(getPath())) == null) {
			superType.getVariables().add(variable);
		}
	}
}
