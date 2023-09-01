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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CharStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.TokenStream;
import org.drools.compiler.compiler.BoundIdentifiers;
import org.drools.compiler.rule.builder.EvaluatorWrapper;
import org.drools.compiler.rule.builder.dialect.java.parser.JavaLexer;
import org.drools.compiler.rule.builder.dialect.java.parser.JavaLocalDeclarationDescr;
import org.drools.compiler.rule.builder.dialect.java.parser.JavaParser;

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
     * @param availableIdentifiers
     *            Total set of declarations available.
     * 
     * @return The <code>Set</code> of declarations used by the expression.
     * @throws RecognitionException 
     *             If an error occurs in the parser.
     */
    public JavaAnalysisResult analyzeExpression(final String expr,
                                                final BoundIdentifiers availableIdentifiers) throws RecognitionException {
        final JavaParser parser = parse( expr );
        parser.conditionalOrExpression();
        
        JavaAnalysisResult result = new JavaAnalysisResult( expr, parser.getIdentifiers() );
        return analyze( result, availableIdentifiers );
    }

    public JavaAnalysisResult analyzeBlock(final String expr,
                                       final BoundIdentifiers availableIdentifiers) throws RecognitionException {
        final JavaParser parser = parse( "{" + expr + "}" );
        parser.block();

        JavaAnalysisResult result = new JavaAnalysisResult( expr, parser.getIdentifiers() );

        if ( parser.getRootBlockDescr().getInScopeLocalVars() != null ) {
            for ( JavaLocalDeclarationDescr descr : parser.getRootBlockDescr().getInScopeLocalVars() ) {
                for (JavaLocalDeclarationDescr.IdentifierDescr ident : descr.getIdentifiers()) {
                    result.addLocalVariable(ident.getIdentifier(), descr);
                }
            }
        }
        result.setBlockDescrs( parser.getRootBlockDescr() );
        result.setAssignedVariables( parser.getAssignedVariables() );

        return analyze( result, availableIdentifiers );
    }

    private JavaParser parse(final String expr) {
        final CharStream charStream = new ANTLRStringStream(expr);
        final JavaLexer lexer = new JavaLexer( charStream );
        final TokenStream tokenStream = new CommonTokenStream( lexer );
        return new JavaParser( tokenStream );
    }

    /**
     * Analyze an expression.
     * 
     * @param availableIdentifiers
     *            Total set of declarations available.
     * @param result
     *            The AST for the expression.
     * 
     * @return The <code>Set</code> of declarations used by the expression.
     */
    private JavaAnalysisResult analyze(JavaAnalysisResult result, BoundIdentifiers availableIdentifiers) {
        final Set<String> identifiers = result.getIdentifiers();
        final Set<String> notBound = new HashSet<>( identifiers );
        
        Map<String, Class<?>> usedDecls = new HashMap<>();
        Map<String, Type> usedGlobals = new HashMap<>();
        Map<String, EvaluatorWrapper> usedOperators = new HashMap<>();
 
        for ( Entry<String, Class<?>> entry : availableIdentifiers.getDeclrClasses().entrySet() ) {
            if ( identifiers.contains( entry.getKey() ) ) {
                usedDecls.put( entry.getKey(), entry.getValue() );
                notBound.remove( entry.getKey() );
            }
        }

        for ( String identifier : identifiers ) {
            Type type = availableIdentifiers.resolveVarType( identifier );
            if (type != null) {
                usedGlobals.put( identifier, type );
                notBound.remove( identifier );
            }
        }

        for ( Map.Entry<String, EvaluatorWrapper> op : availableIdentifiers.getOperators().entrySet() ) {
            if ( identifiers.contains( op.getKey() ) ) {
                usedOperators.put( op.getKey(), op.getValue() );
                notBound.remove( op.getKey() );
            }
        }

        BoundIdentifiers boundIdentifiers = new BoundIdentifiers( usedDecls,
                                                                  availableIdentifiers.getContext(),
                                                                  usedOperators,
                                                                  availableIdentifiers.getThisClass() );
        boundIdentifiers.setGlobals( usedGlobals );

        result.setBoundIdentifiers( boundIdentifiers );
        result.setNotBoundedIdentifiers( notBound );

        return result;
    }
}
