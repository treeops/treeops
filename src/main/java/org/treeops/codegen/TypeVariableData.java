package org.treeops.codegen;

import org.treeops.types.TypeVariable;

public class TypeVariableData extends TypeVariable {
	private String oldName;
	private String oldType;

	public TypeVariableData(TypeVariable v, String oldName, String oldType) {
		super();
		copyFrom(v);
		this.oldName = oldName;
		this.oldType = oldType;
	}

	public String getOldName() {
		return oldName;
	}

	public String getOldType() {
		return oldType;
	}

}
