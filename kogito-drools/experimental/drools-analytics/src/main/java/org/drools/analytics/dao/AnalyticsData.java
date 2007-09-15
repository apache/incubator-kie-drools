package org.drools.analytics.dao;

import java.util.Collection;

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
public interface AnalyticsData {

	public void insert(AnalyticsClass clazz);

	public void insert(Field field);

	public void insert(Variable variable);

	public void insert(AnalyticsRule rule);

	public void insert(Pattern pattern);

	public void insert(Constraint constraint);

	public void insert(Restriction restriction);

	public void insert(FieldClassLink link);

	public void insert(PatternPossibility possibility);

	public void insert(RulePossibility possibility);

	public void insert(RulePackage rulePackage);

	public AnalyticsClass getClassByName(String name);

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
}
