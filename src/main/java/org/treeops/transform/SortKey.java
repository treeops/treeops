package org.treeops.transform;

import java.util.List;

public class SortKey {
	private boolean ascending = true;

	private List<String> path;

	public SortKey(boolean ascending, List<String> path) {
		super();
		this.ascending = ascending;
		this.path = path;
	}

	public boolean isAscending() {
		return ascending;
	}

	public void setAscending(boolean ascending) {
		this.ascending = ascending;
	}

	public List<String> getPath() {
		return path;
	}

	public void setPath(List<String> path) {
		this.path = path;
	}

}
