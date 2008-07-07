package org.drools.verifier.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.drools.verifier.components.ObjectType;
import org.drools.verifier.components.VerifierEvalDescr;
import org.drools.verifier.components.VerifierPredicateDescr;
import org.drools.verifier.components.VerifierRule;
import org.drools.verifier.components.Consequence;
import org.drools.verifier.components.Constraint;
import org.drools.verifier.components.Field;
import org.drools.verifier.components.FieldObjectTypeLink;
import org.drools.verifier.components.OperatorDescr;
import org.drools.verifier.components.Pattern;
import org.drools.verifier.components.PatternPossibility;
import org.drools.verifier.components.Restriction;
import org.drools.verifier.components.RulePackage;
import org.drools.verifier.components.RulePossibility;
import org.drools.verifier.components.Variable;

/**
 * 
 * @author Toni Rikkola
 */
class VerifierDataMaps implements VerifierData {

	private Map<Integer, RulePackage> packagesById = new TreeMap<Integer, RulePackage>();
	private Map<String, RulePackage> packagesByName = new TreeMap<String, RulePackage>();

	private Map<Integer, ObjectType> objectTypesById = new TreeMap<Integer, ObjectType>();
	private Map<String, ObjectType> objectTypesByName = new TreeMap<String, ObjectType>();
	private Map<String, Field> fieldsByObjectTypeAndFieldName = new TreeMap<String, Field>();
	private Map<Integer, Field> fieldsById = new TreeMap<Integer, Field>();
	private DataTree<Integer, Field> fieldsByObjectTypeId = new DataTree<Integer, Field>();
	private Map<String, FieldObjectTypeLink> fieldObjectTypeLinkByIds = new TreeMap<String, FieldObjectTypeLink>();

	private Map<Integer, VerifierRule> rulesById = new TreeMap<Integer, VerifierRule>();
	private Map<Integer, Pattern> patternsById = new TreeMap<Integer, Pattern>();
	private DataTree<Integer, Pattern> patternsByObjectTypeId = new DataTree<Integer, Pattern>();
	private DataTree<String, Pattern> patternsByRuleName = new DataTree<String, Pattern>();
	private Map<Integer, Constraint> constraintsById = new TreeMap<Integer, Constraint>();
	private Map<Integer, Restriction> restrictionsById = new TreeMap<Integer, Restriction>();
	private DataTree<Integer, Restriction> restrictionsByFieldId = new DataTree<Integer, Restriction>();
	private Map<Integer, OperatorDescr> operatorsById = new TreeMap<Integer, OperatorDescr>();
	private Map<Integer, VerifierEvalDescr> evalsById = new TreeMap<Integer, VerifierEvalDescr>();
	private Map<Integer, VerifierPredicateDescr> predicatesById = new TreeMap<Integer, VerifierPredicateDescr>();
	private Map<Integer, Consequence> consiquencesById = new TreeMap<Integer, Consequence>();

	private Map<String, Variable> variablesByRuleAndVariableName = new TreeMap<String, Variable>();

	private Map<Integer, PatternPossibility> patternPossibilitiesById = new TreeMap<Integer, PatternPossibility>();
	private Map<Integer, RulePossibility> rulePossibilitiesById = new TreeMap<Integer, RulePossibility>();

	public void add(ObjectType objectType) {
		objectTypesById.put(Integer.valueOf(objectType.getId()), objectType);
		objectTypesByName.put(objectType.getName(), objectType);
	}

	public void add(Field field) {
		ObjectType objectType = objectTypesById.get(Integer.valueOf(field
				.getObjectTypeId()));
		fieldsByObjectTypeAndFieldName.put(objectType.getName() + "."
				+ field.getName(), field);

		fieldsById.put(field.getId(), field);

		fieldsByObjectTypeId.put(field.getObjectTypeId(), field);
	}

	public void add(Variable variable) {
		variablesByRuleAndVariableName.put(variable.getRuleName() + "."
				+ variable.getName(), variable);
	}

	public void add(VerifierRule rule) {
		rulesById.put(Integer.valueOf(rule.getId()), rule);
	}

	public void add(Pattern pattern) {
		patternsById.put(Integer.valueOf(pattern.getId()), pattern);

		patternsByObjectTypeId.put(pattern.getObjectTypeId(), pattern);
		patternsByRuleName.put(pattern.getRuleName(), pattern);
	}

	public void add(Constraint constraint) {
		constraintsById.put(Integer.valueOf(constraint.getId()), constraint);
	}

	public void add(Restriction restriction) {
		restrictionsById.put(restriction.getId(), restriction);

		restrictionsByFieldId.put(restriction.getFieldId(), restriction);
	}

	public void add(FieldObjectTypeLink link) {
		fieldObjectTypeLinkByIds.put(link.getFieldId() + "."
				+ link.getObjectTypeId(), link);
	}

	public ObjectType getObjectTypeByName(String name) {
		return objectTypesByName.get(name);
	}

	public Field getFieldByObjectTypeAndFieldName(String objectTypeName,
			String fieldName) {
		return fieldsByObjectTypeAndFieldName.get(objectTypeName + "."
				+ fieldName);
	}

	public Variable getVariableByRuleAndVariableName(String ruleName,
			String variableName) {
		return variablesByRuleAndVariableName
				.get(ruleName + "." + variableName);
	}

	public FieldObjectTypeLink getFieldObjectTypeLink(int id, int id2) {
		return fieldObjectTypeLinkByIds.get(id + "." + id2);
	}

	public Collection<VerifierRule> getAllRules() {
		return rulesById.values();
	}

	public void add(PatternPossibility possibility) {
		patternPossibilitiesById.put(possibility.getId(), possibility);
	}

	public void add(RulePossibility possibility) {
		rulePossibilitiesById.put(possibility.getId(), possibility);
	}

	public Collection<ObjectType> getObjectTypesByRuleName(String ruleName) {
		Set<ObjectType> set = new HashSet<ObjectType>();

		for (Pattern pattern : patternsByRuleName.getBranch(ruleName)) {
			ObjectType objectType = getObjectTypeById(pattern.getObjectTypeId());
			set.add(objectType);
		}

		return set;
	}

	public ObjectType getObjectTypeById(int id) {
		return objectTypesById.get(id);
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

		objects.addAll(objectTypesByName.values());
		objects.addAll(fieldsByObjectTypeAndFieldName.values());
		objects.addAll(variablesByRuleAndVariableName.values());

		return objects;
	}

	public Collection<ObjectType> getAllObjectTypes() {
		return objectTypesById.values();
	}

	public Collection<Field> getFieldsByObjectTypeId(int id) {
		return fieldsByObjectTypeId.getBranch(id);
	}

	public Collection<VerifierRule> getRulesByObjectTypeId(int id) {
		Set<VerifierRule> rules = new HashSet<VerifierRule>();

		for (Pattern pattern : patternsByObjectTypeId.getBranch(id)) {
			rules.add(rulesById.get(pattern.getRuleId()));
		}

		return rules;
	}

	public Collection<Field> getAllFields() {
		return fieldsById.values();
	}

	public Collection<VerifierRule> getRulesByFieldId(int id) {

		Set<VerifierRule> rules = new HashSet<VerifierRule>();

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

	public void add(VerifierEvalDescr eval) {
		evalsById.put(eval.getId(), eval);
	}

	public void add(VerifierPredicateDescr predicate) {
		predicatesById.put(predicate.getId(), predicate);
	}

	public void add(Consequence consequence) {
		consiquencesById.put(consequence.getId(), consequence);
	}
}
