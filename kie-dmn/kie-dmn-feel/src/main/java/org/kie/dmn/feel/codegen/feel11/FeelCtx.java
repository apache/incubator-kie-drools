package org.kie.dmn.feel.codegen.feel11;

import org.drools.javaparser.JavaParser;
import org.drools.javaparser.ast.NodeList;
import org.drools.javaparser.ast.expr.Expression;
import org.drools.javaparser.ast.expr.MethodCallExpr;
import org.drools.javaparser.ast.expr.NameExpr;
import org.drools.javaparser.ast.expr.StringLiteralExpr;

public class FeelCtx {

    public static final String FEELCTX_N = "feelExprCtx";
    public static final NameExpr FEELCTX = new NameExpr(FEELCTX_N);
    private static final String FEEL_SUPPORT = CompiledFEELSupport.class.getSimpleName();
    private static final Expression EMPTY_MAP = JavaParser.parseExpression("java.util.Collections.emptyMap()");

    public static Expression emptyContext() {
        return EMPTY_MAP;
    }

    public static MethodCallExpr getValue(String nameRef) {
        return new MethodCallExpr(FEELCTX, "getValue", new NodeList<>(new StringLiteralExpr(nameRef)));
    }

    public static MethodCallExpr current() {
        return new MethodCallExpr(FeelCtx.FEELCTX, "current");
    }

    public static MethodCallExpr openContext() {
        return new MethodCallExpr(
                new NameExpr(FEEL_SUPPORT),
                "openContext")
                .addArgument(FEELCTX);
    }


    public static MethodCallExpr setEntry(String keyText, Expression expression) {
        return new MethodCallExpr(
                null,
                "setEntry",
                new NodeList<>(
                        new StringLiteralExpr(keyText),
                        expression));
    }

    public static MethodCallExpr closeContext(DirectCompilerResult contextEntriesMethodChain) {
        return new MethodCallExpr(
                contextEntriesMethodChain.getExpression(),
                "closeContext");
    }

}
