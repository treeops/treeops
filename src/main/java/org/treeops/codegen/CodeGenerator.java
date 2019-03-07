package org.treeops.codegen;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.treeops.DataNode;
import org.treeops.SchemaNode;
import org.treeops.SchemaExtractor;
import org.treeops.transform.Transformation;
import org.treeops.types.CompositeType;
import org.treeops.types.EnumType;
import org.treeops.types.Type;
import org.treeops.types.TypeExtractor;
import org.treeops.types.TypeVariable;
import org.treeops.types.customization.Customization;
import org.treeops.types.customization.JoinMutuallyExclusiveCustomization;
import org.treeops.utils.Utils;
import org.treeops.utils.XStreamUtils;

public class CodeGenerator {
	private static final Logger LOG = LoggerFactory.getLogger(CodeGenerator.class);

	public static void generate(File outputDir, String packageName, DataNode root, List<Transformation> transformations) throws Exception {
		root = Transformation.runAll(transformations, root);
		LOG.info(DataNode.printElement(root));

		SchemaNode rootSchema = SchemaExtractor.schema(root);
		LOG.info("*********schema:" + SchemaNode.printElement(rootSchema));

		List<Type> types = TypeExtractor.extract(rootSchema);
		types = Customization.process(rootSchema, types, Customization.list(transformations));
		LOG.info("*********types:");
		for (Type t : types) {
			TypeExtractor.printType(t);
		}
		generate(outputDir, packageName, rootSchema, types, transformations);
	}

	public static void generate(File outputDir, String packageName, SchemaNode rootSchema, List<Type> types, List<Transformation> transformations) throws Exception {
		writeTransfromations(outputDir, packageName, transformations);
		generatePOJOs(outputDir, packageName, types);
		generateParser(outputDir, packageName, rootSchema, types, transformations);
	}

	private static void writeTransfromations(File outputDir, String packageName, List<Transformation> transformations) throws Exception {
		XStreamUtils.toFile(transformations, new File(ensurePackageDirCreated(outputDir, packageName), "transformations.xml"));
	}

	private static File ensurePackageDirCreated(File outputDir, String packageName) {
		File packageDir = packageDir(outputDir, packageName);
		packageDir.mkdirs();
		return packageDir;
	}

	private static void generateParser(File dir, String packageName, SchemaNode rootSchema, List<Type> types, List<Transformation> transformations) throws Exception {
		File packageDir = ensurePackageDirCreated(dir, packageName);
		Map<String, Object> objects = velocityContext(packageName);
		objects.put("types", types);
		objects.put("transformations", transformations);
		VelocityUtil.write(new File(packageDir, "Reader.java"), objects, "codegen/reader.vm");
	}

	private static Map<String, Object> velocityContext(String packageName) {
		Map<String, Object> objects = new HashMap<>();
		objects.put("codegen", new CodeGenerator());
		objects.put("packageName", packageName);
		return objects;
	}

	private static void generatePOJOs(File dir, String packageName, List<Type> types) throws Exception {
		for (Type type : types) {
			if (type instanceof EnumType) {
				writeEnum((EnumType) type, dir, packageName);
			} else if (type instanceof CompositeType) {
				writeComposite((CompositeType) type, dir, packageName, types);
			}
		}
	}

	private static void writeComposite(CompositeType type, File dir, String packageName, List<Type> types) throws Exception {
		File packageDir = ensurePackageDirCreated(dir, packageName);

		Map<String, Object> objects = velocityContext(packageName);
		objects.put("type", type);
		VelocityUtil.write(new File(packageDir, capitalizeFirst(type.getName()) + ".java"), objects, "codegen/composite.vm");
	}

	private static void writeEnum(EnumType type, File dir, String packageName) throws Exception {
		File packageDir = ensurePackageDirCreated(dir, packageName);

		Map<String, Object> objects = velocityContext(packageName);
		objects.put("type", type);

		VelocityUtil.write(new File(packageDir, capitalizeFirst(type.getName()) + ".java"), objects, "codegen/enum.vm");
	}

	public static String capitalizeFirst(String s) {
		if (s == null) {
			return null;
		}
		return s.substring(0, 1).toUpperCase() + s.substring(1);
	}

	public static String javaName(String s) {
		s = s.replaceAll(" ", "_");
		if (s.substring(0, 1).matches("[0-9]")) {
			s = "a" + s;
		}
		return s;
	}

	private static File packageDir(File dir, String packageName) {
		return new File(dir.getAbsolutePath() + "/" + packageName.replaceAll("\\.", "/"));
	}

	public static String typeMapping(String type) {
		return JavaTypeMapper.javaType(type);
	}

	public static List<CompositeType> compositeTypes(List<Type> types) {
		return CompositeType.stream(types).collect(Collectors.toList());
	}

	public static boolean isEnum(String typeName, List<Type> types) {
		Optional<Type> type = Type.find(types, typeName);
		return (type.isPresent() && (type.get() instanceof EnumType));
	}

	public static String packageToPath(String packageName) {
		return Utils.packageNameToPath(packageName);
	}

	public static List<TypeVariableData> allVariables(CompositeType type, List<Type> types, List<Transformation> transformations) {
		List<TypeVariable> list = findAllVariables(type, types);
		List<TypeVariableData> res = new ArrayList<>();

		List<JoinMutuallyExclusiveCustomization> joinTransfromations = transformations.stream().filter(t -> (t instanceof JoinMutuallyExclusiveCustomization))
				.map(t -> (JoinMutuallyExclusiveCustomization) t).filter(jt -> CompositeType.pathFound(type.getPaths(), jt.getPath())).collect(Collectors.toList());

		list.stream().forEach(v -> res.addAll(variableData(v, type, types, joinTransfromations)));
		return res;
	}

	private static List<TypeVariable> findAllVariables(CompositeType type, List<Type> types) {
		List<TypeVariable> list = new ArrayList<>(type.getVariables());
		if (type.getSuperType() != null) {
			CompositeType superType = CompositeType.findComposite(types, type.getSuperType());
			if (superType != null) {
				list.addAll(findAllVariables(superType, types));
			}
		}
		return list;
	}

	private static List<TypeVariableData> variableData(TypeVariable v, CompositeType type, List<Type> types, List<JoinMutuallyExclusiveCustomization> joinTransfromations) {
		List<TypeVariableData> res = new ArrayList<>();
		for (JoinMutuallyExclusiveCustomization j : joinTransfromations) {
			if (j.getNewName().equals(v.getName())) {
				for (String oldName : j.getNames()) {
					String oldType = j.getOldName2Type().get(oldName);
					res.add(new TypeVariableData(v, oldName, oldType));
				}
			}
		}
		if (res.isEmpty()) {
			res.add(new TypeVariableData(v, v.getName(), v.getType()));
		}
		return res;
	}

}
