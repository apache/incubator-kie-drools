/*
 * Copyright (c) 2023. Red Hat, Inc. and/or its affiliates.
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

package org.drools.mvel.builder;

import org.drools.compiler.compiler.AnalysisResult;
import org.drools.compiler.compiler.BoundIdentifiers;
import org.drools.compiler.compiler.DescrBuildError;
import org.drools.compiler.rule.builder.GroupByBuilder;
import org.drools.compiler.rule.builder.RuleBuildContext;
import org.drools.compiler.rule.builder.RuleConditionBuilder;
import org.drools.compiler.rule.builder.util.AccumulateUtil;
import org.drools.compiler.rule.builder.util.PackageBuilderUtil;
import org.drools.core.base.extractors.ArrayElementReader;
import org.drools.core.base.extractors.SelfReferenceClassFieldReader;
import org.drools.core.reteoo.RuleTerminalNode.SortDeclarations;
import org.drools.core.rule.Accumulate;
import org.drools.core.rule.Declaration;
import org.drools.core.rule.MultiAccumulate;
import org.drools.core.rule.MutableTypeConstraint;
import org.drools.core.rule.Pattern;
import org.drools.core.rule.RuleConditionElement;
import org.drools.core.rule.SingleAccumulate;
import org.drools.core.rule.accessor.Accumulator;
import org.drools.core.rule.accessor.DeclarationScopeResolver;
import org.drools.core.rule.accessor.ReadAccessor;
import org.drools.core.rule.consequence.KnowledgeHelper;
import org.drools.core.rule.constraint.Constraint;
import org.drools.core.util.index.IndexUtil;
import org.drools.drl.ast.descr.AccumulateDescr.AccumulateFunctionCallDescr;
import org.drools.drl.ast.descr.BaseDescr;
import org.drools.drl.ast.descr.GroupByDescr;
import org.drools.mvel.MVELConstraint;
import org.drools.mvel.MVELDialectRuntimeData;
import org.drools.mvel.asm.AsmUtil;
import org.drools.mvel.expr.MVELAccumulator;
import org.drools.mvel.expr.MVELAccumulatorFunctionExecutor;
import org.drools.mvel.expr.MVELCompilationUnit;
import org.drools.mvel.expr.MVELCompileable;
import org.drools.mvel.expr.MVELEvalExpression;
import org.drools.mvel.expr.MVELObjectExpression;
import org.drools.mvel.expr.MVELReturnValueExpression;
import org.kie.api.runtime.rule.AccumulateFunction;
import org.kie.internal.builder.conf.AccumulateFunctionOption;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.stream.Stream;

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

            MVELReturnValueExpression groupingFunctionCompilation = getGroupingFunctionCompilation(context,
                    groupByDescr,
                    dialect,
                    decls,
                    sourceOuterDeclr,
                    boundIds,
                    readLocalsFromTuple);

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
                                                       declarationClasses);
            } else {
                // it is a custom accumulate
                accumulators = buildCustomAccumulate( context,
                                                      groupByDescr,
                                                      dialect,
                                                      decls,
                                                      sourceOuterDeclr,
                                                      boundIds,
                                                      readLocalsFromTuple );
            }

            List<Declaration> requiredDeclarations = new ArrayList<>();
            for ( Accumulator acc : accumulators ) {
                Collections.addAll( requiredDeclarations, acc.getRequiredDeclarations() );
            }

            MVELDialectRuntimeData data = ( MVELDialectRuntimeData ) context.getPkg().getDialectRuntimeRegistry().getDialectData( "mvel" );

            Accumulate accumulate;
            if (groupByDescr.isMultiFunction()) {
                accumulate = new MultiAccumulate( source,
                                                  requiredDeclarations.toArray( new Declaration[ requiredDeclarations.size() ] ),
                                                  null,
                                                  accumulators,
                                                  accumulators.length + 1);
                int index = 0;
                data.addCompileable( ((MultiAccumulate)accumulate).new GrouppingFunctionWirer( ),
                        groupingFunctionCompilation );
                ((MVELCompileable) groupingFunctionCompilation).compile( data, context.getRule() );
                for ( Accumulator accumulator : accumulators ) {
                    data.addCompileable( ((MultiAccumulate)accumulate).new Wirer( index++ ),
                                         ( MVELCompileable ) accumulator );
                    ((MVELCompileable) accumulator).compile( data, context.getRule() );
                }
            } else {
                accumulate = new SingleAccumulate( source,
                                                   requiredDeclarations.toArray( new Declaration[ requiredDeclarations.size() ] ),
                                                   accumulators[0] );
                data.addCompileable( ((SingleAccumulate)accumulate).new GrouppingFunctionWirer( ),
                        groupingFunctionCompilation );
                ((MVELCompileable) groupingFunctionCompilation).compile( data, context.getRule() );
                data.addCompileable( ((SingleAccumulate)accumulate).new Wirer( ),
                                         (MVELCompileable) accumulators[0] );
                    ((MVELCompileable) accumulators[0]).compile( data, context.getRule() );
            }

            return accumulate;
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

    private MVELReturnValueExpression getGroupingFunctionCompilation(final RuleBuildContext context,
            final GroupByDescr groupByDescr,
            MVELDialect dialect,
            Map<String, Declaration> decls,
            Map<String, Declaration> sourceOuterDeclr,
            BoundIdentifiers boundIds,
            boolean readLocalsFromTuple) {
        Pattern pattern = (Pattern) context.getDeclarationResolver().peekBuildStack();
        final AnalysisResult analysis = dialect.analyzeExpression( context,
                groupByDescr,
                groupByDescr.getGroupingFunction(),
                boundIds );

        MVELCompilationUnit unit = dialect.getMVELCompilationUnit( groupByDescr.getGroupingFunction(),
                analysis,
                getUsedDeclarations( decls,
                        analysis ),
                getUsedDeclarations( sourceOuterDeclr,
                        analysis ),
                null,
                context,
                "drools",
                KnowledgeHelper.class,
                readLocalsFromTuple,
                MVELCompilationUnit.Scope.EXPRESSION );

        // if there is a binding, create the binding
        if ( groupByDescr.getGroupingKey() != null ) {
            if ( context.getDeclarationResolver().isDuplicated( context.getRule(), groupByDescr.getGroupingKey(), analysis.getReturnType().getName() ) ) {
                context.addError( new DescrBuildError( context.getParentDescr(),
                        groupByDescr,
                        null,
                        "Duplicate declaration for variable '" + groupByDescr.getGroupingKey() + "' in the rule '" + context.getRule().getName() + "'" ) );
            } else {
                Declaration declr = pattern.addDeclaration( groupByDescr.getGroupingKey() );
                declr.setReadAccessor(new SelfReferenceClassFieldReader( analysis.getReturnType() ));
            }
        }

        return new MVELReturnValueExpression(unit, UUID.randomUUID().toString());
    }

    private Accumulator[] buildExternalFunctions(final RuleBuildContext context,
                                                 final GroupByDescr groupByDescr,
                                                 MVELDialect dialect,
                                                 Map<String, Declaration> decls,
                                                 Map<String, Declaration> sourceOuterDeclr,
                                                 BoundIdentifiers boundIds,
                                                 boolean readLocalsFromTuple,
                                                 RuleConditionElement source,
                                                 Map<String, Class<?>> declarationClasses) {
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

            final AnalysisResult groupByAnalysis = dialect.analyzeExpression( context,
                    groupByDescr,
                    groupByDescr.getGroupingFunction(),
                    boundIds );

            MVELCompilationUnit unit = dialect.getMVELCompilationUnit( func.getParams().length > 0 ? func.getParams()[0] : "\"\"",
                                                                       analysis,
                    getUsedDeclarationsIncludingGroupBy( decls,
                                                                                            analysis,
                            groupByAnalysis),
                    getUsedDeclarationsIncludingGroupBy( sourceOuterDeclr,
                                                                                            analysis,
                            groupByAnalysis),
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
                                                                     IndexUtil.ConstraintType.EQUAL,
                                                                     context.getDeclarationResolver().getDeclaration( func.getBind() ),
                                                                     groupByDescr.isMultiFunction()
                                                                ? new ArrayElementReader( arrayReader, index, function.getResultType() )
                                                                : new SelfReferenceClassFieldReader( function.getResultType() ),
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
                        declr.setReadAccessor(new SelfReferenceClassFieldReader( function.getResultType() ));
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
                                                 boolean readLocalsFromTuple ) {

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

        final AnalysisResult groupByAnalysis = dialect.analyzeExpression( context,
                groupByDescr,
                groupByDescr.getGroupingFunction(),
                boundIds );

        context.setTypesafe( initCodeAnalysis.isTypesafe() );
        MVELCompilationUnit initUnit = dialect.getMVELCompilationUnit( groupByDescr.getInitCode(),
                                                                       initCodeAnalysis,
                getUsedDeclarationsIncludingGroupBy( decls,
                                                                                            initCodeAnalysis,
                        groupByAnalysis),
                getUsedDeclarationsIncludingGroupBy( sourceOuterDeclr,
                                                                                            initCodeAnalysis,
                        groupByAnalysis),
                                                                       initCodeAnalysis.getMvelVariables(),
                                                                       context,
                                                                       "drools",
                                                                       KnowledgeHelper.class,
                                                                       readLocalsFromTuple,
                                                                       MVELCompilationUnit.Scope.CONSTRAINT );

        context.setTypesafe( actionCodeAnalysis.isTypesafe() );
        MVELCompilationUnit actionUnit = dialect.getMVELCompilationUnit( groupByDescr.getActionCode(),
                                                                         actionCodeAnalysis,
                getUsedDeclarationsIncludingGroupBy( decls,
                                                                                              actionCodeAnalysis,
                        groupByAnalysis),
                getUsedDeclarationsIncludingGroupBy( sourceOuterDeclr,
                                                                                              actionCodeAnalysis,
                        groupByAnalysis),
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
                    getUsedDeclarationsIncludingGroupBy( decls,
                                                                               actionCodeAnalysis,
                            groupByAnalysis),
                    getUsedDeclarationsIncludingGroupBy( sourceOuterDeclr,
                                                                               actionCodeAnalysis,
                            groupByAnalysis),
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
                getUsedDeclarationsIncludingGroupBy( decls,
                                                                                              resultCodeAnalysis,
                        groupByAnalysis),
                getUsedDeclarationsIncludingGroupBy( sourceOuterDeclr,
                                                                                              resultCodeAnalysis,
                        groupByAnalysis),
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

    private Declaration[] getUsedDeclarationsIncludingGroupBy(Map<String, Declaration> decls,
            AnalysisResult analysis,
            AnalysisResult groupbyAnalysis) {
        Declaration[] out = Stream.of(getUsedDeclarations(decls, analysis), getUsedDeclarations(decls, groupbyAnalysis))
                .flatMap(Stream::of)
                .distinct()
                .toArray(Declaration[]::new);
        System.out.println(Arrays.toString(out));
        return out;
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
}
