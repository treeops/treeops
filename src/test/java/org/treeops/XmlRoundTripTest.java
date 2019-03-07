package org.treeops;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.treeops.DataNode;
import org.treeops.utils.Utils;
import org.treeops.xml.XmlPrettyPrinter;
import org.treeops.xml.XmlReader;
import org.treeops.xml.XmlWriter;

public class XmlRoundTripTest {
	private static final Logger LOG = LoggerFactory.getLogger(XmlRoundTripTest.class);

	@Test
	public void testRead() throws Exception {
		checkFile("src/test/resources/books.xml");
		checkFile("src/test/resources/booksEscape.xml");
		checkFile("src/test/resources/mixedTextOne.xml");
		checkFile("src/test/resources/split.xml");
	}

	@Test
	public void testMixed() throws Exception {
		DataNode r = XmlReader.read(new File("src/test/resources/mixedText.xml"));
		LOG.info(DataNode.printElement(r));
		assertEquals(4, r.getChildren().size());
		assertEquals(r.getChild(3).getSingleChild().getName(), "\n	text1\n	text2\n	text3\n	text4\n");
	}

	private void checkFile(String fileName) throws Exception {
		LOG.info("checking " + fileName);
		DataNode r = XmlReader.read(new File(fileName));
		LOG.info(DataNode.printElement(r));

		String xml = XmlPrettyPrinter.format(XmlWriter.write(r));
		LOG.info(fileName + " \n" + xml);

		File file = new File(fileName);
		assertEquals(xml, XmlPrettyPrinter.format(Utils.text(new File(file.getParentFile(), "saved_" + file.getName()))));

	}
}
