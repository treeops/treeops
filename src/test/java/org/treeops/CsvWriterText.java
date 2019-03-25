package org.treeops;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.Arrays;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.treeops.csv.CsvWriter;
import org.treeops.csv.Table;
import org.treeops.utils.Utils;
import org.treeops.xml.XmlReader;

public class CsvWriterText {
	private static final Logger LOG = LoggerFactory.getLogger(CsvWriterText.class);

	@Test
	public void testBooks() throws Exception {
		File file = new File("src/test/resources/books.xml");

		LOG.info("checking " + file);
		DataNode r = XmlReader.read(file);
		LOG.info(DataNode.printElement(r));

		assertEquals(r.getName(), "bookstore");

		Table table = CsvWriter.table(r);
		assertEquals(table.getColumns(), Arrays.asList("book", "category", "title", "lang", "_text_", "author", "year", "price", "optional"));

		String csv = CsvWriter.write(r);

		assertEquals(Utils.removeNewLines(csv), "book,category,title,lang,_text_,author,year,price,optional" //
				+ "book,cooking,title,en,\" Everyday Italian \",Rocco Puccini,2005,30.00,"//
				+ "book,children,title,en,Harry Potter,JK Rowling,2005,29.99,text"//
				+ "book,web,title,,\" Learn XML \",Eirk T.Ray,2003,39.90,");

	}
}
