/*
 * Copyright (c) 2020. Red Hat, Inc. and/or its affiliates.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.mvel;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.drools.compiler.compiler.AnalysisResult;
import org.drools.compiler.compiler.BoundIdentifiers;
import org.drools.compiler.compiler.DescrBuildError;
import org.drools.compiler.compiler.Dialect;
import org.drools.compiler.compiler.DialectConfiguration;
import org.drools.compiler.kie.util.BeanCreator;
import org.drools.mvel.builder.MVELBeanCreator;
import org.drools.compiler.lang.descr.BaseDescr;
import org.drools.compiler.lang.descr.BindingDescr;
import org.drools.compiler.lang.descr.LiteralRestrictionDescr;
import org.drools.compiler.lang.descr.OperatorDescr;
import org.drools.compiler.lang.descr.PredicateDescr;
import org.drools.compiler.lang.descr.RelationalExprDescr;
import org.drools.compiler.rule.builder.ConstraintBuilder;
import org.drools.compiler.rule.builder.PatternBuilder;
import org.drools.compiler.rule.builder.RuleBuildContext;
import org.drools.core.base.ClassObjectType;
import org.drools.core.base.DroolsQuery;
import org.drools.core.base.EvaluatorWrapper;
import org.drools.core.base.SimpleValueType;
import org.drools.core.base.ValueType;
import org.drools.core.base.evaluators.EvaluatorDefinition;
import org.drools.core.base.evaluators.Operator;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.reteoo.LeftTuple;
import org.drools.core.reteoo.RuleTerminalNode;
import org.drools.core.rule.Declaration;
import org.drools.core.rule.Pattern;
import org.drools.core.rule.QueryArgument;
import org.drools.core.rule.constraint.EvaluatorConstraint;
import org.drools.core.spi.Constraint;
import org.drools.core.spi.DeclarationScopeResolver;
import org.drools.core.spi.Evaluator;
import org.drools.core.spi.FieldValue;
import org.drools.core.spi.InternalReadAccessor;
import org.drools.core.spi.KnowledgeHelper;
import org.drools.core.spi.ObjectType;
import org.drools.core.time.TimerExpression;
import org.drools.core.util.index.IndexUtil;
import org.drools.mvel.asm.AsmUtil;
import org.drools.mvel.builder.MVELAnalysisResult;
import org.drools.mvel.builder.MVELDialect;
import org.drools.mvel.builder.MVELDialectConfiguration;
import org.drools.mvel.expr.MVELCompilationUnit;
import org.drools.mvel.expr.MVELCompileable;
import org.drools.mvel.expr.MVELObjectExpression;
import org.drools.mvel.java.JavaDialectConfiguration;
import org.kie.api.definition.rule.Rule;
import org.mvel2.ConversionHandler;
import org.mvel2.DataConversion;
import org.mvel2.MVEL;
import org.mvel2.ParserConfiguration;
import org.mvel2.ParserContext;
import org.mvel2.util.CompatibilityStrategy;
import org.mvel2.util.NullType;
import org.mvel2.util.PropertyTools;

import static org.drools.compiler.rule.builder.PatternBuilder.buildAnalysis;
import static org.drools.compiler.rule.builder.PatternBuilder.buildOperators;
import static org.drools.compiler.rule.builder.PatternBuilder.getOperators;
import static org.drools.compiler.rule.builder.PatternBuilder.getUsedDeclarations;
import static org.drools.compiler.rule.builder.PatternBuilder.registerDescrBuildError;
import static org.drools.compiler.rule.builder.util.PatternBuilderUtil.getNormalizeDate;
import static org.drools.compiler.rule.builder.util.PatternBuilderUtil.normalizeEmptyKeyword;
import static org.drools.compiler.rule.builder.util.PatternBuilderUtil.normalizeStringOperator;
import static org.drools.core.rule.constraint.EvaluatorHelper.WM_ARGUMENT;
import static org.drools.core.util.ClassUtils.convertFromPrimitiveType;
import static org.drools.mvel.asm.AsmUtil.copyErrorLocation;
import static org.drools.mvel.builder.MVELExprAnalyzer.analyze;

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

    @Override
    public DialectConfiguration createJavaDialectConfiguration() {
        return new JavaDialectConfiguration();
    }

    public DialectConfiguration createMVELDialectConfiguration() {
        return new MVELDialectConfiguration();
    }

    public boolean isMvelOperator(String operator) {
        return MVEL_OPERATORS.contains(operator);
    }

    public boolean useMvelExpression() {
        return USE_MVEL_EXPRESSION;
    }

    public Constraint buildVariableConstraint( RuleBuildContext context,
                                               Pattern pattern,
                                               String expression,
                                               Declaration[] declarations,
                                               String leftValue,
                                               OperatorDescr operatorDescr,
                                               String rightValue,
                                               InternalReadAccessor extractor,
                                               Declaration requiredDeclaration,
                                               RelationalExprDescr relDescr,
                                               Map<String, OperatorDescr> aliases) {
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
        if (isUnification && leftValue.equals(rightValue)) {
            expression = resolveUnificationAmbiguity(expression, declarations, leftValue, rightValue);
        }

        expression = normalizeMVELVariableExpression(expression, leftValue, rightValue, relDescr);
        IndexUtil.ConstraintType constraintType = IndexUtil.ConstraintType.decode(operatorDescr.getOperator(), operatorDescr.isNegated());
        MVELCompilationUnit compilationUnit = isUnification ? null : buildCompilationUnit(context, pattern, expression, aliases);
        EvaluatorWrapper[] operators = getOperators(buildOperators(context, pattern, relDescr, aliases));
        return new MVELConstraint( Collections.singletonList( context.getPkg().getName() ), expression, declarations, operators, compilationUnit, constraintType, requiredDeclaration, extractor, isUnification);
    }

    public Constraint buildMvelConstraint(String packageName,
                                          String expression,
                                          Declaration[] declarations,
                                          EvaluatorWrapper[] operators,
                                          RuleBuildContext context,
                                          Declaration[] previousDeclarations,
                                          Declaration[] localDeclarations,
                                          PredicateDescr predicateDescr,
                                          AnalysisResult analysis,
                                          boolean isDynamic) {

        MVELCompilationUnit compilationUnit = buildCompilationUnit(context, previousDeclarations, localDeclarations, predicateDescr, analysis);
        return new MVELConstraint( packageName, expression, declarations, operators, compilationUnit, isDynamic );
    }

    public Constraint buildLiteralConstraint(RuleBuildContext context,
                                             Pattern pattern,
                                             ValueType vtype,
                                             FieldValue field,
                                             String expression,
                                             String leftValue,
                                             String operator,
                                             boolean negated,
                                             String rightValue,
                                             InternalReadAccessor extractor,
                                             LiteralRestrictionDescr restrictionDescr,
                                             Map<String, OperatorDescr> aliases) {
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

        String mvelExpr = normalizeMVELLiteralExpression(vtype, field, expression, leftValue, operator, rightValue, negated, restrictionDescr);
        IndexUtil.ConstraintType constraintType = IndexUtil.ConstraintType.decode(operator, negated);
        if (constraintType == IndexUtil.ConstraintType.EQUAL && negated) {
            mvelExpr = normalizeDoubleNegation(mvelExpr);
        }
        MVELCompilationUnit compilationUnit = buildCompilationUnit(context, pattern, mvelExpr, aliases);
        EvaluatorWrapper[] operators = getOperators(buildOperators(context, pattern, restrictionDescr, aliases));
        return new MVELConstraint(context.getPkg().getName(), mvelExpr, compilationUnit, constraintType, field, extractor, operators);
    }

    private static String normalizeDoubleNegation(String expr) {
        if (expr.charAt( 0 ) == '!') {
            expr = expr.substring( 1 ).trim();
            if (expr.charAt( 0 ) == '(') {
                expr = expr.substring( 1, expr.lastIndexOf( ')' ) ).trim();
            }
            expr = expr.replace( "!=", "==" );
        }
        return expr;
    }

    protected static String resolveUnificationAmbiguity(String expr, Declaration[] declrations, String leftValue, String rightValue) {
        // resolve ambiguity between variable and bound value with the same name in unifications
        rightValue = rightValue + "__";
        for (Declaration declaration : declrations) {
            if (declaration.getIdentifier().equals(leftValue)) {
                declaration.setBindingName(rightValue);
            }
        }
        return leftValue + " == " + rightValue;
    }

    protected static String normalizeMVELLiteralExpression(ValueType vtype,
                                                           FieldValue field,
                                                           String expr,
                                                           String leftValue,
                                                           String operator,
                                                           String rightValue,
                                                           boolean negated,
                                                           LiteralRestrictionDescr restrictionDescr) {
        if (vtype.getSimpleType() == SimpleValueType.DATE) {
            String normalized = leftValue + " " + operator + getNormalizeDate( vtype, field );
            if (!negated) {
                return normalized;
            }
            IndexUtil.ConstraintType constraintType = IndexUtil.ConstraintType.decode(operator);
            return constraintType.getOperator() != null ?
                    leftValue + " " + constraintType.negate().getOperator() + getNormalizeDate( vtype, field ) :
                    "!(" + normalized + ")";
        }
        if (operator.equals("str")) {
            return normalizeStringOperator( leftValue, rightValue, restrictionDescr );
        }
        // resolve ambiguity between mvel's "empty" keyword and constraints like: List(empty == ...)
        return normalizeEmptyKeyword( expr, operator );
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

    private MVELCompilationUnit buildCompilationUnit(RuleBuildContext context, Pattern pattern, String expression, Map<String, OperatorDescr> aliases) {
        Dialect dialect = context.getDialect();
        context.setDialect( context.getDialect( "mvel" ) );

        try {
            PredicateDescr predicateDescr = new PredicateDescr( context.getRuleDescr().getResource(), expression );
            AnalysisResult analysis = buildAnalysis( context, pattern, predicateDescr, aliases );
            if ( analysis == null ) {
                // something bad happened
                return null;
            }

            Declaration[][] usedDeclarations = getUsedDeclarations( context, pattern, analysis );
            return buildCompilationUnit( context, usedDeclarations[0], usedDeclarations[1], predicateDescr, analysis );
        } finally {
            context.setDialect( dialect );
        }
    }

    private MVELCompilationUnit buildCompilationUnit( final RuleBuildContext context,
                                                            final Declaration[] previousDeclarations,
                                                            final Declaration[] localDeclarations,
                                                            final PredicateDescr predicateDescr,
                                                            final AnalysisResult analysis ) {
        if (context.isTypesafe() && analysis instanceof MVELAnalysisResult ) {
            Class<?> returnClass = ((MVELAnalysisResult)analysis).getReturnType();
            if (returnClass != Boolean.class && returnClass != Boolean.TYPE) {
                context.addError( new DescrBuildError( context.getParentDescr(),
                        predicateDescr,
                        null,
                        "Predicate '" + predicateDescr.getContent() + "' must be a Boolean expression\n" + predicateDescr.positionAsString() ) );
            }
        }

        MVELDialect dialect = (MVELDialect) context.getDialect( "mvel" );

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
                                                   context.isInXpath(),
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

    @Override
    public TimerExpression buildTimerExpression( String expression, RuleBuildContext context ) {
        boolean typesafe = context.isTypesafe();
        // pushing consequence LHS into the stack for variable resolution
        context.getDeclarationResolver().pushOnBuildStack( context.getRule().getLhs() );

        try {
            // This builder is re-usable in other dialects, so specify by name
            MVELDialect dialect = (MVELDialect) context.getDialect( "mvel" );

            Map<String, Declaration> decls = context.getDeclarationResolver().getDeclarations(context.getRule());

            MVELAnalysisResult analysis = ( MVELAnalysisResult) dialect.analyzeExpression( context,
                    context.getRuleDescr(),
                    expression,
                    new BoundIdentifiers( DeclarationScopeResolver.getDeclarationClasses( decls ),
                            context ) );
            context.setTypesafe( analysis.isTypesafe() );
            final BoundIdentifiers usedIdentifiers = analysis.getBoundIdentifiers();
            int i = usedIdentifiers.getDeclrClasses().keySet().size();
            Declaration[] previousDeclarations = new Declaration[i];
            i = 0;
            for ( String id :  usedIdentifiers.getDeclrClasses().keySet() ) {
                previousDeclarations[i++] = decls.get( id );
            }
            Arrays.sort(previousDeclarations, RuleTerminalNode.SortDeclarations.instance);

            MVELCompilationUnit unit = dialect.getMVELCompilationUnit( expression,
                    analysis,
                    previousDeclarations,
                    null,
                    null,
                    context,
                    "drools",
                    KnowledgeHelper.class,
                    false,
                    MVELCompilationUnit.Scope.EXPRESSION );

            MVELObjectExpression expr = new MVELObjectExpression( unit, dialect.getId() );

            MVELDialectRuntimeData data = ( MVELDialectRuntimeData ) context.getPkg().getDialectRuntimeRegistry().getDialectData( "mvel" );
            data.addCompileable( context.getRule(), expr );

            expr.compile( data );
            return expr;
        } catch ( final Exception e ) {
            AsmUtil.copyErrorLocation(e, context.getRuleDescr());
            context.addError( new DescrBuildError( context.getParentDescr(),
                    context.getRuleDescr(),
                    null,
                    "Unable to build expression : " + e.getMessage() + "'" + expression + "'" ) );
            return null;
        } finally {
            context.setTypesafe( typesafe );
        }
    }

    @Override
    public TimerExpression buildTimerExpression( String expression, ClassLoader classLoader, Map<String, Declaration> decls ) {
        if (expression == null) {
            return null;
        }

        ParserConfiguration conf = new ParserConfiguration();
        conf.setClassLoader( classLoader );

        MVELAnalysisResult analysis = analyzeExpression( expression, conf, new BoundIdentifiers( DeclarationScopeResolver.getDeclarationClasses( decls ), null ) );

        final BoundIdentifiers usedIdentifiers = analysis.getBoundIdentifiers();
        int i = usedIdentifiers.getDeclrClasses().keySet().size();
        Declaration[] previousDeclarations = new Declaration[i];
        i = 0;
        for ( String id :  usedIdentifiers.getDeclrClasses().keySet() ) {
            previousDeclarations[i++] = decls.get( id );
        }
        Arrays.sort(previousDeclarations, RuleTerminalNode.SortDeclarations.instance);

        MVELCompilationUnit unit = MVELDialect.getMVELCompilationUnit( expression,
                analysis,
                previousDeclarations,
                null,
                null,
                null,
                "drools",
                KnowledgeHelper.class,
                false,
                MVELCompilationUnit.Scope.EXPRESSION );

        MVELObjectExpression expr = new MVELObjectExpression( unit, "mvel" );
        expr.compile( conf );
        return expr;
    }

    @Override
    public AnalysisResult analyzeExpression(Class<?> thisClass, String expr) {
        ParserConfiguration conf = new ParserConfiguration();
        conf.setClassLoader( thisClass.getClassLoader() );
        return analyzeExpression( expr, conf, new BoundIdentifiers( thisClass ) );
    }

    private static MVELAnalysisResult analyzeExpression(String expr,
                                                        ParserConfiguration conf,
                                                        BoundIdentifiers availableIdentifiers) {
        if ( expr.trim().length() <= 0 ) {
            MVELAnalysisResult result = analyze( (Set<String> ) Collections.EMPTY_SET, availableIdentifiers );
            result.setMvelVariables( new HashMap<String, Class< ? >>() );
            result.setTypesafe( true );
            return result;
        }

        MVEL.COMPILER_OPT_ALLOW_NAKED_METH_CALL = true;
        MVEL.COMPILER_OPT_ALLOW_OVERRIDE_ALL_PROPHANDLING = true;
        MVEL.COMPILER_OPT_ALLOW_RESOLVE_INNERCLASSES_WITH_DOTNOTATION = true;
        MVEL.COMPILER_OPT_SUPPORT_JAVA_STYLE_CLASS_LITERALS = true;

        // first compilation is for verification only
        final ParserContext parserContext1 = new ParserContext( conf );
        if ( availableIdentifiers.getThisClass() != null ) {
            parserContext1.addInput( "this", availableIdentifiers.getThisClass() );
        }

        if ( availableIdentifiers.getOperators() != null ) {
            for ( Map.Entry<String, EvaluatorWrapper> opEntry : availableIdentifiers.getOperators().entrySet() ) {
                parserContext1.addInput( opEntry.getKey(), opEntry.getValue().getClass() );
            }
        }

        parserContext1.setStrictTypeEnforcement( false );
        parserContext1.setStrongTyping( false );
        Class< ? > returnType;

        try {
            returnType = MVEL.analyze( expr, parserContext1 );
        } catch ( Exception e ) {
            return null;
        }

        Set<String> requiredInputs = new HashSet<>( parserContext1.getInputs().keySet() );
        Map<String, Class> variables = parserContext1.getVariables();

        // MVEL includes direct fields of context object in non-strict mode. so we need to strip those
        if ( availableIdentifiers.getThisClass() != null ) {
            requiredInputs.removeIf( s -> PropertyTools.getFieldOrAccessor( availableIdentifiers.getThisClass(), s ) != null );
        }

        // now, set the required input types and compile again
        final ParserContext parserContext2 = new ParserContext( conf );
        parserContext2.setStrictTypeEnforcement( true );
        parserContext2.setStrongTyping( true );

        for ( String input : requiredInputs ) {
            if ("this".equals( input )) {
                continue;
            }
            if (WM_ARGUMENT.equals( input )) {
                parserContext2.addInput( input, InternalWorkingMemory.class );
                continue;
            }

            Class< ? > cls = availableIdentifiers.resolveType( input );
            if ( cls == null ) {
                if ( input.equals( "rule" ) ) {
                    cls = Rule.class;
                }
            }
            if ( cls != null ) {
                parserContext2.addInput( input, cls );
            }
        }

        if ( availableIdentifiers.getThisClass() != null ) {
            parserContext2.addInput( "this", availableIdentifiers.getThisClass() );
        }

        try {
            returnType = MVEL.analyze( expr, parserContext2 );
        } catch ( Exception e ) {
            return null;
        }

        requiredInputs = new HashSet<>();
        requiredInputs.addAll( parserContext2.getInputs().keySet() );
        requiredInputs.addAll( variables.keySet() );
        variables = parserContext2.getVariables();

        MVELAnalysisResult result = analyze( requiredInputs, availableIdentifiers );
        result.setReturnType( returnType );
        result.setMvelVariables( (Map<String, Class< ? >>) (Map) variables );
        result.setTypesafe( true );
        return result;
    }

    @Override
    public InternalReadAccessor buildMvelFieldReadAccessor( RuleBuildContext context,
                                                            BaseDescr descr,
                                                            Pattern pattern,
                                                            ObjectType objectType,
                                                            String fieldName,
                                                            boolean reportError) {
        InternalReadAccessor reader;
        Dialect dialect = context.getDialect();
        try {
            MVELDialect mvelDialect = (MVELDialect) context.getDialect("mvel");
            context.setDialect(mvelDialect);

            final AnalysisResult analysis = context.getDialect().analyzeExpression(context,
                    descr,
                    fieldName,
                    new BoundIdentifiers(pattern, context, Collections.EMPTY_MAP,
                            objectType.getClassType()));

            if (analysis == null) {
                // something bad happened
                if (reportError) {
                    registerDescrBuildError(context, descr, "Unable to analyze expression '" + fieldName + "'");
                }
                return null;
            }

            final BoundIdentifiers usedIdentifiers = analysis.getBoundIdentifiers();

            if (!usedIdentifiers.getDeclrClasses().isEmpty()) {
                if (reportError && descr instanceof BindingDescr ) {
                    registerDescrBuildError(context, descr,
                            "Variables can not be used inside bindings. Variable " + usedIdentifiers.getDeclrClasses().keySet() + " is being used in binding '" + fieldName + "'");
                }
                return null;
            }

            reader = context.getPkg().getClassFieldAccessorStore().getMVELReader(context.getPkg().getName(),
                    objectType.getClassName(),
                    fieldName,
                    context.isTypesafe(),
                    (( MVELAnalysisResult ) analysis).getReturnType());

            MVELDialectRuntimeData data = (MVELDialectRuntimeData) context.getPkg().getDialectRuntimeRegistry().getDialectData("mvel");
            (( MVELCompileable ) reader).compile(data, context.getRule());
            data.addCompileable((MVELCompileable) reader);
        } catch (final Exception e) {
            int dotPos = fieldName.indexOf('.');
            String varName = dotPos > 0 ? fieldName.substring(0, dotPos).trim() : fieldName;
            if (context.getKnowledgeBuilder().getGlobals().containsKey(varName)) {
                return null;
            }

            if (reportError) {
                AsmUtil.copyErrorLocation(e, descr);
                registerDescrBuildError(context, descr, e,
                        "Unable to create reader for '" + fieldName + "':" + e.getMessage());
            }
            // if there was an error, set the reader back to null
            reader = null;
        } finally {
            context.setDialect(dialect);
        }
        return reader;
    }

    @Override
    public void setExprInputs(RuleBuildContext context,
                              PatternBuilder.ExprBindings descrBranch,
                              Class<?> thisClass,
                              String expr) {
        MVELDialectRuntimeData data = (MVELDialectRuntimeData) context.getPkg().getDialectRuntimeRegistry().getDialectData("mvel");
        ParserConfiguration conf = data.getParserConfiguration();

        conf.setClassLoader(context.getKnowledgeBuilder().getRootClassLoader());

        final ParserContext pctx = new ParserContext(conf);
        pctx.setStrictTypeEnforcement(false);
        pctx.setStrongTyping(false);
        pctx.addInput("this", thisClass);
        pctx.addInput("empty", boolean.class); // overrides the mvel empty label
        MVEL.COMPILER_OPT_ALLOW_NAKED_METH_CALL = true;
        MVEL.COMPILER_OPT_ALLOW_OVERRIDE_ALL_PROPHANDLING = true;
        MVEL.COMPILER_OPT_ALLOW_RESOLVE_INNERCLASSES_WITH_DOTNOTATION = true;
        MVEL.COMPILER_OPT_SUPPORT_JAVA_STYLE_CLASS_LITERALS = true;

        try {
            MVEL.analysisCompile(expr, pctx);
        } catch (Exception e) {
            // There is a problem in setting the inputs for this expression, but it will be
            // reported during expression analysis, so swallow it at the moment
            return;
        }

        if (!pctx.getInputs().isEmpty()) {
            for (String v : pctx.getInputs().keySet()) {
                // in the following if, we need to check that the expr actually contains a reference
                // to an "empty" property, or the if will evaluate to true even if it doesn't
                if ("this".equals(v) || (PropertyTools.getFieldOrAccessor(thisClass,
                        v) != null && expr.matches("(^|.*\\W)empty($|\\W.*)"))) {
                    descrBranch.getFieldAccessors().add(v);
                } else if ("empty".equals(v)) {
                    // do nothing
                } else if (!context.getPkg().getGlobals().containsKey(v)) {
                    descrBranch.getRuleBindings().add(v);
                } else {
                    descrBranch.getGlobalBindings().add(v);
                }
            }
        }
    }

    @Override
    public FieldValue getMvelFieldValue(RuleBuildContext context, ValueType vtype, String value) {
        try {
            MVEL.COMPILER_OPT_ALLOW_NAKED_METH_CALL = true;
            MVEL.COMPILER_OPT_ALLOW_OVERRIDE_ALL_PROPHANDLING = true;
            MVEL.COMPILER_OPT_ALLOW_RESOLVE_INNERCLASSES_WITH_DOTNOTATION = true;
            MVEL.COMPILER_OPT_SUPPORT_JAVA_STYLE_CLASS_LITERALS = true;

            MVELDialectRuntimeData data = (MVELDialectRuntimeData) context.getPkg().getDialectRuntimeRegistry().getDialectData("mvel");
            ParserConfiguration pconf = data.getParserConfiguration();
            ParserContext pctx = new ParserContext(pconf);

            Object o = MVELSafeHelper.getEvaluator().executeExpression(MVEL.compileExpression(value, pctx));
            if (o != null && vtype == null) {
                // was a compilation problem else where, so guess valuetype so we can continue
                vtype = ValueType.determineValueType(o.getClass());
            }

            return context.getCompilerFactory().getFieldFactory().getFieldValue(o, vtype);
        } catch (final Exception e) {
            // we will fallback to regular preducates, so don't raise an error
        }
        return null;
    }

    @Override
    public QueryArgument buildExpressionQueryArgument(RuleBuildContext context, List<Declaration> declarations, String expression) {
        return new Expression( declarations, expression, getParserContext(context) );
    }

    @Override
    public BeanCreator createMVELBeanCreator( Map<String, Object> parameters) {
        return new MVELBeanCreator(parameters);
    }

    private ParserContext getParserContext(RuleBuildContext context) {
        return MVELCoreComponentsBuilder.getParserContext( context.getPkg().getDialectRuntimeRegistry().getDialectData( "mvel" ), context.getKnowledgeBuilder().getRootClassLoader() );
    }

    public static class Expression implements QueryArgument {
        private List<Declaration> declarations;
        private String expression;
        private ParserContext parserContext;

        private transient Class<?> argumentClass;
        private transient Serializable mvelExpr;

        public Expression() { }

        public Expression( List<Declaration> declarations, String expression, ParserContext parserContext ) {
            this.declarations = declarations;
            this.expression = expression;
            this.parserContext = parserContext;
            init();
        }

        private void init() {
            Map<String, Class> inputs = new HashMap<String, Class>();
            for (Declaration d : declarations) {
                inputs.put(d.getBindingName(), d.getDeclarationClass());
            }
            parserContext.setInputs(inputs);

            this.argumentClass = MVEL.analyze( expression, parserContext );
            this.mvelExpr = MVEL.compileExpression( expression, parserContext );
        }

        @Override
        public Object getValue( InternalWorkingMemory wm, LeftTuple leftTuple ) {
            Map<String, Object> vars = new HashMap<String, Object>();
            for (Declaration d : declarations) {
                vars.put(d.getBindingName(), QueryArgument.evaluateDeclaration( wm, leftTuple, d ));
            }
            return MVELSafeHelper.getEvaluator().executeExpression( this.mvelExpr, vars );
        }

        @Override
        public QueryArgument normalize( ClassLoader classLoader ) {
            parserContext.getParserConfiguration().setClassLoader( classLoader );
            return new Expression( declarations, expression, parserContext );
        }

        @Override
        public void writeExternal( ObjectOutput out ) throws IOException {
            out.writeObject( declarations );
            out.writeObject( expression );
            out.writeObject( parserContext );
        }

        @Override
        public void readExternal( ObjectInput in ) throws IOException, ClassNotFoundException {
            declarations = (List<Declaration>) in.readObject();
            expression = (String) in.readObject();
            parserContext = (ParserContext) in.readObject();

            ParserConfiguration newConf = new ParserConfiguration();
            newConf.setImports( parserContext.getParserConfiguration().getImports() );
            newConf.setPackageImports( parserContext.getParserConfiguration().getPackageImports() );
            parserContext = new ParserContext( newConf );
            parserContext.setInputs( parserContext.getInputs() );
            parserContext.setVariables( parserContext.getVariables() );

            init();
        }
    }
}
