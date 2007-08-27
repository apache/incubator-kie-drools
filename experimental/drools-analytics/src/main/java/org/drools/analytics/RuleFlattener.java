package org.drools.analytics;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.drools.analytics.components.AnalyticsAccessorDescr;
import org.drools.analytics.components.AnalyticsAccumulateDescr;
import org.drools.analytics.components.AnalyticsClass;
import org.drools.analytics.components.AnalyticsCollectDescr;
import org.drools.analytics.components.AnalyticsComponent;
import org.drools.analytics.components.AnalyticsComponentType;
import org.drools.analytics.components.AnalyticsEvalDescr;
import org.drools.analytics.components.AnalyticsFieldAccessDescr;
import org.drools.analytics.components.AnalyticsFromDescr;
import org.drools.analytics.components.AnalyticsFunctionCallDescr;
import org.drools.analytics.components.AnalyticsMethodAccessDescr;
import org.drools.analytics.components.AnalyticsPredicateDescr;
import org.drools.analytics.components.AnalyticsRule;
import org.drools.analytics.components.Constraint;
import org.drools.analytics.components.Field;
import org.drools.analytics.components.LiteralRestriction;
import org.drools.analytics.components.OperatorDescr;
import org.drools.analytics.components.Pattern;
import org.drools.analytics.components.PatternPossibility;
import org.drools.analytics.components.QualifiedIdentifierRestriction;
import org.drools.analytics.components.ReturnValueRestriction;
import org.drools.analytics.components.RulePossibility;
import org.drools.analytics.components.Variable;
import org.drools.analytics.components.VariableRestriction;
import org.drools.analytics.dao.AnalyticsData;
import org.drools.analytics.dao.AnalyticsDataMaps;
import org.drools.lang.descr.AccessorDescr;
import org.drools.lang.descr.AccumulateDescr;
import org.drools.lang.descr.AndDescr;
import org.drools.lang.descr.BaseDescr;
import org.drools.lang.descr.CollectDescr;
import org.drools.lang.descr.ConditionalElementDescr;
import org.drools.lang.descr.DeclarativeInvokerDescr;
import org.drools.lang.descr.EvalDescr;
import org.drools.lang.descr.ExistsDescr;
import org.drools.lang.descr.FieldAccessDescr;
import org.drools.lang.descr.FieldBindingDescr;
import org.drools.lang.descr.FieldConstraintDescr;
import org.drools.lang.descr.ForallDescr;
import org.drools.lang.descr.FromDescr;
import org.drools.lang.descr.FunctionCallDescr;
import org.drools.lang.descr.LiteralRestrictionDescr;
import org.drools.lang.descr.MethodAccessDescr;
import org.drools.lang.descr.NotDescr;
import org.drools.lang.descr.OrDescr;
import org.drools.lang.descr.PackageDescr;
import org.drools.lang.descr.PatternDescr;
import org.drools.lang.descr.PatternSourceDescr;
import org.drools.lang.descr.PredicateDescr;
import org.drools.lang.descr.QualifiedIdentifierRestrictionDescr;
import org.drools.lang.descr.RestrictionConnectiveDescr;
import org.drools.lang.descr.ReturnValueRestrictionDescr;
import org.drools.lang.descr.RuleDescr;
import org.drools.lang.descr.VariableRestrictionDescr;

/**
 * @author Toni Rikkola
 * 
 */
public class RuleFlattener {

	private Solvers solvers = new Solvers();

	private AnalyticsData data = new AnalyticsDataMaps();

	private AnalyticsRule currentRule = null;
	private Pattern currentPattern = null;
	private Constraint currentConstraint = null;
	private AnalyticsClass currentClass = null;
	private Field currentField = null;

	public void insert(PackageDescr packageDescr) {
		solve(packageDescr.getRules());
	}

