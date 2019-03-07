package org.treeops.formats;

public class TextInput extends Input {
	private String text;

	public TextInput(Format format, String text) {
		super(format);
		this.text = text;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	@Override
	public String toString() {
		return "TextInput [text=" + text + "]";
	}

	@Override
	public String description() {
		return "Text";
	}

}