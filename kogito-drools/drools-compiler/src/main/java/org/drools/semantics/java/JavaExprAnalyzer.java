package org.drools.semantics.java;

/*
 * $Id: JavaExprAnalyzer.java,v 1.3 2005/11/10 05:10:08 mproctor Exp $
 *
 * Copyright 2002 (C) The Werken Company. All Rights Reserved.
 *
 * Redistribution and use of this software and associated documentation
 * ("Software"), with or without modification, are permitted provided that the
 * following conditions are met:
 *
 * 1. Redistributions of source code must retain copyright statements and
 * notices. Redistributions must also contain a copy of this document.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * 3. The name "drools" must not be used to endorse or promote products derived
 * from this Software without prior written permission of The Werken Company.
 * For written permission, please contact bob@werken.com.
 *
 * 4. Products derived from this Software may not be called "drools" nor may
 * "drools" appear in their names without prior written permission of The Werken
 * Company. "drools" is a registered trademark of The Werken Company.
 *
 * 5. Due credit should be given to The Werken Company.
 * (http://drools.werken.com/).
 *
 * THIS SOFTWARE IS PROVIDED BY THE WERKEN COMPANY AND CONTRIBUTORS ``AS IS''
 * AND ANY EXPRESSED OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE WERKEN COMPANY OR ITS CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 */

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.drools.rule.Declaration;
import org.drools.semantics.java.parser.JavaLexer;
import org.drools.semantics.java.parser.JavaRecognizer;
import org.drools.semantics.java.parser.JavaTreeParser;

import antlr.RecognitionException;
import antlr.TokenStreamException;
import antlr.collections.AST;

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
     * 
     * @throws TokenStreamException
     *             If an error occurs in the lexer.
     * @throws RecognitionException
     *             If an error occurs in the parser.
     * @throws MissingDeclarationException
     *             If the expression requires a declaration not present in the available declarations.
     */
    public List analyze(String expr,
                        Set availDecls) throws TokenStreamException,
                                        RecognitionException
    {
        JavaLexer lexer = new JavaLexer( new StringReader( expr ) );
        JavaRecognizer parser = new JavaRecognizer( lexer );

        parser.ruleCondition();

        AST ast = parser.getAST();

        return analyze( availDecls,
                        ast );
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
                         AST ast) throws RecognitionException
    {
        JavaTreeParser treeParser = new JavaTreeParser();

        treeParser.init();

        treeParser.exprCondition( ast );

        Set refs = new HashSet( treeParser.getVariableReferences() );

        List decls = new ArrayList();

        Iterator declIter = availDecls.iterator();
        Declaration eachDecl;

        while ( declIter.hasNext() )
        {
            eachDecl = (Declaration) declIter.next();

            if ( refs.contains( eachDecl.getIdentifier() ) )
            {
                decls.add( eachDecl );
                refs.remove( eachDecl.getIdentifier() );
            }
        }

        /*
         * if ( ! refs.isEmpty() ) { throw new MissingDeclarationException( expr, (String) refs.iterator().next() ); }
         */
        return decls;
    }
}
