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
package org.kie.dmn.feel.parser.feel11;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.antlr.v4.runtime.tree.ParseTree;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.kie.dmn.feel.lang.Type;
import org.kie.dmn.feel.lang.ast.ASTNode;
import org.kie.dmn.feel.lang.ast.AtLiteralNode;
import org.kie.dmn.feel.lang.ast.BaseNode;
import org.kie.dmn.feel.lang.ast.BetweenNode;
import org.kie.dmn.feel.lang.ast.BooleanNode;
import org.kie.dmn.feel.lang.ast.ContextEntryNode;
import org.kie.dmn.feel.lang.ast.ContextNode;
import org.kie.dmn.feel.lang.ast.FilterExpressionNode;
import org.kie.dmn.feel.lang.ast.ForExpressionNode;
import org.kie.dmn.feel.lang.ast.FunctionDefNode;
import org.kie.dmn.feel.lang.ast.FunctionInvocationNode;
import org.kie.dmn.feel.lang.ast.IfExpressionNode;
import org.kie.dmn.feel.lang.ast.InNode;
import org.kie.dmn.feel.lang.ast.InfixOpNode;
import org.kie.dmn.feel.lang.ast.InfixOperator;
import org.kie.dmn.feel.lang.ast.InstanceOfNode;
import org.kie.dmn.feel.lang.ast.IterationContextNode;
import org.kie.dmn.feel.lang.ast.ListNode;
import org.kie.dmn.feel.lang.ast.NameDefNode;
import org.kie.dmn.feel.lang.ast.NameRefNode;
import org.kie.dmn.feel.lang.ast.NamedParameterNode;
import org.kie.dmn.feel.lang.ast.NullNode;
import org.kie.dmn.feel.lang.ast.NumberNode;
import org.kie.dmn.feel.lang.ast.PathExpressionNode;
import org.kie.dmn.feel.lang.ast.QualifiedNameNode;
import org.kie.dmn.feel.lang.ast.QuantifiedExpressionNode;
import org.kie.dmn.feel.lang.ast.RangeNode;
import org.kie.dmn.feel.lang.ast.SignedUnaryNode;
import org.kie.dmn.feel.lang.ast.StringNode;
import org.kie.dmn.feel.lang.ast.TypeNode;
import org.kie.dmn.feel.lang.impl.MapBackedType;
import org.kie.dmn.feel.lang.types.BuiltInType;
import org.kie.dmn.feel.util.Msg;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.dmn.feel.util.DynamicTypeUtils.entry;
import static org.kie.dmn.feel.util.DynamicTypeUtils.mapOf;

public class FEELParserTest {

    @Test
    void integerLiteral() {
        String inputExpression = "10";
        BaseNode number = parse( inputExpression );

        assertThat( number).isInstanceOf(NumberNode.class);
        assertThat( number.getResultType()).isEqualTo(BuiltInType.NUMBER);
        assertLocation( inputExpression, number );
    }

    @Test
    void negativeIntegerLiteral() {
        String inputExpression = "-10";
        BaseNode number = parse( inputExpression );

        assertThat( number).isInstanceOf(SignedUnaryNode.class);
        assertThat( number.getResultType()).isEqualTo(BuiltInType.NUMBER);
        assertLocation( inputExpression, number );

        SignedUnaryNode sun = (SignedUnaryNode) number;
        assertThat( sun.getSign()).isEqualTo(SignedUnaryNode.Sign.NEGATIVE);
        assertThat( sun.getExpression()).isInstanceOf(NumberNode.class);
        assertThat( sun.getExpression().getText()).isEqualTo("10");
    }

    @Test
    void positiveIntegerLiteral() {
        String inputExpression = "+10";
        BaseNode number = parse( inputExpression );

        assertThat( number).isInstanceOf(SignedUnaryNode.class);
        assertThat( number.getResultType()).isEqualTo(BuiltInType.NUMBER);
        assertLocation( inputExpression, number );

        SignedUnaryNode sun = (SignedUnaryNode) number;
        assertThat( sun.getSign()).isEqualTo(SignedUnaryNode.Sign.POSITIVE);
        assertThat( sun.getExpression()).isInstanceOf(NumberNode.class);
        assertThat( sun.getExpression().getText()).isEqualTo("10");
    }

    @Test
    void floatLiteral() {
        String inputExpression = "10.5";
        BaseNode number = parse( inputExpression );

        assertThat( number).isInstanceOf(NumberNode.class);
        assertThat( number.getResultType()).isEqualTo(BuiltInType.NUMBER);
        assertLocation( inputExpression, number );
    }

    @Test
    void negativeFloatLiteral() {
        String inputExpression = "-10.5";
        BaseNode number = parse( inputExpression );

        assertThat( number).isInstanceOf(SignedUnaryNode.class);
        assertThat( number.getResultType()).isEqualTo(BuiltInType.NUMBER);
        assertLocation( inputExpression, number );

        SignedUnaryNode sun = (SignedUnaryNode) number;
        assertThat( sun.getSign()).isEqualTo(SignedUnaryNode.Sign.NEGATIVE);
        assertThat( sun.getExpression()).isInstanceOf(NumberNode.class);
        assertThat( sun.getExpression().getText()).isEqualTo("10.5");
    }

    @Test
    void positiveFloatLiteral() {
        String inputExpression = "+10.5";
        BaseNode number = parse( inputExpression );

        assertThat( number).isInstanceOf(SignedUnaryNode.class);
        assertThat( number.getResultType()).isEqualTo(BuiltInType.NUMBER);
        assertLocation( inputExpression, number );

        SignedUnaryNode sun = (SignedUnaryNode) number;
        assertThat( sun.getSign()).isEqualTo(SignedUnaryNode.Sign.POSITIVE);
        assertThat( sun.getExpression()).isInstanceOf(NumberNode.class);
        assertThat( sun.getExpression().getText()).isEqualTo("10.5");
    }

    @Test
    void booleanTrueLiteral() {
        String inputExpression = "true";
        BaseNode bool = parse( inputExpression );

        assertThat( bool).isInstanceOf(BooleanNode.class);
        assertThat( bool.getResultType()).isEqualTo(BuiltInType.BOOLEAN);
        assertLocation( inputExpression, bool );
    }

    @Test
    void booleanFalseLiteral() {
        String inputExpression = "false";
        BaseNode bool = parse( inputExpression );

        assertThat( bool).isInstanceOf(BooleanNode.class);
        assertThat( bool.getResultType()).isEqualTo(BuiltInType.BOOLEAN);
        assertLocation( inputExpression, bool );
    }

    @Test
    void atLiteralDate() {
        String inputExpression = "@\"2016-07-29\"";
        BaseNode bool = parse(inputExpression);

        assertThat(bool).isInstanceOf(AtLiteralNode.class);
        assertThat(bool.getResultType()).isEqualTo(BuiltInType.DATE);
        assertLocation(inputExpression, bool);
    }

    @Test
    void atLiteralTime() {
        String inputExpression = "@\"23:59:00\"";
        BaseNode bool = parse(inputExpression);

        assertThat(bool).isInstanceOf(AtLiteralNode.class);
        assertThat(bool.getResultType()).isEqualTo(BuiltInType.TIME);
        assertLocation(inputExpression, bool);
    }

    @Test
    void atLiteralDateAndTime() {
        String inputExpression = "@\"2016-07-29T05:48:23\"";
        BaseNode bool = parse(inputExpression);

        assertThat(bool).isInstanceOf(AtLiteralNode.class);
        assertThat(bool.getResultType()).isEqualTo(BuiltInType.DATE_TIME);
        assertLocation(inputExpression, bool);
    }

    @Test
    void atLiteralDuration() {
        String inputExpression = "@\"P2Y2M\"";
        BaseNode bool = parse(inputExpression);

        assertThat(bool).isInstanceOf(AtLiteralNode.class);
        assertThat(bool.getResultType()).isEqualTo(BuiltInType.DURATION);
        assertLocation(inputExpression, bool);
    }

    @Test
    void nullLiteral() {
        String inputExpression = "null";
        BaseNode nullLit = parse( inputExpression );

        assertThat( nullLit).isInstanceOf(NullNode.class);
        assertThat( nullLit.getResultType()).isEqualTo(BuiltInType.UNKNOWN);
        assertLocation( inputExpression, nullLit );
    }

    @Test
    void stringLiteral() {
        String inputExpression = "\"some string\"";
        BaseNode stringLit = parse( inputExpression );

        assertThat( stringLit).isInstanceOf(StringNode.class);
        assertThat( stringLit.getResultType()).isEqualTo(BuiltInType.STRING);
        assertLocation( inputExpression, stringLit );
        assertThat(stringLit.getText()).isEqualTo(inputExpression);
    }

