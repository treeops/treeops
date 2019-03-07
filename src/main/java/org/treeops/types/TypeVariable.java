package org.treeops.types;

import java.util.ArrayList;
import java.util.List;

public class TypeVariable {
	private String name;
	private String type;
	private boolean mandatory;
	private boolean collection;

	private List<List<String>> paths = new ArrayList<>();

	public TypeVariable() {
		super();
	}

	public TypeVariable(String name, String type, boolean mandatory, boolean collection) {
		super();
		this.name = name;
		this.type = type;
		this.mandatory = mandatory;
		this.collection = collection;
	}

	public void copyFrom(TypeVariable other) {
		this.name = other.name;
		this.type = other.type;
		this.mandatory = other.mandatory;
		this.collection = other.collection;
		paths.addAll(other.getPaths());
	}

	public TypeVariable(String name) {
		super();
		this.name = name;
	}

	@Override
	public String toString() {
		return name + (mandatory ? "" : "?") + (collection ? "*" : "") + ":" + type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public boolean isMandatory() {
		return mandatory;
	}

	public void setMandatory(boolean mandatory) {
		this.mandatory = mandatory;
	}

	public boolean isCollection() {
		return collection;
	}

	public void setCollection(boolean collection) {
		this.collection = collection;
	}

	public List<List<String>> getPaths() {
		return paths;
	}

	public void setPaths(List<List<String>> paths) {
		this.paths = paths;
	}

}
