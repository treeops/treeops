package org.treeops.json;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

import javax.json.Json;
import javax.json.stream.JsonGenerator;

import org.treeops.DataNode;
import org.treeops.SchemaExtractor;
import org.treeops.SchemaNode;

public class JsonWriter {

	public static String write(DataNode root) throws Exception {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		JsonGenerator jsonGenerator = Json.createGenerator(os);
		writeRoot(root, jsonGenerator);
		return os.toString("UTF-8");
	}

	public static void write(DataNode root, File outputFile) throws Exception {
		try (FileOutputStream os = new FileOutputStream(outputFile);) {
			JsonGenerator jsonGenerator = Json.createGenerator(os);
			writeRoot(root, jsonGenerator);
		}
	}

	private static void writeRoot(DataNode root, JsonGenerator jsonGenerator) {
		SchemaNode schema = SchemaExtractor.schema(root);
		if ((schema.getChildren().size() == 1) && (schema.getSingleChild().getData().getMaxOccurs() == 1) && root.getData().isValueHolder()) {
			jsonGenerator.write(root.getSingleChild().getName());
		} else if ((schema.getChildren().size() == 1) && (schema.getSingleChild().getData().getMaxOccurs() > 1)) {
			jsonGenerator.writeStartArray();
			writeArrayElements(jsonGenerator, schema, root.getChildren());
			jsonGenerator.writeEnd();

		} else {
			jsonGenerator.writeStartObject();
			writeChildren(jsonGenerator, root, schema);
			jsonGenerator.writeEnd();
		}
		jsonGenerator.close();
	}

	private static void writeChildren(JsonGenerator jsonGenerator, DataNode node, SchemaNode rootSchema) {
		SchemaNode schema = rootSchema.find(node.getPath());
		SchemaNode.children(schema).forEach(sc -> writeChild(jsonGenerator, node, rootSchema, sc));
	}

	private static void writeChild(JsonGenerator jsonGenerator, DataNode node, SchemaNode rootSchema, SchemaNode sc) {
		if (sc.getData().isList()) {
			writeListChild(jsonGenerator, node, rootSchema, sc);
		} else {
			writeNonListChild(jsonGenerator, node, rootSchema, sc);
		}
	}

	private static void writeNonListChild(JsonGenerator jsonGenerator, DataNode node, SchemaNode rootSchema, SchemaNode sc) {
		DataNode c = node.getChild(sc.getName());
		if (c != null) {
			String name = c.getName();
			if (c.getData().isValueHolder()) {
				if (c.hasSingleChild()) {
					jsonGenerator.write(name, c.getSingleChild().getName());
				} else {
					jsonGenerator.writeNull(name);
				}
			} else if (c.getChildren().isEmpty()) {
				jsonGenerator.writeStartObject(name).writeEnd();
			} else if (c.hasSingleChild() && (c.getSingleChild().getChildren().isEmpty())) {
				jsonGenerator.write(name, c.getSingleChild().getName());
			} else {
				jsonGenerator.writeStartObject(name);
				writeChildren(jsonGenerator, c, rootSchema);
				jsonGenerator.writeEnd();
			}

		}
	}

	private static void writeListChild(JsonGenerator jsonGenerator, DataNode node, SchemaNode rootSchema, SchemaNode sc) {
		List<DataNode> childs = node.getChilds(sc.getName());
		if (childs.isEmpty() && !sc.getData().isMandatory()) {
			//ignored
		} else {
			jsonGenerator.writeStartArray(sc.getName());
			writeArrayElements(jsonGenerator, rootSchema, childs);
			jsonGenerator.writeEnd();
		}
	}

	private static void writeArrayElements(JsonGenerator jsonGenerator, SchemaNode rootSchema, List<DataNode> childs) {
		for (DataNode c : childs) {

			if (c.getData().isValueHolder()) {
				if (c.hasSingleChild()) {
					jsonGenerator.write(c.getSingleChild().getName());
				} else {
					jsonGenerator.writeNull();
				}
			} else if (c.getChildren().isEmpty()) {
				jsonGenerator.writeStartObject().writeEnd();
			} else if (c.hasSingleChild() && (c.getSingleChild().getChildren().isEmpty())) {
				jsonGenerator.write(c.getSingleChild().getName());
			} else {
				jsonGenerator.writeStartObject();
				writeChildren(jsonGenerator, c, rootSchema);
				jsonGenerator.writeEnd();
			}
		}
	}

}
