package org.treeops.compare;

import java.util.ArrayList;
import java.util.List;

import org.treeops.DataNode;

public class ComparisonSettings {
	private List<List<String>> ignoredPaths = new ArrayList<>();

	public ComparisonSettings(List<List<String>> ignoredPaths) {
		this.ignoredPaths = ignoredPaths;
	}

	public boolean isIgnored(DataNode n) {
		return isIgnored(n.getPath());
	}

	public boolean isIgnored(DataNode n, String childName) {
		List<String> p = n.getPath();
		p.add(childName);
		return isIgnored(p);
	}

	public boolean isIgnored(List<String> path) {
		for (List<String> ignored : ignoredPaths) {
			if (same(ignored, path)) {
				return true;
			}
		}
		return false;
	}

	private boolean same(List<String> p1, List<String> p2) {
		if (p1.size() != p2.size()) {
			return false;
		}

		for (int i = 0; i < p1.size(); i++) {
			String s1 = p1.get(i);
			String s2 = p2.get(i);
			if (!s1.equals(s2)) {
				return false;
			}
		}
		return true;
	}

	public List<List<String>> getIgnoredPaths() {
		return ignoredPaths;
	}

	public void setIgnoredPaths(List<List<String>> ignoredPaths) {
		this.ignoredPaths = ignoredPaths;
	}

}
