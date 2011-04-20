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

package org.drools.rule.builder.dialect.mvel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.drools.base.accumulators.MVELAccumulatorFunctionExecutor;
import org.drools.base.mvel.MVELAccumulator;
import org.drools.base.mvel.MVELCompilationUnit;
import org.drools.base.mvel.MVELCompileable;
import org.drools.compiler.AnalysisResult;
import org.drools.compiler.BoundIdentifiers;
import org.drools.compiler.DescrBuildError;
import org.drools.lang.descr.AccumulateDescr;
import org.drools.lang.descr.AccumulateDescr.AccumulateFunctionCallDescr;
import org.drools.lang.descr.BaseDescr;
import org.drools.reteoo.RuleTerminalNode.SortDeclarations;
import org.drools.rule.Accumulate;
import org.drools.rule.Declaration;
import org.drools.rule.MVELDialectRuntimeData;
import org.drools.rule.Pattern;
import org.drools.rule.RuleConditionElement;
import org.drools.rule.builder.AccumulateBuilder;
import org.drools.rule.builder.RuleBuildContext;
import org.drools.rule.builder.RuleConditionBuilder;
import org.drools.runtime.rule.AccumulateFunction;
import org.drools.spi.Accumulator;
import org.drools.spi.InternalReadAccessor;
import org.drools.spi.KnowledgeHelper;

/**
 * A builder for the java dialect accumulate version
 */
