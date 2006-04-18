package org.drools.semantics.java;
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





import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CharStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.TokenStream;
import org.drools.semantics.java.parser.JavaParser;
import org.drools.semantics.java.parser.JavaParserLexer;

/**
 * Expression analyzer.
 * 
 * @author <a href="mailto:bob@eng.werken.com">bob mcwhirter </a>
 */
public class JavaExprAnalyzer
{
    // ------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------

    /**
     * Construct.
     */
    public JavaExprAnalyzer()
    {
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
    public List analyze(String expr,
                        Set availDecls) throws RecognitionException 
                                        
    {
    		CharStream charStream = new ANTLRStringStream( expr );
        JavaParserLexer lexer = new JavaParserLexer( charStream );
        TokenStream tokenStream = new CommonTokenStream( lexer );
        JavaParser parser = new JavaParser( tokenStream );

        parser.logicalOrExpression();
        
        List identifiers = parser.getIdentifiers();

        return analyze( availDecls,
                        identifiers );
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
    private List analyze(Set availDecls,
                         List identifiers) throws RecognitionException
    {
        List decls = new ArrayList();
       
        for ( Iterator declIter = availDecls.iterator(); declIter.hasNext(); )
        {
            String eachDecl = (String) declIter.next();

            if ( identifiers.contains( eachDecl ) )
            {
                decls.add( eachDecl );
            }
        }

        return decls;
    }
}