    @Test
    void nameReference() {
        String inputExpression = "someSimpleName";
        BaseNode nameRef = parse( inputExpression, mapOf( entry("someSimpleName", BuiltInType.STRING)));

        assertThat( nameRef).isInstanceOf(NameRefNode.class);
        assertThat( nameRef.getResultType()).isEqualTo(BuiltInType.STRING);
        assertLocation( inputExpression, nameRef );
    }

    @Test
    void qualifiedName() {
        String inputExpression = "My Person.Full Name";
        MapBackedType personType = new MapBackedType("Person", mapOf( entry("Full Name", BuiltInType.STRING), entry("Age", BuiltInType.NUMBER)));
        BaseNode qualRef = parse( inputExpression, mapOf( entry("My Person", personType)));

        assertThat( qualRef).isInstanceOf(QualifiedNameNode.class);
        assertThat( qualRef.getResultType()).isEqualTo(BuiltInType.STRING);

        List<NameRefNode> parts = ((QualifiedNameNode) qualRef).getParts();
        // `My Person` ...
        assertThat( parts.get(0)).isInstanceOf(NameRefNode.class);
        assertThat( parts.get(0).getResultType()).isEqualTo(personType);
        // ... `.Full Name`
        assertThat( parts.get(1)).isInstanceOf(NameRefNode.class);
        assertThat( parts.get(1).getResultType()).isEqualTo(BuiltInType.STRING);

        assertLocation( inputExpression, qualRef );
    }

    @Test
    void parensWithLiteral() {
        String inputExpression = "(10.5 )";
        BaseNode number = parse( inputExpression );

        assertThat( number).isInstanceOf(NumberNode.class);
        assertThat( number.getResultType()).isEqualTo(BuiltInType.NUMBER);
        assertThat( number.getText()).isEqualTo("10.5");
    }

    @Test
    void logicalNegation() {
        String inputExpression = "not ( true )";
        BaseNode neg = parse( inputExpression );

        assertThat( neg).isInstanceOf(FunctionInvocationNode.class);
        assertThat( neg.getResultType()).isEqualTo(BuiltInType.UNKNOWN);
        assertThat( neg.getText()).isEqualTo( "not ( true )");

        FunctionInvocationNode not = (FunctionInvocationNode) neg;
        assertThat( not.getParams().getElements().get( 0 )).isInstanceOf(BooleanNode.class);
        assertThat( not.getParams().getElements().get( 0 ).getResultType()).isEqualTo(BuiltInType.BOOLEAN);
        assertThat( not.getParams().getElements().get( 0 ).getText()).isEqualTo("true");
    }

    @Test
    void multiplication() {
        String inputExpression = "10 * x";
        BaseNode infix = parse( inputExpression, mapOf(entry("x", BuiltInType.NUMBER)) );

        assertThat( infix).isInstanceOf(InfixOpNode.class);
        assertThat( infix.getResultType()).isEqualTo(BuiltInType.NUMBER);
        assertThat( infix.getText()).isEqualTo(inputExpression);

        InfixOpNode mult = (InfixOpNode) infix;
        assertThat( mult.getLeft()).isInstanceOf(NumberNode.class);
        assertThat( mult.getLeft().getText()).isEqualTo("10");

        assertThat( mult.getOperator()).isEqualTo(InfixOperator.MULT);

        assertThat( mult.getRight()).isInstanceOf(NameRefNode.class);
        assertThat( mult.getRight().getText()).isEqualTo("x");
    }

    @Test
    void division() {
        String inputExpression = "y / 5 * ( x )";
        BaseNode infix = parse( inputExpression, mapOf(entry("x", BuiltInType.NUMBER), entry("y", BuiltInType.NUMBER)) );

        assertThat( infix).isInstanceOf(InfixOpNode.class);
        assertThat( infix.getResultType()).isEqualTo(BuiltInType.NUMBER);
        assertThat( infix.getText()).isEqualTo(inputExpression);

        InfixOpNode mult = (InfixOpNode) infix;
        assertThat( mult.getLeft()).isInstanceOf(InfixOpNode.class);
        assertThat( mult.getLeft().getText()).isEqualTo( "y / 5");

        InfixOpNode div = (InfixOpNode) mult.getLeft();
        assertThat( div.getLeft()).isInstanceOf(NameRefNode.class);
        assertThat( div.getLeft().getText()).isEqualTo("y");

        assertThat( div.getOperator()).isEqualTo(InfixOperator.DIV);

        assertThat( div.getRight()).isInstanceOf(NumberNode.class);
        assertThat( div.getRight().getText()).isEqualTo("5");

        assertThat( mult.getOperator()).isEqualTo(InfixOperator.MULT);

        assertThat( mult.getRight()).isInstanceOf(NameRefNode.class);
        assertThat( mult.getRight().getText()).isEqualTo("x");
    }

    @Test
    void power1() {
        String inputExpression = "y * 5 ** 3";
        BaseNode infix = parse( inputExpression, mapOf(entry("y", BuiltInType.NUMBER)) );

        assertThat( infix).isInstanceOf(InfixOpNode.class);
        assertThat( infix.getResultType()).isEqualTo(BuiltInType.NUMBER);
        assertThat( infix.getText()).isEqualTo(inputExpression);

        InfixOpNode mult = (InfixOpNode) infix;
        assertThat( mult.getLeft()).isInstanceOf(NameRefNode.class);
        assertThat( mult.getLeft().getText()).isEqualTo("y");

        assertThat( mult.getOperator()).isEqualTo(InfixOperator.MULT);

        assertThat( mult.getRight()).isInstanceOf(InfixOpNode.class);
        assertThat( mult.getRight().getText()).isEqualTo( "5 ** 3");

        InfixOpNode exp = (InfixOpNode) mult.getRight();
        assertThat( exp.getLeft()).isInstanceOf(NumberNode.class);
        assertThat( exp.getLeft().getText()).isEqualTo("5");

        assertThat( exp.getOperator()).isEqualTo(InfixOperator.POW);

        assertThat( exp.getRight()).isInstanceOf(NumberNode.class);
        assertThat( exp.getRight().getText()).isEqualTo("3");
    }

    @Test
    void power2() {
        String inputExpression = "(y * 5) ** 3";
        BaseNode infix = parse( inputExpression, mapOf(entry("y", BuiltInType.NUMBER)) );

        assertThat( infix).isInstanceOf(InfixOpNode.class);
        assertThat( infix.getResultType()).isEqualTo(BuiltInType.NUMBER);
        assertThat( infix.getText()).isEqualTo(inputExpression);

        InfixOpNode exp = (InfixOpNode) infix;
        assertThat( exp.getLeft()).isInstanceOf(InfixOpNode.class);
        assertThat( exp.getLeft().getText()).isEqualTo( "y * 5");

        assertThat( exp.getOperator()).isEqualTo(InfixOperator.POW);

        assertThat( exp.getRight()).isInstanceOf(NumberNode.class);
        assertThat( exp.getRight().getText()).isEqualTo("3");

        InfixOpNode mult = (InfixOpNode) exp.getLeft();
        assertThat( mult.getLeft()).isInstanceOf(NameRefNode.class);
        assertThat( mult.getLeft().getText()).isEqualTo("y");

        assertThat( mult.getOperator()).isEqualTo(InfixOperator.MULT);

        assertThat( mult.getRight()).isInstanceOf(NumberNode.class);
        assertThat( mult.getRight().getText()).isEqualTo("5");
    }

    @Test
    void power3() {
        String inputExpression = "y ** 5 * 3";
        BaseNode infix = parse( inputExpression, mapOf(entry("y", BuiltInType.NUMBER)) );

        assertThat( infix).isInstanceOf(InfixOpNode.class);
        assertThat( infix.getResultType()).isEqualTo(BuiltInType.NUMBER);
        assertThat( infix.getText()).isEqualTo(inputExpression);

        InfixOpNode mult = (InfixOpNode) infix;
        assertThat( mult.getLeft()).isInstanceOf(InfixOpNode.class);
        assertThat( mult.getLeft().getText()).isEqualTo( "y ** 5");

        assertThat( mult.getOperator()).isEqualTo(InfixOperator.MULT);

        assertThat( mult.getRight()).isInstanceOf(NumberNode.class);
        assertThat( mult.getRight().getText()).isEqualTo("3");

        InfixOpNode exp = (InfixOpNode) mult.getLeft();
        assertThat( exp.getLeft()).isInstanceOf(NameRefNode.class);
        assertThat( exp.getLeft().getText()).isEqualTo("y");

        assertThat( exp.getOperator()).isEqualTo(InfixOperator.POW);

        assertThat( exp.getRight()).isInstanceOf(NumberNode.class);
        assertThat( exp.getRight().getText()).isEqualTo("5");
    }

