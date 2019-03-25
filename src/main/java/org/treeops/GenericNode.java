package org.treeops;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.treeops.utils.Utils;

public class GenericNode<T> {
	private GenericNode<T> parent;
	private List<GenericNode<T>> children = new ArrayList<>();
	private T data;
	private String name;

	public GenericNode(GenericNode<T> parent, String name, T data) {
		super();
		this.name = name;
		this.data = data;
		addToParent(parent);
	}

	public void addToParent(GenericNode<T> parent) {
		this.parent = parent;
		if (parent != null) {
			parent.children.add(this);
		}
	}

	public String getName() {
		return name;
	}

	@SuppressWarnings("unchecked")
	public <D extends GenericNode<T>> List<D> flatten(List<D> res) {
		if (res == null) {
			res = new ArrayList<>();
		}
		res.add((D) this);
		for (GenericNode<T> c : children) {
			c.flatten(res);
		}
		return res;
	}

	public String getPathToRoot() {
		return String.join("/", getPath());
	}

	public List<String> getPath() {
		GenericNode<T> e = this;
		List<String> pathToRoot = new ArrayList<>();
		while (e.getParent() != null) {
			pathToRoot.add(0, e.getName());
			e = e.getParent();
		}
		pathToRoot.add(0, e.name);
		return pathToRoot;
	}

	@SuppressWarnings("unchecked")
	public <D extends GenericNode<T>> List<D> getChilds(String childName) {
		List<D> res = new ArrayList<>();
		for (GenericNode<T> c : children) {
			if (childName.equals(c.getName())) {
				res.add((D) c);
			}
		}
		return res;
	}

	@SuppressWarnings("unchecked")
	public <D extends GenericNode<T>> D getChild(String childName, int idx) {
		List<D> res = new ArrayList<>();
		for (GenericNode<T> c : children) {
			if (childName.equals(c.getName())) {
				res.add((D) c);
			}
		}
		return res.get(idx);
	}

	public static <T, D extends GenericNode<T>> List<D> getSiblings(D node) {
		D parent = node.getParent();
		List<D> all = children(parent);
		List<D> siblings = new ArrayList<>();
		for (D c : all) {
			if (!c.getName().equals(node.getName())) {
				siblings.add(c);
			}
		}
		return siblings;
	}

	@SuppressWarnings("unchecked")
	public <D extends GenericNode<T>> D getChild(int idx) {
		return (D) getChildren().get(idx);
	}

	@SuppressWarnings("unchecked")
	public <D extends GenericNode<T>> D getChild(String name) {
		List<GenericNode<T>> res = getChilds(name);
		if (res.isEmpty()) {
			return null;
		}
		return (D) res.get(0);
	}

	public static <T, D extends GenericNode<T>> List<D> children(List<D> nodes) {
		List<D> all = new ArrayList<>();
		for (D m : nodes) {
			all.addAll(m.getChildren());
		}
		return all;
	}

	public static <T> String printElement(GenericNode<T> e) {
		return printElement(e, 0);
	}

	public static <T> String printElement(GenericNode<T> e, int level) {
		StringBuilder s = new StringBuilder("\n" + Utils.tabs(level) + e.getName() + " " + e.getData());
		for (GenericNode<T> c : e.getChildren()) {
			s.append(printElement(c, level + 1));
		}
		return s.toString();
	}

	public void setChildren(List<GenericNode<T>> children) {
		this.children = children;
	}

	@SuppressWarnings("unchecked")
	public <D extends GenericNode<T>> D getParent() {
		return (D) parent;
	}

