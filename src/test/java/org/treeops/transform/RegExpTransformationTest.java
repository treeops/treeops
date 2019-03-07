package org.treeops.transform;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.treeops.DataNode;
import org.treeops.transform.RegExpTransformation;
import org.treeops.utils.Utils;

public class RegExpTransformationTest {

	private static final Logger LOG = LoggerFactory.getLogger(RegExpTransformationTest.class);

	@Test
	public void testGroup() {
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

		assertEquals(root.getChild(1).getChildren().size(), 2);
		assertEquals(root.getChild(1).getChild(0).getName(), "bchild");
		assertEquals(root.getChild(1).getChild(0).getSingleChild().getName(), "bc1");
		assertEquals(root.getChild(1).getChild(1).getName(), "bchild");
		assertEquals(root.getChild(1).getChild(1).getSingleChild().getName(), "bc2");

		LOG.info("before: " + DataNode.printElement(root));
		root = new RegExpTransformation(Utils.list("root", "b", "bchild"), "bchild", "theChild").transform(root);

		root = new RegExpTransformation(Utils.list("root"), "bc([0-9]*)", "childx$1").transform(root);

		LOG.info("after: " + DataNode.printElement(root));
		assertEquals(root.getName(), "root");
		assertEquals(root.getChildren().size(), 3);
		assertEquals(root.getChild(0).getName(), "a");
		assertEquals(root.getChild(1).getName(), "b");
		assertEquals(root.getChild(2).getName(), "c");

		assertEquals(root.getChild(1).getChildren().size(), 2);
		assertEquals(root.getChild(1).getChild(0).getName(), "theChild");
		assertEquals(root.getChild(1).getChild(0).getSingleChild().getName(), "childx1");
		assertEquals(root.getChild(1).getChild(1).getName(), "theChild");
		assertEquals(root.getChild(1).getChild(1).getSingleChild().getName(), "childx2");

	}
}