	private void solve(List descrs) {

		for (Object o : descrs) {
			BaseDescr descr = (BaseDescr) o;
			if (descr instanceof PackageDescr) {
				solve((PackageDescr) descr);
			} else if (descr instanceof RuleDescr) {
				solve((RuleDescr) descr);
			} else if (descr instanceof PatternDescr) {
				solve((PatternDescr) descr);
			} else if (descr instanceof VariableRestrictionDescr) {
				solve((VariableRestrictionDescr) descr);
			} else if (descr instanceof FieldBindingDescr) {
				solve((FieldBindingDescr) descr);
			} else if (descr instanceof FieldConstraintDescr) {
				solve((FieldConstraintDescr) descr);
			} else if (descr instanceof RestrictionConnectiveDescr) {
				solve((RestrictionConnectiveDescr) descr);
			} else if (descr instanceof LiteralRestrictionDescr) {
				solve((LiteralRestrictionDescr) descr);
			} else if (descr instanceof ReturnValueRestrictionDescr) {
				solve((ReturnValueRestrictionDescr) descr);
			} else if (descr instanceof QualifiedIdentifierRestrictionDescr) {
				solve((QualifiedIdentifierRestrictionDescr) descr);
			} else if (descr instanceof FunctionCallDescr) {
				solve((FunctionCallDescr) descr);
			} else if (descr instanceof PredicateDescr) {
				solve((PredicateDescr) descr);
			} else if (descr instanceof AccessorDescr) {
				solve((AccessorDescr) descr);
			} else if (descr instanceof MethodAccessDescr) {
				solve((MethodAccessDescr) descr);
			} else if (descr instanceof FieldAccessDescr) {
				solve((FieldAccessDescr) descr);
			} else if (descr instanceof PatternSourceDescr) {
				solve((PatternSourceDescr) descr);
			} else if (descr instanceof ConditionalElementDescr) {
				solve((ConditionalElementDescr) descr);
			}
		}
	}

	private AnalyticsComponent solve(PatternSourceDescr descr) {
		if (descr instanceof AccumulateDescr) {
			return solve((AccumulateDescr) descr);
		} else if (descr instanceof CollectDescr) {
			return solve((CollectDescr) descr);
		} else if (descr instanceof FromDescr) {
			return solve((FromDescr) descr);
		}
		return null;
	}

	private AnalyticsComponent solve(DeclarativeInvokerDescr descr) {
		if (descr instanceof AccessorDescr) {
			return solve((AccessorDescr) descr);
		} else if (descr instanceof FieldAccessDescr) {
			return solve((FieldAccessDescr) descr);
		} else if (descr instanceof FunctionCallDescr) {
			return solve((FunctionCallDescr) descr);
		} else if (descr instanceof MethodAccessDescr) {
			return solve((MethodAccessDescr) descr);
		}
		return null;
	}

	private void solve(ConditionalElementDescr descr) {
		if (descr instanceof AndDescr) {
			solve((AndDescr) descr);
		} else if (descr instanceof CollectDescr) {
			solve((CollectDescr) descr);
		} else if (descr instanceof EvalDescr) {
			solve((EvalDescr) descr);
		} else if (descr instanceof ExistsDescr) {
			solve((ExistsDescr) descr);
		} else if (descr instanceof ForallDescr) {
			solve((ForallDescr) descr);
		} else if (descr instanceof FromDescr) {
			solve((FromDescr) descr);
		} else if (descr instanceof NotDescr) {
			solve((NotDescr) descr);
		} else if (descr instanceof OrDescr) {
			solve((OrDescr) descr);
		}
	}

	private void solve(ForallDescr descr) {
		solvers.startForall();
		solve(descr.getDescrs());
		solvers.endForall();
	}

	private void solve(ExistsDescr descr) {
		solvers.startExists();
		solve(descr.getDescrs());
		solvers.endExists();
	}

	private void solve(NotDescr descr) {
		solvers.startNot();
		solve(descr.getDescrs());
		solvers.endNot();
	}

	/**
	 * End
	 * 
	 * @param descr
	 * @return
	 */
	private AnalyticsFunctionCallDescr solve(FunctionCallDescr descr) {
		AnalyticsFunctionCallDescr functionCall = new AnalyticsFunctionCallDescr();
		functionCall.setName(descr.getName());
		functionCall.setArguments(descr.getArguments());

		return functionCall;
	}

