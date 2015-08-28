/*
 * Copyright 2015 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.compiler.rule.builder;

import org.drools.compiler.compiler.AnalysisResult;
import org.drools.compiler.compiler.DescrBuildError;
import org.drools.compiler.compiler.Dialect;
import org.drools.compiler.lang.descr.BaseDescr;
import org.drools.compiler.lang.descr.LiteralRestrictionDescr;
import org.drools.compiler.lang.descr.OperatorDescr;
import org.drools.compiler.lang.descr.PredicateDescr;
import org.drools.compiler.lang.descr.RelationalExprDescr;
import org.drools.compiler.rule.builder.dialect.mvel.MVELAnalysisResult;
import org.drools.compiler.rule.builder.dialect.mvel.MVELDialect;
import org.drools.core.base.ClassObjectType;
import org.drools.core.base.DroolsQuery;
import org.drools.core.base.EvaluatorWrapper;
import org.drools.core.base.ValueType;
import org.drools.core.base.evaluators.EvaluatorDefinition;
import org.drools.core.base.evaluators.Operator;
import org.drools.core.base.mvel.MVELCompilationUnit;
import org.drools.core.rule.Declaration;
import org.drools.core.rule.Pattern;
import org.drools.core.rule.constraint.EvaluatorConstraint;
import org.drools.core.rule.constraint.MvelConstraint;
import org.drools.core.spi.Constraint;
import org.drools.core.spi.Evaluator;
import org.drools.core.spi.FieldValue;
import org.drools.core.spi.InternalReadAccessor;
import org.drools.core.spi.KnowledgeHelper;
import org.drools.core.util.index.IndexUtil;
import org.mvel2.ConversionHandler;
import org.mvel2.DataConversion;
import org.mvel2.util.CompatibilityStrategy;
import org.mvel2.util.NullType;

import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.drools.compiler.rule.builder.PatternBuilder.buildAnalysis;
import static org.drools.compiler.rule.builder.PatternBuilder.getUsedDeclarations;
import static org.drools.compiler.rule.builder.dialect.DialectUtil.copyErrorLocation;
import static org.drools.core.util.ClassUtils.convertFromPrimitiveType;

public class MVELConstraintBuilder implements ConstraintBuilder {

    public static final boolean USE_MVEL_EXPRESSION = true;
    protected static final Set<String> MVEL_OPERATORS;

    static {
        if (USE_MVEL_EXPRESSION) {
            CompatibilityStrategy.setCompatibilityEvaluator(StringCoercionCompatibilityEvaluator.INSTANCE);
            DataConversion.addConversionHandler(Boolean.class, BooleanConversionHandler.INSTANCE);
            DataConversion.addConversionHandler(boolean.class, BooleanConversionHandler.INSTANCE);

            MVEL_OPERATORS = new HashSet<String>() {{
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
                add("instanceof");
            }};
        }
    }

    public boolean isMvelOperator(String operator) {
        return MVEL_OPERATORS.contains(operator);
    }

    public boolean useMvelExpression() {
        return USE_MVEL_EXPRESSION;
    }

    public Constraint buildVariableConstraint(RuleBuildContext context,
                                              Pattern pattern,
                                              String expression,
                                              Declaration[] declarations,
                                              String leftValue,
                                              OperatorDescr operatorDescr,
                                              String rightValue,
                                              InternalReadAccessor extractor,
                                              Declaration requiredDeclaration,
                                              RelationalExprDescr relDescr) {
        if (!isMvelOperator(operatorDescr.getOperator())) {
            if (requiredDeclaration == null) {
                return null;
            }

            EvaluatorDefinition.Target right = getRightTarget( extractor );
            EvaluatorDefinition.Target left = (requiredDeclaration.isPatternDeclaration() &&
                                               !(Date.class.isAssignableFrom( requiredDeclaration.getDeclarationClass() )
                                                 || Number.class.isAssignableFrom( requiredDeclaration.getDeclarationClass() ))) ?
                                              EvaluatorDefinition.Target.HANDLE :
                                              EvaluatorDefinition.Target.FACT;
            final Evaluator evaluator = getEvaluator( context,
                                                      relDescr,
                                                      extractor.getValueType(),
                                                      operatorDescr.getOperator(),
                                                      relDescr.isNegated(),
                                                      relDescr.getParametersText(),
                                                      left,
                                                      right );
            return new EvaluatorConstraint(new Declaration[] { requiredDeclaration }, evaluator, extractor);
        }

        boolean isUnification = requiredDeclaration != null &&
                                requiredDeclaration.getPattern().getObjectType().equals( new ClassObjectType( DroolsQuery.class ) ) &&
                                Operator.EQUAL.getOperatorString().equals( operatorDescr.getOperator() );
        if (isUnification) {
            expression = resolveUnificationAmbiguity(expression, declarations, leftValue, rightValue);
        }
        expression = normalizeMVELVariableExpression(expression, leftValue, rightValue, relDescr);
        IndexUtil.ConstraintType constraintType = IndexUtil.ConstraintType.decode(operatorDescr.getOperator());
        MVELCompilationUnit compilationUnit = isUnification ? null : buildCompilationUnit(context, pattern, expression, null);
        return new MvelConstraint( Collections.singletonList( context.getPkg().getName() ), expression, declarations, compilationUnit, constraintType, requiredDeclaration, extractor, isUnification);
    }

    public Constraint buildMvelConstraint(String packageName, String expression, Declaration[] declarations, MVELCompilationUnit compilationUnit, boolean isDynamic) {
        return new MvelConstraint( packageName, expression, declarations, compilationUnit, isDynamic );
    }

    public Constraint buildMvelConstraint(Collection<String> packageName, String expression, Declaration[] declarations, MVELCompilationUnit compilationUnit, IndexUtil.ConstraintType constraintType, Declaration indexingDeclaration, InternalReadAccessor extractor, boolean isUnification) {
        return new MvelConstraint( packageName, expression, declarations, compilationUnit, constraintType, indexingDeclaration, extractor, isUnification );
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
            if (evaluator != null && evaluator.isTemporal()) {
                try {
                    field = context.getCompilerFactory().getFieldFactory().getFieldValue(field.getValue(),
                                                                                         ValueType.DATE_TYPE);
                } catch (Exception e) {
                    context.addError( new DescrBuildError( context.getParentDescr(),
                                                           restrictionDescr,
                                                           null,
                                                           e.getMessage() ) );
                }
            }
            return new EvaluatorConstraint(field, evaluator, extractor);
        }

        String mvelExpr = normalizeMVELLiteralExpression(vtype, field, expression, leftValue, operator, rightValue, restrictionDescr);
        IndexUtil.ConstraintType constraintType = IndexUtil.ConstraintType.decode(operator);
        MVELCompilationUnit compilationUnit = buildCompilationUnit(context, pattern, mvelExpr, null);
        return new MvelConstraint(context.getPkg().getName(), mvelExpr, compilationUnit, constraintType, field, extractor);
    }

    protected static String resolveUnificationAmbiguity(String expr, Declaration[] declrations, String leftValue, String rightValue) {
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

    protected static String normalizeMVELLiteralExpression(ValueType vtype,
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
        if (expr.startsWith("empty") && (operator.equals("==") || operator.equals("!=")) && !Character.isJavaIdentifierPart(expr.charAt(5))) {
            expr = "isEmpty()" + expr.substring(5);
        }

        return expr;
    }

    protected static String normalizeMVELVariableExpression(String expr,
                                                            String leftValue,
                                                            String rightValue,
                                                            RelationalExprDescr relDescr) {
        if (relDescr.getOperator().equals("str")) {
            String method = relDescr.getParametersText();
            if (method.equals("length")) {
                return leftValue + ".length()" + (relDescr.isNegated() ? " != " : " == ") + rightValue;
            }
            return (relDescr.isNegated() ? "!" : "") + leftValue + "." + method + "(" + rightValue + ")";
        }
        return expr;
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
        return ( extractor.isSelfReference() && 
                 !(Date.class.isAssignableFrom( extractor.getExtractToClass() ) || 
                         Number.class.isAssignableFrom( extractor.getExtractToClass() ))) ? EvaluatorDefinition.Target.HANDLE : EvaluatorDefinition.Target.FACT;
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

    public MVELCompilationUnit buildCompilationUnit(RuleBuildContext context, Pattern pattern, String expression, Map<String, OperatorDescr> aliases) {
        Dialect dialect = context.getDialect();
        context.setDialect( context.getDialect( "mvel" ) );

        PredicateDescr predicateDescr = new PredicateDescr( context.getRuleDescr().getResource(), expression );
        AnalysisResult analysis = buildAnalysis(context, pattern, predicateDescr, aliases);
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

            Pattern p = (Pattern) context.getDeclarationResolver().peekBuildStack();
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
                                                   false,
                                                   MVELCompilationUnit.Scope.CONSTRAINT );
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
            if (c1 == NullType.class || c2 == NullType.class) {
                return true;
            }
            if (c1 == String.class || c2 == String.class) {
                return true;
            }
            Class<?> boxed1 = convertFromPrimitiveType(c1);
            Class<?> boxed2 = convertFromPrimitiveType(c2);
            if (boxed1.isAssignableFrom(boxed2) || boxed2.isAssignableFrom(boxed1)) {
                return true;
            }
            if (Number.class.isAssignableFrom(boxed1) && Number.class.isAssignableFrom(boxed2)) {
                return true;
            }
            return !Modifier.isFinal(c1.getModifiers()) && !Modifier.isFinal(c2.getModifiers());
        }

        @Override
        public boolean areComparisonCompatible(Class<?> c1, Class<?> c2) {
            return super.areEqualityCompatible(c1, c2);
        }
    }
}
