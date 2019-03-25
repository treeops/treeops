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

public class MoveUpTransformationTest {

	private static final Logger LOG = LoggerFactory.getLogger(MoveUpTransformationTest.class);

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

		XmlWriter.write(root, new File("build/moveUp.xml"));
		assertEquals(root.getName(), "root");
		assertEquals(root.getChildren().size(), 3);
		assertEquals(root.getChild(0).getName(), "a");
		assertEquals(root.getChild(1).getName(), "b");
		assertEquals(root.getChild(2).getName(), "c");

		LOG.info("before: " + DataNode.printElement(root));
		root = new MoveUpTransformation(Utils.list("root", "b", "bchild")).transform(root);

		LOG.info("after: " + DataNode.printElement(root));
		assertEquals(root.getName(), "root");
		assertEquals(root.getChildren().size(), 5);

		assertEquals(root.getChild(0).getName(), "a");

		assertEquals(root.getChild(1).getName(), "bchild");

		assertEquals(root.getChild(1).getSingleChild().getName(), "bc1");
		assertEquals(root.getChild(2).getName(), "bchild");
		assertEquals(root.getChild(2).getSingleChild().getName(), "bc2");

		assertEquals(root.getChild(3).getName(), "b");
		assertTrue(root.getChild(3).getChildren().isEmpty());
		assertEquals(root.getChild(4).getName(), "c");
	}
}
