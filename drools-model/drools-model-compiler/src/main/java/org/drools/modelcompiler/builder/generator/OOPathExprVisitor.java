package org.drools.modelcompiler.builder.generator;

import org.drools.javaparser.ast.drlx.OOPathChunk;
import org.drools.javaparser.ast.drlx.OOPathExpr;
import org.drools.javaparser.ast.expr.Expression;
import org.drools.javaparser.ast.expr.MethodCallExpr;
import org.drools.javaparser.ast.expr.NameExpr;
import org.drools.modelcompiler.builder.PackageModel;

import java.util.HashSet;

import static org.drools.core.util.ClassUtils.extractGenericType;
import static org.drools.modelcompiler.builder.generator.DrlxParseUtil.*;
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

            final String fieldName = chunk.getField().toString();

            final TypedExpression callExpr = DrlxParseUtil.nameExprToMethodCallExpr(fieldName, previousClass);
            Class<?> fieldType = callExpr.getType();
            if (Iterable.class.isAssignableFrom(fieldType)) {
                fieldType = extractGenericType(previousClass, ((MethodCallExpr) callExpr.getExpression()).getName().toString());
            }

            final String bindingId = context.getExprId(fieldType, fieldName);
            final Expression accessorLambda = generateLambdaWithoutParameters(new HashSet<>(),
                                                                    prepend(new NameExpr("_this"), callExpr.getExpression()));

            final MethodCallExpr reactiveFrom = new MethodCallExpr(null, "reactiveFrom");
            reactiveFrom.addArgument(new NameExpr(toVar(previousBind)));
            reactiveFrom.addArgument(accessorLambda);

            context.addDeclaration(new DeclarationSpec(bindingId, fieldType, reactiveFrom));

            final Expression condition = chunk.getCondition();
            if (condition != null) {
                final ModelGenerator.DrlxParseResult drlxParseResult = ModelGenerator.drlxParse(context, packageModel, fieldType, bindingId, condition.toString());
                context.addExpression(buildExpressionWithIndexing(drlxParseResult));
            }

            previousBind = bindingId;
            previousClass = fieldType;
        }
    }
}
