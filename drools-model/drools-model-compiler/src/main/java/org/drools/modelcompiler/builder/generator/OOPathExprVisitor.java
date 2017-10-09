package org.drools.modelcompiler.builder.generator;

import java.util.HashSet;
import java.util.List;

import org.drools.javaparser.ast.drlx.OOPathChunk;
import org.drools.javaparser.ast.drlx.OOPathExpr;
import org.drools.javaparser.ast.expr.Expression;
import org.drools.javaparser.ast.expr.MethodCallExpr;
import org.drools.javaparser.ast.expr.NameExpr;
import org.drools.modelcompiler.builder.PackageModel;

import static org.drools.modelcompiler.builder.generator.DrlxParseUtil.extractGenericType;
import static org.drools.modelcompiler.builder.generator.DrlxParseUtil.generateLambdaWithoutParameters;
import static org.drools.modelcompiler.builder.generator.DrlxParseUtil.prepend;
import static org.drools.modelcompiler.builder.generator.DrlxParseUtil.toVar;
import static org.drools.modelcompiler.builder.generator.ModelGenerator.buildExpressionWithIndexing;

public class OOPathExprVisitor {

    private final RuleContext context;
    private final PackageModel packageModel;

    public OOPathExprVisitor(RuleContext context, PackageModel packageModel) {
        this.context = context;
        this.packageModel = packageModel;
    }

    public void visit(Class<?> originalClass, String originalBind, OOPathExpr ooPathExpr) {

        Class<?> previousClass = originalClass;
        String previousBind = originalBind;

        for (OOPathChunk chunk : ooPathExpr.getChunks()) {

            String fieldName = chunk.getField().toString();

            TypedExpression callExpr = DrlxParseUtil.nameExprToMethodCallExpr(fieldName, previousClass);
            Class<?> expressionType = callExpr.getType().orElseThrow(RuntimeException::new);
            if (expressionType == List.class) {
                expressionType = extractGenericType(previousClass, ((MethodCallExpr) callExpr.getExpression()).getName().toString());
            }
            String bindingId = context.getExprId(expressionType, fieldName);

            Expression withThis = prepend(new NameExpr("_this"), callExpr.getExpression());
            final Expression expr = generateLambdaWithoutParameters(new HashSet<>(), withThis);

            final MethodCallExpr reactiveFrom = new MethodCallExpr(null, "reactiveFrom");
            reactiveFrom.addArgument(new NameExpr(toVar(previousBind)));
            reactiveFrom.addArgument(expr);

            context.addDeclaration(new DeclarationSpec(bindingId, expressionType, reactiveFrom));

            Expression condition = chunk.getCondition();
            if (condition != null) {
                final ModelGenerator.DrlxParseResult drlxParseResult = ModelGenerator.drlxParse(context, packageModel, expressionType, bindingId, condition.toString());
                Expression dslExpr = buildExpressionWithIndexing(drlxParseResult);
                context.addExpression(dslExpr);
            }

            previousBind = bindingId;
            previousClass = expressionType;
        }
    }
}
