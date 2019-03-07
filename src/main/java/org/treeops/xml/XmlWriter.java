package org.treeops.xml;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;

import org.treeops.DataNode;
import org.treeops.transform.Transformation;
import org.treeops.types.customization.XmlAttributeCustomization;

public class XmlWriter {

	public static String write(DataNode e) throws Exception {
		return write(e, Collections.emptyList());
	}

	public static void write(DataNode e, File file) throws Exception {
		write(e, file, Collections.emptyList());
	}

	public static String write(DataNode e, List<Transformation> transformations) throws Exception {
		StringWriter sw = new StringWriter();
		toXml(e, sw, xmlAttributesPaths(transformations));
		return sw.toString();
	}

	public static void write(DataNode e, File file, List<Transformation> transformations) throws Exception {
		try (FileWriter fw = new FileWriter(file); BufferedWriter bf = new BufferedWriter(fw);) {
			toXml(e, bf, xmlAttributesPaths(transformations));
		}
	}

	private static List<String> xmlAttributesPaths(List<Transformation> transformations) {
		return transformations.stream().filter(t -> (t instanceof XmlAttributeCustomization)).map(t -> String.join("/", ((XmlAttributeCustomization) t).getPath())).collect(Collectors.toList());
	}

	private static void toXml(DataNode e, Writer sw, List<String> xmlAttributesPaths) throws Exception {
		XMLStreamWriter writer = XMLOutputFactory.newInstance().createXMLStreamWriter(sw);
		writer.writeStartDocument();
		write(writer, e, xmlAttributesPaths);
		writer.writeEndDocument();
		writer.flush();
		writer.close();
	}

	private static void write(XMLStreamWriter writer, DataNode e, List<String> xmlAttributesPaths) throws Exception {
		writer.writeStartElement(escapeTag(e.getName()));

		List<String> attributeNames = new ArrayList<>();
		for (DataNode c : DataNode.children(e)) {
			if (xmlAttributesPaths.contains(c.getPathToRoot()) && c.getData().isValueHolder() && c.hasSingleChild()) {
				attributeNames.add(c.getName());
				writer.writeAttribute(c.getName(), c.getSingleChild().getName());
			}
		}

		for (DataNode c : DataNode.children(e)) {
			if (attributeNames.contains(c.getName())) {
				continue;
			}
			if (c.getData().isValueHolder()) {
				writer.writeStartElement(escapeTag(c.getName()));
				if (c.hasSingleChild()) {
					writer.writeCharacters(c.getSingleChild().getName());
				}
				writer.writeEndElement();
			} else {
				write(writer, c, xmlAttributesPaths);
			}

		}
		writer.writeEndElement();
	}

	private static String escapeTag(String name) {
		if (name.matches("[0-9].*")) {
			name = "a" + name;
		}
		return name.replaceAll("/", "-").replaceAll("[ \t]", "-");
	}

}
