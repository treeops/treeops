package org.treeops.utils;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Utils {

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
		return list(s.split("/"));
	}

	public static String spaces(int level) {
		String s = "";
		for (int i = 0; i < level; i++) {
			s += " ";
		}
		return s;
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
		try (InputStream inputStream = clazz.getResourceAsStream(path); Scanner scanner = new Scanner(inputStream, "UTF-8");) {
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
			for (File file : folder.listFiles()) {
				if (file.isDirectory()) {
					folders.add(file);
				} else {
					result.add(file);
				}
			}
		}
		return result;
	}

	public static String packageNameToPath(String packageName) {
		return packageName.replaceAll("\\.", "/");
	}

}
