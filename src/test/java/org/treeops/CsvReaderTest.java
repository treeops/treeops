package org.treeops;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.treeops.csv.CsvReader;
import org.treeops.csv.CsvWriter;
import org.treeops.csv.Table;
import org.treeops.utils.Utils;

public class CsvReaderTest {
	private static final Logger LOG = LoggerFactory.getLogger(CsvReaderTest.class);
	private Table t = table();

	@Test
	public void testRead() throws Exception {
		DataNode root = CsvReader.parse(t);
		LOG.info(DataNode.printElement(root));
		checkTable(root);
	}

	@Test
	public void testReadCsv() throws Exception {
		String csvText = CsvWriter.table2text(t);
		LOG.info("csv " + csvText);
		DataNode root = CsvReader.read(csvText);
		checkTable(root);
	}

	private Table table() {
		List<List<String>> values = new ArrayList<>();
		values.add(Stream.of("a1", "b1", "c1").collect(Collectors.toList()));
		values.add(Stream.of("a2", "b2", "c2").collect(Collectors.toList()));
		return new Table(Utils.list("a", "b", "c"), values);
	}

	private void checkTable(DataNode root) {
		assertEquals(root.getName(), CsvReader.ROOT);
		assertEquals(root.getChildren().size(), 2);

		int i = 0;
		DataNode c = root.getChild(i++);
		assertEquals(CsvReader.ROW, c.getName());
		assertEquals(c.getChild(0).getName(), "a");
		assertEquals(c.getChild(0).getSingleChild().getName(), "a1");
		assertEquals(c.getChild(1).getName(), "b");
		assertEquals(c.getChild(1).getSingleChild().getName(), "b1");
		assertEquals(c.getChild(2).getName(), "c");
		assertEquals(c.getChild(2).getSingleChild().getName(), "c1");

		c = root.getChild(i++);
		assertEquals(CsvReader.ROW, c.getName());
		assertEquals(c.getChild(0).getName(), "a");
		assertEquals(c.getChild(0).getSingleChild().getName(), "a2");
		assertEquals(c.getChild(1).getName(), "b");
		assertEquals(c.getChild(1).getSingleChild().getName(), "b2");
		assertEquals(c.getChild(2).getName(), "c");
		assertEquals(c.getChild(2).getSingleChild().getName(), "c2");
	}

	@Test
	public void testReadBooks() throws Exception {

		File file = new File("src/test/resources/bookmarks.csv");

		LOG.info("checking " + file);
		DataNode r = CsvReader.read(file);
		LOG.info(DataNode.printElement(r));

		assertEquals(r.getName(), CsvReader.ROOT);

		assertEquals(r.getChildren().size(), 2);

		int id = 0;
		int name = 1;
		int url = 2;
		DataNode c = r.getChild(0);

		assertEquals(c.getChildren().size(), 3);

		assertEquals(c.getName(), CsvReader.ROW);
		assertEquals(c.getChild(id).getName(), "Id");
		assertTrue(c.getChild(id).getData().isValueHolder());
		assertEquals(c.getChild(id).getSingleChild().getName(), "1");

		assertEquals(c.getChild(name).getName(), "Name");
		assertTrue(c.getChild(name).getData().isValueHolder());
		assertEquals(c.getChild(name).getSingleChild().getName(), "google");

		assertEquals(c.getChild(url).getName(), "Url");
		assertTrue(c.getChild(url).getData().isValueHolder());
		assertEquals(c.getChild(url).getSingleChild().getName(), "http://www.google.com");

		c = r.getChild(1);
		assertEquals(c.getChildren().size(), 3);

		assertEquals(c.getName(), CsvReader.ROW);
		assertEquals(c.getChild(id).getName(), "Id");
		assertTrue(c.getChild(id).getData().isValueHolder());
		assertEquals(c.getChild(id).getSingleChild().getName(), "2");

		assertEquals(c.getChild(name).getName(), "Name");
		assertTrue(c.getChild(name).getData().isValueHolder());
		assertEquals(c.getChild(name).getSingleChild().getName(), "yahoo");

		assertEquals(c.getChild(url).getName(), "Url");
		assertTrue(c.getChild(url).getData().isValueHolder());
		assertEquals(c.getChild(url).getSingleChild().getName(), "http://www.yahoo.com");

		Table table = CsvWriter.table(r);
		assertEquals(table.getColumns(), Utils.list("row", "Id", "Name", "Url"));

		String csv = CsvWriter.write(r);

		assertEquals(Utils.removeNewLines(csv), "row,Id,Name,Url"//
				+ "row,1,google,http://www.google.com"//
				+ "row,2,yahoo,http://www.yahoo.com");

	}

}
