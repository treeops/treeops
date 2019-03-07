package org.treeops.transform;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.treeops.DataNode;
import org.treeops.transform.MoveSiblingsDownTransformation;
import org.treeops.utils.Utils;
import org.treeops.xml.XmlWriter;

public class MoveSiblingsDownTransformationTest {

	private static final Logger LOG = LoggerFactory.getLogger(MoveSiblingsDownTransformationTest.class);

	@Test
	public void testUp() throws Exception {

		DataNode root = new DataNode("root");

		new DataNode(root, "a");

		DataNode b = new DataNode(root, "b");
		new DataNode(root, "c");

		DataNode bc1 = new DataNode(b, "bchild");
		new DataNode(bc1, "bc1");
		DataNode bc2 = new DataNode(b, "bchild");
		new DataNode(bc2, "bc2");

		XmlWriter.write(root, new File("build/moveSiblingsDown.xml"));
		LOG.info("before: " + DataNode.printElement(root));
		assertEquals(root.getName(), "root");
		assertEquals(root.getChildren().size(), 3);
		assertEquals(root.getChild(0).getName(), "a");
		assertEquals(root.getChild(1).getName(), "b");
		assertEquals(root.getChild(2).getName(), "c");

		root = new MoveSiblingsDownTransformation(Utils.list("root", "a")).transform(root);

		LOG.info("after: " + DataNode.printElement(root));
		assertEquals(root.getName(), "root");

		DataNode nodeA = root.getSingleChild();
		assertEquals(nodeA.getName(), "a");

		assertEquals(nodeA.getChildren().size(), 2);

		DataNode nodeB = nodeA.getChild(0);
		assertEquals(nodeB.getChildren().size(), 2);
		assertEquals(nodeB.getChild(0).getName(), "bchild");
		assertEquals(nodeB.getChild(0).getSingleChild().getName(), "bc1");

		assertEquals(nodeB.getChild(1).getName(), "bchild");
		assertEquals(nodeB.getChild(1).getSingleChild().getName(), "bc2");

		assertEquals(nodeA.getChild(1).getName(), "c");
	}
}