	/**
	 * End
	 * 
	 * @param descr
	 * @return
	 */
	private AnalyticsPredicateDescr solve(PredicateDescr descr) {
		AnalyticsPredicateDescr predicate = new AnalyticsPredicateDescr();
		predicate.setContent(descr.getContent().toString());
		predicate.setClassMethodName(descr.getClassMethodName());

		return predicate;
	}

	/**
	 * End
	 * 
	 * @param descr
	 * @return
	 */
	private AnalyticsEvalDescr solve(EvalDescr descr) {
		AnalyticsEvalDescr eval = new AnalyticsEvalDescr();
		eval.setContent(descr.getContent().toString());
		eval.setClassMethodName(descr.getClassMethodName());

		return eval;
	}

	/**
	 * End
	 * 
	 * @param descr
	 * @return
	 */
	private AnalyticsFromDescr solve(FromDescr descr) {
		AnalyticsFromDescr from = new AnalyticsFromDescr();

		AnalyticsComponent ds = solve(descr.getDataSource());
		from.setDataSourceId(ds.getId());
		from.setDataSourceType(ds.getComponentType());

		return from;
	}

	private AnalyticsAccumulateDescr solve(AccumulateDescr descr) {
		AnalyticsAccumulateDescr accumulate = new AnalyticsAccumulateDescr();

		accumulate.setInputPatternId(solve(descr.getInputPattern()));
		accumulate.setInitCode(descr.getInitCode());
		accumulate.setActionCode(descr.getActionCode());
		accumulate.setReverseCode(descr.getReverseCode());
		accumulate.setResultCode(descr.getResultCode());

		// XXX: Array seems to be always null.
		// accumulate.setDeclarations(descr.getDeclarations());

		accumulate.setClassName(descr.getClassName());
		accumulate.setExternalFunction(descr.isExternalFunction());
		accumulate.setFunctionIdentifier(descr.getFunctionIdentifier());
		accumulate.setExpression(descr.getExpression());

		return accumulate;
	}

	private AnalyticsCollectDescr solve(CollectDescr descr) {
		AnalyticsCollectDescr collect = new AnalyticsCollectDescr();
		collect.setClassMethodName(descr.getClassMethodName());
		collect.setInsidePatternId(solve(descr.getInputPattern()));

		return collect;
	}

	private AnalyticsAccessorDescr solve(AccessorDescr descr) {
		AnalyticsAccessorDescr accessor = new AnalyticsAccessorDescr();
		// TODO: I wonder what this descr does.
		return accessor;
	}

	/**
	 * End
	 * 
	 * @param descr
	 */
	private AnalyticsMethodAccessDescr solve(MethodAccessDescr descr) {
		AnalyticsMethodAccessDescr accessor = new AnalyticsMethodAccessDescr();
		accessor.setMethodName(descr.getMethodName());
		accessor.setArguments(descr.getArguments());
		return accessor;
	}

	/**
	 * End
	 * 
	 * @param descr
	 */
	private AnalyticsFieldAccessDescr solve(FieldAccessDescr descr) {
		AnalyticsFieldAccessDescr accessor = new AnalyticsFieldAccessDescr();
		accessor.setFieldName(descr.getFieldName());
		accessor.setArgument(descr.getArgument());
		return accessor;
	}

	private void solve(PackageDescr descr) {
		solve(descr.getRules());
	}

	private void solve(RuleDescr descr) {

		AnalyticsRule rule = new AnalyticsRule();
		rule.setRuleName(descr.getName());
		rule.setRuleSalience(descr.getSalience());
		rule.setConsequence(descr.getConsequence().toString());
		rule.setLineNumber(descr.getLine());
		data.insert(rule);

		currentRule = rule;

		solvers.startRuleSolver(rule);
		solve(descr.getLhs());
		solvers.endRuleSolver();
	}

