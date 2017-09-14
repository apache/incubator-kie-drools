package org.drools.modelcompiler.builder.generator;

import org.drools.javaparser.JavaParser;
import org.drools.javaparser.ast.expr.MethodCallExpr;
import org.drools.javaparser.ast.expr.NameExpr;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class DrlxParseUtilTest {

    @Test
    public void prependTest() {

        final MethodCallExpr expr = JavaParser.parseExpression("getName().startsWith(\"M\")");
        final NameExpr nameExpr = new NameExpr("_this");


        final MethodCallExpr concatenated = DrlxParseUtil.preprendNameExprToMethodCallExpr(nameExpr, expr);

        assertEquals("_this.getName().startsWith(\"M\")", concatenated.toString());

    }


}