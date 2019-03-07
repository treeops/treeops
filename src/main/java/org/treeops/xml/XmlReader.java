package org.treeops.xml;

import java.io.File;
import java.io.FileInputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.treeops.DataNode;
import org.treeops.GenericNode;
import org.treeops.SchemaExtractor;
import org.treeops.SchemaNode;
import org.treeops.utils.Utils;

public class XmlReader {
	private static final Logger LOG = LoggerFactory.getLogger(XmlReader.class);
	public static final String TEXT_NAME = "_text_";

	public static DataNode read(String xml) throws Exception {
		return process(readInternal(xml));
	}

	public static DataNode read(File file) throws Exception {
		return process(rawReadFile(file));
	}

	public static DataNode readInternal(String xml) throws Exception {
		if (LOG.isTraceEnabled()) {
			LOG.trace("reading xml " + xml);
		}
		File tempFile = File.createTempFile("temp", ".xml");
		tempFile.deleteOnExit();
		Files.write(Paths.get(tempFile.getAbsolutePath()), xml.getBytes(Charset.forName("UTF-8")));
		return rawReadFile(tempFile);
	}

	public static DataNode rawReadFile(File file) throws Exception {
		try (FileInputStream fis = new FileInputStream(file)) {
			return readTree(XMLInputFactory.newFactory().createXMLEventReader(fis));
		}
	}

	public static DataNode process(DataNode root) {
		return processText(root, SchemaExtractor.schema(root));
	}

	private static DataNode processText(DataNode n, SchemaNode rootSchema) {
		SchemaNode schema = rootSchema.find(n.getPath());
		if ((n.getChildren().size() == 1) && (schema.getChildren().size() == 1) && (n.getSingleChild().getName() == XmlReader.TEXT_NAME)) {
			n.getData().setValueHolder(true);
			DataNode textNode = n.getSingleChild();
			n.getChildren().clear();
			if (textNode.getChildren().size() == 1) {
				n.getChildren().add(textNode.getSingleChild());
				textNode.getSingleChild().setParent(n);
			}
		} else {
			for (DataNode c : GenericNode.children(n)) {
				processText(c, rootSchema);
			}
		}
		return n;
	}

	private static DataNode readTree(XMLEventReader reader) throws Exception {
		List<DataNode> result = new ArrayList<>();

		long elementCount = 0;
		DataNode current = null;
		while (reader.hasNext()) {
			XMLEvent event = reader.nextEvent();

			elementCount++;
			if ((elementCount % 5000000) == 0) {
				LOG.info("processing " + elementCount);
			}

			if (event.isStartElement()) {
				StartElement startNode = event.asStartElement();
				String name = startNode.getName().getLocalPart();

				current = addNode(current, name);
				//xmlData(current).setNodeLocation(new XmlNodeLocation(startNode.getLocation().getLineNumber(), startNode.getLocation().getColumnNumber(), startNode.getLocation().getCharacterOffset()));

				xmlData(current).setTempText("");//why do we need it????

				@SuppressWarnings("unchecked")
				Iterator<Attribute> attribs = startNode.getAttributes();
				while (attribs.hasNext()) {
					Attribute attribute = attribs.next();
					String attributeName = name(attribute.getName());
					addAttribute(current, attributeName, attribute.getValue());
				}

			} else if (event.isEndElement()) {
				if (current != null) {
					String text = xmlData(current).getTempText();
					if ((text != null) && (text.length() > 0)) {
						setValueText(current, text);
					}
				}
				if (current.getParent() == null) {
					result.add(current);
					//current.setTempText(null);//TODO:?? should it be for alll???
				}
				current = current.getParent();

			} else if (event.isCharacters()) {
				String text = event.asCharacters().getData();
				if (!Utils.isWhiteSpaceOnly(text)) {
					xmlData(current).setTempText(xmlData(current).getTempText() + text);//TODO: text separator???
				} else {
					if ((text != null) && (text.trim().length() > 0)) {
						LOG.warn("current element is not present, text is ignored " + text);
					}
				}
			}

		}
		LOG.trace("completed");
		return result.get(0);
	}

	private static void addAttribute(DataNode current, String attributeName, String value) {
		addText(current, value, attributeName, XmlNodeType.ATTR, XmlNodeType.ATTR_VALUE);
	}

	private static void setValueText(DataNode current, String text) {
		addText(current, text, TEXT_NAME, XmlNodeType.TEXT, XmlNodeType.TEXT_VALUE);
	}

	private static void addText(DataNode current, String text, String name, XmlNodeType nameNodeType, XmlNodeType textNodeType) {
		DataNode textNode = addNode(current, name);
		textNode.getData().setValueHolder(true);
		xmlData(textNode).setNodeType(nameNodeType);
		DataNode valueNode = addNode(textNode, text);
		xmlData(valueNode).setNodeType(textNodeType);
	}

	private static DataNode addNode(DataNode parent, String name) {
		return new DataNode(parent, name, new XmlParseData());
	}

	private static XmlParseData xmlData(DataNode n) {
		return (XmlParseData) n.getData();
	}

	private static String name(QName name) {
		if (name.getNamespaceURI().equals(XMLConstants.NULL_NS_URI)) {
			return name.getLocalPart();
		}
		return name.getPrefix() + "__" + name.getLocalPart();//TODO: namespaces??? name.getNamespaceURI
	}

}
