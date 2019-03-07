package org.treeops.codegen;

import org.treeops.types.AtomicType;
import org.treeops.types.PatternedType;

public class JavaTypeMapper {

	static String javaType(String type) {
		if (AtomicType.TEXT.getName().equals(type)) {
			return "String";
		}
	
		if (PatternedType.INTEGER.getName().equals(type)) {
			return "Integer";
		}
	
		if (PatternedType.DOUBLE.getName().equals(type)) {
			return "Double";
		}
		return CodeGenerator.capitalizeFirst(type);
	}

}
