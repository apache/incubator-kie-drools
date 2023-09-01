package org.drools.model.codegen.execmodel.util.lambdareplace;

import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.EnumDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.BooleanLiteralExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.PrimitiveType;
import com.github.javaparser.utils.StringEscapeUtils;
import org.drools.model.functions.PredicateInformation;

import static org.drools.model.codegen.execmodel.generator.DrlxParseUtil.createSimpleAnnotation;
import static org.drools.model.codegen.execmodel.generator.DrlxParseUtil.toClassOrInterfaceType;
import static org.drools.model.codegen.execmodel.generator.DrlxParseUtil.toStringLiteral;

public class MaterializedLambdaPredicate extends MaterializedLambda {

    private static final String CLASS_NAME_PREFIX = "LambdaPredicate";
    private final PredicateInformation predicateInformation;

    MaterializedLambdaPredicate(String packageName, String ruleClassName, PredicateInformation predicateInformation) {
        super(packageName, ruleClassName);
        this.predicateInformation = predicateInformation;
    }

    @Override
    String getPrefix() {
        return CLASS_NAME_PREFIX;
    }

    @Override
    void createMethodsDeclaration(EnumDeclaration classDeclaration) {
        createTestMethod(classDeclaration);
        if (!predicateInformation.isEmpty()) {
            createPredicateInformationMethod(classDeclaration);
        }
    }

    private void createTestMethod(EnumDeclaration classDeclaration) {
        MethodDeclaration methodDeclaration = classDeclaration.addMethod("test", Modifier.Keyword.PUBLIC);
        methodDeclaration.setThrownExceptions(NodeList.nodeList(toClassOrInterfaceType(java.lang.Exception.class)));
        methodDeclaration.addAnnotation(createSimpleAnnotation("Override"));
        methodDeclaration.setType(new PrimitiveType(PrimitiveType.Primitive.BOOLEAN));

        setMethodParameter(methodDeclaration);

        ExpressionStmt clone = (ExpressionStmt) lambdaExpr.getBody().clone();
        methodDeclaration.setBody(new BlockStmt(NodeList.nodeList(new ReturnStmt(clone.getExpression()))));
    }

    private void createPredicateInformationMethod(EnumDeclaration classDeclaration) {
        MethodDeclaration methodDeclaration = classDeclaration.addMethod("predicateInformation", Modifier.Keyword.PUBLIC);
        methodDeclaration.addAnnotation("Override");
        ClassOrInterfaceType predicateInformationType = toClassOrInterfaceType(PredicateInformation.class);
        methodDeclaration.setType(predicateInformationType);

        BlockStmt block = new BlockStmt();

        NameExpr infoExpr = new NameExpr("info");
        VariableDeclarationExpr infoVar = new VariableDeclarationExpr(toClassOrInterfaceType(PredicateInformation.class), "info");
        NodeList<Expression> newPredicateInformationArguments = NodeList.nodeList(toStringLiteral(StringEscapeUtils.escapeJava(predicateInformation.getStringConstraint())));
        ObjectCreationExpr newPredicateInformation = new ObjectCreationExpr(null, predicateInformationType, newPredicateInformationArguments);
        block.addStatement(new AssignExpr(infoVar, newPredicateInformation, AssignExpr.Operator.ASSIGN));

        int i = 0;
        NodeList<Expression> addRuleNamesArguments = null;
        for (PredicateInformation.RuleDef ruleDef : predicateInformation.getRuleDefs()) {
            if (i++ % 125 == 0) {
                addRuleNamesArguments = NodeList.nodeList();
                block.addStatement(new MethodCallExpr(infoExpr, "addRuleNames", addRuleNamesArguments));
            }
            addRuleNamesArguments.add(toStringLiteral(ruleDef.getRuleName()));
            addRuleNamesArguments.add(toStringLiteral(ruleDef.getFileName()));
        }
        if (predicateInformation.isMoreThanMaxRuleDefs()) {
            block.addStatement(new MethodCallExpr(infoExpr, "setMoreThanMaxRuleDefs", NodeList.nodeList(new BooleanLiteralExpr(true))));
        }
        block.addStatement(new ReturnStmt(infoExpr));
        methodDeclaration.setBody(block);
    }

    @Override
    protected ClassOrInterfaceType functionType() {
        String type = "Predicate" + lambdaParameters.size();
        return toClassOrInterfaceType("org.drools.model.functions." + type);
    }
}
