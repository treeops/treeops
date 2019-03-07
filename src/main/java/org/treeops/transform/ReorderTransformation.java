package org.treeops.transform;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.treeops.DataNode;
import org.treeops.SchemaNode;
import org.treeops.SchemaExtractor;
import org.treeops.utils.Utils;

public class ReorderTransformation implements Transformation {

	private List<String> path = new ArrayList<>();
	private boolean upOrDown;

	public ReorderTransformation(List<String> path, boolean upOrDown) {
		super();
		this.path = path;
		this.upOrDown = upOrDown;
	}

	@Override
	public DataNode transform(DataNode root) {
		SchemaNode schema = SchemaExtractor.schema(root);

		SchemaNode nodeSchema = schema.child(Utils.withoutFirst(path));
		if (nodeSchema == null || nodeSchema.getParent() == null) {
			return root;
		}
		SchemaNode parentSchema = nodeSchema.getParent();
		if (parentSchema.getChildren().size() < 2) {
			return root;
		}

		Set<DataNode> processedParents = new HashSet<>();
		List<DataNode> found = DataNode.findList(root, path);
		reorderSchema(parentSchema, nodeSchema);
		for (DataNode f : found) {
			DataNode p = f.getParent();

			if (processedParents.contains(p)) {
				continue;
			}
			processedParents.add(p);
			reorder(f, parentSchema, nodeSchema);
		}
		return root;
	}

	private void reorder(DataNode n, SchemaNode parentSchema, SchemaNode nodeSchema) {
		DataNode parent = n.getParent();

		List<DataNode> nodes = reorderChildren(parent, parentSchema);

		parent.setChildren(new ArrayList<>());
		parent.getChildren().addAll(nodes);
	}

	private void reorderSchema(SchemaNode parentSchema, SchemaNode nodeSchema) {
		int index = nodeSchema.indexInParent();
		int newIndex = index;
		if (upOrDown) {
			if (index > 0) {
				newIndex = index - 1;
			}

		} else {
			if (index < parentSchema.getChildren().size() - 1) {
				newIndex = index + 1;
			}
		}
		Collections.swap(parentSchema.getChildren(), index, newIndex);
	}

	private List<DataNode> reorderChildren(DataNode parent, SchemaNode parentSchema) {
		List<DataNode> ordered = new ArrayList<>();
		parentSchema.getChildren().forEach(s -> {
			ordered.addAll(parent.getChilds(s.getName()));
		});
		return ordered;
	}

	@Override
	public String toString() {
		return "Reorder " + path + " " + upOrDown;
	}

}
