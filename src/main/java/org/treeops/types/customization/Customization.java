package org.treeops.types.customization;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.treeops.SchemaNode;
import org.treeops.transform.IdentityTransformation;
import org.treeops.transform.Transformation;
import org.treeops.types.Type;

public abstract class Customization extends IdentityTransformation {

	public static List<Customization> list(List<Transformation> transformations) {
		return transformations.stream().filter(t -> t instanceof Customization).map(t -> (Customization) t).collect(Collectors.toList());
	}

	public abstract void process(SchemaNode rootSchema, List<Type> types);

	public static List<Type> process(SchemaNode rootSchema, List<Type> types, Iterable<Customization> customizations) {
		List<Type> result = new ArrayList<>(types);
		customizations.forEach(c -> c.process(rootSchema, result));
		return result;
	}

}
