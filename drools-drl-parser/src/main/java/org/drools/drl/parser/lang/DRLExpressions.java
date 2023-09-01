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
package org.drools.drl.parser.lang;

import org.antlr.runtime.Parser;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.RecognizerSharedState;
import org.antlr.runtime.TokenStream;
import org.drools.drl.parser.DroolsParserException;
import org.drools.drl.ast.descr.BaseDescr;

import java.util.LinkedList;
import java.util.List;

public abstract class DRLExpressions extends Parser {
    public DRLExpressions(TokenStream input, RecognizerSharedState state) {
        super(input, state);
    }

    public abstract void setBuildDescr( boolean build );
    public abstract boolean isBuildDescr();

    public abstract void setLeftMostExpr( String value );
    public abstract String getLeftMostExpr();

    public abstract void setHasBindings( boolean value );
    public abstract boolean hasBindings();

    public abstract BaseDescr conditionalOrExpression() throws RecognitionException;

    public abstract ParserHelper getHelper();
    public abstract boolean hasErrors();
    public abstract List<DroolsParserException> getErrors();
    public abstract List<String> getErrorMessages();
    public abstract void enableEditorInterface();
    public abstract void disableEditorInterface();
    public abstract LinkedList<DroolsSentence> getEditorInterface();
}
