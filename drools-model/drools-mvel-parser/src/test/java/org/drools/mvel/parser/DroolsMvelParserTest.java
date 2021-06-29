/*
 * Copyright (C) 2007-2010 Júlio Vilmar Gesser.
 * Copyright (C) 2011, 2013-2016 The JavaParser Team.
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * This file is part of JavaParser.
 *
 * JavaParser can be used either under the terms of
 * a) the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 * b) the terms of the Apache License
 *
 * You should have received a copy of both licenses in LICENCE.LGPL and
 * LICENCE.APACHE. Please refer to those files for details.
 *
 * JavaParser is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * Modified by Red Hat, Inc.
 */

package org.drools.mvel.parser;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.concurrent.TimeUnit;

import com.github.javaparser.ParseProblemException;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.BinaryExpr.Operator;
import com.github.javaparser.ast.expr.EnclosedExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.stmt.BlockStmt;
import org.apache.commons.lang3.SystemUtils;
import org.drools.mvel.parser.ast.expr.DrlNameExpr;
import org.drools.mvel.parser.ast.expr.DrlxExpression;
import org.drools.mvel.parser.ast.expr.HalfBinaryExpr;
import org.drools.mvel.parser.ast.expr.HalfPointFreeExpr;
import org.drools.mvel.parser.ast.expr.OOPathChunk;
import org.drools.mvel.parser.ast.expr.OOPathExpr;
import org.drools.mvel.parser.ast.expr.PointFreeExpr;
import org.drools.mvel.parser.ast.expr.TemporalLiteralChunkExpr;
import org.drools.mvel.parser.ast.expr.TemporalLiteralExpr;
import org.drools.mvel.parser.printer.PrintUtil;
import org.junit.Test;

