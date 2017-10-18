package org.drools.modelcompiler.builder.generator;

import org.drools.javaparser.ast.drlx.OOPathChunk;
import org.drools.javaparser.ast.drlx.OOPathExpr;
import org.drools.javaparser.ast.expr.Expression;
import org.drools.javaparser.ast.expr.MethodCallExpr;
import org.drools.javaparser.ast.expr.NameExpr;
import org.drools.modelcompiler.builder.PackageModel;

import java.util.HashSet;
import java.util.List;

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

        // If the OOPath has an inner binding, it will be in the context's declarations without its type (as it's inferred from the last OOPath chunk).
        // We remove the original expression without type and use its name in the last expression
        final List<DeclarationSpec> declarations = context.getDeclarations();
        if(!declarations.isEmpty() && declarations.iterator().next().declarationClass == null) {
            final String innerBindingId = declarations.iterator().next().bindingId;

            final int lastIndex = declarations.size() - 1;
            final DeclarationSpec last = declarations.get(lastIndex);
            declarations.set(lastIndex, new DeclarationSpec(innerBindingId, last.declarationClass, last.optPattern, last.declarationSource));
            declarations.remove(0);
        }

    }
}
