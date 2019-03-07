package org.treeops.json;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import javax.json.Json;
import javax.json.JsonStructure;
import javax.json.JsonWriterFactory;
import javax.json.stream.JsonGenerator;

public class JsonPrettyPrinter {

	public static String format(JsonStructure obj) {
		StringWriter stringWriter = new StringWriter();
		javax.json.JsonWriter jsonWriter = factory().createWriter(stringWriter);
		jsonWriter.write(obj);
		jsonWriter.close();
		return stringWriter.toString();
	}

	public static void format(File input, File output) throws Exception {
		try (FileInputStream is = new FileInputStream(input);
				javax.json.JsonReader jr = javax.json.Json.createReader(is);
				FileOutputStream os = new FileOutputStream(output);
				javax.json.JsonWriter jsonWriter = factory().createWriter(os);) {
			JsonStructure j = jr.read();
			jsonWriter.write(j);
		}
	}

	private static JsonWriterFactory factory() {
		Map<String, Object> properties = new HashMap<>();
		properties.put(JsonGenerator.PRETTY_PRINTING, true);
		return Json.createWriterFactory(properties);
	}

	public static String format(String json) {
		try {
			return format(Json.createReader(new StringReader(json)).read());
		} catch (Exception ex) {
			return json;
		}
	}

}