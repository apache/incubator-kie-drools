package org.drools.rule.builder;

import org.drools.base.ValueType;
import org.drools.base.evaluators.EvaluatorDefinition;
import org.drools.compiler.DescrBuildError;
import org.drools.lang.descr.BaseDescr;
import org.drools.lang.descr.LiteralRestrictionDescr;
import org.drools.rule.Declaration;
import org.drools.rule.LiteralConstraint;
import org.drools.rule.LiteralRestriction;
import org.drools.rule.MVELDialectRuntimeData;
import org.drools.rule.ReturnValueRestriction;
import org.drools.rule.UnificationRestriction;
import org.drools.rule.VariableConstraint;
import org.drools.rule.constraint.BooleanConversionHandler;
import org.drools.rule.constraint.EvaluatorConstraint;
import org.drools.rule.constraint.MvelConstraint;
import org.drools.spi.Constraint;
import org.drools.spi.Evaluator;
import org.drools.spi.FieldValue;
import org.drools.spi.InternalReadAccessor;
import org.drools.spi.Restriction;
import org.mvel2.DataConversion;
import org.mvel2.ParserConfiguration;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class ConstraintBuilder {

    private static final boolean USE_MVEL_EXPRESSION = true;
    private static Set<String> mvelOperators;

    static {
        if (USE_MVEL_EXPRESSION) {
            DataConversion.addConversionHandler(Boolean.class, BooleanConversionHandler.INSTANCE);
            DataConversion.addConversionHandler(boolean.class, BooleanConversionHandler.INSTANCE);

            mvelOperators = new HashSet<String>() {{
                add("==");
                add("!=");
                add(">");
                add(">=");
                add("<");
                add("<=");
                add("str");
                add("contains");
                add("matches");
                add("excludes");
                add("memberOf");
            }};
        }
    }

    private static boolean isMvelOperator(String operator) {
        return mvelOperators.contains(operator);
    }

    public static Constraint buildVariableConstraint(RuleBuildContext context,
                                                     String expr,
                                                     Declaration[] declrations,
                                                     String leftValue,
                                                     String operator,
                                                     String rightValue,
                                                     InternalReadAccessor extractor,
                                                     Restriction restriction) {
        if (USE_MVEL_EXPRESSION) {
            if (!isMvelOperator(operator)) {
                return new EvaluatorConstraint(restriction.getRequiredDeclarations(), restriction.getEvaluator(), extractor);
            }

            String packageName = context.getPkg().getName();
            ParserConfiguration conf = getParserConfiguration(context);

            // resolve ambiguity between variable and bound value with the same name in unifications
            if (restriction instanceof UnificationRestriction && leftValue.equals(rightValue)) {
                rightValue = rightValue + "__";
                for (Declaration declaration : declrations) {
                    if (declaration.getIdentifier().equals(leftValue)) {
                        declaration.setBindingName(rightValue);
                    }
                }
                expr = leftValue + " == " + rightValue;
            }

            return new MvelConstraint(conf, packageName, expr, operator, declrations, getIndexingDeclaration(restriction), extractor);
        } else {
            return new VariableConstraint(extractor, restriction);
        }
    }

    private static Declaration getIndexingDeclaration(Restriction restriction) {
        if (restriction instanceof ReturnValueRestriction) return null;
        Declaration[] declarations = restriction.getRequiredDeclarations();
        return declarations != null && declarations.length > 0 ? declarations[0] : null;
    }

    public static Constraint buildLiteralConstraint(RuleBuildContext context,
                                                    ValueType vtype,
                                                    FieldValue field,
                                                    String expr,
                                                    String value1,
                                                    String operator,
                                                    String value2,
                                                    InternalReadAccessor extractor,
                                                    LiteralRestrictionDescr restrictionDescr) {
        if (USE_MVEL_EXPRESSION) {
            if (!isMvelOperator(operator)) {
                Evaluator evaluator = buildLiteralEvaluator(context, extractor, restrictionDescr, field, vtype);
                return new EvaluatorConstraint(field, evaluator, extractor);
            }

            return buildMVELConstraint(context, vtype, field, expr, value1, operator, value2, restrictionDescr);
        } else {
            LiteralRestriction restriction = buildLiteralRestriction(context, extractor, restrictionDescr, field, vtype);
            if (restriction != null) {
                return new LiteralConstraint( extractor, restriction );
            }
        }
        return null;
    }

    private static Constraint buildMVELConstraint(RuleBuildContext context,
                                          ValueType vtype,
                                          FieldValue field,
                                          String expr,
                                          String leftValue,
                                          String operator,
                                          String rightValue,
                                          LiteralRestrictionDescr restrictionDescr) {
        String packageName = context.getPkg().getName();
        ParserConfiguration conf = getParserConfiguration(context);

        String mvelExpr = normalizeMVELLiteralExpression(vtype, field, expr, leftValue, operator, rightValue, restrictionDescr);

        return new MvelConstraint(conf, packageName, mvelExpr, operator);
    }

    private static ParserConfiguration getParserConfiguration(RuleBuildContext context) {
        MVELDialectRuntimeData data = (MVELDialectRuntimeData) context.getPkg().getDialectRuntimeRegistry().getDialectData( "mvel" );
        return data.getParserConfiguration();
    }

    private static String normalizeMVELLiteralExpression(ValueType vtype,
                                                         FieldValue field,
                                                         String expr,
                                                         String leftValue,
                                                         String operator,
                                                         String rightValue,
                                                         LiteralRestrictionDescr restrictionDescr) {
        if (vtype == ValueType.DATE_TYPE) {
            Date date = (Date)field.getValue();
            return leftValue + " " + operator + (date != null ? " new java.util.Date(" + date.getTime() + ")" : " null");
        }
        if (operator.equals("str")) {
            String method = restrictionDescr.getParameterText();
            if (method.equals("length")) {
                return leftValue + ".length()" + (restrictionDescr.isNegated() ? " != " : " == ") + rightValue;
            }
            return (restrictionDescr.isNegated() ? "!" : "") + leftValue + "." + method + "(" + rightValue + ")";
        }

        // resolve ambiguity between mvel's "empty" keyword and constraints like: List(empty == ...)
        if (expr.startsWith("empty") && (operator.equals("==") || operator.equals("!="))) {
            expr = "isEmpty()" + expr.substring(5);
        }
        return expr;
    }

    public static LiteralRestriction buildLiteralRestriction( RuleBuildContext context,
                                                              InternalReadAccessor extractor,
                                                              LiteralRestrictionDescr literalRestrictionDescr,
                                                              FieldValue field,
                                                              ValueType vtype) {
        Evaluator evaluator = buildLiteralEvaluator(context, extractor, literalRestrictionDescr, field, vtype);
        return evaluator == null ? null : new LiteralRestriction(field, evaluator, extractor);
    }

    public static Evaluator buildLiteralEvaluator( RuleBuildContext context,
                                                   InternalReadAccessor extractor,
                                                   LiteralRestrictionDescr literalRestrictionDescr,
                                                   FieldValue field,
                                                   ValueType vtype) {
        EvaluatorDefinition.Target right = getRightTarget( extractor );
        EvaluatorDefinition.Target left = EvaluatorDefinition.Target.FACT;
        return getEvaluator( context,
                             literalRestrictionDescr,
                             vtype,
                             literalRestrictionDescr.getEvaluator(),
                             literalRestrictionDescr.isNegated(),
                             literalRestrictionDescr.getParameterText(),
                             left,
                             right );
    }

    public static EvaluatorDefinition.Target getRightTarget( final InternalReadAccessor extractor ) {
        return (extractor.isSelfReference() && !(Date.class.isAssignableFrom( extractor.getExtractToClass() ) || Number.class.isAssignableFrom( extractor.getExtractToClass() ))) ? EvaluatorDefinition.Target.HANDLE : EvaluatorDefinition.Target.FACT;
    }

    public static Evaluator getEvaluator( final RuleBuildContext context,
                                    final BaseDescr descr,
                                    final ValueType valueType,
                                    final String evaluatorString,
                                    final boolean isNegated,
                                    final String parameters,
                                    final EvaluatorDefinition.Target left,
                                    final EvaluatorDefinition.Target right ) {

        final EvaluatorDefinition def = context.getConfiguration().getEvaluatorRegistry().getEvaluatorDefinition( evaluatorString );
        if ( def == null ) {
            context.getErrors().add( new DescrBuildError( context.getParentDescr(),
                                                          descr,
                                                          null,
                                                          "Unable to determine the Evaluator for ID '" + evaluatorString + "'" ) );
            return null;
        }

        final Evaluator evaluator = def.getEvaluator( valueType,
                                                      evaluatorString,
                                                      isNegated,
                                                      parameters,
                                                      left,
                                                      right );

        if ( evaluator == null ) {
            context.getErrors().add( new DescrBuildError( context.getParentDescr(),
                                                          descr,
                                                          null,
                                                          "Evaluator '" + (isNegated ? "not " : "") + evaluatorString + "' does not support type '" + valueType ) );
        }

        return evaluator;
    }
}
