package org.treeops;

import java.util.Set;
import java.util.TreeSet;

public class SchemaData {
	private int maxOccurs;
	private boolean mandatory = true;
	private int total;
	private Set<String> values = new TreeSet<>();
	private boolean valueHolder;

	public void merge(SchemaData other) {
		maxOccurs = Math.max(maxOccurs, other.maxOccurs);
		if (!other.mandatory) {
			mandatory = false;
		}
		total += other.total;
		values.addAll(other.values);
		valueHolder = other.valueHolder;
	}

	@Override
	public String toString() {
		return "SchemaData [maxOccurs=" + maxOccurs + ", mandatory=" + mandatory + ", total=" + total + ", values=" + values + ", valueHolder=" + valueHolder + "]";
	}

	public boolean isList() {
		return getMaxOccurs() > 1;
	}

	public int getMaxOccurs() {
		return maxOccurs;
	}

	public boolean isValueHolder() {
		return valueHolder;
	}

	public void setValueHolder(boolean valueHolder) {
		this.valueHolder = valueHolder;
	}

	public void setMaxOccurs(int maxOccurs) {
		this.maxOccurs = maxOccurs;
	}

	public boolean isMandatory() {
		return mandatory;
	}

	public void setMandatory(boolean mandatory) {
		this.mandatory = mandatory;
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public Set<String> getValues() {
		return values;
	}

	public void setValues(Set<String> values) {
		this.values = values;
	}

}
