package org.drools.modelcompiler.util.lambdareplace;

import java.util.HashMap;
import java.util.Map;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.LambdaExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.type.ClassOrInterfaceType;

public class ExecModelLambdaPostProcessor {

    private final static String EXPR_CALL = "expr";
    private final static String ALPHA_INDEXED_BY_CALL = "alphaIndexedBy";
    private final static String D_ALPHA_INDEXED_BY_CALL = "D.alphaIndexedBy";
    private final static String D_ON_CALL = "D.on";
    private final static String EXECUTE_CALL = "execute";
    private final static String TEST_CALL = "test";
    private MaterializedLambdaPredicate materializedLambdaPredicate;

    Map<String, CreatedClass> lambdaClasses = new HashMap<>();
    private String packageName;

    public PostProcessedExecModel convertLambdas(String packageName, Statement inputDSL) {
        this.packageName = packageName;
        Statement clone = inputDSL.clone();

        try {
            clone.findAll(MethodCallExpr.class, mc -> EXPR_CALL.equals(mc.getNameAsString()))
                    .forEach(this::replacePredicateInExpr);

            clone.findAll(MethodCallExpr.class, mc -> isAlphaIndexedBy(mc))
                    .forEach(this::replaceExtractorInAlphaIndexedBy);

            clone.findAll(MethodCallExpr.class, mc -> EXECUTE_CALL.equals(mc.getNameAsString()))
                    .forEach(this::replaceConsequenceInOnCall);

            return new PostProcessedExecModel(clone).addAllLambdaClasses(lambdaClasses.values());
        } catch (MaterializedLambdaPredicate.LambdaTypeNeededException e) {
            System.out.println(e);
            return new PostProcessedExecModel(inputDSL);
        }
    }

    private boolean isAlphaIndexedBy(MethodCallExpr mc) {
        return ALPHA_INDEXED_BY_CALL.equals(mc.getNameAsString()) ||
                D_ALPHA_INDEXED_BY_CALL.equals(mc.getNameAsString());
    }

    private void replacePredicateInExpr(MethodCallExpr methodCallExpr) {

        methodCallExpr.getArguments().forEach(a -> {
            if (a.isLambdaExpr()) {
                LambdaExpr lambdaExpr = a.asLambdaExpr();

                CreatedClass aClass = new MaterializedLambdaPredicate(packageName).create(lambdaExpr.toString());
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

                CreatedClass aClass = new MaterializedLambdaExtractor(packageName).create(lambdaExpr.toString(), returnType);
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

                CreatedClass aClass = new MaterializedLambdaConsequence(packageName).create(lambdaExpr.toString());
                lambdaClasses.put(aClass.getClassNameWithPackage(), aClass);

                ClassOrInterfaceType type = StaticJavaParser.parseClassOrInterfaceType(aClass.getClassNameWithPackage());
                a.replace(lambdaInstance(type));
            }
        });
    }
}
