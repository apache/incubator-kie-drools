package org.drools.modelcompiler.builder.generator.expressiontyper;

import java.util.ArrayList;
import java.util.List;

import org.drools.javaparser.ast.Node;
import org.drools.javaparser.ast.drlx.expr.InlineCastExpr;
import org.drools.javaparser.ast.expr.ArrayAccessExpr;
import org.drools.javaparser.ast.expr.Expression;
import org.drools.javaparser.ast.expr.FieldAccessExpr;
import org.drools.javaparser.ast.expr.MethodCallExpr;

public class FlattenScope {

    public static List<Node> flattenScope(Expression expressionWithScope) {
        List<Node> res = new ArrayList<>();
        if (expressionWithScope instanceof FieldAccessExpr) {
            FieldAccessExpr fieldAccessExpr = (FieldAccessExpr) expressionWithScope;
            res.addAll(flattenScope(fieldAccessExpr.getScope()));
            res.add(fieldAccessExpr.getName());
        } else if (expressionWithScope instanceof MethodCallExpr) {
            MethodCallExpr methodCallExpr = (MethodCallExpr) expressionWithScope;
            if (methodCallExpr.getScope().isPresent()) {
                res.addAll(flattenScope(methodCallExpr.getScope().get()));
            }
            res.add(methodCallExpr);
        } else if (expressionWithScope instanceof InlineCastExpr && ((InlineCastExpr) expressionWithScope).getExpression() instanceof FieldAccessExpr) {
            InlineCastExpr inlineCastExpr = (InlineCastExpr) expressionWithScope;
            Expression internalScope = ((FieldAccessExpr) inlineCastExpr.getExpression()).getScope();
            res.addAll(flattenScope((internalScope)));
            res.add(expressionWithScope);
        } else if (expressionWithScope instanceof ArrayAccessExpr) {
            ArrayAccessExpr arrayAccessExpr = (ArrayAccessExpr) expressionWithScope;
            res.addAll(flattenScope(arrayAccessExpr.getName()));
            res.add(arrayAccessExpr);
        } else {
            res.add(expressionWithScope);
        }
        return res;
    }
}
