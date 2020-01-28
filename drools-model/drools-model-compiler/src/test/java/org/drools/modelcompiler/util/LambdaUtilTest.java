package org.drools.modelcompiler.util;

import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.LambdaExpr;
import org.junit.Test;

import static com.github.javaparser.StaticJavaParser.parseExpression;
import static org.junit.Assert.*;

public class LambdaUtilTest {

    @Test
    public void appendaLambdaToOld() {

        LambdaExpr l1 = parseExpression("(_this) -> _this.getTimeFieldAsDate()");
        LambdaExpr l2 = parseExpression("(_this) -> _this.getTime()");

        Expression expected = parseExpression("(_this) -> _this.getTimeFieldAsDate().getTime()");

        Expression actual = LambdaUtil.appendNewLambdaToOld(l1, l2);
        assertEquals(expected.toString(), actual.toString());
    }

    @Test
    public void appendTwoMethodsToLambda() {

        LambdaExpr l1 = parseExpression("(_this) -> _this.getDueDate()");
        LambdaExpr l2 = parseExpression("(_this) -> _this.getTime().getTime()");

        Expression expected = parseExpression("(_this) -> _this.getDueDate().getTime().getTime()");

        Expression actual = LambdaUtil.appendNewLambdaToOld(l1, l2);
        assertEquals(expected.toString(), actual.toString());
    }
}