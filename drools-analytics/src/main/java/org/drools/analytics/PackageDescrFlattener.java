package org.drools.analytics;

import java.util.Collection;
import java.util.Stack;

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
import org.drools.analytics.components.RulePackage;
import org.drools.analytics.components.RulePossibility;
import org.drools.analytics.components.Variable;
import org.drools.analytics.components.VariableRestriction;
import org.drools.analytics.dao.AnalyticsData;
import org.drools.analytics.dao.AnalyticsDataFactory;
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
public class PackageDescrFlattener {

	private Solvers solvers = new Solvers();

	private Stack<AnalyticsComponent> components = new Stack<AnalyticsComponent>();

	private RulePackage currentPackage = null;
	private AnalyticsRule currentRule = null;
	private Pattern currentPattern = null;
	private Constraint currentConstraint = null;
	private AnalyticsClass currentClass = null;
	private Field currentField = null;

	public void insert(PackageDescr packageDescr) {

		flatten(packageDescr);

		formPossibilities();
	}

	private void flatten(Collection<Object> descrs) {

		for (Object o : descrs) {
			BaseDescr descr = (BaseDescr) o;
			if (descr instanceof PackageDescr) {
				flatten((PackageDescr) descr);
			} else if (descr instanceof RuleDescr) {
				flatten((RuleDescr) descr);
			} else if (descr instanceof PatternDescr) {
				flatten((PatternDescr) descr);
			} else if (descr instanceof VariableRestrictionDescr) {
				flatten((VariableRestrictionDescr) descr);
			} else if (descr instanceof FieldBindingDescr) {
				flatten((FieldBindingDescr) descr);
			} else if (descr instanceof FieldConstraintDescr) {
				flatten((FieldConstraintDescr) descr);
			} else if (descr instanceof RestrictionConnectiveDescr) {
				flatten((RestrictionConnectiveDescr) descr);
			} else if (descr instanceof LiteralRestrictionDescr) {
				flatten((LiteralRestrictionDescr) descr);
			} else if (descr instanceof ReturnValueRestrictionDescr) {
				flatten((ReturnValueRestrictionDescr) descr);
			} else if (descr instanceof QualifiedIdentifierRestrictionDescr) {
				flatten((QualifiedIdentifierRestrictionDescr) descr);
			} else if (descr instanceof FunctionCallDescr) {
				flatten((FunctionCallDescr) descr);
			} else if (descr instanceof PredicateDescr) {
				flatten((PredicateDescr) descr);
			} else if (descr instanceof AccessorDescr) {
				flatten((AccessorDescr) descr);
			} else if (descr instanceof MethodAccessDescr) {
				flatten((MethodAccessDescr) descr);
			} else if (descr instanceof FieldAccessDescr) {
				flatten((FieldAccessDescr) descr);
			} else if (descr instanceof PatternSourceDescr) {
				flatten((PatternSourceDescr) descr);
			} else if (descr instanceof ConditionalElementDescr) {
				flatten((ConditionalElementDescr) descr);
			}
		}
	}

	private AnalyticsComponent flatten(PatternSourceDescr descr) {
		if (descr instanceof AccumulateDescr) {
			return flatten((AccumulateDescr) descr);
		} else if (descr instanceof CollectDescr) {
			return flatten((CollectDescr) descr);
		} else if (descr instanceof FromDescr) {
			return flatten((FromDescr) descr);
		}
		return null;
	}

	private AnalyticsComponent flatten(DeclarativeInvokerDescr descr) {
		if (descr instanceof AccessorDescr) {
			return flatten((AccessorDescr) descr);
		} else if (descr instanceof FieldAccessDescr) {
			return flatten((FieldAccessDescr) descr);
		} else if (descr instanceof FunctionCallDescr) {
			return flatten((FunctionCallDescr) descr);
		} else if (descr instanceof MethodAccessDescr) {
			return flatten((MethodAccessDescr) descr);
		}
		return null;
	}

	private void flatten(ConditionalElementDescr descr) {
		if (descr instanceof AndDescr) {
			flatten((AndDescr) descr);
		} else if (descr instanceof CollectDescr) {
			flatten((CollectDescr) descr);
		} else if (descr instanceof EvalDescr) {
			flatten((EvalDescr) descr);
		} else if (descr instanceof ExistsDescr) {
			flatten((ExistsDescr) descr);
		} else if (descr instanceof ForallDescr) {
			flatten((ForallDescr) descr);
		} else if (descr instanceof FromDescr) {
			flatten((FromDescr) descr);
		} else if (descr instanceof NotDescr) {
			flatten((NotDescr) descr);
		} else if (descr instanceof OrDescr) {
			flatten((OrDescr) descr);
		}
	}

