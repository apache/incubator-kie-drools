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
package org.drools.mvel.compiler.lang;

import org.drools.compiler.builder.impl.EvaluatorRegistry;
import org.drools.drl.ast.descr.AtomicExprDescr;
import org.drools.drl.ast.descr.BindingDescr;
import org.drools.drl.ast.descr.ConnectiveType;
import org.drools.drl.ast.descr.ConstraintConnectiveDescr;
import org.drools.drl.ast.descr.RelationalExprDescr;
import org.drools.drl.parser.DrlExprParser;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kie.internal.builder.conf.LanguageLevelOption;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * DRLExprTreeTest
 */
public class DRLExprParserTest {

    DrlExprParser parser;

    @Before
    public void setUp() throws Exception {
        new EvaluatorRegistry();
        this.parser = new DrlExprParser(LanguageLevelOption.DRL6);
    }

    @After
    public void tearDown() throws Exception {
        this.parser = null;
    }

    @Test
    public void testSimpleExpression() throws Exception {
        String source = "a > b";
        ConstraintConnectiveDescr result = parser.parse( source );
        assertThat(parser.hasErrors()).as(parser.getErrors().toString()).isFalse();

        assertThat(result.getConnective()).isEqualTo(ConnectiveType.AND);
        assertThat(result.getDescrs().size()).isEqualTo(1);

        RelationalExprDescr expr = (RelationalExprDescr) result.getDescrs().get( 0 );
        assertThat(expr.getOperator()).isEqualTo(">");

        AtomicExprDescr left = (AtomicExprDescr) expr.getLeft();
        AtomicExprDescr right = (AtomicExprDescr) expr.getRight();

        assertThat(left.getExpression()).isEqualTo("a");
        assertThat(right.getExpression()).isEqualTo("b");
    }

    @Test
    public void testAndConnective() throws Exception {
        String source = "a > b && 10 != 20";
        ConstraintConnectiveDescr result = parser.parse( source );
        assertThat(parser.hasErrors()).as(parser.getErrors().toString()).isFalse();

        assertThat(result.getConnective()).isEqualTo(ConnectiveType.AND);
        assertThat(result.getDescrs().size()).isEqualTo(2);

        RelationalExprDescr expr = (RelationalExprDescr) result.getDescrs().get( 0 );
        assertThat(expr.getOperator()).isEqualTo(">");
        AtomicExprDescr left = (AtomicExprDescr) expr.getLeft();
        AtomicExprDescr right = (AtomicExprDescr) expr.getRight();
        assertThat(left.getExpression()).isEqualTo("a");
        assertThat(right.getExpression()).isEqualTo("b");

        expr = (RelationalExprDescr) result.getDescrs().get( 1 );
        assertThat(expr.getOperator()).isEqualTo("!=");
        left = (AtomicExprDescr) expr.getLeft();
        right = (AtomicExprDescr) expr.getRight();
        assertThat(left.getExpression()).isEqualTo("10");
        assertThat(right.getExpression()).isEqualTo("20");
    }

    @Test
    public void testConnective2() throws Exception {
        String source = "(a > b || 10 != 20) && someMethod(10) == 20";
        ConstraintConnectiveDescr result = parser.parse( source );
        assertThat(parser.hasErrors()).as(parser.getErrors().toString()).isFalse();

        assertThat(result.getConnective()).isEqualTo(ConnectiveType.AND);
        assertThat(result.getDescrs().size()).isEqualTo(2);

        ConstraintConnectiveDescr or = (ConstraintConnectiveDescr) result.getDescrs().get( 0 );
        assertThat(or.getConnective()).isEqualTo(ConnectiveType.OR);
        assertThat(or.getDescrs().size()).isEqualTo(2);

        RelationalExprDescr expr = (RelationalExprDescr) or.getDescrs().get( 0 );
        assertThat(expr.getOperator()).isEqualTo(">");
        AtomicExprDescr left = (AtomicExprDescr) expr.getLeft();
        AtomicExprDescr right = (AtomicExprDescr) expr.getRight();
        assertThat(left.getExpression()).isEqualTo("a");
        assertThat(right.getExpression()).isEqualTo("b");

        expr = (RelationalExprDescr) or.getDescrs().get( 1 );
        assertThat(expr.getOperator()).isEqualTo("!=");
        left = (AtomicExprDescr) expr.getLeft();
        right = (AtomicExprDescr) expr.getRight();
        assertThat(left.getExpression()).isEqualTo("10");
        assertThat(right.getExpression()).isEqualTo("20");

        expr = (RelationalExprDescr) result.getDescrs().get( 1 );
        assertThat(expr.getOperator()).isEqualTo("==");
        left = (AtomicExprDescr) expr.getLeft();
        right = (AtomicExprDescr) expr.getRight();
        assertThat(left.getExpression()).isEqualTo("someMethod(10)");
        assertThat(right.getExpression()).isEqualTo("20");

    }

