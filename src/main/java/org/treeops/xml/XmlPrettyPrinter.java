package org.treeops.xml;

import java.io.File;
import java.io.FileInputStream;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.treeops.utils.StopWatch;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class XmlPrettyPrinter {

	private static final Logger LOG = LoggerFactory.getLogger(XmlPrettyPrinter.class);

	public static void format(File inputFile, File outputFile) throws Exception {
		try (FileInputStream fis = new FileInputStream(inputFile);) {
			format(new InputSource(fis), new StreamResult(outputFile));
		}
	}

	public static String format(String xml) throws Exception {
		StringWriter sw = new StringWriter();
		format(new InputSource(new StringReader(xml)), new StreamResult(sw));
		return sw.toString();
	}

	public static void format(InputSource inputSource, StreamResult output) throws Exception {
		StopWatch sw = new StopWatch();
		Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(inputSource);
		LOG.info("reading into DOM " + sw.elapsedTime());
		normalizeDom(doc);
		LOG.info("normalization took  " + sw.elapsedTime());
		write(output, doc);
		LOG.info("formatted in " + sw.elapsedTime());
	}

	private static void normalizeDom(Document doc) throws Exception {
		doc.normalize();
		XPath xPath = XPathFactory.newInstance().newXPath();
		NodeList nodeList = (NodeList) xPath.evaluate("//text()[normalize-space()='']", doc, XPathConstants.NODESET);
		for (int i = 0; i < nodeList.getLength(); ++i) {
			org.w3c.dom.Node node = nodeList.item(i);
			node.getParentNode().removeChild(node);
		}
	}

	private static void write(StreamResult output, Document doc) throws Exception {
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		transformerFactory.setAttribute("indent-number", 4);
		Transformer transformer = transformerFactory.newTransformer();
		transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", Integer.toString(4));
		transformer.transform(new DOMSource(doc), output);
	}
}
