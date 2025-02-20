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
package org.drools.drl.parser.antlr4;

import java.util.Collections;
import java.util.List;

import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.drools.drl.ast.descr.BaseDescr;
import org.drools.drl.ast.descr.ConstraintConnectiveDescr;
import org.drools.drl.parser.DrlExprParser;
import org.drools.drl.parser.DroolsParserException;
import org.kie.internal.builder.conf.LanguageLevelOption;

/**
 * This is a helper class that provides helper methods to parse expressions
 * using both the DRLExpressions parser and the DRLExprTree parser.
 */
public class Drl10ExprParser implements DrlExprParser {

    private ParserHelper helper = null;

    private final LanguageLevelOption languageLevel;

    public Drl10ExprParser(LanguageLevelOption languageLevel) {
        this.languageLevel = languageLevel;
    }

    /** Parse an expression from text */
    public ConstraintConnectiveDescr parse(final String text) {
        ConstraintConnectiveDescr constraint = null;
        DRL10Lexer lexer = new DRL10Lexer(CharStreams.fromString(text));
        CommonTokenStream input = new CommonTokenStream(lexer);
        helper = new ParserHelper(input, languageLevel);
        DRL10Expressions parser = new DRL10Expressions(input);
        parser.setHelper(helper);
        parser.addErrorListener(new BaseErrorListener() {
            @Override
            public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e) {
                helper.reportError(offendingSymbol, line, charPositionInLine, msg, e);
            }
        });
        parser.setBuildDescr(true);
        parser.setLeftMostExpr(null); // setting initial value just in case
        BaseDescr expr = parser.conditionalOrExpressionDescr();
        if (expr != null && !parser.hasErrors()) {
            constraint = ConstraintConnectiveDescr.newAnd();
            constraint.addOrMerge(expr);
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
