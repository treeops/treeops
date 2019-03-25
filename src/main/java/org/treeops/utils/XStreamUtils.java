package org.treeops.utils;

import java.io.File;
import java.io.FileOutputStream;

import com.thoughtworks.xstream.XStream;

public class XStreamUtils {

	public static void toFile(Object obj, File file) throws Exception {
		try (FileOutputStream fos = new FileOutputStream(file);) {
			xstreamInstance().toXML(obj, fos);
		}
	}

	private static XStream xstreamInstance() {
		XStream x = new XStream();
		x.allowTypesByRegExp(new String[]{".*"});
		return x;
	}

	@SuppressWarnings("unchecked")
	public static <T> T readFile(File file) {
		return (T) xstreamInstance().fromXML(file);
	}

	@SuppressWarnings("unchecked")
	public static <T> T read(String xml) {
		return (T) xstreamInstance().fromXML(xml);
	}

}
