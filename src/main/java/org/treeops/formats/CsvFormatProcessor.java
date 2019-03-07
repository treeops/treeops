package org.treeops.formats;

import java.io.File;
import java.util.List;

import org.treeops.DataNode;
import org.treeops.FormatProcessor;
import org.treeops.csv.CsvReader;
import org.treeops.csv.CsvWriter;
import org.treeops.transform.Transformation;

public class CsvFormatProcessor implements FormatProcessor {

	@Override
	public void write(DataNode dataRoot, File file, List<Transformation> transformations) throws Exception {
		CsvWriter.write(dataRoot, file);
	}

	@Override
	public String write(DataNode dataRoot, List<Transformation> transformations) throws Exception {
		return CsvWriter.write(dataRoot);
	}

	@Override
	public DataNode read(File file) throws Exception {
		return CsvReader.read(file);
	}

	@Override
	public DataNode read(String text) throws Exception {
		return CsvReader.read(text);
	}

}
