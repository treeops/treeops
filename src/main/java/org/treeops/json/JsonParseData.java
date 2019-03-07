package org.treeops.json;

import org.treeops.ParseData;

public class JsonParseData extends ParseData {
	private JsonNodeType nodeType = JsonNodeType.DEFAULT;

	private String jsonLastName;

	public String getJsonLastName() {
		return jsonLastName;
	}

	public void setJsonLastName(String jsonLastName) {
		this.jsonLastName = jsonLastName;
	}

	public JsonNodeType getNodeType() {
		return nodeType;
	}

	public void setNodeType(JsonNodeType nodeType) {
		this.nodeType = nodeType;
	}

}
