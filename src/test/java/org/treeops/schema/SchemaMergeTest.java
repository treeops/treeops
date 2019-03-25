package org.treeops.schema;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.treeops.DataNode;
import org.treeops.SchemaExtractor;
import org.treeops.SchemaNode;
import org.treeops.compare.SampleDataOrder;

public class SchemaMergeTest {

	@Test
	public void testSchema() {
		DataNode r = SampleDataOrder.createOrder();
		SchemaNode schema = SchemaExtractor.schema(r);
		assertEquals(schema.getName(), "order");
		check(schema.getChild("item"), true, 2);
	}

	@Test
	public void testMerge() {
		DataNode r1 = SampleDataOrder.createOrder();
		DataNode r2 = SampleDataOrder.createOrder();
		SchemaNode schema = SchemaExtractor.mergeSchemas(r1, r2);
		assertEquals(schema.getName(), "order");
		check(schema.getChild("customer"), true, 1);
		check(schema.getChild("item"), true, 2);
		check(schema.getChild("item").getChild("productId"), true, 1);
	}

	@Test
	public void testMergeMandatory1() {
		DataNode r1 = SampleDataOrder.createOrder();
		DataNode r2 = SampleDataOrder.createOrder();
		r1.getChildren().remove(r1.getChild("customer"));
		SchemaNode schema = SchemaExtractor.mergeSchemas(r1, r2);

		assertEquals(schema.getName(), "order");

		check(schema.getChild("customer"), false, 1);
		check(schema.getChild("item"), true, 2);
		check(schema.getChild("item").getChild("productId"), true, 1);
	}

	@Test
	public void testMergeMandatory2() {
		DataNode r1 = SampleDataOrder.createOrder();
		DataNode r2 = SampleDataOrder.createOrder();
		r2.getChildren().remove(r2.getChild("customer"));
		SchemaNode schema = SchemaExtractor.mergeSchemas(r1, r2);

		assertEquals(schema.getName(), "order");

		check(schema.getChild("customer"), false, 1);
		check(schema.getChild("item"), true, 2);
		check(schema.getChild("item").getChild("productId"), true, 1);
	}

	@Test
	public void testMergeList1() {
		DataNode r1 = SampleDataOrder.createOrder();
		DataNode r2 = SampleDataOrder.createOrder();
		r1.getChildren().remove(r1.getChilds("item").get(0));

		SchemaNode schema = SchemaExtractor.mergeSchemas(r1, r2);
		assertEquals(schema.getName(), "order");

		check(schema.getChild("customer"), true, 1);
		check(schema.getChild("item"), true, 2);
		check(schema.getChild("item").getChild("productId"), true, 1);
	}

	@Test
	public void testMergeList2() {
		DataNode r1 = SampleDataOrder.createOrder();
		DataNode r2 = SampleDataOrder.createOrder();
		r2.getChildren().remove(r2.getChilds("item").get(0));

		SchemaNode schema = SchemaExtractor.mergeSchemas(r1, r2);
		assertEquals(schema.getName(), "order");

		check(schema.getChild("customer"), true, 1);
		check(schema.getChild("item"), true, 2);
		check(schema.getChild("item").getChild("productId"), true, 1);
	}

	private void check(SchemaNode n, boolean mandatory, int maxOccurs) {
		assertEquals(n.getData().isMandatory(), mandatory);
		assertEquals(n.getData().getMaxOccurs(), maxOccurs);
	}
}
