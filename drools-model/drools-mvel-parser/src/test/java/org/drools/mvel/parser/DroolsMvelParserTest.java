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
import org.drools.mvel.parser.ast.expr.NullSafeFieldAccessExpr;
import org.drools.mvel.parser.ast.expr.OOPathChunk;
import org.drools.mvel.parser.ast.expr.OOPathExpr;
import org.drools.mvel.parser.ast.expr.PointFreeExpr;
import org.drools.mvel.parser.ast.expr.TemporalLiteralChunkExpr;
import org.drools.mvel.parser.ast.expr.TemporalLiteralExpr;
import org.drools.mvel.parser.printer.PrintUtil;
import org.junit.Test;

import static org.drools.mvel.parser.DrlxParser.parseExpression;
import static org.drools.mvel.parser.printer.PrintUtil.printNode;
import static org.assertj.core.api.Assertions.assertThat;

public class DroolsMvelParserTest {

    private static final Collection<String> operators = new HashSet<>();
    static {
        operators.addAll(Arrays.asList("after", "before", "in", "matches", "includes"));
    }

    final ParseStart<DrlxExpression> parser = DrlxParser.buildDrlxParserWithArguments(operators);

    @Test
    public void testParseSimpleExpr() {
        String expr = "name == \"Mark\"";
        Expression expression = parseExpression( parser, expr ).getExpr();


        BinaryExpr binaryExpr = ( (BinaryExpr) expression );
        assertThat(toString(binaryExpr.getLeft())).isEqualTo("name");
        assertThat(toString(binaryExpr.getRight())).isEqualTo("\"Mark\"");
        assertThat(binaryExpr.getOperator()).isEqualTo(Operator.EQUALS);
    }

    @Test
    public void testBinaryWithNewLine() {
        Expression or = parseExpression(parser, "(addresses == 2 ||\n" +
                "                   addresses == 3  )").getExpr();
        assertThat(printNode(or)).isEqualTo("(addresses == 2 || addresses == 3)");

        Expression and = parseExpression(parser, "(addresses == 2 &&\n addresses == 3  )").getExpr();
        assertThat(printNode(and)).isEqualTo("(addresses == 2 && addresses == 3)");
    }

    @Test
    public void testBinaryWithWindowsNewLine() {
        Expression or = parseExpression(parser, "(addresses == 2 ||\r\n" +
                "                   addresses == 3  )").getExpr();
        assertThat(printNode(or)).isEqualTo("(addresses == 2 || addresses == 3)");

        Expression and = parseExpression(parser, "(addresses == 2 &&\r\n addresses == 3  )").getExpr();
        assertThat(printNode(and)).isEqualTo("(addresses == 2 && addresses == 3)");
    }

    @Test
    public void testBinaryWithNewLineBeginning() {
        Expression or = parseExpression(parser, "(" + newLine() + "addresses == 2 || addresses == 3  )").getExpr();
        assertThat(printNode(or)).isEqualTo("(addresses == 2 || addresses == 3)");

        Expression and = parseExpression(parser, "(" + newLine() + "addresses == 2 && addresses == 3  )").getExpr();
        assertThat(printNode(and)).isEqualTo("(addresses == 2 && addresses == 3)");
    }

    @Test
    public void testBinaryWithNewLineEnd() {
        Expression or = parseExpression(parser, "(addresses == 2 || addresses == 3 " + newLine() + ")").getExpr();
        assertThat(printNode(or)).isEqualTo("(addresses == 2 || addresses == 3)");

        Expression and = parseExpression(parser, "(addresses == 2 && addresses == 3 " + newLine() + ")").getExpr();
        assertThat(printNode(and)).isEqualTo("(addresses == 2 && addresses == 3)");
    }

    @Test
    public void testBinaryWithNewLineBeforeOperator() {
        String andExpr = "(addresses == 2" + newLine() + "&& addresses == 3  )";
        MvelParser mvelParser1 = new MvelParser(new ParserConfiguration(), true);
        Expression and2 = mvelParser1.parse(GeneratedMvelParser::Expression, new StringProvider(andExpr)).getResult().get();
        assertThat(printNode(and2)).isEqualTo("(addresses == 2 && addresses == 3)");

        String orExpr = "(addresses == 2" + newLine() + "|| addresses == 3  )";
        MvelParser mvelParser2 = new MvelParser(new ParserConfiguration(), false);
        Expression or2 = mvelParser2.parse(GeneratedMvelParser::Expression, new StringProvider(orExpr)).getResult().get();
        assertThat(printNode(or2)).isEqualTo("(addresses == 2 || addresses == 3)");
    }

    @Test
    public void testParseSafeCastExpr() {
        String expr = "this instanceof Person && ((Person) this).name == \"Mark\"";
        Expression expression = parseExpression( parser, expr ).getExpr();
        assertThat(printNode(expression)).isEqualTo(expr);
    }

    @Test
    public void testParseInlineCastExpr() {
        String expr = "this#Person.name == \"Mark\"";
        Expression expression = parseExpression( parser, expr ).getExpr();
        assertThat(printNode(expression)).isEqualTo(expr);
    }

    @Test
    public void testParseInlineCastExpr2() {
        String expr = "address#com.pkg.InternationalAddress.state.length == 5";
        Expression expression = parseExpression( parser, expr ).getExpr();
        assertThat(printNode(expression)).isEqualTo(expr);
    }

    @Test
    public void testParseInlineCastExpr3() {
        String expr = "address#org.drools.mvel.compiler.LongAddress.country.substring(1)";
        Expression expression = parseExpression( parser, expr ).getExpr();
        assertThat(printNode(expression)).isEqualTo(expr);
    }

    @Test
    public void testParseInlineCastExpr4() {
        String expr = "address#com.pkg.InternationalAddress.getState().length == 5";
        Expression expression = parseExpression( parser, expr ).getExpr();
        assertThat(printNode(expression)).isEqualTo(expr);
    }

    @Test
    public void testParseNullSafeFieldAccessExpr() {
        String expr = "person!.name == \"Mark\"";
        Expression expression = parseExpression( parser, expr ).getExpr();
        assertThat(printNode(expression)).isEqualTo(expr);
    }

    @Test
    public void testDotFreeExpr() {
        String expr = "this after $a";
        Expression expression = parseExpression( parser, expr ).getExpr();
        assertThat(expression instanceof PointFreeExpr).isTrue();
        assertThat(printNode(expression)).isEqualTo(expr);
    }

    @Test
    public void testDotFreeEnclosed() {
        String expr = "(this after $a)";
        Expression expression = parseExpression( parser, expr ).getExpr();
        assertThat(printNode(expression)).isEqualTo(expr);
    }

    @Test
    public void testConstantUnaryExpression() {
        String expr = "-49";
        Expression expression = parseExpression( parser, expr ).getExpr();
        assertThat(printNode(expression)).isEqualTo(expr);
        assertThat(expression.isUnaryExpr()).isTrue();
    }

    @Test
    public void testVariableUnaryExpression() {
        String expr = "-$a";
        Expression expression = parseExpression( parser, expr ).getExpr();
        assertThat(printNode(expression)).isEqualTo(expr);
        assertThat(expression.isUnaryExpr()).isTrue();
    }

    @Test
    public void testDotFreeEnclosedWithNameExpr() {
        String expr = "(something after $a)";
        Expression expression = parseExpression( parser, expr ).getExpr();
        assertThat(printNode(expression)).isEqualTo(expr);
    }


