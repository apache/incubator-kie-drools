package org.drools.modelcompiler.builder.generator;

import java.util.Optional;

import com.github.javaparser.ParseProblemException;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.expr.CastExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.Node;
import org.drools.modelcompiler.builder.generator.expressiontyper.ExpressionTyper;
import org.drools.modelcompiler.builder.generator.expressiontyper.TypedExpressionResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.github.javaparser.ast.NodeList.nodeList;
import static org.drools.modelcompiler.builder.generator.DrlxParseUtil.unEncloseExpr;

public class PrimitiveTypeConsequenceRewrite {

    private final RuleContext context;

    private static final Logger logger = LoggerFactory.getLogger(PrimitiveTypeConsequenceRewrite.class);

    public PrimitiveTypeConsequenceRewrite(RuleContext context) {
        this.context = context;
    }

    public String rewrite(String consequence) {
        Node blockStmt;
        try {
            blockStmt = StaticJavaParser.parseBlock(consequence);
        } catch (ParseProblemException e) {
            logger.warn(String.format("Cannot post process consequence: %s", consequence));
            return consequence;
        }

        convertNode( blockStmt );
        return blockStmt.toString();
    }

    public static <T extends Node> T rewriteNode( RuleContext context, T node ) {
        if (node instanceof CastExpr) {
            return (T) convertCast(context, (( CastExpr ) node));
        }
        new PrimitiveTypeConsequenceRewrite(context).convertNode( node );
        return node;
    }

    private void convertNode( Node node ) {
        node.findAll(CastExpr.class).forEach(this::convertStatement);
    }

    private void convertStatement(CastExpr ce) {
        ce.replace(convertCast(context, ce));
    }

    private static Expression convertCast(RuleContext context, CastExpr ce) {
        Expression innerExpr = ce.getExpression();
        Optional<Class<?>> castType = context.resolveType(ce.getType().asString());

        TypedExpressionResult typedExpressionResult =
                new ExpressionTyper(context)
                .toTypedExpression(innerExpr);

        Optional<TypedExpression> optTypeExpression = typedExpressionResult.getTypedExpression();

        return optTypeExpression.map(typedExpression -> {
            if (castType.isPresent() &&
                    castType.get().equals(short.class) &&
                    !typedExpression.isNumberLiteral() &&
                    typedExpression.getRawClass().equals(int.class)
            ) {
                Expression unenclosedExpression = unEncloseExpr(typedExpression.getExpression());
                Expression scope = StaticJavaParser.parseExpression(unenclosedExpression.toString());

                MethodCallExpr integerValueOf = new MethodCallExpr(new NameExpr(Integer.class.getCanonicalName()), "valueOf", nodeList(scope));
                MethodCallExpr shortValue = new MethodCallExpr(integerValueOf, "shortValue");
                return shortValue;
            }
            return ce;
        }).orElse( ce );
    }
}
