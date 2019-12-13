package org.drools.modelcompiler.util.lambdareplace;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.LambdaExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;

import static org.drools.modelcompiler.builder.generator.DrlxParseUtil.toVar;
import static org.drools.modelcompiler.builder.generator.DslMethodNames.PATTERN_CALL;
import static org.drools.modelcompiler.builder.generator.expression.FlowExpressionBuilder.BIND_CALL;
import static org.drools.modelcompiler.builder.generator.expression.FlowExpressionBuilder.EXPR_CALL;

public class ReplaceTypeInLambda {

    private ReplaceTypeInLambda() {

    }

    public static void replaceTypeInExprLambda(String bindingId, Class accumulateFunctionResultType, Expression expression) {
        expression.findAll(MethodCallExpr.class).forEach(mc -> {
            if (mc.getArguments().stream().anyMatch(a -> a.toString().equals(toVar(bindingId)))) {
                List<LambdaExpr> allLambdas = new ArrayList<>();

                if (mc.getNameAsString().equals(EXPR_CALL)) {
                    allLambdas.addAll(expression.findAll(LambdaExpr.class));
                }

                Optional<Expression> optScope = mc.getScope();
                if (optScope.isPresent() && optScope.get().asMethodCallExpr().getNameAsString().equals(BIND_CALL)) {
                    allLambdas.addAll(expression.findAll(LambdaExpr.class));
                }

                Optional<Node> optParent = mc.getParentNode(); // In the Pattern DSL they're in the direct pattern
                if (mc.getNameAsString().equals(PATTERN_CALL) && optParent.isPresent()) {
                    List<LambdaExpr> all = expression.findAll(LambdaExpr.class);
                    allLambdas.addAll(all);
                }
                allLambdas.forEach(lambdaExpr -> replaceLambdaParameter(accumulateFunctionResultType, lambdaExpr, bindingId));
            }
        });
    }

    private static void replaceLambdaParameter(Class accumulateFunctionResultType, LambdaExpr lambdaExpr, String bindingId) {
        for (Parameter a : lambdaExpr.getParameters()) {

            if (!a.getType().isUnknownType() &&
                    (a.getNameAsString().equals("_this") || a.getNameAsString().equals(bindingId))) {
                a.setType(StaticJavaParser.parseClassOrInterfaceType(accumulateFunctionResultType.getCanonicalName()));
            }
        }
    }
}