    @Test
    void power4() {
        String inputExpression = "y ** ( 5 * 3 )";
        BaseNode infix = parse( inputExpression, mapOf(entry("y", BuiltInType.NUMBER)) );

        assertThat( infix).isInstanceOf(InfixOpNode.class);
        assertThat( infix.getResultType()).isEqualTo(BuiltInType.NUMBER);
        assertThat( infix.getText()).isEqualTo(inputExpression);

        InfixOpNode exp = (InfixOpNode) infix;
        assertThat( exp.getLeft()).isInstanceOf(NameRefNode.class);
        assertThat( exp.getLeft().getText()).isEqualTo("y");

        assertThat( exp.getOperator()).isEqualTo(InfixOperator.POW);

        assertThat( exp.getRight()).isInstanceOf(InfixOpNode.class);
        assertThat( exp.getRight().getText()).isEqualTo( "5 * 3");

        InfixOpNode mult = (InfixOpNode) exp.getRight();
        assertThat( mult.getLeft()).isInstanceOf(NumberNode.class);
        assertThat( mult.getLeft().getText()).isEqualTo("5");

        assertThat( mult.getOperator()).isEqualTo(InfixOperator.MULT);

        assertThat( mult.getRight()).isInstanceOf(NumberNode.class);
        assertThat( mult.getRight().getText()).isEqualTo("3");
    }

    @Test
    void add1() {
        String inputExpression = "y + 5 * 3";
        BaseNode infix = parse( inputExpression, mapOf(entry("y", BuiltInType.NUMBER)) );

        assertThat( infix).isInstanceOf(InfixOpNode.class);
        assertThat( infix.getResultType()).isEqualTo(BuiltInType.NUMBER);
        assertThat( infix.getText()).isEqualTo(inputExpression);

        InfixOpNode add = (InfixOpNode) infix;
        assertThat( add.getLeft()).isInstanceOf(NameRefNode.class);
        assertThat( add.getLeft().getText()).isEqualTo("y");

        assertThat( add.getOperator()).isEqualTo(InfixOperator.ADD);

        assertThat( add.getRight()).isInstanceOf(InfixOpNode.class);
        assertThat( add.getRight().getText()).isEqualTo( "5 * 3");

        InfixOpNode mult = (InfixOpNode) add.getRight();
        assertThat( mult.getLeft()).isInstanceOf(NumberNode.class);
        assertThat( mult.getLeft().getText()).isEqualTo("5");

        assertThat( mult.getOperator()).isEqualTo(InfixOperator.MULT);

        assertThat( mult.getRight()).isInstanceOf(NumberNode.class);
        assertThat( mult.getRight().getText()).isEqualTo("3");
    }

    @Test
    void sub1() {
        String inputExpression = "(y - 5) ** 3";
        BaseNode infix = parse( inputExpression, mapOf(entry("y", BuiltInType.NUMBER)) );

        assertThat( infix).isInstanceOf(InfixOpNode.class);
        assertThat( infix.getResultType()).isEqualTo(BuiltInType.NUMBER);
        assertThat( infix.getText()).isEqualTo(inputExpression);

        InfixOpNode sub = (InfixOpNode) infix;
        assertThat( sub.getLeft()).isInstanceOf(InfixOpNode.class);
        assertThat( sub.getLeft().getText()).isEqualTo( "y - 5");

        assertThat( sub.getOperator()).isEqualTo(InfixOperator.POW);

        assertThat( sub.getRight()).isInstanceOf(NumberNode.class);
        assertThat( sub.getRight().getText()).isEqualTo("3");

        InfixOpNode mult = (InfixOpNode) sub.getLeft();
        assertThat( mult.getLeft()).isInstanceOf(NameRefNode.class);
        assertThat( mult.getLeft().getText()).isEqualTo("y");

        assertThat( mult.getOperator()).isEqualTo(InfixOperator.SUB);

        assertThat( mult.getRight()).isInstanceOf(NumberNode.class);
        assertThat( mult.getRight().getText()).isEqualTo("5");
    }

    @Test
    void between() {
        String inputExpression = "x between 10+y and 3**z";
        BaseNode between = parse( inputExpression );

        assertThat( between).isInstanceOf(BetweenNode.class);
        assertThat( between.getResultType()).isEqualTo(BuiltInType.BOOLEAN);
        assertThat( between.getText()).isEqualTo(inputExpression);

        BetweenNode btw = (BetweenNode) between;
        assertThat( btw.getValue()).isInstanceOf(NameRefNode.class);
        assertThat( btw.getValue().getText()).isEqualTo("x");

        assertThat( btw.getStart()).isInstanceOf(InfixOpNode.class);
        assertThat( btw.getStart().getText()).isEqualTo( "10+y");

        assertThat( btw.getEnd()).isInstanceOf(InfixOpNode.class);
        assertThat( btw.getEnd().getText()).isEqualTo( "3**z");
    }

    @Test
    void inValueList() {
        // TODO review this test might be wrong as list is not homogeneous 
        String inputExpression = "x / 4 in ( 10+y, true, 80, someVar )";
        BaseNode inNode = parse( inputExpression );

        assertThat( inNode).isInstanceOf(InNode.class);
        assertThat( inNode.getResultType()).isEqualTo(BuiltInType.BOOLEAN);
        assertThat( inNode.getText()).isEqualTo(inputExpression);

        InNode in = (InNode) inNode;
        assertThat( in.getValue()).isInstanceOf(InfixOpNode.class);
        assertThat( in.getValue().getText()).isEqualTo( "x / 4");

        assertThat( in.getExprs()).isInstanceOf(ListNode.class);
        assertThat( in.getExprs().getText()).isEqualTo( "10+y, true, 80, someVar");

        ListNode list = (ListNode) in.getExprs();
        assertThat( list.getElements().get( 0 )).isInstanceOf(InfixOpNode.class);
        assertThat( list.getElements().get( 1 )).isInstanceOf(BooleanNode.class);
        assertThat( list.getElements().get( 2 )).isInstanceOf(NumberNode.class);
        assertThat( list.getElements().get( 3 )).isInstanceOf(NameRefNode.class);
    }

    @Test
    void inUnaryTestList() {
        String inputExpression = "x ** y in ( <=1000, >t, null, (2000..z[, ]z..2000], [(10+5)..(a*b)) )";
        BaseNode inNode = parse( inputExpression );

        assertThat( inNode).isInstanceOf(InNode.class);
        assertThat( inNode.getResultType()).isEqualTo(BuiltInType.BOOLEAN);
        assertThat( inNode.getText()).isEqualTo(inputExpression);

        InNode in = (InNode) inNode;
        assertThat( in.getValue()).isInstanceOf(InfixOpNode.class);
        assertThat( in.getValue().getText()).isEqualTo( "x ** y");

        assertThat( in.getExprs()).isInstanceOf(ListNode.class);
        assertThat( in.getExprs().getText()).isEqualTo( "<=1000, >t, null, (2000..z[, ]z..2000], [(10+5)..(a*b))");

        ListNode list = (ListNode) in.getExprs();
        assertThat( list.getElements().get( 0 )).isInstanceOf(RangeNode.class);
        assertThat( list.getElements().get( 0 ).getText()).isEqualTo( "<=1000");

        assertThat( list.getElements().get( 1 )).isInstanceOf(RangeNode.class);
        assertThat( list.getElements().get( 1 ).getText()).isEqualTo( ">t");

        assertThat( list.getElements().get( 2 )).isInstanceOf(NullNode.class);
        assertThat( list.getElements().get( 2 ).getText()).isEqualTo("null");

        assertThat( list.getElements().get( 3 )).isInstanceOf(RangeNode.class);
        RangeNode interval = (RangeNode) list.getElements().get( 3 );
        assertThat( interval.getText()).isEqualTo( "(2000..z[");
        assertThat( interval.getLowerBound()).isEqualTo(RangeNode.IntervalBoundary.OPEN);
        assertThat( interval.getUpperBound()).isEqualTo(RangeNode.IntervalBoundary.OPEN);
        assertThat( interval.getStart()).isInstanceOf(NumberNode.class);
        assertThat( interval.getEnd()).isInstanceOf(NameRefNode.class);

        assertThat( list.getElements().get( 4 )).isInstanceOf(RangeNode.class);
        interval = (RangeNode) list.getElements().get( 4 );
        assertThat( interval.getText()).isEqualTo( "]z..2000]");
        assertThat( interval.getLowerBound()).isEqualTo(RangeNode.IntervalBoundary.OPEN);
        assertThat( interval.getUpperBound()).isEqualTo(RangeNode.IntervalBoundary.CLOSED);
        assertThat( interval.getStart()).isInstanceOf(NameRefNode.class);
        assertThat( interval.getEnd()).isInstanceOf(NumberNode.class);

        assertThat( list.getElements().get( 5 )).isInstanceOf(RangeNode.class);
        interval = (RangeNode) list.getElements().get( 5 );
        assertThat( interval.getText()).isEqualTo( "[(10+5)..(a*b))");
        assertThat( interval.getLowerBound()).isEqualTo(RangeNode.IntervalBoundary.CLOSED);
        assertThat( interval.getUpperBound()).isEqualTo(RangeNode.IntervalBoundary.OPEN);
        assertThat( interval.getStart()).isInstanceOf(InfixOpNode.class);
        assertThat( interval.getEnd()).isInstanceOf(InfixOpNode.class);

    }

