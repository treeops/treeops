package org.treeops.transform;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.treeops.DataNode;
import org.treeops.utils.Utils;

public class SortTransformation implements Transformation {

	private List<String> path = new ArrayList<>();
	private List<SortKey> sortKeys = new ArrayList<>();

	public SortTransformation(List<String> path, List<SortKey> sortKeys) {
		super();
		this.path = path;
		this.sortKeys = sortKeys;
	}

	@Override
	public DataNode transform(DataNode root) {
		String last = Utils.last(path);
		List<String> parentPath = Utils.withoutLast(path);
		List<DataNode> parents = DataNode.findList(root, parentPath);
		for (DataNode p : parents) {

			List<DataNode> found = p.getChilds(last);
			int first = found.get(0).indexInParent();

			List<DataNode> sorted = sort(found, sortKeys);
			p.getChildren().removeAll(sorted);
			for (DataNode c : sorted) {
				p.getChildren().add(first++, c);
			}
		}
		return root;
	}

	private static List<DataNode> sort(List<DataNode> found, List<SortKey> sortKeys) {
		Collections.sort(found, (o1, o2) -> compareNodes(o1, o2, sortKeys));
		return found;
	}

	private static int compareNodes(DataNode o1, DataNode o2, List<SortKey> sortKeys) {
		for (SortKey k : sortKeys) {
			int res = compare(k, o1, o2);
			if (res != 0) {
				return res;
			}
		}
		return 0;
	}

	private static int compare(SortKey k, DataNode o1, DataNode o2) {
		String v1 = (o1 == null) ? null : o1.childValue(k.getPath());
		String v2 = (o2 == null) ? null : o2.childValue(k.getPath());

		if ((v1 == null) && (v2 == null)) {
			return 0;
		}

		if (k.isAscending()) {
			if (v1 == null) {
				return -1;
			}
			return v1.compareTo(v2);
		} else {
			if (v2 == null) {
				return -1;
			}
			return v2.compareTo(v1);
		}

	}

	@Override
	public String toString() {
		return "Sort " + path + " " + sortKeys;
	}

}