public class MVELAccumulateBuilder
    implements
    AccumulateBuilder {

    public RuleConditionElement build(final RuleBuildContext context,
                                      final BaseDescr descr) {
        return build( context,
                      descr,
                      null );
    }

    @SuppressWarnings("unchecked")
    public RuleConditionElement build(final RuleBuildContext context,
                                      final BaseDescr descr,
                                      final Pattern prefixPattern) {
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

            Map<String, Declaration> decls = context.getDeclarationResolver().getDeclarations(context.getRule());            
            Map<String, Declaration> sourceOuterDeclr = source.getOuterDeclarations();               

            Map<String, Class<?>> declarationClasses = context.getDeclarationResolver().getDeclarationClasses( decls );
            declarationClasses.putAll( context.getDeclarationResolver().getDeclarationClasses( sourceOuterDeclr ) );
            
            Accumulator accumulator = null;
                        
            BoundIdentifiers boundIds = new BoundIdentifiers( declarationClasses, context.getPackageBuilder().getGlobals() ) ;          
            
            if ( accumDescr.isExternalFunction() ) {
                // build an external function executor
                AccumulateFunctionCallDescr func = accumDescr.getFunctions().get( 0 );
                AccumulateFunction function = context.getConfiguration().getAccumulateFunction( func.getFunction() );

                if( function == null ) {
                    context.getErrors().add( new DescrBuildError( accumDescr,
                                                                  context.getRuleDescr(),
                                                                  null,
                                                                  "Unknown accumulate function: '"+func.getFunction()+"' on rule '"+context.getRuleDescr().getName()+"'. All accumulate functions must be registered before building a resource." ) );
                    return null;
                }

                final AnalysisResult analysis = dialect.analyzeExpression( context,
                                                                           accumDescr,
                                                                           func.getParams()[0],
                                                                           boundIds );

                
                MVELCompilationUnit unit = dialect.getMVELCompilationUnit( func.getParams()[0],
                                                                           analysis,
                                                                           getUsedDeclarations( decls, analysis ),
                                                                           getUsedDeclarations( sourceOuterDeclr, analysis ),
                                                                           null,
                                                                           context,
                                                                           "drools",
                                                                           KnowledgeHelper.class );

                accumulator = new MVELAccumulatorFunctionExecutor( unit,
                                                                   function );
            } else {
                // it is a custom accumulate
                final MVELAnalysisResult initCodeAnalysis = (MVELAnalysisResult) dialect.analyzeBlock( context,
                                                                                                       accumDescr,
                                                                                                       accumDescr.getInitCode(),
                                                                                                       boundIds);


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

                AnalysisResult reverseCodeAnalysis = null;
                if ( accumDescr.getReverseCode() != null ) {
                    reverseCodeAnalysis = dialect.analyzeBlock( context,
                                                                accumDescr,
                                                                null,
                                                                accumDescr.getActionCode(),
                                                                boundIds,
                                                                initCodeAnalysis.getMvelVariables(),
                                                                "drools",
                                                                KnowledgeHelper.class );
                }

                
                MVELCompilationUnit initUnit = dialect.getMVELCompilationUnit( (String) accumDescr.getInitCode(),
                                                                               initCodeAnalysis,
                                                                               getUsedDeclarations( decls, initCodeAnalysis ),
                                                                               getUsedDeclarations( sourceOuterDeclr, initCodeAnalysis ),
                                                                               initCodeAnalysis.getMvelVariables(),
                                                                               context,
                                                                               "drools",
                                                                               KnowledgeHelper.class );

                MVELCompilationUnit actionUnit = dialect.getMVELCompilationUnit( (String) accumDescr.getActionCode(),
                                                                                 actionCodeAnalysis,
                                                                                 getUsedDeclarations( decls, actionCodeAnalysis ),
                                                                                 getUsedDeclarations( sourceOuterDeclr, actionCodeAnalysis ),
                                                                                 initCodeAnalysis.getMvelVariables(),
                                                                                 context,
                                                                                 "drools",
                                                                                 KnowledgeHelper.class );

                MVELCompilationUnit reverseUnit = null;
                if ( accumDescr.getReverseCode() != null ) {
                    reverseUnit = dialect.getMVELCompilationUnit( (String) accumDescr.getReverseCode(),
                                                                  reverseCodeAnalysis,
                                                                  getUsedDeclarations( decls, reverseCodeAnalysis ),
                                                                  getUsedDeclarations( sourceOuterDeclr, reverseCodeAnalysis ),
                                                                  initCodeAnalysis.getMvelVariables(),
                                                                  context,
                                                                  "drools",
                                                                  KnowledgeHelper.class );
                }

                MVELCompilationUnit resultUnit = dialect.getMVELCompilationUnit( (String) accumDescr.getResultCode(),
                                                                                 resultCodeAnalysis,
                                                                                 getUsedDeclarations( decls, resultCodeAnalysis ),
                                                                                 getUsedDeclarations( sourceOuterDeclr, resultCodeAnalysis ),
                                                                                 initCodeAnalysis.getMvelVariables(),
                                                                                 context,
                                                                                 "drools",
                                                                                 KnowledgeHelper.class );

                if ( reverseUnit != null ) {
                    Set<String> shadow = new HashSet<String>( source.getOuterDeclarations().keySet() );
                    shadow.retainAll( reverseCodeAnalysis.getNotBoundedIdentifiers() );
                    shadow.addAll( reverseCodeAnalysis.getBoundIdentifiers().getDeclarations().keySet() );

                    String[] shadowVars = (String[]) shadow.toArray( new String[shadow.size()] );
                    
                    actionUnit.setShadowIdentifiers( shadowVars );
                    reverseUnit.setShadowIdentifiers( shadowVars );
                }

                accumulator = new MVELAccumulator( initUnit,
                                                   actionUnit,
                                                   reverseUnit,
                                                   resultUnit );

            }

            final Accumulate accumulate = new Accumulate( source,
                                                          null,
                                                          null,
                                                          new Accumulator[] { accumulator } );

            MVELDialectRuntimeData data = (MVELDialectRuntimeData) context.getPkg().getDialectRuntimeRegistry().getDialectData( "mvel" );
            data.addCompileable( accumulate,
                                 (MVELCompileable) accumulator );

            ((MVELCompileable) accumulator).compile( context.getPackageBuilder().getRootClassLoader() );

            return accumulate;
        } catch ( Exception e ) {
            context.getErrors().add( new DescrBuildError( context.getParentDescr(),
                                                          descr,
                                                          e,
                                                          "Unable to build expression for 'accumulate' : " + e.getMessage() ) );
            return null;
        }
    }
    
    private Declaration[] getUsedDeclarations(Map<String, Declaration> decls, AnalysisResult analysis) {
        final BoundIdentifiers usedIdentifiers = analysis.getBoundIdentifiers();
        int i = usedIdentifiers.getDeclarations().keySet().size();
        List<Declaration> usedDeclarations = new ArrayList<Declaration>();
        for ( String id :  usedIdentifiers.getDeclarations().keySet() ) {
            if ( decls.containsKey( id )) {
                usedDeclarations.add( decls.get( id ) );
            }
        }
        
        if ( !usedDeclarations.isEmpty() ) {
            Collections.sort( usedDeclarations, SortDeclarations.instance );
        }
        
        return usedDeclarations.toArray( new Declaration[usedDeclarations.size()] );
    }

}
