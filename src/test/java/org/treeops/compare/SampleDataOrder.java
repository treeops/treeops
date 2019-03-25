package org.treeops.compare;

import org.treeops.DataNode;

public class SampleDataOrder {
	public static DataNode createOrder() {
		DataNode order = new DataNode("order");
		DataNode customer = new DataNode(order, "customer");
		DataNode.valueNode(customer, "name", "John");
		item("soap", "100", order);
		item("bread", "20", order);
		return order;
	}

	private static void item(String prod, String quantity, DataNode order) {
		DataNode item = new DataNode(order, "item");
		DataNode.valueNode(item, "productId", prod);
		DataNode.valueNode(item, "quantity", quantity);
	}

}
