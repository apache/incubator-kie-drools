package org.drools.modelcompiler.util.lambdareplace;

import java.util.List;

import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.EnumDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.Type;

import static com.github.javaparser.StaticJavaParser.parseClassOrInterfaceType;
import static com.github.javaparser.StaticJavaParser.parseType;
import static org.drools.modelcompiler.util.StringUtil.md5Hash;

public class MaterializedLambdaExtractor extends MaterializedLambda {

    private final static String CLASS_NAME_PREFIX = "LambdaExtractor";
    private String returnType;

    MaterializedLambdaExtractor(String packageName, String ruleClassName, String returnType) {
        super(packageName, ruleClassName);
        this.returnType = returnType;
    }

    @Override
    void createMethodDeclaration(EnumDeclaration classDeclaration) {
        MethodDeclaration methodDeclaration = classDeclaration.addMethod("apply", Modifier.Keyword.PUBLIC);
        methodDeclaration.addAnnotation("Override");
        methodDeclaration.setType(returnTypeJP());

        setMethodParameter(methodDeclaration);

        ExpressionStmt clone = (ExpressionStmt) lambdaExpr.getBody().clone();
        methodDeclaration.setBody(new BlockStmt(NodeList.nodeList(new ReturnStmt(clone.getExpression()))));
    }

    private Type returnTypeJP() {
        return parseType(returnType);
    }

    @Override
    protected NodeList<ClassOrInterfaceType> createImplementedType() {
        ClassOrInterfaceType functionType = functionType();

        List<Type> typeArguments = lambdaParametersToType();
        NodeList<Type> implementedGenericType = NodeList.nodeList(typeArguments);
        implementedGenericType.add(returnTypeJP());
        functionType.setTypeArguments(implementedGenericType);
        return NodeList.nodeList(functionType);
    }

    @Override
    String className(String expressionString) {
        return CLASS_NAME_PREFIX + md5Hash(expressionString);
    }

    @Override
    protected ClassOrInterfaceType functionType() {
        String type = "Function" + lambdaParameters.size();
        return parseClassOrInterfaceType("org.drools.model.functions." + type);
    }
}
