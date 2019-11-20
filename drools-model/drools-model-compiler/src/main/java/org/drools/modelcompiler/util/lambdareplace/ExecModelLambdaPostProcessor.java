package org.drools.modelcompiler.util.lambdareplace;

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

    Map<String, CreatedClass> lambdaClasses = new HashMap<>();
    private String packageName;
    private String ruleClassName;

    public PostProcessedExecModel convertLambdas(String packageName, String ruleClassName, Statement inputDSL) {
        this.packageName = packageName;
        this.ruleClassName = ruleClassName;
        Statement clone = inputDSL.clone();

        try {
            clone.findAll(MethodCallExpr.class, mc -> EXPR_CALL.equals(mc.getNameAsString()))
                    .forEach(this::replacePredicateInExpr);

            clone.findAll(MethodCallExpr.class, mc -> ALPHA_INDEXED_BY_CALL.contains(mc.getName().asString()))
                    .forEach(this::replaceExtractorInAlphaIndexedBy);

            clone.findAll(MethodCallExpr.class, mc -> EXECUTE_CALL.equals(mc.getNameAsString()))
                    .forEach(this::replaceConsequenceInOnCall);

            return new PostProcessedExecModel(clone).addAllLambdaClasses(lambdaClasses.values());
        } catch (LambdaTypeNeededException e) {
            System.out.println(e);
            return new PostProcessedExecModel(inputDSL);
        }
    }

    private void replacePredicateInExpr(MethodCallExpr methodCallExpr) {

        methodCallExpr.getArguments().forEach(a -> {
            if (a.isLambdaExpr()) {
                LambdaExpr lambdaExpr = a.asLambdaExpr();

                CreatedClass aClass = new MaterializedLambdaPredicate(packageName, ruleClassName).create(lambdaExpr.toString());
                lambdaClasses.put(aClass.getClassNameWithPackage(), aClass);

                ClassOrInterfaceType type = StaticJavaParser.parseClassOrInterfaceType(aClass.getClassNameWithPackage());
                a.replace(lambdaInstance(type));
            }
        });
    }

    private Expression lambdaInstance(ClassOrInterfaceType type) {
        return new FieldAccessExpr(new NameExpr(type.asString()), "INSTANCE");
    }

    private void replaceExtractorInAlphaIndexedBy(MethodCallExpr methodCallExpr) {
        Expression argument = methodCallExpr.getArgument(0);

        if (!argument.isClassExpr()) {
            throw new RuntimeException();
        }

        String returnType = argument.asClassExpr().getTypeAsString();

        methodCallExpr.getArguments().forEach(a -> {
            if (a.isLambdaExpr()) {
                LambdaExpr lambdaExpr = a.asLambdaExpr();

                CreatedClass aClass = new MaterializedLambdaExtractor(packageName, ruleClassName, returnType).create(lambdaExpr.toString());
                lambdaClasses.put(aClass.getClassNameWithPackage(), aClass);

                ClassOrInterfaceType type = StaticJavaParser.parseClassOrInterfaceType(aClass.getClassNameWithPackage());
                a.replace(lambdaInstance(type));
            }
        });
    }

    private void replaceConsequenceInOnCall(MethodCallExpr methodCallExpr) {
        methodCallExpr.getArguments().forEach(a -> {
            if (a.isLambdaExpr()) {
                LambdaExpr lambdaExpr = a.asLambdaExpr();

                CreatedClass aClass = new MaterializedLambdaConsequence(packageName, ruleClassName).create(lambdaExpr.toString());
                lambdaClasses.put(aClass.getClassNameWithPackage(), aClass);

                ClassOrInterfaceType type = StaticJavaParser.parseClassOrInterfaceType(aClass.getClassNameWithPackage());
                a.replace(lambdaInstance(type));
            }
        });
    }
}
