package org.treeops.transform;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.List;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.treeops.DataNode;
import org.treeops.transform.DeleteTransformation;
import org.treeops.utils.Utils;
import org.treeops.xml.XmlReader;

public class DeleteTransformationTest {
	private static final Logger LOG = LoggerFactory.getLogger(DeleteTransformationTest.class);

	@Test
	public void testDelete() throws Exception {
		DataNode r = XmlReader.read(new File("src/test/resources/books.xml"));
		LOG.info(DataNode.printElement(r));
		assertEquals(r.getName(), "bookstore");
		assertEquals(r.getChildren().size(), 3);
		assertEquals(r.getChild(0).getName(), "book");

		r = new DeleteTransformation(Utils.list("bookstore", "book", "category")).transform(r);

		List<DataNode> found = DataNode.findList(r, Utils.list("bookstore"));
		assertEquals(found.size(), 1);
		assertTrue(found.get(0) == r);

		found = DataNode.findList(r, Utils.list("bookstore", "book"));
		assertEquals(found.size(), 3);
		assertEquals(found.get(0).getName(), "book");
		assertEquals(found.get(1).getName(), "book");
		assertEquals(found.get(2).getName(), "book");

		found = DataNode.findList(r, Utils.list("bookstore", "book", "category"));
		assertEquals(found.size(), 0);

	}

	@Test
	public void testDeleteRoot() throws Exception {
		DataNode r = new DataNode("root");
		DataNode newRoot = new DataNode(r, "newRoot");

		new DataNode(newRoot, "child1");
		new DataNode(newRoot, "child2");

		LOG.info(DataNode.printElement(r));
		assertEquals(r.getName(), "root");
		assertEquals(r.getChildren().size(), 1);
		assertEquals(r.getChild(0).getName(), "newRoot");

		assertEquals(r.getChild(0).getChildren().size(), 2);
		assertEquals(r.getChild(0).getChild(0).getName(), "child1");
		assertEquals(r.getChild(0).getChild(1).getName(), "child2");

		r = new DeleteTransformation(Utils.list("root")).transform(r);

		assertEquals(r.getName(), "newRoot");
		assertNull(r.getParent());
		assertEquals(r.getChildren().size(), 2);
		assertEquals(r.getChild(0).getName(), "child1");
		assertEquals(r.getChild(1).getName(), "child2");

	}

	@Test
	public void testDeleteRootPrevented() throws Exception {
		DataNode r = new DataNode("root");

		LOG.info(DataNode.printElement(r));
		assertEquals(r.getName(), "root");

		r = new DeleteTransformation(Utils.list("root")).transform(r);

		assertEquals(r.getName(), "root");

	}

	@Test
	public void testDeleteRootPreventedMultiple() throws Exception {
		DataNode r = new DataNode("root");
		new DataNode(r, "child1");
		new DataNode(r, "child2");
		LOG.info(DataNode.printElement(r));
		assertEquals(r.getName(), "root");
		assertEquals(r.getChildren().size(), 2);
		assertEquals(r.getChild(0).getName(), "child1");
		assertEquals(r.getChild(1).getName(), "child2");

		r = new DeleteTransformation(Utils.list("root")).transform(r);

		assertEquals(r.getName(), "root");
		assertEquals(r.getChildren().size(), 2);
		assertEquals(r.getChild(0).getName(), "child1");
		assertEquals(r.getChild(1).getName(), "child2");

	}

}
