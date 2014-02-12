/*
 * Copyright 2007 JBoss Inc
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.compiler.rule.builder.dialect.mvel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drools.compiler.compiler.AnalysisResult;
import org.drools.compiler.compiler.BoundIdentifiers;
import org.drools.compiler.compiler.DescrBuildError;
import org.drools.compiler.lang.descr.AccumulateDescr;
import org.drools.compiler.lang.descr.AccumulateDescr.AccumulateFunctionCallDescr;
import org.drools.compiler.lang.descr.BaseDescr;
import org.drools.compiler.rule.builder.AccumulateBuilder;
import org.drools.compiler.rule.builder.RuleBuildContext;
import org.drools.compiler.rule.builder.RuleConditionBuilder;
import org.drools.compiler.rule.builder.dialect.DialectUtil;
import org.drools.compiler.rule.builder.util.PackageBuilderUtil;
import org.drools.core.base.accumulators.MVELAccumulatorFunctionExecutor;
import org.drools.core.base.extractors.ArrayElementReader;
import org.drools.core.base.extractors.SelfReferenceClassFieldReader;
import org.drools.core.base.mvel.MVELAccumulator;
import org.drools.core.base.mvel.MVELCompilationUnit;
import org.drools.core.base.mvel.MVELCompileable;
import org.drools.core.reteoo.RuleTerminalNode.SortDeclarations;
import org.drools.core.rule.Accumulate;
import org.drools.core.rule.Declaration;
import org.drools.core.rule.MVELDialectRuntimeData;
import org.drools.core.rule.Pattern;
import org.drools.core.rule.RuleConditionElement;
import org.drools.core.spi.Accumulator;
import org.drools.core.spi.InternalReadAccessor;
import org.drools.core.spi.KnowledgeHelper;
import org.kie.api.runtime.rule.AccumulateFunction;

/**
 * A builder for the java dialect accumulate version
 */
