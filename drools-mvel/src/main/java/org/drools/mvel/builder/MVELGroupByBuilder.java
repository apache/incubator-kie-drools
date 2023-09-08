/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.mvel.builder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Supplier;

import org.drools.base.rule.Accumulate;
import org.drools.compiler.compiler.AnalysisResult;
import org.drools.compiler.compiler.BoundIdentifiers;
import org.drools.compiler.compiler.DescrBuildError;
import org.drools.base.util.index.ConstraintTypeOperator;
import org.drools.compiler.rule.builder.GroupByBuilder;
import org.drools.drl.ast.descr.AccumulateDescr.AccumulateFunctionCallDescr;
import org.drools.drl.ast.descr.BaseDescr;
import org.drools.compiler.rule.builder.RuleBuildContext;
import org.drools.compiler.rule.builder.RuleConditionBuilder;
import org.drools.compiler.rule.builder.util.AccumulateUtil;
import org.drools.compiler.rule.builder.util.PackageBuilderUtil;
import org.drools.base.base.extractors.ArrayElementReader;
import org.drools.base.base.extractors.SelfReferenceClassFieldReader;
import org.drools.base.reteoo.SortDeclarations;
import org.drools.base.rule.Declaration;
import org.drools.base.rule.MultiAccumulate;
import org.drools.base.rule.MutableTypeConstraint;
import org.drools.base.rule.Pattern;
import org.drools.base.rule.RuleConditionElement;
import org.drools.base.rule.SingleAccumulate;
import org.drools.base.rule.accessor.Accumulator;
import org.drools.base.rule.constraint.Constraint;
import org.drools.base.rule.accessor.DeclarationScopeResolver;
import org.drools.base.rule.accessor.ReadAccessor;
import org.drools.core.rule.consequence.KnowledgeHelper;
import org.drools.drl.ast.descr.GroupByDescr;
import org.drools.mvel.MVELConstraint;
import org.drools.mvel.MVELDialectRuntimeData;
import org.drools.mvel.MVELGroupByAccumulate;
import org.drools.mvel.asm.AsmUtil;
import org.drools.mvel.expr.MVELAccumulator;
import org.drools.mvel.expr.MVELAccumulatorFunctionExecutor;
import org.drools.mvel.expr.MVELCompilationUnit;
import org.drools.mvel.expr.MVELCompileable;
import org.drools.mvel.expr.MVELReturnValueExpression;
import org.kie.api.runtime.rule.AccumulateFunction;
import org.kie.internal.builder.conf.AccumulateFunctionOption;

/**
 * A builder for the mvel dialect groupby version
 */