    @Test
    public void testLiteral() {
        String bigDecimalLiteral = "bigInteger < (50B)";
        Expression bigDecimalExpr = parseExpression( parser, bigDecimalLiteral ).getExpr();
        assertThat(printNode(bigDecimalExpr)).isEqualTo(bigDecimalLiteral);

        String bigIntegerLiteral = "bigInteger == (50I)";
        Expression bigIntegerExpr = parseExpression( parser, bigIntegerLiteral ).getExpr();
        assertThat(printNode(bigIntegerExpr)).isEqualTo(bigIntegerLiteral);
    }

    @Test
    public void testBigDecimalLiteral() {
        String bigDecimalLiteralWithDecimals = "12.111B";
        Expression bigDecimalExprWithDecimals = parseExpression( parser, bigDecimalLiteralWithDecimals ).getExpr();
        assertThat(printNode(bigDecimalExprWithDecimals)).isEqualTo(bigDecimalLiteralWithDecimals);
    }

    @Test
    public void testDotFreeExprWithOr() {
        String expr = "this after $a || this after $b";
        Expression expression = parseExpression( parser, expr ).getExpr();
        assertThat(expression instanceof BinaryExpr).isTrue();
        assertThat(printNode(expression)).isEqualTo(expr);
    }

    @Test
    public void testDotFreeExprWithArgs() {
        String expr = "this after[5,8] $a";
        Expression expression = parseExpression( parser, expr ).getExpr();
        assertThat(expression instanceof PointFreeExpr).isTrue();
        assertThat(((PointFreeExpr) expression).isNegated()).isFalse();
        assertThat(printNode(expression)).isEqualTo("this after[5ms,8ms] $a"); // please note the parsed expression once normalized would take the time unit for milliseconds.
    }

    @Test
    public void testDotFreeExprWithArgsInfinite() {
        String expr = "this after[5s,*] $a";
        Expression expression = parseExpression( parser, expr ).getExpr();
        assertThat(expression instanceof PointFreeExpr).isTrue();
        assertThat(((PointFreeExpr) expression).isNegated()).isFalse();
        assertThat(printNode(expression)).isEqualTo("this after[5s,*] $a"); // please note the parsed expression once normalized would take the time unit for milliseconds.
    }

    @Test
    public void testDotFreeExprWithThreeArgsInfinite() {
        String expr = "this after[*,*,*,2s] $a";
        Expression expression = parseExpression( parser, expr ).getExpr();
        assertThat(expression instanceof PointFreeExpr).isTrue();
        assertThat(((PointFreeExpr) expression).isNegated()).isFalse();
        assertThat(printNode(expression)).isEqualTo("this after[*,*,*,2s] $a"); // please note the parsed expression once normalized would take the time unit for milliseconds.
    }


    @Test
    public void testDotFreeExprWithArgsNegated() {
        String expr = "this not after[5,8] $a";
        Expression expression = parseExpression( parser, expr ).getExpr();
        assertThat(expression).isInstanceOf(PointFreeExpr.class);
        assertThat(((PointFreeExpr) expression).isNegated()).isTrue();
        assertThat(printNode(expression)).isEqualTo("this not after[5ms,8ms] $a"); // please note the parsed expression once normalized would take the time unit for milliseconds.
    }

    @Test
    public void testDotFreeExprWithTemporalArgs() {
        String expr = "this after[5ms,8d] $a";
        Expression expression = parseExpression( parser, expr ).getExpr();
        assertThat(expression instanceof PointFreeExpr).isTrue();
        assertThat(printNode(expression)).isEqualTo(expr);
    }

    @Test
    public void testDotFreeExprWithFourTemporalArgs() {
        String expr = "this includes[1s,1m,1h,1d] $a";
        Expression expression = parseExpression( parser, expr ).getExpr();
        assertThat(expression instanceof PointFreeExpr).isTrue();
        assertThat(printNode(expression)).isEqualTo(expr);
    }

    @Test
    public void testHalfDotFreeExprWithFourTemporalArgs() {
        String expr = "includes[1s,1m,1h,1d] $a";
        Expression expression = parseExpression( parser, expr ).getExpr();
        assertThat(expression).isInstanceOf(HalfPointFreeExpr.class);
        assertThat(printNode(expression)).isEqualTo(expr);
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
        assertThat(expression instanceof OOPathExpr).isTrue();
        assertThat(printNode(drlx)).isEqualTo(expr);
    }

    @Test
    public void testOOPathExprWithDot() {
        String expr = "/wife.children/toys";
        DrlxExpression drlx = parseExpression( parser, expr );
        Expression expression = drlx.getExpr();
        assertThat(expression instanceof OOPathExpr).isTrue();
        assertThat(printNode(drlx)).isEqualTo(expr);
    }

    @Test
    public void testOOPathExprWithMultipleCondition() {
        String expr = "$address : /address[street == \"Elm\",city == \"Big City\"]";
        DrlxExpression drlx = parseExpression( parser, expr );
        Expression expression = drlx.getExpr();
        assertThat(expression instanceof OOPathExpr).isTrue();
        assertThat(printNode(drlx)).isEqualTo(expr);
    }

    @Test
    public void testOOPathExprWithDeclaration() {
        String expr = "$toy : /wife/children[age > 10]/toys";
        DrlxExpression drlx = parseExpression( parser, expr );
        assertThat(drlx.getBind().asString()).isEqualTo("$toy");
        Expression expression = drlx.getExpr();
        assertThat(expression instanceof OOPathExpr).isTrue();
        assertThat(printNode(drlx)).isEqualTo(expr);
    }

    @Test
    public void testOOPathExprWithBackReference() {
        String expr = "$toy : /wife/children/toys[name.length == ../../name.length]";
        DrlxExpression drlx = parseExpression( parser, expr );
        assertThat(drlx.getBind().asString()).isEqualTo("$toy");
        Expression expression = drlx.getExpr();
        assertThat(expression instanceof OOPathExpr).isTrue();

        final OOPathChunk secondChunk = ((OOPathExpr) expression).getChunks().get(2);
        final BinaryExpr secondChunkFirstCondition = (BinaryExpr) secondChunk.getConditions().get(0).getExpr();
        final DrlNameExpr rightName = (DrlNameExpr) ((FieldAccessExpr)secondChunkFirstCondition.getRight()).getScope();
        assertThat(rightName.getBackReferencesCount()).isEqualTo(2);
        assertThat(printNode(drlx)).isEqualTo(expr);
    }

    @Test
    public void testMapInitializationEmpty() {
        String expr = "countItems([])";
        DrlxExpression drlx = parseExpression( parser, expr );
        assertThat(printNode(drlx)).isEqualTo(expr);
    }

    @Test
    public void testMapInitializationLiteralAsArgument() {
        String expr = "countItems([123 : 456, 789 : 1011])";
        DrlxExpression drlx = parseExpression( parser, expr );
        assertThat(printNode(drlx)).isEqualTo(expr);
    }

    @Test
    public void testParseTemporalLiteral() {
        String expr = "5s";
        TemporalLiteralExpr drlx = DrlxParser.parseTemporalLiteral(expr);
        assertThat(printNode(drlx)).isEqualTo(expr);
        assertThat(drlx.getChunks().size()).isEqualTo(1);
        TemporalLiteralChunkExpr chunk0 = (TemporalLiteralChunkExpr) drlx.getChunks().get(0);
        assertThat(chunk0.getValue()).isEqualTo(5);
        assertThat(chunk0.getTimeUnit()).isEqualTo(TimeUnit.SECONDS);
    }