    @Test
    public void testBinding() throws Exception {
        String source = "$x : property";
        ConstraintConnectiveDescr result = parser.parse( source );
        assertThat(parser.hasErrors()).as(parser.getErrors().toString()).isFalse();

        assertThat(result.getConnective()).isEqualTo(ConnectiveType.AND);
        assertThat(result.getDescrs().size()).isEqualTo(1);

        BindingDescr bind = (BindingDescr) result.getDescrs().get( 0 );
        assertThat(bind.getVariable()).isEqualTo("$x");
        assertThat(bind.getExpression()).isEqualTo("property");
    }

    @Test
    public void testBindingConstraint() throws Exception {
        String source = "$x : property > value";
        ConstraintConnectiveDescr result = parser.parse( source );
        assertThat(parser.hasErrors()).as(parser.getErrors().toString()).isFalse();

        assertThat(result.getConnective()).isEqualTo(ConnectiveType.AND);
        assertThat(result.getDescrs().size()).isEqualTo(1);

        RelationalExprDescr rel = (RelationalExprDescr) result.getDescrs().get( 0 );
        assertThat(rel.getOperator()).isEqualTo(">");

        BindingDescr bind = (BindingDescr) rel.getLeft();
        assertThat(bind.getVariable()).isEqualTo("$x");
        assertThat(bind.getExpression()).isEqualTo("property");

        AtomicExprDescr right = (AtomicExprDescr) rel.getRight();
        assertThat(right.getExpression()).isEqualTo("value");
    }

    @Test
    public void testBindingWithRestrictions() throws Exception {
        String source = "$x : property > value && < 20";
        ConstraintConnectiveDescr result = parser.parse( source );
        assertThat(parser.hasErrors()).as(parser.getErrors().toString()).isFalse();

        assertThat(result.getConnective()).isEqualTo(ConnectiveType.AND);
        assertThat(result.getDescrs().size()).isEqualTo(2);

        RelationalExprDescr rel = (RelationalExprDescr) result.getDescrs().get( 0 );
        assertThat(rel.getOperator()).isEqualTo(">");

        BindingDescr bind = (BindingDescr) rel.getLeft();
        assertThat(bind.getVariable()).isEqualTo("$x");
        assertThat(bind.getExpression()).isEqualTo("property");

        AtomicExprDescr right = (AtomicExprDescr) rel.getRight();
        assertThat(right.getExpression()).isEqualTo("value");
        
        rel = (RelationalExprDescr) result.getDescrs().get( 1 );
        assertThat(rel.getOperator()).isEqualTo("<");

        AtomicExprDescr left = (AtomicExprDescr) rel.getLeft();
        assertThat(left.getExpression()).isEqualTo("property");

        right = (AtomicExprDescr) rel.getRight();
        assertThat(right.getExpression()).isEqualTo("20");
    }

