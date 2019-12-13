package org.drools.modelcompiler.builder.generator.expressiontyper;

import java.util.ArrayList;
import java.util.List;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.expr.ArrayAccessExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import org.drools.mvel.parser.ast.expr.InlineCastExpr;
import org.drools.mvel.parser.ast.expr.NullSafeFieldAccessExpr;
import org.drools.mvel.parser.ast.expr.NullSafeMethodCallExpr;

public class FlattenScope {

    public static List<Node> flattenScope(Expression expressionWithScope) {
        List<Node> res = new ArrayList<>();
        if (expressionWithScope instanceof FieldAccessExpr) {
            FieldAccessExpr fieldAccessExpr = (FieldAccessExpr) expressionWithScope;
            res.addAll(flattenScope(fieldAccessExpr.getScope()));
            res.add(fieldAccessExpr.getName());
        } else if (expressionWithScope instanceof NullSafeFieldAccessExpr) {
            NullSafeFieldAccessExpr fieldAccessExpr = (NullSafeFieldAccessExpr) expressionWithScope;
            res.addAll(flattenScope(fieldAccessExpr.getScope()));
            res.add(fieldAccessExpr.getName());
        } else if (expressionWithScope instanceof MethodCallExpr) {
            MethodCallExpr methodCallExpr = (MethodCallExpr) expressionWithScope;
            if (methodCallExpr.getScope().isPresent()) {
                res.addAll(flattenScope(methodCallExpr.getScope().orElseThrow(() -> new IllegalStateException("Scope expression is not present!"))));
            }
            res.add(methodCallExpr);
        } else if (expressionWithScope instanceof NullSafeMethodCallExpr) {
            NullSafeMethodCallExpr methodCallExpr = (NullSafeMethodCallExpr) expressionWithScope;
            if (methodCallExpr.getScope().isPresent()) {
                res.addAll(flattenScope(methodCallExpr.getScope().orElseThrow(() -> new IllegalStateException("Scope expression is not present!"))));
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

    private FlattenScope() {
        // It is not allowed to create instances of util classes.
    }
}
