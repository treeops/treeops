package org.treeops.json;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.json.Json;
import javax.json.stream.JsonParser;
import javax.json.stream.JsonParser.Event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.treeops.DataNode;

public class JsonReader {
	public static final String VALUE = "Value";
	public static final String OBJECT = "Object";
	public static final String ARRAY = "Array";
	public static final String ROOT = "root";

	private static final Logger LOG = LoggerFactory.getLogger(JsonReader.class);

	public static DataNode read(File file) throws Exception {
		return parse(createParser(file));
	}

	public static DataNode read(String s) throws Exception {
		return parse(createParser(s));
	}

	static class ParseResult {
		DataNode root = addNode(null, ROOT);
		Boolean firstArrayOrObject = null;

		void start(boolean array) {
			if (firstArrayOrObject == null) {
				firstArrayOrObject = array;
			}
		}
	}

	public static DataNode parse(JsonParser parser) {
		ParseResult parseResult = parseInternal(parser);
		if (LOG.isTraceEnabled()) {
			LOG.trace("parse result \n" + DataNode.printElement(parseResult.root));
		}

		processArrayNames(parseResult.root);
		if (LOG.isTraceEnabled()) {
			LOG.trace("after names \n" + DataNode.printElement(parseResult.root));
		}
		processRemoveArrays(parseResult.root);
		if (LOG.isTraceEnabled()) {
			LOG.trace("after remove array \n" + DataNode.printElement(parseResult.root));
		}
		if ((parseResult.firstArrayOrObject != null) && (parseResult.firstArrayOrObject.booleanValue() == false)) {
			DataNode newRoot = parseResult.root.getSingleChild();
			newRoot.setParent(null);
			return newRoot;
		} else {
			return parseResult.root;
		}

	}

	private static ParseResult parseInternal(JsonParser parser) {
		long elementCount = 0;
		ParseResult parseResult = new ParseResult();
		DataNode current = parseResult.root;

		while (parser.hasNext()) {
			final Event event = parser.next();

			elementCount++;
			if ((elementCount % 5000000) == 0) {
				LOG.info("processing " + elementCount);
			}
			//LOG.trace("" + event + " " + (((event == Event.KEY_NAME) || (event == Event.VALUE_NUMBER) || (event == Event.VALUE_STRING)) ? parser.getString() : "") + " " + current.getPathToRoot());
			switch (event) {

				case START_ARRAY : {
					parseResult.start(true);
					current = addNode(current, notNull(jsonData(current).getJsonLastName(), ARRAY));
					jsonData(current).setNodeType(JsonNodeType.JSONARRAY);
					break;
				}
				case END_ARRAY : {
					current = current.getParent();
					break;
				}
				case START_OBJECT : {
					parseResult.start(false);
					current = addNode(current, notNull(jsonData(current).getJsonLastName(), OBJECT));
					jsonData(current).setNodeType(JsonNodeType.JSONOBJECT);
					break;
				}
				case END_OBJECT : {
					current = current.getParent();
					break;
				}
				case KEY_NAME : {
					jsonData(current).setJsonLastName(parser.getString());
					break;
				}
				case VALUE_STRING : {
					current = value(current, parser.getString());
					break;
				}
				case VALUE_NUMBER : {
					current = value(current, parser.getString());
					break;
				}
				case VALUE_TRUE : {
					current = value(current, Boolean.TRUE.toString());
					break;
				}
				case VALUE_FALSE : {
					current = value(current, Boolean.FALSE.toString());
					break;
				}
				case VALUE_NULL : {
					current = value(current, null);
					break;
				}
			}
		}
		parser.close();
		return parseResult;
	}

	private static DataNode addNode(DataNode n, String text) {
		return new DataNode(n, text, new JsonParseData());
	}

	private static void processArrayNames(DataNode n) {
		List<DataNode> children = new ArrayList<>(DataNode.children(n));
		if (jsonData(n).getNodeType() == JsonNodeType.JSONARRAY) {
			for (DataNode c : children) {
				c.setName(n.getName());
			}
		}
		for (DataNode c : children) {
			processArrayNames(c);
		}
	}

	private static void processRemoveArrays(DataNode n) {
		List<DataNode> children = new ArrayList<>(DataNode.children(n));
		for (DataNode c : children) {
			processRemoveArrays(c);
		}

		if ((jsonData(n).getNodeType() == JsonNodeType.JSONARRAY) && (n.getParent() != null) && (jsonData(n.getParent()).getNodeType() != JsonNodeType.JSONARRAY)) {
			DataNode parent = n.getParent();

			int idx = parent.getChildren().indexOf(n);
			parent.getChildren().remove(n);

			for (DataNode c : children) {
				c.setParent(parent);
				parent.getChildren().add(idx++, c);
			}

		}

	}

	public static String notNull(String text, String defaultValue) {
		return text == null ? defaultValue : text;
	}

	private static DataNode value(DataNode current, String text) {
		DataNode currentToUse = current;
		String name = notNull(jsonData(current).getJsonLastName(), VALUE);

		if ((jsonData(current).getNodeType() == JsonNodeType.JSONARRAY) && (text != null)) {
			currentToUse = addNode(current, name);
			name = VALUE;
		}
		DataNode value = addNode(currentToUse, name);
		value.getData().setValueHolder(true);
		jsonData(value).setNodeType(JsonNodeType.JSONVALUE);
		if (text != null) {
			addNode(value, text);
		}

		return current;
	}

	protected static JsonParser createParser(File file) throws IOException {
		return Json.createParser(new StringReader(new String(Files.readAllBytes(Paths.get(file.getAbsolutePath())))));
	}

	protected static JsonParser createParser(String s) {
		return Json.createParser(new StringReader(s));
	}

	private static JsonParseData jsonData(DataNode n) {
		return (JsonParseData) n.getData();
	}

}
