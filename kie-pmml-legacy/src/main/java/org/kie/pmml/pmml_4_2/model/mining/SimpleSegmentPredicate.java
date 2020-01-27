/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.pmml.pmml_4_2.model.mining;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.dmg.pmml.pmml_4_2.descr.SimplePredicate;
import org.kie.pmml.pmml_4_2.PMML4Helper;

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
	private boolean stringLiteralValue;
	private static PMML4Helper helper = new PMML4Helper();
	
	public SimpleSegmentPredicate() {
		// Just an empty constructor
	}
	
	public SimpleSegmentPredicate(SimplePredicate predicate) {
		this.baseFieldName = predicate.getField();
		this.operator = predicate.getOperator();
		this.value = predicate.getValue();
		this.stringLiteralValue = checkValueForStringLiteral(this.value);
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
		StringBuilder bldr = new StringBuilder();
		bldr.append("( ").append(this.getMissingFieldName()).append(" == false )")
		    .append(" && ( ").append(this.getValueFieldName());
		if (operator.equalsIgnoreCase(GREATER)) {
			bldr.append(" > ").append(getValue()).append(" )");
			return bldr.toString();
		} else if (operator.equalsIgnoreCase(LESSER)) {
			bldr.append(" < ").append(getValue()).append(" )");
			return bldr.toString();
		} else if (operator.equalsIgnoreCase(EQUAL)) {
			bldr.append(" == ").append(getValue()).append(" )");
			return bldr.toString();
		} else if (operator.equalsIgnoreCase(NOT_EQUAL)) {
			bldr.append(" != ").append(getValue()).append(" )");
			return bldr.toString();
		} else if (operator.equalsIgnoreCase(MISSING)) {
			return this.getMissingFieldName()+" == true";
		} else if (operator.equalsIgnoreCase(NOT_MISSING)) {
			return this.getMissingFieldName()+" == false";
		} else if (operator.equalsIgnoreCase(GREATER_EQUAL)) {
			bldr.append(" >= ").append(getValue()).append(" )");
			return bldr.toString();
		} else if (operator.equalsIgnoreCase(LESSER_EQUAL)) {
			bldr.append(" <= ").append(getValue()).append(" )");
			return bldr.toString();
		}
		throw new IllegalStateException("PMML - SimplePredicate: Unknown operator ("+operator+")");
	}
	
	private boolean checkValueForStringLiteral(String value) {
		Pattern p = Pattern.compile("[0-9]*\\.?+[0-9]*");
		Matcher m = p.matcher(value.trim());
		return !m.matches();
	}
	
	private String getValue() {
		return stringLiteralValue ? "\""+value+"\"" : value;
	}
	
	public boolean isStringLiteralValue() {
		return stringLiteralValue;
	}
	
	public void setStringLiteralValue(boolean stringLiteralValue) {
		this.stringLiteralValue = stringLiteralValue;
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

	@Override
	public boolean isAlwaysTrue() {
		return false;
	}

	@Override
	public boolean isAlwaysFalse() {
		return false;
	}
}
