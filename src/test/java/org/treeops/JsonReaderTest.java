package org.treeops;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.treeops.DataNode;
import org.treeops.SchemaNode;
import org.treeops.SchemaExtractor;
import org.treeops.json.JsonPrettyPrinter;
import org.treeops.json.JsonReader;
import org.treeops.json.JsonWriter;

public class JsonReaderTest {
	private static final Logger LOG = LoggerFactory.getLogger(JsonReaderTest.class);

	@Test
	public void testEmptyObject() throws Exception {
		DataNode root = read("{}");
		assertEquals(root.getName(), JsonReader.OBJECT);
		assertTrue(root.getChildren().isEmpty());
	}

	@Test
	public void testNull() throws Exception {
		DataNode root = read("null");
		assertEquals(root.getName(), JsonReader.ROOT);
		assertEquals(root.getChildren().size(), 1);
		assertEquals(root.getSingleChild().getName(), JsonReader.VALUE);
		assertTrue(root.getSingleChild().getChildren().isEmpty());
	}

	@Test
	public void testEmptyArray() throws Exception {
		DataNode root = read("[]");
		assertEquals(root.getName(), JsonReader.ROOT);
		assertTrue(root.getChildren().isEmpty());
	}

	@Test
	public void testSingleValues() throws Exception {
		checkFirstNodeIs("5", "5");
		checkFirstNodeIs("5.0", "5.0");
		checkFirstNodeIs("\"text\"", "text");
	}

	private void checkFirstNodeIs(String json, String firstChild) throws Exception {
		DataNode root = read(json);

		assertEquals(root.getName(), JsonReader.ROOT);
		assertEquals(root.getChildren().size(), 1);
		assertEquals(root.getSingleChild().getName(), JsonReader.VALUE);
		assertEquals(root.getSingleChild().getChildren().size(), 1);

		assertEquals(root.getSingleChild().getSingleChild().getName(), firstChild);
		assertTrue(root.getSingleChild().getSingleChild().getChildren().isEmpty());

	}

	@Test
	public void testTopLevelArrayValues() throws Exception {
		DataNode root = read("[1,2,3]");

		assertEquals(root.getName(), JsonReader.ROOT);
		assertEquals(root.getChildren().size(), 3);

		int idx = 0;

		DataNode c = root.getChild(idx++);
		assertEquals(c.getName(), JsonReader.ARRAY);
		assertEquals(c.getSingleChild().getName(), JsonReader.VALUE);
		assertEquals(c.getSingleChild().getSingleChild().getName(), "1");

		c = root.getChild(idx++);
		assertEquals(c.getName(), JsonReader.ARRAY);
		assertEquals(c.getSingleChild().getName(), JsonReader.VALUE);
		assertEquals(c.getSingleChild().getSingleChild().getName(), "2");

		c = root.getChild(idx++);
		assertEquals(c.getName(), JsonReader.ARRAY);
		assertEquals(c.getSingleChild().getName(), JsonReader.VALUE);
		assertEquals(c.getSingleChild().getSingleChild().getName(), "3");
	}

	@Test
	public void testArrayWithinArray() throws Exception {
		DataNode root = read("[1,[2,[3,4]],5]");

		assertEquals(root.getName(), JsonReader.ROOT);
		assertEquals(root.getChildren().size(), 3);

		int idx = 0;

		DataNode c = root.getChild(idx++);
		assertEquals(c.getName(), JsonReader.ARRAY);
		assertEquals(c.getSingleChild().getName(), JsonReader.VALUE);
		assertEquals(c.getSingleChild().getSingleChild().getName(), "1");

		c = root.getChild(idx++);
		assertEquals(c.getName(), JsonReader.ARRAY);
		assertEquals(c.getChildren().size(), 2);

		DataNode d = c.getChild(0);

		assertEquals(d.getName(), JsonReader.ARRAY);
		assertEquals(d.getSingleChild().getName(), JsonReader.VALUE);
		assertEquals(d.getSingleChild().getSingleChild().getName(), "2");

		d = c.getChild(1);
		assertEquals(d.getName(), JsonReader.ARRAY);
		assertEquals(d.getChildren().size(), 2);

		assertEquals(d.getChildren().get(0).getName(), JsonReader.ARRAY);
		assertEquals(d.getChildren().get(0).getSingleChild().getName(), JsonReader.VALUE);
		assertEquals(d.getChildren().get(0).getSingleChild().getSingleChild().getName(), "3");
		assertEquals(d.getChildren().get(1).getName(), JsonReader.ARRAY);
		assertEquals(d.getChildren().get(1).getSingleChild().getName(), JsonReader.VALUE);
		assertEquals(d.getChildren().get(1).getSingleChild().getSingleChild().getName(), "4");

		c = root.getChild(idx++);
		assertEquals(c.getName(), JsonReader.ARRAY);
		assertEquals(c.getSingleChild().getName(), JsonReader.VALUE);
		assertEquals(c.getSingleChild().getSingleChild().getName(), "5");
	}

