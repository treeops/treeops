package org.treeops.types.customization;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.treeops.SchemaNode;
import org.treeops.types.CompositeType;
import org.treeops.types.Type;
import org.treeops.types.TypeVariable;

public class JoinMutuallyExclusiveCustomization extends Customization {
	private static final Logger LOG = LoggerFactory.getLogger(JoinMutuallyExclusiveCustomization.class);

	private List<String> path;
	private String newName;
	private List<String> names;
	private Map<String, String> oldName2Type = new HashMap<>();

	public JoinMutuallyExclusiveCustomization(List<String> path, String newName, List<String> names) {
		super();
		this.path = path;
		this.newName = newName;
		this.names = names;
	}

	public List<String> getPath() {
		return path;
	}

	public String getNewName() {
		return newName;
	}

	public List<String> getNames() {
		return names;
	}

	@Override
	public String toString() {
		return "Join Mutually Exclusive " + path + " newName=" + newName + " names=" + names;
	}

	@Override
	public void process(SchemaNode rootSchema, List<Type> types) {
		SchemaNode schemaNode = rootSchema.find(path);
		if (schemaNode == null) {
			LOG.warn("unable to find schema node " + toString());
			return;
		}

		CompositeType type = CompositeType.findCompositeTypeOnly(schemaNode.getPath(), types);
		if (type == null) {
			LOG.warn("unable to find type " + toString() + " " + type);
			return;
		}

		TypeVariable joinedVar = new TypeVariable(newName);

		List<TypeVariable> removedVariables = new ArrayList<>();
		List<TypeVariable> updatedVariables = new ArrayList<>();
		int firstIdx = -1;
		int i = -1;
		for (TypeVariable v : type.getVariables()) {
			i++;
			if (names.contains(v.getName())) {
				if (firstIdx < 0) {
					firstIdx = i;
				}
				if (!v.getType().equals(joinedVar.getType())) {
					LOG.warn("different type " + toString() + " var: " + v);
				}

				if (v.isMandatory()) {
					joinedVar.setMandatory(true);
				}
				if (v.isCollection()) {
					joinedVar.setCollection(true);
				}

				removedVariables.add(v);
				continue;
			}
			updatedVariables.add(v);
		}

		if (removedVariables.isEmpty()) {
			LOG.warn("removed variables not found! " + toString());
			return;
		}

		List<String> commonTypes = new ArrayList<>();
		for (TypeVariable v : removedVariables) {

			List<String> varTypes = types(v.getType(), types);
			if (commonTypes.isEmpty()) {
				commonTypes.addAll(varTypes);
			} else {
				commonTypes.retainAll(varTypes);
			}

		}

		if (commonTypes.isEmpty()) {
			LOG.warn("No common type " + toString());
			return;
		}
		joinedVar.setType(commonTypes.get(0));
		for (TypeVariable v : removedVariables) {
			joinedVar.getPaths().addAll(v.getPaths());
		}
		updatedVariables.add(firstIdx, joinedVar);
		type.setVariables(updatedVariables);

		oldName2Type = new HashMap<>();
		for (TypeVariable v : removedVariables) {
			oldName2Type.put(v.getName(), v.getType());
		}

	}

	private List<String> types(String type, List<Type> types) {
		List<String> varTypes = new ArrayList<>();
		varTypes.add(type);
		CompositeType t = CompositeType.findComposite(types, type);
		if ((t != null) && (t.getSuperType() != null)) {
			varTypes.addAll(types(t.getSuperType(), types));
		}
		return varTypes;
	}

	public Map<String, String> getOldName2Type() {
		return oldName2Type;
	}

}
