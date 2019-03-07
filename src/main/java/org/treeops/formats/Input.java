package org.treeops.formats;

public abstract class Input {
	private Format format;

	public Format getFormat() {
		return format;
	}

	public void setFormat(Format format) {
		this.format = format;
	}

	public Input(Format format) {
		super();
		this.format = format;
	}

	public abstract String description();

}
