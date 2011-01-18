/*
 * Copyright 2005 JBoss Inc
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
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.antlr.runtime.RecognitionException;
import org.drools.rule.builder.PackageBuildContext;
import org.mvel2.ParserConfiguration;
import org.mvel2.ParserContext;
import org.mvel2.compiler.ExpressionCompiler;

/**
 * Expression analyzer.
 * 
 */
public class MVELExprAnalyzer {

    public MVELExprAnalyzer() {
        // intentionally left blank.
    }

    // ------------------------------------------------------------
    // Instance methods
    // ------------------------------------------------------------

    /**
     * Analyze an expression.
     * 
     * @param expr
     *            The expression to analyze.
     * @param availDecls
     *            Total set of declarations available.
     * 
     * @return The <code>Set</code> of declarations used by the expression.
     * @throws RecognitionException 
     *             If an error occurs in the parser.
     */
    public MVELAnalysisResult analyzeExpression(final PackageBuildContext context,
                                                final String expr,
                                                final Map<String, Class<?>>[] availableIdentifiers,
                                                final Map<String, Class<?>> localTypes) {
        MVELAnalysisResult result = null;
        if ( expr.trim().length() > 0 ) {
            MVELDialect dialect = (MVELDialect) context.getDialect( "mvel" );
            
            // creating a reusable parser configuration
            ParserConfiguration conf = new ParserConfiguration();
            conf.addAllImports( dialect.getImports() );
            if ( dialect.getPackgeImports() != null && !dialect.getPackgeImports().isEmpty() ) {
                for ( String packageImport : ((Collection<String>) dialect.getPackgeImports().values()) ) {
                    conf.addPackageImport( packageImport );
                }
            }
            conf.setClassLoader( context.getPackageBuilder().getRootClassLoader() );
            
            // first compilation is for verification only

            // @todo proper source file name
            final ParserContext parserContext1 = new ParserContext( conf ); 
            parserContext1.setStrictTypeEnforcement( false );
            parserContext1.setInterceptors( dialect.getInterceptors() );
            ExpressionCompiler compiler1 = new ExpressionCompiler( expr );
            compiler1.setVerifyOnly( true );
            compiler1.compile( parserContext1 );

            Set<String> requiredInputs = compiler1.getParserContextState().getInputs().keySet();
            
            // now, set the required input types and compile again
            final ParserContext parserContext2 = new ParserContext( conf ); 
            parserContext2.setStrictTypeEnforcement( false );
            parserContext2.setInterceptors( dialect.getInterceptors() );
            for( Map<String,Class<?>> map : availableIdentifiers ) {
                for( Map.Entry<String, Class<?>> entry : map.entrySet() ) {
                    if( requiredInputs.contains( entry.getKey() ) ) {
                        parserContext2.addInput( entry.getKey(), entry.getValue() );
                    }
                }
            }
            ExpressionCompiler compiler2 = new ExpressionCompiler( expr );
            compiler2.setVerifyOnly( true );
            compiler2.compile( parserContext2 );
            
            result = analyze( compiler2.getParserContextState().getInputs().keySet(),
                              availableIdentifiers );

            result.setMvelVariables( compiler2.getParserContextState().getVariables() );
        } else {
            result = analyze( (Set<String>) Collections.EMPTY_SET,
                              availableIdentifiers );
            result.setMvelVariables( new HashMap<String,Class<?>>() );

        }
        return result;
    }

    /**
     * Analyse an expression.
     * 
     * @param availDecls
     *            Total set of declarations available.
     * @param ast
     *            The AST for the expression.
     * 
     * @return The <code>Set</code> of declarations used by the expression.
     * 
     * @throws RecognitionException
     *             If an error occurs in the parser.
     */
    @SuppressWarnings("unchecked")
    private MVELAnalysisResult analyze(final Set<String> identifiers,
                                       final Map<String, Class<?>>[] availableIdentifiers) {

        MVELAnalysisResult result = new MVELAnalysisResult();
        result.setIdentifiers( new ArrayList<String>( identifiers ) );

        final Set<String> notBound = new HashSet<String>( identifiers );
        final List<String>[] used = new List[availableIdentifiers.length];
        for ( int i = 0, length = used.length; i < length; i++ ) {
            used[i] = new ArrayList<String>();
        }

        for ( int i = 0, length = availableIdentifiers.length; i < length; i++ ) {
            final Set<String> set = availableIdentifiers[i].keySet();
            for ( final String decl : set ) {
                if ( identifiers.contains( decl ) ) {
                    used[i].add( decl );
                    notBound.remove( decl );
                }
            }
        }
        result.setBoundIdentifiers( used );
        result.setNotBoundedIdentifiers( new ArrayList<String>( notBound ) );

        return result;
    }
}
