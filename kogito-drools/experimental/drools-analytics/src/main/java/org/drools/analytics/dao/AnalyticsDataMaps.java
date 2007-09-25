package org.drools.analytics.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

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
import org.drools.analytics.result.Gap;
import org.drools.analytics.result.MissingNumberPattern;
import org.drools.analytics.result.RangeCheckCause;

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
	private Map<Integer, Set<Field>> fieldsByClassId = new TreeMap<Integer, Set<Field>>();
	private Map<String, FieldClassLink> fieldClassLinkByIds = new TreeMap<String, FieldClassLink>();

	private Map<Integer, AnalyticsRule> rulesById = new TreeMap<Integer, AnalyticsRule>();
	private Map<Integer, Pattern> patternsById = new TreeMap<Integer, Pattern>();
	private Map<Integer, Set<Pattern>> patternsByClassId = new TreeMap<Integer, Set<Pattern>>();
	private Map<String, Set<Pattern>> patternsByRuleName = new TreeMap<String, Set<Pattern>>();
	private Map<Integer, Constraint> constraintsById = new TreeMap<Integer, Constraint>();
	private Map<Integer, Restriction> restrictionsById = new TreeMap<Integer, Restriction>();
	private Map<Integer, Set<Restriction>> restrictionsByFieldId = new TreeMap<Integer, Set<Restriction>>();

	private Map<String, Variable> variablesByRuleAndVariableName = new TreeMap<String, Variable>();

	private Map<Integer, PatternPossibility> patternPossibilitiesById = new TreeMap<Integer, PatternPossibility>();
	private Map<Integer, RulePossibility> rulePossibilitiesById = new TreeMap<Integer, RulePossibility>();

	private Map<Integer, Gap> gapsById = new TreeMap<Integer, Gap>();
	private Map<Integer, Set<Gap>> gapsByFieldId = new TreeMap<Integer, Set<Gap>>();
	private Map<Integer, MissingNumberPattern> missingNumberPatternsById = new TreeMap<Integer, MissingNumberPattern>();
	private Map<Integer, Set<MissingNumberPattern>> missingNumberPatternsByFieldId = new TreeMap<Integer, Set<MissingNumberPattern>>();

	public void save(AnalyticsClass clazz) {
		classesById.put(Integer.valueOf(clazz.getId()), clazz);
		classesByName.put(clazz.getName(), clazz);
	}

	public void save(Field field) {
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

	public void save(Variable variable) {
		AnalyticsRule rule = rulesById.get(Integer
				.valueOf(variable.getRuleId()));
		variablesByRuleAndVariableName.put(rule.getRuleName() + "."
				+ variable.getName(), variable);
	}

	public void save(AnalyticsRule rule) {
		rulesById.put(Integer.valueOf(rule.getId()), rule);
	}

	public void save(Pattern pattern) {
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

	public void save(Constraint constraint) {
		constraintsById.put(Integer.valueOf(constraint.getId()), constraint);
	}

	public void save(Restriction restriction) {
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

	public void save(FieldClassLink link) {
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

	public void save(PatternPossibility possibility) {
		patternPossibilitiesById.put(possibility.getId(), possibility);
	}

	public void save(RulePossibility possibility) {
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

	public void save(RulePackage rulePackage) {
		packagesById.put(rulePackage.getId(), rulePackage);
		packagesByName.put(rulePackage.getName(), rulePackage);
	}

	public RulePackage getRulePackageByName(String name) {
		return packagesByName.get(name);
	}

	public void save(Gap gap) {
		gapsById.put(gap.getId(), gap);

		// Save by field id.
		if (gapsByFieldId.containsKey(gap.getField().getId())) {
			Set<Gap> set = gapsByFieldId.get(gap.getField().getId());
			set.add(gap);
		} else {
			Set<Gap> set = new HashSet<Gap>();
			set.add(gap);
			gapsByFieldId.put(gap.getField().getId(), set);
		}
	}

	public void remove(Gap gap) {
		gapsById.remove(gap.getId());

		if (gapsByFieldId.containsKey(gap.getField().getId())) {
			Set<Gap> set = gapsByFieldId.get(gap.getField().getId());
			set.add(gap);

			if (set.isEmpty()) {
				gapsByFieldId.remove(gap.getField().getId());
			}
		}
	}

	public Collection<Field> getFieldsWithGaps() {
		Set<Integer> set = gapsByFieldId.keySet();
		Collection<Field> fields = new ArrayList<Field>();

		for (Integer i : set) {
			fields.add(fieldsById.get(i));
		}

		return fields;
	}

	public Collection<Gap> getGapsByFieldId(int fieldId) {
		return gapsByFieldId.get(fieldId);
	}

	public Collection<Restriction> getRestrictionsByFieldId(int id) {
		return restrictionsByFieldId.get(id);
	}

	public Collection<RangeCheckCause> getRangeCheckCauses() {
		Collection<RangeCheckCause> result = new ArrayList<RangeCheckCause>();

		result.addAll(gapsById.values());
		result.addAll(missingNumberPatternsById.values());

		return result;
	}

	public void save(MissingNumberPattern missingNumberPattern) {
		missingNumberPatternsById.put(missingNumberPattern.getId(),
				missingNumberPattern);

		// Save by field id.
		if (missingNumberPatternsByFieldId.containsKey(missingNumberPattern
				.getField().getId())) {
			Set<MissingNumberPattern> set = missingNumberPatternsByFieldId
					.get(missingNumberPattern.getField().getId());
			set.add(missingNumberPattern);
		} else {
			Set<MissingNumberPattern> set = new HashSet<MissingNumberPattern>();
			set.add(missingNumberPattern);
			missingNumberPatternsByFieldId.put(missingNumberPattern.getField()
					.getId(), set);
		}
	}

	public Collection<RangeCheckCause> getRangeCheckCausesByFieldId(int id) {
		Collection<RangeCheckCause> result = new ArrayList<RangeCheckCause>();

		if (gapsByFieldId.containsKey(id)) {
			result.addAll(gapsByFieldId.get(id));
		}
		if (missingNumberPatternsByFieldId.containsKey(id)) {
			result.addAll(missingNumberPatternsByFieldId.get(id));
		}

		return result;
	}
}