    @Test
    public void testDoubleBinding() throws Exception {
        String source = "$x : x.m( 1, a ) && $y : y[z].foo";
        ConstraintConnectiveDescr result = parser.parse( source );
        assertThat(parser.hasErrors()).as(parser.getErrors().toString()).isFalse();

        assertThat(result.getConnective()).isEqualTo(ConnectiveType.AND);
        assertThat(result.getDescrs().size()).isEqualTo(2);

        BindingDescr bind = (BindingDescr) result.getDescrs().get( 0 );
        assertThat(bind.getVariable()).isEqualTo("$x");
        assertThat(bind.getExpression()).isEqualTo("x.m( 1, a )");

        bind = (BindingDescr) result.getDescrs().get( 1 );
        assertThat(bind.getVariable()).isEqualTo("$y");
        assertThat(bind.getExpression()).isEqualTo("y[z].foo");
    }

    @Test
    public void testDeepBinding() throws Exception {
        String source = "($a : a > $b : b[10].prop || 10 != 20) && $x : someMethod(10) == 20";
        ConstraintConnectiveDescr result = parser.parse( source );
        assertThat(parser.hasErrors()).as(parser.getErrors().toString()).isFalse();

        assertThat(result.getConnective()).isEqualTo(ConnectiveType.AND);
        assertThat(result.getDescrs().size()).isEqualTo(2);

        ConstraintConnectiveDescr or = (ConstraintConnectiveDescr) result.getDescrs().get( 0 );
        assertThat(or.getConnective()).isEqualTo(ConnectiveType.OR);
        assertThat(or.getDescrs().size()).isEqualTo(2);

        RelationalExprDescr expr = (RelationalExprDescr) or.getDescrs().get( 0 );
        assertThat(expr.getOperator()).isEqualTo(">");
        BindingDescr leftBind = (BindingDescr) expr.getLeft();
        BindingDescr rightBind = (BindingDescr) expr.getRight();
        assertThat(leftBind.getVariable()).isEqualTo("$a");
        assertThat(leftBind.getExpression()).isEqualTo("a");
        assertThat(rightBind.getVariable()).isEqualTo("$b");
        assertThat(rightBind.getExpression()).isEqualTo("b[10].prop");

        expr = (RelationalExprDescr) or.getDescrs().get( 1 );
        assertThat(expr.getOperator()).isEqualTo("!=");
        AtomicExprDescr leftExpr = (AtomicExprDescr) expr.getLeft();
        AtomicExprDescr rightExpr = (AtomicExprDescr) expr.getRight();
        assertThat(leftExpr.getExpression()).isEqualTo("10");
        assertThat(rightExpr.getExpression()).isEqualTo("20");

        expr = (RelationalExprDescr) result.getDescrs().get( 1 );
        assertThat(expr.getOperator()).isEqualTo("==");
        leftBind = (BindingDescr) expr.getLeft();
        rightExpr = (AtomicExprDescr) expr.getRight();
        assertThat(leftBind.getVariable()).isEqualTo("$x");
        assertThat(leftBind.getExpression()).isEqualTo("someMethod(10)");
        assertThat(rightExpr.getExpression()).isEqualTo("20");

    }

    @Test(timeout = 10000L)
    public void testNestedExpression() throws Exception {
        // DROOLS-982
        String source = "(((((((((((((((((((((((((((((((((((((((((((((((((( a > b ))))))))))))))))))))))))))))))))))))))))))))))))))";
        ConstraintConnectiveDescr result = parser.parse( source );
        assertThat(parser.hasErrors()).as(parser.getErrors().toString()).isFalse();

        assertThat(result.getConnective()).isEqualTo(ConnectiveType.AND);
        assertThat(result.getDescrs().size()).isEqualTo(1);

        RelationalExprDescr expr = (RelationalExprDescr) result.getDescrs().get( 0 );
        assertThat(expr.getOperator()).isEqualTo(">");

        AtomicExprDescr left = (AtomicExprDescr) expr.getLeft();
        AtomicExprDescr right = (AtomicExprDescr) expr.getRight();

        assertThat(left.getExpression()).isEqualTo("a");
        assertThat(right.getExpression()).isEqualTo("b");
    }
}