    @Test
    public void testParseTemporalLiteralOf2Chunks() {
        String expr = "1m5s";
        TemporalLiteralExpr drlx = DrlxParser.parseTemporalLiteral(expr);
        assertThat(printNode(drlx)).isEqualTo(expr);
        assertThat(drlx.getChunks().size()).isEqualTo(2);
        TemporalLiteralChunkExpr chunk0 = (TemporalLiteralChunkExpr) drlx.getChunks().get(0);
        assertThat(chunk0.getValue()).isEqualTo(1);
        assertThat(chunk0.getTimeUnit()).isEqualTo(TimeUnit.MINUTES);
        TemporalLiteralChunkExpr chunk1 = (TemporalLiteralChunkExpr) drlx.getChunks().get(1);
        assertThat(chunk1.getValue()).isEqualTo(5);
        assertThat(chunk1.getTimeUnit()).isEqualTo(TimeUnit.SECONDS);
    }

    @Test
    public void testInExpression() {
        String expr = "this in ()";
        Expression expression = parseExpression( parser, expr ).getExpr();
        assertThat(expression instanceof PointFreeExpr).isTrue();
        assertThat(printNode(expression)).isEqualTo(expr);
    }

    @Test
    /* This shouldn't be supported, an HalfBinaryExpr should be valid only after a && or a || */
    public void testUnsupportedImplicitParameter() {
        String expr = "== \"Mark\"";
        Expression expression = parseExpression( parser, expr ).getExpr();
        assertThat(expression instanceof HalfBinaryExpr).isTrue();
        assertThat(printNode(expression)).isEqualTo(expr);
    }

    @Test
    public void testAndWithImplicitNegativeParameter() {
        String expr = "value > -2 && < -1";
        Expression expression = parseExpression( parser, expr ).getExpr();

        BinaryExpr comboExpr = ( (BinaryExpr) expression );
        assertThat(comboExpr.getOperator()).isEqualTo(Operator.AND);

        BinaryExpr first = (BinaryExpr) comboExpr.getLeft();
        assertThat(toString(first.getLeft())).isEqualTo("value");
        assertThat(toString(first.getRight())).isEqualTo("-2");
        assertThat(first.getRight().isUnaryExpr()).isTrue();
        assertThat(first.getOperator()).isEqualTo(Operator.GREATER);

        HalfBinaryExpr second = (HalfBinaryExpr) comboExpr.getRight();
        assertThat(toString(second.getRight())).isEqualTo("-1");
        assertThat(second.getRight().isUnaryExpr()).isTrue();
        assertThat(second.getOperator()).isEqualTo(HalfBinaryExpr.Operator.LESS);
    }

    @Test
    public void testAndWithImplicitParameterAndParenthesis() {
        String expr = "value (> 1 && < 2)";
        Expression expression = parseExpression( parser, expr ).getExpr();

        BinaryExpr comboExpr = ( (BinaryExpr) expression );
        assertThat(comboExpr.getOperator()).isEqualTo(Operator.AND);

        BinaryExpr first = (BinaryExpr) comboExpr.getLeft();
        assertThat(toString(first.getLeft())).isEqualTo("value");
        assertThat(toString(first.getRight())).isEqualTo("1");
        assertThat(first.getOperator()).isEqualTo(Operator.GREATER);

        HalfBinaryExpr second = (HalfBinaryExpr) comboExpr.getRight();
        assertThat(toString(second.getRight())).isEqualTo("2");
        assertThat(second.getOperator()).isEqualTo(HalfBinaryExpr.Operator.LESS);
    }

    @Test
    public void testAndWithImplicitParameterAndParenthesisOnThis() {
        String expr = "this (> 1 && < 2)";
        Expression expression = parseExpression( parser, expr ).getExpr();

        BinaryExpr comboExpr = ( (BinaryExpr) expression );
        assertThat(comboExpr.getOperator()).isEqualTo(Operator.AND);

        BinaryExpr first = (BinaryExpr) comboExpr.getLeft();
        assertThat(toString(first.getLeft())).isEqualTo("this");
        assertThat(toString(first.getRight())).isEqualTo("1");
        assertThat(first.getOperator()).isEqualTo(Operator.GREATER);

        HalfBinaryExpr second = (HalfBinaryExpr) comboExpr.getRight();
        assertThat(toString(second.getRight())).isEqualTo("2");
        assertThat(second.getOperator()).isEqualTo(HalfBinaryExpr.Operator.LESS);
    }

    @Test
    public void testAndWithImplicitParameterAndParenthesisComplex() {
        String expr = "value ((> 1 && < 2) || (> 3 && < 4))";
        Expression expression = parseExpression( parser, expr ).getExpr();

        BinaryExpr comboExpr = ( (BinaryExpr) expression );
        assertThat(comboExpr.getOperator()).isEqualTo(Operator.OR);

        BinaryExpr comboExprLeft = ( (BinaryExpr) comboExpr.getLeft() );
        assertThat(comboExprLeft.getOperator()).isEqualTo(Operator.AND);

        BinaryExpr first = (BinaryExpr) comboExprLeft.getLeft();
        assertThat(toString(first.getLeft())).isEqualTo("value");
        assertThat(toString(first.getRight())).isEqualTo("1");
        assertThat(first.getOperator()).isEqualTo(Operator.GREATER);

        HalfBinaryExpr second = (HalfBinaryExpr) comboExprLeft.getRight();
        assertThat(toString(second.getRight())).isEqualTo("2");
        assertThat(second.getOperator()).isEqualTo(HalfBinaryExpr.Operator.LESS);

        BinaryExpr comboExprRight = ( (BinaryExpr) comboExpr.getRight() );
        assertThat(comboExprRight.getOperator()).isEqualTo(Operator.AND);

        BinaryExpr third = (BinaryExpr) comboExprRight.getLeft();
        assertThat(toString(third.getLeft())).isEqualTo("value");
        assertThat(toString(third.getRight())).isEqualTo("3");
        assertThat(third.getOperator()).isEqualTo(Operator.GREATER);

        HalfBinaryExpr forth = (HalfBinaryExpr) comboExprRight.getRight();
        assertThat(toString(forth.getRight())).isEqualTo("4");
        assertThat(forth.getOperator()).isEqualTo(HalfBinaryExpr.Operator.LESS);
    }

    @Test
    public void testAndWithImplicitParameterAndParenthesisComplexOnField() {
        String expr = "value.length ((> 1 && < 2) || (> 3 && < 4))";
        Expression expression = parseExpression( parser, expr ).getExpr();

        BinaryExpr comboExpr = ( (BinaryExpr) expression );
        assertThat(comboExpr.getOperator()).isEqualTo(Operator.OR);

        BinaryExpr comboExprLeft = ( (BinaryExpr) comboExpr.getLeft() );
        assertThat(comboExprLeft.getOperator()).isEqualTo(Operator.AND);

        BinaryExpr first = (BinaryExpr) comboExprLeft.getLeft();
        assertThat(first.getLeft() instanceof FieldAccessExpr).isTrue();
        assertThat(toString(first.getLeft())).isEqualTo("value.length");
        assertThat(toString(first.getRight())).isEqualTo("1");
        assertThat(first.getOperator()).isEqualTo(Operator.GREATER);

        HalfBinaryExpr second = (HalfBinaryExpr) comboExprLeft.getRight();
        assertThat(toString(second.getRight())).isEqualTo("2");
        assertThat(second.getOperator()).isEqualTo(HalfBinaryExpr.Operator.LESS);

        BinaryExpr comboExprRight = ( (BinaryExpr) comboExpr.getRight() );
        assertThat(comboExprRight.getOperator()).isEqualTo(Operator.AND);

        BinaryExpr third = (BinaryExpr) comboExprRight.getLeft();
        assertThat(toString(third.getLeft())).isEqualTo("value.length");
        assertThat(toString(third.getRight())).isEqualTo("3");
        assertThat(third.getOperator()).isEqualTo(Operator.GREATER);

        HalfBinaryExpr forth = (HalfBinaryExpr) comboExprRight.getRight();
        assertThat(toString(forth.getRight())).isEqualTo("4");
        assertThat(forth.getOperator()).isEqualTo(HalfBinaryExpr.Operator.LESS);
    }

