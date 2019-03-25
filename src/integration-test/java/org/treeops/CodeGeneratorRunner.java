package org.treeops;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.treeops.codegen.CodeGenerator;
import org.treeops.transform.RenameTransformation;
import org.treeops.transform.Transformation;
import org.treeops.types.customization.ChangeTypeCustomization;
import org.treeops.types.customization.MakeEnumCustomization;
import org.treeops.utils.Utils;
import org.treeops.xml.XmlReader;

public class CodeGeneratorRunner {
	private static final Logger LOG = LoggerFactory.getLogger(CodeGeneratorRunner.class);

	public static void main(String[] args) throws Exception {
		LOG.info("started");
		CodeGenerator.generate(new File("build/generated"), "org.animals", XmlReader.read(new File("src/test/resources/books.xml")), bookTransformations());
		LOG.info("completed");
	}

	public static List<Transformation> bookTransformations() {
		ArrayList<Transformation> transformations = new ArrayList<>();

		transformations.add(new RenameTransformation(Utils.list("bookstore"), "books"));

		transformations.add(new MakeEnumCustomization(Utils.list("books", "book", "category"), "Category"));
		transformations.add(new ChangeTypeCustomization(Utils.list("books", "book", "year"), "integer"));
		transformations.add(new ChangeTypeCustomization(Utils.list("books", "book", "price"), "double"));

		return transformations;
	}

}
