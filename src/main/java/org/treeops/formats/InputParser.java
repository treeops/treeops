package org.treeops.formats;

import java.io.File;
import java.util.List;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.treeops.DataNode;
import org.treeops.utils.Utils;

public class InputParser {
	private static final Logger LOG = LoggerFactory.getLogger(InputParser.class);

	public static DataNode parse(Input input) throws Exception {
		if (input instanceof FileInput) {
			return parseFileOrDir((FileInput) input);
		} else {
			TextInput input1 = (TextInput) input;
			return input1.getFormat().getProcessor().read(input1.getText());
		}
	}

	public static DataNode parseFileOrDir(FileInput input) throws Exception {
		if (input.getFile().isDirectory()) {
			return parseDir(input.getFormat(), input.getFile());
		} else {
			return input.getFormat().getProcessor().read(input.getFile());
		}
	}

	private static DataNode parseDir(Format format, File dir) {
		LOG.debug("reading dir " + dir);
		List<File> files = Utils.readFiles(dir);
		LOG.debug("found " + files.size());
		return parseFiles(format, dir, files.stream().filter(f -> f.getName().toUpperCase().endsWith(format.name().toUpperCase())));
	}

	public static DataNode parseFiles(Format format, File dir, Stream<File> files) {
		DataNode root = new DataNode("Files");
		files.forEach(file -> {
			DataNode fileNode = new DataNode(root, "File");
			DataNode.valueNode(fileNode, "fileName", file.getAbsolutePath().substring(dir.getAbsolutePath().length()));
			addFile(fileNode, file, format);
		});
		return root;
	}

	private static void addFile(DataNode fileNode, File file, Format format) {
		try {
			DataNode node = parseFileOrDir(new FileInput(format, file));
			node.addToParent(fileNode);
		} catch (Exception ex) {
			LOG.error("Error " + file + " " + ex, ex);
			DataNode.valueNode(fileNode, "Error", "Error " + ex);
		}
	}

}
