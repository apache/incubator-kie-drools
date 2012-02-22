package org.drools.rule.builder;

import org.drools.base.ClassObjectType;
import org.drools.base.ValueType;
import org.drools.base.evaluators.EvaluatorDefinition;
import org.drools.base.mvel.MVELCompilationUnit;
import org.drools.compiler.AnalysisResult;
import org.drools.compiler.DescrBuildError;
import org.drools.compiler.Dialect;
import org.drools.lang.descr.BaseDescr;
import org.drools.lang.descr.LiteralRestrictionDescr;
import org.drools.lang.descr.PredicateDescr;
import org.drools.rule.Declaration;
import org.drools.rule.LiteralConstraint;
import org.drools.rule.LiteralRestriction;
import org.drools.rule.Pattern;
import org.drools.rule.ReturnValueRestriction;
import org.drools.rule.UnificationRestriction;
import org.drools.rule.VariableConstraint;
import org.drools.rule.builder.dialect.mvel.MVELAnalysisResult;
import org.drools.rule.builder.dialect.mvel.MVELDialect;
import org.drools.rule.constraint.BooleanConversionHandler;
import org.drools.rule.constraint.EvaluatorConstraint;
import org.drools.rule.constraint.MvelConstraint;
import org.drools.spi.Constraint;
import org.drools.spi.Evaluator;
import org.drools.spi.FieldValue;
import org.drools.spi.InternalReadAccessor;
import org.drools.spi.KnowledgeHelper;
import org.drools.spi.Restriction;
import org.mvel2.DataConversion;

import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.drools.rule.builder.PatternBuilder.buildAnalysis;
import static org.drools.rule.builder.dialect.DialectUtil.copyErrorLocation;

public class ConstraintBuilder {

    public static final boolean USE_MVEL_EXPRESSION = true;
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

    public static boolean isMvelOperator(String operator) {
        return mvelOperators.contains(operator);
    }

    public static Constraint buildVariableConstraint(RuleBuildContext context,
                                                     Pattern pattern,
                                                     String expression,
                                                     Declaration[] declarations,
                                                     String leftValue,
                                                     String operator,
                                                     String rightValue,
                                                     InternalReadAccessor extractor,
                                                     Restriction restriction) {
        if (USE_MVEL_EXPRESSION) {
            if (!isMvelOperator(operator)) {
                return new EvaluatorConstraint(restriction.getRequiredDeclarations(), restriction.getEvaluator(), extractor);
            }

            boolean isUnification = restriction instanceof UnificationRestriction;
            if (isUnification) {
                expression = resolveUnificationAmbiguity(expression, declarations, leftValue, rightValue);
            }
            boolean isIndexable = operator.equals("==");
            MVELCompilationUnit compilationUnit = null;
            // MVELCompilationUnit compilationUnit = buildCompilationUnit(context, pattern, expression, declarations);
            return new MvelConstraint(context.getPkg().getName(), expression, declarations, compilationUnit, isIndexable, getIndexingDeclaration(restriction), extractor, isUnification);
        } else {
            return new VariableConstraint(extractor, restriction);
        }
    }

    public static Constraint buildLiteralConstraint(RuleBuildContext context,
                                                    Pattern pattern,
                                                    ValueType vtype,
                                                    FieldValue field,
                                                    String expression,
                                                    String leftValue,
                                                    String operator,
                                                    String rightValue,
                                                    InternalReadAccessor extractor,
                                                    LiteralRestrictionDescr restrictionDescr) {
        if (USE_MVEL_EXPRESSION) {
            if (!isMvelOperator(operator)) {
                Evaluator evaluator = buildLiteralEvaluator(context, extractor, restrictionDescr, vtype);
                return new EvaluatorConstraint(field, evaluator, extractor);
            }

            String mvelExpr = normalizeMVELLiteralExpression(vtype, field, expression, leftValue, operator, rightValue, restrictionDescr);
            boolean isIndexable = operator.equals("==");
            MVELCompilationUnit compilationUnit = null;
            // MVELCompilationUnit compilationUnit = buildCompilationUnit(context, pattern, mvelExpr, null);
            return new MvelConstraint(context.getPkg().getName(), mvelExpr, compilationUnit, isIndexable);
        } else {
            LiteralRestriction restriction = buildLiteralRestriction(context, extractor, restrictionDescr, field, vtype);
            return restriction != null ? new LiteralConstraint(extractor, restriction) : null;
        }
    }

