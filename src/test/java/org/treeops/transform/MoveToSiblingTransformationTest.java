package org.treeops.transform;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.treeops.DataNode;
import org.treeops.utils.Utils;
import org.treeops.xml.XmlWriter;

public class MoveToSiblingTransformationTest {

	private static final Logger LOG = LoggerFactory.getLogger(MoveToSiblingTransformationTest.class);

	@Test
	public void testMoveToSibling() throws Exception {

		DataNode root = new DataNode("root");

		new DataNode(root, "a");
		DataNode b = new DataNode(root, "b");
		DataNode c1 = new DataNode(root, "c");
		DataNode c1Child = new DataNode(c1, "child");
		new DataNode(c1Child, "c1");

		DataNode c2 = new DataNode(root, "c");
		DataNode c2Child = new DataNode(c2, "child");
		new DataNode(c2Child, "c2");

		new DataNode(root, "c");

		DataNode bc1 = new DataNode(b, "bchild");
		new DataNode(bc1, "bc1");
		DataNode bc2 = new DataNode(b, "bchild");
		new DataNode(bc2, "bc2");

		LOG.info("before: " + DataNode.printElement(root));
		XmlWriter.write(root, new File("build/moveToSibling.xml"));
		assertEquals(root.getName(), "root");
		assertEquals(root.getChildren().size(), 5);
		assertEquals(root.getChild(0).getName(), "a");
		assertEquals(root.getChild(1).getName(), "b");
		assertEquals(root.getChild(2).getName(), "c");
		assertEquals(root.getChild(3).getName(), "c");
		assertEquals(root.getChild(3).getName(), "c");

		DataNode nodeB = root.getChild(1);
		assertEquals(nodeB.getChildren().size(), 2);

		assertEquals(nodeB.getChild(0).getName(), "bchild");
		assertEquals(nodeB.getChild(0).getSingleChild().getName(), "bc1");

		assertEquals(nodeB.getChild(1).getName(), "bchild");
		assertEquals(nodeB.getChild(1).getSingleChild().getName(), "bc2");

		DataNode nodeC = root.getChild(2);
		assertEquals(nodeC.getSingleChild().getName(), "child");
		assertEquals(nodeC.getSingleChild().getSingleChild().getName(), "c1");

		nodeC = root.getChild(3);
		assertEquals(nodeC.getSingleChild().getName(), "child");
		assertEquals(nodeC.getSingleChild().getSingleChild().getName(), "c2");

		nodeC = root.getChild(4);
		assertTrue(nodeC.getChildren().isEmpty());

		root = new MoveToSiblingTransformation(Utils.list("root", "b"), Utils.list("root", "c", "child")).transform(root);

		LOG.info("after: " + DataNode.printElement(root));
		assertEquals(root.getName(), "root");
		assertEquals(root.getChildren().size(), 4);

		assertEquals(root.getChild(0).getName(), "a");
		assertEquals(root.getChild(2).getName(), "c");
		assertEquals(root.getChild(3).getName(), "c");
		assertEquals(root.getChild(3).getName(), "c");

		nodeC = root.getChild(1);
		assertEquals(nodeC.getSingleChild().getName(), "child");
		assertEquals(nodeC.getSingleChild().getChild(0).getName(), "c1");

		nodeB = nodeC.getSingleChild().getChild(1);
		assertEquals(nodeB.getChildren().size(), 2);

		assertEquals(nodeB.getChild(0).getName(), "bchild");
		assertEquals(nodeB.getChild(0).getSingleChild().getName(), "bc1");

		assertEquals(nodeB.getChild(1).getName(), "bchild");
		assertEquals(nodeB.getChild(1).getSingleChild().getName(), "bc2");

		nodeC = root.getChild(2);
		assertEquals(nodeC.getSingleChild().getName(), "child");
		assertEquals(nodeC.getSingleChild().getChild(0).getName(), "c2");

		nodeB = nodeC.getSingleChild().getChild(1);
		assertEquals(nodeB.getChildren().size(), 2);

		assertEquals(nodeB.getChild(0).getName(), "bchild");
		assertEquals(nodeB.getChild(0).getSingleChild().getName(), "bc1");

		assertEquals(nodeB.getChild(1).getName(), "bchild");
		assertEquals(nodeB.getChild(1).getSingleChild().getName(), "bc2");

		nodeC = root.getChild(3);
		assertTrue(nodeC.getChildren().isEmpty());
	}
}
