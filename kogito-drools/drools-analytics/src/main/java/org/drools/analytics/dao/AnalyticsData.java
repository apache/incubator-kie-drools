package org.drools.analytics.dao;

import java.util.Collection;

import org.drools.analytics.components.AnalyticsClass;
import org.drools.analytics.components.AnalyticsEvalDescr;
import org.drools.analytics.components.AnalyticsPredicateDescr;
import org.drools.analytics.components.AnalyticsRule;
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
public interface AnalyticsData {

	public void save(AnalyticsClass clazz);

	public void save(Field field);

	public void save(Variable variable);

	public void save(AnalyticsRule rule);

	public void save(Pattern pattern);

	public void save(Constraint constraint);

	public void save(Restriction restriction);

	public void save(FieldClassLink link);

	public void save(PatternPossibility possibility);

	public void save(RulePossibility possibility);

	public void save(RulePackage rulePackage);

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

	public void save(OperatorDescr operatorDescr);

	public void save(AnalyticsEvalDescr eval);

	public void save(AnalyticsPredicateDescr predicate);
}
