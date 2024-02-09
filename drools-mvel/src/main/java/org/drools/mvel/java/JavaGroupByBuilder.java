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
package org.drools.mvel.java;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.drools.base.base.ValueType;
import org.drools.base.reteoo.SortDeclarations;
import org.drools.base.util.index.ConstraintTypeOperator;
import org.drools.compiler.compiler.AnalysisResult;
import org.drools.compiler.compiler.BoundIdentifiers;
import org.drools.compiler.compiler.DescrBuildError;
import org.drools.compiler.rule.builder.GroupByBuilder;
import org.drools.compiler.rule.builder.RuleBuildContext;
import org.drools.compiler.rule.builder.RuleConditionBuilder;
import org.drools.compiler.rule.builder.dialect.java.parser.JavaLocalDeclarationDescr;
import org.drools.compiler.rule.builder.util.AccumulateUtil;
import org.drools.compiler.rule.builder.util.PackageBuilderUtil;
import org.drools.core.base.accumulators.JavaAccumulatorFunctionExecutor;
import org.drools.base.base.extractors.ArrayElementReader;
import org.drools.base.base.extractors.SelfReferenceClassFieldReader;
import org.drools.base.rule.Accumulate;
import org.drools.base.rule.Declaration;
import org.drools.base.rule.MultiAccumulate;
import org.drools.base.rule.MutableTypeConstraint;
import org.drools.base.rule.Pattern;
import org.drools.base.rule.RuleConditionElement;
import org.drools.base.rule.SingleAccumulate;
import org.drools.base.rule.accessor.Accumulator;
import org.drools.base.rule.accessor.DeclarationScopeResolver;
import org.drools.base.rule.accessor.ReadAccessor;
import org.drools.base.rule.constraint.Constraint;
import org.drools.drl.ast.descr.AccumulateDescr.AccumulateFunctionCallDescr;
import org.drools.drl.ast.descr.AndDescr;
import org.drools.drl.ast.descr.BaseDescr;
import org.drools.drl.ast.descr.GroupByDescr;
import org.drools.mvel.MVELConstraint;
import org.drools.mvel.MVELGroupByAccumulate;
import org.drools.mvel.builder.MVELExprAnalyzer;
import org.kie.api.runtime.rule.AccumulateFunction;
import org.kie.internal.builder.conf.AccumulateFunctionOption;

import static org.drools.mvel.java.JavaRuleBuilderHelper.createVariableContext;
import static org.drools.mvel.java.JavaRuleBuilderHelper.generateTemplates;

/**
 * A builder for the java dialect groupby version
 */
