package org.drools.modelcompiler.builder.generator;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.drools.javaparser.ast.drlx.OOPathChunk;
import org.drools.javaparser.ast.drlx.OOPathExpr;
import org.drools.javaparser.ast.expr.Expression;
import org.drools.javaparser.ast.expr.MethodCallExpr;
import org.drools.javaparser.ast.expr.NameExpr;
import org.drools.modelcompiler.builder.PackageModel;

import static org.drools.core.util.ClassUtils.extractGenericType;
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

        List<ModelGenerator.DrlxParseResult> ooPathConditionExpressions = new ArrayList<>();

        for (OOPathChunk chunk : ooPathExpr.getChunks()) {

            final String fieldName = chunk.getField().toString();

            final TypedExpression callExpr = DrlxParseUtil.nameExprToMethodCallExpr(fieldName, previousClass);
            Class<?> fieldType = (chunk.getInlineCast() != null)
                    ? DrlxParseUtil.getClassFromContext(context.getPkg().getTypeResolver(), chunk.getInlineCast().toString())
                    : callExpr.getType();

            if (Iterable.class.isAssignableFrom(fieldType)) {
                fieldType = extractGenericType(previousClass, ((MethodCallExpr) callExpr.getExpression()).getName().toString());
            }

            final String bindingId = context.getOOPathId(fieldType, fieldName);
            final Expression accessorLambda = generateLambdaWithoutParameters(new ArrayList<>(),
                                                                    prepend(new NameExpr("_this"), callExpr.getExpression()));

            final MethodCallExpr reactiveFrom = new MethodCallExpr(null, "reactiveFrom");
            reactiveFrom.addArgument(new NameExpr(toVar(previousBind)));
            reactiveFrom.addArgument(accessorLambda);

            DeclarationSpec newDeclaration = new DeclarationSpec(bindingId, fieldType, reactiveFrom);
            context.addDeclaration(newDeclaration);
            context.addOOPathDeclaration(newDeclaration);

            final Expression condition = chunk.getCondition();
            if (condition != null) {
                final ModelGenerator.DrlxParseResult drlxParseResult = ModelGenerator.drlxParse(context, packageModel, fieldType, bindingId, condition.toString());
                ooPathConditionExpressions.add(drlxParseResult);
            }

            previousBind = bindingId;
            previousClass = fieldType;
        }

        // If the OOPath has an inner binding, it will be in the context's declarations without its type (as it's inferred from the last OOPath chunk).
        // We remove the original expression without type and use its name in the last expression
        List<DeclarationSpec> declarations = context.getDeclarations();
        final Optional<DeclarationSpec> missingClassDeclarationFound = declarations.stream().filter(d -> d.declarationClass == null).findFirst();

        missingClassDeclarationFound.ifPresent(missingClassDeclaration -> {
            final String innerBindingId = missingClassDeclaration.getBindingId();

            final int lastIndex = declarations.size() - 1;
            final DeclarationSpec last = declarations.get(lastIndex);
            declarations.set(lastIndex, new DeclarationSpec(innerBindingId, last.declarationClass, last.optPattern, last.declarationSource));
            declarations.remove(declarations.indexOf(missingClassDeclaration));

            // In the meanwhile some condition could have used that binding, we need to rename that also
            for(ModelGenerator.DrlxParseResult r : ooPathConditionExpressions) {
                if(r.getExprId().equals(last.getBindingId())) {
                    r.setExprId(innerBindingId);
                } else if(r.getPatternBinding().equals(last.getBindingId())) {
                    r.setPatternBinding(innerBindingId);
                }
            }
        });

        ooPathConditionExpressions.forEach(d -> context.addExpression(buildExpressionWithIndexing(context, d)));


    }
}
