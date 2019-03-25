package org.treeops.transform;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.treeops.DataNode;
import org.treeops.utils.Utils;

public class InsertChildTransformationTest {

	private static final Logger LOG = LoggerFactory.getLogger(InsertChildTransformationTest.class);

	@Test
	public void testUp() {

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
		assertEquals(root.getChild(2).getName(), "c");

		b = root.getChild(1);
		assertEquals(b.getChildren().size(), 2);
		assertEquals(b.getChild(0).getName(), "bchild");
		assertEquals(b.getChild(0).getChildren().size(), 1);
		assertEquals(b.getChild(0).getChild(0).getName(), "bc1");

		assertEquals(b.getChild(1).getName(), "bchild");
		assertEquals(b.getChild(1).getChildren().size(), 1);
		assertEquals(b.getChild(1).getChild(0).getName(), "bc2");

		LOG.info("before: " + DataNode.printElement(root));
		root = new InsertChildTransformation(Utils.list("root", "b", "bchild"), "newChild").transform(root);
		LOG.info("after: " + DataNode.printElement(root));

		assertEquals(root.getName(), "root");
		assertEquals(root.getChildren().size(), 3);
		assertEquals(root.getChild(0).getName(), "a");
		assertEquals(root.getChild(1).getName(), "b");
		assertEquals(root.getChild(2).getName(), "c");

		b = root.getChild(1);
		assertEquals(b.getChildren().size(), 2);
		assertEquals(b.getChild(0).getName(), "bchild");
		assertEquals(b.getChild(0).getChildren().size(), 2);
		assertEquals(b.getChild(0).getChild(0).getName(), "bc1");
		assertEquals(b.getChild(0).getChild(1).getName(), "newChild");

		assertEquals(b.getChild(1).getName(), "bchild");
		assertEquals(b.getChild(1).getChildren().size(), 2);
		assertEquals(b.getChild(1).getChild(0).getName(), "bc2");
		assertEquals(b.getChild(1).getChild(1).getName(), "newChild");

	}
}
