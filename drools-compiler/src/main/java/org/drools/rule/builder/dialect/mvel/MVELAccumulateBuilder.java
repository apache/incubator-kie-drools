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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.drools.base.accumulators.AccumulateFunction;
import org.drools.base.accumulators.MVELAccumulatorFunctionExecutor;
import org.drools.base.mvel.DroolsMVELFactory;
import org.drools.base.mvel.MVELAccumulator;
import org.drools.compiler.Dialect;
import org.drools.lang.descr.AccumulateDescr;
import org.drools.lang.descr.BaseDescr;
import org.drools.rule.Accumulate;
import org.drools.rule.Declaration;
import org.drools.rule.Pattern;
import org.drools.rule.RuleConditionElement;
import org.drools.rule.builder.AccumulateBuilder;
import org.drools.rule.builder.RuleBuildContext;
import org.drools.rule.builder.RuleConditionBuilder;
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

    public RuleConditionElement build(final RuleBuildContext context,
                                      final BaseDescr descr,
                                      final Pattern prefixPattern) {

        final AccumulateDescr accumDescr = (AccumulateDescr) descr;

        if( ! accumDescr.hasValidInput() ) {
            return null;
        }

        final RuleConditionBuilder builder = context.getDialect().getBuilder( accumDescr.getInput().getClass() );

        // create source CE
        final RuleConditionElement source = builder.build( context,
                                                           accumDescr.getInput() );

        if ( source == null ) {
            return null;
        }

        MVELDialect dialect = (MVELDialect) context.getDialect();

        final Declaration[] sourceDeclArr = (Declaration[]) source.getOuterDeclarations().values().toArray( new Declaration[0] );

        final DroolsMVELFactory factory = new DroolsMVELFactory( context.getDeclarationResolver().getDeclarations(),
                                                                 source.getOuterDeclarations(),
                                                                 context.getPkg().getGlobals() );

        Accumulator accumulator = null;
        Declaration[] declarations = null;

        if ( accumDescr.isExternalFunction() ) {
            // build an external function executor
            final Dialect.AnalysisResult analysis = dialect.analyzeExpression( context,
                                                                               accumDescr,
                                                                               accumDescr.getExpression() );

            int size = analysis.getBoundIdentifiers()[0].size();
            declarations = new Declaration[size];
            for ( int i = 0; i < size; i++ ) {
                declarations[i] = context.getDeclarationResolver().getDeclaration( (String) analysis.getBoundIdentifiers()[0].get( i ) );
            }

            final Serializable expression = dialect.compile( (String) accumDescr.getExpression(),
                                                             analysis,
                                                             null,
                                                             source.getOuterDeclarations(),
                                                             context );

            AccumulateFunction function = context.getConfiguration().getAccumulateFunction( accumDescr.getFunctionIdentifier() );

            accumulator = new MVELAccumulatorFunctionExecutor( factory,
                                                               expression,
                                                               function );
        } else {
            // it is a custom accumulate
            final MVELAnalysisResult initCodeAnalysis = (MVELAnalysisResult) dialect.analyzeBlock( context,
                                                                                                   accumDescr,
                                                                                                   accumDescr.getInitCode() );

            final MVELAnalysisResult actionCodeAnalysis = (MVELAnalysisResult) dialect.analyzeBlock( context,
                                                                                                     accumDescr,
                                                                                                     null,
                                                                                                     accumDescr.getActionCode(),
                                                                                                     initCodeAnalysis.getMvelVariables() );
            actionCodeAnalysis.setMvelVariables( initCodeAnalysis.getMvelVariables() );
            final MVELAnalysisResult resultCodeAnalysis = (MVELAnalysisResult) dialect.analyzeExpression( context,
                                                                                                          accumDescr,
                                                                                                          accumDescr.getResultCode(),
                                                                                                          initCodeAnalysis.getMvelVariables() );
            resultCodeAnalysis.setMvelVariables( initCodeAnalysis.getMvelVariables() );

            final List requiredDeclarations = new ArrayList( initCodeAnalysis.getBoundIdentifiers()[0] );
            requiredDeclarations.addAll( actionCodeAnalysis.getBoundIdentifiers()[0] );
            requiredDeclarations.addAll( resultCodeAnalysis.getBoundIdentifiers()[0] );

            if ( accumDescr.getReverseCode() != null ) {
                final Dialect.AnalysisResult reverseCodeAnalysis = context.getDialect().analyzeBlock( context,
                                                                                                      accumDescr,
                                                                                                      accumDescr.getActionCode() );
                requiredDeclarations.addAll( reverseCodeAnalysis.getBoundIdentifiers()[0] );
            }

            declarations = new Declaration[requiredDeclarations.size()];
            for ( int i = 0, size = requiredDeclarations.size(); i < size; i++ ) {
                declarations[i] = context.getDeclarationResolver().getDeclaration( (String) requiredDeclarations.get( i ) );
            }

            final Serializable init = dialect.compile( (String) accumDescr.getInitCode(),
                                                       initCodeAnalysis,
                                                       null,
                                                       source.getOuterDeclarations(),
                                                       context );
            final Serializable action = dialect.compile( (String) accumDescr.getActionCode(),
                                                         actionCodeAnalysis,
                                                         null,
                                                         source.getOuterDeclarations(),
                                                         context );

            Serializable reverse = null;
            if ( accumDescr.getReverseCode() != null ) {
                reverse = dialect.compile( (String) accumDescr.getReverseCode(),
                                           resultCodeAnalysis,
                                           null,
                                           source.getOuterDeclarations(),
                                           context );
            }

            final Serializable result = dialect.compile( (String) accumDescr.getResultCode(),
                                                         resultCodeAnalysis,
                                                         null,
                                                         source.getOuterDeclarations(),
                                                         context );

            accumulator = new MVELAccumulator( factory,
                                               init,
                                               action,
                                               reverse,
                                               result );

        }

        final Accumulate accumulate = new Accumulate( source,
                                                      declarations,
                                                      sourceDeclArr,
                                                      accumulator );
        return accumulate;
    }

}
