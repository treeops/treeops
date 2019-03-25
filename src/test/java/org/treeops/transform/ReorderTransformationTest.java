package org.treeops.transform;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.treeops.DataNode;
import org.treeops.utils.Utils;

public class ReorderTransformationTest {

	private static final Logger LOG = LoggerFactory.getLogger(ReorderTransformationTest.class);

	@Test
	public void testReorder() {
		DataNode root = new DataNode("root");

		row(root, "A", "1");
		row(root, "A", "2");
		row(root, "B", "3");

		assertEquals(root.getName(), "root");
		assertEquals(root.getChildren().size(), 3);
		int i = 0;
		assertEquals(root.getChild(i).getName(), "row");
		assertEquals(root.getChild(i).getChild(0).getName(), "id");
		assertEquals(root.getChild(i).getChild(0).getSingleChild().getName(), "1");
		assertEquals(root.getChild(i).getChild(1).getName(), "group");
		assertEquals(root.getChild(i).getChild(1).getSingleChild().getName(), "A");

		i++;
		assertEquals(root.getChild(i).getName(), "row");
		assertEquals(root.getChild(i).getChild(0).getName(), "id");
		assertEquals(root.getChild(i).getChild(0).getSingleChild().getName(), "2");
		assertEquals(root.getChild(i).getChild(1).getName(), "group");
		assertEquals(root.getChild(i).getChild(1).getSingleChild().getName(), "A");

		i++;
		assertEquals(root.getChild(i).getName(), "row");
		assertEquals(root.getChild(i).getChild(0).getName(), "id");
		assertEquals(root.getChild(i).getChild(0).getSingleChild().getName(), "3");
		assertEquals(root.getChild(i).getChild(1).getName(), "group");
		assertEquals(root.getChild(i).getChild(1).getSingleChild().getName(), "B");

		LOG.info("before: " + DataNode.printElement(root));
		root = new ReorderTransformation(Utils.list("root", "row", "id"), false).transform(root);

		LOG.info("after: " + DataNode.printElement(root));

		assertEquals(root.getName(), "root");
		assertEquals(root.getChildren().size(), 3);
		i = 0;
		assertEquals(root.getChild(i).getName(), "row");
		assertEquals(root.getChild(i).getChild(0).getName(), "group");
		assertEquals(root.getChild(i).getChild(0).getSingleChild().getName(), "A");
		assertEquals(root.getChild(i).getChild(1).getName(), "id");
		assertEquals(root.getChild(i).getChild(1).getSingleChild().getName(), "1");

		i++;
		assertEquals(root.getChild(i).getName(), "row");
		assertEquals(root.getChild(i).getChild(0).getName(), "group");
		assertEquals(root.getChild(i).getChild(0).getSingleChild().getName(), "A");
		assertEquals(root.getChild(i).getChild(1).getName(), "id");
		assertEquals(root.getChild(i).getChild(1).getSingleChild().getName(), "2");

		i++;
		assertEquals(root.getChild(i).getName(), "row");
		assertEquals(root.getChild(i).getChild(0).getName(), "group");
		assertEquals(root.getChild(i).getChild(0).getSingleChild().getName(), "B");
		assertEquals(root.getChild(i).getChild(1).getName(), "id");
		assertEquals(root.getChild(i).getChild(1).getSingleChild().getName(), "3");
	}

	@Test
	public void testReorderDown() {
		DataNode root = new DataNode("root");

		row(root, "A", "1");
		row(root, "A", "2");
		row(root, "B", "3");

		assertEquals(root.getName(), "root");
		assertEquals(root.getChildren().size(), 3);
		int i = 0;
		assertEquals(root.getChild(i).getName(), "row");
		assertEquals(root.getChild(i).getChild(0).getName(), "id");
		assertEquals(root.getChild(i).getChild(0).getSingleChild().getName(), "1");
		assertEquals(root.getChild(i).getChild(1).getName(), "group");
		assertEquals(root.getChild(i).getChild(1).getSingleChild().getName(), "A");

		i++;
		assertEquals(root.getChild(i).getName(), "row");
		assertEquals(root.getChild(i).getChild(0).getName(), "id");
		assertEquals(root.getChild(i).getChild(0).getSingleChild().getName(), "2");
		assertEquals(root.getChild(i).getChild(1).getName(), "group");
		assertEquals(root.getChild(i).getChild(1).getSingleChild().getName(), "A");

		i++;
		assertEquals(root.getChild(i).getName(), "row");
		assertEquals(root.getChild(i).getChild(0).getName(), "id");
		assertEquals(root.getChild(i).getChild(0).getSingleChild().getName(), "3");
		assertEquals(root.getChild(i).getChild(1).getName(), "group");
		assertEquals(root.getChild(i).getChild(1).getSingleChild().getName(), "B");

		LOG.info("before: " + DataNode.printElement(root));
		root = new ReorderTransformation(Utils.list("root", "row", "group"), true).transform(root);

		LOG.info("after: " + DataNode.printElement(root));

		assertEquals(root.getName(), "root");
		assertEquals(root.getChildren().size(), 3);
		i = 0;
		assertEquals(root.getChild(i).getName(), "row");
		assertEquals(root.getChild(i).getChild(0).getName(), "group");
		assertEquals(root.getChild(i).getChild(0).getSingleChild().getName(), "A");
		assertEquals(root.getChild(i).getChild(1).getName(), "id");
		assertEquals(root.getChild(i).getChild(1).getSingleChild().getName(), "1");

		i++;
		assertEquals(root.getChild(i).getName(), "row");
		assertEquals(root.getChild(i).getChild(0).getName(), "group");
		assertEquals(root.getChild(i).getChild(0).getSingleChild().getName(), "A");
		assertEquals(root.getChild(i).getChild(1).getName(), "id");
		assertEquals(root.getChild(i).getChild(1).getSingleChild().getName(), "2");

		i++;
		assertEquals(root.getChild(i).getName(), "row");
		assertEquals(root.getChild(i).getChild(0).getName(), "group");
		assertEquals(root.getChild(i).getChild(0).getSingleChild().getName(), "B");
		assertEquals(root.getChild(i).getChild(1).getName(), "id");
		assertEquals(root.getChild(i).getChild(1).getSingleChild().getName(), "3");
	}

	private void row(DataNode root, String group, String id) {
		DataNode r = new DataNode(root, "row");
		new DataNode(new DataNode(r, "id"), id);
		new DataNode(new DataNode(r, "group"), group);
	}
}
