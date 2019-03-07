package org.treeops;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.treeops.json.JsonWriter;
import org.treeops.utils.StopWatch;
import org.treeops.xml.XmlPrettyPrinter;
import org.treeops.xml.XmlWriter;

/** creates potentially large XML containing file names on disk */
public class DiskTree {
	private static final Logger LOG = LoggerFactory.getLogger(DiskTree.class);

	public static void main(String[] args) throws Exception {

		File folder = new File("c:\\");

		LOG.info("started " + folder);
		StopWatch sw = new StopWatch();
		DataNode root = new DataNode("root");

		readFiles(root, folder);
		LOG.info("read in " + sw.elapsedTime() + " sec");

		JsonWriter.write(root, new File("c:\\temp\\out.json"));
		LOG.info("exported json in " + sw.elapsedTime() + " sec");

		XmlWriter.write(root, new File("c:\\temp\\out.xml"));
		XmlPrettyPrinter.format(new File("c:\\temp\\out.xml"), new File("c:\\temp\\out.xml"));
		LOG.info("exported xml in " + sw.elapsedTime() + " sec");

	}

	private static void readFiles(DataNode n, File folder) {
		try {
			for (File file : folder.listFiles()) {

				if (file.isDirectory()) {
					DataNode dir = new DataNode(n, "Dir");
					DataNode.valueNode(dir, "name", file.getName());
					readFiles(dir, file);
				} else {
					DataNode.valueNode(n, "File", file.getName());
				}

			}
		} catch (Exception ex) {
			//LOG.error("skipping " + folder + " :" + ex);
		}
	}

}
