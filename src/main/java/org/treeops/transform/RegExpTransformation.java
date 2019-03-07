package org.treeops.transform;

import java.util.ArrayList;
import java.util.List;

import org.treeops.DataNode;

public class RegExpTransformation implements Transformation {

	private List<String> path = new ArrayList<>();
	private String regExp;
	private String replacement;

	public RegExpTransformation(List<String> path, String regExp, String replacement) {
		super();
		this.path = path;
		this.regExp = regExp;
		this.replacement = replacement;
	}

	@Override
	public DataNode transform(DataNode root) {
		List<DataNode> found = DataNode.findList(root, path);
		for (DataNode f : found) {
			replaceRegExps(f);
		}
		return root;
	}

	private void replaceRegExps(DataNode n) {
		if (rename(n)) {
			return;
		}
		for (DataNode c : DataNode.children(n)) {
			replaceRegExps(c);
		}
	}

	private boolean rename(DataNode n) {
		boolean matched = n.getName().matches(regExp);
		n.setName(n.getName().replaceAll(regExp, replacement));
		return matched;
	}

	@Override
	public String toString() {
		return "Reg Exp " + path + " " + regExp + " " + replacement;
	}

}