	public void setParent(GenericNode<T> parent) {
		this.parent = parent;
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

	public boolean hasSingleChild() {
		return getChildren().size() == 1;
	}

	@SuppressWarnings("unchecked")
	public <D extends GenericNode<T>> List<D> getChildren() {
		return (List<D>) children;
	}

	@SuppressWarnings("unchecked")
	public static <T, D extends GenericNode<T>> List<D> children(D n) {
		return (List<D>) n.getChildren();
	}

	public void setName(String name) {
		this.name = name;
	}

	@SuppressWarnings("unchecked")
	public <D extends GenericNode<T>> D getSingleChild() {
		if (getChildren().size() == 1) {
			return (D) getChildren().get(0);
		}
		throw new RuntimeException("expected single child " + getChildren().size());
	}

	public <D extends GenericNode<T>> D find(List<String> path) {
		if (!getName().equals(path.get(0))) {
			return null;
		}
		return findDescendant(Utils.tail(path, 1));
	}

	public <D extends GenericNode<T>> D findDescendant(List<String> path) {
		@SuppressWarnings("unchecked")
		D current = (D) this;
		for (String pe : path) {
			current = current.getChild(pe);
			if (current == null) {
				break;
			}
		}
		return current;
	}

	public static <T, D extends GenericNode<T>> List<D> findList(D node, List<String> pathWithRoot) {
		List<D> res = new ArrayList<>();
		if (node.getName().equals(pathWithRoot.get(0))) {
			List<String> tailPath = Utils.tail(pathWithRoot, 1);
			if (tailPath.isEmpty()) {
				res.add(node);
			} else {
				for (D c : children(node)) {
					res.addAll(findList(c, tailPath));
				}
			}
		}
		return res;
	}

	public int getNumDescendants() {
		int n = 1;
		for (GenericNode<T> c : getChildren()) {
			n += c.getNumDescendants();
		}
		return n;
	}

	public List<List<String>> getChildPaths(boolean includeThis) {
		List<List<String>> res = new ArrayList<>();
		if (includeThis) {
			res.add(getPath());
		}
		for (GenericNode<T> c : getChildren()) {
			res.addAll(c.getChildPaths(true));
		}
		return res;
	}

	@SuppressWarnings("unchecked")
	public <D extends GenericNode<T>> List<D> flatten(boolean includeThis) {
		List<D> res = new ArrayList<>();
		if (includeThis) {
			res.add((D) this);
		}
		for (GenericNode<T> c : getChildren()) {
			res.addAll(c.flatten(true));
		}
		return res;
	}

	@Override
	public String toString() {
		return getPathToRoot();
	}

	public int indexInParent() {
		if (getParent() != null) {
			int i = -1;
			for (GenericNode<T> c : getParent().children) {
				i++;
				if (c == this) {
					return i;
				}
			}
		}
		return -1;
	}

	public static <T> String name(GenericNode<T> n) {
		return (n == null) ? null : n.getName();
	}

	@SuppressWarnings("unchecked")
	public <D extends GenericNode<T>> D child(List<String> pathToChild) {
		if (pathToChild.isEmpty()) {
			return (D) this;
		}

		GenericNode<T> child = getChild(Utils.first(pathToChild));
		if (child == null) {
			return null;
		}
		if (pathToChild.size() == 1) {
			return (D) child;
		}
		return child.child(Utils.withoutFirst(pathToChild));

	}

	public String childValue(List<String> pathToChild) {
		GenericNode<T> child = child(pathToChild);
		if ((child != null) && (child.getChildren().size() > 0)) {
			return child.getChildren().get(0).getName();
		}
		return null;
	}

	public List<String> listChildPaths(boolean includeThis) {
		return flatten(includeThis).stream().map(c -> relativePathText(c)).collect(Collectors.toList());
	}

	public String relativePathText(GenericNode<T> c) {
		return String.join("/", relativePath(c));
	}

	public List<String> relativePath(GenericNode<T> c) {
		return c.getPath().stream().skip(getPath().size()).collect(Collectors.toList());
	}

	public void removeChildren(String childName) {
		children = children.stream().filter(c -> !c.getName().equals(childName)).collect(Collectors.toList());
	}

	public boolean hasChildren() {
		return !children.isEmpty();
	}
}