    @Test
    void inUnaryTest() {
        String inputExpression = "x - y in [(10+5)..(a*b))";
        BaseNode inNode = parse( inputExpression );

        assertThat( inNode).isInstanceOf(InNode.class);
        assertThat( inNode.getResultType()).isEqualTo(BuiltInType.BOOLEAN);
        assertThat( inNode.getText()).isEqualTo(inputExpression);

        InNode in = (InNode) inNode;
        assertThat( in.getValue()).isInstanceOf(InfixOpNode.class);
        assertThat( in.getValue().getText()).isEqualTo( "x - y");

        assertThat( in.getExprs()).isInstanceOf(RangeNode.class);
        assertThat( in.getExprs().getText()).isEqualTo( "[(10+5)..(a*b))");
    }

    @Test
    void inUnaryTestStrings() {
        final String inputExpression = "name in [\"A\"..\"Z...\")";
        final BaseNode inNode = parse( inputExpression );

        assertThat( inNode).isInstanceOf(InNode.class);
        assertThat( inNode.getResultType()).isEqualTo(BuiltInType.BOOLEAN);
        assertThat( inNode.getText()).isEqualTo(inputExpression);

        final InNode in = (InNode) inNode;
        assertThat( in.getExprs()).isInstanceOf(RangeNode.class);
        final RangeNode range = (RangeNode) in.getExprs();
        assertThat( range.getStart().getText()).isEqualTo( "\"A\"");
        assertThat( range.getEnd().getText()).isEqualTo( "\"Z...\"");
    }

    @Test
    void comparisonInFixOp() {
        String inputExpression = "foo >= bar * 10";
        BaseNode infix = parse( inputExpression );

        assertThat( infix).isInstanceOf(InfixOpNode.class);
        assertThat( infix.getResultType()).isEqualTo(BuiltInType.BOOLEAN);
        assertThat( infix.getText()).isEqualTo(inputExpression);

        InfixOpNode in = (InfixOpNode) infix;
        assertThat( in.getLeft()).isInstanceOf(NameRefNode.class);
        assertThat( in.getLeft().getText()).isEqualTo("foo");

        assertThat( in.getRight()).isInstanceOf(InfixOpNode.class);
        assertThat( in.getRight().getText()).isEqualTo( "bar * 10");
    }

    @Test
    void conditionalLogicalOp() {
        String inputExpression = "foo < 10 and bar = \"x\" or baz";
        BaseNode infix = parse( inputExpression );

        assertThat( infix).isInstanceOf(InfixOpNode.class);
        assertThat( infix.getResultType()).isEqualTo(BuiltInType.BOOLEAN);
        assertThat( infix.getText()).isEqualTo(inputExpression);

        InfixOpNode or = (InfixOpNode) infix;
        assertThat( or.getLeft()).isInstanceOf(InfixOpNode.class);
        assertThat( or.getLeft().getText()).isEqualTo( "foo < 10 and bar = \"x\"");

        assertThat( or.getOperator()).isEqualTo(InfixOperator.OR);

        assertThat( or.getRight()).isInstanceOf(NameRefNode.class);
        assertThat( or.getRight().getText()).isEqualTo("baz");

        InfixOpNode and = (InfixOpNode) or.getLeft();
        assertThat( and.getLeft()).isInstanceOf(InfixOpNode.class);
        assertThat( and.getLeft().getText()).isEqualTo( "foo < 10");

        assertThat( and.getOperator()).isEqualTo(InfixOperator.AND);

        assertThat( and.getRight()).isInstanceOf(InfixOpNode.class);
        assertThat( and.getRight().getText()).isEqualTo( "bar = \"x\"");
    }

    @Test
    void emptyList() {
        String inputExpression = "[]";
        BaseNode list = parse( inputExpression );

        assertThat( list).isInstanceOf(ListNode.class);
        assertThat( list.getResultType()).isEqualTo(BuiltInType.LIST);
        assertThat( list.getText()).isEqualTo(inputExpression);

        ListNode ln = (ListNode) list;
        assertThat( ln.getElements()).isEmpty();
    }

    @Test
    void expressionList() {
        // TODO review this test is potentially wrong as the list is not homogeneous
        String inputExpression = "[ 10, foo * bar, true ]";
        BaseNode list = parse( inputExpression );

        assertThat( list).isInstanceOf(ListNode.class);
        assertThat( list.getResultType()).isEqualTo(BuiltInType.LIST);
        assertThat( list.getText()).isEqualTo( "10, foo * bar, true");

        ListNode ln = (ListNode) list;
        assertThat( ln.getElements()).hasSize(3);
        assertThat( ln.getElements().get( 0 )).isInstanceOf(NumberNode.class);
        assertThat( ln.getElements().get( 1 )).isInstanceOf(InfixOpNode.class);
        assertThat( ln.getElements().get( 2 )).isInstanceOf(BooleanNode.class);
    }

    @Test
    void emptyContext() {
        String inputExpression = "{}";
        BaseNode context = parse( inputExpression );

        assertThat( context).isInstanceOf(ContextNode.class);
        assertThat( context.getText()).isEqualTo(inputExpression);

        ContextNode ctx = (ContextNode) context;
        assertThat( ctx.getEntries()).isEmpty();
    }

    @Test
    void contextWithMultipleEntries() {
        String inputExpression = "{ \"a string key\" : 10,"
                       + " a non-string key : foo+bar,"
                       + " a key.with + /' odd chars : [10..50] }";
        BaseNode ctxbase = parse( inputExpression, mapOf(entry("foo", BuiltInType.NUMBER), entry("bar", BuiltInType.NUMBER)));

        assertThat( ctxbase).isInstanceOf(ContextNode.class);
        assertThat( ctxbase.getText()).isEqualTo(inputExpression);

        ContextNode ctx = (ContextNode) ctxbase;
        assertThat( ctx.getEntries()).hasSize(3);

        ContextEntryNode entry = ctx.getEntries().get( 0 );
        assertThat(entry.getName()).isInstanceOf(StringNode.class);
        StringNode nameNode = (StringNode) entry.getName();
        assertThat(nameNode.getText()).isNotNull();
        assertThat(nameNode.getText()).isEqualTo("\"a string key\""); // Reference String literal test, BaseNode#getText() return the FEEL equivalent expression, in this case quoted.
        assertThat( entry.getValue()).isInstanceOf(NumberNode.class);
        assertThat( entry.getResultType()).isEqualTo(BuiltInType.NUMBER);
        assertThat( entry.getValue().getText()).isEqualTo("10");

        entry = ctx.getEntries().get( 1 );
        assertThat( entry.getName()).isInstanceOf(NameDefNode.class);
        NameDefNode name = (NameDefNode) entry.getName();
        assertThat( name.getParts()).isNotNull();
        assertThat( name.getParts()).hasSize(5);
        assertThat( entry.getName().getText()).isEqualTo("a non-string key");
        assertThat( entry.getValue()).isInstanceOf(InfixOpNode.class);
        assertThat( entry.getResultType()).isEqualTo(BuiltInType.NUMBER);
        assertThat( entry.getValue().getText()).isEqualTo( "foo+bar");

        entry = ctx.getEntries().get( 2 );
        assertThat( entry.getName()).isInstanceOf(NameDefNode.class);
        name = (NameDefNode) entry.getName();
        assertThat( name.getParts()).isNotNull();
        assertThat( name.getParts()).hasSize(9);
        assertThat( entry.getName().getText()).isEqualTo("a key.with + /' odd chars");
        assertThat( entry.getValue()).isInstanceOf(RangeNode.class);
        assertThat( entry.getResultType()).isEqualTo(BuiltInType.RANGE);
        assertThat( entry.getValue().getText()).isEqualTo( "[10..50]");
    }

