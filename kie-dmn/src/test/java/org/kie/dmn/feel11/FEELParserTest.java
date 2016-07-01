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
import org.kie.dmn.lang.ast.*;

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
        assertThat( sun.getSign(), is( SignedUnaryNode.Sign.NEGATIVE ) );
        assertThat( sun.getExpression(), is( instanceOf( NumberNode.class ) ) );
        assertThat( sun.getExpression().getText(), is( "10" ) );
    }

    @Test
    public void testPositiveIntegerLiteral() {
        String token = "+10";
        BaseNode number = parse( token );

        assertThat( number, is( instanceOf( SignedUnaryNode.class ) ) );
        assertTokenLocation( token, number );

        SignedUnaryNode sun = (SignedUnaryNode) number;
        assertThat( sun.getSign(), is( SignedUnaryNode.Sign.POSITIVE ) );
        assertThat( sun.getExpression(), is( instanceOf( NumberNode.class ) ) );
        assertThat( sun.getExpression().getText(), is( "10" ) );
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
        assertThat( sun.getSign(), is( SignedUnaryNode.Sign.NEGATIVE ) );
        assertThat( sun.getExpression(), is( instanceOf( NumberNode.class ) ) );
        assertThat( sun.getExpression().getText(), is( "10.5" ) );
    }

    @Test
    public void testPositiveFloatLiteral() {
        String token = "+10.5";
        BaseNode number = parse( token );

        assertThat( number, is( instanceOf( SignedUnaryNode.class ) ) );
        assertTokenLocation( token, number );

        SignedUnaryNode sun = (SignedUnaryNode) number;
        assertThat( sun.getSign(), is( SignedUnaryNode.Sign.POSITIVE ) );
        assertThat( sun.getExpression(), is( instanceOf( NumberNode.class ) ) );
        assertThat( sun.getExpression().getText(), is( "10.5" ) );
    }

    @Test
    public void testBooleanTrueLiteral() {
        String token = "true";
        BaseNode bool = parse( token );

        assertThat( bool, is( instanceOf( BooleanNode.class ) ) );
        assertTokenLocation( token, bool );
    }

    @Test
    public void testBooleanFalseLiteral() {
        String token = "false";
        BaseNode bool = parse( token );

        assertThat( bool, is( instanceOf( BooleanNode.class ) ) );
        assertTokenLocation( token, bool );
    }

    @Test
    public void testNullLiteral() {
        String token = "null";
        BaseNode nullLit = parse( token );

        assertThat( nullLit, is( instanceOf( NullNode.class ) ) );
        assertTokenLocation( token, nullLit );
    }

    @Test
    public void testStringLiteral() {
        String token = "\"some string\"";
        BaseNode nullLit = parse( token );

        assertThat( nullLit, is( instanceOf( StringNode.class ) ) );
        assertTokenLocation( token, nullLit );
    }

    @Test
    public void testNameReference() {
        String token = "someSimpleName";
        BaseNode nullLit = parse( token );

        assertThat( nullLit, is( instanceOf( VariableNode.class ) ) );
        assertTokenLocation( token, nullLit );
    }

    @Test
    public void testParensWithLiteral() {
        String token = "(10.5 )";
        BaseNode number = parse( token );

        assertThat( number, is( instanceOf( NumberNode.class ) ) );
        assertThat( number.getText(), is( "10.5" ) );
    }

    @Test
    public void testLogicalNegation() {
        String token = "not ( true )";
        BaseNode neg = parse( token );

        assertThat( neg, is( instanceOf( NotNode.class ) ) );
        assertThat( neg.getText(), is( "not ( true )" ) );

        NotNode not = (NotNode) neg;
        assertThat( not.getExpression(), is( instanceOf( BooleanNode.class ) ) );
        assertThat( not.getExpression().getText(), is( "true" ) );
    }

    @Test
    public void testMultiplication() {
        String token = "10 * x";
        BaseNode infix = parse( token );

        assertThat( infix, is( instanceOf( InfixOpNode.class ) ) );
        assertThat( infix.getText(), is( token ) );

        InfixOpNode mult = (InfixOpNode) infix;
        assertThat( mult.getLeft(), is( instanceOf( NumberNode.class ) ) );
        assertThat( mult.getLeft().getText(), is( "10" ) );

        assertThat( mult.getOperator(), is( "*" ) );

        assertThat( mult.getRight(), is( instanceOf( VariableNode.class ) ) );
        assertThat( mult.getRight().getText(), is( "x" ) );
    }

    @Test
    public void testDivision() {
        String token = "y / 5 * ( x )";
        BaseNode infix = parse( token );

        assertThat( infix, is( instanceOf( InfixOpNode.class ) ) );
        assertThat( infix.getText(), is( token ) );

        InfixOpNode mult = (InfixOpNode) infix;
        assertThat( mult.getLeft(), is( instanceOf( InfixOpNode.class ) ) );
        assertThat( mult.getLeft().getText(), is( "y / 5" ) );

        InfixOpNode div = (InfixOpNode) mult.getLeft();
        assertThat( div.getLeft(), is( instanceOf( VariableNode.class ) ) );
        assertThat( div.getLeft().getText(), is( "y" ) );

        assertThat( div.getOperator(), is( "/" ) );

        assertThat( div.getRight(), is( instanceOf( NumberNode.class ) ) );
        assertThat( div.getRight().getText(), is( "5" ) );

        assertThat( mult.getOperator(), is( "*" ) );

        assertThat( mult.getRight(), is( instanceOf( VariableNode.class ) ) );
        assertThat( mult.getRight().getText(), is( "x" ) );
    }

    @Test
    public void testPower1() {
        String token = "y * 5 ** 3";
        BaseNode infix = parse( token );

        assertThat( infix, is( instanceOf( InfixOpNode.class ) ) );
        assertThat( infix.getText(), is( token ) );

        InfixOpNode mult = (InfixOpNode) infix;
        assertThat( mult.getLeft(), is( instanceOf( VariableNode.class ) ) );
        assertThat( mult.getLeft().getText(), is( "y" ) );

        assertThat( mult.getOperator(), is( "*" ) );

        assertThat( mult.getRight(), is( instanceOf( InfixOpNode.class ) ) );
        assertThat( mult.getRight().getText(), is( "5 ** 3" ) );

        InfixOpNode exp = (InfixOpNode) mult.getRight();
        assertThat( exp.getLeft(), is( instanceOf( NumberNode.class ) ) );
        assertThat( exp.getLeft().getText(), is( "5" ) );

        assertThat( exp.getOperator(), is( "**" ) );

        assertThat( exp.getRight(), is( instanceOf( NumberNode.class ) ) );
        assertThat( exp.getRight().getText(), is( "3" ) );
    }

    @Test
    public void testPower2() {
        String token = "(y * 5) ** 3";
        BaseNode infix = parse( token );

        assertThat( infix, is( instanceOf( InfixOpNode.class ) ) );
        assertThat( infix.getText(), is( token ) );

        InfixOpNode exp = (InfixOpNode) infix;
        assertThat( exp.getLeft(), is( instanceOf( InfixOpNode.class ) ) );
        assertThat( exp.getLeft().getText(), is( "y * 5" ) );

        assertThat( exp.getOperator(), is( "**" ) );

        assertThat( exp.getRight(), is( instanceOf( NumberNode.class ) ) );
        assertThat( exp.getRight().getText(), is( "3" ) );

        InfixOpNode mult = (InfixOpNode) exp.getLeft();
        assertThat( mult.getLeft(), is( instanceOf( VariableNode.class ) ) );
        assertThat( mult.getLeft().getText(), is( "y" ) );

        assertThat( mult.getOperator(), is( "*" ) );

        assertThat( mult.getRight(), is( instanceOf( NumberNode.class ) ) );
        assertThat( mult.getRight().getText(), is( "5" ) );
    }

    @Test
    public void testPower3() {
        String token = "y ** 5 * 3";
        BaseNode infix = parse( token );

        assertThat( infix, is( instanceOf( InfixOpNode.class ) ) );
        assertThat( infix.getText(), is( token ) );

        InfixOpNode mult = (InfixOpNode) infix;
        assertThat( mult.getLeft(), is( instanceOf( InfixOpNode.class ) ) );
        assertThat( mult.getLeft().getText(), is( "y ** 5" ) );

        assertThat( mult.getOperator(), is( "*" ) );

        assertThat( mult.getRight(), is( instanceOf( NumberNode.class ) ) );
        assertThat( mult.getRight().getText(), is( "3" ) );

        InfixOpNode exp = (InfixOpNode) mult.getLeft();
        assertThat( exp.getLeft(), is( instanceOf( VariableNode.class ) ) );
        assertThat( exp.getLeft().getText(), is( "y" ) );

        assertThat( exp.getOperator(), is( "**" ) );

        assertThat( exp.getRight(), is( instanceOf( NumberNode.class ) ) );
        assertThat( exp.getRight().getText(), is( "5" ) );
    }

    @Test
    public void testPower4() {
        String token = "y ** ( 5 * 3 )";
        BaseNode infix = parse( token );

        assertThat( infix, is( instanceOf( InfixOpNode.class ) ) );
        assertThat( infix.getText(), is( token ) );

        InfixOpNode exp = (InfixOpNode) infix;
        assertThat( exp.getLeft(), is( instanceOf( VariableNode.class ) ) );
        assertThat( exp.getLeft().getText(), is( "y" ) );

        assertThat( exp.getOperator(), is( "**" ) );

        assertThat( exp.getRight(), is( instanceOf( InfixOpNode.class ) ) );
        assertThat( exp.getRight().getText(), is( "5 * 3" ) );

        InfixOpNode mult = (InfixOpNode) exp.getRight();
        assertThat( mult.getLeft(), is( instanceOf( NumberNode.class ) ) );
        assertThat( mult.getLeft().getText(), is( "5" ) );

        assertThat( mult.getOperator(), is( "*" ) );

        assertThat( mult.getRight(), is( instanceOf( NumberNode.class ) ) );
        assertThat( mult.getRight().getText(), is( "3" ) );
    }

    @Test
    public void testAdd1() {
        String token = "y + 5 * 3";
        BaseNode infix = parse( token );

        assertThat( infix, is( instanceOf( InfixOpNode.class ) ) );
        assertThat( infix.getText(), is( token ) );

        InfixOpNode add = (InfixOpNode) infix;
        assertThat( add.getLeft(), is( instanceOf( VariableNode.class ) ) );
        assertThat( add.getLeft().getText(), is( "y" ) );

        assertThat( add.getOperator(), is( "+" ) );

        assertThat( add.getRight(), is( instanceOf( InfixOpNode.class ) ) );
        assertThat( add.getRight().getText(), is( "5 * 3" ) );

        InfixOpNode mult = (InfixOpNode) add.getRight();
        assertThat( mult.getLeft(), is( instanceOf( NumberNode.class ) ) );
        assertThat( mult.getLeft().getText(), is( "5" ) );

        assertThat( mult.getOperator(), is( "*" ) );

        assertThat( mult.getRight(), is( instanceOf( NumberNode.class ) ) );
        assertThat( mult.getRight().getText(), is( "3" ) );
    }

    @Test
    public void testSub1() {
        String token = "(y - 5) ** 3";
        BaseNode infix = parse( token );

        assertThat( infix, is( instanceOf( InfixOpNode.class ) ) );
        assertThat( infix.getText(), is( token ) );

        InfixOpNode sub = (InfixOpNode) infix;
        assertThat( sub.getLeft(), is( instanceOf( InfixOpNode.class ) ) );
        assertThat( sub.getLeft().getText(), is( "y - 5" ) );

        assertThat( sub.getOperator(), is( "**" ) );

        assertThat( sub.getRight(), is( instanceOf( NumberNode.class ) ) );
        assertThat( sub.getRight().getText(), is( "3" ) );

        InfixOpNode mult = (InfixOpNode) sub.getLeft();
        assertThat( mult.getLeft(), is( instanceOf( VariableNode.class ) ) );
        assertThat( mult.getLeft().getText(), is( "y" ) );

        assertThat( mult.getOperator(), is( "-" ) );

        assertThat( mult.getRight(), is( instanceOf( NumberNode.class ) ) );
        assertThat( mult.getRight().getText(), is( "5" ) );
    }

    @Test
    public void testBetween() {
        String token = "x between 10+y and 3**z";
        BaseNode between = parse( token );

        assertThat( between, is( instanceOf( BetweenNode.class ) ) );
        assertThat( between.getText(), is( token ) );

        BetweenNode btw = (BetweenNode) between;
        assertThat( btw.getValue(), is( instanceOf( VariableNode.class ) ) );
        assertThat( btw.getValue().getText(), is( "x" ) );

        assertThat( btw.getStart(), is( instanceOf( InfixOpNode.class ) ) );
        assertThat( btw.getStart().getText(), is( "10+y" ) );

        assertThat( btw.getEnd(), is( instanceOf( InfixOpNode.class ) ) );
        assertThat( btw.getEnd().getText(), is( "3**z" ) );
    }

    @Test
    public void testInValueList() {
        String token = "x / 4 in ( 10+y, true, 80, someVar )";
        BaseNode inNode = parse( token );

        assertThat( inNode, is( instanceOf( InNode.class ) ) );
        assertThat( inNode.getText(), is( token ) );

        InNode in = (InNode) inNode;
        assertThat( in.getValue(), is( instanceOf( InfixOpNode.class ) ) );
        assertThat( in.getValue().getText(), is( "x / 4" ) );

        assertThat( in.getExprs(), is( instanceOf( ListNode.class ) ) );
        assertThat( in.getExprs().getText(), is( "10+y, true, 80, someVar" ) );

        ListNode list = (ListNode) in.getExprs();
        assertThat( list.getElements().get( 0 ), is( instanceOf( InfixOpNode.class ) ) );
        assertThat( list.getElements().get( 1 ), is( instanceOf( BooleanNode.class ) ) );
        assertThat( list.getElements().get( 2 ), is( instanceOf( NumberNode.class ) ) );
        assertThat( list.getElements().get( 3 ), is( instanceOf( VariableNode.class ) ) );
    }

    private void assertTokenLocation(String token, BaseNode number) {
        assertThat( number.getText(), is( token ) );
        assertThat( number.getStartChar(), is( 0 ) );
        assertThat( number.getStartLine(), is( 1 ) );
        assertThat( number.getStartColumn(), is( 0 ) );
        assertThat( number.getEndChar(), is( token.length() - 1 ) );
        assertThat( number.getEndLine(), is( 1 ) );
        assertThat( number.getEndColumn(), is( token.length() ) );
    }

    private BaseNode parse(String input) {
        ParseTree tree = FEELParser.parse( input );
        ASTBuilderVisitor v = new ASTBuilderVisitor();
        BaseNode expr = v.visit( tree );
        return expr;
    }

}
