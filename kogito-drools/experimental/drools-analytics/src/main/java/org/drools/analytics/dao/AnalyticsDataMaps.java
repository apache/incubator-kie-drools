package org.drools.analytics.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.drools.analytics.components.AnalyticsClass;
import org.drools.analytics.components.AnalyticsRule;
import org.drools.analytics.components.Constraint;
import org.drools.analytics.components.Field;
import org.drools.analytics.components.FieldClassLink;
import org.drools.analytics.components.Pattern;
import org.drools.analytics.components.PatternPossibility;
import org.drools.analytics.components.Restriction;
import org.drools.analytics.components.RulePackage;
import org.drools.analytics.components.RulePossibility;
import org.drools.analytics.components.Variable;

/**
 * 
 * @author Toni Rikkola
 */
public class AnalyticsDataMaps implements AnalyticsData {

	private Map<Integer, RulePackage> packagesById = new HashMap<Integer, RulePackage>();
	private Map<String, RulePackage> packagesByName = new HashMap<String, RulePackage>();

	private Map<Integer, AnalyticsClass> classesById = new HashMap<Integer, AnalyticsClass>();
	private Map<String, AnalyticsClass> classesByName = new HashMap<String, AnalyticsClass>();
	private Map<String, Field> fieldsByClassAndFieldName = new HashMap<String, Field>();
	private Map<Integer, Field> fieldsById = new HashMap<Integer, Field>();
	private Map<Integer, Set<Field>> fieldsByClassId = new HashMap<Integer, Set<Field>>();
	private Map<String, FieldClassLink> fieldClassLinkByIds = new HashMap<String, FieldClassLink>();

	private Map<Integer, AnalyticsRule> rulesById = new HashMap<Integer, AnalyticsRule>();
	private Map<Integer, Pattern> patternsById = new HashMap<Integer, Pattern>();
	private Map<Integer, Set<Pattern>> patternsByClassId = new HashMap<Integer, Set<Pattern>>();
	private Map<String, Set<Pattern>> patternsByRuleName = new HashMap<String, Set<Pattern>>();
	private Map<Integer, Constraint> constraintsById = new HashMap<Integer, Constraint>();
	private Map<Integer, Restriction> restrictionsById = new HashMap<Integer, Restriction>();
	private Map<Integer, Set<Restriction>> restrictionsByFieldId = new HashMap<Integer, Set<Restriction>>();

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

		fieldsById.put(field.getId(), field);

		// Save by class id.
		if (fieldsByClassId.containsKey(field.getClassId())) {
			Set<Field> set = fieldsByClassId.get(field.getClassId());
			set.add(field);
		} else {
			Set<Field> set = new HashSet<Field>();
			set.add(field);
			fieldsByClassId.put(field.getClassId(), set);
		}
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

		// Save by class id.
		if (patternsByClassId.containsKey(pattern.getClassId())) {
			Set<Pattern> set = patternsByClassId.get(pattern.getClassId());
			set.add(pattern);
		} else {
			Set<Pattern> set = new HashSet<Pattern>();
			set.add(pattern);
			patternsByClassId.put(pattern.getClassId(), set);
		}

		// Save by rule name.
		if (patternsByRuleName.containsKey(pattern.getRuleName())) {
			Set<Pattern> set = patternsByRuleName.get(pattern.getRuleName());
			set.add(pattern);
		} else {
			Set<Pattern> set = new HashSet<Pattern>();
			set.add(pattern);
			patternsByRuleName.put(pattern.getRuleName(), set);
		}
	}

	public void insert(Constraint constraint) {
		constraintsById.put(Integer.valueOf(constraint.getId()), constraint);
	}

	public void insert(Restriction restriction) {
		restrictionsById.put(restriction.getId(), restriction);

		// Save by field id.
		if (restrictionsByFieldId.containsKey(restriction.getFieldId())) {
			Set<Restriction> set = restrictionsByFieldId.get(restriction
					.getFieldId());
			set.add(restriction);
		} else {
			Set<Restriction> set = new HashSet<Restriction>();
			set.add(restriction);
			restrictionsByFieldId.put(restriction.getFieldId(), set);
		}
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

	public Collection<AnalyticsRule> getAllRules() {
		return rulesById.values();
	}

	public void insert(PatternPossibility possibility) {
		patternPossibilitiesById.put(possibility.getId(), possibility);
	}

	public void insert(RulePossibility possibility) {
		rulePossibilitiesById.put(possibility.getId(), possibility);
	}

	public Collection<AnalyticsClass> getClassesByRuleName(String ruleName) {
		Set<AnalyticsClass> set = new HashSet<AnalyticsClass>();

		for (Pattern pattern : patternsByRuleName.get(ruleName)) {
			AnalyticsClass clazz = getClassById(pattern.getClassId());
			set.add(clazz);
		}

		return set;
	}

	public AnalyticsClass getClassById(int id) {
		return classesById.get(id);
	}

	public Collection<? extends Object> getAll() {
		List<Object> objects = new ArrayList<Object>();

		objects.addAll(packagesById.values());

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

	public Collection<AnalyticsClass> getAllClasses() {
		return classesById.values();
	}

	public Collection<Field> getFieldsByClassId(int id) {
		return fieldsByClassId.get(id);
	}

	public Collection<AnalyticsRule> getRulesByClassId(int id) {
		Set<AnalyticsRule> rules = new HashSet<AnalyticsRule>();

		for (Pattern pattern : patternsByClassId.get(id)) {
			rules.add(rulesById.get(pattern.getRuleId()));
		}

		return rules;
	}

	public Collection<Field> getAllFields() {
		return fieldsById.values();
	}

	public Collection<AnalyticsRule> getRulesByFieldId(int id) {

		Set<AnalyticsRule> rules = new HashSet<AnalyticsRule>();

		for (Restriction restriction : restrictionsByFieldId.get(id)) {
			rules.add(rulesById.get(restriction.getRuleId()));
		}

		return rules;
	}

	public Collection<RulePackage> getAllRulePackages() {
		return packagesById.values();
	}

	public void insert(RulePackage rulePackage) {
		packagesById.put(rulePackage.getId(), rulePackage);
		packagesByName.put(rulePackage.getName(), rulePackage);
	}

	public RulePackage getRulePackageByName(String name) {
		return packagesByName.get(name);
	}
}
