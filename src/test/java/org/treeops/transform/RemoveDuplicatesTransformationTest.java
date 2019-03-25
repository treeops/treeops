package org.treeops.transform;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.treeops.DataNode;
import org.treeops.utils.Utils;
import org.treeops.xml.XmlWriter;

public class RemoveDuplicatesTransformationTest {

	private static final Logger LOG = LoggerFactory.getLogger(RemoveDuplicatesTransformationTest.class);

	@Test
	public void testRemoveDuplicates() throws Exception {
		DataNode root = new DataNode("root");

		new DataNode(root, "a");
		DataNode b = new DataNode(root, "b");
		new DataNode(root, "c");

		DataNode bc1 = new DataNode(b, "bchild");
		new DataNode(bc1, "bc1");
		DataNode bc2 = new DataNode(b, "bchild");
		new DataNode(bc2, "bc2");

		DataNode bc3 = new DataNode(b, "bchild");
		new DataNode(bc3, "bc2");

		XmlWriter.write(root, new File("build/remDup.xml"));

		assertEquals(root.getName(), "root");
		assertEquals(root.getChildren().size(), 3);
		assertEquals(root.getChild(0).getName(), "a");
		assertEquals(root.getChild(1).getName(), "b");
		assertEquals(root.getChild(2).getName(), "c");

		assertEquals(root.getChild(1).getChildren().size(), 3);

		LOG.info("before: " + DataNode.printElement(root));
		root = new RemoveDuplicatesTransformation(Utils.list("root", "b", "bchild")).transform(root);

		LOG.info("after: " + DataNode.printElement(root));
		assertEquals(root.getName(), "root");
		assertEquals(root.getChildren().size(), 3);
		assertEquals(root.getChild(1).getChildren().size(), 2);

	}
}