	private void solve(OrDescr descr) {
		OperatorDescr operatorDescr = OperatorDescr
				.valueOf(OperatorDescr.Type.OR);
		solvers.startOperator(operatorDescr);
		solve(descr.getDescrs());
		solvers.endOperator();
	}

	private void solve(AndDescr descr) {
		OperatorDescr operatorDescr = OperatorDescr
				.valueOf(OperatorDescr.Type.AND);
		solvers.startOperator(operatorDescr);
		solve(descr.getDescrs());
		solvers.endOperator();
	}

	private int solve(PatternDescr descr) {
		AnalyticsClass clazz = data.getClassByName(descr.getObjectType());
		if (clazz == null) {
			clazz = new AnalyticsClass();
			clazz.setName(descr.getObjectType());
			data.insert(clazz);
		}
		currentClass = clazz;

		Pattern pattern = new Pattern();
		pattern.setRuleId(currentRule.getId());
		pattern.setRuleName(currentRule.getRuleName());
		pattern.setClassId(clazz.getId());
		pattern.setPatternNot(solvers.getRuleSolver().isChildNot());
		pattern.setPatternExists(solvers.getRuleSolver().isExists());
		pattern.setPatternForall(solvers.getRuleSolver().isForall());

		data.insert(pattern);
		currentPattern = pattern;

		if (descr.getIdentifier() != null) {
			Variable variable = new Variable();
			variable.setRuleId(currentRule.getId());
			variable.setName(descr.getIdentifier());

			variable.setObjectType(AnalyticsComponentType.CLASS);
			variable.setObjectId(clazz.getId());
			variable.setObjectName(descr.getObjectType());

			data.insert(variable);
		}

		// Solve source.
		if (descr.getSource() != null) {
			AnalyticsComponent source = solve(descr.getSource());
			pattern.setSourceId(source.getId());
			pattern.setSourceType(source.getComponentType());
		} else {
			pattern.setSourceId(0);
			pattern.setSourceType(AnalyticsComponentType.NOTHING);
		}
		solvers.startPatternSolver(pattern);
		solve(descr.getConstraint());
		solvers.endPatternSolver();

		return pattern.getId();
	}

	private void solve(FieldConstraintDescr descr) {
		Field field = data.getFieldByClassAndFieldName(currentClass.getName(),
				descr.getFieldName());
		if (field == null) {
			field = createField(descr.getFieldName(), descr.getLine(),
					currentClass.getId(), currentClass.getName());
			data.insert(field);
		}
		currentField = field;

		Constraint constraint = new Constraint();

		constraint.setRuleId(currentRule.getId());
		constraint.setFieldId(currentField.getId());
		constraint.setPatternId(currentPattern.getId());
		constraint.setPatternIsNot(currentPattern.isPatternNot());
		constraint.setFieldId(field.getId());

		data.insert(constraint);

		currentConstraint = constraint;

		solve(descr.getRestriction());
	}

	private void solve(RestrictionConnectiveDescr descr) {
		// TODO: check.
		solve(descr.getRestrictions());
	}

	/**
	 * End
	 * 
	 * @param descr
	 */
	private void solve(FieldBindingDescr descr) {
		Variable variable = new Variable();
		variable.setRuleId(currentRule.getId());
		variable.setName(descr.getIdentifier());

		variable.setObjectType(AnalyticsComponentType.FIELD);

		data.insert(variable);
	}

	/**
	 * End
	 * 
	 * Foo( bar == $bar )<br>
	 * $bar is a VariableRestrictionDescr
	 * 
	 * @param descr
	 */
	private void solve(VariableRestrictionDescr descr) {
		Variable variable = data.getVariableByRuleAndVariableName(currentRule
				.getRuleName(), descr.getIdentifier());
		VariableRestriction restriction = new VariableRestriction();

		restriction.setRuleId(currentRule.getId());
		restriction.setRuleName(currentRule.getRuleName());
		restriction.setPatternId(currentPattern.getId());
		restriction.setPatternIsNot(currentPattern.isPatternNot());
		restriction.setConstraintId(currentConstraint.getId());
		restriction.setFieldId(currentConstraint.getFieldId());
		restriction.setEvaluator(descr.getEvaluator());
		restriction.setVariableId(variable.getId());
		restriction.setVariableName(descr.getIdentifier());

		// Set field value, if it is unset.
		currentField.setFieldType(Field.FieldType.VARIABLE);

		data.insert(restriction);
		solvers.addRestriction(restriction);
	}