    @Test
    public void testAndWithImplicitParameterAndParenthesisComplexOnNullSafeField() {
        String expr = "value!.length ((> 1 && < 2) || (> 3 && < 4))";
        Expression expression = parseExpression( parser, expr ).getExpr();

        BinaryExpr comboExpr = ( (BinaryExpr) expression );
        assertThat(comboExpr.getOperator()).isEqualTo(Operator.OR);

        BinaryExpr comboExprLeft = ( (BinaryExpr) comboExpr.getLeft() );
        assertThat(comboExprLeft.getOperator()).isEqualTo(Operator.AND);

        BinaryExpr first = (BinaryExpr) comboExprLeft.getLeft();
        assertThat(first.getLeft() instanceof NullSafeFieldAccessExpr).isTrue();
        assertThat(toString(first.getLeft())).isEqualTo("value!.length");
        assertThat(toString(first.getRight())).isEqualTo("1");
        assertThat(first.getOperator()).isEqualTo(Operator.GREATER);

        HalfBinaryExpr second = (HalfBinaryExpr) comboExprLeft.getRight();
        assertThat(toString(second.getRight())).isEqualTo("2");
        assertThat(second.getOperator()).isEqualTo(HalfBinaryExpr.Operator.LESS);

        BinaryExpr comboExprRight = ( (BinaryExpr) comboExpr.getRight() );
        assertThat(comboExprRight.getOperator()).isEqualTo(Operator.AND);

        BinaryExpr third = (BinaryExpr) comboExprRight.getLeft();
        assertThat(toString(third.getLeft())).isEqualTo("value!.length");
        assertThat(toString(third.getRight())).isEqualTo("3");
        assertThat(third.getOperator()).isEqualTo(Operator.GREATER);

        HalfBinaryExpr forth = (HalfBinaryExpr) comboExprRight.getRight();
        assertThat(toString(forth.getRight())).isEqualTo("4");
        assertThat(forth.getOperator()).isEqualTo(HalfBinaryExpr.Operator.LESS);
    }

    @Test
    public void testAndWithImplicitParameterAndParenthesisMixedLeft() {
        String expr = "value ((> 1 && < 2) || > 3)";
        Expression expression = parseExpression( parser, expr ).getExpr();

        BinaryExpr comboExpr = ( (BinaryExpr) expression );
        assertThat(comboExpr.getOperator()).isEqualTo(Operator.OR);

        BinaryExpr comboExprLeft = ( (BinaryExpr) comboExpr.getLeft() );
        assertThat(comboExprLeft.getOperator()).isEqualTo(Operator.AND);

        BinaryExpr first = (BinaryExpr) comboExprLeft.getLeft();
        assertThat(toString(first.getLeft())).isEqualTo("value");
        assertThat(toString(first.getRight())).isEqualTo("1");
        assertThat(first.getOperator()).isEqualTo(Operator.GREATER);

        HalfBinaryExpr second = (HalfBinaryExpr) comboExprLeft.getRight();
        assertThat(toString(second.getRight())).isEqualTo("2");
        assertThat(second.getOperator()).isEqualTo(HalfBinaryExpr.Operator.LESS);

        BinaryExpr third = ( (BinaryExpr) comboExpr.getRight() );
        assertThat(toString(third.getLeft())).isEqualTo("value");
        assertThat(toString(third.getRight())).isEqualTo("3");
        assertThat(third.getOperator()).isEqualTo(Operator.GREATER);
    }

    @Test
    public void testAndWithImplicitParameterAndParenthesisMixedRight() {
        String expr = "value (< 1 || (> 2 && < 3))";
        Expression expression = parseExpression( parser, expr ).getExpr();

        BinaryExpr comboExpr = ( (BinaryExpr) expression );
        assertThat(comboExpr.getOperator()).isEqualTo(Operator.OR);

        BinaryExpr first = ( (BinaryExpr) comboExpr.getLeft() );
        assertThat(toString(first.getLeft())).isEqualTo("value");
        assertThat(toString(first.getRight())).isEqualTo("1");
        assertThat(first.getOperator()).isEqualTo(Operator.LESS);

        BinaryExpr comboExprRight = ( (BinaryExpr) comboExpr.getRight() );
        assertThat(comboExprRight.getOperator()).isEqualTo(Operator.AND);

        BinaryExpr third = (BinaryExpr) comboExprRight.getLeft();
        assertThat(toString(third.getLeft())).isEqualTo("value");
        assertThat(toString(third.getRight())).isEqualTo("2");
        assertThat(third.getOperator()).isEqualTo(Operator.GREATER);

        HalfBinaryExpr forth = (HalfBinaryExpr) comboExprRight.getRight();
        assertThat(toString(forth.getRight())).isEqualTo("3");
        assertThat(forth.getOperator()).isEqualTo(HalfBinaryExpr.Operator.LESS);
    }

    @Test
    public void testOrWithImplicitParameter() {
        String expr = "name == \"Mark\" || == \"Mario\" || == \"Luca\"";
        Expression expression = parseExpression( parser, expr ).getExpr();

        BinaryExpr comboExpr = ( (BinaryExpr) expression );
        assertThat(comboExpr.getOperator()).isEqualTo(Operator.OR);

        BinaryExpr first = ((BinaryExpr)((BinaryExpr) comboExpr.getLeft()).getLeft());
        assertThat(toString(first.getLeft())).isEqualTo("name");
        assertThat(toString(first.getRight())).isEqualTo("\"Mark\"");
        assertThat(first.getOperator()).isEqualTo(Operator.EQUALS);

        HalfBinaryExpr second = (HalfBinaryExpr) ((BinaryExpr) comboExpr.getLeft()).getRight();
        assertThat(toString(second.getRight())).isEqualTo("\"Mario\"");
        assertThat(second.getOperator()).isEqualTo(HalfBinaryExpr.Operator.EQUALS);

        HalfBinaryExpr third = (HalfBinaryExpr) comboExpr.getRight();
        assertThat(toString(third.getRight())).isEqualTo("\"Luca\"");
        assertThat(third.getOperator()).isEqualTo(HalfBinaryExpr.Operator.EQUALS);
    }

