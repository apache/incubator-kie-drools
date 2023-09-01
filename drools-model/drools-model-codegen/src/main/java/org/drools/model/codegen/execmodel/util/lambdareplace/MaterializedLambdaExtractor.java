package org.drools.model.codegen.execmodel.util.lambdareplace;

import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.EnumDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.Type;

import static com.github.javaparser.ast.NodeList.nodeList;
import static org.drools.model.codegen.execmodel.generator.DrlxParseUtil.createSimpleAnnotation;
import static org.drools.model.codegen.execmodel.generator.DrlxParseUtil.toClassOrInterfaceType;

public class MaterializedLambdaExtractor extends MaterializedLambda {

    private final static String CLASS_NAME_PREFIX = "LambdaExtractor";
    private final Type returnType;

    MaterializedLambdaExtractor(String packageName, String ruleClassName, Type returnType) {
        super(packageName, ruleClassName);
        this.returnType = returnType;
    }

    @Override
    void createMethodsDeclaration(EnumDeclaration classDeclaration) {
        MethodDeclaration methodDeclaration = classDeclaration.addMethod("apply", Modifier.Keyword.PUBLIC);
        methodDeclaration.addAnnotation(createSimpleAnnotation("Override"));
        methodDeclaration.setType(returnType);

        setMethodParameter(methodDeclaration);

        Statement clonedBody = lambdaExpr.getBody().clone();
        if (clonedBody.isExpressionStmt()) {
            methodDeclaration.setBody(new BlockStmt(nodeList(new ReturnStmt(clonedBody
                                                                                    .asExpressionStmt()
                                                                                    .getExpression()))));
        } else if (clonedBody.isBlockStmt()) {
            methodDeclaration.setBody(clonedBody.asBlockStmt());
        }
    }

    @Override
    protected NodeList<ClassOrInterfaceType> createImplementedTypes() {
        ClassOrInterfaceType functionType = functionType();

        NodeList<Type> typeArguments = lambdaParametersToTypeArguments();
        typeArguments.add(returnType);
        functionType.setTypeArguments(typeArguments);
        return nodeList(functionType, lambdaExtractorType());
    }

    @Override
    String getPrefix() {
        return CLASS_NAME_PREFIX;
    }

    @Override
    protected ClassOrInterfaceType functionType() {
        String type = "Function" + lambdaParameters.size();
        return toClassOrInterfaceType("org.drools.model.functions." + type);
    }
}