	@Test
	public void testTopLevelArrayObject() throws Exception {
		DataNode root = read("[{\"a\":1, \"b\":2}, {\"a\":3,\"b\":4}, null, {\"a\":5}, {\"a\":6,\"b\":null}]");

		assertEquals(root.getName(), JsonReader.ROOT);
		assertEquals(root.getChildren().size(), 5);

		int idx = 0;

		DataNode c = root.getChild(idx++);
		assertEquals(c.getName(), JsonReader.ARRAY);
		assertEquals(c.getChildren().size(), 2);
		assertEquals(c.getChild("a").getSingleChild().getName(), "1");
		assertEquals(c.getChild("b").getSingleChild().getName(), "2");

		c = root.getChild(idx++);
		assertEquals(c.getName(), JsonReader.ARRAY);
		assertEquals(c.getChildren().size(), 2);
		assertEquals(c.getChild("a").getSingleChild().getName(), "3");
		assertEquals(c.getChild("b").getSingleChild().getName(), "4");

		c = root.getChild(idx++);
		assertEquals(c.getName(), JsonReader.ARRAY);
		assertTrue(c.getChildren().isEmpty());

		c = root.getChild(idx++);
		assertEquals(c.getName(), JsonReader.ARRAY);
		assertEquals(c.getChildren().size(), 1);
		assertEquals(c.getChild("a").getSingleChild().getName(), "5");

		c = root.getChild(idx++);
		assertEquals(c.getName(), JsonReader.ARRAY);
		assertEquals(c.getChildren().size(), 2);
		assertEquals(c.getChild("a").getSingleChild().getName(), "6");
		assertTrue(c.getChild("b").getChildren().isEmpty());

	}

	@Test
	public void testImport() throws Exception {
		String json = "{\"a\":[1,2,{\"b\":true},3],\"c\":\"4\", \"d\":null, \"e\":{ \"name\" : \"Name\" }, \"ea\":[] }";

		DataNode root = read(json);

		assertEquals(root.getName(), "Object");
		assertEquals(root.getChildren().size(), 7);

		int i = 0;
		DataNode c = root.getChild(i++);
		assertEquals("a", c.getName());
		assertEquals(c.getSingleChild().getName(), JsonReader.VALUE);
		assertEquals(c.getSingleChild().getSingleChild().getName(), "1");

		c = root.getChild(i++);
		assertEquals("a", c.getName());
		assertEquals(c.getSingleChild().getName(), JsonReader.VALUE);
		assertEquals(c.getSingleChild().getSingleChild().getName(), "2");

		c = root.getChild(i++);
		assertEquals("a", c.getName());
		assertEquals(c.getSingleChild().getName(), "b");
		assertEquals(c.getSingleChild().getSingleChild().getName(), "true");

		c = root.getChild(i++);
		assertEquals("a", c.getName());
		assertEquals(c.getSingleChild().getName(), JsonReader.VALUE);
		assertEquals(c.getSingleChild().getSingleChild().getName(), "3");

		c = root.getChild(i++);
		assertEquals("c", c.getName());
		assertEquals(c.getSingleChild().getName(), "4");

		c = root.getChild(i++);
		assertEquals("d", c.getName());
		assertTrue(c.getChildren().isEmpty());

		c = root.getChild(i++);
		assertEquals("e", c.getName());
		assertEquals(c.getSingleChild().getName(), "name");
		assertEquals(c.getSingleChild().getSingleChild().getName(), "Name");

	}