import static org.drools.mvel.parser.DrlxParser.parseExpression;
import static org.drools.mvel.parser.printer.PrintUtil.printConstraint;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class DroolsMvelParserTest {

    private static final Collection<String> operators = new HashSet<>();
    {
        operators.addAll(Arrays.asList("after", "before", "in", "matches", "includes"));
    }

    final ParseStart<DrlxExpression> parser = DrlxParser.buildDrlxParserWithArguments(operators);

    @Test
    public void testParseSimpleExpr() {
        String expr = "name == \"Mark\"";
        Expression expression = parseExpression( parser, expr ).getExpr();


        BinaryExpr binaryExpr = ( (BinaryExpr) expression );
        assertEquals("name", toString(binaryExpr.getLeft()));
        assertEquals("\"Mark\"", toString(binaryExpr.getRight()));
        assertEquals(Operator.EQUALS, binaryExpr.getOperator());
    }

    @Test
    public void testBinaryWithNewLine() {
        Expression or = parseExpression(parser, "(addresses == 2 ||\n" +
                "                   addresses == 3  )").getExpr();
        assertEquals("(addresses == 2 || addresses == 3)", printConstraint(or));

        Expression and = parseExpression(parser, "(addresses == 2 &&\n addresses == 3  )").getExpr();
        assertEquals("(addresses == 2 && addresses == 3)", printConstraint(and));
    }

    @Test
    public void testBinaryWithWindowsNewLine() {
        Expression or = parseExpression(parser, "(addresses == 2 ||\r\n" +
                "                   addresses == 3  )").getExpr();
        assertEquals("(addresses == 2 || addresses == 3)", printConstraint(or));

        Expression and = parseExpression(parser, "(addresses == 2 &&\r\n addresses == 3  )").getExpr();
        assertEquals("(addresses == 2 && addresses == 3)", printConstraint(and));
    }

    @Test
    public void testBinaryWithNewLineBeginning() {
        Expression or = parseExpression(parser, "(" + newLine() + "addresses == 2 || addresses == 3  )").getExpr();
        assertEquals("(addresses == 2 || addresses == 3)", printConstraint(or));

        Expression and = parseExpression(parser, "(" + newLine() + "addresses == 2 && addresses == 3  )").getExpr();
        assertEquals("(addresses == 2 && addresses == 3)", printConstraint(and));
    }

    @Test
    public void testBinaryWithNewLineEnd() {
        Expression or = parseExpression(parser, "(addresses == 2 || addresses == 3 " + newLine() + ")").getExpr();
        assertEquals("(addresses == 2 || addresses == 3)", printConstraint(or));

        Expression and = parseExpression(parser, "(addresses == 2 && addresses == 3 " + newLine() + ")").getExpr();
        assertEquals("(addresses == 2 && addresses == 3)", printConstraint(and));
    }

    @Test
    public void testBinaryWithNewLineBeforeOperator() {
        String andExpr = "(addresses == 2" + newLine() + "&& addresses == 3  )";
        MvelParser mvelParser1 = new MvelParser(new ParserConfiguration(), true);
        Expression and2 = mvelParser1.parse(GeneratedMvelParser::Expression, new StringProvider(andExpr)).getResult().get();
        assertEquals("(addresses == 2 && addresses == 3)", printConstraint(and2));

        String orExpr = "(addresses == 2" + newLine() + "|| addresses == 3  )";
        MvelParser mvelParser2 = new MvelParser(new ParserConfiguration(), false);
        Expression or2 = mvelParser2.parse(GeneratedMvelParser::Expression, new StringProvider(orExpr)).getResult().get();
        assertEquals("(addresses == 2 || addresses == 3)", printConstraint(or2));
    }

    @Test
    public void testParseSafeCastExpr() {
        String expr = "this instanceof Person && ((Person) this).name == \"Mark\"";
        Expression expression = parseExpression( parser, expr ).getExpr();
        assertEquals(expr, printConstraint(expression));
    }

    @Test
    public void testParseInlineCastExpr() {
        String expr = "this#Person.name == \"Mark\"";
        Expression expression = parseExpression( parser, expr ).getExpr();
        assertEquals(expr, printConstraint(expression));
    }

    @Test
    public void testParseInlineCastExpr2() {
        String expr = "address#com.pkg.InternationalAddress.state.length == 5";
        Expression expression = parseExpression( parser, expr ).getExpr();
        assertEquals(expr, printConstraint(expression));
    }

    @Test
    public void testParseInlineCastExpr3() {
        String expr = "address#org.drools.mvel.compiler.LongAddress.country.substring(1)";
        Expression expression = parseExpression( parser, expr ).getExpr();
        assertEquals(expr, printConstraint(expression));
    }

    @Test
    public void testParseInlineCastExpr4() {
        String expr = "address#com.pkg.InternationalAddress.getState().length == 5";
        Expression expression = parseExpression( parser, expr ).getExpr();
        assertEquals(expr, printConstraint(expression));
    }

    @Test
    public void testParseNullSafeFieldAccessExpr() {
        String expr = "person!.name == \"Mark\"";
        Expression expression = parseExpression( parser, expr ).getExpr();
        assertEquals(expr, printConstraint(expression));
    }

    @Test
    public void testDotFreeExpr() {
        String expr = "this after $a";
        Expression expression = parseExpression( parser, expr ).getExpr();
        assertTrue(expression instanceof PointFreeExpr);
        assertEquals(expr, printConstraint(expression));
    }

    @Test
    public void testDotFreeEnclosed() {
        String expr = "(this after $a)";
        Expression expression = parseExpression( parser, expr ).getExpr();
        assertEquals(expr, printConstraint(expression));
    }

    @Test
    public void testDotFreeEnclosedWithNameExpr() {
        String expr = "(something after $a)";
        Expression expression = parseExpression( parser, expr ).getExpr();
        assertEquals(expr, printConstraint(expression));
    }


    @Test
    public void testLiteral() {
        String bigDecimalLiteral = "bigInteger < (50B)";
        Expression bigDecimalExpr = parseExpression( parser, bigDecimalLiteral ).getExpr();
        assertEquals(bigDecimalLiteral, printConstraint(bigDecimalExpr));

        String bigIntegerLiteral = "bigInteger == (50I)";
        Expression bigIntegerExpr = parseExpression( parser, bigIntegerLiteral ).getExpr();
        assertEquals(bigIntegerLiteral, printConstraint(bigIntegerExpr));
    }

    @Test
    public void testBigDecimalLiteral() {
        String bigDecimalLiteralWithDecimals = "12.111B";
        Expression bigDecimalExprWithDecimals = parseExpression( parser, bigDecimalLiteralWithDecimals ).getExpr();
        assertEquals(bigDecimalLiteralWithDecimals, printConstraint(bigDecimalExprWithDecimals));
    }

    @Test
    public void testDotFreeExprWithOr() {
        String expr = "this after $a || this after $b";
        Expression expression = parseExpression( parser, expr ).getExpr();
        assertTrue(expression instanceof BinaryExpr);
        assertEquals(expr, printConstraint(expression));
    }

    @Test
    public void testDotFreeExprWithArgs() {
        String expr = "this after[5,8] $a";
        Expression expression = parseExpression( parser, expr ).getExpr();
        assertTrue(expression instanceof PointFreeExpr);
        assertFalse(((PointFreeExpr)expression).isNegated());
        assertEquals("this after[5ms,8ms] $a", printConstraint(expression)); // please note the parsed expression once normalized would take the time unit for milliseconds.
    }

    @Test
    public void testDotFreeExprWithArgsInfinite() {
        String expr = "this after[5s,*] $a";
        Expression expression = parseExpression( parser, expr ).getExpr();
        assertTrue(expression instanceof PointFreeExpr);
        assertFalse(((PointFreeExpr)expression).isNegated());
        assertEquals("this after[5s,*] $a", printConstraint(expression)); // please note the parsed expression once normalized would take the time unit for milliseconds.
    }

    @Test
    public void testDotFreeExprWithThreeArgsInfinite() {
        String expr = "this after[*,*,*,2s] $a";
        Expression expression = parseExpression( parser, expr ).getExpr();
        assertTrue(expression instanceof PointFreeExpr);
        assertFalse(((PointFreeExpr)expression).isNegated());
        assertEquals("this after[*,*,*,2s] $a", printConstraint(expression)); // please note the parsed expression once normalized would take the time unit for milliseconds.
    }


    @Test
    public void testDotFreeExprWithArgsNegated() {
        String expr = "this not after[5,8] $a";
        Expression expression = parseExpression( parser, expr ).getExpr();
        assertThat(expression, instanceOf(PointFreeExpr.class));
        assertTrue(((PointFreeExpr)expression).isNegated());
        assertEquals("this not after[5ms,8ms] $a", printConstraint(expression)); // please note the parsed expression once normalized would take the time unit for milliseconds.
    }

    @Test
    public void testDotFreeExprWithTemporalArgs() {
        String expr = "this after[5ms,8d] $a";
        Expression expression = parseExpression( parser, expr ).getExpr();
        assertTrue(expression instanceof PointFreeExpr);
        assertEquals(expr, printConstraint(expression));
    }

    @Test
    public void testDotFreeExprWithFourTemporalArgs() {
        String expr = "this includes[1s,1m,1h,1d] $a";
        Expression expression = parseExpression( parser, expr ).getExpr();
        assertTrue(expression instanceof PointFreeExpr);
        assertEquals(expr, printConstraint(expression));
    }

    @Test
    public void testHalfDotFreeExprWithFourTemporalArgs() {
        String expr = "includes[1s,1m,1h,1d] $a";
        Expression expression = parseExpression( parser, expr ).getExpr();
        assertThat(expression, instanceOf(HalfPointFreeExpr.class));
        assertEquals(expr, printConstraint(expression));
    }

    @Test(expected = ParseProblemException.class)
    public void testInvalidTemporalArgs() {
        String expr = "this after[5ms,8f] $a";
        Expression expression = parseExpression( parser, expr ).getExpr();
    }

    @Test
    public void testOOPathExpr() {
        String expr = "/wife/children[age > 10]/toys";
        DrlxExpression drlx = parseExpression( parser, expr );
        Expression expression = drlx.getExpr();
        assertTrue(expression instanceof OOPathExpr);
        assertEquals(expr, printConstraint(drlx));
    }

    @Test
    public void testOOPathExprWithDot() {
        String expr = "/wife.children/toys";
        DrlxExpression drlx = parseExpression( parser, expr );
        Expression expression = drlx.getExpr();
        assertTrue(expression instanceof OOPathExpr);
        assertEquals(expr, printConstraint(drlx));
    }

    @Test
    public void testOOPathExprWithMultipleCondition() {
        String expr = "$address : /address[street == \"Elm\",city == \"Big City\"]";
        DrlxExpression drlx = parseExpression( parser, expr );
        Expression expression = drlx.getExpr();
        assertTrue(expression instanceof OOPathExpr);
        assertEquals(expr, printConstraint(drlx));
    }

    @Test
    public void testOOPathExprWithDeclaration() {
        String expr = "$toy : /wife/children[age > 10]/toys";
        DrlxExpression drlx = parseExpression( parser, expr );
        assertEquals("$toy", drlx.getBind().asString());
        Expression expression = drlx.getExpr();
        assertTrue(expression instanceof OOPathExpr);
        assertEquals(expr, printConstraint(drlx));
    }

    @Test
    public void testOOPathExprWithBackReference() {
        String expr = "$toy : /wife/children/toys[name.length == ../../name.length]";
        DrlxExpression drlx = parseExpression( parser, expr );
        assertEquals("$toy", drlx.getBind().asString());
        Expression expression = drlx.getExpr();
        assertTrue(expression instanceof OOPathExpr);

        final OOPathChunk secondChunk = ((OOPathExpr) expression).getChunks().get(2);
        final BinaryExpr secondChunkFirstCondition = (BinaryExpr) secondChunk.getConditions().get(0).getExpr();
        final DrlNameExpr rightName = (DrlNameExpr) ((FieldAccessExpr)secondChunkFirstCondition.getRight()).getScope();
        assertEquals(2, rightName.getBackReferencesCount());
        assertEquals(expr, printConstraint(drlx));
    }

    @Test
    public void testMapInitializationEmpty() {
        String expr = "countItems([])";
        DrlxExpression drlx = parseExpression( parser, expr );
        assertEquals(expr, printConstraint(drlx));
    }

    @Test
    public void testMapInitializationLiteralAsArgument() {
        String expr = "countItems([123 : 456, 789 : 1011])";
        DrlxExpression drlx = parseExpression( parser, expr );
        assertEquals(expr, printConstraint(drlx));
    }

    @Test
    public void testParseTemporalLiteral() {
        String expr = "5s";
        TemporalLiteralExpr drlx = DrlxParser.parseTemporalLiteral(expr);
        assertEquals(expr, printConstraint(drlx));
        assertEquals(1, drlx.getChunks().size());
        TemporalLiteralChunkExpr chunk0 = (TemporalLiteralChunkExpr) drlx.getChunks().get(0);
        assertEquals(5, chunk0.getValue());
        assertEquals(TimeUnit.SECONDS, chunk0.getTimeUnit());
    }

    @Test
    public void testParseTemporalLiteralOf2Chunks() {
        String expr = "1m5s";
        TemporalLiteralExpr drlx = DrlxParser.parseTemporalLiteral(expr);
        assertEquals(expr, printConstraint(drlx));
        assertEquals(2, drlx.getChunks().size());
        TemporalLiteralChunkExpr chunk0 = (TemporalLiteralChunkExpr) drlx.getChunks().get(0);
        assertEquals(1, chunk0.getValue());
        assertEquals(TimeUnit.MINUTES, chunk0.getTimeUnit());
        TemporalLiteralChunkExpr chunk1 = (TemporalLiteralChunkExpr) drlx.getChunks().get(1);
        assertEquals(5, chunk1.getValue());
        assertEquals(TimeUnit.SECONDS, chunk1.getTimeUnit());
    }

    @Test
    public void testInExpression() {
        String expr = "this in ()";
        Expression expression = parseExpression( parser, expr ).getExpr();
        assertTrue(expression instanceof PointFreeExpr);
        assertEquals(expr, printConstraint(expression));
    }

    @Test
    /* This shouldn't be supported, an HalfBinaryExpr should be valid only after a && or a || */
    public void testUnsupportedImplicitParameter() {
        String expr = "== \"Mark\"";
        Expression expression = parseExpression( parser, expr ).getExpr();
        assertTrue(expression instanceof HalfBinaryExpr);
        assertEquals(expr, printConstraint(expression));
    }

    @Test
    public void testAndWithImplicitNegativeParameter() {
        String expr = "value > -2 && < -1";
        Expression expression = parseExpression( parser, expr ).getExpr();

        BinaryExpr comboExpr = ( (BinaryExpr) expression );
        assertEquals(Operator.AND, comboExpr.getOperator());

        BinaryExpr first = (BinaryExpr) comboExpr.getLeft();
        assertEquals("value", toString(first.getLeft()));
        assertEquals("-2", toString(first.getRight()));
        assertEquals(Operator.GREATER, first.getOperator());

        HalfBinaryExpr second = (HalfBinaryExpr) comboExpr.getRight();
        assertEquals("-1", toString(second.getRight()));
        assertEquals(HalfBinaryExpr.Operator.LESS, second.getOperator());
    }

    @Test
    public void testAndWithImplicitParameterAndParenthesis() {
        String expr = "value (> 1 && < 2)";
        Expression expression = parseExpression( parser, expr ).getExpr();

        BinaryExpr comboExpr = ( (BinaryExpr) expression );
        assertEquals(Operator.AND, comboExpr.getOperator());

        BinaryExpr first = (BinaryExpr) comboExpr.getLeft();
        assertEquals("value", toString(first.getLeft()));
        assertEquals("1", toString(first.getRight()));
        assertEquals(Operator.GREATER, first.getOperator());

        HalfBinaryExpr second = (HalfBinaryExpr) comboExpr.getRight();
        assertEquals("2", toString(second.getRight()));
        assertEquals(HalfBinaryExpr.Operator.LESS, second.getOperator());
    }

    @Test
    public void testAndWithImplicitParameterAndParenthesisOnThis() {
        String expr = "this (> 1 && < 2)";
        Expression expression = parseExpression( parser, expr ).getExpr();

        BinaryExpr comboExpr = ( (BinaryExpr) expression );
        assertEquals(Operator.AND, comboExpr.getOperator());

        BinaryExpr first = (BinaryExpr) comboExpr.getLeft();
        assertEquals("this", toString(first.getLeft()));
        assertEquals("1", toString(first.getRight()));
        assertEquals(Operator.GREATER, first.getOperator());

        HalfBinaryExpr second = (HalfBinaryExpr) comboExpr.getRight();
        assertEquals("2", toString(second.getRight()));
        assertEquals(HalfBinaryExpr.Operator.LESS, second.getOperator());
    }

    @Test
    public void testAndWithImplicitParameterAndParenthesisComplex() {
        String expr = "value ((> 1 && < 2) || (> 3 && < 4))";
        Expression expression = parseExpression( parser, expr ).getExpr();

        BinaryExpr comboExpr = ( (BinaryExpr) expression );
        assertEquals(Operator.OR, comboExpr.getOperator());

        BinaryExpr comboExprLeft = ( (BinaryExpr) comboExpr.getLeft() );
        assertEquals(Operator.AND, comboExprLeft.getOperator());

        BinaryExpr first = (BinaryExpr) comboExprLeft.getLeft();
        assertEquals("value", toString(first.getLeft()));
        assertEquals("1", toString(first.getRight()));
        assertEquals(Operator.GREATER, first.getOperator());

        HalfBinaryExpr second = (HalfBinaryExpr) comboExprLeft.getRight();
        assertEquals("2", toString(second.getRight()));
        assertEquals(HalfBinaryExpr.Operator.LESS, second.getOperator());

        BinaryExpr comboExprRight = ( (BinaryExpr) comboExpr.getRight() );
        assertEquals(Operator.AND, comboExprRight.getOperator());

        BinaryExpr third = (BinaryExpr) comboExprRight.getLeft();
        assertEquals("value", toString(third.getLeft()));
        assertEquals("3", toString(third.getRight()));
        assertEquals(Operator.GREATER, third.getOperator());

        HalfBinaryExpr forth = (HalfBinaryExpr) comboExprRight.getRight();
        assertEquals("4", toString(forth.getRight()));
        assertEquals(HalfBinaryExpr.Operator.LESS, forth.getOperator());
    }

    @Test
    public void testAndWithImplicitParameterAndParenthesisMixedLeft() {
        String expr = "value ((> 1 && < 2) || > 3)";
        Expression expression = parseExpression( parser, expr ).getExpr();

        BinaryExpr comboExpr = ( (BinaryExpr) expression );
        assertEquals(Operator.OR, comboExpr.getOperator());

        BinaryExpr comboExprLeft = ( (BinaryExpr) comboExpr.getLeft() );
        assertEquals(Operator.AND, comboExprLeft.getOperator());

        BinaryExpr first = (BinaryExpr) comboExprLeft.getLeft();
        assertEquals("value", toString(first.getLeft()));
        assertEquals("1", toString(first.getRight()));
        assertEquals(Operator.GREATER, first.getOperator());

        HalfBinaryExpr second = (HalfBinaryExpr) comboExprLeft.getRight();
        assertEquals("2", toString(second.getRight()));
        assertEquals(HalfBinaryExpr.Operator.LESS, second.getOperator());

        BinaryExpr third = ( (BinaryExpr) comboExpr.getRight() );
        assertEquals("value", toString(third.getLeft()));
        assertEquals("3", toString(third.getRight()));
        assertEquals(Operator.GREATER, third.getOperator());
    }

    @Test
    public void testAndWithImplicitParameterAndParenthesisMixedRight() {
        String expr = "value (< 1 || (> 2 && < 3))";
        Expression expression = parseExpression( parser, expr ).getExpr();

        BinaryExpr comboExpr = ( (BinaryExpr) expression );
        assertEquals(Operator.OR, comboExpr.getOperator());

        BinaryExpr first = ( (BinaryExpr) comboExpr.getLeft() );
        assertEquals("value", toString(first.getLeft()));
        assertEquals("1", toString(first.getRight()));
        assertEquals(Operator.LESS, first.getOperator());

        BinaryExpr comboExprRight = ( (BinaryExpr) comboExpr.getRight() );
        assertEquals(Operator.AND, comboExprRight.getOperator());

        BinaryExpr third = (BinaryExpr) comboExprRight.getLeft();
        assertEquals("value", toString(third.getLeft()));
        assertEquals("2", toString(third.getRight()));
        assertEquals(Operator.GREATER, third.getOperator());

        HalfBinaryExpr forth = (HalfBinaryExpr) comboExprRight.getRight();
        assertEquals("3", toString(forth.getRight()));
        assertEquals(HalfBinaryExpr.Operator.LESS, forth.getOperator());
    }

    @Test
    public void testOrWithImplicitParameter() {
        String expr = "name == \"Mark\" || == \"Mario\" || == \"Luca\"";
        Expression expression = parseExpression( parser, expr ).getExpr();

        BinaryExpr comboExpr = ( (BinaryExpr) expression );
        assertEquals(Operator.OR, comboExpr.getOperator());

        BinaryExpr first = ((BinaryExpr)((BinaryExpr) comboExpr.getLeft()).getLeft());
        assertEquals("name", toString(first.getLeft()));
        assertEquals("\"Mark\"", toString(first.getRight()));
        assertEquals(Operator.EQUALS, first.getOperator());

        HalfBinaryExpr second = (HalfBinaryExpr) ((BinaryExpr) comboExpr.getLeft()).getRight();
        assertEquals("\"Mario\"", toString(second.getRight()));
        assertEquals(HalfBinaryExpr.Operator.EQUALS, second.getOperator());

        HalfBinaryExpr third = (HalfBinaryExpr) comboExpr.getRight();
        assertEquals("\"Luca\"", toString(third.getRight()));
        assertEquals(HalfBinaryExpr.Operator.EQUALS, third.getOperator());
    }

    @Test
    public void testAndWithImplicitParameter() {
        String expr = "name == \"Mark\" && == \"Mario\" && == \"Luca\"";
        Expression expression = parseExpression( parser, expr ).getExpr();


        BinaryExpr comboExpr = ( (BinaryExpr) expression );
        assertEquals(Operator.AND, comboExpr.getOperator());

        BinaryExpr first = ((BinaryExpr)((BinaryExpr) comboExpr.getLeft()).getLeft());
        assertEquals("name", toString(first.getLeft()));
        assertEquals("\"Mark\"", toString(first.getRight()));
        assertEquals(Operator.EQUALS, first.getOperator());

        HalfBinaryExpr second = (HalfBinaryExpr) ((BinaryExpr) comboExpr.getLeft()).getRight();
        assertEquals("\"Mario\"", toString(second.getRight()));
        assertEquals(HalfBinaryExpr.Operator.EQUALS, second.getOperator());

        HalfBinaryExpr third = (HalfBinaryExpr) comboExpr.getRight();
        assertEquals("\"Luca\"", toString(third.getRight()));
        assertEquals(HalfBinaryExpr.Operator.EQUALS, third.getOperator());
    }

    @Test
    public void testAndWithImplicitParameter2() {
        String expr = "name == \"Mark\" && == \"Mario\" || == \"Luca\"";
        Expression expression = parseExpression( parser, expr ).getExpr();


        BinaryExpr comboExpr = ( (BinaryExpr) expression );
        assertEquals(Operator.OR, comboExpr.getOperator());
        assertEquals(Operator.AND, ((BinaryExpr)(comboExpr.getLeft())).getOperator());

        BinaryExpr first = ((BinaryExpr)((BinaryExpr) comboExpr.getLeft()).getLeft());
        assertEquals("name", toString(first.getLeft()));
        assertEquals("\"Mark\"", toString(first.getRight()));
        assertEquals(Operator.EQUALS, first.getOperator());

        HalfBinaryExpr second = (HalfBinaryExpr) ((BinaryExpr) comboExpr.getLeft()).getRight();
        assertEquals("\"Mario\"", toString(second.getRight()));
        assertEquals(HalfBinaryExpr.Operator.EQUALS, second.getOperator());

        HalfBinaryExpr third = (HalfBinaryExpr) comboExpr.getRight();
        assertEquals("\"Luca\"", toString(third.getRight()));
        assertEquals(HalfBinaryExpr.Operator.EQUALS, third.getOperator());
    }

    @Test
    public void testAndWithImplicitParameter3() {
        String expr = "age == 2 && == 3 || == 4";
        Expression expression = parseExpression( parser, expr ).getExpr();


        BinaryExpr comboExpr = ( (BinaryExpr) expression );
        assertEquals(Operator.OR, comboExpr.getOperator());
        assertEquals(Operator.AND, ((BinaryExpr)(comboExpr.getLeft())).getOperator());

        BinaryExpr first = ((BinaryExpr)((BinaryExpr) comboExpr.getLeft()).getLeft());
        assertEquals("age", toString(first.getLeft()));
        assertEquals("2", toString(first.getRight()));
        assertEquals(Operator.EQUALS, first.getOperator());

        HalfBinaryExpr second = (HalfBinaryExpr) ((BinaryExpr) comboExpr.getLeft()).getRight();
        assertEquals("3", toString(second.getRight()));
        assertEquals(HalfBinaryExpr.Operator.EQUALS, second.getOperator());

        HalfBinaryExpr third = (HalfBinaryExpr) comboExpr.getRight();
        assertEquals("4", toString(third.getRight()));
        assertEquals(HalfBinaryExpr.Operator.EQUALS, third.getOperator());
    }

    @Test
    public void dotFreeWithRegexp() {
        String expr = "name matches \"[a-z]*\"";
        Expression expression = parseExpression( parser, expr ).getExpr();
        assertThat(expression, instanceOf(PointFreeExpr.class));
        assertEquals("name matches \"[a-z]*\"", printConstraint(expression));
        PointFreeExpr e = (PointFreeExpr)expression;
        assertEquals("matches", e.getOperator().asString());
        assertEquals("name", toString(e.getLeft()));
        assertEquals("\"[a-z]*\"", toString(e.getRight().get(0)));
    }

    @Test
    public void implicitOperatorWithRegexps() {
        String expr = "name matches \"[a-z]*\" || matches \"pippo\"";
        Expression expression = parseExpression(parser, expr).getExpr();
        assertEquals("name matches \"[a-z]*\" || matches \"pippo\"", printConstraint(expression));
    }

    @Test
    public void halfPointFreeExpr() {
        String expr = "matches \"[A-Z]*\"";
        Expression expression = parseExpression(parser, expr).getExpr();
        assertThat(expression, instanceOf(HalfPointFreeExpr.class));
        assertEquals("matches \"[A-Z]*\"", printConstraint(expression));
    }

    @Test
    public void halfPointFreeExprNegated() {
        String expr = "not matches \"[A-Z]*\"";
        Expression expression = parseExpression(parser, expr).getExpr();
        assertThat(expression, instanceOf(HalfPointFreeExpr.class));
        assertEquals("not matches \"[A-Z]*\"", printConstraint(expression));
    }

    @Test
    public void regressionTestHalfPointFree() {
        assertThat(parseExpression(parser, "getAddress().getAddressName().length() == 5").getExpr(), instanceOf(BinaryExpr.class));
        assertThat(parseExpression(parser, "isFortyYearsOld(this, true)").getExpr(), instanceOf(MethodCallExpr.class));
        assertThat(parseExpression(parser, "getName().startsWith(\"M\")").getExpr(), instanceOf(MethodCallExpr.class));
        assertThat(parseExpression(parser, "isPositive($i.intValue())").getExpr(), instanceOf(MethodCallExpr.class));
        assertThat(parseExpression(parser, "someEntity.someString in (\"1.500\")").getExpr(), instanceOf(PointFreeExpr.class));
    }

    @Test
    public void mvelSquareBracketsOperators() {
        testMvelSquareOperator("this str[startsWith] \"M\"", "str[startsWith]", "this", "\"M\"", false);
        testMvelSquareOperator("this not str[startsWith] \"M\"", "str[startsWith]", "this", "\"M\"", true);
        testMvelSquareOperator("this str[endsWith] \"K\"", "str[endsWith]", "this", "\"K\"", false);
        testMvelSquareOperator("this str[length] 17", "str[length]", "this", "17", false);
    }

    @Test
    public void halfPointFreeMVEL() {
        String expr = "this str[startsWith] \"M\" || str[startsWith] \"E\"";
        Expression expression = parseExpression(parser, expr).getExpr();
        assertEquals("this str[startsWith] \"M\" || str[startsWith] \"E\"", printConstraint(expression));

        Expression expression2 = parseExpression(parser, "str[startsWith] \"E\"").getExpr();
        assertThat(expression2, instanceOf(HalfPointFreeExpr.class));
        assertEquals("str[startsWith] \"E\"", printConstraint(expression2));
    }


    @Test
    public void testLambda() {
        String expr = "x -> y";
        DrlxExpression expression = parseExpression(parser, expr);
        assertEquals(expr, printConstraint(expression));
    }

    @Test
    public void testLambdaParameter() {
        String expr = "($p).setCanDrinkLambda(() -> true)";
        DrlxExpression expression = parseExpression(parser, expr);
        assertEquals(expr, printConstraint(expression));
    }

    @Test
    public void testModifyStatement() {
        String expr = "{ modify ( $p )  { name = \"Luca\", age = \"35\" }; }";

        BlockStmt expression = MvelParser.parseBlock(expr);
        assertEquals("{" + newLine() +
                             "    modify ($p) { name = \"Luca\", age = \"35\" };" + newLine() +
                             "}", printConstraint(expression));
    }

    @Test(expected = ParseProblemException.class)
    public void testModifyFailing() {
        String expr = "{ modify  { name = \"Luca\", age = \"35\" }; }";
        MvelParser.parseBlock(expr);
    }

    @Test
    public void testModifyStatementSemicolon() {
        String expr = "{ modify ( $p )  { name = \"Luca\"; }; }";

        BlockStmt expression = MvelParser.parseBlock(expr);
        assertEquals("{" + newLine() +
                             "    modify ($p) { name = \"Luca\" };" + newLine() +
                             "}", printConstraint(expression));
    }

    @Test
    public void testModifySemiColon() {
        String expr = "{ modify($p) { setAge(1); }; }";

        BlockStmt expression = MvelParser.parseBlock(expr);
        assertEquals("{" + newLine() +
                             "    modify ($p) { setAge(1) };" + newLine() +
                             "}", printConstraint(expression));
    }

    @Test
    public void testModifyMultiple() {
        String expr = "{ modify($p) { setAge(1)," + newLine() + " setAge(2);setAge(3)" + newLine() + "setAge(4); }; }";

        BlockStmt expression = MvelParser.parseBlock(expr);
        assertEquals("{" + newLine() +
                             "    modify ($p) { setAge(1), setAge(2), setAge(3), setAge(4) };" + newLine() +
                             "}", printConstraint(expression));
    }

    @Test
    public void testModifyEmptyBlock() {
        String expr = "{ modify( $s ) { } }";

        BlockStmt expression = MvelParser.parseBlock(expr);
        assertEquals("{" + newLine() +
                             "    modify ($s) {  };" + newLine() +
                             "}", printConstraint(expression));
    }

    @Test
    public void testModifyWithoutSemicolon() {
        String expr = "{modify($p) { setAge($p.getAge()+1) } }";

        BlockStmt expression = MvelParser.parseBlock(expr);
        assertEquals("{" + newLine() +
                             "    modify ($p) { setAge($p.getAge() + 1) };" + newLine() +
                             "}"
                , printConstraint(expression));
    }


    @Test
    public void testModifyWithCast() {
        String expr = "{modify( (BooleanEvent)$toEdit.get(0) ){  }}";

        BlockStmt expression = MvelParser.parseBlock(expr);
        assertEquals("{" + newLine() +
                             "    modify ((BooleanEvent) $toEdit.get(0)) {  };" + newLine() +
                             "}"
                , printConstraint(expression));
    }
    
    
    @Test
    public void testWithStatement() {
        String expr = "{ with ( $p )  { name = \"Luca\", age = \"35\" }; }";

        BlockStmt expression = MvelParser.parseBlock(expr);
        assertEquals("{" + newLine() +
                             "    with ($p) { name = \"Luca\", age = \"35\" };" + newLine() +
                             "}", printConstraint(expression));
    }

    @Test(expected = ParseProblemException.class)
    public void testWithFailing() {
        String expr = "{ with  { name = \"Luca\", age = \"35\" }; }";
        MvelParser.parseBlock(expr);
    }

    @Test
    public void testWithStatementSemicolon() {
        String expr = "{ with ( $p )  { name = \"Luca\"; }; }";

        BlockStmt expression = MvelParser.parseBlock(expr);
        assertEquals("{" + newLine() +
                             "    with ($p) { name = \"Luca\" };" + newLine() +
                             "}", printConstraint(expression));
    }

    @Test
    public void testWithSemiColon() {
        String expr = "{ with($p) { setAge(1); }; }";

        BlockStmt expression = MvelParser.parseBlock(expr);
        assertEquals("{" + newLine() +
                             "    with ($p) { setAge(1) };" + newLine() +
                             "}", printConstraint(expression));
    }

    @Test
    public void testWithEmptyBlock() {
        String expr = "{ with( $s ) { } }";

        BlockStmt expression = MvelParser.parseBlock(expr);
        assertEquals("{" + newLine() +
                             "    with ($s) {  };" + newLine() +
                             "}", printConstraint(expression));
    }

    @Test
    public void testWithWithoutSemicolon() {
        String expr = "{with($p) { setAge($p.getAge()+1) } }";

        BlockStmt expression = MvelParser.parseBlock(expr);
        assertEquals("{" + newLine() +
                             "    with ($p) { setAge($p.getAge() + 1) };" + newLine() +
                             "}"
                , printConstraint(expression));
    }


    @Test
    public void testWithWithCast() {
        String expr = "{with( (BooleanEvent)$toEdit.get(0) ){  }}";

        BlockStmt expression = MvelParser.parseBlock(expr);
        assertEquals("{" + newLine() +
                             "    with ((BooleanEvent) $toEdit.get(0)) {  };" + newLine() +
                             "}"
                , printConstraint(expression));
    }

    @Test
    public void testWithConstructor() {
        String expr = "{ with(s1 = new Some()) { }; }";

        BlockStmt expression = MvelParser.parseBlock(expr);
        assertEquals("{" + newLine() +
                             "    with (s1 = new Some()) {  };" + newLine() +
                             "}", printConstraint(expression));
    }

    @Test
    public void testWithoutSemicolon() {
        String expr = "{             " +
                        "a()" + newLine() +
                        "b()" + newLine() +
                        "}";

        BlockStmt expression = MvelParser.parseBlock(expr);
        assertEquals("{" + newLine() +
                             "    a();" + newLine() +
                             "    b();" + newLine() +
                             "}", printConstraint(expression));
    }

    @Test
    public void testWithoutSemicolonMethod() {
        String expr = "{             " +
                "delete($person)" + newLine() +
                "delete($pet)" + newLine() +
                "}";

        BlockStmt expression = MvelParser.parseBlock(expr);
        assertEquals("{" + newLine() +
                             "    delete($person);" + newLine() +
                             "    delete($pet);" + newLine() +
                             "}", printConstraint(expression));
    }

    @Test
    public void testWithoutSemicolonMethodComment() {
        String expr = "{             " +
                "delete($person) // comment" + newLine() +
                "delete($pet) // comment " + newLine() +
                "}";

        BlockStmt expression = MvelParser.parseBlock(expr);
        assertEquals("{" + newLine() +
                             "    delete($person);" + newLine() +
                             "    delete($pet);" + newLine() +
                             "}", printConstraint(expression));
    }

    @Test
    public void testWithoutSemicolonMethodCommentOppositeOSLineEndings() {
        final String oppositeLineEnding = SystemUtils.IS_OS_WINDOWS ? "\n" : "\r\n";
        String expr = "{             " +
                "delete($person) // comment" + oppositeLineEnding +
                "delete($pet) // comment" + oppositeLineEnding +
                "}";

        BlockStmt expression = MvelParser.parseBlock(expr);
        assertEquals("{" + newLine() +
                             "    delete($person);" + newLine() +
                             "    delete($pet);" + newLine() +
                             "}", printConstraint(expression));
    }

    @Test
    public void statementsWithComments() {
        String expr = "{             " +
                "delete($person); // comment" + newLine() +
                "delete($pet); // comment " + newLine() +
                "}";

        BlockStmt expression = MvelParser.parseBlock(expr);
        assertEquals("{" + newLine() +
                             "    delete($person);" + newLine() +
                             "    delete($pet);" + newLine() +
                             "}", printConstraint(expression));
    }


    @Test
    public void singleLineBlock() {
        String expr = "{ delete($person); } // comment ";

        BlockStmt expression = MvelParser.parseBlock(expr);
        assertEquals("{" + newLine() +
                             "    delete($person);" + newLine() +
                             "}", printConstraint(expression));
    }

    @Test
    public void singleLineBlockWithoutsemicolon() {
        String expr = "{ delete($person) } // comment";

        BlockStmt expression = MvelParser.parseBlock(expr);
        assertEquals("{" + newLine() +
                             "    delete($person);" + newLine() +
                             "}", printConstraint(expression));
    }

    @Test
    public void commentsWithEmptyStatements() {
        String expr = "{" +
                "// modify ; something" + newLine() +
                "/* modify ; something */" + newLine() +
                "setAge(47)" + newLine() +
                "}";

        BlockStmt expression = MvelParser.parseBlock(expr);
        assertEquals("{" + newLine() +
                             "    setAge(47);" + newLine() +
                             "}", printConstraint(expression));
    }

    @Test
    public void newLineInFunctionCall() {
        String expr = "{" +
                "func(x " + newLine() +
                ")" + newLine() +
                "}";

        BlockStmt expression = MvelParser.parseBlock(expr);
        assertEquals("{" + newLine() +
                             "    func(x);" + newLine() +
                             "}", printConstraint(expression));
    }

    @Test
    public void newLineInFunctionCall2() {
        Expression expression = MvelParser.parseExpression("func(x," + newLine() + " 2)");
        assertEquals("func(x, 2)", printConstraint(expression));
    }

    @Test
    public void newLineInFunctionCall3() {
        Expression expression = MvelParser.parseExpression("func(x" + newLine() + ", 2)");
        assertEquals("func(x, 2)", printConstraint(expression));
    }

    @Test
    public void commentsWithEmptyStatements2() {
        String expr = "{" +
                "  globalA.add(\"A\");" + newLine() +
                "  modify( $p ) {" + newLine() +
                "    // modify ; something" + newLine() +
                "    /* modify ; something */" + newLine() +
                "    setAge(47)" + newLine() +
                "  }" + newLine() +
                "  globalB.add(\"B\");" + newLine() +
                "  // modify ; something" + newLine() +
                "  /* modify ; something */" +
                "}";

        BlockStmt expression = MvelParser.parseBlock(expr);
        assertEquals("{" + newLine() +
                             "    globalA.add(\"A\");" + newLine() +
                             "    modify ($p) { setAge(47) };" + newLine() +
                             "    globalB.add(\"B\");" + newLine() +
                             "}", printConstraint(expression));

    }

    @Test
    public void testModifyLambda() {
        String expr = "{  modify($p) {  setCanDrinkLambda(() -> true); } }";
        BlockStmt expression = MvelParser.parseBlock(expr);
        assertEquals("{" + newLine() +
                             "    modify ($p) { setCanDrinkLambda(() -> true) };" + newLine() +
                             "}", printConstraint(expression));
    }

    @Test
    public void testNewExpression() {
        String expr = "money == new BigInteger(\"3\")";

        Expression expression = parseExpression(parser, expr).getExpr();
        assertEquals(expr, printConstraint(expression));
    }

    @Test
    public void testArrayCreation() {
        String expr = "new Object[] { \"getMessageId\", ($s != null ? $s : \"42103\") }";

        Expression expression = parseExpression(parser, expr).getExpr();
        assertEquals(expr, printConstraint(expression));
    }


    @Test
    public void testArrayCreation2() {
    String expr = "functions.arrayContainsInstanceWithParameters((Object[]) $f.getPersons())";

        Expression expression = parseExpression(parser, expr).getExpr();
        assertEquals(expr, printConstraint(expression));
    }

    @Test
    public void testSpecialNewlineHandling() {
        String expr = "{ a() \nprint(1) }";

        assertEquals("There should be 2 statements",
                     2, MvelParser.parseBlock(expr).getStatements().size());

        MvelParser mvelParser = new MvelParser(new ParserConfiguration(), false);
        ParseResult<BlockStmt> r = mvelParser.parse(GeneratedMvelParser::BlockParseStart, new StringProvider(expr));
        assertFalse("Parsing should break at newline", r.isSuccessful());
    }

        @Test
        public void testLineBreakAtTheEndOfStatementWithoutSemicolon() {
            String expr =
                    "{  Person p2 = new Person(\"John\");\n" +
                    "  p2.age = 30\n" + // a line break at the end of the statement without a semicolon
                    "insert(p2);\n }";

            MvelParser mvelParser = new MvelParser(new ParserConfiguration(), true);
            ParseResult<BlockStmt> r = mvelParser.parse(GeneratedMvelParser::BlockParseStart, new StringProvider(expr));
            BlockStmt blockStmt = r.getResult().get();
            assertEquals("Should parse 3 statements", 3, blockStmt.getStatements().size());

        }

    private void testMvelSquareOperator(String wholeExpression, String operator, String left, String right, boolean isNegated) {
        String expr = wholeExpression;
        Expression expression = parseExpression(parser, expr ).getExpr();
        assertThat(expression, instanceOf(PointFreeExpr.class));
        assertEquals(wholeExpression, printConstraint(expression));
        PointFreeExpr e = (PointFreeExpr)expression;
        assertEquals(operator, e.getOperator().asString());
        assertEquals(left, toString(e.getLeft()));
        assertEquals(right, toString(e.getRight().get(0)));
        assertEquals(isNegated, e.isNegated());
    }

    private String toString(Node n) {
        return PrintUtil.printConstraint(n);
    }

    private String newLine() {
        return System.lineSeparator();
    }

    @Test
    public void testBindVariable() {
        String expr = "$n : name == \"Mark\"";
        DrlxExpression drlxExpression = parseExpression( parser, expr );
        SimpleName bind = drlxExpression.getBind();
        assertEquals("$n", bind.asString());

        Expression expression = drlxExpression.getExpr();
        BinaryExpr binaryExpr = ( (BinaryExpr) expression );
        assertEquals("name", toString(binaryExpr.getLeft()));
        assertEquals("\"Mark\"", toString(binaryExpr.getRight()));
        assertEquals(Operator.EQUALS, binaryExpr.getOperator());
    }

    @Test
    public void testEnclosedBindVariable() {
        String expr = "($n : name == \"Mario\")";

        DrlxExpression drlxExpression = parseExpression(parser, expr);
        Expression enclosedExpr = drlxExpression.getExpr();
        assertTrue(enclosedExpr instanceof EnclosedExpr);
        Expression inner = ((EnclosedExpr) enclosedExpr).getInner();
        assertTrue(inner instanceof DrlxExpression);
        DrlxExpression innerDrlxExpression = (DrlxExpression) inner;

        SimpleName bind = innerDrlxExpression.getBind();
        assertEquals("$n", bind.asString());

        Expression expression = innerDrlxExpression.getExpr();
        BinaryExpr binaryExpr = ((BinaryExpr) expression);
        assertEquals("name", toString(binaryExpr.getLeft()));
        assertEquals("\"Mario\"", toString(binaryExpr.getRight()));
        assertEquals(Operator.EQUALS, binaryExpr.getOperator());
    }

    @Test
    public void testComplexEnclosedBindVariable() {
        String expr = "($n : name == \"Mario\") && (age > 20)";

        DrlxExpression drlxExpression = parseExpression(parser, expr);
        Expression bExpr = drlxExpression.getExpr();
        assertTrue(bExpr instanceof BinaryExpr);

        Node left = ((BinaryExpr) bExpr).getLeft();
        assertTrue(left instanceof EnclosedExpr);
        Expression inner = ((EnclosedExpr) left).getInner();
        assertTrue(inner instanceof DrlxExpression);
        DrlxExpression innerDrlxExpression = (DrlxExpression) inner;

        SimpleName bind = innerDrlxExpression.getBind();
        assertEquals("$n", bind.asString());

        Expression expression = innerDrlxExpression.getExpr();
        BinaryExpr binaryExpr = ((BinaryExpr) expression);
        assertEquals("name", toString(binaryExpr.getLeft()));
        assertEquals("\"Mario\"", toString(binaryExpr.getRight()));
        assertEquals(Operator.EQUALS, binaryExpr.getOperator());

        Node right = ((BinaryExpr) bExpr).getRight();
        assertTrue(right instanceof EnclosedExpr);
        Expression expression2 = ((EnclosedExpr) right).getInner();

        BinaryExpr binaryExpr2 = ((BinaryExpr) expression2);
        assertEquals("age", toString(binaryExpr2.getLeft()));
        assertEquals("20", toString(binaryExpr2.getRight()));
        assertEquals(Operator.GREATER, binaryExpr2.getOperator());
    }
}
