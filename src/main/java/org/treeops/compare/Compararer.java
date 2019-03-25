package org.treeops.compare;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.treeops.DataNode;
import org.treeops.GenericNode;
import org.treeops.utils.Utils;

public class Compararer {

	public static ComparisonResult compare(DataNode r1, DataNode r2, ComparisonSettings comparisonSettings) {
		return compare(r1, r2, comparisonSettings, null);
	}

	private static ComparisonResult compare(DataNode r1, DataNode r2, ComparisonSettings comparisonSettings, ComparisonResult parent) {
		DataNode referenceNode = (r1 == null) ? r2 : r1;
		boolean ignored = comparisonSettings.isIgnored(referenceNode);
		ComparisonResult res = new ComparisonResult(parent, referenceNode.getName(), new ComparisonData(r1, r2, ignored));

		if ((r1 != null) && (r2 != null)) {
			res.getData().setNodeSame(r1.getName().equals(r2.getName()));

			if (res.getData().isNodeSame()) {
				if (r1.getData().isValueHolder() && r2.getData().isValueHolder()) {
					res.getData().setNodeSame(Utils.sameText(DataNode.value(r1), DataNode.value(r2)));
				} else if (!ignored) {
					compareChildren(res, r1, r2, comparisonSettings);
				}
			}
		} else {
			res.getData().setNodeSame(false);
		}
		return res;
	}

	private static void compareChildren(ComparisonResult parentRes, DataNode r1, DataNode r2, ComparisonSettings comparisonSettings) {
		List<String> childNames = childNames(r1, r2);
		for (String childName : childNames) {
			compareChild(parentRes, r1, r2, comparisonSettings, childName);
		}
	}

	private static void compareChild(ComparisonResult parentRes, DataNode r1, DataNode r2, ComparisonSettings comparisonSettings, String childName) {
		boolean childIgnored = comparisonSettings.isIgnored(r1, childName);
		List<DataNode> list1 = r1.getChilds(childName);
		List<DataNode> list2 = r2.getChilds(childName);
		if ((list1.size() != list2.size()) && !childIgnored) {
			parentRes.getData().setAllChildrenSame(false);
		}
		for (int i = 0; i < Math.min(list1.size(), list2.size()); i++) {
			DataNode e1 = list1.get(i);
			DataNode e2 = list2.get(i);
			ComparisonResult childComparisonResult = compare(e1, e2, comparisonSettings, parentRes);
			if (!childComparisonResult.getData().isSameNodeAndChildren() && !childIgnored) {
				parentRes.getData().setAllChildrenSame(false);
			}
		}
		for (int i = list1.size(); i < list2.size(); i++) {
			DataNode e = list2.get(i);
			missing(null, e, childIgnored, parentRes, childName);
		}
		for (int i = list2.size(); i < list1.size(); i++) {
			DataNode e = list1.get(i);
			missing(e, null, childIgnored, parentRes, childName);
		}
	}

	private static void missing(DataNode n1, DataNode n2, boolean childIgnored, ComparisonResult parentRes, String childName) {
		new ComparisonResult(parentRes, childName, new ComparisonData(n1, n2, false, false, childIgnored));
	}

	public static List<String> childNames(DataNode r1, DataNode r2) {
		return Stream.concat(r1.getChildren().stream(), r2.getChildren().stream()).map(GenericNode::getName).distinct().collect(Collectors.toList());
	}
}
