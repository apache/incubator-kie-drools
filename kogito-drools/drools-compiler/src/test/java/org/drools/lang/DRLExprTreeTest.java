/*
 * Copyright 2011 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.lang;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import junit.framework.TestCase;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CharStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognizerSharedState;
import org.antlr.runtime.tree.CommonTreeNodeStream;
import org.drools.base.evaluators.EvaluatorRegistry;
import org.drools.compiler.DrlExprParser;
import org.drools.lang.descr.AtomicExprDescr;
import org.drools.lang.descr.ConnectiveType;
import org.drools.lang.descr.ConstraintConnectiveDescr;
import org.drools.lang.descr.RelationalExprDescr;

/**
 * DRLExprTreeTest
 */
public class DRLExprTreeTest extends TestCase {

    DrlExprParser parser;

    protected void setUp() throws Exception {
        super.setUp();
        new EvaluatorRegistry();
        this.parser = new DrlExprParser();
    }

    protected void tearDown() throws Exception {
        this.parser = null;
        super.tearDown();
    }

    public void testSimpleExpression() throws Exception {
        String source = "a > b";
        ConstraintConnectiveDescr result = parser.parse( source );
        assertFalse( parser.getErrors().toString(),
                     parser.hasErrors() );

        assertEquals( ConnectiveType.AND,
                      result.getConnective() );
        assertEquals( 1,
                      result.getDescrs().size() );

        RelationalExprDescr expr = (RelationalExprDescr) result.getDescrs().get( 0 );
        assertEquals( ">",
                      expr.getOperator() );

        AtomicExprDescr left = (AtomicExprDescr) expr.getLeft();
        AtomicExprDescr right = (AtomicExprDescr) expr.getRight();

        assertEquals( "a",
                      left.getExpression() );
        assertEquals( "b",
                      right.getExpression() );
    }

    public void testAndConnective() throws Exception {
        String source = "a > b && 10 != 20";
        ConstraintConnectiveDescr result = parser.parse( source );
        assertFalse( parser.getErrors().toString(),
                     parser.hasErrors() );

        assertEquals( ConnectiveType.AND,
                      result.getConnective() );
        assertEquals( 2,
                      result.getDescrs().size() );

        RelationalExprDescr expr = (RelationalExprDescr) result.getDescrs().get( 0 );
        assertEquals( ">",
                      expr.getOperator() );
        AtomicExprDescr left = (AtomicExprDescr) expr.getLeft();
        AtomicExprDescr right = (AtomicExprDescr) expr.getRight();
        assertEquals( "a",
                      left.getExpression() );
        assertEquals( "b",
                      right.getExpression() );

        expr = (RelationalExprDescr) result.getDescrs().get( 1 );
        assertEquals( "!=",
                      expr.getOperator() );
        left = (AtomicExprDescr) expr.getLeft();
        right = (AtomicExprDescr) expr.getRight();
        assertEquals( "10",
                      left.getExpression() );
        assertEquals( "20",
                      right.getExpression() );
    }

    public void testConnective2() throws Exception {
        String source = "(a > b || 10 != 20) && someMethod(10) == 20";
        ConstraintConnectiveDescr result = parser.parse( source );
        assertFalse( parser.getErrors().toString(),
                     parser.hasErrors() );

        assertEquals( ConnectiveType.AND,
                      result.getConnective() );
        assertEquals( 2,
                      result.getDescrs().size() );

        ConstraintConnectiveDescr or = (ConstraintConnectiveDescr) result.getDescrs().get( 0 );
        assertEquals( ConnectiveType.OR,
                      or.getConnective() );
        assertEquals( 2,
                      or.getDescrs().size() );
        
        RelationalExprDescr expr = (RelationalExprDescr) or.getDescrs().get( 0 );
        assertEquals( ">",
                      expr.getOperator() );
        AtomicExprDescr left = (AtomicExprDescr) expr.getLeft();
        AtomicExprDescr right = (AtomicExprDescr) expr.getRight();
        assertEquals( "a",
                      left.getExpression() );
        assertEquals( "b",
                      right.getExpression() );

        expr = (RelationalExprDescr) or.getDescrs().get( 1 );
        assertEquals( "!=",
                      expr.getOperator() );
        left = (AtomicExprDescr) expr.getLeft();
        right = (AtomicExprDescr) expr.getRight();
        assertEquals( "10",
                      left.getExpression() );
        assertEquals( "20",
                      right.getExpression() );

        expr = (RelationalExprDescr) result.getDescrs().get( 1 );
        assertEquals( "==",
                      expr.getOperator() );
        left = (AtomicExprDescr) expr.getLeft();
        right = (AtomicExprDescr) expr.getRight();
        assertEquals( "someMethod(10)",
                      left.getExpression() );
        assertEquals( "20",
                      right.getExpression() );

    }

}
