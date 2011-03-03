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
import org.drools.lang.DRLExpressions;
import org.drools.lang.DRLLexer;
import org.drools.lang.ParserHelper;
import org.drools.lang.descr.BaseDescr;
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
            parser.setBuildDescr( true );
            BaseDescr expr = parser.conditionalAndExpression();
            if ( expr != null && !parser.hasErrors() ) {
                constraint = ConstraintConnectiveDescr.newAnd();
                constraint.addOrMerge( expr );
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
