package org.treeops.transform;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.treeops.DataNode;
import org.treeops.utils.Utils;

public class GroupTransformation implements Transformation {

	private List<String> path = new ArrayList<>();
	private List<String> groupPath = new ArrayList<>();
	private String groupName;

	public GroupTransformation(List<String> path, List<String> groupPath, String groupName) {
		super();
		this.path = path;
		this.groupPath = groupPath;
		this.groupName = groupName;
	}

	@Override
	public DataNode transform(DataNode root) {
		String last = Utils.last(path);
		List<String> parentPath = Utils.withoutLast(path);
		List<DataNode> parents = DataNode.findList(root, parentPath);
		for (DataNode p : parents) {

			List<DataNode> found = p.getChilds(last);
			if (found.isEmpty()) {
				continue;
			}
			int first = found.get(0).indexInParent();

			Map<String, DataNode> group2nodes = new TreeMap<>();

			for (DataNode c : found) {
				String value = c.childValue(groupPath);
				if (value == null) {
					value = "";
				}
				c.addToParent(group2nodes.computeIfAbsent(value, k -> new DataNode(groupName)));
			}

			p.getChildren().removeAll(found);
			AtomicInteger index = new AtomicInteger(first);
			group2nodes.forEach((k, groupNode) -> {
				groupNode.setParent(p);
				p.getChildren().add(index.getAndIncrement(), groupNode);
			});

		}
		return root;
	}

	@Override
	public String toString() {
		return "Group " + groupName + " " + path + " " + groupPath;
	}

}
