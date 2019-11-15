package org.drools.modelcompiler.util.lambdareplace;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.LambdaExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.PrimitiveType;
import com.github.javaparser.ast.type.Type;

import static com.github.javaparser.StaticJavaParser.parseClassOrInterfaceType;
import static org.drools.modelcompiler.util.StringUtil.md5Hash;

public class LambdaClass {

    private final static String CLASS_NAME_PREFIX = "Lambda";
    private LambdaExpr lambdaExpr;
    private String className;

    private final String packageName;

    private List<LambdaParameter> lambdaParameters = new ArrayList<>();

    public LambdaClass(String packageName) {
        this.packageName = packageName;
    }

    public CreatedClass createClass(String expressionString) {
        Expression expression = StaticJavaParser.parseExpression(expressionString);

        if (!expression.isLambdaExpr()) {
            throw new NotLambdaException();
        }

        lambdaExpr = expression.asLambdaExpr();
        className = CLASS_NAME_PREFIX + md5Hash(expressionString);

        parseParameters();

        CompilationUnit compilationUnit = new CompilationUnit(packageName);
        ClassOrInterfaceDeclaration classDeclaration = createClass(compilationUnit);

        createMethodDeclaration(classDeclaration);

        return new CreatedClass(compilationUnit, className, packageName);
    }

    private ClassOrInterfaceDeclaration createClass(CompilationUnit compilationUnit) {
        ClassOrInterfaceDeclaration classOrInterfaceDeclaration = compilationUnit.addClass(className);
        classOrInterfaceDeclaration.setImplementedTypes(createImplementedType());
        return classOrInterfaceDeclaration;
    }

    private void parseParameters() {
        NodeList<Parameter> parameters = lambdaExpr.getParameters();
        for (Parameter p : parameters) {
            Type c = p.getType();
            lambdaParameters.add(new LambdaParameter(p.getNameAsString(), c));
        }
    }

    private void createMethodDeclaration(ClassOrInterfaceDeclaration classDeclaration) {
        MethodDeclaration methodDeclaration = classDeclaration.addMethod("test", Modifier.Keyword.PUBLIC);
        methodDeclaration.setType(new PrimitiveType(PrimitiveType.Primitive.BOOLEAN));

        setMethodParameter(methodDeclaration);

        ExpressionStmt clone = (ExpressionStmt) lambdaExpr.getBody().clone();
        methodDeclaration.setBody(new BlockStmt(NodeList.nodeList(new ReturnStmt(clone.getExpression()))));
    }

    private void setMethodParameter(MethodDeclaration methodDeclaration) {
        for (LambdaParameter parameter : lambdaParameters) {
            methodDeclaration.addParameter(new Parameter(parameter.type, parameter.name));
        }
    }

    private NodeList<ClassOrInterfaceType> createImplementedType() {
        ClassOrInterfaceType bifunction = functionType();

        List<Type> typeArguments = lambdaParameters.stream()
                .map(p -> p.type)
                .collect(Collectors.toList());

        bifunction.setTypeArguments(NodeList.nodeList(typeArguments));
        return NodeList.nodeList(bifunction);
    }

    private ClassOrInterfaceType functionType() {
        String type = "Predicate" + lambdaParameters.size();
        return parseClassOrInterfaceType("org.drools.model.functions." + type);
    }

    private static class LambdaParameter {

        String name;
        Type type;

        LambdaParameter(String name, Type type) {
            this.name = name;
            this.type = type;
        }
    }

    private class NoFunctionForTypesException extends RuntimeException {

    }
}
