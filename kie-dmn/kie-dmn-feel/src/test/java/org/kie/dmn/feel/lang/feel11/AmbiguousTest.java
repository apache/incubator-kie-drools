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

package org.kie.dmn.feel.lang.feel11;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.junit.Test;

public class AmbiguousTest {

    @Test
    public void testFunctionInvocationWithKeyword() {
        String inputExpression = "date and time( now )";
        ANTLRInputStream input = new ANTLRInputStream( inputExpression );
        AmbiguousLexer lexer = new AmbiguousLexer( input );
        CommonTokenStream tokens = new CommonTokenStream( lexer );
        AmbiguousParser parser = new AmbiguousParser( tokens );
        ParseTree tree = parser.expr();

        System.out.println( tree.toStringTree( parser ) );

    }

    @Test
    public void testLogical() {
        String inputExpression = "someX or someY and someZ";
        ANTLRInputStream input = new ANTLRInputStream( inputExpression );
        AmbiguousLexer lexer = new AmbiguousLexer( input );
        CommonTokenStream tokens = new CommonTokenStream( lexer );
        AmbiguousParser parser = new AmbiguousParser( tokens );
        ParseTree tree = parser.expr();

        System.out.println( tree.toStringTree( parser ) );
    }

}
