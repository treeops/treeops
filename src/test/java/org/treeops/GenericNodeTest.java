package org.treeops;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.treeops.utils.Utils;
import org.treeops.xml.XmlReader;

public class GenericNodeTest {
	private static final Logger LOG = LoggerFactory.getLogger(GenericNodeTest.class);

	@Test
	public void testFindNodes() throws Exception {

		DataNode r = XmlReader.read(new File("src/test/resources/books.xml"));
		LOG.info(DataNode.printElement(r));
		assertEquals(r.getName(), "bookstore");
		assertEquals(r.getChildren().size(), 3);
		assertEquals(r.getChild(0).getName(), "book");

		List<DataNode> found = DataNode.findList(r, Utils.list("bookstore"));
		assertEquals(found.size(), 1);
		assertTrue(found.get(0) == r);

		found = DataNode.findList(r, Utils.list("bookstore2"));
		assertEquals(found.size(), 0);

		found = DataNode.findList(r, Utils.list("bookstore", "book"));
		assertEquals(found.size(), 3);
		assertEquals(found.get(0).getName(), "book");
		assertEquals(found.get(1).getName(), "book");
		assertEquals(found.get(2).getName(), "book");

		found = DataNode.findList(r, Utils.list("bookstore", "book", "category"));
		assertEquals(found.size(), 3);
		assertEquals(found.get(0).getName(), "category");

	}

	@Test
	public void testIndexInParent() throws Exception {
		DataNode r = XmlReader.read(new File("src/test/resources/books.xml"));
		LOG.info(DataNode.printElement(r));
		assertEquals(r.getName(), "bookstore");
		assertEquals(r.getChildren().size(), 3);
		assertEquals(r.getChild(0).getName(), "book");

		assertEquals(r.indexInParent(), -1);
		assertEquals(r.getChildren().get(0).indexInParent(), 0);
		assertEquals(r.getChildren().get(1).indexInParent(), 1);
		assertEquals(r.getChildren().get(2).indexInParent(), 2);
	}

	@Test
	public void testListChilds() throws Exception {
		DataNode r = XmlReader.read(new File("src/test/resources/books.xml"));
		LOG.info(DataNode.printElement(r));
		assertEquals(r.getName(), "bookstore");
		assertEquals(r.getChildren().size(), 3);
		assertEquals(r.getChild(0).getName(), "book");

		assertEquals(r.relativePath(r.getChild(0)), Collections.singletonList("book"));

		assertEquals(r.getChild(0).listChildPaths(false), Arrays.asList("category", "category/cooking", "title", "title/lang", "title/lang/en", "title/_text_", "title/_text_/ Everyday Italian ",
				"author", "author/Rocco Puccini", "year", "year/2005", "price", "price/30.00"));
	}

}
