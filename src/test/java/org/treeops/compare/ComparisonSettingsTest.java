package org.treeops.compare;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.treeops.utils.Utils;

public class ComparisonSettingsTest {

	@Test
	public void test() {
		ComparisonSettings s = new ComparisonSettings(Utils.list(Utils.list("a", "b", "c"), Utils.list("a", "b", "d")));
		assertTrue(s.isIgnored(Utils.list("a", "b", "c")));
		assertTrue(s.isIgnored(Utils.list("a", "b", "d")));
		assertFalse(s.isIgnored(Utils.list("a", "b", "c", "d")));
		assertFalse(s.isIgnored(Utils.list("a", "b")));

	}

}
