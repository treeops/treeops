package org.treeops.types;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.treeops.DataNode;
import org.treeops.SchemaNode;
import org.treeops.SchemaExtractor;
import org.treeops.types.CompositeType;
import org.treeops.types.Type;
import org.treeops.types.TypeExtractor;

public class MergeTypesTest {
	private static final Logger LOG = LoggerFactory.getLogger(MergeTypesTest.class);

	@Test
	public void testTypes() throws Exception {

		DataNode order = new DataNode(null, "order");
		DataNode.valueNode(order, "quantity", "300");
		DataNode billing = new DataNode(order, "billing");
		DataNode.valueNode(billing, "method", "card");
		DataNode addressBilling = new DataNode(billing, "address");
		DataNode.valueNode(addressBilling, "line1", "Billing address line 1");
		DataNode.valueNode(addressBilling, "line2", "Billing address line 2");
		DataNode.valueNode(addressBilling, "town", "London");

		DataNode shipping = new DataNode(order, "shipping");
		DataNode.valueNode(shipping, "type", "train");
		DataNode addressShipping = new DataNode(shipping, "address");
		DataNode.valueNode(addressShipping, "line1", "Shipping address line 1");
		DataNode.valueNode(addressShipping, "line2", "Shipping address line 2");
		DataNode.valueNode(addressShipping, "town", "Bristol");
		DataNode.valueNode(addressShipping, "postCode", "B21 5GF");

		LOG.info(DataNode.printElement(order));

		SchemaNode rootSchema = SchemaExtractor.schema(order);

		List<Type> types = TypeExtractor.extract(rootSchema);
		for (Type type : types) {
			TypeExtractor.printType(type);
		}

		assertEquals(types.size(), 4);
		int i = 0;
		Type type = types.get(i++);
		CompositeType compositeType = (CompositeType) type;
		assertEquals(type.getName(), "order");
		assertEquals(compositeType.getVariables().size(), 3);

		int j = 0;
		assertEquals(compositeType.getVariables().get(j++).toString(), "quantity:integer");
		assertEquals(compositeType.getVariables().get(j++).toString(), "billing:billing");
		assertEquals(compositeType.getVariables().get(j++).toString(), "shipping:shipping");

		type = types.get(i++);
		compositeType = (CompositeType) type;
		assertEquals(type.getName(), "billing");
		j = 0;
		assertEquals(compositeType.getVariables().get(j++).toString(), "method:text");
		assertEquals(compositeType.getVariables().get(j++).toString(), "address:address");

		type = types.get(i++);
		compositeType = (CompositeType) type;
		assertEquals(type.getName(), "address");
		assertEquals(compositeType.getVariables().size(), 4);

		j = 0;
		assertEquals(compositeType.getVariables().get(j++).toString(), "line1:text");
		assertEquals(compositeType.getVariables().get(j++).toString(), "line2:text");
		assertEquals(compositeType.getVariables().get(j++).toString(), "town:text");
		assertEquals(compositeType.getVariables().get(j++).toString(), "postCode:text");

		type = types.get(i++);
		compositeType = (CompositeType) type;
		assertEquals(type.getName(), "shipping");
		j = 0;
		assertEquals(compositeType.getVariables().get(j++).toString(), "type:text");
		assertEquals(compositeType.getVariables().get(j++).toString(), "address:address");

	}

}
