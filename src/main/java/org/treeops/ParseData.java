package org.treeops;

public class ParseData {
	private boolean valueHolder;

	public boolean isValueHolder() {
		return valueHolder;
	}

	public void setValueHolder(boolean valueHolder) {
		this.valueHolder = valueHolder;
	}

	public ParseData(boolean valueHolder) {
		super();
		this.valueHolder = valueHolder;
	}

	public ParseData() {
		super();
	}

	public ParseData copy() {
		return new ParseData(isValueHolder());
	}

	@Override
	public String toString() {
		return valueHolder ? "valueHolder" : "notV";
	}

}
