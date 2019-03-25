package org.treeops;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.treeops.xml.XmlPrettyPrinter;
import org.treeops.xml.XmlReader;
import org.treeops.xml.XmlWriter;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class XmWriterTest {
	private static final Logger LOG = LoggerFactory.getLogger(XmWriterTest.class);

	@Test
	public void testRead() throws Exception {
		String fileName = "src/test/resources/books.xml";

		DataNode r = XmlReader.read(new File(fileName));
		LOG.info(DataNode.printElement(r));
		assertEquals(r.getName(), "bookstore");
		assertEquals(r.getChildren().size(), 3);
		assertEquals(r.getChild(0).getName(), "book");

		String outputXml = XmlPrettyPrinter.format(XmlWriter.write(r));
		LOG.info("xml " + outputXml);

		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();

		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(new InputSource(new StringReader(outputXml)));
		doc.getDocumentElement().normalize();

		assertEquals(doc.getDocumentElement().getNodeName(), "bookstore");

		NodeList books = doc.getDocumentElement().getElementsByTagName("book");
		assertEquals(books.getLength(), 3);

		Element book = (Element) books.item(0);
		assertEquals(book.getNodeName(), "book");

		assertEquals(book.getElementsByTagName("category").getLength(), 1);
		assertEquals(book.getElementsByTagName("category").item(0).getTextContent(), "cooking");

		Element title = (Element) book.getElementsByTagName("title").item(0);
		assertEquals(title.getElementsByTagName("lang").item(0).getTextContent(), "en");
		assertEquals(title.getElementsByTagName(XmlReader.TEXT_NAME).item(0).getTextContent(), " Everyday Italian ");

		Element author = (Element) book.getElementsByTagName("author").item(0);
		assertEquals(author.getTextContent(), "Rocco Puccini");

	}

}
