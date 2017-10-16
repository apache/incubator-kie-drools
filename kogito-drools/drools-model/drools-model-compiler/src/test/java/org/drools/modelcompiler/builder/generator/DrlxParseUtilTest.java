package org.drools.modelcompiler.builder.generator;

import org.drools.javaparser.JavaParser;
import org.drools.javaparser.ast.expr.Expression;
import org.drools.javaparser.ast.expr.MethodCallExpr;
import org.drools.javaparser.ast.expr.NameExpr;
import org.drools.modelcompiler.domain.Person;
import org.junit.Test;

import static org.junit.Assert.*;

public class DrlxParseUtilTest {

    @Test
    public void prependTest() {

        final Expression expr = JavaParser.parseExpression("getAddressName().startsWith(\"M\")");
        final NameExpr nameExpr = new NameExpr("_this");


        final Expression concatenated = DrlxParseUtil.prepend(nameExpr, expr);

        assertEquals("_this.getAddressName().startsWith(\"M\")", concatenated.toString());

    }

    @Test(expected = UnsupportedOperationException.class)
    public void throwExceptionWhenMissingNode() {

        final Expression expr = JavaParser.parseExpression("this");

        DrlxParseUtil.prepend(null, expr);

    }

    @Test
    public void transformMethodExpressionToMethodCallExpressionTypeSafe() {

        final Expression expr = JavaParser.parseExpression("address.addressName.startsWith(\"M\")");
        final Expression expr1 = JavaParser.parseExpression("getAddress().addressName.startsWith(\"M\")");
        final Expression expr2 = JavaParser.parseExpression("address.getAddressName().startsWith(\"M\")");

        final MethodCallExpr expected = JavaParser.parseExpression("getAddress().getAddressName().startsWith(\"M\")");

        assertEquals(expected.toString(), DrlxParseUtil.toMethodCallWithClassCheck(expr, Person.class).getExpression().toString());
        assertEquals(expected.toString(), DrlxParseUtil.toMethodCallWithClassCheck(expr1, Person.class).getExpression().toString());
        assertEquals(expected.toString(), DrlxParseUtil.toMethodCallWithClassCheck(expr2, Person.class).getExpression().toString());
    }


}