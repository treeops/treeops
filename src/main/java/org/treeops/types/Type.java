package org.treeops.types;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class Type {

	private String name;
	private List<List<String>> paths = new ArrayList<>();

	public Type(String name) {
		super();
		this.name = name;
	}

	@Override
	public String toString() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public List<List<String>> getPaths() {
		return paths;
	}

	public void setPaths(List<List<String>> paths) {
		this.paths = paths;
	}

	public static Optional<Type> find(List<Type> types, String name) {
		return types.stream().filter(t -> t.getName().equals(name)).findFirst();
	}

}
