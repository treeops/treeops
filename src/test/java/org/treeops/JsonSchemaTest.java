package org.treeops;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.treeops.json.JsonReader;

public class JsonSchemaTest {
	private static final Logger LOG = LoggerFactory.getLogger(JsonSchemaTest.class);

	@Test
	public void testProperObject() throws Exception {

		SchemaNode schemaRoot = read("{\"obj\": {\"a\":1 , \"b\":[{\"id\":2, \"name\":\"Name2\"}] }}");
		assertEquals(schemaRoot.getName(), "Object");
		assertEquals(schemaRoot.getSingleChild().getName(), "obj");
		assertEquals(schemaRoot.getSingleChild().getChildren().get(0).getName(), "a");
		assertTrue(schemaRoot.getSingleChild().getChildren().get(0).getChildren().isEmpty());

		assertEquals(schemaRoot.getSingleChild().getChildren().get(0).getData().getValues().toString(), "[1]");

		assertEquals(schemaRoot.getSingleChild().getChildren().get(1).getName(), "b");

		assertEquals(schemaRoot.getSingleChild().getChildren().get(1).getChildren().get(0).getName(), "id");

		assertEquals(schemaRoot.getSingleChild().getChildren().get(1).getChildren().get(1).getName(), "name");
		assertEquals(schemaRoot.getSingleChild().getChildren().get(1).getChildren().get(1).getData().getValues().toString(), "[Name2]");
	}

	@Test
	public void testArray() {
		SchemaNode schemaRoot = read("[{\"a\":1, \"b\":2}, {\"a\":3,\"b\":4}]");
		assertEquals(schemaRoot.getName(), "root");
	}

	@Test
	public void testObject() {
		SchemaNode schemaRoot = read("{\"a\":1, \"b\":2}");
		assertEquals(schemaRoot.getName(), "Object");
	}

	@Test
	public void testNullObject() {
		SchemaNode schemaRoot = read("[{\"a\":{ \"b\":2}}, {\"a\":null}]");
		assertEquals(schemaRoot.getName(), "root");
	}

	@Test
	public void testSinleNullObject() {
		SchemaNode schemaRoot = read("[{\"a\":null}]");
		assertEquals(schemaRoot.getName(), "root");
	}

	private SchemaNode read(String json) {
		DataNode root = JsonReader.read(json);
		LOG.info("read " + DataNode.printElement(root));
		SchemaNode schemaRoot = SchemaExtractor.schema(root);
		LOG.info("schema: " + SchemaNode.printElement(schemaRoot));
		return schemaRoot;
	}

}
