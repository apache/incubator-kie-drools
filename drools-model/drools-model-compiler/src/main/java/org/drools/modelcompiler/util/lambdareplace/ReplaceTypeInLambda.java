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

public class ReplaceTypeInLambda {
    public static void replaceTypeInExprLambda(String bindingId, Class accumulateFunctionResultType, Expression expression) {
        expression.findAll(MethodCallExpr.class).forEach(mc -> {
            if(mc.getArguments().stream().anyMatch(a -> a.toString().equals(toVar(bindingId)))) {
                List<LambdaExpr> allLambdas = new ArrayList<>(mc.findAll(LambdaExpr.class)); // Flow DSL

                Optional<Node> optParent = mc.getParentNode(); // In the Pattern DSL they're in the direct pattern
                if(optParent.isPresent()) {
                    MethodCallExpr parent = (MethodCallExpr) optParent.get();
//                    allLambdas.addAll(parent.findAll(LambdaExpr.class));
                }

                allLambdas.forEach(lambdaExpr -> replaceLambdaParameter(accumulateFunctionResultType, lambdaExpr));

            }
        });
    }

    private static void replaceLambdaParameter(Class accumulateFunctionResultType, LambdaExpr lambdaExpr) {
        for(Parameter a : lambdaExpr.getParameters()) {
            a.setType(StaticJavaParser.parseClassOrInterfaceType(accumulateFunctionResultType.getCanonicalName()));
        }
    }
}
