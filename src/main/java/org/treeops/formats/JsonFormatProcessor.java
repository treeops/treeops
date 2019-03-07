package org.treeops.formats;

import java.io.File;
import java.util.List;

import org.treeops.DataNode;
import org.treeops.FormatProcessor;
import org.treeops.json.JsonPrettyPrinter;
import org.treeops.json.JsonReader;
import org.treeops.json.JsonWriter;
import org.treeops.transform.Transformation;

public class JsonFormatProcessor implements FormatProcessor {

	@Override
	public void write(DataNode dataRoot, File file, List<Transformation> transformations) throws Exception {
		File temp = File.createTempFile(file.getName(), ".json");
		JsonWriter.write(dataRoot, temp);
		JsonPrettyPrinter.format(temp, file);
	}

	@Override
	public String write(DataNode dataRoot, List<Transformation> transformations) throws Exception {
		return JsonPrettyPrinter.format(JsonWriter.write(dataRoot));
	}

	@Override
	public DataNode read(File file) throws Exception {
		return JsonReader.read(file);
	}

	@Override
	public DataNode read(String text) throws Exception {
		return JsonReader.read(text);
	}

}
