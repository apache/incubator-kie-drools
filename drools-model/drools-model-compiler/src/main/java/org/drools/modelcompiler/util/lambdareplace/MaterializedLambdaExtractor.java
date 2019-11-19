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
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.ast.type.UnknownType;

import static com.github.javaparser.StaticJavaParser.parseClassOrInterfaceType;
import static org.drools.modelcompiler.util.StringUtil.md5Hash;

public class MaterializedLambdaExtractor {

    private final static String CLASS_NAME_PREFIX = "LambdaExtractor";
    private LambdaExpr lambdaExpr;
    private String className;

    private final String packageName;

    private List<LambdaParameter> lambdaParameters = new ArrayList<>();
    private String returnType;

    public MaterializedLambdaExtractor(String packageName) {
        this.packageName = packageName;
    }

    public CreatedClass create(String expressionString, String returnType) {
        this.returnType = returnType;
        Expression expression = StaticJavaParser.parseExpression(expressionString);

        if (!expression.isLambdaExpr()) {
            throw new NotLambdaException();
        }

        lambdaExpr = expression.asLambdaExpr();
        className = CLASS_NAME_PREFIX + md5Hash(expressionString);

        parseParameters();

        CompilationUnit compilationUnit = new CompilationUnit(packageName);
        ClassOrInterfaceDeclaration classDeclaration = createPredicate(compilationUnit);

        createMethodDeclaration(classDeclaration);

        return new CreatedClass(compilationUnit, className, packageName);
    }

    private ClassOrInterfaceDeclaration createPredicate(CompilationUnit compilationUnit) {
        ClassOrInterfaceDeclaration classOrInterfaceDeclaration = compilationUnit.addClass(className);
        classOrInterfaceDeclaration.setImplementedTypes(createImplementedType());
        return classOrInterfaceDeclaration;
    }

    private void parseParameters() {
        NodeList<Parameter> parameters = lambdaExpr.getParameters();
        for (Parameter p : parameters) {
            Type c = p.getType();
            if(c instanceof UnknownType) {
                throw new LambdaTypeNeededException(lambdaExpr.toString());
            }
            lambdaParameters.add(new LambdaParameter(p.getNameAsString(), c));
        }
    }

    private void createMethodDeclaration(ClassOrInterfaceDeclaration classDeclaration) {
        MethodDeclaration methodDeclaration = classDeclaration.addMethod("apply", Modifier.Keyword.PUBLIC);
        methodDeclaration.addAnnotation("Override");
        methodDeclaration.setType(returnTypeJP());

        setMethodParameter(methodDeclaration);

        ExpressionStmt clone = (ExpressionStmt) lambdaExpr.getBody().clone();
        methodDeclaration.setBody(new BlockStmt(NodeList.nodeList(new ReturnStmt(clone.getExpression()))));
    }

    private ClassOrInterfaceType returnTypeJP() {
        return parseClassOrInterfaceType(returnType);
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

        NodeList<Type> implementedGenericType = NodeList.nodeList(typeArguments);
        implementedGenericType.add(returnTypeJP());
        bifunction.setTypeArguments(implementedGenericType);
        return NodeList.nodeList(bifunction);
    }

    private ClassOrInterfaceType functionType() {
        String type = "Function" + lambdaParameters.size();
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

    public static class LambdaTypeNeededException extends RuntimeException {

        private final String lambda;

        public LambdaTypeNeededException(String lambda) {
            this.lambda = lambda;
        }

        @Override
        public String getMessage() {
            return "Missing argument in Lambda: " + lambda;
        }
    }
}
