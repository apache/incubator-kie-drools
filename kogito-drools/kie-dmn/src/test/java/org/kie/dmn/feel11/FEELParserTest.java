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

import org.antlr.v4.runtime.tree.ParseTree;
import org.junit.Test;
import org.kie.dmn.lang.ast.BaseNode;
import org.kie.dmn.lang.ast.NumberNode;
import org.kie.dmn.lang.ast.SignedUnaryNode;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class FEELParserTest {

    @Test
    public void testIntegerLiteral() {
        String token = "10";
        BaseNode number = parse( token );

        assertThat( number, is( instanceOf( NumberNode.class ) ) );
        assertTokenLocation( token, number );
    }

    @Test
    public void testNegativeIntegerLiteral() {
        String token = "-10";
        BaseNode number = parse( token );

        assertThat( number, is( instanceOf( SignedUnaryNode.class ) ) );
        assertTokenLocation( token, number );

        SignedUnaryNode sun = (SignedUnaryNode) number;
        assertThat( sun.getSign(), is(SignedUnaryNode.Sign.NEGATIVE ) );
        assertThat( sun.getExpression(), is(instanceOf( NumberNode.class )));
        assertThat( sun.getExpression().getText(), is( "10" ) );
    }

    @Test
    public void testPositiveIntegerLiteral() {
        String token = "+10";
        BaseNode number = parse( token );

        assertThat( number, is( instanceOf( SignedUnaryNode.class ) ) );
        assertTokenLocation( token, number );

        SignedUnaryNode sun = (SignedUnaryNode) number;
        assertThat( sun.getSign(), is(SignedUnaryNode.Sign.POSITIVE ) );
        assertThat( sun.getExpression(), is(instanceOf( NumberNode.class )));
        assertThat( sun.getExpression().getText(), is("10") );
    }

    @Test
    public void testFloatLiteral() {
        String token = "10.5";
        BaseNode number = parse( token );

        assertThat( number, is( instanceOf( NumberNode.class ) ) );
        assertTokenLocation( token, number );
    }

    @Test
    public void testNegativeFloatLiteral() {
        String token = "-10.5";
        BaseNode number = parse( token );

        assertThat( number, is( instanceOf( SignedUnaryNode.class ) ) );
        assertTokenLocation( token, number );

        SignedUnaryNode sun = (SignedUnaryNode) number;
        assertThat( sun.getSign(), is(SignedUnaryNode.Sign.NEGATIVE ) );
        assertThat( sun.getExpression(), is(instanceOf( NumberNode.class )));
        assertThat( sun.getExpression().getText(), is( "10.5" ) );
    }

    @Test
    public void testPositiveFloatLiteral() {
        String token = "+10.5";
        BaseNode number = parse( token );

        assertThat( number, is( instanceOf( SignedUnaryNode.class ) ) );
        assertTokenLocation( token, number );

        SignedUnaryNode sun = (SignedUnaryNode) number;
        assertThat( sun.getSign(), is(SignedUnaryNode.Sign.POSITIVE ) );
        assertThat( sun.getExpression(), is(instanceOf( NumberNode.class )));
        assertThat( sun.getExpression().getText(), is( "10.5" ) );
    }

    private void assertTokenLocation(String token, BaseNode number) {
        assertThat( number.getText(), is( token ) );
        assertThat( number.getStartChar(), is(0) );
        assertThat( number.getStartLine(), is(1) );
        assertThat( number.getStartColumn(), is(0) );
        assertThat( number.getEndChar(), is(token.length()-1) );
        assertThat( number.getEndLine(), is(1) );
        assertThat( number.getEndColumn(), is( token.length() ) );
    }

    private BaseNode parse(String input) {
        ParseTree tree = FEELParser.parse( input );
        ASTBuilderVisitor v = new ASTBuilderVisitor();
        BaseNode expr = v.visit( tree );
        return expr;
    }

}
