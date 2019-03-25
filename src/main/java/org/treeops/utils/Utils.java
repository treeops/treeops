package org.treeops.utils;

import java.io.File;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Scanner;

public class Utils {

	public static final String SLASH = "/";

	public static File ensurePackageDirCreated(File outputDir, String packageName) {
		File packageDir = Utils.packageDir(outputDir, packageName);
		boolean created = packageDir.mkdirs();
		if ((!created) && (!packageDir.exists())) {
			throw new RuntimeException("unable to mkdirs " + packageDir.getAbsolutePath());
		}
		return packageDir;
	}

	public static File packageDir(File dir, String packageName) {
		return new File(dir.getAbsolutePath() + File.separator + packageName.replaceAll("\\.", SLASH));
	}

	public static String last(List<String> path) {
		return path.get(path.size() - 1);
	}

	public static List<String> withoutLast(List<String> path) {
		List<String> parentPath = new ArrayList<>(path);
		parentPath.remove(path.size() - 1);
		return parentPath;
	}

	public static String first(List<String> path) {
		return path.get(0);
	}

	public static List<String> withoutFirst(List<String> path) {
		List<String> parentPath = new ArrayList<>(path);
		parentPath.remove(0);
		return parentPath;
	}

	public static List<String> fromPath(String s) {
		return list(s.split(SLASH));
	}

	public static String spaces(int level) {
		StringBuilder s = new StringBuilder();
		for (int i = 0; i < level; i++) {
			s.append(" ");
		}
		return s.toString();
	}

	public static String tabs(int level) {
		return spaces(level).replaceAll(" ", "    ");
	}

	@SafeVarargs
	public static <T> T[] array(T... ts) {
		return ts;
	}

	@SafeVarargs
	public static <T> List<T> list(T... ts) {
		return Arrays.asList(ts);
	}

	public static boolean isWhiteSpaceOnly(String text) {
		if (text == null) {
			return true;
		}
		return text.replaceAll("\\s", "").trim().isEmpty();
	}

	public static void stringToFile(File file, String s) throws Exception {
		java.nio.file.Files.write(Paths.get(file.toURI()), s.getBytes("utf-8"), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
	}

	public static String text(File file) throws Exception {
		return new String(Files.readAllBytes(Paths.get(file.getAbsolutePath())));
	}

	public static List<String> tail(List<String> list, int startIdx) {
		return list.subList(startIdx, list.size());
	}

	public static String removeNewLines(String s) {
		return s.replaceAll("\\n", "").replaceAll("\\r", "");
	}

	public static String readClasspathFile(String path, Class<?> clazz) throws Exception {
		try (InputStream inputStream = clazz.getResourceAsStream(path); Scanner scanner = new Scanner(inputStream, StandardCharsets.UTF_8.name());) {
			return scanner.useDelimiter("\\A").next();
		}
	}

	public static List<File> readFiles(File root) {
		if (!root.exists()) {
			throw new RuntimeException("folder don't exists " + root.getAbsolutePath());
		}

		List<File> folders = new ArrayList<>();
		folders.add(root);

		List<File> result = new ArrayList<>();
		while (!folders.isEmpty()) {
			File folder = folders.remove(0);
			File[] folderFiles = folder.listFiles();
			if (folderFiles != null) {
				for (File file : folderFiles) {
					if (file.isDirectory()) {
						folders.add(file);
					} else {
						result.add(file);
					}
				}
			}
		}
		return result;
	}

	public static String packageNameToPath(String packageName) {
		return packageName.replaceAll("\\.", SLASH);
	}

	public static boolean sameText(String s1, String s2) {
		if (s1 == null) {
			return s2 == null;
		}
		if (s2 == null) {
			return false;
		}
		return s1.equals(s2);
	}

	public static String truncateText(Collection<String> values, int maxChars) {
		if (values.isEmpty()) {
			return "";
		}
		String res = "" + values;
		if (res.length() > maxChars) {
			return res.substring(0, maxChars);
		}
		return res;
	}
}
