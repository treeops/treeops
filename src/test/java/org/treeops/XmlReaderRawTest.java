package org.treeops;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.treeops.DataNode;
import org.treeops.xml.XmlPrettyPrinter;
import org.treeops.xml.XmlReader;
import org.treeops.xml.XmlWriter;

public class XmlReaderRawTest {
	private static final Logger LOG = LoggerFactory.getLogger(XmlReaderRawTest.class);

	@Test
	public void testRead() throws Exception {
		String fileName = "src/test/resources/books.xml";

		DataNode r = XmlReader.rawReadFile(new File(fileName));
		LOG.info(DataNode.printElement(r));
		assertEquals(r.getName(), "bookstore");
		assertEquals(r.getChildren().size(), 3);

		assertEquals(r.getChild(0).getName(), "book");
		assertEquals(r.getChild(1).getName(), "book");
		assertEquals(r.getChild(2).getName(), "book");

		DataNode n = r.getChild(0);

		assertEquals(n.getChildren().size(), 5);

		DataNode c = n.getChild(0);
		assertEquals(c.getName(), "category");
		assertEquals(c.getSingleChild().getName(), "cooking");

		c = n.getChild(1);
		assertEquals(c.getName(), "title");
		assertEquals(c.getChildren().size(), 2);

		assertEquals(c.getChild(0).getName(), "lang");
		assertTrue(c.getChild(0).getData().isValueHolder());
		assertEquals(c.getChild(0).getSingleChild().getName(), "en");

		c = n.getChild(2);

		assertEquals(c.getChildren().size(), 1);
		assertEquals(c.getName(), "author");
		assertEquals(c.getSingleChild().getName(), XmlReader.TEXT_NAME);
		assertTrue(c.getSingleChild().getData().isValueHolder());

		assertEquals(c.getSingleChild().getSingleChild().getName(), "Rocco Puccini");

		LOG.info("xml " + XmlPrettyPrinter.format(XmlWriter.write(r)));

	}

}
