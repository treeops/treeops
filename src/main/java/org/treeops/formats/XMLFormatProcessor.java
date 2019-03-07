package org.treeops.formats;

import java.io.File;
import java.util.List;

import org.treeops.DataNode;
import org.treeops.FormatProcessor;
import org.treeops.transform.Transformation;
import org.treeops.xml.XmlPrettyPrinter;
import org.treeops.xml.XmlReader;
import org.treeops.xml.XmlWriter;

public class XMLFormatProcessor implements FormatProcessor {

	@Override
	public String write(DataNode dataRoot, List<Transformation> transformations) throws Exception {
		return XmlPrettyPrinter.format(XmlWriter.write(dataRoot, transformations));
	}

	@Override
	public void write(DataNode dataRoot, File file, List<Transformation> transformations) throws Exception {
		XmlWriter.write(dataRoot, file, transformations);
		XmlPrettyPrinter.format(file, file);
	}

	@Override
	public DataNode read(String text) throws Exception {
		return XmlReader.read(text);
	}

	@Override
	public DataNode read(File file) throws Exception {
		return XmlReader.read(file);
	}
}