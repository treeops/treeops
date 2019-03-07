package org.treeops.transform;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.treeops.DataNode;
import org.treeops.transform.InsertParentTransformation;
import org.treeops.utils.Utils;
import org.treeops.xml.XmlWriter;

public class InsertParentTransformationTest {

	private static final Logger LOG = LoggerFactory.getLogger(InsertParentTransformationTest.class);

	@Test
	public void testInsert() throws Exception {
		DataNode root = new DataNode("root");

		new DataNode(root, "a");
		DataNode b = new DataNode(root, "b");
		new DataNode(root, "c");

		DataNode bc1 = new DataNode(b, "bchild");
		new DataNode(bc1, "bc1");
		DataNode bc2 = new DataNode(b, "bchild");
		new DataNode(bc2, "bc2");

		XmlWriter.write(root, new File("build/insertParent.xml"));

		assertEquals(root.getName(), "root");
		assertEquals(root.getChildren().size(), 3);
		assertEquals(root.getChild(0).getName(), "a");
		assertEquals(root.getChild(1).getName(), "b");
		assertEquals(root.getChild(2).getName(), "c");

		LOG.info("before: " + DataNode.printElement(root));
		root = new InsertParentTransformation(Utils.list("root", "b", "bchild"), "bbb").transform(root);

		LOG.info("after: " + DataNode.printElement(root));
		assertEquals(root.getName(), "root");
		assertEquals(root.getChildren().size(), 3);

		assertEquals(root.getChild(0).getName(), "a");
		assertEquals(root.getChild(1).getName(), "b");

		assertEquals(root.getChild(2).getName(), "c");
		DataNode bbb = root.getChild(1).getSingleChild();
		assertEquals(bbb.getName(), "bbb");

		assertEquals(bbb.getChildren().size(), 2);

		assertEquals(bbb.getChild(0).getName(), "bchild");
		assertEquals(bbb.getChild(0).getSingleChild().getName(), "bc1");

		assertEquals(bbb.getChild(1).getName(), "bchild");
		assertEquals(bbb.getChild(1).getSingleChild().getName(), "bc2");
	}

	@Test
	public void testInsertRoot() {
		DataNode root = new DataNode("root");

		new DataNode(root, "a");
		new DataNode(root, "b");

		assertEquals(root.getName(), "root");
		assertEquals(root.getChildren().size(), 2);
		assertEquals(root.getChild(0).getName(), "a");
		assertEquals(root.getChild(1).getName(), "b");

		LOG.info("before: " + DataNode.printElement(root));
		root = new InsertParentTransformation(Utils.list("root"), "newRoot").transform(root);

		LOG.info("after: " + DataNode.printElement(root));
		assertEquals(root.getName(), "newRoot");
		assertEquals(root.getSingleChild().getName(), "root");

		assertEquals(root.getSingleChild().getChildren().size(), 2);
		assertEquals(root.getSingleChild().getChild(0).getName(), "a");
		assertEquals(root.getSingleChild().getChild(1).getName(), "b");

	}

}
