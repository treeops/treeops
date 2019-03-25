package org.treeops.compare;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.stream.Stream;

import org.junit.Test;
import org.treeops.DataNode;
import org.treeops.ParseData;
import org.treeops.utils.Utils;

public class ComparerTest {

	@Test
	public void test() {
		DataNode r1 = new DataNode("root");
		DataNode r2 = new DataNode("root");
		ComparisonSettings settings = new ComparisonSettings(new ArrayList<>());
		ComparisonResult res = Compararer.compare(r1, r2, settings);
		assertTrue(res.getData().isSameNodeAndChildren());
		assertFalse(res.getData().isIgnored());
		assertTrue(res.getData().getN1() == r1);
		assertTrue(res.getData().getN2() == r2);
	}

	@Test
	public void testDiff() {
		DataNode r1 = new DataNode("root1");
		DataNode r2 = new DataNode("root2");
		ComparisonSettings settings = new ComparisonSettings(new ArrayList<>());
		ComparisonResult res = Compararer.compare(r1, r2, settings);
		assertFalse(res.getData().isNodeSame());
		assertFalse(res.getData().isIgnored());
		assertTrue(res.getData().getN1() == r1);
		assertTrue(res.getData().getN2() == r2);
	}

	@Test
	public void testNullLeft() {
		DataNode r1 = new DataNode("root1");

		ComparisonSettings settings = new ComparisonSettings(new ArrayList<>());
		ComparisonResult res = Compararer.compare(r1, null, settings);
		assertFalse(res.getData().isNodeSame());
		assertFalse(res.getData().isIgnored());
		assertTrue(res.getData().getN1() == r1);
		assertTrue(res.getData().getN2() == null);
	}

	@Test
	public void testNullRight() {
		DataNode r2 = new DataNode("root2");

		ComparisonSettings settings = new ComparisonSettings(new ArrayList<>());
		ComparisonResult res = Compararer.compare(null, r2, settings);
		assertFalse(res.getData().isNodeSame());
		assertFalse(res.getData().isIgnored());
		assertTrue(res.getData().getN1() == null);
		assertTrue(res.getData().getN2() == r2);
	}

	@Test
	public void testChildCompare() {
		assertTrue(Compararer.compare(node("a"), node("a"), new ComparisonSettings(new ArrayList<>())).getData().isSameNodeAndChildren());
		assertFalse(Compararer.compare(node("a"), node("b"), new ComparisonSettings(new ArrayList<>())).getData().isAllChildrenSame());
		assertFalse(Compararer.compare(node("a"), node("a", "b"), new ComparisonSettings(new ArrayList<>())).getData().isAllChildrenSame());
		assertTrue(Compararer.compare(node("a", "b"), node("a", "b"), new ComparisonSettings(new ArrayList<>())).getData().isAllChildrenSame());
		assertTrue(Compararer.compare(node("b", "a"), node("a", "b"), new ComparisonSettings(new ArrayList<>())).getData().isAllChildrenSame());
		assertTrue(Compararer.compare(node("a", "a"), node("a", "a"), new ComparisonSettings(new ArrayList<>())).getData().isAllChildrenSame());
		assertFalse(Compararer.compare(node("a", "a", "a"), node("a", "a"), new ComparisonSettings(new ArrayList<>())).getData().isAllChildrenSame());
		assertFalse(Compararer.compare(node("a", "a"), node("a", "a", "a"), new ComparisonSettings(new ArrayList<>())).getData().isAllChildrenSame());
	}

	@Test
	public void testChildNames() {
		assertEquals(Compararer.childNames(node("a"), node("a")), Utils.list("a"));
		assertEquals(Compararer.childNames(node("a", "b"), node("a")), Utils.list("a", "b"));
		assertEquals(Compararer.childNames(node("a", "b"), node("a", "c")), Utils.list("a", "b", "c"));
		assertEquals(Compararer.childNames(node("a"), node("c", "a")), Utils.list("a", "c"));
	}

	private DataNode node(String... childs) {
		DataNode n = new DataNode("root");
		Stream.of(childs).forEach(c -> new DataNode(n, c, new ParseData()));
		return n;
	}

}
