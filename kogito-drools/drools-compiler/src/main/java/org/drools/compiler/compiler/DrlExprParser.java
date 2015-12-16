/*
 * Copyright 2011 Red Hat, Inc. and/or its affiliates.
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

package org.drools.compiler.compiler;

import java.util.Collections;
import java.util.List;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.RecognizerSharedState;
import org.drools.compiler.lang.DRLExpressions;
import org.drools.compiler.lang.DRLLexer;
import org.drools.compiler.lang.ParserHelper;
import org.drools.compiler.lang.descr.BaseDescr;
import org.drools.compiler.lang.descr.ConstraintConnectiveDescr;
import org.kie.internal.builder.conf.LanguageLevelOption;

/**
 * This is a helper class that provides helper methods to parse expressions
 * using both the DRLExpressions parser and the DRLExprTree parser.
 */
public class DrlExprParser {

    private ParserHelper helper = null;

    private final LanguageLevelOption languageLevel;

    public DrlExprParser(LanguageLevelOption languageLevel) {
        this.languageLevel = languageLevel;
    }

    /** Parse an expression from text */
    public ConstraintConnectiveDescr parse( final String text ) {
        ConstraintConnectiveDescr constraint = null;
        try {
            DRLLexer lexer = DRLFactory.getDRLLexer(new ANTLRStringStream(text), languageLevel);
            CommonTokenStream input = new CommonTokenStream( lexer );
            RecognizerSharedState state = new RecognizerSharedState();
            helper = new ParserHelper( input, state, languageLevel );
            DRLExpressions parser = DRLFactory.getDRLExpressions(input, state, helper, languageLevel);
            parser.setBuildDescr( true );
            parser.setLeftMostExpr( null ); // setting initial value just in case
            BaseDescr expr = parser.conditionalOrExpression();
            if ( expr != null && !parser.hasErrors() ) {
                constraint = ConstraintConnectiveDescr.newAnd();
                constraint.addOrMerge( expr );
            }
        } catch ( RecognitionException e ) {
            helper.reportError( e );
        }
        return constraint;
    }
    
    public String getLeftMostExpr() {
        return helper != null ? helper.getLeftMostExpr() : null;
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
