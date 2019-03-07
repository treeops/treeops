package org.treeops;

import java.io.File;
import java.util.List;

import org.treeops.transform.Transformation;

public interface FormatProcessor {
	void write(DataNode dataRoot, File file, List<Transformation> transformations) throws Exception;

	String write(DataNode dataRoot, List<Transformation> transformations) throws Exception;

	DataNode read(File file) throws Exception;

	DataNode read(String text) throws Exception;
}