public class MVELGroupByBuilder
    implements
        GroupByBuilder {

    public RuleConditionElement build( final RuleBuildContext context,
            final BaseDescr descr ) {
        return build( context,
                descr,
                null );
    }

    @SuppressWarnings("unchecked")
    public RuleConditionElement build( final RuleBuildContext context,
            final BaseDescr descr,
            final Pattern prefixPattern ) {
        boolean typesafe = context.isTypesafe();
        try {
            final GroupByDescr groupByDescr = (GroupByDescr) descr;

            if ( !groupByDescr.hasValidInput() ) {
                return null;
            }

            final RuleConditionBuilder builder = (RuleConditionBuilder) context.getDialect().getBuilder( groupByDescr.getInput().getClass() );

            // create source CE
            final RuleConditionElement source = builder.build( context,
                    groupByDescr.getInput() );

            if ( source == null ) {
                return null;
            }

            MVELDialect dialect = (MVELDialect) context.getDialect();

            Map<String, Declaration> decls = context.getDeclarationResolver().getDeclarations( context.getRule() );
            Map<String, Declaration> sourceOuterDeclr = source.getOuterDeclarations();

            Map<String, Class< ? >> declarationClasses = DeclarationScopeResolver.getDeclarationClasses( decls );
            declarationClasses.putAll( DeclarationScopeResolver.getDeclarationClasses( sourceOuterDeclr ) );

            BoundIdentifiers boundIds = new BoundIdentifiers( declarationClasses,
                    context );

            final boolean readLocalsFromTuple = PackageBuilderUtil.isReadLocalsFromTuple(context, groupByDescr, source);

            return addGroupingFunctionCompilation(context,
                    groupByDescr,
                    (Pattern) context.getDeclarationResolver().peekBuildStack(),
                    declarationClasses,
                    decls,
                    sourceOuterDeclr,
                    readLocalsFromTuple,
                    groupingFunctionCompilation -> {
                        Accumulator[] accumulators;
                        if ( groupByDescr.isExternalFunction() ) {
                            // uses accumulate functions
                            accumulators = buildExternalFunctions( context,
                                    groupByDescr,
                                    dialect,
                                    decls,
                                    sourceOuterDeclr,
                                    boundIds,
                                    readLocalsFromTuple,
                                    source,
                                    declarationClasses,
                                    groupingFunctionCompilation);
                        } else {
                            // it is a custom accumulate
                            accumulators = buildCustomAccumulate( context,
                                    groupByDescr,
                                    dialect,
                                    decls,
                                    sourceOuterDeclr,
                                    boundIds,
                                    readLocalsFromTuple,
                                    groupingFunctionCompilation);
                        }

                        List<Declaration> requiredDeclarations = new ArrayList<>();
                        for ( Accumulator acc : accumulators ) {
                            Collections.addAll( requiredDeclarations, acc.getRequiredDeclarations() );
                        }

                        Accumulate accumulate;
                        if (groupByDescr.isMultiFunction()) {
                            accumulate = new MultiAccumulate( source,
                                    requiredDeclarations.toArray( new Declaration[ requiredDeclarations.size() ] ),
                                    accumulators,
                                    accumulators.length + 1);
                        } else {
                            accumulate = new SingleAccumulate( source,
                                    requiredDeclarations.toArray( new Declaration[ requiredDeclarations.size() ] ),
                                    accumulators[0] );
                        }

                        return accumulate;
                    });
        } catch ( Exception e ) {
            AsmUtil.copyErrorLocation(e, descr);
            context.addError( new DescrBuildError( context.getParentDescr(),
                    descr,
                    e,
                    "Unable to build expression for 'groupby' : " + e.getMessage() ) );
            return null;
        } finally {
            context.setTypesafe( typesafe );
        }
    }

    private final class GroupingFunctionCompilationResult {
        final AnalysisResult analysisResult;
        final Declaration[] requiredDeclarations;
        final MVELReturnValueExpression groupingFunction;

        public GroupingFunctionCompilationResult(AnalysisResult analysisResult, Declaration[] requiredDeclarations,
                MVELReturnValueExpression groupingFunction) {
            this.analysisResult = analysisResult;
            this.requiredDeclarations = requiredDeclarations;
            this.groupingFunction = groupingFunction;
        }
    }

    private MVELGroupByAccumulate addGroupingFunctionCompilation(
            RuleBuildContext context,
            GroupByDescr groupByDescr,
            Pattern pattern,
            Map<String, Class<?>> declCls,
            Map<String, Declaration> decls,
            Map<String, Declaration> sourceDeclarations,
            boolean readLocalsFromTuple,
            Function<GroupingFunctionCompilationResult, Accumulate> innerAccumulateSupplier) {
        MVELDialect dialect = (MVELDialect) context.getDialect();
        // analyze the expression
        Map<String, Declaration> groupByDeclsInScope = new HashMap<>(sourceDeclarations);

        final MVELAnalysisResult analysis = (MVELAnalysisResult) context.getDialect().analyzeExpression( context,
                groupByDescr,
                groupByDescr.getGroupingFunction(),
                new BoundIdentifiers(DeclarationScopeResolver.getDeclarationClasses(groupByDeclsInScope), context) );

        if ( analysis == null ) {
            // not possible to get the analysis results - compilation error has been already logged
            return null;
        }

        // create the array of used declarations
        final Declaration[] requiredDeclarations = getUsedDeclarations( decls,
                analysis );

        final Declaration[] previousDeclarations = getUsedDeclarations( sourceDeclarations,
                analysis );

        Class<?> keyType = MVELExprAnalyzer.getExpressionType(context, DeclarationScopeResolver.getDeclarationClasses(groupByDeclsInScope), pattern, groupByDescr.getGroupingFunction());
        MVELCompilationUnit unit = dialect.getMVELCompilationUnit( groupByDescr.getGroupingFunction(),
                analysis,
                requiredDeclarations,
                previousDeclarations,
                analysis.getMvelVariables(),
                context,
                "drools",
                KnowledgeHelper.class,
                readLocalsFromTuple,
                MVELCompilationUnit.Scope.EXPRESSION );
        MVELReturnValueExpression groupingFunction = new MVELReturnValueExpression(unit, UUID.randomUUID().toString());

        MVELGroupByAccumulate out = new MVELGroupByAccumulate(
                innerAccumulateSupplier.apply(new GroupingFunctionCompilationResult(
                        analysis,
                        bindGroupingFunctionReaderToDeclaration(context,
                            groupByDescr,
                            pattern,
                            declCls,
                            decls,
                            new ArrayElementReader( new SelfReferenceClassFieldReader(Object[].class), groupByDescr.getFunctions().size(), keyType ),
                            keyType),
                        groupingFunction)),
                requiredDeclarations,
                null,
                true);

        MVELDialectRuntimeData data = ( MVELDialectRuntimeData ) context.getPkg().getDialectRuntimeRegistry().getDialectData( "mvel" );

        data.addCompileable( out.new GroupingFunctionWirer( ),
                groupingFunction );
        groupingFunction.compile(data, context.getRule());

        if (out.getInnerAccumulate() instanceof MultiAccumulate) {
            MultiAccumulate innerAccumulate = (MultiAccumulate) out.getInnerAccumulate();
            int index = 0;
            for ( Accumulator accumulator : innerAccumulate.getAccumulators() ) {
                data.addCompileable(innerAccumulate.new Wirer( index++ ),
                        ( MVELCompileable ) accumulator );
                ((MVELCompileable) accumulator).compile( data, context.getRule() );
            }
        } else {
            SingleAccumulate innerAccumulate = (SingleAccumulate) out.getInnerAccumulate();
            MVELCompileable accumulator = (MVELCompileable) innerAccumulate.getAccumulators()[0];
            data.addCompileable(innerAccumulate.new Wirer( ),
                    accumulator );
            accumulator.compile( data, context.getRule() );
        }

        return out;
    }

    private Accumulator[] buildExternalFunctions(final RuleBuildContext context,
            final GroupByDescr groupByDescr,
            MVELDialect dialect,
            Map<String, Declaration> decls,
            Map<String, Declaration> sourceOuterDeclr,
            BoundIdentifiers boundIds,
            boolean readLocalsFromTuple,
            RuleConditionElement source,
            Map<String, Class<?>> declarationClasses,
            GroupingFunctionCompilationResult groupingFunctionCompilationResult) {
        Accumulator[] accumulators;
        List<AccumulateFunctionCallDescr> functions = groupByDescr.getFunctions();

        accumulators = new Accumulator[functions.size()];
        // creating the custom array reader
        ReadAccessor arrayReader = new SelfReferenceClassFieldReader( Object[].class );

        int index = 0;
        Pattern pattern = (Pattern) context.getDeclarationResolver().peekBuildStack();
        for ( AccumulateFunctionCallDescr func : functions ) {
            // build an external function executor
            Supplier<Class<?>> classSupplier = () -> MVELExprAnalyzer.getExpressionType(context,
                    declarationClasses,
                    source,
                    func.getParams()[0]);
            String functionName = AccumulateUtil.getFunctionName(classSupplier, func.getFunction());
            AccumulateFunction function = context.getConfiguration().getOption(AccumulateFunctionOption.KEY, functionName).getFunction();
            if( function == null ) {
                // might have been imported in the package
                function = context.getPkg().getAccumulateFunctions().get(func.getFunction());
            }
            if ( function == null ) {
                context.addError( new DescrBuildError( groupByDescr,
                        context.getRuleDescr(),
                        null,
                        "Unknown accumulate function: '" + func.getFunction() + "' on rule '" + context.getRuleDescr().getName()
                                + "'. All accumulate functions must be registered before building a resource." ) );
                return null;
            }

            final AnalysisResult analysis = dialect.analyzeExpression( context,
                    groupByDescr,
                    func.getParams().length > 0 ? func.getParams()[0] : "\"\"",
                    boundIds );

            MVELCompilationUnit unit = dialect.getMVELCompilationUnit( func.getParams().length > 0 ? func.getParams()[0] : "\"\"",
                    analysis,
                    getUsedDeclarations( decls,
                            analysis),
                    getUsedDeclarations( sourceOuterDeclr,
                            analysis),
                    null,
                    context,
                    "drools",
                    KnowledgeHelper.class,
                    readLocalsFromTuple,
                    MVELCompilationUnit.Scope.CONSTRAINT );

            accumulators[index] = new MVELAccumulatorFunctionExecutor( unit,
                    function );
            // if there is a binding, create the binding
            if ( func.getBind() != null ) {
                if ( context.getDeclarationResolver().isDuplicated( context.getRule(), func.getBind(), function.getResultType().getName() ) ) {
                    if ( ! func.isUnification() ) {
                        context.addError( new DescrBuildError( context.getParentDescr(),
                                groupByDescr,
                                null,
                                "Duplicate declaration for variable '" + func.getBind() + "' in the rule '" + context.getRule().getName() + "'" ) );
                    } else {
                        Declaration inner = context.getDeclarationResolver().getDeclaration( func.getBind() );
                        MutableTypeConstraint c = new MVELConstraint(Collections.singletonList(context.getPkg().getName()),
                                groupByDescr.isMultiFunction()
                                        ? "this[ " + index + " ] == " + func.getBind()
                                        : "this == " + func.getBind(),
                                new Declaration[] { inner },
                                null,
                                null,
                                ConstraintTypeOperator.EQUAL,
                                context.getDeclarationResolver().getDeclaration( func.getBind() ),
                                new ArrayElementReader(arrayReader, index, function.getResultType()),
                                true);
                        c.setType(Constraint.ConstraintType.BETA);
                        pattern.addConstraint( c );
                        index++;
                    }
                } else {
                    Declaration declr = pattern.addDeclaration( func.getBind() );
                    if (groupByDescr.isMultiFunction()) {
                        declr.setReadAccessor(new ArrayElementReader(arrayReader, index, function.getResultType()));
                    } else {
                        declr.setReadAccessor(new ArrayElementReader( arrayReader, index, function.getResultType() ));
                    }
                }
            }
            index++;
        }
        return accumulators;
    }

    private Accumulator[] buildCustomAccumulate( final RuleBuildContext context,
            final GroupByDescr groupByDescr,
            MVELDialect dialect,
            Map<String, Declaration> decls,
            Map<String, Declaration> sourceOuterDeclr,
            BoundIdentifiers boundIds,
            boolean readLocalsFromTuple,
            GroupingFunctionCompilationResult groupingFunctionCompilationResult) {

        Accumulator[] accumulators;
        final MVELAnalysisResult initCodeAnalysis = (MVELAnalysisResult) dialect.analyzeBlock( context,
                groupByDescr,
                groupByDescr.getInitCode(),
                boundIds );

        // need to copy boundIds, as this as a "this" object.
        final MVELAnalysisResult actionCodeAnalysis = (MVELAnalysisResult) dialect.analyzeBlock( context,
                groupByDescr.getActionCode(),
                boundIds,
                initCodeAnalysis.getMvelVariables(),
                "drools",
                KnowledgeHelper.class );

        final MVELAnalysisResult resultCodeAnalysis = (MVELAnalysisResult) dialect.analyzeExpression( context,
                groupByDescr,
                groupByDescr.getResultCode(),
                boundIds,
                initCodeAnalysis.getMvelVariables() );

        context.setTypesafe( initCodeAnalysis.isTypesafe() );
        MVELCompilationUnit initUnit = dialect.getMVELCompilationUnit( groupByDescr.getInitCode(),
                initCodeAnalysis,
                getUsedDeclarations( decls,
                        initCodeAnalysis),
                getUsedDeclarations( sourceOuterDeclr,
                        initCodeAnalysis),
                initCodeAnalysis.getMvelVariables(),
                context,
                "drools",
                KnowledgeHelper.class,
                readLocalsFromTuple,
                MVELCompilationUnit.Scope.CONSTRAINT );

        context.setTypesafe( actionCodeAnalysis.isTypesafe() );
        MVELCompilationUnit actionUnit = dialect.getMVELCompilationUnit( groupByDescr.getActionCode(),
                actionCodeAnalysis,
                getUsedDeclarations( decls,
                        actionCodeAnalysis),
                getUsedDeclarations( sourceOuterDeclr,
                        actionCodeAnalysis),
                initCodeAnalysis.getMvelVariables(),
                context,
                "drools",
                KnowledgeHelper.class,
                readLocalsFromTuple,
                MVELCompilationUnit.Scope.CONSTRAINT );

        MVELCompilationUnit reverseUnit = null;
        if ( groupByDescr.getReverseCode() != null ) {
            context.setTypesafe( actionCodeAnalysis.isTypesafe() );
            reverseUnit = dialect.getMVELCompilationUnit( groupByDescr.getReverseCode(),
                    actionCodeAnalysis,
                    getUsedDeclarations( decls,
                            actionCodeAnalysis),
                    getUsedDeclarations( sourceOuterDeclr,
                            actionCodeAnalysis),
                    initCodeAnalysis.getMvelVariables(),
                    context,
                    "drools",
                    KnowledgeHelper.class,
                    readLocalsFromTuple,
                    MVELCompilationUnit.Scope.CONSTRAINT );
        }

        context.setTypesafe( resultCodeAnalysis.isTypesafe() );
        MVELCompilationUnit resultUnit = dialect.getMVELCompilationUnit( groupByDescr.getResultCode(),
                resultCodeAnalysis,
                getUsedDeclarations( decls,
                        resultCodeAnalysis),
                getUsedDeclarations( sourceOuterDeclr,
                        resultCodeAnalysis),
                initCodeAnalysis.getMvelVariables(),
                context,
                "drools",
                KnowledgeHelper.class,
                readLocalsFromTuple,
                MVELCompilationUnit.Scope.CONSTRAINT );

        accumulators = new Accumulator[]{new MVELAccumulator( initUnit,
                actionUnit,
                reverseUnit,
                resultUnit )};
        return accumulators;
    }

    private Declaration[] getUsedDeclarations( Map<String, Declaration> decls,
            AnalysisResult analysis) {
        final BoundIdentifiers usedIdentifiers = analysis.getBoundIdentifiers();
        List<Declaration> usedDeclarations = new ArrayList<>();
        for ( String id : usedIdentifiers.getDeclrClasses().keySet() ) {
            if ( decls.containsKey( id ) ) {
                usedDeclarations.add( decls.get( id ) );
            }
        }

        if ( !usedDeclarations.isEmpty() ) {
            Collections.sort( usedDeclarations,
                    SortDeclarations.instance );
        }

        return usedDeclarations.toArray( new Declaration[usedDeclarations.size()] );
    }

    private Declaration[] bindGroupingFunctionReaderToDeclaration( RuleBuildContext context, GroupByDescr groupByDescr, Pattern pattern, Map<String, Class<?>> declCls, Map<String, Declaration> decls, ReadAccessor readAccessor, Class<?> resultType) {
        if ( groupByDescr.getGroupingKey() != null ) {
            if ( context.getDeclarationResolver().isDuplicated( context.getRule(), groupByDescr.getGroupingKey(), resultType.getName() ) ) {
                context.addError( new DescrBuildError( context.getParentDescr(),
                        groupByDescr,
                        null,
                        "Duplicate declaration for variable '" + groupByDescr.getGroupingKey() + "' in the rule '" + context.getRule().getName() + "'" ) );
            } else {
                Declaration declr = pattern.addDeclaration( groupByDescr.getGroupingKey() );
                declr.setDeclarationClass(resultType);
                declr.setReadAccessor( readAccessor );
                declCls.put(groupByDescr.getGroupingKey(), resultType);
                decls.put(groupByDescr.getGroupingKey(), declr);
                return new Declaration[] {declr};
            }
        }
        return new Declaration[] {};
    }
}
