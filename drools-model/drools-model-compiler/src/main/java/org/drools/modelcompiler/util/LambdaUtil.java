package org.drools.modelcompiler.util;

import com.github.javaparser.ast.expr.CastExpr;
import com.github.javaparser.ast.expr.EnclosedExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.LambdaExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.Type;

import static com.github.javaparser.ast.NodeList.nodeList;

public class LambdaUtil {

    private static String AND_THEN_CALL = "andThen";

    private LambdaUtil() {

    }

    public static MethodCallExpr compose(LambdaExpr l1, LambdaExpr l2, Type aType, Type bType) {
        Type type = new ClassOrInterfaceType(null, new SimpleName("org.drools.model.functions.Function1"),
                                             nodeList(aType, bType));

        Expression castedExpr = new EnclosedExpr(new CastExpr(type, new EnclosedExpr(l1)));
        return new MethodCallExpr(castedExpr, AND_THEN_CALL, nodeList(l2));
    }
}
