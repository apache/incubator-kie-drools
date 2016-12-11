/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.dmn.feel.parser.feel11;

import org.antlr.v4.runtime.*;
import org.kie.dmn.feel.lang.Type;
import org.kie.dmn.feel.lang.impl.JavaBackedType;
import org.kie.dmn.feel.lang.impl.FEELEventListenersManager;
import org.kie.dmn.feel.runtime.events.FEELEvent;
import org.kie.dmn.feel.runtime.events.SyntaxErrorEvent;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class FEELParser {

    public static FEEL_1_1Parser parse(FEELEventListenersManager eventsManager, String source, Map<String, Type> inputVariableTypes, Map<String, Object> inputVariables) {
        ANTLRInputStream input = new ANTLRInputStream(source);
        FEEL_1_1Lexer lexer = new FEEL_1_1Lexer( input );
        CommonTokenStream tokens = new CommonTokenStream( lexer );
        FEEL_1_1Parser parser = new FEEL_1_1Parser( tokens );
        parser.setErrorHandler( new FEELErrorHandler() );
        parser.removeErrorListeners(); // removes the error listener that prints to the console
        parser.addErrorListener( new FEELParserErrorListener( eventsManager ) );

        // pre-loads the parser with symbols
        defineVariables( inputVariableTypes, inputVariables, parser );
        
        return parser;
    }

    public static boolean isVariableNameValid( String source ) {
        if( source == null ) {
            return false;
        }
        ANTLRInputStream input = new ANTLRInputStream(source);
        FEEL_1_1Lexer lexer = new FEEL_1_1Lexer( input );
        CommonTokenStream tokens = new CommonTokenStream( lexer );
        FEEL_1_1Parser parser = new FEEL_1_1Parser( tokens );
        parser.setErrorHandler( new FEELErrorHandler() );
        FEELParserErrorChecker errorChecker = new FEELParserErrorChecker();
        parser.removeErrorListeners(); // removes the error listener that prints to the console
        parser.addErrorListener( errorChecker );
        FEEL_1_1Parser.NameDefinitionContext nameDef = parser.nameDefinition();

        if( ! errorChecker.hasErrors() &&
            nameDef != null &&
            source.trim().equals( parser.getHelper().getOriginalText( nameDef ) ) ) {
            return true;
        }
        return false;
    }

    private static void defineVariables(Map<String, Type> inputVariableTypes, Map<String, Object> inputVariables, FEEL_1_1Parser parser) {
        inputVariableTypes.forEach( (name, type) -> {
            parser.getHelper().defineVariable( name, type );
        } );
        
        inputVariables.forEach( (name, value) -> {
            parser.getHelper().defineVariable( name );
            if( value instanceof Map ) {
                try {
                    parser.getHelper().pushName( name );
                    parser.getHelper().pushScope();
                    defineVariables( Collections.EMPTY_MAP, (Map<String, Object>) value, parser );
                } finally {
                    parser.getHelper().popScope();
                    parser.getHelper().popName();
                }
            }
        } );

    }

    public static class FEELErrorHandler extends DefaultErrorStrategy {

        @Override
        protected void reportFailedPredicate(Parser recognizer, FailedPredicateException e) {
            // don't do anything
        }
    }

    public static class FEELParserErrorListener extends BaseErrorListener {
        private final FEELEventListenersManager eventsManager;

        public FEELParserErrorListener(FEELEventListenersManager eventsManager) {
            this.eventsManager = eventsManager;
        }

        @Override
        public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e) {
            FEELEventListenersManager.notifyListeners( eventsManager , () -> {
                return new SyntaxErrorEvent( FEELEvent.Severity.ERROR,
                                             msg,
                                             e,
                                             line,
                                             charPositionInLine,
                                             offendingSymbol );
                }
            );
        }
    }

    public static class FEELParserErrorChecker extends BaseErrorListener {
        private final AtomicBoolean errors = new AtomicBoolean(false);

        @Override
        public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e) {
            this.errors.set( true );
        }

        public boolean hasErrors() {
            return this.errors.get();
        }
    }

}
