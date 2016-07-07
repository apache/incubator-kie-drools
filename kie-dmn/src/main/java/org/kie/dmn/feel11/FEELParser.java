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

package org.kie.dmn.feel11;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.ParseTree;

import java.util.stream.Stream;

public class FEELParser {

    public static ParseTree parse(String source, String... symbols) {
        ANTLRInputStream input = new ANTLRInputStream(source);
        FEEL_1_1Lexer lexer = new FEEL_1_1Lexer( input );
        CommonTokenStream tokens = new CommonTokenStream( lexer );
        FEEL_1_1Parser parser = new FEEL_1_1Parser( tokens );
        parser.setErrorHandler( new FEELErrorHandler() );

        // pre-loads the parser with symbols
        Stream.of( symbols ).forEach( s -> parser.getHelper().defineVariable( s ) );
        ParseTree tree = parser.expression();
        return tree;
    }

    public static class FEELErrorHandler extends DefaultErrorStrategy {

        @Override
        protected void reportFailedPredicate(Parser recognizer, FailedPredicateException e) {
            // don't do anything
        }

//        @Override
//        public void recover(Parser recognizer, RecognitionException e) {
//            if( e instanceof FailedPredicateException ) {
//                // not an error, don't try to recover
//                return;
//            }
//            super.recover( recognizer, e );
//        }
    }

}