    private static String resolveUnificationAmbiguity(String expr, Declaration[] declrations, String leftValue, String rightValue) {
        // resolve ambiguity between variable and bound value with the same name in unifications
        if (leftValue.equals(rightValue)) {
            rightValue = rightValue + "__";
            for (Declaration declaration : declrations) {
                if (declaration.getIdentifier().equals(leftValue)) {
                    declaration.setBindingName(rightValue);
                }
            }
            expr = leftValue + " == " + rightValue;
        }
        return expr;
    }

    private static Declaration getIndexingDeclaration(Restriction restriction) {
        if (restriction instanceof ReturnValueRestriction) return null;
        Declaration[] declarations = restriction.getRequiredDeclarations();
        return declarations != null && declarations.length > 0 ? declarations[0] : null;
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
        Evaluator evaluator = buildLiteralEvaluator(context, extractor, literalRestrictionDescr, vtype);
        return evaluator == null ? null : new LiteralRestriction(field, evaluator, extractor);
    }

    public static Evaluator buildLiteralEvaluator( RuleBuildContext context,
                                                   InternalReadAccessor extractor,
                                                   LiteralRestrictionDescr literalRestrictionDescr,
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
            context.addError( new DescrBuildError( context.getParentDescr(),
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
            context.addError( new DescrBuildError( context.getParentDescr(),
                                                          descr,
                                                          null,
                                                          "Evaluator '" + (isNegated ? "not " : "") + evaluatorString + "' does not support type '" + valueType ) );
        }

        return evaluator;
    }

    public static MVELCompilationUnit buildCompilationUnit(RuleBuildContext context, Pattern pattern, String expression, Declaration[] declarations) {
        Dialect dialect = context.getDialect();
        context.setDialect( context.getDialect( "mvel" ) );

        PredicateDescr predicateDescr = new PredicateDescr( context.getRuleDescr().getResource(), expression );
        AnalysisResult analysis = buildAnalysis(context, pattern, predicateDescr, null);
        MVELCompilationUnit compilationUnit = buildCompilationUnit(context, declarations, null, predicateDescr, analysis);

        context.setDialect(dialect);
        return compilationUnit;
    }

    public static MVELCompilationUnit buildCompilationUnit( final RuleBuildContext context,
                                                            final Declaration[] previousDeclarations,
                                                            final Declaration[] localDeclarations,
                                                            final PredicateDescr predicateDescr,
                                                            final AnalysisResult analysis ) {
        if (context.isTypesafe() && analysis instanceof MVELAnalysisResult) {
            Class<?> returnClass = ((MVELAnalysisResult)analysis).getReturnType();
            if (returnClass != Boolean.class && returnClass != Boolean.TYPE) {
                context.addError( new DescrBuildError( context.getParentDescr(),
                        predicateDescr,
                        null,
                        "Predicate '" + predicateDescr.getContent() + "' must be a Boolean expression\n" + predicateDescr.positionAsString() ) );
            }
        }

        MVELDialect dialect = (MVELDialect) context.getDialect( context.getDialect().getId() );

        MVELCompilationUnit unit = null;

        try {
            Map<String, Class< ? >> declIds = context.getDeclarationResolver().getDeclarationClasses( context.getRule() );

            Pattern p = (Pattern) context.getBuildStack().peek();
            if ( p.getObjectType() instanceof ClassObjectType) {
                declIds.put( "this",
                        ((ClassObjectType) p.getObjectType()).getClassType() );
            }

            unit = dialect.getMVELCompilationUnit( (String) predicateDescr.getContent(),
                                                    analysis,
                                                    previousDeclarations,
                                                    localDeclarations,
                                                    null,
                                                    context,
                                                    "drools",
                                                    KnowledgeHelper.class );
        } catch ( final Exception e ) {
            copyErrorLocation(e, predicateDescr);
            context.addError( new DescrBuildError( context.getParentDescr(),
                    predicateDescr,
                    e,
                    "Unable to build expression for 'inline-eval' : " + e.getMessage() + "'" + predicateDescr.getContent() + "'\n" + e.getMessage() ) );
        }

        return unit;
    }
}
