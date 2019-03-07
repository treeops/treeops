package org.treeops.types;

public class AtomicType extends Type {

	public AtomicType(String name) {
		super(name);
	}

	public static AtomicType TEXT = new AtomicType("text");
}
