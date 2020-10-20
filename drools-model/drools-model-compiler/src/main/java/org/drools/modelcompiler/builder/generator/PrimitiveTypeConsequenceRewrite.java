package org.drools.modelcompiler.builder.generator;

import java.util.Optional;

import com.github.javaparser.ParseProblemException;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.expr.CastExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import org.drools.modelcompiler.builder.generator.expressiontyper.ExpressionTyper;
import org.drools.modelcompiler.builder.generator.expressiontyper.TypedExpressionResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.drools.modelcompiler.builder.generator.DrlxParseUtil.unEncloseExpr;

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

        blockStmt.findAll(CastExpr.class).forEach(this::convertStatement);

        return blockStmt.toString();
    }

    private void convertStatement(CastExpr ce) {
        Expression innerExpr = ce.getExpression();
        Optional<Class<?>> castType = context.resolveType(ce.getType().asString());

        TypedExpressionResult typedExpressionResult =
                new ExpressionTyper(context)
                .toTypedExpression(innerExpr);

        Optional<TypedExpression> optTypeExpression = typedExpressionResult.getTypedExpression();

        optTypeExpression.ifPresent(typedExpression -> {
            if (castType.isPresent() &&
                    castType.get().equals(short.class) &&
                    !typedExpression.isNumberLiteral() &&
                    typedExpression.getRawClass().equals(int.class)
            ) {
                Expression unenclosedExpression = unEncloseExpr(typedExpression.getExpression());
                Expression scope = StaticJavaParser.parseExpression(unenclosedExpression.toString());
                MethodCallExpr shortValue = new MethodCallExpr(scope, "shortValue");
                ce.replace(shortValue);
            }
        });
    }
}