    @Test
    public void testAndWithImplicitParameter() {
        String expr = "name == \"Mark\" && == \"Mario\" && == \"Luca\"";
        Expression expression = parseExpression( parser, expr ).getExpr();


        BinaryExpr comboExpr = ( (BinaryExpr) expression );
        assertThat(comboExpr.getOperator()).isEqualTo(Operator.AND);

        BinaryExpr first = ((BinaryExpr)((BinaryExpr) comboExpr.getLeft()).getLeft());
        assertThat(toString(first.getLeft())).isEqualTo("name");
        assertThat(toString(first.getRight())).isEqualTo("\"Mark\"");
        assertThat(first.getOperator()).isEqualTo(Operator.EQUALS);

        HalfBinaryExpr second = (HalfBinaryExpr) ((BinaryExpr) comboExpr.getLeft()).getRight();
        assertThat(toString(second.getRight())).isEqualTo("\"Mario\"");
        assertThat(second.getOperator()).isEqualTo(HalfBinaryExpr.Operator.EQUALS);

        HalfBinaryExpr third = (HalfBinaryExpr) comboExpr.getRight();
        assertThat(toString(third.getRight())).isEqualTo("\"Luca\"");
        assertThat(third.getOperator()).isEqualTo(HalfBinaryExpr.Operator.EQUALS);
    }

    @Test
    public void testAndWithImplicitParameter2() {
        String expr = "name == \"Mark\" && == \"Mario\" || == \"Luca\"";
        Expression expression = parseExpression( parser, expr ).getExpr();


        BinaryExpr comboExpr = ( (BinaryExpr) expression );
        assertThat(comboExpr.getOperator()).isEqualTo(Operator.OR);
        assertThat(((BinaryExpr) (comboExpr.getLeft())).getOperator()).isEqualTo(Operator.AND);

        BinaryExpr first = ((BinaryExpr)((BinaryExpr) comboExpr.getLeft()).getLeft());
        assertThat(toString(first.getLeft())).isEqualTo("name");
        assertThat(toString(first.getRight())).isEqualTo("\"Mark\"");
        assertThat(first.getOperator()).isEqualTo(Operator.EQUALS);

        HalfBinaryExpr second = (HalfBinaryExpr) ((BinaryExpr) comboExpr.getLeft()).getRight();
        assertThat(toString(second.getRight())).isEqualTo("\"Mario\"");
        assertThat(second.getOperator()).isEqualTo(HalfBinaryExpr.Operator.EQUALS);

        HalfBinaryExpr third = (HalfBinaryExpr) comboExpr.getRight();
        assertThat(toString(third.getRight())).isEqualTo("\"Luca\"");
        assertThat(third.getOperator()).isEqualTo(HalfBinaryExpr.Operator.EQUALS);
    }

    @Test
    public void testAndWithImplicitParameter3() {
        String expr = "age == 2 && == 3 || == 4";
        Expression expression = parseExpression( parser, expr ).getExpr();


        BinaryExpr comboExpr = ( (BinaryExpr) expression );
        assertThat(comboExpr.getOperator()).isEqualTo(Operator.OR);
        assertThat(((BinaryExpr) (comboExpr.getLeft())).getOperator()).isEqualTo(Operator.AND);

        BinaryExpr first = ((BinaryExpr)((BinaryExpr) comboExpr.getLeft()).getLeft());
        assertThat(toString(first.getLeft())).isEqualTo("age");
        assertThat(toString(first.getRight())).isEqualTo("2");
        assertThat(first.getOperator()).isEqualTo(Operator.EQUALS);

        HalfBinaryExpr second = (HalfBinaryExpr) ((BinaryExpr) comboExpr.getLeft()).getRight();
        assertThat(toString(second.getRight())).isEqualTo("3");
        assertThat(second.getOperator()).isEqualTo(HalfBinaryExpr.Operator.EQUALS);

        HalfBinaryExpr third = (HalfBinaryExpr) comboExpr.getRight();
        assertThat(toString(third.getRight())).isEqualTo("4");
        assertThat(third.getOperator()).isEqualTo(HalfBinaryExpr.Operator.EQUALS);
    }

    @Test
    public void dotFreeWithRegexp() {
        String expr = "name matches \"[a-z]*\"";
        Expression expression = parseExpression( parser, expr ).getExpr();
        assertThat(expression).isInstanceOf(PointFreeExpr.class);
        assertThat(printNode(expression)).isEqualTo("name matches \"[a-z]*\"");
        PointFreeExpr e = (PointFreeExpr)expression;
        assertThat(e.getOperator().asString()).isEqualTo("matches");
        assertThat(toString(e.getLeft())).isEqualTo("name");
        assertThat(toString(e.getRight().get(0))).isEqualTo("\"[a-z]*\"");
    }

    @Test
    public void implicitOperatorWithRegexps() {
        String expr = "name matches \"[a-z]*\" || matches \"pippo\"";
        Expression expression = parseExpression(parser, expr).getExpr();
        assertThat(printNode(expression)).isEqualTo("name matches \"[a-z]*\" || matches \"pippo\"");
    }

    @Test
    public void halfPointFreeExpr() {
        String expr = "matches \"[A-Z]*\"";
        Expression expression = parseExpression(parser, expr).getExpr();
        assertThat(expression).isInstanceOf(HalfPointFreeExpr.class);
        assertThat(printNode(expression)).isEqualTo("matches \"[A-Z]*\"");
    }

    @Test
    public void halfPointFreeExprNegated() {
        String expr = "not matches \"[A-Z]*\"";
        Expression expression = parseExpression(parser, expr).getExpr();
        assertThat(expression).isInstanceOf(HalfPointFreeExpr.class);
        assertThat(printNode(expression)).isEqualTo("not matches \"[A-Z]*\"");
    }

    @Test
    public void regressionTestHalfPointFree() {
        assertThat(parseExpression(parser, "getAddress().getAddressName().length() == 5").getExpr()).isInstanceOf(BinaryExpr.class);
        assertThat(parseExpression(parser, "isFortyYearsOld(this, true)").getExpr()).isInstanceOf(MethodCallExpr.class);
        assertThat(parseExpression(parser, "getName().startsWith(\"M\")").getExpr()).isInstanceOf(MethodCallExpr.class);
        assertThat(parseExpression(parser, "isPositive($i.intValue())").getExpr()).isInstanceOf(MethodCallExpr.class);
        assertThat(parseExpression(parser, "someEntity.someString in (\"1.500\")").getExpr()).isInstanceOf(PointFreeExpr.class);
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
        assertThat(printNode(expression)).isEqualTo("this str[startsWith] \"M\" || str[startsWith] \"E\"");

        Expression expression2 = parseExpression(parser, "str[startsWith] \"E\"").getExpr();
        assertThat(expression2).isInstanceOf(HalfPointFreeExpr.class);
        assertThat(printNode(expression2)).isEqualTo("str[startsWith] \"E\"");
    }


    @Test
    public void testLambda() {
        String expr = "x -> y";
        DrlxExpression expression = parseExpression(parser, expr);
        assertThat(printNode(expression)).isEqualTo(expr);
    }

    @Test
    public void testLambdaParameter() {
        String expr = "($p).setCanDrinkLambda(() -> true)";
        DrlxExpression expression = parseExpression(parser, expr);
        assertThat(printNode(expression)).isEqualTo(expr);
    }

    @Test
    public void testModifyStatement() {
        String expr = "{ modify ( $p )  { name = \"Luca\", age = \"35\" }; }";

        BlockStmt expression = MvelParser.parseBlock(expr);
        assertThat(printNode(expression)).isEqualTo("{" + newLine() +
                "    modify ($p) { name = \"Luca\", age = \"35\" };" + newLine() +
                "}");
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
        assertThat(printNode(expression)).isEqualTo("{" + newLine() +
                "    modify ($p) { name = \"Luca\" };" + newLine() +
                "}");
    }

