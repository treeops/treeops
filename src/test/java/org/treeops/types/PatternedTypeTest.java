package org.treeops.types;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class PatternedTypeTest {

	@Test
	public void testInteger() {

		assertTrue(match("0", PatternedType.INTEGER));
		assertTrue(match("1", PatternedType.INTEGER));
		assertTrue(match("123", PatternedType.INTEGER));
		assertTrue(match("-123", PatternedType.INTEGER));

		assertFalse(match("", PatternedType.INTEGER));
		assertFalse(match("x", PatternedType.INTEGER));
		assertFalse(match("a1", PatternedType.INTEGER));
		assertFalse(match("1a1", PatternedType.INTEGER));
		assertFalse(match("a1", PatternedType.INTEGER));
	}

	@Test
	public void testDouble() {
		assertTrue(match("0", PatternedType.DOUBLE));
		assertTrue(match("1", PatternedType.DOUBLE));
		assertTrue(match("123", PatternedType.DOUBLE));
		assertTrue(match("-123", PatternedType.DOUBLE));

		assertTrue(match("123.45", PatternedType.DOUBLE));
		assertTrue(match("-123.45", PatternedType.DOUBLE));

		assertTrue(match("123.0", PatternedType.DOUBLE));

		assertFalse(match("", PatternedType.DOUBLE));
		assertFalse(match("x", PatternedType.INTEGER));
		assertFalse(match("a1", PatternedType.INTEGER));
		assertFalse(match("1a1", PatternedType.INTEGER));
		assertFalse(match("a1", PatternedType.INTEGER));

	}

	private boolean match(String s, PatternedType t) {
		return s.matches(t.getRegexp());
	}

}
