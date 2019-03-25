package org.treeops.transform;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import javax.xml.ws.Holder;

import org.treeops.DataNode;
import org.treeops.GenericNode;
import org.treeops.utils.Utils;

public class RemoveDuplicatesTransformation implements Transformation {

	private List<String> path = new ArrayList<>();

	public RemoveDuplicatesTransformation(List<String> path) {
		super();
		this.path = path;
	}

	@Override
	public DataNode transform(DataNode root) {
		String last = Utils.last(path);
		List<String> parentPath = Utils.withoutLast(path);
		List<DataNode> parents = DataNode.findList(root, parentPath);
		for (DataNode p : parents) {
			removeDuplicates(p, p.getChilds(last));
		}
		return root;
	}

	private static void removeDuplicates(DataNode p, List<DataNode> toCheck) {
		while (!toCheck.isEmpty()) {
			DataNode c = toCheck.remove(0);
			List<DataNode> newToCheck = new ArrayList<>();
			for (DataNode d : toCheck) {
				if (same(c, d)) {
					p.getChildren().remove(d);

				} else {
					newToCheck.add(d);
				}
			}
			toCheck = newToCheck;
		}
	}

	private static boolean same(DataNode n1, DataNode n2) {
		if (!n1.getName().equals(n2.getName())) {
			return false;
		}
		Holder<Boolean> resultHolder = new Holder<>(true);
		Stream.concat(n1.getChildren().stream(), n2.getChildren().stream()).map(GenericNode::getName).distinct().forEach(childName -> checkSame(childName, n1, n2, resultHolder));

		return resultHolder.value;
	}

	private static void checkSame(String childName, DataNode n1, DataNode n2, Holder<Boolean> resultHolder) {
		if (!checkSame(childName, n1, n2)) {
			resultHolder.value = false;
		}
	}

	public static boolean checkSame(String childName, DataNode n1, DataNode n2) {
		List<DataNode> nodes1 = n1.getChilds(childName);
		List<DataNode> nodes2 = n2.getChilds(childName);
		if (nodes1.size() != nodes2.size()) {
			return false;
		}
		int i = 0;
		for (DataNode c1 : nodes1) {
			DataNode c2 = nodes2.get(i++);
			if (!same(c1, c2)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public String toString() {
		return "Remove dupliucates " + path;
	}

}