    @Test
    public void testModifySemiColon() {
        String expr = "{ modify($p) { setAge(1); }; }";

        BlockStmt expression = MvelParser.parseBlock(expr);
        assertThat(printNode(expression)).isEqualTo("{" + newLine() +
                "    modify ($p) { setAge(1) };" + newLine() +
                "}");
    }

    @Test
    public void testModifyMultiple() {
        String expr = "{ modify($p) { setAge(1)," + newLine() + " setAge(2), setAge(3)," + newLine() + "setAge(4); }; }";

        BlockStmt expression = MvelParser.parseBlock(expr);
        assertThat(printNode(expression)).isEqualTo("{" + newLine() +
                "    modify ($p) { setAge(1), setAge(2), setAge(3), setAge(4) };" + newLine() +
                "}");
    }

    @Test
    public void testModifyEmptyBlock() {
        String expr = "{ modify( $s ) { } }";

        BlockStmt expression = MvelParser.parseBlock(expr);
        assertThat(printNode(expression)).isEqualTo("{" + newLine() +
                "    modify ($s) {  };" + newLine() +
                "}");
    }

    @Test
    public void testModifyWithoutSemicolon() {
        String expr = "{modify($p) { setAge($p.getAge()+1) } }";

        BlockStmt expression = MvelParser.parseBlock(expr);
        assertThat(printNode(expression)).isEqualTo("{" + newLine() +
                "    modify ($p) { setAge($p.getAge() + 1) };" + newLine() +
                "}");
    }


    @Test
    public void testModifyWithCast() {
        String expr = "{modify( (BooleanEvent)$toEdit.get(0) ){  }}";

        BlockStmt expression = MvelParser.parseBlock(expr);
        assertThat(printNode(expression)).isEqualTo("{" + newLine() +
                "    modify ((BooleanEvent) $toEdit.get(0)) {  };" + newLine() +
                "}");
    }
    
    
    @Test
    public void testWithStatement() {
        String expr = "{ with ( $p )  { name = \"Luca\", age = \"35\" }; }";

        BlockStmt expression = MvelParser.parseBlock(expr);
        assertThat(printNode(expression)).isEqualTo("{" + newLine() +
                "    with ($p) { name = \"Luca\", age = \"35\" };" + newLine() +
                "}");
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
        assertThat(printNode(expression)).isEqualTo("{" + newLine() +
                "    with ($p) { name = \"Luca\" };" + newLine() +
                "}");
    }

    @Test
    public void testWithSemiColon() {
        String expr = "{ with($p) { setAge(1); }; }";

        BlockStmt expression = MvelParser.parseBlock(expr);
        assertThat(printNode(expression)).isEqualTo("{" + newLine() +
                "    with ($p) { setAge(1) };" + newLine() +
                "}");
    }

    @Test
    public void testWithEmptyBlock() {
        String expr = "{ with( $s ) { } }";

        BlockStmt expression = MvelParser.parseBlock(expr);
        assertThat(printNode(expression)).isEqualTo("{" + newLine() +
                "    with ($s) {  };" + newLine() +
                "}");
    }

    @Test
    public void testWithWithoutSemicolon() {
        String expr = "{with($p) { setAge($p.getAge()+1) } }";

        BlockStmt expression = MvelParser.parseBlock(expr);
        assertThat(printNode(expression)).isEqualTo("{" + newLine() +
                "    with ($p) { setAge($p.getAge() + 1) };" + newLine() +
                "}");
    }


    @Test
    public void testWithWithCast() {
        String expr = "{with( (BooleanEvent)$toEdit.get(0) ){  }}";

        BlockStmt expression = MvelParser.parseBlock(expr);
        assertThat(printNode(expression)).isEqualTo("{" + newLine() +
                "    with ((BooleanEvent) $toEdit.get(0)) {  };" + newLine() +
                "}");
    }

    @Test
    public void testWithConstructor() {
        String expr = "{ with(s1 = new Some()) { }; }";

        BlockStmt expression = MvelParser.parseBlock(expr);
        assertThat(printNode(expression)).isEqualTo("{" + newLine() +
                "    with (s1 = new Some()) {  };" + newLine() +
                "}");
    }

    @Test
    public void testWithoutSemicolon() {
        String expr = "{             " +
                        "a()" + newLine() +
                        "b()" + newLine() +
                        "}";

        BlockStmt expression = MvelParser.parseBlock(expr);
        assertThat(printNode(expression)).isEqualTo("{" + newLine() +
                "    a();" + newLine() +
                "    b();" + newLine() +
                "}");
    }

    @Test
    public void testWithoutSemicolonMethod() {
        String expr = "{             " +
                "delete($person)" + newLine() +
                "delete($pet)" + newLine() +
                "}";

        BlockStmt expression = MvelParser.parseBlock(expr);
        assertThat(printNode(expression)).isEqualTo("{" + newLine() +
                "    delete($person);" + newLine() +
                "    delete($pet);" + newLine() +
                "}");
    }

    @Test
    public void testWithoutSemicolonMethodComment() {
        String expr = "{             " +
                "delete($person) // comment" + newLine() +
                "delete($pet) // comment " + newLine() +
                "}";

        BlockStmt expression = MvelParser.parseBlock(expr);
        assertThat(printNode(expression)).isEqualTo("{" + newLine() +
                "    delete($person);" + newLine() +
                "    delete($pet);" + newLine() +
                "}");
    }

    @Test
    public void testWithoutSemicolonMethodCommentOppositeOSLineEndings() {
        final String oppositeLineEnding = SystemUtils.IS_OS_WINDOWS ? "\n" : "\r\n";
        String expr = "{             " +
                "delete($person) // comment" + oppositeLineEnding +
                "delete($pet) // comment" + oppositeLineEnding +
                "}";

        BlockStmt expression = MvelParser.parseBlock(expr);
        assertThat(printNode(expression)).isEqualTo("{" + newLine() +
                "    delete($person);" + newLine() +
                "    delete($pet);" + newLine() +
                "}");
    }

    @Test
    public void statementsWithComments() {
        String expr = "{             " +
                "delete($person); // comment" + newLine() +
                "delete($pet); // comment " + newLine() +
                "}";

        BlockStmt expression = MvelParser.parseBlock(expr);
        assertThat(printNode(expression)).isEqualTo("{" + newLine() +
                "    delete($person);" + newLine() +
                "    delete($pet);" + newLine() +
                "}");
    }


    @Test
    public void singleLineBlock() {
        String expr = "{ delete($person); } // comment ";

        BlockStmt expression = MvelParser.parseBlock(expr);
        assertThat(printNode(expression)).isEqualTo("{" + newLine() +
                "    delete($person);" + newLine() +
                "}");
    }

    @Test
    public void singleLineBlockWithoutsemicolon() {
        String expr = "{ delete($person) } // comment";

        BlockStmt expression = MvelParser.parseBlock(expr);
        assertThat(printNode(expression)).isEqualTo("{" + newLine() +
                "    delete($person);" + newLine() +
                "}");
    }

    @Test
    public void commentsWithEmptyStatements() {
        String expr = "{" +
                "// modify ; something" + newLine() +
                "/* modify ; something */" + newLine() +
                "setAge(47)" + newLine() +
                "}";

        BlockStmt expression = MvelParser.parseBlock(expr);
        assertThat(printNode(expression)).isEqualTo("{" + newLine() +
                "    setAge(47);" + newLine() +
                "}");
    }

