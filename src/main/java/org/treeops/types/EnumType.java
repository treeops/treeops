package org.treeops.types;

import java.util.HashSet;
import java.util.Set;

public class EnumType extends AtomicType {

	private Set<String> values = new HashSet<>();

	public EnumType(String name) {
		super(name);
	}

	public Set<String> getValues() {
		return values;
	}

	@Override
	public String toString() {
		return "EnumType " + getName() + "[values=" + values + "]";
	}

}
