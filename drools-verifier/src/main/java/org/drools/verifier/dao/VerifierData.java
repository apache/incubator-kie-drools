package org.drools.verifier.dao;

import java.util.Collection;

import org.drools.verifier.components.Consequence;
import org.drools.verifier.components.Constraint;
import org.drools.verifier.components.Field;
import org.drools.verifier.components.FieldObjectTypeLink;
import org.drools.verifier.components.ObjectType;
import org.drools.verifier.components.OperatorDescr;
import org.drools.verifier.components.Pattern;
import org.drools.verifier.components.PatternPossibility;
import org.drools.verifier.components.Restriction;
import org.drools.verifier.components.RulePackage;
import org.drools.verifier.components.RulePossibility;
import org.drools.verifier.components.Variable;
import org.drools.verifier.components.VerifierEvalDescr;
import org.drools.verifier.components.VerifierPredicateDescr;
import org.drools.verifier.components.VerifierRule;

/**
 * 
 * @author Toni Rikkola
 */
public interface VerifierData {

	public void add(Consequence consequence);

	public void add(ObjectType objectType);

	public void add(Field field);

	public void add(Variable variable);

	public void add(VerifierRule rule);

	public void add(Pattern pattern);

	public void add(Constraint constraint);

	public void add(Restriction restriction);

	public void add(FieldObjectTypeLink link);

	public void add(PatternPossibility possibility);

	public void add(RulePossibility possibility);

	public void add(RulePackage rulePackage);

	public ObjectType getObjectTypeByName(String name);

	public ObjectType getObjectTypeById(int id);

	public Field getFieldByObjectTypeAndFieldName(String className, String fieldName);

	public Variable getVariableByRuleAndVariableName(String ruleName,
			String variableName);

	public Collection<? extends Object> getAll();

	public FieldObjectTypeLink getFieldObjectTypeLink(int id, int id2);

	public Collection<VerifierRule> getAllRules();

	public Collection<ObjectType> getObjectTypesByRuleName(String ruleName);

	public Collection<ObjectType> getAllObjectTypes();

	public Collection<RulePackage> getAllRulePackages();

	public Collection<Field> getFieldsByObjectTypeId(int id);

	public Collection<VerifierRule> getRulesByObjectTypeId(int id);

	public Collection<Field> getAllFields();

	public Collection<VerifierRule> getRulesByFieldId(int id);

	public RulePackage getRulePackageByName(String name);

	public Collection<Restriction> getRestrictionsByFieldId(int id);

	public void add(OperatorDescr operatorDescr);

	public void add(VerifierEvalDescr eval);

	public void add(VerifierPredicateDescr predicate);
}
