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
import org.kie.dmn.api.feel.runtime.events.FEELEvent;
import org.kie.dmn.feel.lang.Type;
import org.kie.dmn.feel.lang.impl.FEELEventListenersManager;
import org.kie.dmn.feel.runtime.events.SyntaxErrorEvent;
import org.kie.dmn.feel.util.Msg;

import java.util.*;
import java.util.regex.Pattern;

public class FEELParser {

    private static final List<String> REUSABLE_KEYWORDS = Arrays.asList(
            "for", "return", "if", "then", "else", "some", "every", "satisfies", "instance", "of",
            "function", "external", "or", "and", "between", "not", "null", "true", "false"
    );
    private static final Pattern DIGITS_PATTERN = Pattern.compile( "[0-9]*" );

    public static FEEL_1_1Parser parse(FEELEventListenersManager eventsManager, String source, Map<String, Type> inputVariableTypes, Map<String, Object> inputVariables) {
        ANTLRInputStream input = new ANTLRInputStream(source);
        FEEL_1_1Lexer lexer = new FEEL_1_1Lexer( input );
        CommonTokenStream tokens = new CommonTokenStream( lexer );
        FEEL_1_1Parser parser = new FEEL_1_1Parser( tokens );
        parser.setHelper( new ParserHelper( eventsManager ) );
        parser.setErrorHandler( new FEELErrorHandler() );
        parser.removeErrorListeners(); // removes the error listener that prints to the console
        parser.addErrorListener( new FEELParserErrorListener( eventsManager ) );

        // pre-loads the parser with symbols
        defineVariables( inputVariableTypes, inputVariables, parser );
        
        return parser;
    }
    
    /**
     * Either namePart is a string of digits, or it must be a valid name itself 
     */
    public static boolean isVariableNamePartValid( String namePart ) {
        return DIGITS_PATTERN.matcher(namePart).matches() || isVariableNameValid(namePart);
    }

    public static boolean isVariableNameValid( String source ) {
        return checkVariableName( source ).isEmpty();
    }

    public static List<FEELEvent> checkVariableName( String source ) {
        if( source == null || source.isEmpty() ) {
            return Collections.singletonList( new SyntaxErrorEvent( FEELEvent.Severity.ERROR,
                                                                    Msg.createMessage( Msg.INVALID_VARIABLE_NAME_EMPTY ),
                                                                    null,
                                                                    0,
                                                                    0,
                                                                    null ) );
        }
        ANTLRInputStream input = new ANTLRInputStream(source);
        FEEL_1_1Lexer lexer = new FEEL_1_1Lexer( input );
        CommonTokenStream tokens = new CommonTokenStream( lexer );
        FEEL_1_1Parser parser = new FEEL_1_1Parser( tokens );
        parser.setHelper( new ParserHelper() );
        parser.setErrorHandler( new FEELErrorHandler() );
        FEELParserErrorListener errorChecker = new FEELParserErrorListener( null );
        parser.removeErrorListeners(); // removes the error listener that prints to the console
        parser.addErrorListener( errorChecker );
        FEEL_1_1Parser.NameDefinitionContext nameDef = parser.nameDefinition();

        if( ! errorChecker.hasErrors() &&
            nameDef != null &&
            source.trim().equals( parser.getHelper().getOriginalText( nameDef ) ) ) {
            return Collections.emptyList();
        }
        return errorChecker.getErrors();
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
        private List<FEELEvent> errors = null;

        public FEELParserErrorListener(FEELEventListenersManager eventsManager) {
            this.eventsManager = eventsManager;
        }

        @Override
        public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e) {
            final SyntaxErrorEvent error;

            CommonToken token = (CommonToken) offendingSymbol;
            if( ((Parser)recognizer).getRuleInvocationStack().contains( "nameDefinition" ) ) {
                error = generateInvalidVariableError( offendingSymbol, line, charPositionInLine, e, token );
            } else {
                error = new SyntaxErrorEvent( FEELEvent.Severity.ERROR,
                                                  msg,
                                                  e,
                                                  line,
                                                  charPositionInLine,
                                                  offendingSymbol );
            }

            // if the event manager is set, notify listeners, otherwise store the error in the errors list
            if( eventsManager != null ) {
                FEELEventListenersManager.notifyListeners( eventsManager , () -> error );
            } else {
                if( errors == null ) {
                    errors = new ArrayList<>(  );
                }
                errors.add( error );
            }
        }

        public boolean hasErrors() {
            return this.errors != null && ! this.errors.isEmpty();
        }

        public List<FEELEvent> getErrors() {
            return errors;
        }
    }

    private static SyntaxErrorEvent generateInvalidVariableError(Object offendingSymbol, int line, int charPositionInLine, RecognitionException e, CommonToken token) {
        String chars = token.getText().length() == 1 ? "character" : "sequence of characters";
        if( charPositionInLine == 0 ) {
            if( "in".equals( token.getText() ) || REUSABLE_KEYWORDS.contains( token.getText() ) ) {
                return new SyntaxErrorEvent(
                        FEELEvent.Severity.ERROR,
                        Msg.createMessage( Msg.INVALID_VARIABLE_NAME_START, "keyword", token.getText() ),
                        e,
                        line,
                        charPositionInLine,
                        offendingSymbol );
            } else {
                return new SyntaxErrorEvent(
                        FEELEvent.Severity.ERROR,
                        Msg.createMessage( Msg.INVALID_VARIABLE_NAME_START, chars, token.getText() ),
                        e,
                        line,
                        charPositionInLine,
                        offendingSymbol );
            }
        } else if( "in".equals( token.getText() ) ) {
            return new SyntaxErrorEvent( FEELEvent.Severity.ERROR,
                                              Msg.createMessage( Msg.INVALID_VARIABLE_NAME, "keyword", token.getText() ),
                                              e,
                                              line,
                                              charPositionInLine,
                                              offendingSymbol );
        } else {
            return new SyntaxErrorEvent( FEELEvent.Severity.ERROR,
                                              Msg.createMessage( Msg.INVALID_VARIABLE_NAME, chars, token.getText() ),
                                              e,
                                              line,
                                              charPositionInLine,
                                              offendingSymbol );
        }
    }

}
