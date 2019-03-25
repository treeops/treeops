package org.treeops.compare;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.treeops.compare.SampleDataOrder.createOrder;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.treeops.DataNode;
import org.treeops.types.customization.XmlAttributeCustomization;
import org.treeops.utils.Utils;
import org.treeops.xml.XmlPrettyPrinter;
import org.treeops.xml.XmlWriter;

public class CompareComplexTest {
	private static final Logger LOG = LoggerFactory.getLogger(CompareComplexTest.class);

	@Test
	public void testSame() throws Exception {
		DataNode r1 = createOrder();
		DataNode r2 = createOrder();
		writeToFile(r1, "order");

		ComparisonSettings settings = new ComparisonSettings(new ArrayList<>());
		ComparisonResult res = Compararer.compare(r1, r2, settings);

		assertTrue(res.getData().isSameNodeAndChildren());
	}

	@Test
	public void testDiffName() throws Exception {
		DataNode r1 = createOrder();
		DataNode r2 = createOrder();
		r2.getChild("customer").getChild("name").getSingleChild().setName("George");
		writeToFile(r2, "orderNameGeorge");

		ComparisonSettings settings = new ComparisonSettings(new ArrayList<>());
		ComparisonResult res = Compararer.compare(r1, r2, settings);

		assertTrue(res.getChild("item").getChild("productId").getChildren().isEmpty());

		assertFalse(res.getData().isSameNodeAndChildren());
		assertEquals(res.getChilds("item").size(), 2);
		assertTrue(res.getChilds("item").get(0).getData().isSameNodeAndChildren());
		assertTrue(res.getChilds("item").get(1).getData().isSameNodeAndChildren());

		assertFalse(res.getChild("customer").getData().isSameNodeAndChildren());
		ComparisonResult nameResult = res.getChild("customer").getChild("name");

		assertFalse(nameResult.getData().isSameNodeAndChildren());
		assertTrue(nameResult.getChildren().isEmpty());
	}

	@Test
	public void testIgnore() throws Exception {
		DataNode r1 = createOrder();
		DataNode r2 = createOrder();
		r2.getChild("customer").getChild("name").getSingleChild().setName("George");
		writeToFile(r2, "orderNameGeorge");

		List<List<String>> ignore = new ArrayList<>();
		ignore.add(Utils.list("order", "customer", "name"));
		ComparisonSettings settings = new ComparisonSettings(ignore);

		ComparisonResult res = Compararer.compare(r1, r2, settings);

		assertTrue(res.getChild("item").getChild("productId").getChildren().isEmpty());

		assertTrue(res.getData().isSameNodeAndChildren());
		assertFalse(res.getData().isIgnored());
		assertEquals(res.getChilds("item").size(), 2);
		assertTrue(res.getChilds("item").get(0).getData().isSameNodeAndChildren());
		assertTrue(res.getChilds("item").get(1).getData().isSameNodeAndChildren());

		assertFalse(res.getChild("customer").getData().isIgnored());
		assertTrue(res.getChild("customer").getData().isSameNodeAndChildren());

		assertTrue(res.getChild("customer").getChild("name").getData().isIgnored());
		assertFalse(res.getChild("customer").getChild("name").getData().isSameNodeAndChildren());

		ComparisonResult nameResult = res.getChild("customer").getChild("name");

		assertFalse(nameResult.getData().isSameNodeAndChildren());
		assertTrue(nameResult.getChildren().isEmpty());
	}

