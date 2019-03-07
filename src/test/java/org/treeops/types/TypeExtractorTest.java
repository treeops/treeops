package org.treeops.types;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.List;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.treeops.DataNode;
import org.treeops.SchemaData;
import org.treeops.SchemaNode;
import org.treeops.SchemaExtractor;
import org.treeops.types.AtomicType;
import org.treeops.types.CompositeType;
import org.treeops.types.PatternedType;
import org.treeops.types.Type;
import org.treeops.types.TypeExtractor;
import org.treeops.types.TypeVariable;
import org.treeops.utils.Utils;
import org.treeops.xml.XmlReader;

public class TypeExtractorTest {
	private static final Logger LOG = LoggerFactory.getLogger(TypeExtractorTest.class);

	@Test
	public void testTypes() throws Exception {
		DataNode r = XmlReader.read(new File("src/test/resources/books.xml"));
		LOG.info(DataNode.printElement(r));
		assertEquals(r.getName(), "bookstore");
		assertEquals(r.getChildren().size(), 3);
		assertEquals(r.getChild(0).getName(), "book");

		SchemaNode rootSchema = SchemaExtractor.schema(r);

		List<Type> types = TypeExtractor.extract(rootSchema);
		for (Type type : types) {
			TypeExtractor.printType(type);
		}
		assertEquals(types.size(), 3);
		int i = 0;
		Type type = types.get(i++);
		CompositeType compositeType = (CompositeType) type;
		assertEquals(type.getName(), "bookstore");
		assertEquals(compositeType.getVariables().size(), 1);
		TypeVariable typeVariable = compositeType.getVariables().get(0);
		assertEquals(typeVariable.getName(), "book");
		assertTrue(typeVariable.isCollection());
		assertTrue(typeVariable.isMandatory());
		assertEquals(typeVariable.getType(), "book");

		type = types.get(i++);
		compositeType = (CompositeType) type;
		assertEquals(type.getName(), "book");
		assertEquals(compositeType.getVariables().size(), 6);

		int j = 0;
		assertEquals(compositeType.getVariables().get(j++).toString(), "category:text");
		assertEquals(compositeType.getVariables().get(j++).toString(), "title:title");
		assertEquals(compositeType.getVariables().get(j++).toString(), "author:text");
		assertEquals(compositeType.getVariables().get(j++).toString(), "year:integer");
		assertEquals(compositeType.getVariables().get(j++).toString(), "price:double");
		assertEquals(compositeType.getVariables().get(j++).toString(), "optional?:text");

		type = types.get(i++);
		compositeType = (CompositeType) type;
		assertEquals(type.getName(), "title");

	}

	@Test
	public void testAtomicTypes() {
		assertEquals(type("1", "2"), PatternedType.INTEGER);
		assertEquals(type("1"), PatternedType.INTEGER);

		assertEquals(type("1.0"), PatternedType.DOUBLE);
		assertEquals(type("1", "1.5"), PatternedType.DOUBLE);
		assertEquals(type("1", "1.5", "a"), PatternedType.TEXT);

		assertEquals(type("x", "1.5"), AtomicType.TEXT);

		assertEquals(type(), AtomicType.TEXT);
		assertEquals(type("1", "x"), AtomicType.TEXT);

	}

	private static AtomicType type(String... vals) {
		SchemaNode n = new SchemaNode(null, "a", new SchemaData());
		n.getData().getValues().addAll(Utils.list(vals));
		return TypeExtractor.getAtomicType(n);
	}

}
