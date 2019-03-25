package org.treeops.xml;

import org.treeops.ParseData;

public class XmlParseData extends ParseData {

	private XmlNodeType nodeType = XmlNodeType.DEFAULT;

	//used during parsing to store the collected text content spread around
	private String tempText;

	public XmlNodeType getNodeType() {
		return nodeType;
	}

	public void setNodeType(XmlNodeType nodeType) {
		this.nodeType = nodeType;
	}

	public String getTempText() {
		return tempText;
	}

	public void setTempText(String tempText) {
		this.tempText = tempText;
	}

}
