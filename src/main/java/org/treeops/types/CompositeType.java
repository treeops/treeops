package org.treeops.types;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class CompositeType extends Type {

	private String superType;
	private List<TypeVariable> variables = new ArrayList<>();

	public CompositeType(String name) {
		super(name);
	}

	public List<TypeVariable> getVariables() {
		return variables;
	}

	public String getSuperType() {
		return superType;
	}

	public void setSuperType(String superType) {
		this.superType = superType;
	}

	public void setVariables(List<TypeVariable> variables) {
		this.variables = variables;
	}

	public TypeVariable getVariable(String name) {
		for (TypeVariable v : variables) {
			if (v.getName().equals(name)) {
				return v;
			}
		}
		return null;
	}

	public static Stream<CompositeType> stream(List<Type> types) {
		return types.stream().filter(t -> (t instanceof CompositeType)).map(t -> (CompositeType) t);
	}

	public static CompositeType findComposite(List<Type> list, String name) {
		Optional<Type> t = Type.find(list, name);
		if (!t.isPresent()) {
			return null;
		}
		Type found = t.get();
		if (found instanceof CompositeType) {
			return (CompositeType) found;
		}
		return null;
	}

	public static Type findType(List<String> path, List<Type> list) {
		return findType(path, list, true);
	}

	public static Type findType(List<String> path, List<Type> list, boolean inVariables) {
		for (Type type : list) {

			if (pathFound(type.getPaths(), path)) {
				return type;
			}

			if (inVariables && (type instanceof CompositeType)) {
				CompositeType compositeType = (CompositeType) type;
				for (TypeVariable v : compositeType.getVariables()) {
					if (pathFound(v.getPaths(), path)) {
						return type;
					}
				}
			}

		}
		return null;
	}

	public static CompositeType findCompositeType(List<String> path, List<Type> list) {
		return findComnposite(path, list, true);
	}

	public static CompositeType findCompositeTypeOnly(List<String> path, List<Type> list) {
		return findComnposite(path, list, false);

	}

	private static CompositeType findComnposite(List<String> path, List<Type> list, boolean inVariables) {
		Type t = findType(path, list, inVariables);
		if (t instanceof CompositeType) {
			return (CompositeType) t;
		}
		return null;
	}

	public static CompositeType findCompositeTypeForValueNode(List<String> path, List<Type> list) {
		return findCompositeType(path, list);
	}

	public static boolean pathFound(List<List<String>> paths, List<String> path) {
		for (List<String> p : paths) {
			if (p.toString().contentEquals(path.toString())) {
				return true;
			}
		}
		return false;
	}

}
