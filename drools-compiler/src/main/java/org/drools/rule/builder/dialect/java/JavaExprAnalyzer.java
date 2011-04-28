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

package org.drools.rule.builder.dialect.java;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CharStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.TokenStream;
import org.drools.base.EvaluatorWrapper;
import org.drools.compiler.BoundIdentifiers;
import org.drools.rule.builder.dialect.java.parser.JavaLexer;
import org.drools.rule.builder.dialect.java.parser.JavaLocalDeclarationDescr;
import org.drools.rule.builder.dialect.java.parser.JavaParser;

/**
 * Expression analyzer.
 */
public class JavaExprAnalyzer {
    // ------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------

    /**
     * Construct.
     */
    public JavaExprAnalyzer() {
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
    @SuppressWarnings("unchecked")
    public JavaAnalysisResult analyzeExpression(final String expr,
                                                final BoundIdentifiers availableIdentifiers) throws RecognitionException {
        final CharStream charStream = new ANTLRStringStream( expr );
        final JavaLexer lexer = new JavaLexer( charStream );
        final TokenStream tokenStream = new CommonTokenStream( lexer );
        final JavaParser parser = new JavaParser( tokenStream );

        parser.conditionalOrExpression();
        
        JavaAnalysisResult result = new JavaAnalysisResult();
        result.setIdentifiers(new HashSet<String>( parser.getIdentifiers() ) );
        return analyze( result,
                        availableIdentifiers );
    }

    @SuppressWarnings("unchecked")
    public JavaAnalysisResult analyzeBlock(final String expr,
                                       final BoundIdentifiers availableIdentifiers) throws RecognitionException {
        final CharStream charStream = new ANTLRStringStream( "{" + expr + "}" );
        final JavaLexer lexer = new JavaLexer( charStream );
        final TokenStream tokenStream = new CommonTokenStream( lexer );
        final JavaParser parser = new JavaParser( tokenStream );

        parser.block();

        JavaAnalysisResult result = new JavaAnalysisResult();
        result.setIdentifiers( new HashSet<String>( parser.getIdentifiers() ) );
        result.setLocalVariables( new HashMap<String,JavaLocalDeclarationDescr>() );
        for( Iterator<?> it = parser.getLocalDeclarations().iterator(); it.hasNext(); ) {
            JavaLocalDeclarationDescr descr = (JavaLocalDeclarationDescr) it.next();
            for( Iterator<?> identIt = descr.getIdentifiers().iterator(); identIt.hasNext(); ) {
                JavaLocalDeclarationDescr.IdentifierDescr ident = (JavaLocalDeclarationDescr.IdentifierDescr) identIt.next();
                result.addLocalVariable( ident.getIdentifier(), descr );
            }
        }
        result.setBlockDescrs( parser.getRootBlockDescr() );

        return analyze( result,
                        availableIdentifiers );
    }

    /**
     * Analyze an expression.
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
    private JavaAnalysisResult analyze(final JavaAnalysisResult result,
                                   final BoundIdentifiers availableIdentifiers) throws RecognitionException {
        final Set<String> identifiers = result.getIdentifiers();
        final Set<String> notBound = new HashSet<String>( identifiers );
        
        Map<String, Class<?>> usedDecls = new HashMap<String, Class<?>>();
        Map<String, Class<?>> usedGlobals = new HashMap<String, Class<?>>();
        Map<String, EvaluatorWrapper> usedOperators = new HashMap<String, EvaluatorWrapper>();
 
        for ( Entry<String, Class<?>> entry : availableIdentifiers.getDeclrClasses().entrySet() ) {
            if ( identifiers.contains( entry.getKey() ) ) {
                usedDecls.put( entry.getKey(),
                               entry.getValue() );
                notBound.remove( entry.getKey() );
            }
        }

        for ( Entry<String, Class<?>> entry : availableIdentifiers.getGlobals().entrySet() ) {
            if ( identifiers.contains( entry.getKey() ) ) {
                usedGlobals.put( entry.getKey(),
                               entry.getValue() );
                notBound.remove( entry.getKey() );
            }
        }

        for ( Map.Entry<String, EvaluatorWrapper> op : availableIdentifiers.getOperators().entrySet() ) {
            if ( identifiers.contains( op.getKey() ) ) {
                usedOperators.put( op.getKey(), op.getValue() );
                notBound.remove( op );
            }
        }

        result.setBoundIdentifiers( new BoundIdentifiers( usedDecls,
                                                          usedGlobals,
                                                          usedOperators ) );
        result.setNotBoundedIdentifiers( notBound );

        return result;
    }
}
