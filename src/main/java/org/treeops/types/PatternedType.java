package org.treeops.types;

import java.util.List;

import org.treeops.utils.Utils;

public class PatternedType extends AtomicType {

	private String regexp;

	public PatternedType(String name, String regexp) {
		super(name);
		this.regexp = regexp;
	}

	public String getRegexp() {
		return regexp;
	}

	public static PatternedType INTEGER = new PatternedType("integer", "^\\-?[0-9]+$");
	public static PatternedType DOUBLE = new PatternedType("double", "^\\-?[0-9]+(\\.[0-9]*)?$");
	//TODO: decimal/boolean/date

	public static List<PatternedType> list() {
		return Utils.list(INTEGER, DOUBLE);
	}
}