	private void flatten(ForallDescr descr) {
		solvers.startForall();
		flatten(descr.getDescrs());
		solvers.endForall();
	}

	private void flatten(ExistsDescr descr) {
		solvers.startExists();
		flatten(descr.getDescrs());
		solvers.endExists();
	}

	private void flatten(NotDescr descr) {
		solvers.startNot();
		flatten(descr.getDescrs());
		solvers.endNot();
	}

	/**
	 * End
	 * 
	 * @param descr
	 * @return
	 */
	private AnalyticsFunctionCallDescr flatten(FunctionCallDescr descr) {
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
	private AnalyticsPredicateDescr flatten(PredicateDescr descr) {
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
	private AnalyticsEvalDescr flatten(EvalDescr descr) {
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
	private AnalyticsFromDescr flatten(FromDescr descr) {
		AnalyticsFromDescr from = new AnalyticsFromDescr();

		AnalyticsComponent ds = flatten(descr.getDataSource());
		from.setDataSourceId(ds.getId());
		from.setDataSourceType(ds.getComponentType());

		return from;
	}

	private AnalyticsAccumulateDescr flatten(AccumulateDescr descr) {
		AnalyticsAccumulateDescr accumulate = new AnalyticsAccumulateDescr();

		accumulate.setInputPatternId(flatten(descr.getInputPattern()));
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

	private AnalyticsCollectDescr flatten(CollectDescr descr) {
		AnalyticsCollectDescr collect = new AnalyticsCollectDescr();
		collect.setClassMethodName(descr.getClassMethodName());
		collect.setInsidePatternId(flatten(descr.getInputPattern()));

		return collect;
	}

	private AnalyticsAccessorDescr flatten(AccessorDescr descr) {
		AnalyticsAccessorDescr accessor = new AnalyticsAccessorDescr();
		// TODO: I wonder what this descr does.
		return accessor;
	}

	/**
	 * End
	 * 
	 * @param descr
	 */
	private AnalyticsMethodAccessDescr flatten(MethodAccessDescr descr) {
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
	private AnalyticsFieldAccessDescr flatten(FieldAccessDescr descr) {
		AnalyticsFieldAccessDescr accessor = new AnalyticsFieldAccessDescr();
		accessor.setFieldName(descr.getFieldName());
		accessor.setArgument(descr.getArgument());
		return accessor;
	}

	private void flatten(PackageDescr descr) {
		AnalyticsData data = AnalyticsDataFactory.getAnalyticsData();
		RulePackage rulePackage = data.getRulePackageByName(descr.getName());

		if (rulePackage == null) {
			rulePackage = new RulePackage();

			rulePackage.setName(descr.getName());
			data.save(rulePackage);
		}

		currentPackage = rulePackage;

		components.push(rulePackage);
		flatten(descr.getRules());
		components.pop();
	}

	private void flatten(RuleDescr descr) {
		AnalyticsData data = AnalyticsDataFactory.getAnalyticsData();

		AnalyticsRule rule = new AnalyticsRule();
		rule.setRuleName(descr.getName());
		rule.setRuleSalience(descr.getSalience());
		rule.setConsequence(descr.getConsequence().toString());
		rule.setLineNumber(descr.getLine());
		rule.setPackageId(currentPackage.getId());

		data.save(rule);

		currentPackage.getRules().add(rule);
		currentRule = rule;

		solvers.startRuleSolver(rule);
		flatten(descr.getLhs());
		solvers.endRuleSolver();
	}

	private void flatten(OrDescr descr) {
		OperatorDescr operatorDescr = OperatorDescr
				.valueOf(OperatorDescr.Type.OR);
		solvers.startOperator(operatorDescr);
		flatten(descr.getDescrs());
		solvers.endOperator();
	}

	private void flatten(AndDescr descr) {
		OperatorDescr operatorDescr = OperatorDescr
				.valueOf(OperatorDescr.Type.AND);
		solvers.startOperator(operatorDescr);
		flatten(descr.getDescrs());
		solvers.endOperator();
	}

	private int flatten(PatternDescr descr) {
		AnalyticsData data = AnalyticsDataFactory.getAnalyticsData();

		AnalyticsClass clazz = data.getClassByPackageAndName(descr
				.getObjectType());
		if (clazz == null) {
			clazz = new AnalyticsClass();
			clazz.setName(descr.getObjectType());
			data.save(clazz);
		}
		currentClass = clazz;

		Pattern pattern = new Pattern();
		pattern.setRuleId(currentRule.getId());
		pattern.setRuleName(currentRule.getRuleName());
		pattern.setClassId(clazz.getId());
		pattern.setPatternNot(solvers.getRuleSolver().isChildNot());
		pattern.setPatternExists(solvers.getRuleSolver().isExists());
		pattern.setPatternForall(solvers.getRuleSolver().isForall());

		data.save(pattern);
		currentPattern = pattern;

		if (descr.getIdentifier() != null) {
			Variable variable = new Variable();
			variable.setRuleId(currentRule.getId());
			variable.setName(descr.getIdentifier());

			variable.setObjectType(AnalyticsComponentType.CLASS);
			variable.setObjectId(clazz.getId());
			variable.setObjectName(descr.getObjectType());

			data.save(variable);
		}

		// flatten source.
		if (descr.getSource() != null) {
			AnalyticsComponent source = flatten(descr.getSource());
			pattern.setSourceId(source.getId());
			pattern.setSourceType(source.getComponentType());
		} else {
			pattern.setSourceId(0);
			pattern.setSourceType(AnalyticsComponentType.NOTHING);
		}
		solvers.startPatternSolver(pattern);
		flatten(descr.getConstraint());
		solvers.endPatternSolver();

		return pattern.getId();
	}

	private void flatten(FieldConstraintDescr descr) {
		AnalyticsData data = AnalyticsDataFactory.getAnalyticsData();

		Field field = data.getFieldByClassAndFieldName(currentClass.getName(),
				descr.getFieldName());
		if (field == null) {
			field = createField(descr.getFieldName(), descr.getLine(),
					currentClass.getId(), currentClass.getName());
			data.save(field);
		}
		currentField = field;

		Constraint constraint = new Constraint();

		constraint.setRuleId(currentRule.getId());
		constraint.setFieldId(currentField.getId());
		constraint.setPatternId(currentPattern.getId());
		constraint.setPatternIsNot(currentPattern.isPatternNot());
		constraint.setFieldId(field.getId());

		data.save(constraint);

		currentConstraint = constraint;

		flatten(descr.getRestriction());
	}

	private void flatten(RestrictionConnectiveDescr descr) {
		// TODO: check.
		flatten(descr.getRestrictions());
	}

	/**
	 * End
	 * 
	 * @param descr
	 */
	private void flatten(FieldBindingDescr descr) {
		AnalyticsData data = AnalyticsDataFactory.getAnalyticsData();

		Variable variable = new Variable();
		variable.setRuleId(currentRule.getId());
		variable.setName(descr.getIdentifier());

		variable.setObjectType(AnalyticsComponentType.FIELD);

		data.save(variable);
	}

	/**
	 * End
	 * 
	 * Foo( bar == $bar )<br>
	 * $bar is a VariableRestrictionDescr
	 * 
	 * @param descr
	 */
	private void flatten(VariableRestrictionDescr descr) {
		AnalyticsData data = AnalyticsDataFactory.getAnalyticsData();

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

		data.save(restriction);
		solvers.addRestriction(restriction);
	}

	/**
	 * End
	 * 
	 * @param descr
	 */
	private void flatten(ReturnValueRestrictionDescr descr) {
		AnalyticsData data = AnalyticsDataFactory.getAnalyticsData();

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

		data.save(restriction);
		solvers.addRestriction(restriction);

	}

	/**
	 * End
	 * 
	 * @param descr
	 */
	private void flatten(LiteralRestrictionDescr descr) {
		AnalyticsData data = AnalyticsDataFactory.getAnalyticsData();

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

		data.save(restriction);
		solvers.addRestriction(restriction);
	}

	/**
	 * End
	 * 
	 * @param descr
	 */
	private void flatten(QualifiedIdentifierRestrictionDescr descr) {
		AnalyticsData data = AnalyticsDataFactory.getAnalyticsData();

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

		data.save(restriction);
		solvers.addRestriction(restriction);
	}

	private Field createField(String fieldName, int line, int classId,
			String className) {
		Field field = new Field();
		field.setClassId(classId);
		field.setClassName(className);
		field.setName(fieldName);
		field.setLineNumber(line);

		currentClass.getFields().add(field);
		return field;
	}

	private void formPossibilities() {
		AnalyticsData data = AnalyticsDataFactory.getAnalyticsData();

		for (PatternPossibility possibility : solvers.getPatternPossibilities()) {
			data.save(possibility);
		}
		for (RulePossibility possibility : solvers.getRulePossibilities()) {
			data.save(possibility);
		}
	}
}
