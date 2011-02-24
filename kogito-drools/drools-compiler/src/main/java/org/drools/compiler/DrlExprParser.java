/*
 * Copyright 2011 JBoss Inc
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

package org.drools.compiler;

import java.util.Collections;
import java.util.List;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.RecognizerSharedState;
import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.tree.CommonTreeNodeStream;
import org.drools.lang.DRLExprTree;
import org.drools.lang.DRLExpressions;
import org.drools.lang.DRLExpressions.conditionalAndExpression_return;
import org.drools.lang.DRLLexer;
import org.drools.lang.ParserHelper;
import org.drools.lang.descr.ConstraintConnectiveDescr;

/**
 * This is a helper class that provides helper methods to parse expressions
 * using both the DRLExpressions parser and the DRLExprTree parser.
 */
public class DrlExprParser {

    private ParserHelper helper = null;

    public DrlExprParser() {
    }

    /** Parse an expression from text */
    public ConstraintConnectiveDescr parse( final String text ) throws DroolsParserException {
        ConstraintConnectiveDescr constraint = null;
        try {
            DRLLexer lexer = new DRLLexer( new ANTLRStringStream( text ) );
            CommonTokenStream input = new CommonTokenStream( lexer );
            RecognizerSharedState state = new RecognizerSharedState();
            helper = new ParserHelper( input,
                                       state );
            DRLExpressions parser = new DRLExpressions( input, state, helper );
            conditionalAndExpression_return expr = parser.conditionalAndExpression();
            if ( expr != null && !parser.hasErrors() ) {
                CommonTree tree = (CommonTree) expr.getTree();
                CommonTreeNodeStream nodes = new CommonTreeNodeStream( tree );
                nodes.setTokenStream( input );
                // Create a tree walker attached to the nodes stream
                DRLExprTree walker = new DRLExprTree( nodes );
                walker.setHelper( helper );
                constraint = walker.constraint();
            }
        } catch ( RecognitionException e ) {
            helper.reportError( e );
        }
        return constraint;
    }

    /**
     * @return true if there were parser errors.
     */
    public boolean hasErrors() {
        return helper != null && helper.hasErrors();
    }

    /**
     * @return a list of errors found while parsing. 
     */
    @SuppressWarnings("unchecked")
    public List<DroolsParserException> getErrors() {
        return helper != null ? helper.getErrors() : Collections.EMPTY_LIST;
    }

}