public class MVELAccumulateBuilder
    implements
        AccumulateBuilder {

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
            final AccumulateDescr accumDescr = (AccumulateDescr) descr;

            if ( !accumDescr.hasValidInput() ) {
                return null;
            }

            final RuleConditionBuilder builder = (RuleConditionBuilder) context.getDialect().getBuilder( accumDescr.getInput().getClass() );

            // create source CE
            final RuleConditionElement source = builder.build( context,
                                                               accumDescr.getInput() );

            if ( source == null ) {
                return null;
            }

            MVELDialect dialect = (MVELDialect) context.getDialect();

            Map<String, Declaration> decls = context.getDeclarationResolver().getDeclarations( context.getRule() );
            Map<String, Declaration> sourceOuterDeclr = source.getOuterDeclarations();

            Map<String, Declaration> mergedDecl = new HashMap( decls );
            mergedDecl.putAll( sourceOuterDeclr );

            Map<String, Class< ? >> declarationClasses = context.getDeclarationResolver().getDeclarationClasses( decls );
            declarationClasses.putAll( context.getDeclarationResolver().getDeclarationClasses( sourceOuterDeclr ) );

            BoundIdentifiers boundIds = new BoundIdentifiers( declarationClasses,
                                                              context.getKnowledgeBuilder().getGlobals() );
            boundIds.setDeclarations( mergedDecl );

            Accumulator[] accumulators = null;

            final boolean readLocalsFromTuple = PackageBuilderUtil.isReadLocalsFromTuple(accumDescr, source);
            
            if ( accumDescr.isExternalFunction() ) {
                // uses accumulate functions
                accumulators = buildExternalFunctions( context,
                                                       accumDescr,
                                                       dialect,
                                                       decls,
                                                       sourceOuterDeclr,
                                                       boundIds,
                                                       readLocalsFromTuple );
            } else {
                // it is a custom accumulate
                accumulators = buildCustomAccumulate( context,
                                                      accumDescr,
                                                      source,
                                                      dialect,
                                                      decls,
                                                      sourceOuterDeclr,
                                                      boundIds,
                                                      readLocalsFromTuple );
            }

            final Accumulate accumulate = new Accumulate( source,
                                                          null,
                                                          accumulators,
                                                          accumDescr.isMultiFunction() );

            MVELDialectRuntimeData data = (MVELDialectRuntimeData) context.getPkg().getDialectRuntimeRegistry().getDialectData( "mvel" );
            int index = 0;
            for ( Accumulator accumulator : accumulators ) {
                data.addCompileable( accumulate.new Wirer( index++ ),
                                     (MVELCompileable) accumulator );
                ((MVELCompileable) accumulator).compile( data );
            }

            return accumulate;
        } catch ( Exception e ) {
            DialectUtil.copyErrorLocation(e, descr);
            context.addError( new DescrBuildError( context.getParentDescr(),
                                                          descr,
                                                          e,
                                                          "Unable to build expression for 'accumulate' : " + e.getMessage() ) );
            return null;
        } finally {
            context.setTypesafe( typesafe );
        }
    }

    private Accumulator[] buildExternalFunctions( final RuleBuildContext context,
                                                  final AccumulateDescr accumDescr,
                                                  MVELDialect dialect,
                                                  Map<String, Declaration> decls,
                                                  Map<String, Declaration> sourceOuterDeclr,
                                                  BoundIdentifiers boundIds,
                                                  boolean readLocalsFromTuple ) {
        Accumulator[] accumulators;
        List<AccumulateFunctionCallDescr> functions = accumDescr.getFunctions();

        accumulators = new Accumulator[functions.size()];
        // creating the custom array reader
        InternalReadAccessor arrayReader = new SelfReferenceClassFieldReader( Object[].class,
                                                                              "this" );

        int index = 0;
        Pattern pattern = (Pattern) context.getBuildStack().peek();
        for ( AccumulateFunctionCallDescr func : functions ) {
            // build an external function executor
            AccumulateFunction function = context.getConfiguration().getAccumulateFunction( func.getFunction() );
            if( function == null ) {
                // might have been imported in the package
                function = context.getKnowledgeBuilder().getPackage().getAccumulateFunctions().get(func.getFunction());
            }
            if ( function == null ) {
                context.addError( new DescrBuildError( accumDescr,
                                                              context.getRuleDescr(),
                                                              null,
                                                              "Unknown accumulate function: '" + func.getFunction() + "' on rule '" + context.getRuleDescr().getName()
                                                                      + "'. All accumulate functions must be registered before building a resource." ) );
                return null;
            }

            // if there is a binding, create the binding
            if ( func.getBind() != null ) {
                if ( pattern.getDeclaration( func.getBind() ) != null ) {
                    context.addError(new DescrBuildError(context.getParentDescr(),
                            accumDescr,
                            null,
                            "Duplicate declaration for variable '" + func.getBind() + "' in the rule '" + context.getRule().getName() + "'"));
                } else {
                    createResultBind( pattern,
                                  index,
                                  arrayReader,
                                  func,
                                  function );
                }
            }

            final AnalysisResult analysis = dialect.analyzeExpression( context,
                                                                       accumDescr,
                                                                       func.getParams().length > 0 ? func.getParams()[0] : "\"\"",
                                                                       boundIds );

            MVELCompilationUnit unit = dialect.getMVELCompilationUnit( func.getParams().length > 0 ? func.getParams()[0] : "\"\"",
                                                                       analysis,
                                                                       getUsedDeclarations( decls,
                                                                                            analysis ),
                                                                       getUsedDeclarations( sourceOuterDeclr,
                                                                                            analysis ),
                                                                       null,
                                                                       context,
                                                                       "drools",
                                                                       KnowledgeHelper.class,
                                                                       readLocalsFromTuple );

            accumulators[index++] = new MVELAccumulatorFunctionExecutor( unit,
                                                                         function );
        }
        return accumulators;
    }

    private Accumulator[] buildCustomAccumulate( final RuleBuildContext context,
                                                 final AccumulateDescr accumDescr,
                                                 final RuleConditionElement source,
                                                 MVELDialect dialect,
                                                 Map<String, Declaration> decls,
                                                 Map<String, Declaration> sourceOuterDeclr,
                                                 BoundIdentifiers boundIds,
                                                 boolean readLocalsFromTuple ) {

        Accumulator[] accumulators;
        final MVELAnalysisResult initCodeAnalysis = (MVELAnalysisResult) dialect.analyzeBlock( context,
                                                                                               accumDescr,
                                                                                               accumDescr.getInitCode(),
                                                                                               boundIds );

        // need to copy boundIds, as this as a "this" object.
        final MVELAnalysisResult actionCodeAnalysis = (MVELAnalysisResult) dialect.analyzeBlock( context,
                                                                                                 accumDescr,
                                                                                                 null,
                                                                                                 accumDescr.getActionCode(),
                                                                                                 boundIds,
                                                                                                 initCodeAnalysis.getMvelVariables(),
                                                                                                 "drools",
                                                                                                 KnowledgeHelper.class );

        final MVELAnalysisResult resultCodeAnalysis = (MVELAnalysisResult) dialect.analyzeExpression( context,
                                                                                                      accumDescr,
                                                                                                      accumDescr.getResultCode(),
                                                                                                      boundIds,
                                                                                                      initCodeAnalysis.getMvelVariables() );

//        MVELAnalysisResult reverseCodeAnalysis = null;
//        if ( accumDescr.getReverseCode() != null ) {
//            reverseCodeAnalysis = (MVELAnalysisResult) dialect.analyzeBlock( context,
//                                                                             accumDescr,
//                                                                             null,
//                                                                             accumDescr.getActionCode(),
//                                                                             boundIds,
//                                                                             initCodeAnalysis.getMvelVariables(),
//                                                                             "drools",
//                                                                             KnowledgeHelper.class );
//        }

        context.setTypesafe( initCodeAnalysis.isTypesafe() );
        MVELCompilationUnit initUnit = dialect.getMVELCompilationUnit( (String) accumDescr.getInitCode(),
                                                                       initCodeAnalysis,
                                                                       getUsedDeclarations( decls,
                                                                                            initCodeAnalysis ),
                                                                       getUsedDeclarations( sourceOuterDeclr,
                                                                                            initCodeAnalysis ),
                                                                       initCodeAnalysis.getMvelVariables(),
                                                                       context,
                                                                       "drools",
                                                                       KnowledgeHelper.class,
                                                                       readLocalsFromTuple );

        context.setTypesafe( actionCodeAnalysis.isTypesafe() );
        MVELCompilationUnit actionUnit = dialect.getMVELCompilationUnit( (String) accumDescr.getActionCode(),
                                                                         actionCodeAnalysis,
                                                                         getUsedDeclarations( decls,
                                                                                              actionCodeAnalysis ),
                                                                         getUsedDeclarations( sourceOuterDeclr,
                                                                                              actionCodeAnalysis ),
                                                                         initCodeAnalysis.getMvelVariables(),
                                                                         context,
                                                                         "drools",
                                                                         KnowledgeHelper.class,
                                                                         readLocalsFromTuple );

        MVELCompilationUnit reverseUnit = null;
        if ( accumDescr.getReverseCode() != null ) {
            context.setTypesafe( actionCodeAnalysis.isTypesafe() );
            reverseUnit = dialect.getMVELCompilationUnit( (String) accumDescr.getReverseCode(),
                                                          actionCodeAnalysis,
                                                          getUsedDeclarations( decls,
                                                                               actionCodeAnalysis ),
                                                          getUsedDeclarations( sourceOuterDeclr,
                                                                               actionCodeAnalysis ),
                                                          initCodeAnalysis.getMvelVariables(),
                                                          context,
                                                          "drools",
                                                          KnowledgeHelper.class,
                                                          readLocalsFromTuple );
        }

        context.setTypesafe( resultCodeAnalysis.isTypesafe() );
        MVELCompilationUnit resultUnit = dialect.getMVELCompilationUnit( (String) accumDescr.getResultCode(),
                                                                         resultCodeAnalysis,
                                                                         getUsedDeclarations( decls,
                                                                                              resultCodeAnalysis ),
                                                                         getUsedDeclarations( sourceOuterDeclr,
                                                                                              resultCodeAnalysis ),
                                                                         initCodeAnalysis.getMvelVariables(),
                                                                         context,
                                                                         "drools",
                                                                         KnowledgeHelper.class,
                                                                         readLocalsFromTuple );

//        if ( reverseUnit != null ) {
//            Set<String> shadow = new HashSet<String>( source.getOuterDeclarations().keySet() );
//            shadow.retainAll( reverseCodeAnalysis.getNotBoundedIdentifiers() );
//            shadow.addAll( reverseCodeAnalysis.getBoundIdentifiers().getDeclrClasses().keySet() );
//
//            String[] shadowVars = (String[]) shadow.toArray( new String[shadow.size()] );
//
//            actionUnit.setShadowIdentifiers( shadowVars );
//            reverseUnit.setShadowIdentifiers( shadowVars );
//        }

        accumulators = new Accumulator[]{new MVELAccumulator( initUnit,
                                                              actionUnit,
                                                              reverseUnit,
                                                              resultUnit )};
        return accumulators;
    }

    private Declaration[] getUsedDeclarations( Map<String, Declaration> decls,
                                               AnalysisResult analysis ) {
        final BoundIdentifiers usedIdentifiers = analysis.getBoundIdentifiers();
        List<Declaration> usedDeclarations = new ArrayList<Declaration>();
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

    private void createResultBind( final Pattern pattern,
                                   int index,
                                   InternalReadAccessor arrayReader,
                                   AccumulateFunctionCallDescr fc,
                                   AccumulateFunction function ) {
        // bind function result on the result pattern
        Declaration declr = pattern.addDeclaration( fc.getBind() );

        Class< ? > type = function.getResultType();

        // this bit is different, notice its the ArrayElementReader that we wire up to, not the declaration.
        ArrayElementReader reader = new ArrayElementReader( arrayReader,
                                                            index,
                                                            type );
        declr.setReadAccessor( reader );
    }

}
