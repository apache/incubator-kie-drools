package org.drools.analytics.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drools.analytics.components.AnalyticsClass;
import org.drools.analytics.components.AnalyticsRule;
import org.drools.analytics.components.Constraint;
import org.drools.analytics.components.Field;
import org.drools.analytics.components.FieldClassLink;
import org.drools.analytics.components.Pattern;
import org.drools.analytics.components.PatternPossibility;
import org.drools.analytics.components.Restriction;
import org.drools.analytics.components.RulePossibility;
import org.drools.analytics.components.Variable;

/**
 * 
 * @author Toni Rikkola
 */
public class AnalyticsDataMaps implements AnalyticsData {

	private Map<Integer, AnalyticsClass> classesById = new HashMap<Integer, AnalyticsClass>();
	private Map<String, AnalyticsClass> classesByName = new HashMap<String, AnalyticsClass>();
	private Map<String, Field> fieldsByClassAndFieldName = new HashMap<String, Field>();
	private Map<String, FieldClassLink> fieldClassLinkByIds = new HashMap<String, FieldClassLink>();

	private Map<Integer, AnalyticsRule> rulesById = new HashMap<Integer, AnalyticsRule>();
	private Map<Integer, Pattern> patternsById = new HashMap<Integer, Pattern>();
	private Map<Integer, Constraint> constraintsById = new HashMap<Integer, Constraint>();
	private Map<Integer, Restriction> restrictionsById = new HashMap<Integer, Restriction>();

	private Map<String, Variable> variablesByRuleAndVariableName = new HashMap<String, Variable>();

	private Map<Integer, PatternPossibility> patternPossibilitiesById = new HashMap<Integer, PatternPossibility>();
	private Map<Integer, RulePossibility> rulePossibilitiesById = new HashMap<Integer, RulePossibility>();

	private static AnalyticsDataMaps map;

	private AnalyticsDataMaps() {
	}

	public static AnalyticsDataMaps getAnalyticsDataMaps() {
		if (map == null) {
			map = new AnalyticsDataMaps();
		}
		return map;
	}

	public void insert(AnalyticsClass clazz) {
		classesById.put(Integer.valueOf(clazz.getId()), clazz);
		classesByName.put(clazz.getName(), clazz);
	}

	public void insert(Field field) {
		AnalyticsClass clazz = classesById.get(Integer.valueOf(field
				.getClassId()));
		fieldsByClassAndFieldName.put(clazz.getName() + "." + field.getName(),
				field);
	}

	public void insert(Variable variable) {
		AnalyticsRule rule = rulesById.get(Integer
				.valueOf(variable.getRuleId()));
		variablesByRuleAndVariableName.put(rule.getRuleName() + "."
				+ variable.getName(), variable);
	}

	public void insert(AnalyticsRule rule) {
		rulesById.put(Integer.valueOf(rule.getId()), rule);
	}

	public void insert(Pattern pattern) {
		patternsById.put(Integer.valueOf(pattern.getId()), pattern);
	}

	public void insert(Constraint constraint) {
		constraintsById.put(Integer.valueOf(constraint.getId()), constraint);
	}

	public void insert(Restriction restriction) {
		restrictionsById.put(restriction.getId(), restriction);
	}

	public void insert(FieldClassLink link) {
		fieldClassLinkByIds.put(link.getFieldId() + "." + link.getClassId(),
				link);
	}

	public AnalyticsClass getClassByName(String name) {
		return classesByName.get(name);
	}

	public Field getFieldByClassAndFieldName(String className, String fieldName) {
		return fieldsByClassAndFieldName.get(className + "." + fieldName);
	}

	public Variable getVariableByRuleAndVariableName(String ruleName,
			String variableName) {
		return variablesByRuleAndVariableName
				.get(ruleName + "." + variableName);
	}

	public FieldClassLink getFieldClassLink(int id, int id2) {
		return fieldClassLinkByIds.get(id + "." + id2);
	}

	public void insert(PatternPossibility possibility) {
		patternPossibilitiesById.put(possibility.getId(), possibility);
	}

	public void insert(RulePossibility possibility) {
		rulePossibilitiesById.put(possibility.getId(), possibility);
	}

	public Collection<? extends Object> getAll() {
		List<Object> objects = new ArrayList<Object>();

		objects.addAll(rulesById.values());
		objects.addAll(patternsById.values());
		objects.addAll(constraintsById.values());
		objects.addAll(restrictionsById.values());

		objects.addAll(patternPossibilitiesById.values());
		objects.addAll(rulePossibilitiesById.values());

		objects.addAll(classesByName.values());
		objects.addAll(fieldsByClassAndFieldName.values());
		objects.addAll(variablesByRuleAndVariableName.values());

		return objects;
	}
}
