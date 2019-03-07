package org.treeops.formats;

import org.treeops.FormatProcessor;

public enum Format {
	XML(new XMLFormatProcessor()),

	JSON(new JsonFormatProcessor()),

	CSV(new CsvFormatProcessor());

	private Format(FormatProcessor p) {
		this.processor = p;
	}

	private final FormatProcessor processor;

	public FormatProcessor getProcessor() {
		return processor;
	}

}
