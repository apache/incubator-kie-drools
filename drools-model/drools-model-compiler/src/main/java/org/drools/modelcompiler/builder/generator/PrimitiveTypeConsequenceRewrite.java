package org.drools.modelcompiler.builder.generator;

import java.util.Optional;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.expr.CastExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.Statement;

public class PrimitiveTypeConsequenceRewrite {

    private final RuleContext context;

    public PrimitiveTypeConsequenceRewrite(RuleContext context) {
        this.context = context;
    }

    public String rewrite(String consequence) {

        BlockStmt blockStmt = StaticJavaParser.parseBlock(consequence);
        for (Statement t : blockStmt.getStatements()) {
            t.findAll(CastExpr.class, ce -> ce.getExpression().isNameExpr()).forEach(this::convertStatement);
        }

        return blockStmt.toString();
    }

    private void convertStatement(CastExpr ce) {
        Expression innerExpr = ce.getExpression();
        Optional<Class<?>> castType = context.resolveType(ce.getType().asString());

        String nameExprDeclaration = innerExpr.asNameExpr().toString();
        Optional<DeclarationSpec> declarationById = context.getDeclarationById(nameExprDeclaration);

        if (castType.isPresent() && declarationById.isPresent()) {
            if (castType.get().equals(short.class) && declarationById.get().getDeclarationClass().equals(int.class)) {
                ce.replace(new MethodCallExpr(innerExpr.asNameExpr(), "shortValue"));
            }
        }
    }
}
