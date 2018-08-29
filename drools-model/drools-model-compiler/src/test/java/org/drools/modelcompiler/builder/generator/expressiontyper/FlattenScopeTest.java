package org.drools.modelcompiler.builder.generator.expressiontyper;

import java.util.List;

import org.drools.javaparser.ast.Node;
import org.drools.javaparser.ast.expr.Expression;
import org.drools.javaparser.ast.expr.NameExpr;
import org.drools.javaparser.ast.expr.SimpleName;
import org.drools.modelcompiler.builder.generator.DrlxParseUtil;
import org.junit.Test;

import static java.util.Arrays.asList;
import static org.drools.modelcompiler.builder.generator.expressiontyper.FlattenScope.flattenScope;
import static org.junit.Assert.*;

public class FlattenScopeTest {


    @Test
    public void flattenFields() {
        List<Node> actual = flattenScope(expr("Field.INT"));
        List<Node> expected = asList(new NameExpr("Field"), new SimpleName("INT"));

        assertArrayEquals(expected.toArray(), actual.toArray());
    }

    private Expression expr(String inputExpr) {
        return DrlxParseUtil.parseExpression(inputExpr).getExpr();
    }
}