	@Test
	public void testMissingNameLeft() throws Exception {
		DataNode r1 = createOrder();
		DataNode r2 = createOrder();
		r2.getChild("customer").removeChildren("name");
		writeToFile(r1, "order1");
		writeToFile(r2, "order2");

		ComparisonSettings settings = new ComparisonSettings(new ArrayList<>());
		ComparisonResult res = Compararer.compare(r1, r2, settings);
		LOG.info(ComparisonResult.printElement(res));
		assertTrue(res.getChild("item").getChild("productId").getChildren().isEmpty());

		assertFalse(res.getData().isSameNodeAndChildren());
		assertEquals(res.getChilds("item").size(), 2);
		assertTrue(res.getChilds("item").get(0).getData().isSameNodeAndChildren());
		assertTrue(res.getChilds("item").get(1).getData().isSameNodeAndChildren());

		assertFalse(res.getChild("customer").getData().isSameNodeAndChildren());
		ComparisonResult nameResult = res.getChild("customer").getChild("name");

		assertFalse(nameResult.getData().isSameNodeAndChildren());
		assertTrue(nameResult.getChildren().isEmpty());

		assertEquals(nameResult.getData().getN1().getPathToRoot(), "order/customer/name");
		assertNull(nameResult.getData().getN2());
	}

	@Test
	public void testMissingNameRight() throws Exception {
		DataNode r1 = createOrder();
		DataNode r2 = createOrder();
		r1.getChild("customer").removeChildren("name");
		writeToFile(r1, "order1right");
		writeToFile(r2, "order2right");

		ComparisonSettings settings = new ComparisonSettings(new ArrayList<>());
		ComparisonResult res = Compararer.compare(r1, r2, settings);
		LOG.info(ComparisonResult.printElement(res));
		assertTrue(res.getChild("item").getChild("productId").getChildren().isEmpty());

		assertFalse(res.getData().isSameNodeAndChildren());
		assertEquals(res.getChilds("item").size(), 2);
		assertTrue(res.getChilds("item").get(0).getData().isSameNodeAndChildren());
		assertTrue(res.getChilds("item").get(1).getData().isSameNodeAndChildren());

		assertFalse(res.getChild("customer").getData().isSameNodeAndChildren());
		ComparisonResult nameResult = res.getChild("customer").getChild("name");

		assertFalse(nameResult.getData().isSameNodeAndChildren());
		assertTrue(nameResult.getChildren().isEmpty());

		assertEquals(nameResult.getData().getN2().getPathToRoot(), "order/customer/name");
		assertNull(nameResult.getData().getN1());
	}

	@Test
	public void testMissingItem() throws Exception {
		DataNode r1 = createOrder();
		DataNode r2 = createOrder();
		r2.getChild("customer").removeChildren("name");
		r2.getChildren().remove(r2.getChilds("item").get(1));
		r2.getChilds("item").get(0).getChild("quantity").getSingleChild().setName("30");

		writeToFile(r1, "order1item");
		writeToFile(r2, "order2item");

		ComparisonSettings settings = new ComparisonSettings(new ArrayList<>());
		ComparisonResult res = Compararer.compare(r1, r2, settings);
		LOG.info(ComparisonResult.printElement(res));
		assertTrue(res.getChild("item").getChild("productId").getChildren().isEmpty());

		assertFalse(res.getData().isSameNodeAndChildren());
		assertEquals(res.getChilds("item").size(), 2);
		assertFalse(res.getChilds("item").get(0).getData().isSameNodeAndChildren());
		assertFalse(res.getChilds("item").get(1).getData().isSameNodeAndChildren());

		assertFalse(res.getChild("customer").getData().isSameNodeAndChildren());
		ComparisonResult nameResult = res.getChild("customer").getChild("name");

		assertFalse(nameResult.getData().isSameNodeAndChildren());
		assertTrue(nameResult.getChildren().isEmpty());

		assertEquals(nameResult.getData().getN1().getPathToRoot(), "order/customer/name");
		assertNull(nameResult.getData().getN2());
	}

	private void writeToFile(DataNode order, String name) throws Exception {
		File file = new File("build/" + name + ".xml");
		XmlWriter.write(order, file, Utils.list(new XmlAttributeCustomization(Utils.list("order", "customer", "name")), new XmlAttributeCustomization(Utils.list("order", "item", "productId")),
				new XmlAttributeCustomization(Utils.list("order", "item", "quantity"))));
		XmlPrettyPrinter.format(file, file);
	}

}
