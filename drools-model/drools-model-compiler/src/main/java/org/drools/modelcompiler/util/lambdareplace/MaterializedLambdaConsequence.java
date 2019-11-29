package org.drools.modelcompiler.util.lambdareplace;

import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.EnumDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.VoidType;

import static com.github.javaparser.StaticJavaParser.parseClassOrInterfaceType;
import static org.drools.modelcompiler.util.StringUtil.md5Hash;

public class MaterializedLambdaConsequence extends MaterializedLambda {

    private final static String CLASS_NAME_PREFIX = "LambdaConsequence";

    MaterializedLambdaConsequence(String packageName, String ruleClassName) {
        super(packageName, ruleClassName);
    }

    protected String className(String expressionString) {
        return CLASS_NAME_PREFIX + md5Hash(expressionString);
    }

    @Override
    void createMethodDeclaration(EnumDeclaration classDeclaration) {
        boolean hasDroolsParameter = lambdaParameters.stream().anyMatch(this::isDroolsParameter);
        if(hasDroolsParameter) {
            throw new DroolsNeededInConsequenceException(lambdaExpr.toString());
        }

        MethodDeclaration methodDeclaration = classDeclaration.addMethod("execute", Modifier.Keyword.PUBLIC);
        methodDeclaration.setThrownExceptions(NodeList.nodeList(parseClassOrInterfaceType("Exception")));
        methodDeclaration.addAnnotation("Override");
        methodDeclaration.setType(new VoidType());

        setMethodParameter(methodDeclaration);

        Statement body = lambdaExpr.getBody().clone();
        if (body.isExpressionStmt()) {
            BlockStmt clone = new BlockStmt(NodeList.nodeList(body));
            methodDeclaration.setBody(clone);
        } else if (body.isBlockStmt()) {
            BlockStmt clone = (BlockStmt) body.clone();
            methodDeclaration.setBody(clone);
        }
    }

    private boolean isDroolsParameter(LambdaParameter p) {
        String anObject = p.type.asString();
        return "org.drools.model.Drools".equals(anObject) || "Drools".equals(anObject);
    }

    @Override
    protected ClassOrInterfaceType functionType() {
        String type = "Block" + lambdaParameters.size();
        return parseClassOrInterfaceType("org.drools.model.functions." + type);
    }
}

