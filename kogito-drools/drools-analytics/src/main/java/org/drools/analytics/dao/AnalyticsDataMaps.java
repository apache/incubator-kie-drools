package org.drools.analytics.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.drools.analytics.components.AnalyticsClass;
import org.drools.analytics.components.AnalyticsEvalDescr;
import org.drools.analytics.components.AnalyticsPredicateDescr;
import org.drools.analytics.components.AnalyticsRule;
import org.drools.analytics.components.Consequence;
import org.drools.analytics.components.Constraint;
import org.drools.analytics.components.Field;
import org.drools.analytics.components.FieldClassLink;
import org.drools.analytics.components.OperatorDescr;
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
class AnalyticsDataMaps implements AnalyticsData {

	private Map<Integer, RulePackage> packagesById = new TreeMap<Integer, RulePackage>();
	private Map<String, RulePackage> packagesByName = new TreeMap<String, RulePackage>();

	private Map<Integer, AnalyticsClass> classesById = new TreeMap<Integer, AnalyticsClass>();
	private Map<String, AnalyticsClass> classesByName = new TreeMap<String, AnalyticsClass>();
	private Map<String, Field> fieldsByClassAndFieldName = new TreeMap<String, Field>();
	private Map<Integer, Field> fieldsById = new TreeMap<Integer, Field>();
	private DataTree<Integer, Field> fieldsByClassId = new DataTree<Integer, Field>();
	private Map<String, FieldClassLink> fieldClassLinkByIds = new TreeMap<String, FieldClassLink>();

	private Map<Integer, AnalyticsRule> rulesById = new TreeMap<Integer, AnalyticsRule>();
	private Map<Integer, Pattern> patternsById = new TreeMap<Integer, Pattern>();
	private DataTree<Integer, Pattern> patternsByClassId = new DataTree<Integer, Pattern>();
	private DataTree<String, Pattern> patternsByRuleName = new DataTree<String, Pattern>();
	private Map<Integer, Constraint> constraintsById = new TreeMap<Integer, Constraint>();
	private Map<Integer, Restriction> restrictionsById = new TreeMap<Integer, Restriction>();
	private DataTree<Integer, Restriction> restrictionsByFieldId = new DataTree<Integer, Restriction>();
	private Map<Integer, OperatorDescr> operatorsById = new TreeMap<Integer, OperatorDescr>();
	private Map<Integer, AnalyticsEvalDescr> evalsById = new TreeMap<Integer, AnalyticsEvalDescr>();
	private Map<Integer, AnalyticsPredicateDescr> predicatesById = new TreeMap<Integer, AnalyticsPredicateDescr>();
	private Map<Integer, Consequence> consiquencesById = new TreeMap<Integer, Consequence>();

	private Map<String, Variable> variablesByRuleAndVariableName = new TreeMap<String, Variable>();

	private Map<Integer, PatternPossibility> patternPossibilitiesById = new TreeMap<Integer, PatternPossibility>();
	private Map<Integer, RulePossibility> rulePossibilitiesById = new TreeMap<Integer, RulePossibility>();

	public void add(AnalyticsClass clazz) {
		classesById.put(Integer.valueOf(clazz.getId()), clazz);
		classesByName.put(clazz.getName(), clazz);
	}

	public void add(Field field) {
		AnalyticsClass clazz = classesById.get(Integer.valueOf(field
				.getClassId()));
		fieldsByClassAndFieldName.put(clazz.getName() + "." + field.getName(),
				field);

		fieldsById.put(field.getId(), field);

		fieldsByClassId.put(field.getClassId(), field);
	}

	public void add(Variable variable) {
		variablesByRuleAndVariableName.put(variable.getRuleName() + "."
				+ variable.getName(), variable);
	}

	public void add(AnalyticsRule rule) {
		rulesById.put(Integer.valueOf(rule.getId()), rule);
	}

	public void add(Pattern pattern) {
		patternsById.put(Integer.valueOf(pattern.getId()), pattern);

		patternsByClassId.put(pattern.getClassId(), pattern);
		patternsByRuleName.put(pattern.getRuleName(), pattern);
	}

	public void add(Constraint constraint) {
		constraintsById.put(Integer.valueOf(constraint.getId()), constraint);
	}

	public void add(Restriction restriction) {
		restrictionsById.put(restriction.getId(), restriction);

		restrictionsByFieldId.put(restriction.getFieldId(), restriction);
	}

	public void add(FieldClassLink link) {
		fieldClassLinkByIds.put(link.getFieldId() + "." + link.getClassId(),
				link);
	}

	public AnalyticsClass getClassByPackageAndName(String name) {
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

	public void add(PatternPossibility possibility) {
		patternPossibilitiesById.put(possibility.getId(), possibility);
	}

	public void add(RulePossibility possibility) {
		rulePossibilitiesById.put(possibility.getId(), possibility);
	}

	public Collection<AnalyticsClass> getClassesByRuleName(String ruleName) {
		Set<AnalyticsClass> set = new HashSet<AnalyticsClass>();

		for (Pattern pattern : patternsByRuleName.getBranch(ruleName)) {
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
		objects.addAll(operatorsById.values());
		objects.addAll(evalsById.values());
		objects.addAll(predicatesById.values());
		objects.addAll(consiquencesById.values());

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
		return fieldsByClassId.getBranch(id);
	}

	public Collection<AnalyticsRule> getRulesByClassId(int id) {
		Set<AnalyticsRule> rules = new HashSet<AnalyticsRule>();

		for (Pattern pattern : patternsByClassId.getBranch(id)) {
			rules.add(rulesById.get(pattern.getRuleId()));
		}

		return rules;
	}

	public Collection<Field> getAllFields() {
		return fieldsById.values();
	}

	public Collection<AnalyticsRule> getRulesByFieldId(int id) {

		Set<AnalyticsRule> rules = new HashSet<AnalyticsRule>();

		for (Restriction restriction : restrictionsByFieldId.getBranch(id)) {
			rules.add(rulesById.get(restriction.getRuleId()));
		}

		return rules;
	}

	public Collection<RulePackage> getAllRulePackages() {
		return packagesById.values();
	}

	public void add(RulePackage rulePackage) {
		packagesById.put(rulePackage.getId(), rulePackage);
		packagesByName.put(rulePackage.getName(), rulePackage);
	}

	public RulePackage getRulePackageByName(String name) {
		return packagesByName.get(name);
	}

	public Collection<Restriction> getRestrictionsByFieldId(int id) {
		return restrictionsByFieldId.getBranch(id);
	}

	public void add(OperatorDescr operatorDescr) {
		operatorsById.put(operatorDescr.getId(), operatorDescr);
	}

	public void add(AnalyticsEvalDescr eval) {
		evalsById.put(eval.getId(), eval);
	}

	public void add(AnalyticsPredicateDescr predicate) {
		predicatesById.put(predicate.getId(), predicate);
	}

	public void add(Consequence consequence) {
		consiquencesById.put(consequence.getId(), consequence);
	}
}
