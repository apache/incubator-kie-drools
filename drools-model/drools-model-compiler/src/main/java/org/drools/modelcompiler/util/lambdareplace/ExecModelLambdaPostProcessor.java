package org.drools.modelcompiler.util.lambdareplace;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.LambdaExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.type.ClassOrInterfaceType;

import static org.drools.modelcompiler.builder.generator.DslMethodNames.ALPHA_INDEXED_BY_CALL;
import static org.drools.modelcompiler.builder.generator.DslMethodNames.EXECUTE_CALL;
import static org.drools.modelcompiler.builder.generator.expression.PatternExpressionBuilder.EXPR_CALL;

public class ExecModelLambdaPostProcessor {

    private Map<String, CreatedClass> lambdaClasses = new HashMap<>();

    public PostProcessedExecModel convertLambdas(String packageName, String ruleClassName, Statement inputDSL, Collection<String> imports) {
        Statement clone = inputDSL.clone();

        try {
            clone.findAll(MethodCallExpr.class, mc -> EXPR_CALL.equals(mc.getNameAsString()))
                    .forEach(methodCallExpr1 -> extractLambdaFromMethodCall(methodCallExpr1, new MaterializedLambdaPredicate(packageName, ruleClassName), imports));

            clone.findAll(MethodCallExpr.class, mc -> ALPHA_INDEXED_BY_CALL.contains(mc.getName().asString()))
                    .forEach(methodCallExpr -> {
                        Expression argument = methodCallExpr.getArgument(0);

                        if (!argument.isClassExpr()) {
                            throw new RuntimeException();
                        }

                        String returnType = argument.asClassExpr().getTypeAsString();
                        extractLambdaFromMethodCall(methodCallExpr, new MaterializedLambdaExtractor(packageName, ruleClassName, returnType), imports);
                    });

            clone.findAll(MethodCallExpr.class, mc -> EXECUTE_CALL.equals(mc.getNameAsString()))
                    .forEach(methodCallExpr -> extractLambdaFromMethodCall(methodCallExpr, new MaterializedLambdaConsequence(packageName, ruleClassName), imports));

            return new PostProcessedExecModel(clone).addAllLambdaClasses(lambdaClasses.values());
        } catch (LambdaTypeNeededException e) {
            System.out.println(e);
            return new PostProcessedExecModel(inputDSL);
        }
    }

    private Expression lambdaInstance(ClassOrInterfaceType type) {
        return new FieldAccessExpr(new NameExpr(type.asString()), "INSTANCE");
    }

    private void extractLambdaFromMethodCall(MethodCallExpr methodCallExpr, MaterializedLambda lambdaExtractor, Collection<String> imports) {
        methodCallExpr.getArguments().forEach(a -> {
            if (a.isLambdaExpr()) {
                LambdaExpr lambdaExpr = a.asLambdaExpr();

                CreatedClass aClass = lambdaExtractor.create(lambdaExpr.toString(), imports);
                lambdaClasses.put(aClass.getClassNameWithPackage(), aClass);

                ClassOrInterfaceType type = StaticJavaParser.parseClassOrInterfaceType(aClass.getClassNameWithPackage());
                a.replace(lambdaInstance(type));
            }
        });
    }
}
