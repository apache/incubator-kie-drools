package org.drools.modelcompiler.builder.generator;

import org.apache.commons.math3.analysis.function.Exp;
import org.drools.javaparser.JavaParser;
import org.drools.javaparser.ast.expr.Expression;
import org.drools.javaparser.ast.expr.MethodCallExpr;
import org.drools.javaparser.ast.expr.NameExpr;
import org.drools.modelcompiler.Person;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.*;

public class DrlxParseUtilTest {

    @Test
    public void prependTest() {

        final Expression expr = JavaParser.parseExpression("getName().startsWith(\"M\")");
        final NameExpr nameExpr = new NameExpr("_this");


        final Expression concatenated = DrlxParseUtil.prepend(nameExpr, expr);

        assertEquals("_this.getName().startsWith(\"M\")", concatenated.toString());

    }

    @Test(expected = UnsupportedOperationException.class)
    public void throwExceptionWhenMissingNode() {

        final Expression expr = JavaParser.parseExpression("this");

        DrlxParseUtil.prepend(null, expr);

    }

    @Test
    @Ignore
    public void transformMethodExpressionToMethodCallExpression() {

        final Expression expr = JavaParser.parseExpression("address.name.startsWith(\"M\")");
        final Expression expr1 = JavaParser.parseExpression("getAddress().name.startsWith(\"M\")");
        final Expression expr2 = JavaParser.parseExpression("address.getName().startsWith(\"M\")");

        final MethodCallExpr expected = JavaParser.parseExpression("getName().getAddress().startsWith(\"M\")");

        assertEquals(expected, DrlxParseUtil.toMethodCall(expr, Person.class));
        assertEquals(expected, DrlxParseUtil.toMethodCall(expr1, Person.class));
        assertEquals(expected, DrlxParseUtil.toMethodCall(expr2, Person.class));
    }


}