    @Test
    public void newLineInFunctionCall() {
        String expr = "{" +
                "func(x " + newLine() +
                ")" + newLine() +
                "}";

        BlockStmt expression = MvelParser.parseBlock(expr);
        assertThat(printNode(expression)).isEqualTo("{" + newLine() +
                "    func(x);" + newLine() +
                "}");
    }

    @Test
    public void newLineInFunctionCall2() {
        Expression expression = MvelParser.parseExpression("func(x," + newLine() + " 2)");
        assertThat(printNode(expression)).isEqualTo("func(x, 2)");
    }

    @Test
    public void newLineInFunctionCall3() {
        Expression expression = MvelParser.parseExpression("func(x" + newLine() + ", 2)");
        assertThat(printNode(expression)).isEqualTo("func(x, 2)");
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
        assertThat(printNode(expression)).isEqualTo("{" + newLine() +
                "    globalA.add(\"A\");" + newLine() +
                "    modify ($p) { setAge(47) };" + newLine() +
                "    globalB.add(\"B\");" + newLine() +
                "}");

    }

    @Test
    public void testModifyLambda() {
        String expr = "{  modify($p) {  setCanDrinkLambda(() -> true); } }";
        BlockStmt expression = MvelParser.parseBlock(expr);
        assertThat(printNode(expression)).isEqualTo("{" + newLine() +
                "    modify ($p) { setCanDrinkLambda(() -> true) };" + newLine() +
                "}");
    }

    @Test
    public void testNewExpression() {
        String expr = "money == new BigInteger(\"3\")";

        Expression expression = parseExpression(parser, expr).getExpr();
        assertThat(printNode(expression)).isEqualTo(expr);
    }

    @Test
    public void testArrayCreation() {
        String expr = "new Object[] { \"getMessageId\", ($s != null ? $s : \"42103\") }";

        Expression expression = parseExpression(parser, expr).getExpr();
        assertThat(printNode(expression)).isEqualTo(expr);
    }


    @Test
    public void testArrayCreation2() {
    String expr = "functions.arrayContainsInstanceWithParameters((Object[]) $f.getPersons())";

        Expression expression = parseExpression(parser, expr).getExpr();
        assertThat(printNode(expression)).isEqualTo(expr);
    }

    @Test
    public void testSpecialNewlineHandling() {
        String expr = "{ a() \nprint(1) }";

        assertThat(MvelParser.parseBlock(expr).getStatements().size()).as("There should be 2 statements").isEqualTo(2);

        MvelParser mvelParser = new MvelParser(new ParserConfiguration(), false);
        ParseResult<BlockStmt> r = mvelParser.parse(GeneratedMvelParser::BlockParseStart, new StringProvider(expr));
        assertThat(r.isSuccessful()).as("Parsing should break at newline").isFalse();
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
        assertThat(blockStmt.getStatements().size()).as("Should parse 3 statements").isEqualTo(3);
    }

    private void testMvelSquareOperator(String wholeExpression, String operator, String left, String right, boolean isNegated) {
        String expr = wholeExpression;
        Expression expression = parseExpression(parser, expr ).getExpr();
        assertThat(expression).isInstanceOf(PointFreeExpr.class);
        assertThat(printNode(expression)).isEqualTo(wholeExpression);
        PointFreeExpr e = (PointFreeExpr)expression;
        assertThat(e.getOperator().asString()).isEqualTo(operator);
        assertThat(toString(e.getLeft())).isEqualTo(left);
        assertThat(toString(e.getRight().get(0))).isEqualTo(right);
        assertThat(e.isNegated()).isEqualTo(isNegated);
    }

    private String toString(Node n) {
        return PrintUtil.printNode(n);
    }

    private String newLine() {
        return System.lineSeparator();
    }

    @Test
    public void testBindVariable() {
        String expr = "$n : name == \"Mark\"";
        DrlxExpression drlxExpression = parseExpression( parser, expr );
        SimpleName bind = drlxExpression.getBind();
        assertThat(bind.asString()).isEqualTo("$n");

        Expression expression = drlxExpression.getExpr();
        BinaryExpr binaryExpr = ( (BinaryExpr) expression );
        assertThat(toString(binaryExpr.getLeft())).isEqualTo("name");
        assertThat(toString(binaryExpr.getRight())).isEqualTo("\"Mark\"");
        assertThat(binaryExpr.getOperator()).isEqualTo(Operator.EQUALS);
    }

    @Test
    public void testEnclosedBindVariable() {
        String expr = "($n : name == \"Mario\")";

        DrlxExpression drlxExpression = parseExpression(parser, expr);
        Expression enclosedExpr = drlxExpression.getExpr();
        assertThat(enclosedExpr instanceof EnclosedExpr).isTrue();
        Expression inner = ((EnclosedExpr) enclosedExpr).getInner();
        assertThat(inner instanceof DrlxExpression).isTrue();
        DrlxExpression innerDrlxExpression = (DrlxExpression) inner;

        SimpleName bind = innerDrlxExpression.getBind();
        assertThat(bind.asString()).isEqualTo("$n");

        Expression expression = innerDrlxExpression.getExpr();
        BinaryExpr binaryExpr = ((BinaryExpr) expression);
        assertThat(toString(binaryExpr.getLeft())).isEqualTo("name");
        assertThat(toString(binaryExpr.getRight())).isEqualTo("\"Mario\"");
        assertThat(binaryExpr.getOperator()).isEqualTo(Operator.EQUALS);
    }

    @Test
    public void testComplexEnclosedBindVariable() {
        String expr = "($n : name == \"Mario\") && (age > 20)";

        DrlxExpression drlxExpression = parseExpression(parser, expr);
        Expression bExpr = drlxExpression.getExpr();
        assertThat(bExpr instanceof BinaryExpr).isTrue();

        Node left = ((BinaryExpr) bExpr).getLeft();
        assertThat(left instanceof EnclosedExpr).isTrue();
        Expression inner = ((EnclosedExpr) left).getInner();
        assertThat(inner instanceof DrlxExpression).isTrue();
        DrlxExpression innerDrlxExpression = (DrlxExpression) inner;

        SimpleName bind = innerDrlxExpression.getBind();
        assertThat(bind.asString()).isEqualTo("$n");

        Expression expression = innerDrlxExpression.getExpr();
        BinaryExpr binaryExpr = ((BinaryExpr) expression);
        assertThat(toString(binaryExpr.getLeft())).isEqualTo("name");
        assertThat(toString(binaryExpr.getRight())).isEqualTo("\"Mario\"");
        assertThat(binaryExpr.getOperator()).isEqualTo(Operator.EQUALS);

        Node right = ((BinaryExpr) bExpr).getRight();
        assertThat(right instanceof EnclosedExpr).isTrue();
        Expression expression2 = ((EnclosedExpr) right).getInner();

        BinaryExpr binaryExpr2 = ((BinaryExpr) expression2);
        assertThat(toString(binaryExpr2.getLeft())).isEqualTo("age");
        assertThat(toString(binaryExpr2.getRight())).isEqualTo("20");
        assertThat(binaryExpr2.getOperator()).isEqualTo(Operator.GREATER);
    }