    @Test
    void variableWithInKeyword() {
        String inputExpression = "{ a variable with in keyword : 10, "
                + " another variable : a variable with in keyword + 20, "
                + " another in variable : an external in variable / 2 }";
        BaseNode ctxbase = parse( inputExpression, mapOf(entry("an external in variable", BuiltInType.NUMBER)) );

        assertThat( ctxbase).isInstanceOf(ContextNode.class);
        assertThat( ctxbase.getText()).isEqualTo(inputExpression);

        ContextNode ctx = (ContextNode) ctxbase;
        assertThat( ctx.getEntries()).hasSize(3);

        ContextEntryNode entry = ctx.getEntries().get( 0 );
        assertThat( entry.getName()).isInstanceOf(NameDefNode.class);
        NameDefNode name = (NameDefNode) entry.getName();
        assertThat( name.getParts()).isNotNull();
        assertThat( name.getParts()).hasSize(5);
        assertThat( entry.getName().getText()).isEqualTo("a variable with in keyword");
        assertThat( entry.getValue()).isInstanceOf(NumberNode.class);
        assertThat( entry.getResultType()).isEqualTo(BuiltInType.NUMBER);
        assertThat( entry.getValue().getText()).isEqualTo("10");

        entry = ctx.getEntries().get( 1 );
        assertThat( entry.getName()).isInstanceOf(NameDefNode.class);
        name = (NameDefNode) entry.getName();
        assertThat( name.getParts()).isNotNull();
        assertThat( name.getParts()).hasSize(2);
        assertThat( entry.getName().getText()).isEqualTo("another variable");
        assertThat( entry.getValue()).isInstanceOf(InfixOpNode.class);
        assertThat( entry.getResultType()).isEqualTo(BuiltInType.NUMBER);
        assertThat( entry.getValue().getText()).isEqualTo( "a variable with in keyword + 20");

        entry = ctx.getEntries().get( 2 );
        assertThat( entry.getName()).isInstanceOf(NameDefNode.class);
        name = (NameDefNode) entry.getName();
        assertThat( name.getParts()).isNotNull();
        assertThat( name.getParts()).hasSize(3);
        assertThat( entry.getName().getText()).isEqualTo("another in variable");
        assertThat( entry.getValue()).isInstanceOf(InfixOpNode.class);
        assertThat( entry.getResultType()).isEqualTo(BuiltInType.NUMBER);
        assertThat( entry.getValue().getText()).isEqualTo( "an external in variable / 2");
    }

    @Test
    void nestedContexts() {
        String inputExpression = "{ a value : 10,"
                       + " an applicant : { "
                       + "    first name : \"Edson\", "
                       + "    last + name : \"Tirelli\", "
                       + "    full name : first name + last + name, "
                       + "    address : {"
                       + "        street : \"55 broadway st\","
                       + "        city : \"New York\" "
                       + "    }, "
                       + "    xxx: last + name"
                       + " } "
                       + "}";
        BaseNode ctxbase = parse( inputExpression );

        assertThat( ctxbase).isInstanceOf(ContextNode.class);
        assertThat( ctxbase.getText()).isEqualTo(inputExpression);

        ContextNode ctx = (ContextNode) ctxbase;
        assertThat( ctx.getEntries()).hasSize(2);

        ContextEntryNode entry = ctx.getEntries().get( 0 );
        assertThat( entry.getName()).isInstanceOf(NameDefNode.class);
        NameDefNode name = (NameDefNode) entry.getName();
        assertThat( name.getText()).isEqualTo("a value");
        assertThat( entry.getValue()).isInstanceOf(NumberNode.class);
        assertThat( entry.getResultType()).isEqualTo(BuiltInType.NUMBER);
        assertThat( entry.getValue().getText()).isEqualTo("10");

        entry = ctx.getEntries().get( 1 );
        assertThat( entry.getName()).isInstanceOf(NameDefNode.class);
        name = (NameDefNode) entry.getName();
        assertThat( name.getText()).isEqualTo( "an applicant");
        assertThat( entry.getValue()).isInstanceOf(ContextNode.class);

        ContextNode applicant = (ContextNode) entry.getValue();
        assertThat( applicant.getEntries()).hasSize(5);
        assertThat( applicant.getEntries().get( 0 ).getName().getText()).isEqualTo("first name");
        assertThat( applicant.getEntries().get( 0 ).getResultType()).isEqualTo(BuiltInType.STRING);
        assertThat( applicant.getEntries().get( 1 ).getName().getText()).isEqualTo("last + name");
        assertThat( applicant.getEntries().get( 1 ).getResultType()).isEqualTo(BuiltInType.STRING);
        assertThat( applicant.getEntries().get( 2 ).getName().getText()).isEqualTo("full name");
        assertThat( applicant.getEntries().get( 2 ).getResultType()).isEqualTo(BuiltInType.STRING);
        assertThat( applicant.getEntries().get( 3 ).getName().getText()).isEqualTo("address");
        assertThat( applicant.getEntries().get( 3 ).getValue()).isInstanceOf(ContextNode.class);

        ContextNode address = (ContextNode) applicant.getEntries().get( 3 ).getValue();
        assertThat( address.getEntries()).hasSize(2);
        assertThat( address.getEntries().get( 0 ).getName().getText()).isEqualTo("street");
        assertThat( address.getEntries().get( 0 ).getResultType()).isEqualTo(BuiltInType.STRING);
        assertThat( address.getEntries().get( 1 ).getName().getText()).isEqualTo("city");
        assertThat( address.getEntries().get( 0 ).getResultType()).isEqualTo(BuiltInType.STRING);
    }

    @Test
    void nestedContexts2() {
        String inputExpression = "{ an applicant : { "
                                 + "    home address : {"
                                 + "        street name: \"broadway st\","
                                 + "        city : \"New York\" "
                                 + "    } "
                                 + " },\n "
                                 + " street : an applicant.home address.street name \n"
                                 + "}";
        BaseNode ctxbase = parse( inputExpression );

        assertThat( ctxbase).isInstanceOf(ContextNode.class);
        assertThat( ctxbase.getText()).isEqualTo(inputExpression);

        ContextNode ctx = (ContextNode) ctxbase;
        assertThat( ctx.getEntries()).hasSize(2);

        ContextEntryNode entry = ctx.getEntries().get( 1 );
        assertThat( entry.getName()).isInstanceOf(NameDefNode.class);
        assertThat( entry.getResultType()).isEqualTo(BuiltInType.STRING);
        NameDefNode name = (NameDefNode) entry.getName();
        assertThat( name.getText()).isEqualTo("street");
        assertThat( entry.getValue()).isInstanceOf(QualifiedNameNode.class);
        QualifiedNameNode qnn = (QualifiedNameNode) entry.getValue();
        assertThat( qnn.getParts().get( 0 ).getText()).isEqualTo("an applicant");
        assertThat( qnn.getParts().get( 1 ).getText()).isEqualTo("home address");
        assertThat( qnn.getParts().get( 2 ).getText()).isEqualTo("street name");
    }

    @Test
    void functionDefinition() {
        String inputExpression = "{ is minor : function( person's age ) person's age < 21 }";
        BaseNode ctxbase = parse( inputExpression );

        assertThat( ctxbase).isInstanceOf(ContextNode.class);
        assertThat( ctxbase.getText()).isEqualTo(inputExpression);

        ContextNode ctx = (ContextNode) ctxbase;
        assertThat( ctx.getEntries()).hasSize(1);

        ContextEntryNode entry = ctx.getEntries().get( 0 );
        assertThat( entry.getName()).isInstanceOf(NameDefNode.class);
        NameDefNode name = (NameDefNode) entry.getName();
        assertThat( name.getText()).isEqualTo("is minor");
        assertThat( entry.getValue()).isInstanceOf(FunctionDefNode.class);
        assertThat( entry.getValue().getText()).isEqualTo("function( person's age ) person's age < 21");

        FunctionDefNode isMinorFunc = (FunctionDefNode) entry.getValue();
        assertThat( isMinorFunc.getFormalParameters()).hasSize(1);
        assertThat( isMinorFunc.getFormalParameters().get( 0 ).getText()).isEqualTo( "person's age");
        assertThat( isMinorFunc.isExternal()).isEqualTo(false);
        assertThat( isMinorFunc.getBody()).isInstanceOf(InfixOpNode.class);
    }