	@Test
	public void testArrayWithinArrayWithinArray() throws Exception {
		DataNode root = read("[1,[2,[3,4]],5]");

		assertEquals(root.getName(), JsonReader.ROOT);
		assertEquals(root.getChildren().size(), 3);

		int idx = 0;

		DataNode c = root.getChild(idx++);
		assertEquals(c.getName(), JsonReader.ARRAY);
		assertEquals(c.getChildren().size(), 1);
		assertEquals(c.getSingleChild().getName(), JsonReader.VALUE);
		assertEquals(c.getSingleChild().getSingleChild().getName(), "1");

		c = root.getChild(idx++);
		assertEquals(c.getName(), JsonReader.ARRAY);
		assertEquals(c.getChildren().size(), 2);

		DataNode d = c.getChild(0);

		assertEquals(d.getChild(0).getName(), JsonReader.VALUE);
		assertEquals(d.getChild(0).getSingleChild().getName(), "2");

		d = c.getChild(1);

		assertEquals(d.getName(), JsonReader.ARRAY);
		assertEquals(d.getChildren().size(), 2);

		assertEquals(d.getChild(0).getName(), JsonReader.ARRAY);
		assertEquals(d.getChild(0).getSingleChild().getName(), JsonReader.VALUE);
		assertEquals(d.getChild(0).getSingleChild().getSingleChild().getName(), "3");

		assertEquals(d.getChild(1).getName(), JsonReader.ARRAY);
		assertEquals(d.getChild(1).getSingleChild().getName(), JsonReader.VALUE);
		assertEquals(d.getChild(1).getSingleChild().getSingleChild().getName(), "4");

		c = root.getChild(idx++);
		assertEquals(c.getName(), JsonReader.ARRAY);
		assertEquals(c.getChildren().size(), 1);
		assertEquals(c.getSingleChild().getName(), JsonReader.VALUE);
		assertEquals(c.getSingleChild().getSingleChild().getName(), "5");
	}

	@Test
	public void testObjectWithinArray() throws Exception {
		DataNode root = read("[1,{\"a\":2,\"b\":3},4]");

		assertEquals(root.getName(), JsonReader.ROOT);
		assertEquals(root.getChildren().size(), 3);

		int idx = 0;

		DataNode c = root.getChild(idx++);
		assertEquals(c.getName(), JsonReader.ARRAY);
		assertEquals(c.getChildren().size(), 1);
		assertEquals(c.getSingleChild().getName(), JsonReader.VALUE);
		assertTrue(c.getSingleChild().getData().isValueHolder());
		assertEquals(c.getSingleChild().getSingleChild().getName(), "1");

		c = root.getChild(idx++);
		assertEquals(c.getName(), JsonReader.ARRAY);
		assertEquals(c.getChildren().size(), 2);
		assertEquals(c.getChild("a").getSingleChild().getName(), "2");
		assertEquals(c.getChild("b").getSingleChild().getName(), "3");

		c = root.getChild(idx++);
		assertEquals(c.getName(), JsonReader.ARRAY);
		assertEquals(c.getChildren().size(), 1);
		assertEquals(c.getSingleChild().getName(), JsonReader.VALUE);
		assertEquals(c.getSingleChild().getSingleChild().getName(), "4");
	}

	private DataNode read(String json) throws Exception {
		DataNode root = JsonReader.read(json);
		LOG.info("json : \n" + JsonPrettyPrinter.format(json) + " \n" + DataNode.printElement(root));

		SchemaNode schemaRoot = SchemaExtractor.schema(root);
		LOG.info("schema: " + SchemaNode.printElement(schemaRoot));

		String jsonText = JsonWriter.write(root);
		LOG.info("written:\n" + JsonPrettyPrinter.format(jsonText));

		return root;
	}

}
