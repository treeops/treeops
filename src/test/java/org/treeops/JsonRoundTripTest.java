package org.treeops;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.treeops.DataNode;
import org.treeops.SchemaNode;
import org.treeops.SchemaExtractor;
import org.treeops.json.JsonPrettyPrinter;
import org.treeops.json.JsonReader;
import org.treeops.json.JsonWriter;
import org.treeops.utils.Utils;

public class JsonRoundTripTest {
	private static final Logger LOG = LoggerFactory.getLogger(JsonRoundTripTest.class);

	@Test
	public void testRoundTripArray() throws Exception {
		check("[{\"a\":1,\"b\":[{\"c\":11},{\"c\":12,\"d\":12}]}]", "{\"Array\":{\"a\":\"1\",\"b\":[{\"c\":\"11\"},{\"c\":\"12\",\"d\":\"12\"}]}}");

		check("[11,[21,[31,[41,[51,[61,[71,72],62],52],42],32],22],12]",
				"[{\"Value\":\"11\"},{\"Array\":[{\"Value\":\"21\"},{\"Array\":[{\"Value\":\"31\"},{\"Array\":[{\"Value\":\"41\"},{\"Array\":[{\"Value\":\"51\"},{\"Array\":[{\"Value\":\"61\"},{\"Array\":[{\"Value\":\"71\"},{\"Value\":\"72\"}]},{\"Value\":\"62\"}]},{\"Value\":\"52\"}]},{\"Value\":\"42\"}]},{\"Value\":\"32\"}]},{\"Value\":\"22\"}]},{\"Value\":\"12\"}]");
		check("[11,[21,[31,32],22],12]", "[{\"Value\":\"11\"},{\"Array\":[{\"Value\":\"21\"},{\"Array\":[{\"Value\":\"31\"},{\"Value\":\"32\"}]},{\"Value\":\"22\"}]},{\"Value\":\"12\"}]");
		check("[1,[2,[3]]]", "[{\"Value\":\"1\"},{\"Array\":[{\"Value\":\"2\"},{\"Array\":{\"Value\":\"3\"}}]}]");

		check("[{\"a\":1},[{\"a\":2},[{\"a\":3}]]]", "[{\"a\":\"1\"},{\"Array\":[{\"a\":\"2\"},{\"Array\":{\"a\":\"3\"}}]}]");

	}

	@Test
	public void testRoundTrip() throws Exception {
		check(Utils.text(new File("src/test/resources/books.json")),
				"{\"bookstore\":{\"book\":[{\"category\":\"cooking\",\"title\":{\"lang\":\"en\",\"text\":\" Everyday Italian \"},\"author\":\"Rocco Puccini\",\"year\":\"2005\",\"price\":\"30.00\"},{\"category\":\"children\",\"title\":{\"lang\":\"en\",\"text\":\"Harry Potter\"},\"author\":\"JK Rowling\",\"year\":\"2005\",\"price\":\"29.99\",\"optional\":\"text\"},{\"category\":\"web\",\"title\":\" Learn XML \",\"author\":\"Eirk T.Ray\",\"year\":\"2003\",\"price\":\"39.90\"}]}}");

		check("[1,[2,3]]", "[{\"Value\":\"1\"},{\"Array\":[{\"Value\":\"2\"},{\"Value\":\"3\"}]}]");
		check("[{\"name\":\"name1\", \"children\":[{\"child\":1},{\"child\":2}] }, {\"name\":\"name2\", \"children\":[{\"child\":3}] }]",
				"[{\"name\":\"name1\",\"children\":[{\"child\":\"1\"},{\"child\":\"2\"}]},{\"name\":\"name2\",\"children\":[{\"child\":\"3\"}]}]");
		check("{\"name\":\"name1\", \"children\":[{\"child\":1},{\"child\":2}] }", "{\"name\":\"name1\",\"children\":[{\"child\":\"1\"},{\"child\":\"2\"}]}");
		check("[]", "{}");
		check("{}", "{}");
		check("null", "{\"Value\":null}");
		check("5", "{\"Value\":\"5\"}");
		check("\"text\"", "{\"Value\":\"text\"}");
		check("[1,2,3]", "[{\"Value\":\"1\"},{\"Value\":\"2\"},{\"Value\":\"3\"}]");
		check("[1,[2,3]]", "[{\"Value\":\"1\"},{\"Array\":[{\"Value\":\"2\"},{\"Value\":\"3\"}]}]");

		check("[1,[2,[3,4]],5]", "[{\"Value\":\"1\"},{\"Array\":[{\"Value\":\"2\"},{\"Array\":[{\"Value\":\"3\"},{\"Value\":\"4\"}]}]},{\"Value\":\"5\"}]");

		check("[1,{\"a\":2,\"b\":3},4]", "[{\"Value\":\"1\"},{\"a\":\"2\",\"b\":\"3\"},{\"Value\":\"4\"}]");
		check("{\"a\":[1,2,{\"b\":true},3],\"c\":\"4\", \"d\":null, \"e\":{ \"name\" : \"Name\" }, \"ea\":[] }",
				"{\"a\":[{\"Value\":\"1\"},{\"Value\":\"2\"},{\"b\":\"true\"},{\"Value\":\"3\"}],\"c\":\"4\",\"d\":null,\"e\":{\"name\":\"Name\"}}");
		check("[{\"a\":1, \"b\":2}, {\"a\":3,\"b\":4}, null]", "[{\"a\":\"1\",\"b\":\"2\"},{\"a\":\"3\",\"b\":\"4\"},null]");
		check("[1,{\"a\":2,\"b\":3},4]", "[{\"Value\":\"1\"},{\"a\":\"2\",\"b\":\"3\"},{\"Value\":\"4\"}]");
	}

	private void check(String json, String output) throws Exception {
		String read = read(json);
		assertEquals(read, output);

		String read2 = r(read);
		String read3 = r(read2);
		if (!read2.equals(read3)) {
			LOG.info("*** read2 \n" + DataNode.printElement(JsonReader.read(read)));
			LOG.info("*** read3 \n" + DataNode.printElement(JsonReader.read(read2)));
		}
		assertEquals(read2, read3);
		if (!read.equals(r(read))) {
			LOG.info("*************** diff \n " + JsonPrettyPrinter.format(json) + "\n**********\n " + JsonPrettyPrinter.format(read) + "\n**********\n " + JsonPrettyPrinter.format(r(read)));
		}
		assertEquals(read, read(read));
	}

	private String read(String json) throws Exception {
		return read(json, true);
	}

	private String r(String json) throws Exception {
		return read(json, false);
	}

	private String read(String json, boolean print) throws Exception {
		DataNode root = JsonReader.read(json);
		if (print) {
			LOG.info("json : \n" + JsonPrettyPrinter.format(json) + " \n" + DataNode.printElement(root));
		}

		SchemaNode schemaRoot = SchemaExtractor.schema(root);
		if (print) {
			LOG.info("schema: " + SchemaNode.printElement(schemaRoot));
		}

		String jsonText = JsonWriter.write(root);
		if (print) {
			LOG.info("written:\n" + JsonPrettyPrinter.format(jsonText));
		}

		if (print) {
			LOG.info("short:\n" + jsonText.replaceAll("\"", "\\\\\""));
		}
		return jsonText;
	}

}