    @Test
    void externalFunctionDefinition() {
        String inputExpression = "{ trigonometric cosine : function( angle ) external {"
                       + "    java : {"
                       + "        class : \"java.lang.Math\","
                       + "        method signature : \"cos(double)\""
                       + "    }"
                       + "}}";
        BaseNode ctxbase = parse( inputExpression );

        assertThat( ctxbase).isInstanceOf(ContextNode.class);
        assertThat( ctxbase.getText()).isEqualTo(inputExpression);

        ContextNode ctx = (ContextNode) ctxbase;
        assertThat( ctx.getEntries()).hasSize(1);

        ContextEntryNode entry = ctx.getEntries().get( 0 );
        assertThat( entry.getName()).isInstanceOf(NameDefNode.class);
        NameDefNode name = (NameDefNode) entry.getName();
        assertThat( name.getText()).isEqualTo("trigonometric cosine");
        assertThat( entry.getValue()).isInstanceOf(FunctionDefNode.class);
        assertThat( entry.getValue().getText()).isEqualTo("function( angle ) external {"
                                                 + "    java : {"
                                                 + "        class : \"java.lang.Math\","
                                                 + "        method signature : \"cos(double)\""
                                                 + "    }"
                                                 + "}");

        FunctionDefNode cos = (FunctionDefNode) entry.getValue();
        assertThat( cos.getFormalParameters()).hasSize(1);
        assertThat( cos.getFormalParameters().get( 0 ).getText()).isEqualTo("angle");
        assertThat( cos.isExternal()).isEqualTo(true);
        assertThat( cos.getBody()).isInstanceOf(ContextNode.class);

        ContextNode body = (ContextNode) cos.getBody();
        assertThat( body.getEntries()).hasSize(1);
        ContextEntryNode java = body.getEntries().get( 0 );
        assertThat( java.getName().getText()).isEqualTo("java");
        assertThat( java.getValue()).isInstanceOf(ContextNode.class);

        ContextNode def = (ContextNode) java.getValue();
        assertThat( def.getEntries()).hasSize(2);
        assertThat( def.getEntries().get( 0 ).getName().getText()).isEqualTo("class");
        assertThat( def.getEntries().get( 0 ).getValue()).isInstanceOf(StringNode.class);
        assertThat( def.getEntries().get( 0 ).getValue().getText()).isEqualTo( "\"java.lang.Math\"");
        assertThat( def.getEntries().get( 1 ).getName().getText()).isEqualTo( "method signature");
        assertThat( def.getEntries().get( 1 ).getValue()).isInstanceOf(StringNode.class);
        assertThat( def.getEntries().get( 1 ).getValue().getText()).isEqualTo( "\"cos(double)\"");
    }

    @Test
    void forExpression() {
        String inputExpression = "for item in order.items return item.price * item.quantity";
        BaseNode forbase = parse( inputExpression );

        assertThat( forbase).isInstanceOf(ForExpressionNode.class);
        assertThat( forbase.getText()).isEqualTo(inputExpression);
        assertThat( forbase.getResultType()).isEqualTo(BuiltInType.LIST);

        ForExpressionNode forExpr = (ForExpressionNode) forbase;
        assertThat( forExpr.getIterationContexts()).hasSize(1);
        assertThat( forExpr.getExpression()).isInstanceOf(InfixOpNode.class);
        assertThat( forExpr.getExpression().getText()).isEqualTo( "item.price * item.quantity");

        IterationContextNode ic = forExpr.getIterationContexts().get( 0 );
        assertThat( ic.getName().getText()).isEqualTo("item");
        assertThat( ic.getExpression()).isInstanceOf(QualifiedNameNode.class);
        assertThat( ic.getExpression().getText()).isEqualTo("order.items");
    }

    @Test
    void ifExpression() {
        String inputExpression = "if applicant.age < 18 then \"declined\" else \"accepted\"";
        BaseNode ifBase = parse( inputExpression );

        assertThat( ifBase).isInstanceOf(IfExpressionNode.class);
        assertThat( ifBase.getText()).isEqualTo(inputExpression);
        assertThat( ifBase.getResultType()).isEqualTo(BuiltInType.STRING);

        IfExpressionNode ifExpr = (IfExpressionNode) ifBase;
        assertThat( ifExpr.getCondition().getText()).isEqualTo( "applicant.age < 18");
        assertThat( ifExpr.getThenExpression().getText()).isEqualTo( "\"declined\"");
        assertThat( ifExpr.getElseExpression().getText()).isEqualTo( "\"accepted\"");
    }

    @Test
    void quantifiedExpressionSome() {
        String inputExpression = "some item in order.items satisfies item.price > 100";
        BaseNode someBase = parse( inputExpression );

        assertThat( someBase).isInstanceOf(QuantifiedExpressionNode.class);
        assertThat( someBase.getText()).isEqualTo(inputExpression);
        assertThat( someBase.getResultType()).isEqualTo(BuiltInType.BOOLEAN);

        QuantifiedExpressionNode someExpr = (QuantifiedExpressionNode) someBase;
        assertThat( someExpr.getQuantifier()).isEqualTo(QuantifiedExpressionNode.Quantifier.SOME);
        assertThat( someExpr.getIterationContexts()).hasSize(1);
        assertThat( someExpr.getIterationContexts().get( 0 ).getText()).isEqualTo( "item in order.items");
        assertThat( someExpr.getExpression().getText()).isEqualTo( "item.price > 100");
    }

    @Test
    void quantifiedExpressionEvery() {
        String inputExpression = "every item in order.items satisfies item.price > 100";
        BaseNode everyBase = parse( inputExpression );

        assertThat( everyBase).isInstanceOf(QuantifiedExpressionNode.class);
        assertThat( everyBase.getText()).isEqualTo(inputExpression);
        assertThat( everyBase.getResultType()).isEqualTo(BuiltInType.BOOLEAN);

        QuantifiedExpressionNode everyExpr = (QuantifiedExpressionNode) everyBase;
        assertThat( everyExpr.getQuantifier()).isEqualTo(QuantifiedExpressionNode.Quantifier.EVERY);
        assertThat( everyExpr.getIterationContexts()).hasSize(1);
        assertThat( everyExpr.getIterationContexts().get( 0 ).getText()).isEqualTo( "item in order.items");
        assertThat( everyExpr.getExpression().getText()).isEqualTo( "item.price > 100");
    }

    @Test
    void instanceOfExpression() {
        String inputExpression = "\"foo\" instance of string";
        BaseNode instanceOfBase = parse( inputExpression );

        assertThat( instanceOfBase).isInstanceOf(InstanceOfNode.class);
        assertThat( instanceOfBase.getText()).isEqualTo(inputExpression);
        assertThat( instanceOfBase.getResultType()).isEqualTo(BuiltInType.BOOLEAN);

        InstanceOfNode ioExpr = (InstanceOfNode) instanceOfBase;
        assertThat( ioExpr.getExpression()).isInstanceOf(StringNode.class);
        assertThat( ioExpr.getExpression().getText()).isEqualTo( "\"foo\"");
        assertThat( ioExpr.getType()).isInstanceOf(TypeNode.class);
        assertThat( ioExpr.getType().getText()).isEqualTo("string");
    }

    @Test
    void instanceOfExpressionAnd() {
        String inputExpression = "\"foo\" instance of string and 10 instance of number";
        BaseNode andExpr = parse( inputExpression );

        assertThat( andExpr).isInstanceOf(InfixOpNode.class);
        assertThat( andExpr.getText()).isEqualTo(inputExpression);
        assertThat( andExpr.getResultType()).isEqualTo(BuiltInType.BOOLEAN);

        InfixOpNode and = (InfixOpNode) andExpr;
        assertThat( and.getOperator()).isEqualTo(InfixOperator.AND);
        assertThat( and.getLeft()).isInstanceOf(InstanceOfNode.class);
        assertThat( and.getRight()).isInstanceOf(InstanceOfNode.class);
        assertThat( and.getLeft().getText()).isEqualTo( "\"foo\" instance of string");
        assertThat( and.getRight().getText()).isEqualTo( "10 instance of number");
        assertThat( and.getLeft().getResultType()).isEqualTo(BuiltInType.BOOLEAN);
        assertThat( and.getRight().getResultType()).isEqualTo(BuiltInType.BOOLEAN);

        InstanceOfNode ioExpr = (InstanceOfNode) and.getLeft();
        assertThat( ioExpr.getExpression()).isInstanceOf(StringNode.class);
        assertThat( ioExpr.getExpression().getText()).isEqualTo( "\"foo\"");
        assertThat( ioExpr.getType()).isInstanceOf(TypeNode.class);
        assertThat( ioExpr.getType().getText()).isEqualTo("string");

        ioExpr = (InstanceOfNode) and.getRight();
        assertThat( ioExpr.getExpression()).isInstanceOf(NumberNode.class);
        assertThat( ioExpr.getExpression().getText()).isEqualTo("10");
        assertThat( ioExpr.getType()).isInstanceOf(TypeNode.class);
        assertThat( ioExpr.getType().getText()).isEqualTo("number");
    }

    @Test
    void instanceOfExpressionFunction() {
        String inputExpression = "duration instance of function";
        BaseNode instanceOfBase = parse( inputExpression );

        assertThat( instanceOfBase).isInstanceOf(InstanceOfNode.class);
        assertThat( instanceOfBase.getText()).isEqualTo(inputExpression);
        assertThat( instanceOfBase.getResultType()).isEqualTo(BuiltInType.BOOLEAN);

        InstanceOfNode ioExpr = (InstanceOfNode) instanceOfBase;
        assertThat( ioExpr.getExpression()).isInstanceOf(NameRefNode.class);
        assertThat( ioExpr.getExpression().getText()).isEqualTo("duration");
        assertThat( ioExpr.getType()).isInstanceOf(TypeNode.class);
        assertThat( ioExpr.getType().getText()).isEqualTo("function");
    }

