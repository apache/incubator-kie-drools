package org.drools.verifier.dao;

import java.util.Collection;

import org.drools.verifier.components.AnalyticsClass;
import org.drools.verifier.components.AnalyticsEvalDescr;
import org.drools.verifier.components.AnalyticsPredicateDescr;
import org.drools.verifier.components.AnalyticsRule;
import org.drools.verifier.components.Consequence;
import org.drools.verifier.components.Constraint;
import org.drools.verifier.components.Field;
import org.drools.verifier.components.FieldClassLink;
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
public interface AnalyticsData {

	public void add(Consequence consequence);

	public void add(AnalyticsClass clazz);

	public void add(Field field);

	public void add(Variable variable);

	public void add(AnalyticsRule rule);

	public void add(Pattern pattern);

	public void add(Constraint constraint);

	public void add(Restriction restriction);

	public void add(FieldClassLink link);

	public void add(PatternPossibility possibility);

	public void add(RulePossibility possibility);

	public void add(RulePackage rulePackage);

	public AnalyticsClass getClassByPackageAndName(String name);

	public AnalyticsClass getClassById(int id);

	public Field getFieldByClassAndFieldName(String className, String fieldName);

	public Variable getVariableByRuleAndVariableName(String ruleName,
			String variableName);

	public Collection<? extends Object> getAll();

	public FieldClassLink getFieldClassLink(int id, int id2);

	public Collection<AnalyticsRule> getAllRules();

	public Collection<AnalyticsClass> getClassesByRuleName(String ruleName);

	public Collection<AnalyticsClass> getAllClasses();

	public Collection<RulePackage> getAllRulePackages();

	public Collection<Field> getFieldsByClassId(int id);

	public Collection<AnalyticsRule> getRulesByClassId(int id);

	public Collection<Field> getAllFields();

	public Collection<AnalyticsRule> getRulesByFieldId(int id);

	public RulePackage getRulePackageByName(String name);

	public Collection<Restriction> getRestrictionsByFieldId(int id);

	public void add(OperatorDescr operatorDescr);

	public void add(AnalyticsEvalDescr eval);

	public void add(AnalyticsPredicateDescr predicate);
}
