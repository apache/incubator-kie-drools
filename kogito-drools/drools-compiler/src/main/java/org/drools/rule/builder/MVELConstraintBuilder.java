package org.drools.rule.builder;

import org.drools.base.ClassObjectType;
import org.drools.base.DroolsQuery;
import org.drools.base.EvaluatorWrapper;
import org.drools.base.ValueType;
import org.drools.base.evaluators.EvaluatorDefinition;
import org.drools.base.evaluators.Operator;
import org.drools.base.mvel.MVELCompilationUnit;
import org.drools.compiler.AnalysisResult;
import org.drools.compiler.DescrBuildError;
import org.drools.compiler.Dialect;
import org.drools.lang.descr.*;
import org.drools.rule.Declaration;
import org.drools.rule.Pattern;
import org.drools.rule.ReturnValueRestriction;
import org.drools.rule.builder.dialect.mvel.MVELAnalysisResult;
import org.drools.rule.builder.dialect.mvel.MVELDialect;
import org.drools.rule.constraint.EvaluatorConstraint;
import org.drools.rule.constraint.MvelConstraint;
import org.drools.spi.Constraint;
import org.drools.spi.Evaluator;
import org.drools.spi.FieldValue;
import org.drools.spi.InternalReadAccessor;
import org.drools.spi.KnowledgeHelper;
import org.drools.spi.Restriction;
import org.mvel2.ConversionHandler;
import org.mvel2.DataConversion;
import org.mvel2.util.CompatibilityStrategy;

import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.drools.rule.builder.PatternBuilder.buildAnalysis;
import static org.drools.rule.builder.PatternBuilder.getUsedDeclarations;
import static org.drools.rule.builder.dialect.DialectUtil.copyErrorLocation;

public class MVELConstraintBuilder implements ConstraintBuilder {

    public static final boolean USE_MVEL_EXPRESSION = true;
    protected static Set<String> mvelOperators;

    static {
        if (USE_MVEL_EXPRESSION) {
            CompatibilityStrategy.setCompatibilityEvaluator(StringCoercionCompatibilityEvaluator.INSTANCE);
            DataConversion.addConversionHandler(Boolean.class, BooleanConversionHandler.INSTANCE);
            DataConversion.addConversionHandler(boolean.class, BooleanConversionHandler.INSTANCE);

            mvelOperators = new HashSet<String>() {{
                add("==");
                add("!=");
                add(">");
                add(">=");
                add("<");
                add("<=");
                add("~=");
                add("str");
                add("contains");
                add("matches");
                add("excludes");
                add("memberOf");
            }};
        }
    }

    public boolean isMvelOperator(String operator) {
        return mvelOperators.contains(operator);
    }

    public boolean useMvelExpression() {
        return USE_MVEL_EXPRESSION;
    }

    public Constraint buildVariableConstraint(RuleBuildContext context,
                                              Pattern pattern,
                                              String expression,
                                              Declaration[] declarations,
                                              String leftValue,
                                              OperatorDescr operator,
                                              String rightValue,
                                              InternalReadAccessor extractor,
                                              Declaration requiredDeclaration,
                                              RelationalExprDescr relDescr) {
        if (!isMvelOperator(operator.getOperator())) {
            EvaluatorDefinition.Target right = getRightTarget( extractor );
            EvaluatorDefinition.Target left = (requiredDeclaration.isPatternDeclaration() && !(Date.class.isAssignableFrom( requiredDeclaration.getExtractor().getExtractToClass() ) || Number.class.isAssignableFrom( requiredDeclaration.getExtractor().getExtractToClass() ))) ? EvaluatorDefinition.Target.HANDLE : EvaluatorDefinition.Target.FACT;
            final Evaluator evaluator = getEvaluator( context,
                                                      relDescr,
                                                      extractor.getValueType(),
                                                      operator.getOperator(),
                                                      relDescr.isNegated(),
                                                      relDescr.getParametersText(),
                                                      left,
                                                      right );
            return new EvaluatorConstraint(new Declaration[] { requiredDeclaration }, evaluator, extractor);
        }

        boolean isUnification = requiredDeclaration != null && requiredDeclaration.getPattern().getObjectType().equals( new ClassObjectType( DroolsQuery.class ) ) && Operator.EQUAL.getOperatorString().equals( operator );
        if (isUnification) {
            expression = resolveUnificationAmbiguity(expression, declarations, leftValue, rightValue);
        }
        boolean isIndexable = operator.equals("==");
        MVELCompilationUnit compilationUnit = isUnification ? null : buildCompilationUnit(context, pattern, expression);
        return new MvelConstraint(context.getPkg().getName(), expression, declarations, compilationUnit, isIndexable, requiredDeclaration, extractor, isUnification);
    }

    public Constraint buildMvelConstraint(String packageName, String expression, Declaration[] declarations, MVELCompilationUnit compilationUnit, boolean isDynamic) {
        return new MvelConstraint( packageName, expression, declarations, compilationUnit, isDynamic );
    }

    public Constraint buildMvelConstraint(String packageName, String expression, Declaration[] declarations, MVELCompilationUnit compilationUnit, boolean isDynamic, PredicateDescr base ) {
        return new MvelConstraint( packageName, expression, declarations, compilationUnit, isDynamic );
    }

