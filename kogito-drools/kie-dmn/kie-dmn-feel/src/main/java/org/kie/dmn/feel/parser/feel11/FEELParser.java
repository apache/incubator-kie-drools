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

import java.util.Collections;
import java.util.Map;

public class FEELParser {

    public static FEEL_1_1Parser parse(String source, Map<String, Type> inputVariableTypes, Map<String, Object> inputVariables) {
        ANTLRInputStream input = new ANTLRInputStream(source);
        FEEL_1_1Lexer lexer = new FEEL_1_1Lexer( input );
        CommonTokenStream tokens = new CommonTokenStream( lexer );
        FEEL_1_1Parser parser = new FEEL_1_1Parser( tokens );
        parser.setErrorHandler( new FEELErrorHandler() );

        // pre-loads the parser with symbols
        defineVariables( inputVariableTypes, inputVariables, parser );
        return parser;
    }

    private static void defineVariables(Map<String, Type> inputVariableTypes, Map<String, Object> inputVariables, FEEL_1_1Parser parser) {
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
        inputVariableTypes.forEach( (name, type) -> {
            if( ! inputVariables.containsKey( name ) ) {
                parser.getHelper().defineVariable( name );
            }
        } );
    }

    public static class FEELErrorHandler extends DefaultErrorStrategy {

        @Override
        protected void reportFailedPredicate(Parser recognizer, FailedPredicateException e) {
            // don't do anything
        }



    }

}
