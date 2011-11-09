package org.drools.rule.builder;

import org.drools.base.ValueType;
import org.drools.base.evaluators.EvaluatorDefinition;
import org.drools.compiler.DescrBuildError;
import org.drools.lang.descr.BaseDescr;
import org.drools.lang.descr.LiteralRestrictionDescr;
import org.drools.rule.LiteralConstraint;
import org.drools.rule.LiteralRestriction;
import org.drools.rule.constraint.BooleanConversionHandler;
import org.drools.rule.constraint.MvelLiteralConstraint;
import org.drools.rule.constraint.SoundexLiteralContraint;
import org.drools.spi.Constraint;
import org.drools.spi.Evaluator;
import org.drools.spi.FieldValue;
import org.drools.spi.InternalReadAccessor;
import org.mvel2.DataConversion;

import java.util.Date;
import java.util.Set;

public class ConstraintBuilder {

    private static final boolean USE_MVEL_EXPRESSION = false;

    static {
        if (USE_MVEL_EXPRESSION) {
            DataConversion.addConversionHandler(Boolean.class, BooleanConversionHandler.INSTANCE);
            DataConversion.addConversionHandler(boolean.class, BooleanConversionHandler.INSTANCE);
        }
    }

    public static Constraint buildConstraint(RuleBuildContext context,
                                             ValueType vtype,
                                             FieldValue field,
                                             String expr,
                                             String value1,
                                             String operator,
                                             String value2,
                                             InternalReadAccessor extractor,
                                             LiteralRestrictionDescr restrictionDescr) {
        if (USE_MVEL_EXPRESSION) {
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
        Set<String> imports = context.getPkg().getImports().keySet();

        if (operator.equals("soundslike")) {
            return new SoundexLiteralContraint(imports, leftValue, operator, rightValue);
        }

        String mvelExpr = normalizeMVELExpression(vtype, field, expr, leftValue, operator, rightValue, restrictionDescr);
        return new MvelLiteralConstraint(imports, vtype, mvelExpr, leftValue, operator, rightValue);
    }

    private static String normalizeMVELExpression(ValueType vtype,
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
        return expr;
    }

    public static LiteralRestriction buildLiteralRestriction( final RuleBuildContext context,
                                                        final InternalReadAccessor extractor,
                                                        final LiteralRestrictionDescr literalRestrictionDescr,
                                                        final FieldValue field,
                                                        final ValueType vtype) {
        EvaluatorDefinition.Target right = getRightTarget( extractor );
        EvaluatorDefinition.Target left = EvaluatorDefinition.Target.FACT;
        final Evaluator evaluator = getEvaluator( context,
                                                  literalRestrictionDescr,
                                                  vtype,
                                                  literalRestrictionDescr.getEvaluator(),
                                                  literalRestrictionDescr.isNegated(),
                                                  literalRestrictionDescr.getParameterText(),
                                                  left,
                                                  right );
        if ( evaluator == null ) {
            return null;
        }

        return new LiteralRestriction( field,
                                       evaluator,
                                       extractor );
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
