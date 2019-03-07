package org.treeops.transform;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.List;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.treeops.DataNode;
import org.treeops.transform.FilterTransformation;
import org.treeops.utils.Utils;
import org.treeops.xml.XmlReader;

public class FilterTransformationTest {
	private static final Logger LOG = LoggerFactory.getLogger(FilterTransformationTest.class);

	@Test
	public void testFindNodes() throws Exception {
		String fileName = "src/test/resources/books.xml";
		DataNode r = XmlReader.read(new File(fileName));
		LOG.info(DataNode.printElement(r));
		assertEquals(r.getName(), "bookstore");
		assertEquals(r.getChildren().size(), 3);
		assertEquals(r.getChild(0).getName(), "book");

		List<DataNode> found = DataNode.findList(r, Utils.list("bookstore"));
		assertEquals(found.size(), 1);
		assertTrue(found.get(0) == r);

		found = DataNode.findList(r, Utils.list("bookstore", "book"));
		assertEquals(found.size(), 3);
		assertEquals(found.get(0).getName(), "book");

		found = DataNode.findList(r, Utils.list("bookstore", "book", "category"));
		assertEquals(found.size(), 3);

		r = new FilterTransformation(Utils.list("bookstore", "book", "category", "web")).transform(r);

		found = DataNode.findList(r, Utils.list("bookstore"));
		assertEquals(found.size(), 1);
		assertTrue(found.get(0) == r);

		found = DataNode.findList(r, Utils.list("bookstore", "book"));
		assertEquals(found.size(), 1);
		assertEquals(found.get(0).getName(), "book");

		found = DataNode.findList(r, Utils.list("bookstore", "book", "category"));
		assertEquals(found.size(), 1);

	}

}
