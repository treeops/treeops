package org.treeops.transform;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.treeops.DataNode;
import org.treeops.ParseData;
import org.treeops.transform.ValueHolderTransformation;
import org.treeops.utils.Utils;

public class ValueHolderTransformationTest {

	private static final Logger LOG = LoggerFactory.getLogger(ValueHolderTransformationTest.class);

	@Test
	public void testReorder() {
		DataNode root = new DataNode("root");

		DataNode r = new DataNode(root, "row");
		new DataNode(new DataNode(r, "id", new ParseData(true)), "1");
		new DataNode(new DataNode(r, "group", new ParseData(true)), "a");

		assertEquals(root.getName(), "root");
		assertEquals(root.getChildren().size(), 1);

		assertEquals(root.getSingleChild().getName(), "row");
		assertEquals(root.getSingleChild().getChild(0).getName(), "id");
		assertEquals(root.getSingleChild().getChild(0).getSingleChild().getName(), "1");
		assertTrue(root.getSingleChild().getChild(0).getData().isValueHolder());

		assertEquals(root.getSingleChild().getChild(1).getName(), "group");
		assertEquals(root.getSingleChild().getChild(1).getSingleChild().getName(), "a");
		assertTrue(root.getSingleChild().getChild(1).getData().isValueHolder());

		LOG.info("before: " + DataNode.printElement(root));
		root = new ValueHolderTransformation(Utils.list("root", "row", "id"), false).transform(root);

		LOG.info("after: " + DataNode.printElement(root));
		assertEquals(root.getName(), "root");
		assertEquals(root.getChildren().size(), 1);

		assertEquals(root.getSingleChild().getName(), "row");
		assertEquals(root.getSingleChild().getChild(0).getName(), "id");
		assertEquals(root.getSingleChild().getChild(0).getSingleChild().getName(), "1");
		assertFalse(root.getSingleChild().getChild(0).getData().isValueHolder());

		assertEquals(root.getSingleChild().getChild(1).getName(), "group");
		assertEquals(root.getSingleChild().getChild(1).getSingleChild().getName(), "a");
		assertTrue(root.getSingleChild().getChild(1).getData().isValueHolder());

		root = new ValueHolderTransformation(Utils.list("root", "row", "id"), true).transform(root);

		LOG.info("after: " + DataNode.printElement(root));
		assertEquals(root.getName(), "root");
		assertEquals(root.getChildren().size(), 1);

		assertEquals(root.getSingleChild().getName(), "row");
		assertEquals(root.getSingleChild().getChild(0).getName(), "id");
		assertEquals(root.getSingleChild().getChild(0).getSingleChild().getName(), "1");
		assertTrue(root.getSingleChild().getChild(0).getData().isValueHolder());

		assertEquals(root.getSingleChild().getChild(1).getName(), "group");
		assertEquals(root.getSingleChild().getChild(1).getSingleChild().getName(), "a");
		assertTrue(root.getSingleChild().getChild(1).getData().isValueHolder());

	}

}
