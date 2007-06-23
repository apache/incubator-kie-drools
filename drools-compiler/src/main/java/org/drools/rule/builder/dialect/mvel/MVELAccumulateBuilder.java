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
import org.drools.lang.descr.AccumulateDescr;
import org.drools.lang.descr.BaseDescr;
import org.drools.lang.descr.PatternDescr;
import org.drools.rule.Accumulate;
import org.drools.rule.ConditionalElement;
import org.drools.rule.Declaration;
import org.drools.rule.Pattern;
import org.drools.rule.builder.AccumulateBuilder;
import org.drools.rule.builder.ConditionalElementBuilder;
import org.drools.rule.builder.Dialect;
import org.drools.rule.builder.PatternBuilder;
import org.drools.rule.builder.RuleBuildContext;
import org.drools.spi.Accumulator;
import org.mvel.MVEL;

/**
 * A builder for the java dialect accumulate version
 * 
 * @author etirelli
 */
public class MVELAccumulateBuilder
    implements
    ConditionalElementBuilder,
    AccumulateBuilder {

    public ConditionalElement build(final RuleBuildContext context,
                                    final BaseDescr descr) {
        return build( context,
                      descr,
                      null );
    }

    public ConditionalElement build(final RuleBuildContext context,
                                    final BaseDescr descr,
                                    final Pattern prefixPattern) {

        final AccumulateDescr accumDescr = (AccumulateDescr) descr;

        final PatternBuilder patternBuilder = (PatternBuilder) context.getDialect().getBuilder( PatternDescr.class );

        // create source pattern
        final Pattern sourcePattern = patternBuilder.build( context,
                                                            accumDescr.getSourcePattern() );

        if ( sourcePattern == null ) {
            return null;
        }

        // create result pattern
        final Pattern resultPattern = patternBuilder.build( context,
                                                            accumDescr.getResultPattern() );

        final Declaration[] sourceDeclArr = (Declaration[]) sourcePattern.getOuterDeclarations().values().toArray( new Declaration[0] );

        final DroolsMVELFactory factory = new DroolsMVELFactory( context.getDeclarationResolver().getDeclarations(),
                                                                 sourcePattern.getOuterDeclarations(),
                                                                 context.getPkg().getGlobals() );
        factory.setNextFactory( ((MVELDialect) context.getDialect()).getClassImportResolverFactory() );

        Accumulator accumulator = null;
        Declaration[] declarations = null;

        if ( accumDescr.isExternalFunction() ) {
            // build an external function executor
            final Dialect.AnalysisResult analysis = context.getDialect().analyzeExpression( context,
                                                                                            accumDescr,
                                                                                            accumDescr.getExpression() );

            int size = analysis.getBoundIdentifiers()[0].size();
            declarations = new Declaration[size];
            for ( int i = 0; i < size; i++ ) {
                declarations[i] = context.getDeclarationResolver().getDeclaration( (String) analysis.getBoundIdentifiers()[0].get( i ) );
            }

            final Serializable expression = MVEL.compileExpression( (String) accumDescr.getExpression(),
                                                                    ((MVELDialect) context.getDialect()).getClassImportResolverFactory().getImportedClasses() );

            AccumulateFunction function = context.getConfiguration().getAccumulateFunction( accumDescr.getFunctionIdentifier() );
            
            accumulator = new MVELAccumulatorFunctionExecutor( factory,
                                                               expression,
                                                               function );
        } else {
            // it is a custom accumulate
            final Dialect.AnalysisResult analysis1 = context.getDialect().analyzeBlock( context,
                                                                                        accumDescr,
                                                                                        accumDescr.getInitCode() );
            final Dialect.AnalysisResult analysis2 = context.getDialect().analyzeBlock( context,
                                                                                        accumDescr,
                                                                                        accumDescr.getActionCode() );
            final Dialect.AnalysisResult analysis3 = context.getDialect().analyzeExpression( context,
                                                                                             accumDescr,
                                                                                             accumDescr.getResultCode() );

            final List requiredDeclarations = new ArrayList( analysis1.getBoundIdentifiers()[0] );
            requiredDeclarations.addAll( analysis2.getBoundIdentifiers()[0] );
            requiredDeclarations.addAll( analysis3.getBoundIdentifiers()[0] );

            declarations = new Declaration[requiredDeclarations.size()];
            for ( int i = 0, size = requiredDeclarations.size(); i < size; i++ ) {
                declarations[i] = context.getDeclarationResolver().getDeclaration( (String) requiredDeclarations.get( i ) );
            }

            final Serializable init = MVEL.compileExpression( (String) accumDescr.getInitCode(),
                                                              ((MVELDialect) context.getDialect()).getClassImportResolverFactory().getImportedClasses() );
            final Serializable action = MVEL.compileExpression( (String) accumDescr.getActionCode(),
                                                                ((MVELDialect) context.getDialect()).getClassImportResolverFactory().getImportedClasses() );
            Serializable reverse = null;
            if ( accumDescr.getReverseCode() != null ) {
                reverse = MVEL.compileExpression( (String) accumDescr.getReverseCode(),
                                                  ((MVELDialect) context.getDialect()).getClassImportResolverFactory().getImportedClasses() );
            }
            final Serializable result = MVEL.compileExpression( (String) accumDescr.getResultCode(),
                                                                ((MVELDialect) context.getDialect()).getClassImportResolverFactory().getImportedClasses() );

            accumulator = new MVELAccumulator( factory,
                                               init,
                                               action,
                                               reverse,
                                               result );

        }

        final Accumulate accumulate = new Accumulate( sourcePattern,
                                                      resultPattern,
                                                      declarations,
                                                      sourceDeclArr,
                                                      accumulator );
        return accumulate;
    }

}
