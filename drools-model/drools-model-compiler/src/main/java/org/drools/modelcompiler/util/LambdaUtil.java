package org.drools.modelcompiler.util;

import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.expr.EnclosedExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.LambdaExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;

public class LambdaUtil {

    private static String AND_THEN_CALL = "andThen";

    private LambdaUtil() {

    }

    public static Expression compose(LambdaExpr l1, LambdaExpr l2) {
        return new MethodCallExpr(new EnclosedExpr(l1), AND_THEN_CALL, NodeList.nodeList(l2));
    }
}
