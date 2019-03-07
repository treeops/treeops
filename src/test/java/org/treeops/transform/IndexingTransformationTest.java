package org.treeops.transform;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.List;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.treeops.DataNode;
import org.treeops.transform.IndexingTransformation;
import org.treeops.utils.Utils;
import org.treeops.xml.XmlReader;

public class IndexingTransformationTest {
	private static final Logger LOG = LoggerFactory.getLogger(IndexingTransformationTest.class);

	@Test
	public void testFindNodes() throws Exception {
		String fileName = "src/test/resources/books.xml";
		DataNode r = XmlReader.read(new File(fileName));
		LOG.info(DataNode.printElement(r));
		assertEquals(r.getName(), "bookstore");
		assertEquals(r.getChildren().size(), 3);
		assertEquals(r.getChild(0).getName(), "book");

		r = new IndexingTransformation(Utils.list("bookstore", "book", "category")).transform(r);

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

		found = DataNode.findList(r, Utils.list("bookstore", "book", "category1"));
		assertEquals(found.size(), 3);

	}

	@Test
	public void testFindNodesRoot() throws Exception {
		String fileName = "src/test/resources/books.xml";
		DataNode r = XmlReader.read(new File(fileName));
		LOG.info(DataNode.printElement(r));
		assertEquals(r.getName(), "bookstore");
		assertEquals(r.getChildren().size(), 3);
		assertEquals(r.getChild(0).getName(), "book");

		r = new IndexingTransformation(Utils.list("bookstore", "book")).transform(r);

		List<DataNode> found = DataNode.findList(r, Utils.list("bookstore"));
		assertEquals(found.size(), 1);
		assertTrue(found.get(0) == r);

		found = DataNode.children(r);
		assertEquals(found.size(), 3);
		assertEquals(found.get(0).getName(), "book1");
		assertEquals(found.get(1).getName(), "book2");
		assertEquals(found.get(2).getName(), "book3");

		found = DataNode.findList(r, Utils.list("bookstore", "book1", "category"));
		assertEquals(found.size(), 1);

	}

}
