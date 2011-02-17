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

import java.util.HashSet;
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
import org.drools.lang.descr.BaseDescr;
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

/**
 * A builder for the java dialect accumulate version
 * 
 * @author etirelli
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
            Map<String, Declaration> decls = context.getDeclarationResolver().getDeclarations(context.getRule());

            Declaration[] previousDeclarations = (Declaration[]) decls.values().toArray( new Declaration[decls.size()] );

            // create source CE
            final RuleConditionElement source = builder.build( context,
                                                               accumDescr.getInput() );

            if ( source == null ) {
                return null;
            }

            MVELDialect dialect = (MVELDialect) context.getDialect();

            Map<String, Declaration> outerDeclr = source.getOuterDeclarations();
            
            final Declaration[] sourceDeclArr = outerDeclr.values().toArray( new Declaration[outerDeclr.size()] );

            Accumulator accumulator = null;
            Declaration[] declarations = null;

            Map<String, Class> declarationsMap = context.getDeclarationResolver().getDeclarationClasses( decls );
            BoundIdentifiers boundIds = new BoundIdentifiers( declarationsMap, context.getPackageBuilder().getGlobals() ) ;
            
            if ( accumDescr.isExternalFunction() ) {
                // build an external function executor
                AccumulateFunction function = context.getConfiguration().getAccumulateFunction( accumDescr.getFunctionIdentifier() );

                if( function == null ) {
                    context.getErrors().add( new DescrBuildError( accumDescr,
                                                                  context.getRuleDescr(),
                                                                  null,
                                                                  "Unknown accumulate function: '"+accumDescr.getFunctionIdentifier()+"' on rule '"+context.getRuleDescr().getName()+"'. All accumulate functions must be registered before building a resource." ) );
                    return null;
                }

                final AnalysisResult analysis = dialect.analyzeExpression( context,
                                                                           accumDescr,
                                                                           accumDescr.getExpression(),
                                                                           boundIds );

                
                MVELCompilationUnit unit = dialect.getMVELCompilationUnit( (String) accumDescr.getExpression(),
                                                                           analysis,
                                                                           previousDeclarations,
                                                                           sourceDeclArr,
                                                                           null,
                                                                           context );

                accumulator = new MVELAccumulatorFunctionExecutor( unit,
                                                                   function );
            } else {
                // it is a custom accumulate
                final MVELAnalysisResult initCodeAnalysis = (MVELAnalysisResult) dialect.analyzeBlock( context,
                                                                                                       accumDescr,
                                                                                                       accumDescr.getInitCode(),
                                                                                                       boundIds);                                

                final MVELAnalysisResult actionCodeAnalysis = (MVELAnalysisResult) dialect.analyzeBlock( context,
                                                                                                         accumDescr,
                                                                                                         null,
                                                                                                         accumDescr.getActionCode(),
                                                                                                         boundIds,
                                                                                                         initCodeAnalysis.getMvelVariables() );
                
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
                                                                initCodeAnalysis.getMvelVariables() );
                }

                MVELCompilationUnit initUnit = dialect.getMVELCompilationUnit( (String) accumDescr.getInitCode(),
                                                                               initCodeAnalysis,
                                                                               previousDeclarations,
                                                                               sourceDeclArr,
                                                                               null,
                                                                               context );

                MVELCompilationUnit actionUnit = dialect.getMVELCompilationUnit( (String) accumDescr.getActionCode(),
                                                                                 actionCodeAnalysis,
                                                                                 previousDeclarations,
                                                                                 sourceDeclArr,
                                                                                 initCodeAnalysis.getMvelVariables(),
                                                                                 context );

                MVELCompilationUnit reverseUnit = null;
                if ( accumDescr.getReverseCode() != null ) {
                    reverseUnit = dialect.getMVELCompilationUnit( (String) accumDescr.getReverseCode(),
                                                                  reverseCodeAnalysis,
                                                                  previousDeclarations,
                                                                  sourceDeclArr,
                                                                  initCodeAnalysis.getMvelVariables(),
                                                                  context );
                }

                MVELCompilationUnit resultUnit = dialect.getMVELCompilationUnit( (String) accumDescr.getResultCode(),
                                                                                 resultCodeAnalysis,
                                                                                 previousDeclarations,
                                                                                 sourceDeclArr,
                                                                                 initCodeAnalysis.getMvelVariables(),
                                                                                 context );

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
                                                          declarations,
                                                          sourceDeclArr,
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

}
