package org.treeops.formats;

import java.io.File;

public class FileInput extends Input {
	private File file;

	public FileInput(Format format, File file) {
		super(format);
		this.file = file;
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	@Override
	public String toString() {
		return "FileInput " + " " + file;
	}

	@Override
	public String description() {
		return "File " + file;
	}

}
