package org.treeops;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class SchemaDataTest {

	@Test
	public void testMerge() {
		SchemaData d1 = new SchemaData();
		SchemaData d2 = new SchemaData();
		d1.setTotal(1);
		d2.setTotal(1);
		d1.merge(d2);

		assertEquals(d1.getMaxOccurs(), 0);
		assertEquals(d1.getTotal(), 2);
		assertTrue(d1.getValues().isEmpty());
		assertEquals(d1.isValueHolder(), false);

	}

	@Test
	public void testMergeMulti() {
		SchemaData d1 = new SchemaData();
		SchemaData d2 = new SchemaData();
		d1.setMaxOccurs(2);
		d1.setTotal(1);
		d2.setTotal(1);
		d1.merge(d2);

		assertEquals(d1.getMaxOccurs(), 2);
		assertEquals(d1.getTotal(), 2);
		assertTrue(d1.getValues().isEmpty());
		assertEquals(d1.isValueHolder(), false);
		assertTrue(d1.isMandatory());

	}

	@Test
	public void testMergeMulti2() {
		SchemaData d1 = new SchemaData();
		SchemaData d2 = new SchemaData();
		d2.setMaxOccurs(2);
		d1.setTotal(1);
		d2.setTotal(1);
		d1.merge(d2);

		assertEquals(d1.getMaxOccurs(), 2);
		assertEquals(d1.getTotal(), 2);
		assertTrue(d1.getValues().isEmpty());
		assertEquals(d1.isValueHolder(), false);
		assertTrue(d1.isMandatory());
	}

	@Test
	public void testMandatory1() {
		SchemaData d1 = new SchemaData();
		SchemaData d2 = new SchemaData();
		d1.setMaxOccurs(2);
		d1.setMandatory(false);
		d1.setTotal(1);
		d2.setTotal(1);
		d1.merge(d2);

		assertEquals(d1.getMaxOccurs(), 2);
		assertEquals(d1.getTotal(), 2);
		assertTrue(d1.getValues().isEmpty());
		assertEquals(d1.isValueHolder(), false);
		assertFalse(d1.isMandatory());
	}

	@Test
	public void testMandatory2() {
		SchemaData d1 = new SchemaData();
		SchemaData d2 = new SchemaData();
		d1.setMaxOccurs(2);
		d2.setMandatory(false);
		d1.setTotal(1);
		d2.setTotal(1);
		d1.merge(d2);

		assertEquals(d1.getMaxOccurs(), 2);
		assertEquals(d1.getTotal(), 2);
		assertTrue(d1.getValues().isEmpty());
		assertEquals(d1.isValueHolder(), false);
		assertFalse(d1.isMandatory());
	}

	@Test
	public void testMaxOccurs() {
		SchemaData d1 = new SchemaData();
		SchemaData d2 = new SchemaData();
		d1.setMaxOccurs(1);
		d2.setMaxOccurs(2);
		d1.merge(d2);

		assertEquals(d1.getMaxOccurs(), 2);
	}

	@Test
	public void testMaxOccurs2() {
		SchemaData d1 = new SchemaData();
		SchemaData d2 = new SchemaData();
		d1.setMaxOccurs(2);
		d2.setMaxOccurs(1);
		d1.merge(d2);
		assertEquals(d1.getMaxOccurs(), 2);
	}

}
