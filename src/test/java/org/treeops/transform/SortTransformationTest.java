package org.treeops.transform;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.treeops.DataNode;
import org.treeops.transform.SortKey;
import org.treeops.transform.SortTransformation;
import org.treeops.utils.Utils;

public class SortTransformationTest {

	private static final Logger LOG = LoggerFactory.getLogger(SortTransformationTest.class);

	@Test
	public void testInsert() {
		DataNode root = new DataNode("root");

		new DataNode(root, "a");
		DataNode b = new DataNode(root, "b");
		new DataNode(root, "c");

		DataNode bc1 = new DataNode(b, "bchild");
		new DataNode(bc1, "bc1");
		DataNode bc2 = new DataNode(b, "bchild");
		new DataNode(bc2, "bc2");

		assertEquals(root.getName(), "root");
		assertEquals(root.getChildren().size(), 3);
		assertEquals(root.getChild(0).getName(), "a");
		assertEquals(root.getChild(1).getName(), "b");
		assertEquals(root.getChild(1).getChildren().size(), 2);

		assertEquals(root.getChild(1).getChild(0).getName(), "bchild");
		assertEquals(root.getChild(1).getChild(0).getSingleChild().getName(), "bc1");

		assertEquals(root.getChild(1).getChild(1).getName(), "bchild");
		assertEquals(root.getChild(1).getChild(1).getSingleChild().getName(), "bc2");

		assertEquals(root.getChild(2).getName(), "c");

		LOG.info("before: " + DataNode.printElement(root));

		root = new SortTransformation(Utils.list("root", "b", "bchild"), Utils.list(new SortKey(false, Utils.list()))).transform(root);

		LOG.info("after: " + DataNode.printElement(root));
		assertEquals(root.getName(), "root");
		assertEquals(root.getChildren().size(), 3);

		assertEquals(root.getChild(0).getName(), "a");
		assertEquals(root.getChild(1).getName(), "b");

		assertEquals(root.getChild(1).getChildren().size(), 2);

		assertEquals(root.getChild(1).getChild(0).getName(), "bchild");
		assertEquals(root.getChild(1).getChild(0).getSingleChild().getName(), "bc2");

		assertEquals(root.getChild(1).getChild(1).getName(), "bchild");
		assertEquals(root.getChild(1).getChild(1).getSingleChild().getName(), "bc1");

		assertEquals(root.getChild(2).getName(), "c");

	}
}
