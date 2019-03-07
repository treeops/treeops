package org.treeops.transform;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.treeops.DataNode;
import org.treeops.transform.MoveUpBecomeParentTransformation;
import org.treeops.utils.Utils;
import org.treeops.xml.XmlWriter;

public class MoveUpBecomeParentTransformationTest {

	private static final Logger LOG = LoggerFactory.getLogger(MoveUpBecomeParentTransformationTest.class);

	@Test
	public void test() throws Exception {

		DataNode root = new DataNode("root");

		for (int i = 0; i < 3; i++) {

			DataNode row = new DataNode(root, "row");
			DataNode.valueNode(row, "rowId", "" + (i + 1));

			for (int j = 0; j < 4; j++) {
				DataNode column = new DataNode(row, "column");
				DataNode.valueNode(column, "columnId", "row" + (i + 1) + "col" + (j + 1));

				for (int k = 0; k < 2; k++) {
					DataNode val = new DataNode(column, "value");
					DataNode.valueNode(val, "valId", "row" + (i + 1) + "col" + (j + 1) + "val" + (k + 1));
				}
			}
		}

		LOG.info("before: " + DataNode.printElement(root));
		XmlWriter.write(root, new File("build/moveUpBecomeParent.xml"));

		root = new MoveUpBecomeParentTransformation(Utils.list("root", "row", "column", "value")).transform(root);

		XmlWriter.write(root, new File("build/moveUpBecomeParent1.xml"));
		LOG.info("after1: " + DataNode.printElement(root));

		root = new MoveUpBecomeParentTransformation(Utils.list("root", "row", "value")).transform(root);

		XmlWriter.write(root, new File("build/moveUpBecomeParent2.xml"));
		LOG.info("after2: " + DataNode.printElement(root));

		assertEquals(root.getName(), "root");
		assertEquals(root.getChildren().size(), 24);
		int idx = -1;
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 4; j++) {
				for (int k = 0; k < 2; k++) {
					idx++;
					DataNode n = root.getChild(idx);
					assertEquals(n.getName(), "value");
					assertEquals(n.getChildren().size(), 3);
					assertEquals(n.getChild("row").getSingleChild().getName(), "rowId");
					assertEquals(n.getChild("row").getSingleChild().getSingleChild().getName(), "" + (i + 1));

					assertEquals(n.getChild("valId").getSingleChild().getName(), "row" + (i + 1) + "col" + (j + 1) + "val" + (k + 1));

					assertEquals(n.getChild("column").getSingleChild().getName(), "columnId");
					assertEquals(n.getChild("column").getSingleChild().getSingleChild().getName(), "row" + (i + 1) + "col" + (j + 1));

				}
			}
		}

	}
}
