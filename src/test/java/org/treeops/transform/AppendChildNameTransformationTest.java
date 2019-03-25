package org.treeops.transform;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.treeops.DataNode;
import org.treeops.types.customization.XmlAttributeCustomization;
import org.treeops.utils.Utils;
import org.treeops.xml.XmlPrettyPrinter;
import org.treeops.xml.XmlWriter;

public class AppendChildNameTransformationTest {

	private static final Logger LOG = LoggerFactory.getLogger(AppendChildNameTransformationTest.class);

	@Test
	public void testGroup() throws Exception {
		DataNode root = new DataNode("root");
		row(root, "A", 1);
		row(root, "B", 3);

		LOG.info("before: " + DataNode.printElement(root));
		debugOutputToFile(root);

		root = new AppendChildTransformation(Utils.list("root", "element"), Utils.list("subElement", "elementId"), true).transform(root);

		LOG.info("after: " + DataNode.printElement(root));

		assertEquals(root.getName(), "root");
		assertEquals(root.getChildren().size(), 2);

		assertEquals(root.getChild(0).getName(), "element_A");
		DataNode element = root.getChild(0);
		assertEquals(element.getChildren().size(), 2);
		DataNode subElement = element.getChild(0);
		assertEquals(subElement.getSingleChild().getName(), "id");
		assertEquals(subElement.getSingleChild().getSingleChild().getName(), "1");

		subElement = element.getChild(1);
		assertEquals(subElement.getSingleChild().getName(), "id");
		assertEquals(subElement.getSingleChild().getSingleChild().getName(), "2");

		assertEquals(root.getChild(1).getName(), "element_B");
		element = root.getChild(1);
		assertEquals(element.getChildren().size(), 2);
		subElement = element.getChild(0);
		assertEquals(subElement.getSingleChild().getName(), "id");
		assertEquals(subElement.getSingleChild().getSingleChild().getName(), "3");

		subElement = element.getChild(1);
		assertEquals(subElement.getSingleChild().getName(), "id");
		assertEquals(subElement.getSingleChild().getSingleChild().getName(), "4");

	}

	private void debugOutputToFile(DataNode root) throws Exception {
		File file = new File("build/appendChildNameElement.xml");

		XmlWriter.write(root, file,
				Utils.list(new XmlAttributeCustomization(Utils.list("root", "element", "subElement", "id")), new XmlAttributeCustomization(Utils.list("root", "element", "subElement", "elementId"))));
		XmlPrettyPrinter.format(file, file);
	}

	private void row(DataNode root, String group, int idx) {
		DataNode e = new DataNode(root, "element");
		element(e, group, idx);
		element(e, group, idx + 1);
	}

	private void element(DataNode e, String group, int idx) {
		DataNode s = new DataNode(e, "subElement");
		DataNode.valueNode(s, "id", "" + idx);
		DataNode.valueNode(s, "elementId", group);
	}

}
