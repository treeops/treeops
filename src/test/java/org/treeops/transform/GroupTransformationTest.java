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

public class GroupTransformationTest {

	private static final Logger LOG = LoggerFactory.getLogger(GroupTransformationTest.class);

	@Test
	public void testGroup() throws Exception {
		DataNode root = new DataNode("root");

		row(root, "A", "1");
		row(root, "A", "2");
		row(root, "B", "3");

		LOG.info("before: " + DataNode.printElement(root));
		File file = new File("build/group.xml");

		XmlWriter.write(root, file, Utils.list(new XmlAttributeCustomization(Utils.list("root", "row", "id")), new XmlAttributeCustomization(Utils.list("root", "row", "group"))));
		XmlPrettyPrinter.format(file, file);

		assertEquals(root.getName(), "root");
		assertEquals(root.getChildren().size(), 3);
		int i = 0;
		assertEquals(root.getChild(i).getName(), "row");
		assertEquals(root.getChild(i).getChild(0).getName(), "id");
		assertEquals(root.getChild(i).getChild(0).getSingleChild().getName(), "1");
		assertEquals(root.getChild(i).getChild(1).getName(), "group");
		assertEquals(root.getChild(i).getChild(1).getSingleChild().getName(), "A");

		i++;
		assertEquals(root.getChild(i).getName(), "row");
		assertEquals(root.getChild(i).getChild(0).getName(), "id");
		assertEquals(root.getChild(i).getChild(0).getSingleChild().getName(), "2");
		assertEquals(root.getChild(i).getChild(1).getName(), "group");
		assertEquals(root.getChild(i).getChild(1).getSingleChild().getName(), "A");

		i++;
		assertEquals(root.getChild(i).getName(), "row");
		assertEquals(root.getChild(i).getChild(0).getName(), "id");
		assertEquals(root.getChild(i).getChild(0).getSingleChild().getName(), "3");
		assertEquals(root.getChild(i).getChild(1).getName(), "group");
		assertEquals(root.getChild(i).getChild(1).getSingleChild().getName(), "B");

		root = new GroupTransformation(Utils.list("root", "row"), Utils.list("group"), "Group").transform(root);

		LOG.info("after: " + DataNode.printElement(root));
		assertEquals(root.getName(), "root");
		assertEquals(root.getChildren().size(), 2);

		assertEquals(root.getChild(0).getName(), "Group");
		assertEquals(root.getChild(0).getChildren().size(), 2);
		assertEquals(root.getChild(0).getChild(0).getName(), "row");
		assertEquals(root.getChild(0).getChild(0).getChild(0).getName(), "id");
		assertEquals(root.getChild(0).getChild(0).getChild(0).getSingleChild().getName(), "1");

		assertEquals(root.getChild(0).getChild(1).getName(), "row");
		assertEquals(root.getChild(0).getChild(1).getChild(0).getName(), "id");
		assertEquals(root.getChild(0).getChild(1).getChild(0).getSingleChild().getName(), "2");

		assertEquals(root.getChild(1).getName(), "Group");
		assertEquals(root.getChild(1).getChildren().size(), 1);
		assertEquals(root.getChild(1).getChild(0).getName(), "row");
		assertEquals(root.getChild(1).getChild(0).getChild(0).getName(), "id");
		assertEquals(root.getChild(1).getChild(0).getChild(0).getSingleChild().getName(), "3");

	}

	private void row(DataNode root, String group, String id) {
		DataNode r = new DataNode(root, "row");
		new DataNode(new DataNode(r, "id"), id);
		new DataNode(new DataNode(r, "group"), group);
		r.getChild("id").getData().setValueHolder(true);
		r.getChild("group").getData().setValueHolder(true);

	}
}
