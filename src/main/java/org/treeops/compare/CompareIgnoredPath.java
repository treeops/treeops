package org.treeops.compare;

import java.util.List;
import java.util.stream.Collectors;

import org.treeops.transform.IdentityTransformation;
import org.treeops.transform.Transformation;

public class CompareIgnoredPath extends IdentityTransformation {
	private final List<String> path;

	public CompareIgnoredPath(List<String> path) {
		super();
		this.path = path;
	}

	public List<String> getPath() {
		return path;
	}

	@Override
	public String toString() {
		return "Compare Ignored " + path;
	}

	public static List<List<String>> ignoredPaths(List<Transformation> transformations) {
		return Transformation.filter(transformations, CompareIgnoredPath.class).stream().map(c -> c.path).collect(Collectors.toList());
	}
}