    @Test
    void pathExpression() {
        String inputExpression = "[ 10, 15 ].size";
        BaseNode pathBase = parse( inputExpression );

        assertThat( pathBase).isInstanceOf(PathExpressionNode.class);
        assertThat( pathBase.getText()).isEqualTo(inputExpression);

        PathExpressionNode pathExpr = (PathExpressionNode) pathBase;
        assertThat( pathExpr.getExpression()).isInstanceOf(ListNode.class);
        assertThat( pathExpr.getExpression().getText()).isEqualTo( "10, 15");
        assertThat( pathExpr.getName()).isInstanceOf(NameRefNode.class);
        assertThat( pathExpr.getName().getText()).isEqualTo("size");
    }

    @Test
    void filterExpression() {
        String inputExpression = "[ {x:1, y:2}, {x:2, y:3} ][ x=1 ]";
        BaseNode filterBase = parse( inputExpression );

        assertThat( filterBase).isInstanceOf(FilterExpressionNode.class);
        assertThat( filterBase.getText()).isEqualTo(inputExpression);

        FilterExpressionNode filter = (FilterExpressionNode) filterBase;
        assertThat( filter.getExpression()).isInstanceOf(ListNode.class);
        assertThat( filter.getExpression().getText()).isEqualTo( "{x:1, y:2}, {x:2, y:3}");
        assertThat( filter.getFilter()).isInstanceOf(InfixOpNode.class);
        assertThat( filter.getFilter().getText()).isEqualTo( "x=1");
    }

    @Test
    void functionInvocationNamedParams() {
        String inputExpression = "my.test.Function( named parameter 1 : x+10, named parameter 2 : \"foo\" )";
        BaseNode functionBase = parse( inputExpression );

        assertThat( functionBase).isInstanceOf(FunctionInvocationNode.class);
        assertThat( functionBase.getText()).isEqualTo(inputExpression);

        FunctionInvocationNode function = (FunctionInvocationNode) functionBase;
        assertThat( function.getName()).isInstanceOf(QualifiedNameNode.class);
        assertThat( function.getName().getText()).isEqualTo("my.test.Function");
        assertThat( function.getParams()).isInstanceOf(ListNode.class);
        assertThat( function.getParams().getElements()).hasSize(2);
        assertThat( function.getParams().getElements().get( 0 )).isInstanceOf(NamedParameterNode.class);
        assertThat( function.getParams().getElements().get( 1 )).isInstanceOf(NamedParameterNode.class);

        NamedParameterNode named = (NamedParameterNode) function.getParams().getElements().get( 0 );
        assertThat( named.getText()).isEqualTo( "named parameter 1 : x+10");
        assertThat( named.getName().getText()).isEqualTo( "named parameter 1");
        assertThat( named.getExpression()).isInstanceOf(InfixOpNode.class);
        assertThat( named.getExpression().getText()).isEqualTo( "x+10");

        named = (NamedParameterNode) function.getParams().getElements().get( 1 );
        assertThat( named.getText()).isEqualTo( "named parameter 2 : \"foo\"");
        assertThat( named.getName().getText()).isEqualTo( "named parameter 2");
        assertThat( named.getExpression()).isInstanceOf(StringNode.class);
        assertThat( named.getExpression().getText()).isEqualTo( "\"foo\"");
    }

    @Test
    void functionInvocationPositionalParams() {
        String inputExpression = "my.test.Function( x+10, \"foo\" )";
        BaseNode functionBase = parse( inputExpression );

        assertThat( functionBase).isInstanceOf(FunctionInvocationNode.class);
        assertThat( functionBase.getText()).isEqualTo(inputExpression);

        FunctionInvocationNode function = (FunctionInvocationNode) functionBase;
        assertThat( function.getName()).isInstanceOf(QualifiedNameNode.class);
        assertThat( function.getName().getText()).isEqualTo("my.test.Function");
        assertThat( function.getParams()).isInstanceOf(ListNode.class);
        assertThat( function.getParams().getElements()).hasSize(2);
        assertThat( function.getParams().getElements().get( 0 )).isInstanceOf(InfixOpNode.class);
        assertThat( function.getParams().getElements().get( 1 )).isInstanceOf(StringNode.class);
    }

    @Test
    void functionInvocationWithKeyword() {
        String inputExpression = "date and time( \"2016-07-29T19:47:53\" )";
        BaseNode functionBase = parse( inputExpression );

        assertThat( functionBase).isInstanceOf(FunctionInvocationNode.class);
        assertThat( functionBase.getText()).isEqualTo(inputExpression);

        FunctionInvocationNode function = (FunctionInvocationNode) functionBase;
        assertThat( function.getName()).isInstanceOf(NameRefNode.class);
        assertThat( function.getName().getText()).isEqualTo( "date and time");
        assertThat( function.getParams()).isInstanceOf(ListNode.class);
        assertThat( function.getParams().getElements()).hasSize(1);
        assertThat( function.getParams().getElements().get( 0 )).isInstanceOf(StringNode.class);
    }

    @Test
    void functionInvocationWithExpressionParameters() {
        String inputExpression = "date and time( date(\"2016-07-29\"), time(\"19:47:53\") )";
        BaseNode functionBase = parse( inputExpression );

        assertThat( functionBase).isInstanceOf(FunctionInvocationNode.class);
        assertThat( functionBase.getText()).isEqualTo(inputExpression);

        FunctionInvocationNode function = (FunctionInvocationNode) functionBase;
        assertThat( function.getName()).isInstanceOf(NameRefNode.class);
        assertThat( function.getName().getText()).isEqualTo( "date and time");
        assertThat( function.getParams()).isInstanceOf(ListNode.class);
        assertThat( function.getParams().getElements()).hasSize(2);
        assertThat( function.getParams().getElements().get( 0 )).isInstanceOf(FunctionInvocationNode.class);
        assertThat( function.getParams().getElements().get( 1 )).isInstanceOf(FunctionInvocationNode.class);
    }

    @Test
    void functionInvocationEmptyParams() {
        String inputExpression = "my.test.Function()";
        BaseNode functionBase = parse( inputExpression );

        assertThat( functionBase).isInstanceOf(FunctionInvocationNode.class);
        assertThat( functionBase.getText()).isEqualTo(inputExpression);

        FunctionInvocationNode function = (FunctionInvocationNode) functionBase;
        assertThat( function.getName()).isInstanceOf(QualifiedNameNode.class);
        assertThat( function.getName().getText()).isEqualTo("my.test.Function");
        assertThat( function.getParams()).isInstanceOf(ListNode.class);
        assertThat( function.getParams().getElements()).isEmpty();
    }

