package org.treeops;

import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.treeops.codegen.CodeGenerator;
import org.treeops.transform.Transformation;
import org.treeops.types.customization.ChangeTypeCustomization;
import org.treeops.types.customization.JoinMutuallyExclusiveCustomization;
import org.treeops.types.customization.MakeEnumCustomization;
import org.treeops.types.customization.MoveToSuperTypeCustomization;
import org.treeops.types.customization.SetSuperTypeCustomization;
import org.treeops.types.customization.XmlAttributeCustomization;
import org.treeops.utils.Utils;
import org.treeops.xml.XmlPrettyPrinter;
import org.treeops.xml.XmlWriter;

public class CodeGenTest {
	private static final Logger LOG = LoggerFactory.getLogger(CodeGenTest.class);

	@Test
	public void test() throws Exception {
		LOG.info("******************* code gen test");

		File tempFile = File.createTempFile("tempDir", "");
		File classesDir = new File(tempFile.getAbsolutePath() + ".classes");
		assertTrue(classesDir.mkdir());
		File srcDir = new File(tempFile.getAbsolutePath() + ".src");
		srcDir.mkdir();
		LOG.info(" classes " + classesDir + " src: " + srcDir);
		String packageName = "org.animals3";
		DataNode root = friends();
		File xmlFile = new File(srcDir, "friends.xml");
		XmlWriter.write(root, xmlFile);
		List<Transformation> transformations = transformations();
		CodeGenerator.generate(srcDir, packageName, root, transformations);
		String srcDirWithPackage = srcDir.getAbsolutePath() + File.separator + packageName.replaceAll("\\.", File.separator + File.separator) + File.separator;
		writeTestClass(new File(srcDirWithPackage + File.separator + "CheckTest.java"), packageName);
		assertTrue(runProcess("javac  -d " + classesDir.getAbsolutePath() + " -cp " + System.getProperty("java.class.path") + " " + srcDirWithPackage + "*.java"));
		assertTrue(runProcess("java  -cp " + System.getProperty("java.class.path") + File.pathSeparator + classesDir.getAbsolutePath() + " -Dfriends.xml.file=" + xmlFile.getAbsolutePath()
				+ " org.junit.runner.JUnitCore " + packageName + ".CheckTest"));
	}

	private void writeTestClass(File file, String packageName) throws Exception {
		Utils.stringToFile(file, "package " + packageName + ";" + Utils.readClasspathFile("/codegen/AnimalCheckTest.template", CodeGenTest.class));
	}

	private static void printLines(String cmd, InputStream ins) throws Exception {
		String line = null;
		BufferedReader in = new BufferedReader(new InputStreamReader(ins));
		while ((line = in.readLine()) != null) {
			LOG.info(cmd + " " + line);
		}
	}

	private static boolean runProcess(String command) throws Exception {
		Process p = Runtime.getRuntime().exec(command);
		printLines("stdout:", p.getInputStream());
		printLines("stderr:", p.getErrorStream());
		p.waitFor();
		LOG.info("exitValue():" + p.exitValue());
		return p.exitValue() == 0;
	}

	private List<Transformation> transformations() {
		List<Transformation> transformations = new ArrayList<>();
		transformations.add(new MakeEnumCustomization(Utils.list("friends", "friend", "sport"), "SportEnum"));

		transformations.add(new ChangeTypeCustomization(Utils.list("friends", "friend", "age"), "integer"));

		transformations.add(new SetSuperTypeCustomization(Utils.list("friends", "friend", "pet", "dog"), "Animal"));
		transformations.add(new SetSuperTypeCustomization(Utils.list("friends", "friend", "pet", "cat"), "Animal"));

		transformations.add(new MoveToSuperTypeCustomization(Utils.list("friends", "friend", "pet", "dog", "name")));
		transformations.add(new MoveToSuperTypeCustomization(Utils.list("friends", "friend", "pet", "cat", "name")));

		transformations.add(new JoinMutuallyExclusiveCustomization(Utils.list("friends", "friend", "pet"), "pet", Utils.list("cat", "dog")));
		return transformations;
	}

	public static DataNode friends() throws Exception {
		DataNode root = new DataNode(null, "friends");

		add(root, "Emma", "tennis", true, "Bear", "leather", 8);
		add(root, "Bob", "football", true, "Bella", "chain", 9);
		add(root, "Nick", "badminton", false, "Fluffy", "fish", 10);

		LOG.info(DataNode.printElement(root));

		List<Transformation> transformations = new ArrayList<>();
		transformations.add(new XmlAttributeCustomization(Utils.list("friends", "friend", "name")));
		transformations.add(new XmlAttributeCustomization(Utils.list("friends", "friend", "age")));
		transformations.add(new XmlAttributeCustomization(Utils.list("friends", "friend", "sport")));
		transformations.add(new XmlAttributeCustomization(Utils.list("friends", "friend", "pet", "dog", "name")));
		transformations.add(new XmlAttributeCustomization(Utils.list("friends", "friend", "pet", "dog", "collarType")));
		transformations.add(new XmlAttributeCustomization(Utils.list("friends", "friend", "pet", "cat", "name")));
		transformations.add(new XmlAttributeCustomization(Utils.list("friends", "friend", "pet", "cat", "favouriteCatFood")));

		File file = new File("build/friends.xml");
		XmlWriter.write(root, file, transformations);
		XmlPrettyPrinter.format(file, file);
		return root;
	}

	private static void add(DataNode root, String name, String sport, boolean dogOrCat, String petName, String petData, Integer age) {
		DataNode friend = new DataNode(root, "friend");
		DataNode.valueNode(friend, "name", name);
		DataNode.valueNode(friend, "age", age.toString());
		DataNode.valueNode(friend, "sport", sport);
		DataNode pet = new DataNode(friend, "pet");
		if (dogOrCat) {
			DataNode dog = new DataNode(pet, "dog");
			DataNode.valueNode(dog, "collarType", petData);
			DataNode.valueNode(dog, "name", petName);
		} else {
			DataNode cat = new DataNode(pet, "cat");
			DataNode.valueNode(cat, "favouriteCatFood", petData);
			DataNode.valueNode(cat, "name", petName);
		}
	}

}