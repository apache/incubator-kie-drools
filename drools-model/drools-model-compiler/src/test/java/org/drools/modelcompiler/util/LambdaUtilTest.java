package org.drools.modelcompiler.util;

import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.LambdaExpr;
import org.junit.Test;

import static com.github.javaparser.StaticJavaParser.parseClassOrInterfaceType;
import static com.github.javaparser.StaticJavaParser.parseExpression;
import static org.junit.Assert.*;

public class LambdaUtilTest {

    @Test
    public void compose() {

        LambdaExpr l1 = parseExpression("(_this) -> _this.getTimeFieldAsDate()");
        LambdaExpr l2 = parseExpression("(_this) -> _this.getTime()");

        Expression expected = parseExpression("((org.drools.model.functions.Function1<StockTick, Date>)((_this) -> _this.getTimeFieldAsDate())).andThen((_this) -> _this.getTime())");

        Expression actual = LambdaUtil.compose(l1, l2, parseClassOrInterfaceType("StockTick"), parseClassOrInterfaceType("Date"));
        assertEquals(expected.toString(), actual.toString());
    }
}