    public Constraint buildMvelConstraint(String packageName, String expression, MVELCompilationUnit compilationUnit, boolean isIndexable, FieldValue fieldValue, InternalReadAccessor extractor) {
        return new MvelConstraint( packageName, expression, compilationUnit, isIndexable, fieldValue, extractor );
    }

    public Constraint buildMvelConstraint(String packageName, String expression, Declaration[] declarations, MVELCompilationUnit compilationUnit, boolean isIndexable, Declaration indexingDeclaration, InternalReadAccessor extractor, boolean isUnification) {
        return new MvelConstraint( packageName, expression, declarations, compilationUnit, isIndexable, indexingDeclaration, extractor, isUnification );
    }

    public Constraint buildLiteralConstraint(RuleBuildContext context,
                                             Pattern pattern,
                                             ValueType vtype,
                                             FieldValue field,
                                             String expression,
                                             String leftValue,
                                             String operator,
                                             String rightValue,
                                             InternalReadAccessor extractor,
                                             LiteralRestrictionDescr restrictionDescr) {
        if (!isMvelOperator(operator)) {
            Evaluator evaluator = buildLiteralEvaluator(context, extractor, restrictionDescr, vtype);
            return new EvaluatorConstraint(field, evaluator, extractor);
        }

        String mvelExpr = normalizeMVELLiteralExpression(vtype, field, expression, leftValue, operator, rightValue, restrictionDescr);
        boolean isIndexable = operator.equals("==");
        MVELCompilationUnit compilationUnit = buildCompilationUnit(context, pattern, mvelExpr);
        return new MvelConstraint(context.getPkg().getName(), mvelExpr, compilationUnit, isIndexable, field, extractor);
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

    protected static Declaration getIndexingDeclaration(Restriction restriction) {
        if (restriction instanceof ReturnValueRestriction) return null;
        Declaration[] declarations = restriction.getRequiredDeclarations();
        return declarations != null && declarations.length > 0 ? declarations[0] : null;
    }


    public Evaluator buildLiteralEvaluator( RuleBuildContext context,
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

    public EvaluatorDefinition.Target getRightTarget( final InternalReadAccessor extractor ) {
        return (extractor.isSelfReference() && !(Date.class.isAssignableFrom( extractor.getExtractToClass() ) || Number.class.isAssignableFrom( extractor.getExtractToClass() ))) ? EvaluatorDefinition.Target.HANDLE : EvaluatorDefinition.Target.FACT;
    }

    public Evaluator getEvaluator( final RuleBuildContext context,
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

    public EvaluatorWrapper wrapEvaluator( Evaluator evaluator, Declaration left, Declaration right ) {
        return new EvaluatorWrapper( evaluator, left, right );
    }

    public MVELCompilationUnit buildCompilationUnit(RuleBuildContext context, Pattern pattern, String expression) {
        Dialect dialect = context.getDialect();
        context.setDialect( context.getDialect( "mvel" ) );

        PredicateDescr predicateDescr = new PredicateDescr( context.getRuleDescr().getResource(), expression );
        AnalysisResult analysis = buildAnalysis(context, pattern, predicateDescr, null );
        if ( analysis == null ) {
            // something bad happened
            return null;
        }

        Declaration[][] usedDeclarations = getUsedDeclarations(context, pattern, analysis);
        MVELCompilationUnit compilationUnit = buildCompilationUnit(context, usedDeclarations[0], usedDeclarations[1], predicateDescr, analysis);

        context.setDialect(dialect);
        return compilationUnit;
    }

    public MVELCompilationUnit buildCompilationUnit( final RuleBuildContext context,
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
                                                    KnowledgeHelper.class,
                                                    false );
        } catch ( final Exception e ) {
            copyErrorLocation(e, predicateDescr);
            context.addError( new DescrBuildError( context.getParentDescr(),
                    predicateDescr,
                    e,
                    "Unable to build expression for 'inline-eval' : " + e.getMessage() + "'" + predicateDescr.getContent() + "'\n" + e.getMessage() ) );
        }

        return unit;
    }

    public static class BooleanConversionHandler implements ConversionHandler {

        private static final BooleanConversionHandler INSTANCE = new BooleanConversionHandler();

        private BooleanConversionHandler() { }

        public Object convertFrom(Object in) {
            if (in.getClass() == Boolean.class || in.getClass() == boolean.class) {
                return in;
            }
            return in instanceof String && ((String)in).equalsIgnoreCase("true");
        }

        public boolean canConvertFrom(Class cls) {
            return cls == Boolean.class || cls == boolean.class || cls == String.class;
        }
    }

    public static class StringCoercionCompatibilityEvaluator extends CompatibilityStrategy.DefaultCompatibilityEvaluator {

        private static final CompatibilityStrategy.CompatibilityEvaluator INSTANCE = new StringCoercionCompatibilityEvaluator();

        private StringCoercionCompatibilityEvaluator() { }

        @Override
        public boolean areEqualityCompatible(Class<?> c1, Class<?> c2) {
            return true;
        }

        @Override
        public boolean areComparisonCompatible(Class<?> c1, Class<?> c2) {
            return super.areEqualityCompatible(c1, c2);
        }
    }
}
