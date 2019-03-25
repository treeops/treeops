package org.treeops.types;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.treeops.SchemaNode;

public class TypeExtractor {
	private static final Logger LOG = LoggerFactory.getLogger(TypeExtractor.class);

	public static List<Type> extract(SchemaNode rootSchema) {
		List<Type> types = new ArrayList<>();
		process(rootSchema, types);
		types = mergeSimilarTypes(types);
		return types;
	}

	private static Type process(SchemaNode schemaNode, List<Type> types) {
		if (schemaNode.getData().isValueHolder()) {
			return getAtomicType(schemaNode);
		} else {
			CompositeType type = new CompositeType(schemaNode.getName());
			types.add(type);
			type.getPaths().add(schemaNode.getPath());
			for (SchemaNode c : SchemaNode.children(schemaNode)) {
				TypeVariable var = new TypeVariable(c.getName());
				var.setMandatory(c.getData().isMandatory());
				var.setCollection(c.getData().getMaxOccurs() > 1);
				var.getPaths().add(c.getPath());
				if (c.getData().isValueHolder()) {
					var.setType(getAtomicType(c).getName());
				} else {
					var.setType(process(c, types).getName());
				}
				type.getVariables().add(var);
			}
			return type;
		}
	}

	public static AtomicType getAtomicType(SchemaNode c) {

		List<PatternedType> patternedTypes = PatternedType.list();

		Map<PatternedType, Boolean> canBeMap = new HashMap<>();
		patternedTypes.forEach(t -> canBeMap.put(t, true));

		c.getData().getValues().forEach(v -> {
			for (PatternedType t : patternedTypes) {
				if (!v.matches(t.getRegexp())) {
					canBeMap.put(t, false);
				}

			}
		});

		if (!c.getData().getValues().isEmpty()) {

			for (PatternedType t : patternedTypes) {
				if (canBeMap.get(t)) {
					return t;
				}
			}
		}

		return AtomicType.TEXT;
	}

	private static List<Type> mergeSimilarTypes(List<Type> types) {
		Map<String, Type> name2type = new HashMap<>();
		List<Type> res = new ArrayList<>();
		for (Type type : types) {
			Type another = name2type.get(type.getName());
			if (another == null) {
				name2type.put(type.getName(), type);
				res.add(type);
			} else if ((type instanceof CompositeType) && (another instanceof CompositeType)) {
				merge((CompositeType) type, (CompositeType) another);
			}
		}
		return res;
	}

	private static void merge(CompositeType type, CompositeType another) {
		another.getPaths().addAll(type.getPaths());

		for (TypeVariable v : type.getVariables()) {
			TypeVariable anotherVariable = another.getVariable(v.getName());
			if (anotherVariable == null) {
				another.getVariables().add(v);
			} else {

				if (v.isCollection()) {
					anotherVariable.setCollection(true);
				}

				if (!v.isMandatory()) {
					anotherVariable.setMandatory(false);
				}

				if (!v.getType().equals(anotherVariable.getType())) {
					LOG.warn("merge - different type encountered " + type.getPaths() + " " + v.getType() + " " + anotherVariable.getType());
				}
			}
		}
	}

	public static void printType(Type type) {
		if (type instanceof CompositeType) {
			CompositeType compositeType = (CompositeType) type;
			LOG.info("type " + type.getName() + (compositeType.getSuperType() != null ? (" extends " + compositeType.getSuperType()) : "") + " " + compositeType.getVariables().size() + " vars "
					+ type.getPaths());
			for (TypeVariable v : compositeType.getVariables()) {
				LOG.info("   " + v);
			}
		} else if (type instanceof EnumType) {

			EnumType enumType = (EnumType) type;
			LOG.info("type " + type.getName() + " values:" + enumType.getValues());

		} else {
			LOG.info("type " + type.getName());
		}
	}

}
