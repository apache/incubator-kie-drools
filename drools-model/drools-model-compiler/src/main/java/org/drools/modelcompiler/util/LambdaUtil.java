package org.drools.modelcompiler.util;

import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.expr.CastExpr;
import com.github.javaparser.ast.expr.EnclosedExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.LambdaExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.Type;

public class LambdaUtil {

    private static String AND_THEN_CALL = "andThen";

    private LambdaUtil() {

    }

    public static Expression compose(LambdaExpr l1, LambdaExpr l2) {
        Type type = new ClassOrInterfaceType(null, "Function1<StockTick, Date>");
        Expression castedExpr = new EnclosedExpr(new CastExpr(type, new EnclosedExpr(l1)));
        MethodCallExpr andThenExpr = new MethodCallExpr(castedExpr, AND_THEN_CALL, NodeList.nodeList(l2));
        return andThenExpr ;
    }
}
