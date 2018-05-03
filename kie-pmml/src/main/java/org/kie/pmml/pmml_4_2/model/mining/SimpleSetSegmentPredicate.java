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

import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.dmg.pmml.pmml_4_2.descr.SimpleSetPredicate;
import org.kie.pmml.pmml_4_2.PMML4Helper;
import org.mvel2.integration.impl.MapVariableResolverFactory;
import org.mvel2.templates.CompiledTemplate;
import org.mvel2.templates.TemplateCompiler;
import org.mvel2.templates.TemplateRuntime;

public class SimpleSetSegmentPredicate implements PredicateRuleProducer {
	private String setType;
	private String operator;
	private String baseFieldName;
	private String setValuesString;
	private BigInteger valueCount;
	private List<Object> valuesList;
	private static CompiledTemplate template;
	private static PMML4Helper helper = new PMML4Helper();
	
	private static String PREDICATE_TEMPLATE = ""
			+ "@{fieldName} @{operator} ("
			+ "@foreach{value: values}"
			+ "  @if{ setType==\"string\" }\"@{value}\" @elseif{ setType!=\"string\" }@{value}@end{}"
			+ "@end{\",\"} )";
	
	public SimpleSetSegmentPredicate(SimpleSetPredicate predicate) {
		this.setType = predicate.getArray().getType();
		this.operator = predicate.getBooleanOperator();
		this.baseFieldName = predicate.getField();
		this.setValuesString = predicate.getArray().getContent();
		this.valueCount = predicate.getArray().getN();
		this.valuesList = getValueObjects();
		if (this.valueCount != null && this.valueCount.intValue() != this.valuesList.size()) {
			throw new IllegalStateException("PMML-SimpleSetPredicate: Number of values found ("
					+valuesList.size()+") does not equal number of values declared ("
					+this.valueCount+")");
		}
		if (this.operator == null || 
				(!this.operator.equals("isIn") &&
				 !this.operator.equals("isNotIn"))) {
			throw new IllegalStateException("PMML-SimpleSetPredicate: booleanOperator was not one of the allowed values (\"isIn\" or \"isNotIn\")");
		}
	}

	public String getSetType() {
		return setType;
	}

	public void setSetType(String setType) {
		this.setType = setType;
	}

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

	public String getFieldName() {
		return baseFieldName;
	}
	
	public String getCapitalizedFieldName() {
		return helper.compactAsJavaId(this.baseFieldName, true);
	}
	
	public String getMissingFieldName() {
		return "m".concat(getCapitalizedFieldName());
	}
	
	public String getValueFieldName() {
		return "v".concat(getCapitalizedFieldName());
	}

	public void setFieldName(String fieldName) {
		this.baseFieldName = fieldName;
	}

	public String getSetValues() {
		return setValuesString;
	}

	public void setSetValues(String setValues) {
		this.setValuesString = setValues;
	}
	
	private CompiledTemplate getTemplate() {
		if (template == null) {
			template = TemplateCompiler.compileTemplate(PREDICATE_TEMPLATE);
		}
		return template;
	}
	
	private Matcher getMatcher() {
		Pattern pattern = null;
		if ("int".equalsIgnoreCase(this.setType)) {
			pattern = Pattern.compile("\\b(\\d*)\\w+\\b");
		} else if ("real".equalsIgnoreCase(this.setType)) {
			pattern = Pattern.compile("\\b(\\d*\\.?\\d*\\w+)\\b");
		} else {
			pattern = Pattern.compile("\\b(?:(?<=\")[^\"]*(?=\")|\\w+)\\b");
		}
		if (pattern != null) {
			return pattern.matcher(this.setValuesString);
		}
		return null;
	}
	
	private List<Object> getValueObjects() {
		List<Object> objects = new ArrayList<>();
		Matcher matcher = getMatcher();
		if (matcher != null) {
			if ("int".equalsIgnoreCase(this.setType)) {
				while(matcher.find()) {
					Integer value = Integer.valueOf(matcher.group(0));
					objects.add(value);
				}
			} else if ("real".equals(this.setType)) {
				while (matcher.find()) {
					String valueStr = matcher.group(0);
					Double value = Double.valueOf(valueStr);
					objects.add(value);
				}
			} else {
				while (matcher.find()) {
					objects.add(matcher.group(0));
				}
			}
		}
		return objects;
	}
	
	private String getOperatorText() {
		if (this.operator != null) {
			if (this.operator.equalsIgnoreCase("isIn")) {
				return "in";
			} else if (this.operator.equalsIgnoreCase("isNotIn")) {
				return "not in";
			}
		}
		throw new IllegalStateException("PMML-SimpleSetPredicate: booleanOperator was not one of the allowed values (\"isIn\" or \"isNotIn\")");
	}

	@Override
	public String getPredicateRule() {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		CompiledTemplate ct = getTemplate();
		if (ct != null) {
			Map<String,Object>vars = new HashMap<>();
			vars.put("fieldName", this.getValueFieldName());
			vars.put("operator", getOperatorText());
			vars.put("setType", setType);
			vars.put("values", getValueObjects());
			TemplateRuntime.execute(ct,null,new MapVariableResolverFactory(vars),baos);
		}
		return new String(baos.toByteArray());
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
