package org.treeops.compare;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.treeops.DataNode;

import cucumber.api.java8.En;

public class CompareFeatures implements En {
	private static final Logger LOG = LoggerFactory.getLogger(CompareFeatures.class);

	public CompareFeatures() {
		When("two trees are compared", (io.cucumber.datatable.DataTable dataTable) -> dataTable.asMaps().forEach(row -> run(new CompareEntry(row))));
		Then("the result as described", () -> {
		});

	}

	private void run(CompareEntry e) {

		LOG.info("case " + e + "...");
		ComparisonSettings settings = new ComparisonSettings(new ArrayList<>());
		if (e.ignored != null) {
			settings.getIgnoredPaths().add(Collections.singletonList(e.ignored));
		}

		DataNode n1 = node(e.leftValue, e.left);
		DataNode n2 = node(e.rightValue, e.right);

		ComparisonResult res = Compararer.compare(n1, n2, settings);

		assertEquals(res.getLeftValue(), e.resultLeft);
		assertEquals(res.getRightValue(), e.resultRight);
		LOG.info("case " + e + "success");
	}

	private DataNode node(String val, String name) {

		if (val != null) {
			return DataNode.valueNode(null, name, val);
		} else {
			return new DataNode(null, name);
		}

	}

	static class CompareEntry {
		private String left;
		private String leftValue;
		private String right;
		private String rightValue;
		private String resultLeft;
		private String resultRight;
		private String ignored;

		public CompareEntry(Map<String, String> map) {
			super();
			left = map.get("left");
			leftValue = map.get("left value");
			right = map.get("right");
			rightValue = map.get("right value");
			resultLeft = map.get("result left");
			resultRight = map.get("result right");
			ignored = map.get("ignored");
		}

		@Override
		public String toString() {
			return "CompareEntry [left=" + left + ", leftValue=" + leftValue + ", right=" + right + ", rightValue=" + rightValue + ", resultLeft=" + resultLeft + ", resultRight=" + resultRight
					+ ", ignored=" + ignored + "]";
		}

	}
}
