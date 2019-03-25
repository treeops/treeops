package org.treeops.types.customization;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.treeops.SchemaNode;
import org.treeops.types.CompositeType;
import org.treeops.types.EnumType;
import org.treeops.types.Type;
import org.treeops.types.TypeVariable;
import org.treeops.utils.Utils;

public class MakeEnumCustomization extends Customization {
	private static final Logger LOG = LoggerFactory.getLogger(MakeEnumCustomization.class);

	private List<String> path;
	private String name;

	public MakeEnumCustomization(List<String> path, String name) {
		super();
		this.path = path;
		this.name = name;
	}

	public List<String> getPath() {
		return path;
	}

	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return "Make Enum " + name + " for " + path;
	}

	@Override
	public void process(SchemaNode rootSchema, List<Type> types) {
		SchemaNode schemaNode = rootSchema.find(getPath());
		if (schemaNode == null) {
			LOG.warn("unable to find schema node " + toString());
			return;
		}

		EnumType enumType = Type.find(types, getName()).map(t -> ((EnumType) t)).orElseGet(() -> {
			EnumType e = new EnumType(getName());
			types.add(e);
			return e;
		});
		enumType.getPaths().add(getPath());
		enumType.getValues().addAll(schemaNode.getData().getValues());

		if (!schemaNode.getData().isValueHolder()) {
			LOG.warn("schema node is not a value holder " + toString());
			return;
		}

		if (schemaNode.getParent() == null) {
			LOG.warn("not applicable to root " + toString());
			return;
		}

		CompositeType type = CompositeType.findCompositeTypeForValueNode(schemaNode.getPath(), types);
		if (type == null) {
			LOG.warn("unable to find parent type " + toString());
			return;
		}
		TypeVariable variable = type.getVariable(Utils.last(getPath()));
		if (variable == null) {
			LOG.warn("unable to find variable " + toString());
			return;
		}
		variable.setType(enumType.getName());
	}

}
