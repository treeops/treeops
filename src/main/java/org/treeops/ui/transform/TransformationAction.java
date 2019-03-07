package org.treeops.ui.transform;

import java.util.function.Function;

import org.treeops.SchemaNode;
import org.treeops.transform.Transformation;

public class TransformationAction {
	private String name;
	private Function<SchemaNode, Transformation> factory;

	public String getName() {
		return name;
	}

	public TransformationAction(String name, Function<SchemaNode, Transformation> factory) {
		super();
		this.name = name;
		this.factory = factory;
	}

	public Transformation create(SchemaNode s) {
		return factory.apply(s);
	}

}
