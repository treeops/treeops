package org.treeops.utils;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class UtilsTest {

	@Test
	public void testSameText() {
		assertTrue(Utils.sameText("s", "s"));
		assertFalse(Utils.sameText("s1", "s2"));

		assertFalse(Utils.sameText("s", null));
		assertFalse(Utils.sameText(null, "s"));
		assertTrue(Utils.sameText(null, null));
	}
}
