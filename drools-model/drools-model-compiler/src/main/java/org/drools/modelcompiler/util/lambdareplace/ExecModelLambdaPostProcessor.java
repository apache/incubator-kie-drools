package org.drools.modelcompiler.util.lambdareplace;

import java.util.HashMap;
import java.util.Map;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.expr.LambdaExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.type.ClassOrInterfaceType;

public class ExecModelLambdaPostProcessor {

    private final static String EXPR_CALL = "expr";
    private final static String ALPHA_INDEXED_BY_CALL = "alphaIndexedBy";
    private final static String TEST_CALL = "test";
    private MaterializedLambdaPredicate materializedLambdaPredicate;

    Map<String, CreatedClass> lambdaClasses = new HashMap<>();
    private String packageName;

    public PostProcessedExecModel convertLambdas(String packageName, Statement inputDSL) {
        this.packageName = packageName;
        Statement clone = inputDSL.clone();

        try {
            clone.findAll(MethodCallExpr.class, mc -> EXPR_CALL.equals(mc.getNameAsString()))
                    .forEach(this::replaceLambdaInExpr);

//            clone.findAll(MethodCallExpr.class, mc -> ALPHA_INDEXED_BY_CALL.equals(mc.getNameAsString()))
//                    .forEach(this::replaceLambdaInExpr);

            return new PostProcessedExecModel(clone).addAllLambdaClasses(lambdaClasses.values());
        } catch (MaterializedLambdaPredicate.LambdaTypeNeededException e) {
            System.out.println(e);
            return new PostProcessedExecModel(inputDSL);
        }
    }

    private void replaceLambdaInExpr(MethodCallExpr methodCallExpr) {

        methodCallExpr.getArguments().forEach(a -> {
            if (a.isLambdaExpr()) {
                LambdaExpr lambdaExpr = a.asLambdaExpr();

                CreatedClass aClass = new MaterializedLambdaPredicate(packageName).createPredicate(lambdaExpr.toString());
                lambdaClasses.put(aClass.getClassNameWithPackage(), aClass);

                ClassOrInterfaceType type = StaticJavaParser.parseClassOrInterfaceType(aClass.getClassNameWithPackage());
                a.replace(new ObjectCreationExpr(null, type, NodeList.nodeList()));
            }
        });
    }
}
