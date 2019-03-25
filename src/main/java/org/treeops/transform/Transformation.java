package org.treeops.transform;

import java.util.List;
import java.util.stream.Collectors;

import org.treeops.DataNode;

public interface Transformation {
	DataNode transform(DataNode root);

	static DataNode runAll(List<Transformation> list, DataNode root) {
		DataNode current = root;
		for (Transformation t : list) {
			current = t.transform(current);
		}
		return current;
	}

	@SuppressWarnings("unchecked")
	static <T extends Transformation> List<T> filter(List<Transformation> transformations, Class<T> clazz) {
		return transformations.stream().filter(t -> clazz.isInstance(t)).map(t -> (T) t).collect(Collectors.toList());
	}

}