public class JavaGroupByBuilder
    implements
        GroupByBuilder {

    public RuleConditionElement build( final RuleBuildContext context,
                                       final BaseDescr descr ) {
        return build( context,
                      descr,
                      null );
    }

    public RuleConditionElement build( final RuleBuildContext context,
                                       final BaseDescr descr,
                                       final Pattern prefixPattern ) {

        final GroupByDescr groupByDescr = (GroupByDescr) descr;
        if ( !groupByDescr.hasValidInput() ) {
            return null;
        }

        // build source
        BaseDescr input = groupByDescr.getInput();
        if( input instanceof AndDescr && ((AndDescr) input).getDescrs().size() == 1 ) {
            input = ((AndDescr) input).getDescrs().get( 0 );
        }
        
        final RuleConditionBuilder builder = (RuleConditionBuilder) context.getDialect().getBuilder( input.getClass() );
        final RuleConditionElement source = builder.build( context,
                                                           input );
        if ( source == null ) {
            return null;
        }

        final boolean readLocalsFromTuple = PackageBuilderUtil.isReadLocalsFromTuple(context, groupByDescr, source);
        
        Map<String, Declaration> declsInScope = context.getDeclarationResolver().getDeclarations( context.getRule() );
        if (prefixPattern != null && prefixPattern.getDeclaration() != null) {
            declsInScope.remove( prefixPattern.getDeclaration().getIdentifier() );
        }
        Map<String, Class< ? >> declCls = DeclarationScopeResolver.getDeclarationClasses( declsInScope );

        Accumulate accumulate;
        if ( groupByDescr.isExternalFunction() ) {
            // if it uses 1+ external function, build methods for them
            accumulate = buildExternalFunctionCall( context,
                                                    groupByDescr,
                                                    source,
                                                    declsInScope,
                                                    declCls,
                                                    readLocalsFromTuple );
        } else {
            // if it uses inline code, build the class for it
            accumulate = buildInlineAccumulate( context,
                                                groupByDescr,
                                                source,
                                                declsInScope,
                                                declCls,
                                                readLocalsFromTuple );
        }

        return accumulate;
    }

    private MVELGroupByAccumulate addGroupingFunctionCompilation(
            RuleBuildContext context,
            GroupByDescr groupByDescr,
            Pattern pattern,
            Map<String, Class<?>> declCls,
            Declaration[] sourceDeclArr,
            boolean readLocalsFromTuple,
            Function<Collection<Declaration>, Accumulate> innerAccumulateSupplier) {
        // analyze the expression
        Map<String, Declaration> groupByDeclsInScope = new HashMap<>();
        groupByDeclsInScope.putAll(Arrays.stream(sourceDeclArr)
                .collect(Collectors.toMap(Declaration::getIdentifier, Function.identity())));

        final JavaAnalysisResult analysis = (JavaAnalysisResult) context.getDialect().analyzeExpression( context,
                groupByDescr,
                groupByDescr.getGroupingFunction(),
                new BoundIdentifiers(DeclarationScopeResolver.getDeclarationClasses(groupByDeclsInScope), context) );

        if ( analysis == null ) {
            // not possible to get the analysis results - compilation error has been already logged
            return null;
        }

        // create the array of used declarations
        final BoundIdentifiers usedIdentifiers = analysis.getBoundIdentifiers();
        final Declaration[] requiredDeclarations = collectRequiredDeclarations( groupByDeclsInScope,
                new HashSet<>(),
                usedIdentifiers );

        final String className = "groupbyExpression" + context.getNextId();

        Class<?> keyType = MVELExprAnalyzer.getExpressionType(context, DeclarationScopeResolver.getDeclarationClasses(groupByDeclsInScope), pattern, groupByDescr.getGroupingFunction());
        MVELGroupByAccumulate out = new MVELGroupByAccumulate(
                innerAccumulateSupplier.apply(bindGroupingFunctionReaderToDeclaration(context,
                        groupByDescr,
                        pattern,
                        declCls,
                        new ArrayElementReader( new SelfReferenceClassFieldReader(Object[].class), groupByDescr.getFunctions().size(), keyType ),
                        keyType)),
                requiredDeclarations,
                null,
                false);

        final Map<String, Object> map = createVariableContext( className,
                groupByDescr.getGroupingFunction(),
                context,
                requiredDeclarations,
                new Declaration[] {},
                usedIdentifiers.getGlobals()
        );
        map.put( "readLocalsFromTuple",
                readLocalsFromTuple ? Boolean.TRUE : Boolean.FALSE );

        generateTemplates("returnValueMethod",
                "returnValueInvoker",
                context,
                className,
                map,
                out.new GroupingFunctionWirer(),
                groupByDescr);

        return out;
    }

    private Accumulate buildExternalFunctionCall( RuleBuildContext context,
                                                  GroupByDescr groupByDescr,
                                                  RuleConditionElement source,
                                                  Map<String, Declaration> declsInScope,
                                                  Map<String, Class< ? >> declCls,
                                                  boolean readLocalsFromTuple) {
        // list of functions to build
        final List<AccumulateFunctionCallDescr> funcCalls = groupByDescr.getFunctions();
        // list of available source declarations
        final Declaration[] sourceDeclArr = source.getOuterDeclarations().values().toArray( new Declaration[source.getOuterDeclarations().size()] );
        Arrays.sort( sourceDeclArr, SortDeclarations.instance );

        // set of required previous declarations
        Set<Declaration> requiredDecl = new HashSet<>();

        Pattern pattern = (Pattern) context.getDeclarationResolver().peekBuildStack();

        Function<Collection<Declaration>, Accumulate> accumulateSupplier;
        if (groupByDescr.isMultiFunction()) {
            accumulateSupplier = keyDecls -> {
                for (Declaration declaration : keyDecls) {
                    declsInScope.put(declaration.getIdentifier(), declaration);
                }
                requiredDecl.addAll(keyDecls);

                Accumulator[] accumulators = new Accumulator[funcCalls.size()];
                // creating the custom array reader
                ReadAccessor reader = new SelfReferenceClassFieldReader( Object[].class );

                int index = 0;
                for ( AccumulateFunctionCallDescr fc : funcCalls ) {
                    AccumulateFunction function = getAccumulateFunction(context, groupByDescr, fc, source, declCls);
                    if (function == null) {
                        return null;
                    }

                    bindReaderToDeclaration(context, groupByDescr, pattern, fc, new ArrayElementReader(reader, index, function.getResultType()), function.getResultType(), index);
                    accumulators[index++] = buildAccumulator(context, groupByDescr, declsInScope, declCls, readLocalsFromTuple, sourceDeclArr, requiredDecl, keyDecls, fc, function);
                }
                requiredDecl.addAll(List.of(sourceDeclArr));
                return new MultiAccumulate(source, requiredDecl.toArray(new Declaration[requiredDecl.size()]), accumulators, accumulators.length + 1);
            };
        } else {
            accumulateSupplier = keyDecls -> {
                for (Declaration declaration : keyDecls) {
                    declsInScope.put(declaration.getIdentifier(), declaration);
                }
                requiredDecl.addAll(keyDecls);
                AccumulateFunctionCallDescr fc = groupByDescr.getFunctions().get(0);
                AccumulateFunction function = getAccumulateFunction(context, groupByDescr, fc, source, declCls);
                if (function == null) {
                    return null;
                }

                Class<?> returnType = function.getResultType();
                if (!pattern.isCompatibleWithAccumulateReturnType(returnType)) {
                    context.addError( new DescrBuildError( groupByDescr,
                                                           context.getRuleDescr(),
                                                           null,
                                                           "Pattern of type: '" + pattern.getObjectType() + "' on rule '" + context.getRuleDescr().getName() +
                                                           "' is not compatible with type " + returnType.getCanonicalName() + " returned by accumulate function.") );
                    return null;
                }

                bindReaderToDeclaration(context, groupByDescr, pattern, fc, new ArrayElementReader( new SelfReferenceClassFieldReader(Object[].class), 0, function.getResultType() ), function.getResultType(), -1);
                Accumulator accumulator = buildAccumulator(context, groupByDescr, declsInScope, declCls, readLocalsFromTuple, sourceDeclArr, requiredDecl, keyDecls, fc, function);

                requiredDecl.addAll(List.of(sourceDeclArr));
                return new SingleAccumulate( source,
                                             requiredDecl.toArray(new Declaration[requiredDecl.size()]),
                                             accumulator );
            };
        }
        return addGroupingFunctionCompilation(context,
            groupByDescr,
            pattern,
            declCls,
            sourceDeclArr,
            readLocalsFromTuple,
            accumulateSupplier);
    }

    private Collection<Declaration> bindGroupingFunctionReaderToDeclaration( RuleBuildContext context, GroupByDescr groupByDescr, Pattern pattern, Map<String, Class<?>> declCls, ReadAccessor readAccessor, Class<?> resultType) {
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
                return Collections.singletonList(declr);
            }
        }
        return Collections.emptyList();
    }

    private void bindReaderToDeclaration( RuleBuildContext context, GroupByDescr groupByDescr, Pattern pattern, AccumulateFunctionCallDescr fc, ReadAccessor readAccessor, Class<?> resultType, int index ) {
        if ( fc.getBind() != null ) {
            if ( context.getDeclarationResolver().isDuplicated( context.getRule(), fc.getBind(), resultType.getName() ) ) {
                if ( ! fc.isUnification() ) {
                    context.addError( new DescrBuildError( context.getParentDescr(),
                                                           groupByDescr,
                                                           null,
                                                           "Duplicate declaration for variable '" + fc.getBind() + "' in the rule '" + context.getRule().getName() + "'" ) );
                } else {
                    Declaration inner = context.getDeclarationResolver().getDeclaration( fc.getBind() );
                    MutableTypeConstraint c = new MVELConstraint(Collections.singletonList(context.getPkg().getName()),
                                                       index >= 0
                                                            ? "this[ " + index + " ] == " + fc.getBind()
                                                            : "this == " + fc.getBind(),
                                                                 new Declaration[] { inner },
                                                                 null,
                                                                 null,
                                                                 ConstraintTypeOperator.EQUAL,
                                                                 context.getDeclarationResolver().getDeclaration( fc.getBind() ),
                                                       index >= 0
                                                            ? new ArrayElementReader( readAccessor, index, resultType )
                                                            : readAccessor,
                                                                 true);
                    c.setType(Constraint.ConstraintType.BETA);
                    pattern.addConstraint( c );
                }
            } else {
                Declaration declr = pattern.addDeclaration( fc.getBind() );
                declr.setReadAccessor( readAccessor );
            }
        }
    }

    private AccumulateFunction getAccumulateFunction(RuleBuildContext context,
                                                     GroupByDescr groupByDescr,
                                                     AccumulateFunctionCallDescr fc,
                                                     RuleConditionElement source,
                                                     Map<String, Class< ? >> declCls) {
        String functionName = AccumulateUtil.getFunctionName(() -> MVELExprAnalyzer.getExpressionType(context, declCls, source, fc.getParams()[0]), fc.getFunction());

        // find the corresponding function
        AccumulateFunction function = context.getConfiguration().getOption(AccumulateFunctionOption.KEY, functionName).getFunction();
        if( function == null ) {
            // might have been imported in the package
            function = context.getPkg().getAccumulateFunctions().get( functionName );
        }
        if ( function == null ) {
            context.addError( new DescrBuildError( groupByDescr,
                                                   context.getRuleDescr(),
                                                   null,
                                                   "Unknown accumulate function: '" + functionName + "' on rule '" + context.getRuleDescr().getName() + "'. All accumulate functions must be registered before building a resource." ) );
        }
        return function;
    }

    private Accumulator buildAccumulator(RuleBuildContext context, GroupByDescr groupByDescr, Map<String, Declaration> declsInScope, Map<String, Class<?>> declCls, boolean readLocalsFromTuple, Declaration[] sourceDeclArr, Set<Declaration> requiredDecl, Collection<Declaration> keyDeclarations, AccumulateFunctionCallDescr fc, AccumulateFunction function) {
        // analyze the expression
        final JavaAnalysisResult analysis = (JavaAnalysisResult) context.getDialect().analyzeBlock( context,
                                                                                                    groupByDescr,
                                                                                                    fc.getParams().length > 0 ? fc.getParams()[0] : "\"\"",
                                                                                                    new BoundIdentifiers( declCls, context ) );

        if ( analysis == null ) {
            // not possible to get the analysis results - compilation error has been already logged
            return null;
        }

        // TODO: Find out why the grouping key is not put into BoundIdentifier despite
        //       being accessible in context and being in declCls
        final BoundIdentifiers usedIdentifiers = analysis.getBoundIdentifiers();
        if (groupByDescr.getGroupingKey() != null && Stream.of(fc.getParams()).anyMatch(parameter -> parameter.contains(groupByDescr.getGroupingKey()))) {
            usedIdentifiers.getDeclrClasses().put(groupByDescr.getGroupingKey(), declCls.get(groupByDescr.getGroupingKey()));
        }

        // create the array of used declarations
        Declaration[] previousDeclarations = collectRequiredDeclarations( declsInScope,
                                                                          requiredDecl,
                                                                          usedIdentifiers );


        // generate the code template
        return generateFunctionCallCodeTemplate( context,
                                                 groupByDescr,
                                                 sourceDeclArr,
                                                 fc,
                                                 function,
                                                 usedIdentifiers,
                                                 previousDeclarations,
                                                 readLocalsFromTuple );
    }

    private Declaration[] collectRequiredDeclarations( Map<String, Declaration> declsInScope,
                                                       Set<Declaration> requiredDecl,
                                                       final BoundIdentifiers usedIdentifiers ) {
        final Declaration[] previousDeclarations = new Declaration[usedIdentifiers.getDeclrClasses().size()];
        int i = 0;
        for ( String key : usedIdentifiers.getDeclrClasses().keySet() ) {
            Declaration d = declsInScope.get( key );
            previousDeclarations[i++] = d;
            requiredDecl.add( d );
        }
        return previousDeclarations;
    }

    private JavaAccumulatorFunctionExecutor generateFunctionCallCodeTemplate( final RuleBuildContext context,
                                                                              final GroupByDescr groupByDescr,
                                                                              final Declaration[] sourceDeclArr,
                                                                              AccumulateFunctionCallDescr fc,
                                                                              AccumulateFunction function,
                                                                              final BoundIdentifiers usedIdentifiers,
                                                                              final Declaration[] previousDeclarations,
                                                                              final boolean readLocalsFromTuple ) {
        final String className = "accumulateExpression" + context.getNextId();
        final Map<String, Object> map = createVariableContext( className,
                                                               fc.getParams().length > 0 ? fc.getParams()[ 0 ] : "\"\"",
                                                               context,
                                                               previousDeclarations,
                                                               sourceDeclArr,
                                                               usedIdentifiers.getGlobals()
        );
        map.put( "readLocalsFromTuple",
                 readLocalsFromTuple ? Boolean.TRUE : Boolean.FALSE );

        JavaAccumulatorFunctionExecutor accumulator = new JavaAccumulatorFunctionExecutor( function );

        generateTemplates("returnValueMethod",
                "returnValueInvoker",
                context,
                className,
                map,
                accumulator,
                groupByDescr);
        return accumulator;
    }

    private Accumulate buildInlineAccumulate( final RuleBuildContext context,
                                              final GroupByDescr groupByDescr,
                                              final RuleConditionElement source,
                                              Map<String, Declaration> decls,
                                              Map<String, Class< ? >> declCls,
                                              final boolean readLocalsFromTuple) {
        // ELSE, if it is not an external function, build it using the regular java builder
        final Declaration[] sourceDeclArr = source.getOuterDeclarations().values().toArray( new Declaration[source.getOuterDeclarations().size()] );
        Arrays.sort( sourceDeclArr, SortDeclarations.instance );
        return addGroupingFunctionCompilation(context,
                groupByDescr,
                (Pattern) context.getDeclarationResolver().peekBuildStack(),
                new HashMap<>(),
                sourceDeclArr,
                readLocalsFromTuple,
                keyDecls -> {
                    final String className = "Accumulate" + context.getNextId();
                    groupByDescr.setClassName( className );

                    BoundIdentifiers available = new BoundIdentifiers( declCls, context );

                    final JavaAnalysisResult initCodeAnalysis = (JavaAnalysisResult) context.getDialect().analyzeBlock( context,
                            groupByDescr,
                            groupByDescr.getInitCode(),
                            available );
                    final AnalysisResult actionCodeAnalysis = context.getDialect().analyzeBlock( context,
                            groupByDescr,
                            groupByDescr.getActionCode(),
                            available );

                    final AnalysisResult resultCodeAnalysis = context.getDialect().analyzeExpression( context,
                            groupByDescr,
                            groupByDescr.getResultCode(),
                            available );

                    if ( initCodeAnalysis == null || actionCodeAnalysis == null || resultCodeAnalysis == null ) {
                        // not possible to get the analysis results - compilation error has been already logged
                        return null;
                    }

                    final Set<String> requiredDeclarations = new HashSet<>( initCodeAnalysis.getBoundIdentifiers().getDeclrClasses().keySet() );
                    requiredDeclarations.addAll( actionCodeAnalysis.getBoundIdentifiers().getDeclrClasses().keySet() );
                    requiredDeclarations.addAll( resultCodeAnalysis.getBoundIdentifiers().getDeclrClasses().keySet() );

                    final Map<String, Type> requiredGlobals = new HashMap<>( initCodeAnalysis.getBoundIdentifiers().getGlobals() );
                    requiredGlobals.putAll( actionCodeAnalysis.getBoundIdentifiers().getGlobals() );
                    requiredGlobals.putAll( resultCodeAnalysis.getBoundIdentifiers().getGlobals() );

                    if ( groupByDescr.getReverseCode() != null ) {
                        final AnalysisResult reverseCodeAnalysis = context.getDialect().analyzeBlock( context,
                                groupByDescr,
                                groupByDescr.getActionCode(),
                                available );
                        requiredDeclarations.addAll( reverseCodeAnalysis.getBoundIdentifiers().getDeclrClasses().keySet() );
                        requiredGlobals.putAll( reverseCodeAnalysis.getBoundIdentifiers().getGlobals() );
                    }

                    final Declaration[] declarations = new Declaration[requiredDeclarations.size()];
                    int i = 0;
                    for ( Iterator<String> it = requiredDeclarations.iterator(); it.hasNext(); i++ ) {
                        declarations[i] = decls.get( it.next() );
                    }

                    final Map<String, Object> map = createVariableContext( className,
                            null,
                            context,
                            declarations,
                            null,
                            requiredGlobals
                    );

                    map.put( "className",
                            groupByDescr.getClassName() );
                    map.put( "innerDeclarations",
                            sourceDeclArr );
                    map.put( "isMultiPattern",
                            readLocalsFromTuple ? Boolean.TRUE : Boolean.FALSE );

                    final String initCode = this.fixInitCode( initCodeAnalysis,
                            groupByDescr.getInitCode() );
                    final String actionCode = groupByDescr.getActionCode();
                    final String resultCode = groupByDescr.getResultCode();

                    String[] attributesTypes = new String[initCodeAnalysis.getLocalVariablesMap().size()];
                    String[] attributes = new String[initCodeAnalysis.getLocalVariablesMap().size()];
                    int index = 0;
                    for ( Map.Entry<String, JavaLocalDeclarationDescr> entry : initCodeAnalysis.getLocalVariablesMap().entrySet() ) {
                        attributes[index] = entry.getKey();
                        attributesTypes[index] = entry.getValue().getType();
                        index++;
                    }

                    map.put( "attributes",
                            attributes );
                    map.put( "attributesTypes",
                            attributesTypes );

                    map.put( "initCode",
                            initCode );
                    map.put( "actionCode",
                            actionCode );
                    map.put( "resultCode",
                            resultCode );
                    if ( groupByDescr.getReverseCode() == null ) {
                        map.put( "reverseCode",
                                "" );
                        map.put( "supportsReverse",
                                "false" );
                    } else {
                        map.put( "reverseCode",
                                groupByDescr.getReverseCode() );
                        map.put( "supportsReverse",
                                "true" );
                    }

                    map.put( "hashCode",
                            actionCode.hashCode());

                    SingleAccumulate accumulate = new SingleAccumulate(source, declarations);

                    generateTemplates("accumulateInnerClass",
                            "accumulateInvoker",
                            context,
                            className,
                            map,
                            accumulate.new Wirer(),
                            groupByDescr);

                    return accumulate;
                });
    }

    public String fixInitCode( JavaAnalysisResult analysis,
                                  final String originalCode ) {
        TreeSet<JavaLocalDeclarationDescr> locals = new TreeSet<>( new Comparator<JavaLocalDeclarationDescr>() {
            public int compare( JavaLocalDeclarationDescr o1,
                                JavaLocalDeclarationDescr o2 ) {
                return o1.getStart() - o2.getStart();
            }
        } );

        locals.addAll(analysis.getLocalVariablesMap().values());

        StringBuilder initCode = new StringBuilder();
        int lastAdded = 0;
        for ( JavaLocalDeclarationDescr d : locals ) {
            // adding chunk
            initCode.append( originalCode.substring( lastAdded,
                                                     d.getStart() ) );
            lastAdded = d.getEnd();
            // adding variable initializations
            for ( JavaLocalDeclarationDescr.IdentifierDescr id : d.getIdentifiers() ) {
                initCode.append( originalCode.substring( id.getStart(),
                                                         id.getEnd() ) );
                initCode.append( ";" );
                lastAdded = id.getEnd();
                while ( lastAdded < originalCode.length() && (Character.isWhitespace( originalCode.charAt( lastAdded ) ) || originalCode.charAt( lastAdded ) == ';') ) {
                    lastAdded++;
                }
            }
        }
        initCode.append( originalCode.substring( lastAdded ) );

        return initCode.toString();
    }

}
