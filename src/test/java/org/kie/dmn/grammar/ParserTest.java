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

package org.kie.dmn.grammar;

import org.junit.Assert;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.junit.Before;
import org.junit.Test;
import org.kie.dmn.feel.MvelFEELEvaluator;

import java.util.HashMap;
import java.util.Map;

public class ParserTest {

    private MvelFEELEvaluator feel;

    @Before
    public void setup() {
        feel = new MvelFEELEvaluator();
    }

    @Test
    public void testSimpleLiteral() {
        evaluateExpression( "10", 10 );
    }

    @Test
    public void testSimpleSum() {
        evaluateExpression( "10 + 10", 20 );
    }

    @Test
    public void testSimpleExpression() {
        evaluateExpression( "10 + 5 * 5", 35 );
    }

    @Test
    public void testInWithListOfValues() {
        evaluateExpression( "5 in (4, 5, 6)", true );
    }

    @Test
    public void testInWithListOfValues2() {
        evaluateExpression( "10+3 in (7-1, 8+2, 1+2*6)", true );
    }

    @Test
    public void testInWithUnaryOperator() {
        evaluateExpression( "10+3 in < 20", true );
    }

    @Test
    public void testInWithUnaryOperators() {
        evaluateExpression( "10+3 in ( > 1000 , < 20, null )", true );
    }

    @Test
    public void testInWithUnaryOperators2() {
        evaluateExpression( "10+3 in ( > 1000 , null )", false );
    }

    private void evaluateExpression(String expr, Object result) {
        evaluateExpression( expr, result, new HashMap<String, Object>( ) );
    }

    private void evaluateExpression(String expr, Object result, Map<String, Object> vars) {
        FEEL_1_1Parser parser = parse( expr );
        ParseTree tree = parser.expression();
        Assert.assertEquals( result, feel.evaluate( tree, vars ) );
    }

    private FEEL_1_1Parser parse( String source ) {
        ANTLRInputStream input = new ANTLRInputStream(source);
        FEEL_1_1Lexer lexer = new FEEL_1_1Lexer( input );
        CommonTokenStream tokens = new CommonTokenStream( lexer );
        FEEL_1_1Parser parser = new FEEL_1_1Parser( tokens );
        return parser;
    }


}
