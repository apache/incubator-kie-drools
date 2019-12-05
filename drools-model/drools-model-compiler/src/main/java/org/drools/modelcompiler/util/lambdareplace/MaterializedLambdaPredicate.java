package org.drools.modelcompiler.util.lambdareplace;

import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.EnumDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.PrimitiveType;

import static com.github.javaparser.StaticJavaParser.parseClassOrInterfaceType;
import static org.drools.modelcompiler.util.StringUtil.md5Hash;

public class MaterializedLambdaPredicate extends MaterializedLambda {

    private final static String CLASS_NAME_PREFIX = "LambdaPredicate";

    MaterializedLambdaPredicate(String packageName, String ruleClassName) {
        super(packageName, ruleClassName);
    }

    @Override
    String className(String expressionString) {
        return CLASS_NAME_PREFIX + md5Hash(expressionString);
    }

    @Override
    void createMethodDeclaration(EnumDeclaration classDeclaration) {
        MethodDeclaration methodDeclaration = classDeclaration.addMethod("test", Modifier.Keyword.PUBLIC);
        methodDeclaration.addAnnotation("Override");
        methodDeclaration.setType(new PrimitiveType(PrimitiveType.Primitive.BOOLEAN));

        setMethodParameter(methodDeclaration);

        ExpressionStmt clone = (ExpressionStmt) lambdaExpr.getBody().clone();
        methodDeclaration.setBody(new BlockStmt(NodeList.nodeList(new ReturnStmt(clone.getExpression()))));
    }

    @Override
    protected ClassOrInterfaceType functionType() {
        String type = "Predicate" + lambdaParameters.size();
        return parseClassOrInterfaceType("org.drools.model.functions." + type);
    }
}