    @Test
    public void testBindingOnRight() {
        String expr = "$n : name == \"Mario\" && $a : age > 20";

        DrlxExpression drlxExpression = parseExpression(parser, expr);
        Expression bExpr = drlxExpression.getExpr();
        assertThat(bExpr instanceof BinaryExpr).isTrue();

        Node left = ((BinaryExpr) bExpr).getLeft();
        assertThat(left instanceof DrlxExpression).isTrue();
        DrlxExpression leftExpr = (DrlxExpression) left;

        SimpleName leftBind = leftExpr.getBind();
        assertThat(leftBind.asString()).isEqualTo("$n");

        Expression expression = leftExpr.getExpr();
        BinaryExpr binaryExpr = ((BinaryExpr) expression);
        assertThat(toString(binaryExpr.getLeft())).isEqualTo("name");
        assertThat(toString(binaryExpr.getRight())).isEqualTo("\"Mario\"");
        assertThat(binaryExpr.getOperator()).isEqualTo(Operator.EQUALS);

        Node right = ((BinaryExpr) bExpr).getRight();
        assertThat(right instanceof DrlxExpression).isTrue();
        DrlxExpression rightExpr = (DrlxExpression) right;

        SimpleName rightBind = rightExpr.getBind();
        assertThat(rightBind.asString()).isEqualTo("$a");

        BinaryExpr binaryExpr2 = ((BinaryExpr) rightExpr.getExpr());
        assertThat(toString(binaryExpr2.getLeft())).isEqualTo("age");
        assertThat(toString(binaryExpr2.getRight())).isEqualTo("20");
        assertThat(binaryExpr2.getOperator()).isEqualTo(Operator.GREATER);
    }

    @Test
    public void test3BindingOn3Conditions() {
        String expr = "$n : name == \"Mario\" && $a : age > 20 && $l : likes != null";

        DrlxExpression drlxExpression = parseExpression(parser, expr);
        Expression bExpr = drlxExpression.getExpr();
        assertThat(bExpr instanceof BinaryExpr).isTrue();

        Expression left = ((BinaryExpr) bExpr).getLeft();
        assertThat(left instanceof BinaryExpr).isTrue();
        BinaryExpr leftExpr = (BinaryExpr) left;

        DrlxExpression first = (DrlxExpression) leftExpr.getLeft();
        DrlxExpression second = (DrlxExpression) leftExpr.getRight();
        DrlxExpression third = (DrlxExpression) ((BinaryExpr) bExpr).getRight();

        SimpleName bind = first.getBind();
        assertThat(bind.asString()).isEqualTo("$n");
        BinaryExpr binaryExpr = ((BinaryExpr) first.getExpr());
        assertThat(toString(binaryExpr.getLeft())).isEqualTo("name");
        assertThat(toString(binaryExpr.getRight())).isEqualTo("\"Mario\"");
        assertThat(binaryExpr.getOperator()).isEqualTo(Operator.EQUALS);

        bind = second.getBind();
        assertThat(bind.asString()).isEqualTo("$a");
        binaryExpr = ((BinaryExpr) second.getExpr());
        assertThat(toString(binaryExpr.getLeft())).isEqualTo("age");
        assertThat(toString(binaryExpr.getRight())).isEqualTo("20");
        assertThat(binaryExpr.getOperator()).isEqualTo(Operator.GREATER);

        bind = third.getBind();
        assertThat(bind.asString()).isEqualTo("$l");
        binaryExpr = ((BinaryExpr) third.getExpr());
        assertThat(toString(binaryExpr.getLeft())).isEqualTo("likes");
        assertThat(toString(binaryExpr.getRight())).isEqualTo("null");
        assertThat(binaryExpr.getOperator()).isEqualTo(Operator.NOT_EQUALS);
    }

    @Test
    public void testBindingOnRightWithOr() {
        String expr = "$n : name == \"Mario\" || $a : age > 20";

        DrlxExpression drlxExpression = parseExpression(parser, expr);
        Expression bExpr = drlxExpression.getExpr();
        assertThat(bExpr instanceof BinaryExpr).isTrue();
        assertThat(((BinaryExpr) bExpr).getOperator() == BinaryExpr.Operator.OR).isTrue();

        Node left = ((BinaryExpr) bExpr).getLeft();
        assertThat(left instanceof DrlxExpression).isTrue();
        DrlxExpression leftExpr = (DrlxExpression) left;

        SimpleName leftBind = leftExpr.getBind();
        assertThat(leftBind.asString()).isEqualTo("$n");

        Expression expression = leftExpr.getExpr();
        BinaryExpr binaryExpr = ((BinaryExpr) expression);
        assertThat(toString(binaryExpr.getLeft())).isEqualTo("name");
        assertThat(toString(binaryExpr.getRight())).isEqualTo("\"Mario\"");
        assertThat(binaryExpr.getOperator()).isEqualTo(Operator.EQUALS);

        Node right = ((BinaryExpr) bExpr).getRight();
        assertThat(right instanceof DrlxExpression).isTrue();
        DrlxExpression rightExpr = (DrlxExpression) right;

        SimpleName rightBind = rightExpr.getBind();
        assertThat(rightBind.asString()).isEqualTo("$a");

        BinaryExpr binaryExpr2 = ((BinaryExpr) rightExpr.getExpr());
        assertThat(toString(binaryExpr2.getLeft())).isEqualTo("age");
        assertThat(toString(binaryExpr2.getRight())).isEqualTo("20");
        assertThat(binaryExpr2.getOperator()).isEqualTo(Operator.GREATER);
    }

    @Test
    public void test3BindingOn3ConditionsWithOrAnd() {
        String expr = "$n : name == \"Mario\" || $a : age > 20 && $l : likes != null";

        DrlxExpression drlxExpression = parseExpression(parser, expr);
        Expression bExpr = drlxExpression.getExpr();
        assertThat(bExpr instanceof BinaryExpr).isTrue();
        assertThat(((BinaryExpr) bExpr).getOperator() == BinaryExpr.Operator.OR).isTrue();

        Expression left = ((BinaryExpr) bExpr).getLeft();
        assertThat(left instanceof DrlxExpression).isTrue();

        Expression right = ((BinaryExpr) bExpr).getRight();
        assertThat(right instanceof BinaryExpr).isTrue();
        BinaryExpr rightExpr = (BinaryExpr) right;
        assertThat(rightExpr.getOperator() == BinaryExpr.Operator.AND).isTrue();

        DrlxExpression first = (DrlxExpression) left;
        DrlxExpression second = (DrlxExpression) rightExpr.getLeft();
        DrlxExpression third = (DrlxExpression) rightExpr.getRight();

        SimpleName bind = first.getBind();
        assertThat(bind.asString()).isEqualTo("$n");
        BinaryExpr binaryExpr = ((BinaryExpr) first.getExpr());
        assertThat(toString(binaryExpr.getLeft())).isEqualTo("name");
        assertThat(toString(binaryExpr.getRight())).isEqualTo("\"Mario\"");
        assertThat(binaryExpr.getOperator()).isEqualTo(Operator.EQUALS);

        bind = second.getBind();
        assertThat(bind.asString()).isEqualTo("$a");
        binaryExpr = ((BinaryExpr) second.getExpr());
        assertThat(toString(binaryExpr.getLeft())).isEqualTo("age");
        assertThat(toString(binaryExpr.getRight())).isEqualTo("20");
        assertThat(binaryExpr.getOperator()).isEqualTo(Operator.GREATER);

        bind = third.getBind();
        assertThat(bind.asString()).isEqualTo("$l");
        binaryExpr = ((BinaryExpr) third.getExpr());
        assertThat(toString(binaryExpr.getLeft())).isEqualTo("likes");
        assertThat(toString(binaryExpr.getRight())).isEqualTo("null");
        assertThat(binaryExpr.getOperator()).isEqualTo(Operator.NOT_EQUALS);
    }
}
