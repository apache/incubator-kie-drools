package org.drools.model.codegen.execmodel.util.lambdareplace;

import java.util.List;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.EnumDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.VoidType;

import static org.drools.model.codegen.execmodel.generator.DrlxParseUtil.createSimpleAnnotation;
import static org.drools.model.codegen.execmodel.generator.DrlxParseUtil.toClassOrInterfaceType;

public class MaterializedLambdaConsequence extends MaterializedLambda {

    private final static String CLASS_NAME_PREFIX = "LambdaConsequence";
    private final List<BitMaskVariable> bitMaskVariables;

    MaterializedLambdaConsequence(String packageName, String ruleClassName, List<BitMaskVariable> bitMaskVariables) {
        super(packageName, ruleClassName);
        this.bitMaskVariables = bitMaskVariables;
    }

    @Override
    void createMethodsDeclaration(EnumDeclaration classDeclaration) {
        MethodDeclaration methodDeclaration = classDeclaration.addMethod("execute", Modifier.Keyword.PUBLIC);
        methodDeclaration.setThrownExceptions(NodeList.nodeList(toClassOrInterfaceType(java.lang.Exception.class)));
        methodDeclaration.addAnnotation(createSimpleAnnotation("Override"));
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

    @Override
    String getPrefix() {
        return CLASS_NAME_PREFIX;
    }

    @Override
    protected EnumDeclaration create(CompilationUnit compilationUnit) {
        EnumDeclaration lambdaClass = super.create(compilationUnit);

        boolean hasDroolsParameter = lambdaParameters.stream().anyMatch(this::isDroolsParameter);
        if (hasDroolsParameter) {
            bitMaskVariables.forEach(vd -> vd.generateBitMaskField(lambdaClass));
        }
        return lambdaClass;
    }

    private boolean isDroolsParameter(LambdaParameter p) {
        String anObject = p.type.asString();
        return "org.drools.model.Drools".equals(anObject) || "Drools".equals(anObject);
    }

    @Override
    protected ClassOrInterfaceType functionType() {
        String type = "Block" + lambdaParameters.size();
        return toClassOrInterfaceType("org.drools.model.functions." + type);
    }
}

