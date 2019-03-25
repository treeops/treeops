package org.treeops.codegen;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;

public class VelocityUtil {

	public static String applyTemplate(Map<String, Object> objects, String templateName) {
		Writer writer = new StringWriter();
		write(objects, templateName, writer);
		return writer.toString();
	}

	private static void write(Map<String, Object> objects, String templateName, Writer writer) {
		VelocityEngine velocityEngine = new VelocityEngine();
		velocityEngine.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
		velocityEngine.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
		velocityEngine.init();

		Template t = velocityEngine.getTemplate(templateName);
		VelocityContext context = new VelocityContext();
		objects.forEach((k, v) -> context.put(k, v));

		t.merge(context, writer);
	}

	public static void write(File output, Map<String, Object> objects, String templateName) throws Exception {
		try (FileOutputStream fos = new FileOutputStream(output); OutputStreamWriter osw = new OutputStreamWriter(fos, StandardCharsets.UTF_8);) {
			write(objects, templateName, osw);
		}
	}

}
