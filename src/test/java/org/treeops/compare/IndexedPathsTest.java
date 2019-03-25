package org.treeops.compare;

import static org.junit.Assert.assertEquals;
import static org.treeops.compare.SampleDataOrder.createOrder;

import org.junit.Test;
import org.treeops.DataNode;
import org.treeops.SchemaExtractor;
import org.treeops.SchemaNode;

public class IndexedPathsTest {

	private DataNode r = createOrder();
	private SchemaNode schema = SchemaExtractor.schema(r);

	@Test
	public void testSame() throws Exception {

		assertEquals(indexedPath(r), "order");
		assertEquals(indexedPath(r.getChild("customer")), "order/customer");
		assertEquals(indexedPath(r.getChild("customer").getChild("name")), "order/customer/name");

		DataNode item = r.getChild("item", 0);

		assertEquals(indexedPath(item), "order/item[1]");
		assertEquals(indexedPath(item.getChild("productId")), "order/item[1]/productId");

		item = r.getChild("item", 1);

		assertEquals(indexedPath(item), "order/item[2]");
		assertEquals(indexedPath(item.getChild("productId")), "order/item[2]/productId");

	}

	private String indexedPath(DataNode n) {
		return schema.getIndexedPathToRoot(n);
	}
}
