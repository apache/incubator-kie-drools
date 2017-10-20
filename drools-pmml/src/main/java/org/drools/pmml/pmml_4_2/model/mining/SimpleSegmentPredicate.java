package org.drools.pmml.pmml_4_2.model.mining;

import java.util.ArrayList;
import java.util.List;

import org.dmg.pmml.pmml_4_2.descr.SimplePredicate;
import org.drools.pmml.pmml_4_2.PMML4Helper;

public class SimpleSegmentPredicate implements PredicateRuleProducer {
	public static final String EQUAL = "equal";
	public static final String NOT_EQUAL = "notEqual";
	public static final String GREATER = "greaterThan";
	public static final String GREATER_EQUAL = "greaterOrEqual";
	public static final String LESSER = "lessThan";
	public static final String LESSER_EQUAL = "lessOrEqual";
	public static final String MISSING = "isMissing";
	public static final String NOT_MISSING = "isNotMissing";
	private String baseFieldName;
	private String operator;
	private String value;
	private static PMML4Helper helper = new PMML4Helper();
	
	public SimpleSegmentPredicate() {
		// Just an empty constructor
	}
	
	public SimpleSegmentPredicate(SimplePredicate predicate) {
		this.baseFieldName = predicate.getField();
		this.operator = predicate.getOperator();
		this.value = predicate.getValue();
		if (this.operator == null) {
			throw new IllegalStateException("PMML - SimplePredicate: Missing operator");
		}
	}
	
	public String getBaseFieldName() {
		return this.baseFieldName;
	}
	
	public String getCapitalizedFieldName() {
		return helper.compactAsJavaId(this.baseFieldName, true);
	}
	
	public String getValueFieldName() {
		return "v".concat(getCapitalizedFieldName());
	}
	
	public String getMissingFieldName() {
		return "m".concat(getCapitalizedFieldName());
	}

	@Override
	public String getPredicateRule() {
		if (operator.equalsIgnoreCase(GREATER)) {
			return this.getValueFieldName()+" > "+this.value;
		} else if (operator.equalsIgnoreCase(LESSER)) {
			return this.getValueFieldName()+" < "+this.value;
		} else if (operator.equalsIgnoreCase(EQUAL)) {
			return this.getValueFieldName()+" == "+this.value;
		} else if (operator.equalsIgnoreCase(NOT_EQUAL)) {
			return this.getValueFieldName()+" != "+this.value;
		} else if (operator.equalsIgnoreCase(MISSING)) {
			return this.getMissingFieldName()+" == true";
		} else if (operator.equalsIgnoreCase(NOT_MISSING)) {
			return this.getMissingFieldName()+" == false";
		} else if (operator.equalsIgnoreCase(GREATER_EQUAL)) {
			return this.getValueFieldName()+" >= "+this.value;
		} else if (operator.equalsIgnoreCase(LESSER_EQUAL)) {
			return this.getValueFieldName()+" <= "+this.value;
		}
		throw new IllegalStateException("PMML - SimplePredicate: Unknown operator ("+operator+")");
	}

	@Override
	public List<String> getPredicateFieldNames() {
		List<String> fieldNames = new ArrayList<>();
		fieldNames.add(baseFieldName);
		return fieldNames;
	}

	@Override
	public List<String> getFieldMissingFieldNames() {
		List<String> fieldNames = new ArrayList<>();
		fieldNames.add(getMissingFieldName());
		return fieldNames;
	}
}