    @Disabled("dropped since DMNv1.2")
    @Test
    void functionDecisionTableInvocation() {
        String inputExpression = "decision table( "
                                 + "    outputs: \"Applicant Risk Rating\","
                                 + "    input expression list: [\"Applicant Age\", \"Medical History\"],"
                                 + "    rule list: ["
                                 + "        [ >60      , \"good\" , \"Medium\" ],"
                                 + "        [ >60      , \"bad\"  , \"High\"   ],"
                                 + "        [ [25..60] , -        , \"Medium\" ]," // also another problem is the - operator cannot be inside of expression.
                                 + "        [ <25      , \"good\" , \"Low\"    ],"
                                 + "        [ <25      , \"bad\"  , \"Medium\" ] ],"
                                 + "    hit policy: \"Unique\" )";
        // need to call parse passing in the input variables
        BaseNode functionBase = parse( inputExpression );

        assertThat( functionBase).isInstanceOf(FunctionInvocationNode.class);
        assertThat( functionBase.getText()).isEqualTo(inputExpression);

        FunctionInvocationNode function = (FunctionInvocationNode) functionBase;
        assertThat( function.getName()).isInstanceOf(NameRefNode.class);
        assertThat( function.getName().getText()).isEqualTo( "decision table");
        assertThat( function.getParams()).isInstanceOf(ListNode.class);
        assertThat( function.getParams().getElements()).hasSize(4);
        assertThat( function.getParams().getElements().get( 0 )).isInstanceOf(NamedParameterNode.class);
        assertThat( function.getParams().getElements().get( 1 )).isInstanceOf(NamedParameterNode.class);
        assertThat( function.getParams().getElements().get( 2 )).isInstanceOf(NamedParameterNode.class);
        assertThat( function.getParams().getElements().get( 3 )).isInstanceOf(NamedParameterNode.class);

        NamedParameterNode named = (NamedParameterNode) function.getParams().getElements().get( 0 );
        assertThat( named.getText()).isEqualTo( "outputs: \"Applicant Risk Rating\"");
        assertThat( named.getName().getText()).isEqualTo("outputs");
        assertThat( named.getExpression()).isInstanceOf(StringNode.class);
        assertThat( named.getExpression().getText()).isEqualTo( "\"Applicant Risk Rating\"");

        named = (NamedParameterNode) function.getParams().getElements().get( 1 );
        assertThat( named.getName().getText()).isEqualTo( "input expression list");
        assertThat( named.getExpression()).isInstanceOf(ListNode.class);

        ListNode list = (ListNode) named.getExpression();
        assertThat( list.getElements()).hasSize(2);
        assertThat( list.getElements().get( 0 )).isInstanceOf(StringNode.class);
        assertThat( list.getElements().get( 0 ).getText()).isEqualTo( "\"Applicant Age\"");
        assertThat( list.getElements().get( 1 )).isInstanceOf(StringNode.class);
        assertThat( list.getElements().get( 1 ).getText()).isEqualTo( "\"Medical History\"");

        named = (NamedParameterNode) function.getParams().getElements().get( 2 );
        assertThat( named.getName().getText()).isEqualTo( "rule list");
        assertThat( named.getExpression()).isInstanceOf(ListNode.class);

        list = (ListNode) named.getExpression();
        assertThat(list.getElements()).hasSize(5); // this assert on the 5 rows but third row contains the - operation which is not allowed in expression.
        assertThat( list.getElements().get( 0 )).isInstanceOf(ListNode.class);

        ListNode rule = (ListNode) list.getElements().get( 0 );
        assertThat( rule.getElements()).hasSize(3);
        assertThat( rule.getElements().get( 0 )).isInstanceOf(RangeNode.class);
        assertThat( rule.getElements().get( 0 ).getText()).isEqualTo( ">60");
        assertThat( rule.getElements().get( 1 )).isInstanceOf(StringNode.class);
        assertThat( rule.getElements().get( 1 ).getText()).isEqualTo( "\"good\"");
        assertThat( rule.getElements().get( 2 )).isInstanceOf(StringNode.class);
        assertThat( rule.getElements().get( 2 ).getText()).isEqualTo( "\"Medium\"");

        named = (NamedParameterNode) function.getParams().getElements().get( 3 );
        assertThat( named.getName().getText()).isEqualTo( "hit policy");
        assertThat( named.getExpression()).isInstanceOf(StringNode.class);
        assertThat( named.getExpression().getText()).isEqualTo( "\"Unique\"");
    }

    @Test
    void contextPathExpression() {
        String inputExpression = "{ x : \"foo\" }.x";
        BaseNode pathBase = parse( inputExpression );

        assertThat( pathBase).isInstanceOf(PathExpressionNode.class);
        assertThat( pathBase.getText()).isEqualTo(inputExpression);
        assertThat( pathBase.getResultType()).isEqualTo(BuiltInType.STRING);

        PathExpressionNode pathExpr = (PathExpressionNode) pathBase;
        assertThat( pathExpr.getExpression()).isInstanceOf(ContextNode.class);
        assertThat( pathExpr.getExpression().getText()).isEqualTo( "{ x : \"foo\" }");
        assertThat( pathExpr.getName()).isInstanceOf(NameRefNode.class);
        assertThat( pathExpr.getName().getText()).isEqualTo("x");
    }

    @Test
    void contextPathExpression2() {
        String inputExpression = "{ x : { y : \"foo\" } }.x.y";
        BaseNode pathBase = parse( inputExpression );

        assertThat( pathBase).isInstanceOf(PathExpressionNode.class);
        assertThat( pathBase.getText()).isEqualTo(inputExpression);
        assertThat( pathBase.getResultType()).isEqualTo(BuiltInType.STRING);

        PathExpressionNode pathExpr = (PathExpressionNode) pathBase;
        assertThat( pathExpr.getExpression()).isInstanceOf(ContextNode.class);
        assertThat( pathExpr.getExpression().getText()).isEqualTo( "{ x : { y : \"foo\" } }");
        assertThat( pathExpr.getName()).isInstanceOf(QualifiedNameNode.class);
        assertThat( pathExpr.getName().getText()).isEqualTo("x.y");
    }

    @Test
    void contextPathExpression3() {
        String inputExpression = "{ first name : \"bob\" }.first name";
        BaseNode pathBase = parse( inputExpression );

        assertThat( pathBase).isInstanceOf(PathExpressionNode.class);
        assertThat( pathBase.getText()).isEqualTo(inputExpression);
        assertThat( pathBase.getResultType()).isEqualTo(BuiltInType.STRING);

        PathExpressionNode pathExpr = (PathExpressionNode) pathBase;
        assertThat( pathExpr.getExpression()).isInstanceOf(ContextNode.class);
        assertThat( pathExpr.getExpression().getText()).isEqualTo( "{ first name : \"bob\" }");
        assertThat( pathExpr.getName()).isInstanceOf(NameRefNode.class);
        assertThat( pathExpr.getName().getText()).isEqualTo( "first name");
    }

    @Test
    void variableName() {
        String var = "valid variable name";
        assertThat( FEELParser.isVariableNameValid( var )).isEqualTo(true);
    }

    @Test
    void variableNameWithValidCharacters() {
        String var = "?_873./-'+*valid";
        assertThat( FEELParser.isVariableNameValid( var )).isEqualTo(true);
    }

    @Test
    void variableNameWithValidCharactersHorseEmoji() {
        String var = "🐎";
        assertThat(FEELParser.isVariableNameValid(var)).isEqualTo(true);
    }

    @Test
    void variableNameWithInvalidCharacterPercentSimplified() {
        String var = "banana%mango";
        assertThat(FEELParser.isVariableNameValid(var)).isEqualTo(false);
        assertThat(FEELParser.checkVariableName(var).get(0).getMessage()).isEqualTo(Msg.createMessage(Msg.INVALID_VARIABLE_NAME, "character", "%"));
    }

    @Test
    void variableNameWithInvalidCharacterPercent() {
        String var = "?_873./-'%+*valid";
        assertThat( FEELParser.isVariableNameValid( var )).isEqualTo(false);
        assertThat( FEELParser.checkVariableName( var ).get( 0 ).getMessage()).isEqualTo( Msg.createMessage(Msg.INVALID_VARIABLE_NAME, "character", "%"));
    }

    @Test
    void variableNameWithInvalidCharacterAt() {
        String var = "?_873./-'@+*valid";
        assertThat(FEELParser.isVariableNameValid(var)).isEqualTo(false);
        assertThat(FEELParser.checkVariableName(var).get(0).getMessage()).isEqualTo(Msg.createMessage(Msg.INVALID_VARIABLE_NAME, "character", "@"));
    }

    @Test
    void variableNameInvalidStartCharacter() {
        String var = "5variable can't start with a number";
        assertThat( FEELParser.isVariableNameValid( var )).isEqualTo(false);
        assertThat( FEELParser.checkVariableName( var ).get( 0 ).getMessage()).isEqualTo( Msg.createMessage(Msg.INVALID_VARIABLE_NAME_START, "character", "5"));
    }

    @Test
    void variableNameCantStartWithKeyword() {
        String var = "for keyword is an invalid start for a variable name";
        assertThat( FEELParser.isVariableNameValid( var )).isEqualTo(false);
        assertThat( FEELParser.checkVariableName( var ).get( 0 ).getMessage()).isEqualTo( Msg.createMessage(Msg.INVALID_VARIABLE_NAME_START, "keyword", "for"));
    }

    public static void assertLocation(String inputExpression, ASTNode number) {
        assertThat( number.getText()).isEqualTo(inputExpression);
        assertThat( number.getStartChar()).isEqualTo(0);
        assertThat( number.getStartLine()).isEqualTo(1);
        assertThat( number.getStartColumn()).isEqualTo(0);
        assertThat( number.getEndChar()).isEqualTo( inputExpression.length() - 1 );
        assertThat( number.getEndLine()).isEqualTo(1);
        assertThat( number.getEndColumn()).isEqualTo( inputExpression.length() );
    }

    private BaseNode parse(String input) {
        return parse( input, Collections.emptyMap() );
    }

    private BaseNode parse(String input, Map<String, Type> inputTypes) {
        FEEL_1_1Parser parser = FEELParser.parse(null, input, inputTypes, Collections.emptyMap(), Collections.emptyList(), Collections.emptyList(), null);

        ParseTree tree = parser.expression();

        ASTBuilderVisitor v = new ASTBuilderVisitor(inputTypes, null);
        BaseNode expr = v.visit( tree );
        return expr;
    }

}
