package org.treeops.types;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.treeops.DataNode;
import org.treeops.SchemaExtractor;
import org.treeops.SchemaNode;
import org.treeops.transform.Transformation;
import org.treeops.types.customization.ChangeTypeCustomization;
import org.treeops.types.customization.Customization;
import org.treeops.types.customization.JoinMutuallyExclusiveCustomization;
import org.treeops.types.customization.MakeEnumCustomization;
import org.treeops.types.customization.MoveToSuperTypeCustomization;
import org.treeops.types.customization.SetSuperTypeCustomization;
import org.treeops.utils.Utils;

public class CustomizationTest {
	private static final Logger LOG = LoggerFactory.getLogger(CustomizationTest.class);

	private void add(DataNode root, String name, String sport, boolean dogOrCat, String petName, String petData, Integer age) {
		DataNode friend = new DataNode(root, "friend");
		DataNode.valueNode(friend, "name", name);
		DataNode.valueNode(friend, "age", age.toString());
		DataNode.valueNode(friend, "sport", sport);
		DataNode pet = new DataNode(friend, "pet");
		if (dogOrCat) {
			DataNode dog = new DataNode(pet, "dog");
			DataNode.valueNode(dog, "collarType", petData);
			DataNode.valueNode(dog, "name", petName);
		} else {
			DataNode cat = new DataNode(pet, "cat");
			DataNode.valueNode(cat, "favouriteCatFood", petData);
			DataNode.valueNode(cat, "name", petName);
		}
	}

	@Test
	public void testTypes() throws Exception {
		DataNode root = new DataNode(null, "friends");

		add(root, "Emma", "tennis", true, "Bear", "leather", 8);
		add(root, "Bob", "football", true, "Bella", "chain", 9);
		add(root, "Nick", "badminton", false, "Fluffy", "fish", 10);

		LOG.info(DataNode.printElement(root));
		SchemaNode rootSchema = SchemaExtractor.schema(root);

		List<Type> types = TypeExtractor.extract(rootSchema);
		LOG.info("*************************************** without customizations");
		for (Type type : types) {
			TypeExtractor.printType(type);
		}

		assertEquals(types.size(), 5);
		int i = 0;
		Type type = types.get(i++);
		CompositeType compositeType = (CompositeType) type;
		assertEquals(type.getName(), "friends");
		assertEquals(compositeType.getVariables().size(), 1);
		int j = 0;
		assertEquals(compositeType.getVariables().get(j++).toString(), "friend*:friend");

		type = types.get(i++);
		compositeType = (CompositeType) type;
		assertEquals(type.getName(), "friend");
		assertEquals(compositeType.getVariables().size(), 4);
		j = 0;
		assertEquals(compositeType.getVariables().get(j++).toString(), "name:text");
		assertEquals(compositeType.getVariables().get(j++).toString(), "age:integer");
		assertEquals(compositeType.getVariables().get(j++).toString(), "sport:text");
		assertEquals(compositeType.getVariables().get(j++).toString(), "pet:pet");

		type = types.get(i++);
		compositeType = (CompositeType) type;
		assertEquals(type.getName(), "pet");
		assertEquals(compositeType.getVariables().size(), 2);
		j = 0;
		assertEquals(compositeType.getVariables().get(j++).toString(), "dog?:dog");
		assertEquals(compositeType.getVariables().get(j++).toString(), "cat?:cat");

		type = types.get(i++);
		compositeType = (CompositeType) type;
		assertEquals(type.getName(), "dog");
		assertNull(compositeType.getSuperType());
		assertEquals(compositeType.getVariables().size(), 2);

		j = 0;
		assertEquals(compositeType.getVariables().get(j++).toString(), "collarType:text");
		assertEquals(compositeType.getVariables().get(j++).toString(), "name:text");

		type = types.get(i++);
		compositeType = (CompositeType) type;
		assertEquals(type.getName(), "cat");
		assertNull(compositeType.getSuperType());
		assertEquals(compositeType.getVariables().size(), 2);
		j = 0;
		assertEquals(compositeType.getVariables().get(j++).toString(), "favouriteCatFood:text");
		assertEquals(compositeType.getVariables().get(j++).toString(), "name:text");

		List<Transformation> transformations = new ArrayList<>();
		transformations.add(new MakeEnumCustomization(Utils.list("friends", "friend", "sport"), "SportEnum"));

		transformations.add(new ChangeTypeCustomization(Utils.list("friends", "friend", "age"), "integer"));

		transformations.add(new SetSuperTypeCustomization(Utils.list("friends", "friend", "pet", "dog"), "Animal"));
		transformations.add(new SetSuperTypeCustomization(Utils.list("friends", "friend", "pet", "cat"), "Animal"));

		transformations.add(new MoveToSuperTypeCustomization(Utils.list("friends", "friend", "pet", "dog", "name")));
		transformations.add(new MoveToSuperTypeCustomization(Utils.list("friends", "friend", "pet", "cat", "name")));

		transformations.add(new JoinMutuallyExclusiveCustomization(Utils.list("friends", "friend", "pet"), "pet", Utils.list("cat", "dog")));

		types = TypeExtractor.extract(rootSchema);
		types = Customization.process(rootSchema, types, Customization.list(transformations));

		LOG.info("*************************************** after applying customizations");
		for (Type t : types) {
			TypeExtractor.printType(t);
		}

		assertEquals(types.size(), 7);
		i = 0;
		type = types.get(i++);
		compositeType = (CompositeType) type;
		assertEquals(type.getName(), "friends");
		assertEquals(compositeType.getVariables().size(), 1);
		j = 0;
		assertEquals(compositeType.getVariables().get(j++).toString(), "friend*:friend");

		type = types.get(i++);
		compositeType = (CompositeType) type;
		assertEquals(type.getName(), "friend");
		assertEquals(compositeType.getVariables().size(), 4);
		j = 0;
		assertEquals(compositeType.getVariables().get(j++).toString(), "name:text");
		assertEquals(compositeType.getVariables().get(j++).toString(), "age:integer");

		assertEquals(compositeType.getVariables().get(j++).toString(), "sport:SportEnum");
		assertEquals(compositeType.getVariables().get(j++).toString(), "pet:pet");

		type = types.get(i++);
		compositeType = (CompositeType) type;
		assertEquals(type.getName(), "pet");
		assertEquals(compositeType.getVariables().size(), 1);
		j = 0;
		assertEquals(compositeType.getVariables().get(j++).toString(), "pet?:Animal");

		type = types.get(i++);
		compositeType = (CompositeType) type;
		assertEquals(type.getName(), "dog");
		assertEquals(compositeType.getSuperType(), "Animal");
		assertEquals(compositeType.getVariables().size(), 1);
		j = 0;
		assertEquals(compositeType.getVariables().get(j++).toString(), "collarType:text");

		type = types.get(i++);
		compositeType = (CompositeType) type;
		assertEquals(type.getName(), "cat");
		assertEquals(compositeType.getSuperType(), "Animal");

		assertEquals(compositeType.getVariables().size(), 1);
		j = 0;
		assertEquals(compositeType.getVariables().get(j++).toString(), "favouriteCatFood:text");

		type = types.get(i++);
		assertEquals(type.getName(), "SportEnum");
		EnumType enumType = (EnumType) type;
		assertEquals(enumType.getValues().toString(), "[badminton, football, tennis]");

		type = types.get(i++);
		compositeType = (CompositeType) type;
		assertEquals(type.getName(), "Animal");
		assertEquals(compositeType.getVariables().size(), 1);
		j = 0;
		assertEquals(compositeType.getVariables().get(j++).toString(), "name:text");
	}

}