	/**
	 * End
	 * 
	 * @param descr
	 */
	private void solve(ReturnValueRestrictionDescr descr) {
		ReturnValueRestriction restriction = new ReturnValueRestriction();

		restriction.setRuleId(currentRule.getId());
		restriction.setRuleName(currentRule.getRuleName());
		restriction.setPatternId(currentPattern.getId());
		restriction.setPatternIsNot(currentPattern.isPatternNot());
		restriction.setConstraintId(currentConstraint.getId());
		restriction.setFieldId(currentConstraint.getFieldId());
		restriction.setEvaluator(descr.getEvaluator());
		restriction.setClassMethodName(descr.getClassMethodName());
		restriction.setContent(descr.getContent());
		restriction.setDeclarations(descr.getDeclarations());

		data.insert(restriction);
		solvers.addRestriction(restriction);

	}

	/**
	 * End
	 * 
	 * @param descr
	 */
	private void solve(LiteralRestrictionDescr descr) {
		LiteralRestriction restriction = new LiteralRestriction();

		restriction.setRuleId(currentRule.getId());
		restriction.setRuleName(currentRule.getRuleName());
		restriction.setRuleId(currentRule.getId());
		restriction.setPatternId(currentPattern.getId());
		restriction.setPatternIsNot(currentPattern.isPatternNot());
		restriction.setConstraintId(currentConstraint.getId());
		restriction.setFieldId(currentConstraint.getFieldId());
		restriction.setEvaluator(descr.getEvaluator());
		restriction.setValue(descr.getText());

		// Set field value, if it is unset.
		currentField.setFieldType(restriction.getValueType());

		data.insert(restriction);
		solvers.addRestriction(restriction);
	}

	/**
	 * End
	 * 
	 * @param descr
	 */
	private void solve(QualifiedIdentifierRestrictionDescr descr) {
		String text = descr.getText();
		Variable variable = data.getVariableByRuleAndVariableName(currentRule
				.getRuleName(), text.substring(0, text.indexOf(".")));

		QualifiedIdentifierRestriction restriction = new QualifiedIdentifierRestriction();

		restriction.setRuleId(currentRule.getId());
		restriction.setPatternId(currentPattern.getId());
		restriction.setPatternIsNot(currentPattern.isPatternNot());
		restriction.setConstraintId(currentConstraint.getId());
		restriction.setFieldId(currentConstraint.getFieldId());
		restriction.setEvaluator(descr.getEvaluator());
		restriction.setVariableId(variable.getId());
		restriction.setVariableName(text.substring(0, text.indexOf(".")));
		restriction.setVariablePath(text.substring(text.indexOf(".")));

		// Set field value, if it is unset.
		currentField.setFieldType(Field.FieldType.VARIABLE);

		variable.setObjectType(AnalyticsComponentType.FIELD);

		data.insert(restriction);
		solvers.addRestriction(restriction);
	}

	private Field createField(String fieldName, int line, int classId,
			String className) {
		Field field = new Field();
		field.setClassId(classId);
		field.setClassName(className);
		field.setName(fieldName);
		field.setLineNumber(line);

		return field;
	}

	public List<PatternPossibility> getPatternPossibilities() {
		return solvers.getPatternPossibilities();
	}

	public List<RulePossibility> getRulePossibilities() {
		return solvers.getRulePossibilities();
	}

	public Collection<Object> getDataObjects() {
		Collection<Object> objects = new ArrayList<Object>();
		objects.addAll(data.getAll());
		objects.addAll(solvers.getPatternPossibilities());
		objects.addAll(solvers.getRulePossibilities());

		return objects;
	}
}
