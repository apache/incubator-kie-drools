package org.drools.modelcompiler.builder.generator;

import java.util.Optional;

import com.github.javaparser.ParseProblemException;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.expr.CastExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PrimitiveTypeConsequenceRewrite {

    private final RuleContext context;

    private static final Logger logger = LoggerFactory.getLogger(PrimitiveTypeConsequenceRewrite.class);

    public PrimitiveTypeConsequenceRewrite(RuleContext context) {
        this.context = context;
    }

    public String rewrite(String consequence) {

        BlockStmt blockStmt;
        try {
            blockStmt = StaticJavaParser.parseBlock(consequence);
        } catch (ParseProblemException e) {
            logger.warn(String.format("Cannot post process consequence: %s", consequence));
            return consequence;
        }
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
